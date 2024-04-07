package com.longfor.datav.admin.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.longfor.datav.admin.service.IMemberService;
import com.longfor.datav.common.constants.CommonConstant;
import com.longfor.datav.common.dto.DimensionInfoDTO;
import com.longfor.datav.common.dto.MemberSnapshotDimensionInfoDTO;
import com.longfor.datav.common.dto.SnapshotDataDTO;
import com.longfor.datav.common.dto.SnapshotScoreDetailDTO;
import com.longfor.datav.common.enums.*;
import com.longfor.datav.common.vo.PageResponse;
import com.longfor.datav.common.vo.req.MemberInfoRequest;
import com.longfor.datav.common.vo.req.MemberListRequest;
import com.longfor.datav.common.vo.resp.*;
import com.longfor.datav.dao.entity.*;
import com.longfor.datav.dao.service.*;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 成员管理接口实现类
 * @author zhaoyl
 * @date 2024/1/26 13:47
 * @since 1.0
 */
@Service
public class MemberServiceImpl implements IMemberService {
    @Autowired
    private ITDAccountService accountService;

    @Autowired
    private ITDAccountTeamRelationService accountTeamRelationService;

    @Autowired
    private ITDTeamService teamService;

    @Autowired
    private ITDSnapshotTableService snapshotTableService;

    @Autowired
    private ITDTimeSprintRelationService timeSprintRelationService;

    //团队综合模块json
    private static final String COMMON_CODE_FLAG = "zonghemokuai";

    @Override
    public PageResponse<List<MemberListResponse>> memberList(MemberListRequest request) {
        LambdaQueryWrapper<TDAccount> lambdaQueryWrapper = buildMemberListQueryWrapper(request);
        Page<TDAccount> page = accountService.page(new Page<>(request.getPageNum(), request.getPageSize()), lambdaQueryWrapper);
        if(CollectionUtils.isEmpty(page.getRecords())){
            return PageResponse.page(Lists.newArrayList(), 0L);
        }
        return PageResponse.page(handelMemberData(page.getRecords()), page.getTotal());
    }

    private List<MemberListResponse> handelMemberData(List<TDAccount> list){
        //根据oa账号查询关联团队
        List<String> oaList = list.stream().map(TDAccount::getAccount).filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList());
        //账号团队信息Map
        Map<String, TDAccountTeamRelation> accountTeamMap = null;
        //团队信息Map
        Map<String, TDTeam> teamInfoMap = null;
        //快照得分年度数据
        Map<String,MemberInfoResponse> memberInfoMap = null;
        if(!CollectionUtils.isEmpty(oaList)){
            List<TDAccountTeamRelation> relationList = accountTeamRelationService.list(Wrappers.<TDAccountTeamRelation>lambdaQuery()
                    .in(TDAccountTeamRelation::getAccount, oaList));
            accountTeamMap = ListUtils.emptyIfNull(relationList).stream()
                    .sorted(Comparator.comparing(TDAccountTeamRelation::getCreateTime))
                    .collect(Collectors.toMap(TDAccountTeamRelation::getAccount, Function.identity(), (a, b) -> b));
            //获取个人得分快照信息
            memberInfoMap = ListUtils.emptyIfNull(getPersonSnapshotDataList(String.valueOf(DateUtil.thisYear()), oaList)).stream().map(x -> {
                JSONObject contentJson = JSONObject.parseObject(x.getContent());
                if (Objects.isNull(contentJson)) {
                    return null;
                }
                JSONObject memberInfoJson = contentJson.getJSONObject(CommonConstant.SNAPSHOT_DATA_OF_PERSON_INFO);
                if (Objects.isNull(memberInfoJson)) {
                    return null;
                }
                MemberInfoResponse response = JSONObject.parseObject(memberInfoJson.toJSONString(), MemberInfoResponse.class);
                return response;
            }).filter(Objects::nonNull).collect(Collectors.toMap(MemberInfoResponse::getOaAccount, Function.identity(), (a, b) -> b));
        }
        if(Objects.nonNull(accountTeamMap)){
            List<String> teamCodeList = accountTeamMap.values().stream().map(TDAccountTeamRelation::getTeamCode).filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList());
            if(!CollectionUtils.isEmpty(teamCodeList)){
                List<TDTeam> teamList = teamService.list(Wrappers.<TDTeam>lambdaQuery().in(TDTeam::getCode, teamCodeList));
                teamInfoMap = ListUtils.emptyIfNull(teamList).stream().collect(Collectors.toMap(TDTeam::getCode, Function.identity(), (a, b) -> b));
            }
        }
        Map<String, TDAccountTeamRelation> finalAccountTeamMap = accountTeamMap;
        Map<String, TDTeam> finalTeamInfoMap = teamInfoMap;
        Map<String,MemberInfoResponse> finalMemberInfoMap = memberInfoMap;
        return ListUtils.emptyIfNull(list).stream().map(x -> {
            MemberListResponse response = new MemberListResponse();
            response.setOaAccount(x.getAccount());
            response.setName(x.getName());
            Optional.ofNullable(finalMemberInfoMap).map(a -> a.get(x.getAccount())).ifPresent(a -> {
                response.setScore(String.valueOf(a.getScore()));
                response.setRank(a.getRank());
            });
            response.setRole(Optional.of(x.getRole()).map(EmployeeRoleEnum::fromCode).map(EmployeeRoleEnum::getMsg).orElse("-"));
            //员工状态
            response.setStatus(Optional.ofNullable(x.getStatus()).map(EmployeeStatusEnum::fromCode).map(EmployeeStatusEnum::getMsg).orElse("-"));
            //所属岗位
            response.setPostName(Optional.ofNullable(x.getJob()).map(PostTypeEnum::fromCode).map(PostTypeEnum::getMsg).orElse("-"));
            Optional.ofNullable(finalAccountTeamMap).map(a -> a.get(x.getAccount())).ifPresent(a -> {
                //所属团队
                response.setTeamCode(a.getTeamCode());
                Optional.ofNullable(finalTeamInfoMap).map(t -> t.get(a.getTeamCode())).ifPresent(t -> {
                    response.setTeamName(t.getName());
                    response.setTeamSdm(t.getSdm());
                    response.setPrincipalName(t.getPrincipal());
                    response.setDesc(t.getDesc());
                });
            });
            return response;
        }).sorted((a,b) -> {
            Double score1 = Double.parseDouble(StrUtil.blankToDefault(a.getScore(),"0"));
            Double score2 = Double.parseDouble(StrUtil.blankToDefault(b.getScore(),"0"));
            return score2.compareTo(score1);
        }).collect(Collectors.toList());
    }
    private LambdaQueryWrapper<TDAccount> buildMemberListQueryWrapper(MemberListRequest request) {
        LambdaQueryWrapper<TDAccount> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(StringUtils.isNotBlank(request.getName()),TDAccount::getName, request.getName());
        lambdaQueryWrapper.eq(StringUtils.isNotBlank(request.getRole()),TDAccount::getRole, request.getRole());
        lambdaQueryWrapper.in(!CollectionUtils.isEmpty(request.getAccounts()),TDAccount::getAccount, request.getAccounts());
        //未删除的
        lambdaQueryWrapper.eq(TDAccount::getIsDelete, DeleteStatusEnum.NO.getCode());
        return lambdaQueryWrapper;
    }

    /**
     * 根据冲刺获取月份
     * @param sprintCycle
     * @return
     */
    private String sprintToMonth(String sprintCycle){
        if(StringUtils.isBlank(sprintCycle)){
            return null;
        }
        //todo 目前只处理冲刺不跨月的情况，跨月取最大日期的月份
        return Optional.ofNullable(timeSprintRelationService.getOne(Wrappers.<TDTimeSprintRelation>lambdaQuery()
                .eq(TDTimeSprintRelation::getPeriod, sprintCycle)
                .eq(TDTimeSprintRelation::getYear,String.valueOf(DateUtil.thisYear())).last("limit 1")))
                .map(x -> DateUtil.month(x.getEndTime()) + 1)
                .map(String::valueOf)
                .orElse(null);
    }
    @Override
    public MemberInfoResponse memberInfo(MemberInfoRequest request) {
        List<SnapshotDataDTO> personSnapshotDataList = getPersonSnapshotDataList(StrUtil.blankToDefault(request.getPeriod(),String.valueOf(DateUtil.thisYear())), Arrays.asList(request.getOaAccount()));
        if(CollectionUtils.isEmpty(personSnapshotDataList)){
            return null;
        }
        SnapshotDataDTO snapshotDataDTO = personSnapshotDataList.get(0);
        JSONObject contentJson = JSONObject.parseObject(snapshotDataDTO.getContent());
        if(Objects.isNull(contentJson)){
          return null;
        }
        JSONObject memberInfoJson = contentJson.getJSONObject(CommonConstant.SNAPSHOT_DATA_OF_PERSON_INFO);
        JSONArray itemScoreJson = contentJson.getJSONArray(CommonConstant.SNAPSHOT_DATA_OF_ITEM_SCORE);
        MemberInfoResponse response = JSONObject.parseObject(memberInfoJson.toJSONString(), MemberInfoResponse.class);
        response.setList(JSONArray.parseArray(itemScoreJson.toJSONString(), MemberInfoResponse.ScoreInfo.class));
        //计算团队得分
        response.setTeamScore(getTeamAvgScore(response.getTeamCode()));
        return response;
    }

    /**
     * 获取团队平均得分
     * @param teamCode
     * @return
     */
    private Integer getTeamAvgScore(String teamCode) {
        if(StringUtils.isBlank(teamCode)){
            return 0;
        }
        TDSnapshotTable snapshotTable = snapshotTableService.getOne(Wrappers.<TDSnapshotTable>lambdaQuery()
                .eq(TDSnapshotTable::getType, SnapshotAccountTypeEnum.TEAM.getCode())
                .eq(TDSnapshotTable::getYear, String.valueOf(DateUtil.thisYear()))
                .eq(TDSnapshotTable::getCodeFlag,COMMON_CODE_FLAG)
                .eq(TDSnapshotTable::getPeriodFlag, String.valueOf(DateUtil.thisYear()))
                .eq(TDSnapshotTable::getModelFlag, TeamModelDataEnum.TEAM_OVERALL_STANDING.getCode())
                .orderByDesc(TDSnapshotTable::getSnapshotTime)
                .last(" limit 1"));
        if(Objects.isNull(snapshotTable) || StringUtils.isBlank(snapshotTable.getContent())){
            return 0;
        }
        List<TeamOverallStandingsResponse> teamOverallStandingsResponses = JSONObject.parseArray(snapshotTable.getContent(), TeamOverallStandingsResponse.class);
        double teamOveralScore = ListUtils.emptyIfNull(teamOverallStandingsResponses).stream()
                .filter(x -> StringUtils.equals(teamCode,x.getTeamCode()))
                .mapToDouble(TeamOverallStandingsResponse::getScore)
                .sum();
        //计算冲刺个数
        List<TDSnapshotTable> sprintCount = snapshotTableService.list(Wrappers.<TDSnapshotTable>lambdaQuery()
                .select(TDSnapshotTable::getPeriodFlag)
                .eq(TDSnapshotTable::getType, SnapshotAccountTypeEnum.TEAM.getCode())
                .eq(TDSnapshotTable::getYear, String.valueOf(DateUtil.thisYear()))
                .ne(TDSnapshotTable::getPeriodFlag,String.valueOf(DateUtil.thisYear()))
                .eq(TDSnapshotTable::getCodeFlag,teamCode)
                .groupBy(TDSnapshotTable::getPeriodFlag));

        //返回平均分数
        return sprintCount.size() == 0 ? 0 :  (int)(teamOveralScore / sprintCount.size());
    }

    @Override
    public List<MemberScoreHistoryResponse> scoreHistory(MemberInfoRequest request) {
        List<SnapshotDataDTO> personSnapshotDataList = getPersonSnapshotDataList(request.getPeriod(), Arrays.asList(request.getOaAccount()));
        return ListUtils.emptyIfNull(personSnapshotDataList)
                .stream()
                .sorted(Comparator.comparing(SnapshotDataDTO::getSprintCycle))
                .map(x -> {
                    MemberScoreHistoryResponse response = new MemberScoreHistoryResponse();
                    response.setSprintCycle(x.getSprintCycle());
                    JSONObject jsonObject = JSONObject.parseObject(x.getContent()).getJSONObject(CommonConstant.SNAPSHOT_DATA_OF_PERSON_INFO);
                    if(Objects.nonNull(jsonObject)){
                        response.setScore(jsonObject.getString("score"));
                        response.setRank(jsonObject.getInteger("rank"));
                    }
                    return response;
                })
                .collect(Collectors.toList());

    }
    /**
     * 根据条件查询快照历史数据
     * @param sprintCycle 冲刺周期
     * @param oaAccount oa账号
     * @return
     */
    private List<SnapshotDataDTO> getPersonSnapshotDataList(String sprintCycle,List<String> oaAccounts){
        List<TDSnapshotTable> dataList = snapshotTableService.list(Wrappers.<TDSnapshotTable>lambdaQuery()
                .eq(TDSnapshotTable::getType, SnapshotAccountTypeEnum.PERSON.getCode())
                .eq(TDSnapshotTable::getYear, String.valueOf(DateUtil.thisYear()))
                .in(!CollectionUtils.isEmpty(oaAccounts),TDSnapshotTable::getCodeFlag, oaAccounts)
                .ne(StringUtils.isBlank(sprintCycle),TDSnapshotTable::getPeriodFlag, DateUtil.thisYear())
                .eq(StringUtils.isNotBlank(sprintCycle),TDSnapshotTable::getPeriodFlag, sprintCycle));
        if(CollectionUtils.isEmpty(dataList)){
            return Lists.newArrayList();
        }
        return dataList.stream()
                .filter(x -> StringUtils.isNotBlank(x.getContent()))
                .map(x -> {
                    SnapshotDataDTO snapshotDataDTO = new SnapshotDataDTO();
                    snapshotDataDTO.setCode(x.getCodeFlag());
                    snapshotDataDTO.setName(x.getNameFlag());
                    snapshotDataDTO.setYear(x.getYear());
                    snapshotDataDTO.setSprintCycle(x.getPeriodFlag());
                    snapshotDataDTO.setContent(x.getContent());
                    return snapshotDataDTO;
                }).collect(Collectors.toList());
    }

    @Override
    public List<MemberDimensionListResponse> dataInfo(MemberInfoRequest request) {
        List<SnapshotDataDTO> personSnapshotDataList = getPersonSnapshotDataList(StrUtil.blankToDefault(request.getPeriod(),String.valueOf(DateUtil.thisYear())), Arrays.asList(request.getOaAccount()));
        if(CollectionUtils.isEmpty(personSnapshotDataList)){
            return Lists.newArrayList();
        }
        JSONArray itemScoreJson = JSONObject.parseObject(personSnapshotDataList.get(0).getContent()).getJSONArray(CommonConstant.SNAPSHOT_DATA_OF_ITEM_SCORE);
        JSONArray dimensionListJson = JSONObject.parseObject(personSnapshotDataList.get(0).getContent()).getJSONArray(CommonConstant.SNAPSHOT_DATA_OF_HISTORY_SCORE_DETAIL);
        //纬度得分数据
        List<MemberScoreHistoryResponse> scoreInfos = JSONArray.parseArray(itemScoreJson.toJSONString(), MemberScoreHistoryResponse.class);
        //得分详情数据
        List<SnapshotScoreDetailDTO> scoreHistoryDetail = JSONArray.parseArray(dimensionListJson.toJSONString(), SnapshotScoreDetailDTO.class);

        return ListUtils.emptyIfNull(scoreInfos).stream().map(x -> {
                    MemberDimensionListResponse response = new MemberDimensionListResponse();
                    response.setDimensionCode(x.getCode());
                    response.setDimensionName(x.getMetrics());
                    response.setDimensionDesc(x.getDesc());
                    response.setScore(x.getScore());
                    response.setIsParent(1);
                    response.setChildList(ListUtils.emptyIfNull(scoreHistoryDetail).stream()
                            .filter(y -> StringUtils.equals(x.getCode(),y.getParentDimensionCode()))
                            .map(y -> {
                                MemberScoreHistoryResponse child = new MemberScoreHistoryResponse();
                                child.setSprintCycle(y.getDate());
                                child.setScore(String.valueOf(y.getScore()));
                                child.setMetrics(y.getDimensionName());
                                child.setCode(y.getDimensionCode());
                                child.setDesc(y.getDimensionDesc());
                                child.setValue(y.getValue());
                                return child;
                            }).collect(Collectors.toList()));
                    return response;
                }
        ).collect(Collectors.toList());
    }

    @Override
    public List<MemberScoreHistoryResponse> scoreInfo(MemberInfoRequest request) {
        List<SnapshotDataDTO> personSnapshotDataList = getPersonSnapshotDataList(request.getPeriod(), Arrays.asList(request.getOaAccount()));
        return ListUtils.emptyIfNull(personSnapshotDataList)
                .stream()
                .sorted(Comparator.comparing(SnapshotDataDTO::getSprintCycle))
                .flatMap(x -> {
                    JSONArray jsonArray = JSONObject.parseObject(x.getContent()).getJSONArray(CommonConstant.SNAPSHOT_DATA_OF_HISTORY_SCORE_DETAIL);
                    if(Objects.isNull(jsonArray)){
                        return null;
                    }
                    List<SnapshotScoreDetailDTO> snapshotScoreDetailDTOs = JSONArray.parseArray(jsonArray.toJSONString(), SnapshotScoreDetailDTO.class);
                    if(CollectionUtils.isEmpty(snapshotScoreDetailDTOs)){
                        return null;
                    }
                    //根据纬度编码过滤
                    if(StringUtils.isBlank(request.getDimensionCode())){
                        snapshotScoreDetailDTOs = snapshotScoreDetailDTOs.stream()
                                .filter(y -> StringUtils.equals(request.getDimensionCode(),y.getDimensionCode()) || StringUtils.equals(request.getDimensionCode(),y.getParentDimensionCode()))
                                .collect(Collectors.toList());
                    }
                    return ListUtils.emptyIfNull(snapshotScoreDetailDTOs).stream()
                            .map(y -> {
                                MemberScoreHistoryResponse response = new MemberScoreHistoryResponse();
                                response.setSprintCycle(y.getDate());
                                response.setScore(String.valueOf(y.getScore()));
                                response.setMetrics(y.getDimensionName());
                                return response;
                            });
                })
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(MemberScoreHistoryResponse::getSprintCycle).reversed())
                .collect(Collectors.toList());
    }

    public static void main(String[] args) {
        System.out.println(DateUtil.month(DateUtil.parseDate("2021-12-31 00:00:00")) + 1);
    }
}
