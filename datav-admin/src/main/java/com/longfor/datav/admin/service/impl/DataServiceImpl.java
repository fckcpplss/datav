package com.longfor.datav.admin.service.impl;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.result.ExcelImportResult;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.longfor.datav.admin.exception.BusinessException;
import com.longfor.datav.admin.service.IDataService;
import com.longfor.datav.admin.service.IMemberService;
import com.longfor.datav.admin.service.IRuleBusiness;
import com.longfor.datav.admin.service.SnapshotTeamDataService;
import com.longfor.datav.common.dto.MemberSnapshotAllDataDTO;
import com.longfor.datav.common.dto.MemberSnapshotDimensionInfoDTO;
import com.longfor.datav.common.dto.ScoreDataImportDTO;
import com.longfor.datav.common.dto.SnapshotScoreDetailDTO;
import com.longfor.datav.common.enums.*;
import com.longfor.datav.common.utils.RuleUtil;
import com.longfor.datav.common.vo.req.CalculateScoreReq;
import com.longfor.datav.common.vo.req.MemberListRequest;
import com.longfor.datav.common.vo.resp.CalculateScoreResp;
import com.longfor.datav.common.vo.resp.MemberListResponse;
import com.longfor.datav.common.vo.resp.MemberScoreHistoryResponse;
import com.longfor.datav.dao.entity.*;
import com.longfor.datav.dao.service.*;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DataServiceImpl implements IDataService {

    @Autowired
    private ITDDimensionIntegralService dimensionIntegralService;

    @Autowired
    private ITDDimensionService dimensionService;

    @Autowired
    private ITDSnapshotTableService snapshotTableService;

    @Autowired
    private IMemberService memberService;

    @Autowired
    private ITDAccountService accountService;

    @Autowired
    private ITDTimeSprintRelationService timeSprintRelationService;

    @Resource
    private SnapshotTeamDataService dataService;

    @Autowired
    private IRuleBusiness ruleBusiness;

    private List<ScoreDataImportDTO> getAndCheckImportData(MultipartFile file){
        InputStream inputStream = null;
        Optional.ofNullable(file)
                .filter(f -> f.getSize() > 0)
                .orElseThrow(() -> new BusinessException("上传文件不能为空"));
        try{
            Optional.ofNullable(file.getOriginalFilename())
                    .map(name -> name.substring(name.lastIndexOf(".")+1))
                    .filter(extension -> Arrays.asList("xls","xlsx","XLS","XLSX").contains(extension))
                    .orElseThrow(() -> new BusinessException("文件格式不正确"));
            inputStream = file.getInputStream();
            ImportParams importParams = new ImportParams();
            importParams.setNeedVerify(true);
            importParams.setHeadRows(1);
            ExcelImportResult<ScoreDataImportDTO> importResult = ExcelImportUtil.importExcelMore(inputStream, ScoreDataImportDTO.class, importParams);
            if(Objects.isNull(importResult) || (CollectionUtils.isEmpty(importResult.getFailList()) && CollectionUtils.isEmpty(importResult.getList()))){
                throw new BusinessException("导入文件内容为空");
            }
            if(importResult.getFailList().size() + importResult.getList().size() > 2000){
                throw new BusinessException("导入文件条数不能超过2000条");
            }
            log.info("数据倒入，校验成功条数 = {}，校验失败条数 = {}",Optional.ofNullable(importResult.getList()).map(List::size).orElse(0),Optional.ofNullable(importResult.getFailList()).map(List::size).orElse(0));
            List<ScoreDataImportDTO> failList = importResult.getFailList();
            if(!CollectionUtils.isEmpty(failList)){
                throw new BusinessException(new StringBuilder("导入失败；")
                        .append("存在空值或格式错误：").append(failList.size()).append("条；")
                        .append("错误信息：<br/>").append(ListUtils.emptyIfNull(failList).stream().map(x -> new StringBuilder("行号："+ (x.getRowNum() + 1)).append("，").append(x.getErrorMsg()).toString()).collect(Collectors.joining("；<br/>")))
                        .toString());
            }
            List<ScoreDataImportDTO> importDataList = importResult.getList()
                    .stream()
                    .filter(x -> StringUtils.isNotBlank(x.getDimensionCode()))
                    .map(data -> {
                        //字符串去除前后空格
                        Arrays.stream(data.getClass().getDeclaredFields()).forEach(field -> {
                            if(field.getType().toString().equals("class java.lang.String")){
                                Object fieldValue = ReflectUtil.getFieldValue(data, field);
                                ReflectUtil.setFieldValue(data,field,String.valueOf(fieldValue).trim());
                            }
                        });
                        return data;
                    }).collect(Collectors.toList());
            log.info("数据倒入，去除空格后的数据 = {}", JSON.toJSONString(importDataList));
            return importDataList;
        }catch (Exception ex){
            ex.printStackTrace();
            if(ex instanceof BusinessException){
                throw new BusinessException(ex.getMessage());
            }
            throw new BusinessException("系统异常");
        }finally {
            if(Objects.nonNull(inputStream)){
                try {
                    inputStream.close();
                } catch (IOException e) {
                }
            }
        }
    }

    @Override
    public String dataImport(MultipartFile file) {
        List<ScoreDataImportDTO> andCheckImportDatas = getAndCheckImportData(file);
        List<TDDimensionIntegral> dataList = ListUtils.emptyIfNull(andCheckImportDatas).stream()
                .map(x -> {
                    log.info("数据倒入，处理数据 = {}", JSON.toJSONString(x));
                    TDDimensionIntegral dimensionIntegral = new TDDimensionIntegral();
                    dimensionIntegral.setDimensionCode(Optional.ofNullable(DimensionCodeEnum.fromMsg(x.getDimensionCode())).map(DimensionCodeEnum::getCode).orElse(null));
                    dimensionIntegral.setFraction(Double.parseDouble(x.getFraction()));
                    dimensionIntegral.setValue(x.getValue());
                    //团队类型，团队编码兼容code和名称
                    if(x.getType().equals(SnapshotAccountTypeEnum.TEAM.getMessage())){
                        dimensionIntegral.setCodeFlag(Optional.ofNullable(TeamCodeEnum.fromCode(x.getAccount()))
                                .map(TeamCodeEnum::getCode)
                                .orElse(Optional.ofNullable(TeamCodeEnum.fromMsg(x.getAccount())).map(TeamCodeEnum::getCode).orElse(null)));
                    }else{
                        dimensionIntegral.setCodeFlag(x.getAccount());
                    }
                    //todo 数据强校验
                    dimensionIntegral.setPeriodType(Optional.ofNullable(PeriodTypeEnum.fromMsg(x.getPeriodType())).map(PeriodTypeEnum::getCode).orElse(null));
                    dimensionIntegral.setType(Optional.ofNullable(SnapshotAccountTypeEnum.fromMsg(x.getType())).map(SnapshotAccountTypeEnum::getCode).orElse(null));
                    dimensionIntegral.setPeriodFlag(x.getPeriod());
                    dimensionIntegral.setYear(x.getYear());
                    dimensionIntegral.setUpdateTime(LocalDateTimeUtil.now());
                    dimensionIntegral.setCreateTime(LocalDateTimeUtil.now());
                    return dimensionIntegral;
                })
                .collect(Collectors.toList());
        dataList.stream().forEach(item -> {
            Optional.ofNullable(dimensionIntegralService.getOne(Wrappers.<TDDimensionIntegral>lambdaQuery()
                    .eq(TDDimensionIntegral::getDimensionCode,item.getDimensionCode())
                    .eq(TDDimensionIntegral::getType,item.getType())
                    .eq(TDDimensionIntegral::getPeriodType,item.getPeriodType())
                    .eq(TDDimensionIntegral::getCodeFlag,item.getCodeFlag())
                    .eq(TDDimensionIntegral::getYear,item.getYear())
                    .eq(TDDimensionIntegral::getPeriodFlag,item.getPeriodFlag()).last(" limit 1")))
                    .ifPresent(item1 -> {
                        item.setId(item1.getId());
                     });
        });
        dimensionIntegralService.saveOrUpdateBatch(dataList);
        ThreadUtil.execAsync(() -> {
            log.info("开始同步快照数据");
            handelPersonSnapshotData();
            dataService.snapshotTeam();
        });

        return StrUtil.format("成功导入{}条数据",dataList.size());
    }

    @Override
    public void handelPersonSnapshotData(String account, String year,String month,String sprint) {
        log.info("同步个人得分信息开始,开始处理{}的数据",account);

        //年度得分数据
        List<TDDimensionIntegral> yearDataList = getDimensionIntegralDataList(account,PeriodTypeEnum.YEAR,year);

        //月度得分数据
//        List<TDDimensionIntegral> monthDataList = getDimensionIntegralDataList(account,PeriodTypeEnum.MONTH,month);

        //冲刺得分数据
        List<TDDimensionIntegral> spintDataList = getDimensionIntegralDataList(account,PeriodTypeEnum.SPRINT,sprint);

        //人员信息
        MemberListResponse memberInfo = ListUtils.emptyIfNull(memberService.memberList(new MemberListRequest(null, null, Arrays.asList(account))).getData())
                .stream()
                .findFirst().orElse(null);

        //处理成员月度数据
        //handelMemberPeriodData(PeriodTypeEnum.MONTH,month,account,memberInfo,monthDataList);

        //处理成员冲刺数据
        handelMemberPeriodData(PeriodTypeEnum.SPRINT,sprint,account,memberInfo,spintDataList,false);

        //处理成员年度数据
        handelMemberPeriodData(PeriodTypeEnum.YEAR,year,account,memberInfo,yearDataList,false);
    }

    private void handelMemberPeriodRankData(PeriodTypeEnum periodTypeEnum, String period) {
        List<TDSnapshotTable> snapshotTables = snapshotTableService.list(Wrappers.<TDSnapshotTable>lambdaQuery()
                .eq(TDSnapshotTable::getYear, String.valueOf(DateUtil.thisYear()))
                .eq(TDSnapshotTable::getType, SnapshotAccountTypeEnum.PERSON.getCode())
                .eq(TDSnapshotTable::getPeriodFlag, getPeriod(periodTypeEnum, period))
                .eq(TDSnapshotTable::getModelFlag, "all"));
        if(CollectionUtils.isEmpty(snapshotTables)){
            return;
        }
        AtomicInteger atomicInteger = new AtomicInteger(1);
        List<TDSnapshotTable> updateInfoList = snapshotTables.stream()
                .sorted((a,b) -> {
                    MemberSnapshotAllDataDTO aData = JSON.parseObject(a.getContent(), MemberSnapshotAllDataDTO.class);
                    MemberSnapshotAllDataDTO bData = JSON.parseObject(b.getContent(), MemberSnapshotAllDataDTO.class);
                    return Double.compare(Double.parseDouble(bData.getHistoryScore()),Double.parseDouble(aData.getHistoryScore()));
                })
                .map(item -> {
                    MemberSnapshotAllDataDTO memberSnapshotAllDataDTO = JSON.parseObject(item.getContent(), MemberSnapshotAllDataDTO.class);
                    MemberListResponse memberListResponse = memberSnapshotAllDataDTO.getInfo();
                    memberListResponse.setRank(atomicInteger.getAndIncrement());
                    memberSnapshotAllDataDTO.setInfo(memberListResponse);
                    item.setContent(JSON.toJSONString(memberSnapshotAllDataDTO));
                    return item;
                }).collect(Collectors.toList());
        snapshotTableService.updateBatchById(updateInfoList);
    }

    @Override
    public void handelPersonSnapshotData() {
        List<TDDimensionIntegral> dimensionIntegrals = dimensionIntegralService.list(Wrappers.<TDDimensionIntegral>lambdaQuery()
                .eq(TDDimensionIntegral::getType, SnapshotAccountTypeEnum.PERSON.getCode())
                .eq(TDDimensionIntegral::getYear, String.valueOf(DateUtil.thisYear())));
//        List<TDAccount> accountList = accountService.list(Wrappers.<TDAccount>lambdaQuery().eq(TDAccount::getIsDelete, DeleteStatusEnum.NO.getCode()));
        if(CollectionUtils.isEmpty(dimensionIntegrals)){
            return;
        }
        List<String> accountList = dimensionIntegrals.stream().map(TDDimensionIntegral::getCodeFlag).distinct().collect(Collectors.toList());
        accountList.stream().forEach(item -> {
            try{
                handelPersonSnapshotData(item,null,null,null);
            }catch (Exception ex){
                log.error(StrUtil.format("同步个人得分信息失败，account = {}",item),ex);
            }
        });
    }

    @Override
    public CalculateScoreResp calculateScore(CalculateScoreReq req) {
        CalculateScoreResp calculateScoreResp = new CalculateScoreResp();
        BeanUtils.copyProperties(req,calculateScoreResp);
        DimensionCodeEnum dimensionCodeEnum = DimensionCodeEnum.fromMsg(req.getDimensionalName());
        if(ObjectUtils.isEmpty(dimensionCodeEnum)){
            throw new BusinessException("指标项名称输入错误");
        }
        //执行规则引擎
        Integer score = RuleUtil.execute(dimensionCodeEnum.getCode(),req.getDimensional());
        calculateScoreResp.setFraction(String.valueOf(score));
        //记录得分表
        TDDimensionIntegral tdDimensionIntegral = new TDDimensionIntegral();
        Optional.ofNullable(dimensionIntegralService.getOne(Wrappers.<TDDimensionIntegral>lambdaQuery()
                .eq(TDDimensionIntegral::getDimensionCode, dimensionCodeEnum.getCode())
                .eq(TDDimensionIntegral::getType, req.getType())
                .eq(TDDimensionIntegral::getPeriodType, req.getPeriodType())
                .eq(TDDimensionIntegral::getCodeFlag, req.getCodeFlag())
                .eq(TDDimensionIntegral::getYear, req.getYear())
                .eq(TDDimensionIntegral::getPeriodFlag, req.getPeriodFlag())
                .last(" limit 1"))).ifPresent(exist -> {
                    tdDimensionIntegral.setId(exist.getId());
                });

        tdDimensionIntegral.setCodeFlag(req.getCodeFlag());
        tdDimensionIntegral.setDimensionCode(dimensionCodeEnum.getCode());
        tdDimensionIntegral.setFraction(Double.valueOf(score));
        tdDimensionIntegral.setPeriodFlag(req.getPeriodFlag());
        tdDimensionIntegral.setPeriodType(req.getPeriodType());
        tdDimensionIntegral.setYear(req.getYear());
        tdDimensionIntegral.setType(req.getType());
        tdDimensionIntegral.setValue(req.getDimensional());
        tdDimensionIntegral.setCreateTime(LocalDateTimeUtil.now());
        tdDimensionIntegral.setUpdateTime(LocalDateTimeUtil.now());
        dimensionIntegralService.saveOrUpdate(tdDimensionIntegral);
        //同步快照
        ThreadUtil.execAsync(() -> {
            log.info("开始同步快照数据");
            if(req.getType() == DimensionTypeEnum.PERSON.getCode()){
                handelPersonSnapshotData(req.getCodeFlag(),req.getYear(),null,req.getPeriodFlag());
            }
            if(req.getType() == DimensionTypeEnum.TEAM.getCode()){
                dataService.snapshotTeam();
            }
        });
        return calculateScoreResp;
    }

    private void handelInitMemberPeriodData(PeriodTypeEnum periodTypeEnum,String period,String account,MemberListResponse memberInfo){
        MemberSnapshotAllDataDTO memberSnapshotAllDataDTO = new MemberSnapshotAllDataDTO();
        MemberListResponse memberListResponse = new MemberListResponse();
        BeanUtils.copyProperties(memberInfo,memberListResponse);
        //设置个人信息
        memberListResponse.setScore("0");
        memberListResponse.setRank(1);
        memberSnapshotAllDataDTO.setInfo(memberListResponse);

        //查询得分记录
        List<TDDimensionIntegral> dataList = getDimensionIntegralDataList(account,null,periodTypeEnum.equals(PeriodTypeEnum.YEAR) ? null : getPeriod(periodTypeEnum,period));
        if(!CollectionUtils.isEmpty(dataList)){

            handelMemberPeriodData(periodTypeEnum,period,account,memberInfo,dataList,true);
        }else{
            //父纬度列表
            List<TDDimension> parentDimensionList = dimensionService.list(Wrappers.<TDDimension>lambdaQuery()
                    .eq(TDDimension::getType, SnapshotAccountTypeEnum.PERSON.getCode())
                    .eq(TDDimension::getIsFirstNode,1));
            //未得分的父指标记录
            List<MemberScoreHistoryResponse> memberScoreHistoryResponses = ListUtils.emptyIfNull(parentDimensionList).stream().map(x -> {
                MemberScoreHistoryResponse memberScoreHistoryResponse = new MemberScoreHistoryResponse();
                memberScoreHistoryResponse.setScore("0");
                memberScoreHistoryResponse.setCode(x.getCode());
                memberScoreHistoryResponse.setMetrics(x.getName());
                return memberScoreHistoryResponse;
            }).collect(Collectors.toList());
            //设置指标得分信息
            memberSnapshotAllDataDTO.setItemScore(memberScoreHistoryResponses);

            //设置得分明细
            memberSnapshotAllDataDTO.setHistoryScoreDetail(Lists.newArrayList());
            //设置指标详情数据
            memberSnapshotAllDataDTO.setDimensionList(Lists.newArrayList());
            //设置历史得分信息
            memberSnapshotAllDataDTO.setHistoryScore("0");
            List<TDSnapshotTable> insertOrUpdateDataList = Lists.newArrayList();
            TDSnapshotTable snapshotTable = new TDSnapshotTable();
            Optional.ofNullable(snapshotTableService.getOne(Wrappers.<TDSnapshotTable>lambdaQuery()
                    .eq(TDSnapshotTable::getYear, String.valueOf(DateUtil.thisYear()))
                    .eq(TDSnapshotTable::getType,SnapshotAccountTypeEnum.PERSON.getCode())
                    .eq(TDSnapshotTable::getCodeFlag,account)
                    .eq(TDSnapshotTable::getPeriodFlag,getPeriod(periodTypeEnum,period))
                    .eq(TDSnapshotTable::getModelFlag,"all")
                    .last(" limit 1"))).ifPresent(exist -> {
                snapshotTable.setId(exist.getId());
            });
            snapshotTable.setYear(String.valueOf(DateUtil.thisYear()));
            snapshotTable.setType(SnapshotAccountTypeEnum.PERSON.getCode());
            snapshotTable.setCodeFlag(account);
            snapshotTable.setNameFlag(memberInfo.getName());
            snapshotTable.setPeriodFlag(getPeriod(periodTypeEnum,period));
            snapshotTable.setContent(JSON.toJSONString(memberSnapshotAllDataDTO));
            snapshotTable.setSnapshotTime(LocalDateTimeUtil.now());
            snapshotTable.setModelFlag("all");
            insertOrUpdateDataList.add(snapshotTable);
            XxlJobLogger.log("同步个人得分信息开始,开始批量插入或更新快照数据，{}",JSON.toJSONString(insertOrUpdateDataList));
            snapshotTableService.saveOrUpdateBatch(insertOrUpdateDataList);

            //计算排名
            handelMemberPeriodRankData(periodTypeEnum,period);
        }

    }

    /**
     * 根据类型获取冲刺周期
     * @param periodTypeEnum
     * @param period
     * @return
     */
    private String getPeriod(PeriodTypeEnum periodTypeEnum,String period){
        if(StringUtils.isNotBlank(period)){
            return period;
        }
        //查询最新冲刺信息
        TDTimeSprintRelation timeSprintRelation = timeSprintRelationService.getOne(Wrappers.<TDTimeSprintRelation>lambdaQuery()
                .eq(TDTimeSprintRelation::getYear, String.valueOf(DateUtil.thisYear()))
                .orderByDesc(TDTimeSprintRelation::getEndTime).last(" limit 1"));
        if(periodTypeEnum.equals(PeriodTypeEnum.YEAR)){
            return LocalDateTimeUtil.now().format(DateTimeFormatter.ofPattern("yyyy"));
        }
        if (periodTypeEnum.equals(PeriodTypeEnum.MONTH)){
            //查询最新月份
            return String.valueOf(DateUtil.month(timeSprintRelation.getEndTime()));
        }
        if (periodTypeEnum.equals(PeriodTypeEnum.SPRINT)){
            //查询最新冲刺
            return timeSprintRelation.getPeriod();
        }
        return period;
    }

    /**
     * 过滤掉纬度编码不存在的得分数据
     * @param dimensionList
     * @param dataList
     * @return
     */

    private List<TDDimensionIntegral> checkAndFilterData(List<TDDimension> dimensionList,List<TDDimensionIntegral> dataList){
        if(CollectionUtils.isEmpty(dataList)){
            return null;
        }
        /**
         * 过滤掉纬度编码不存在的得分数据
         */
        dataList = dataList.stream().filter(x -> dimensionList.stream().anyMatch(y -> y.getCode().equals(x.getDimensionCode()))).collect(Collectors.toList());
        return dataList;
    }

    /**
     * 按照周期值分组计算纬度得分
     * @param periodTypeEnum 周期类型
     * @param period 周期值
     * @param account 团队或个人账号
     * @param memberInfo 成员信息
     * @param dataList 年度/月度/冲刺得分数据
     * @Param isInitalData 是否是初始话数据，初始话数据也走这个方法，避免循环调用
     */
    private void handelMemberPeriodData(PeriodTypeEnum periodTypeEnum,String period,String account,MemberListResponse memberInfo, List<TDDimensionIntegral> dataList,boolean isInitalData) {
        /**
         * 从得分数据获取子纬度编码
         */
        List<String> dimensionCodes = ListUtils.emptyIfNull(dataList).stream().map(TDDimensionIntegral::getDimensionCode).distinct().collect(Collectors.toList());

        /**
         * 得分数据子纬度编码存在的数据
         */
        List<TDDimension> dimensionList = CollectionUtils.isEmpty(dimensionCodes) ? Lists.newArrayList() :  dimensionService.list(Wrappers.<TDDimension>lambdaQuery().in(TDDimension::getCode, dimensionCodes));

        //过滤数据
        dataList = checkAndFilterData(dimensionList,dataList);

        /**
         * 得分数据为空，录入空数据
         */
        if(CollectionUtils.isEmpty(dataList)){
            if(!isInitalData){
                handelInitMemberPeriodData(periodTypeEnum,period,account,memberInfo);
            }
            return;
        }

        //子纬度信息map
        Map<String, TDDimension> dimensionMap = CollectionUtils.isEmpty(dimensionCodes) ? Maps.newHashMap() : ListUtils.emptyIfNull(dimensionList)
                .stream()
                .collect(Collectors.toMap(TDDimension::getCode, Function.identity(),(a, b) -> b));

        /**
         * 个人类型下的所有父纬度
         */
        List<TDDimension> parentDimensionList = dimensionService.list(Wrappers.<TDDimension>lambdaQuery()
                .eq(TDDimension::getType, SnapshotAccountTypeEnum.PERSON.getCode())
                .eq(TDDimension::getIsFirstNode,1));

        /**
         * 父纬度信息map，方便后续根据code使用
         */
        Map<String, TDDimension> parentDimensionMap = CollectionUtils.isEmpty(parentDimensionList) ? Maps.newHashMap() : ListUtils.emptyIfNull(parentDimensionList)
                .stream()
                .collect(Collectors.toMap(TDDimension::getCode, Function.identity(),(a,b) -> b));

        /**
         * 按照周期值分组计算纬度得分
         */
        Map<String, MemberSnapshotAllDataDTO> groupByPeriodDataMap = dataList.stream().collect(Collectors.groupingBy(x -> !isInitalData ? x.getPeriodFlag() : getPeriod(periodTypeEnum,period), Collectors.collectingAndThen(Collectors.toList(), list -> {
            MemberSnapshotAllDataDTO memberSnapshotAllDataDTO = new MemberSnapshotAllDataDTO();
            /**
             * 组装得分详情，包括子纬度编码、父纬度编码、分数、时间
             * 过滤掉是父纬度的得分数据
             */
            List<SnapshotScoreDetailDTO> snapshotScoreDetails = list.stream()
                    .filter(x -> !parentDimensionMap.containsKey(x.getDimensionCode()))
                    .map(x -> {
                        SnapshotScoreDetailDTO snapshotScoreDetailDTO = new SnapshotScoreDetailDTO();
                        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        snapshotScoreDetailDTO.setDate(x.getCreateTime().format(fmt));
                        snapshotScoreDetailDTO.setScore(x.getFraction());
                        snapshotScoreDetailDTO.setValue(x.getValue());
                        snapshotScoreDetailDTO.setDimensionCode(x.getDimensionCode());
                        Optional.ofNullable(dimensionMap).map(map -> map.get(x.getDimensionCode())).ifPresent(subDimension -> {
                            snapshotScoreDetailDTO.setDimensionName(subDimension.getName());
                            snapshotScoreDetailDTO.setDimensionDesc(subDimension.getRemark());
                            snapshotScoreDetailDTO.setParentDimensionCode(subDimension.getParentCode());
                            Optional.ofNullable(parentDimensionMap).map(pm -> pm.get(subDimension.getParentCode())).ifPresent(parentDimension -> {
                                snapshotScoreDetailDTO.setParentDimensionName(parentDimension.getName());
                                snapshotScoreDetailDTO.setInitialScore(parentDimension.getInitialScore());
                            });
                        });
                        return snapshotScoreDetailDTO;
                    }).collect(Collectors.toList());

            /**
             * 根据得分详情，分组计算父纬度得分
             * 过滤掉父编码为空的数据
             * 过滤掉父编码不正确的数据
             */
            Map<String, Double> parentDimensionCodeAndMap = ListUtils.emptyIfNull(snapshotScoreDetails)
                    .stream()
                    .filter(x -> StringUtils.isNotBlank(x.getParentDimensionCode()))
                    .collect(Collectors.groupingBy(SnapshotScoreDetailDTO::getParentDimensionCode, Collectors.collectingAndThen(Collectors.toList(), list1 -> {
                        //初始分数 + 总得分
                        return list1.get(0).getInitialScore() + list1.stream().mapToDouble(x -> Double.valueOf(x.getScore())).sum();
                    })));
            /**
             * 父纬度得分记录详情
             */
            List<MemberScoreHistoryResponse> parentDimensionScoreList = CollectionUtils.isEmpty(parentDimensionCodeAndMap) ? Lists.newArrayList() : parentDimensionCodeAndMap.entrySet().stream().map(x -> {
                MemberScoreHistoryResponse memberScoreHistoryResponse = new MemberScoreHistoryResponse();
                memberScoreHistoryResponse.setScore(String.valueOf(x.getValue()));
                memberScoreHistoryResponse.setCode(x.getKey());
                Optional.ofNullable(parentDimensionMap).map(map -> map.get(x.getKey())).ifPresent(item -> {
                    memberScoreHistoryResponse.setMetrics(item.getName());
                    memberScoreHistoryResponse.setDesc(item.getRemark());
                });
                return memberScoreHistoryResponse;
            }).collect(Collectors.toList());
            /**
             * 未得分的父指标记录
             */
            List<MemberScoreHistoryResponse> parentDimensionNoScoreList = parentDimensionList.stream().filter(x -> parentDimensionScoreList.stream().noneMatch(y -> y.getCode().equals(x.getCode()))).map(x -> {
                MemberScoreHistoryResponse memberScoreHistoryResponse = new MemberScoreHistoryResponse();
                memberScoreHistoryResponse.setScore(String.valueOf(x.getInitialScore()));
                memberScoreHistoryResponse.setCode(x.getCode());
                memberScoreHistoryResponse.setMetrics(x.getName());
                memberScoreHistoryResponse.setDesc(x.getRemark());
                return memberScoreHistoryResponse;
            }).collect(Collectors.toList());
            //设置指标得分信息
            memberSnapshotAllDataDTO.setItemScore(ListUtils.union(parentDimensionScoreList,parentDimensionNoScoreList));
            //计算历史总得分
            double historyScore = memberSnapshotAllDataDTO.getItemScore().stream().map(MemberScoreHistoryResponse::getScore).mapToDouble(Double::parseDouble).sum();
            //设置历史得分信息，从父纬度得分中计算
            memberSnapshotAllDataDTO.setHistoryScore(String.valueOf(historyScore));

            MemberListResponse memberListResponse = new MemberListResponse();
            BeanUtils.copyProperties(memberInfo,memberListResponse);
            //设置个人信息
            memberListResponse.setScore(memberSnapshotAllDataDTO.getHistoryScore());
            memberListResponse.setRank(1);
            memberSnapshotAllDataDTO.setInfo(memberListResponse);
            //设置得分明细
            memberSnapshotAllDataDTO.setHistoryScoreDetail(snapshotScoreDetails);
            //设置指标详情数据
            memberSnapshotAllDataDTO.setDimensionList(snapshotScoreDetails.stream().flatMap(x -> {
                List<MemberSnapshotDimensionInfoDTO> dataList_ = new ArrayList<>();
                MemberSnapshotDimensionInfoDTO memberSnapshotDimensionInfoDTO = new MemberSnapshotDimensionInfoDTO();
                memberSnapshotDimensionInfoDTO.setDimensionCode(x.getDimensionCode());
                memberSnapshotDimensionInfoDTO.setScore(String.valueOf(x.getScore()));
                memberSnapshotDimensionInfoDTO.setIsParent(0);
                Optional.ofNullable(dimensionMap).map(map -> map.get(x.getDimensionCode())).ifPresent(item -> {
                    memberSnapshotDimensionInfoDTO.setDimensionName(item.getName());
                    memberSnapshotDimensionInfoDTO.setParentDimensionCode(item.getParentCode());
                    memberSnapshotDimensionInfoDTO.setDimensionDesc(item.getRemark());
                });
                dataList_.add(memberSnapshotDimensionInfoDTO);

                MemberSnapshotDimensionInfoDTO parentMemberDimensionListResponse = new MemberSnapshotDimensionInfoDTO();
                parentMemberDimensionListResponse.setDimensionCode(memberSnapshotDimensionInfoDTO.getParentDimensionCode());
                parentMemberDimensionListResponse.setIsParent(1);
                parentMemberDimensionListResponse.setParentDimensionCode(null);
                parentMemberDimensionListResponse.setScore(null);
                Optional.ofNullable(parentDimensionMap).map(map -> map.get(memberSnapshotDimensionInfoDTO.getParentDimensionCode())).ifPresent(item -> {
                    parentMemberDimensionListResponse.setDimensionName(item.getName());
                    parentMemberDimensionListResponse.setDimensionDesc(item.getRemark());
                });
                dataList_.add(parentMemberDimensionListResponse);
                return dataList_.stream();
            }).collect(Collectors.toList()));
            return memberSnapshotAllDataDTO;
        })));

        List<TDSnapshotTable> insertOrUpdateDataList = groupByPeriodDataMap.entrySet().stream().map(x -> {
            TDSnapshotTable snapshotTable = new TDSnapshotTable();
            Optional.ofNullable(snapshotTableService.getOne(Wrappers.<TDSnapshotTable>lambdaQuery()
                    .eq(TDSnapshotTable::getYear, String.valueOf(DateUtil.thisYear()))
                    .eq(TDSnapshotTable::getType,SnapshotAccountTypeEnum.PERSON.getCode())
                    .eq(TDSnapshotTable::getCodeFlag,account)
                    .eq(TDSnapshotTable::getPeriodFlag,x.getKey())
                    .eq(TDSnapshotTable::getModelFlag,"all")
                    .last(" limit 1"))).ifPresent(exist -> {
                snapshotTable.setId(exist.getId());

            });
            snapshotTable.setYear(String.valueOf(DateUtil.thisYear()));
            snapshotTable.setType(SnapshotAccountTypeEnum.PERSON.getCode());
            snapshotTable.setCodeFlag(account);
            snapshotTable.setNameFlag(memberInfo.getName());
            snapshotTable.setPeriodFlag(!isInitalData ? x.getKey() : getPeriod(periodTypeEnum,period));
            snapshotTable.setContent(JSON.toJSONString(x.getValue()));
            snapshotTable.setSnapshotTime(LocalDateTimeUtil.now());
            snapshotTable.setModelFlag("all");
            return snapshotTable;
        }).collect(Collectors.toList());
        XxlJobLogger.log("同步个人得分信息开始,开始批量插入或更新快照数据，{}",JSON.toJSONString(insertOrUpdateDataList));
        snapshotTableService.saveOrUpdateBatch(insertOrUpdateDataList);

        groupByPeriodDataMap.keySet().stream().forEach(item -> {
            //计算排名
            handelMemberPeriodRankData(periodTypeEnum,item);
        });
    }

    /**
     * 获取指标数据集合
     * @return
     */
    private List<TDDimensionIntegral> getDimensionIntegralDataList(String account,PeriodTypeEnum periodTypeEnum,String periodFlag) {
        LambdaQueryWrapper<TDDimensionIntegral> lambdaQueryWrapper = Wrappers.<TDDimensionIntegral>lambdaQuery()
                .eq(TDDimensionIntegral::getType, SnapshotAccountTypeEnum.PERSON.getCode())
                .eq(TDDimensionIntegral::getCodeFlag, account)
                .eq(TDDimensionIntegral::getYear, String.valueOf(DateUtil.thisYear()));
        if(Objects.nonNull(periodTypeEnum)){
            lambdaQueryWrapper.eq(TDDimensionIntegral::getPeriodType, periodTypeEnum.getCode());
        }
        if(StringUtils.isNotBlank(periodFlag)){
            lambdaQueryWrapper.eq(TDDimensionIntegral::getPeriodFlag, periodFlag);
        }
        //查询指标得分详情
        List<TDDimensionIntegral> dimensionIntegrals = dimensionIntegralService.list(lambdaQueryWrapper);
        return dimensionIntegrals;
    }
}
