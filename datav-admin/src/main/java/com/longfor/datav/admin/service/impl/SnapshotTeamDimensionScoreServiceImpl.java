package com.longfor.datav.admin.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.longfor.datav.admin.service.SnapshotTeamDimensionScoreService;
import com.longfor.datav.admin.service.TeamCommonService;
import com.longfor.datav.common.enums.DimensionTypeEnum;
import com.longfor.datav.common.enums.ScoreDirectionEnum;
import com.longfor.datav.common.enums.SnapshotAccountTypeEnum;
import com.longfor.datav.common.enums.TeamModelDataEnum;
import com.longfor.datav.common.utils.TimeUtil;
import com.longfor.datav.common.vo.TDDimensionFatherSonRelationVo;
import com.longfor.datav.common.vo.resp.TeamDimensionScoreResponse;
import com.longfor.datav.common.vo.resp.TeamOverallStandingsResponse;
import com.longfor.datav.dao.entity.TDDimension;
import com.longfor.datav.dao.entity.TDDimensionIntegral;
import com.longfor.datav.dao.entity.TDSnapshotTable;
import com.longfor.datav.dao.entity.TDTeam;
import com.longfor.datav.dao.service.ITDDimensionService;
import com.longfor.datav.dao.service.ITDSnapshotTableService;
import com.longfor.datav.dao.service.ITDTeamService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 快照每个指标的团队评分服务实现
 *
 * @author zyh
 * @date 2024-02-01
 * @since jdk 1.8
 */

@Service
@Slf4j
public class SnapshotTeamDimensionScoreServiceImpl implements SnapshotTeamDimensionScoreService {

    @Resource
    private ITDDimensionService dimensionService;

    @Resource
    private ITDSnapshotTableService tableService;

    @Resource
    private ITDTeamService teamService;

    @Resource
    private TeamCommonService commonService;

    private static final String COMMON_CODE_FLAG = "zonghemokuai";

    private static final String COMMON_NAME_FLAG = "综合模块";

    private static final int ZERO_SCORE = 0;

    @Override
    public void snapshotPeriodData(List<TDDimensionIntegral> integralList) {
        //1.1.获取指标对应名称编码
        Map<String, TDDimension> dimensionMap = dimensionService.getDimensionCodeNameMap();
        //1.2.获取团队第一级指标数据，需要全部展示
        List<TDDimension> dimensionList = dimensionService.getDimensionByLevel(1, DimensionTypeEnum.TEAM.getCode());

        //2.按照周期字段进行分组
        Map<String, List<TDDimensionIntegral>> integralMap = commonService.getPeriodDimensionIntegral(integralList);
        Map<String, TDDimension> firstLevelDimensionMap = dimensionService.getFirstDimensionCodeEntry();

        //4.按照周期处理每个指标的团队排名
        for (Map.Entry<String, List<TDDimensionIntegral>> entry : integralMap.entrySet()) {
            //4.1获取每个团队的排名数据
            List<TeamDimensionScoreResponse> responseList = getDimensionScore(entry.getValue()
                    , dimensionMap, dimensionList, firstLevelDimensionMap);
            getPeriodHistoryComparison(entry.getKey(), responseList);

            //4.2保存数据
            TDSnapshotTable snapshotTable = new TDSnapshotTable();
            snapshotTable.setType(SnapshotAccountTypeEnum.TEAM.getCode());
            snapshotTable.setCodeFlag(COMMON_CODE_FLAG);
            snapshotTable.setNameFlag(COMMON_NAME_FLAG);
            snapshotTable.setPeriodFlag(entry.getKey());
            snapshotTable.setSnapshotTime(LocalDateTime.now());
            snapshotTable.setYear(TimeUtil.getCurrentYear());
            snapshotTable.setContent(JSON.toJSONString(responseList));
            snapshotTable.setModelFlag(TeamModelDataEnum.TEAM_DIMENSION_SCORE.getCode());

            tableService.saveAndUpdate(snapshotTable);
        }
    }

    @Override
    public void snapshotYearData(List<TDDimensionIntegral> integralList) {
        //1.1.获取指标对应名称编码
        Map<String, TDDimension> dimensionMap = dimensionService.getDimensionCodeNameMap();
        Map<String, TDDimension> firstLevelDimensionMap = dimensionService.getFirstDimensionCodeEntry();

        //1.2.获取团队第一级指标数据，需要全部展示
        List<TDDimension> dimensionList = dimensionService.getDimensionByLevel(1, DimensionTypeEnum.TEAM.getCode());
        List<TeamDimensionScoreResponse> responseList = getDimensionScore(integralList, dimensionMap
                , dimensionList, firstLevelDimensionMap);
        //1.2获取环比数据
        getYearHistoryComparison(responseList);

        //2.1保存数据
        TDSnapshotTable snapshotTable = new TDSnapshotTable();
        snapshotTable.setType(SnapshotAccountTypeEnum.TEAM.getCode());
        snapshotTable.setCodeFlag(COMMON_CODE_FLAG);
        snapshotTable.setNameFlag(COMMON_NAME_FLAG);
        snapshotTable.setPeriodFlag(TimeUtil.getCurrentYear());
        snapshotTable.setContent(JSON.toJSONString(responseList));
        snapshotTable.setSnapshotTime(LocalDateTime.now());
        snapshotTable.setYear(TimeUtil.getCurrentYear());
        snapshotTable.setModelFlag(TeamModelDataEnum.TEAM_DIMENSION_SCORE.getCode());

        //2.2校验是否存在数据
        Long id = tableService.checkTDSnapshotTable(snapshotTable);
        if (null != id) {
            snapshotTable.setId(id);
        }

        //2.3保存当前数据
        tableService.saveOrUpdate(snapshotTable);
    }


    private List<TeamDimensionScoreResponse> getDimensionScore(List<TDDimensionIntegral> integralList, Map<String, TDDimension> dimensionMap
            , List<TDDimension> dimensionList, Map<String, TDDimension> firstLevelDimensionMap) {
        //1获取指标数据，以及每个二层指标包含的三层指标的积分关系
        Map<String, List<TDDimensionIntegral>> dimensionListMap = new HashMap<>();

        //2初始化指标map
        for (TDDimension dimension : dimensionList) {
            dimensionListMap.put(dimension.getCode(), new ArrayList<>());
        }

        //3根据有变动的积分记录，按照二层指标进行分类
        for (TDDimensionIntegral tdDimensionIntegral : integralList) {
            TDDimensionFatherSonRelationVo vo = commonService.getTDDimensionByChildCode(tdDimensionIntegral.getDimensionCode());
            if (ObjectUtils.isEmpty(vo)) {
                continue;
            }
            if (dimensionListMap.containsKey(vo.getPValue())) {
                List<TDDimensionIntegral> tdDimensionIntegrals = dimensionListMap.get(vo.getPValue());
                tdDimensionIntegrals.add(tdDimensionIntegral);
                dimensionListMap.put(vo.getPValue(), tdDimensionIntegrals);
            }
        }

        //4根据各个指标计算出每个团队的分数以及排名
        List<TeamDimensionScoreResponse> responseList = new ArrayList<>();
        for (Map.Entry<String, List<TDDimensionIntegral>> listEntry : dimensionListMap.entrySet()) {
            //4.1首先获取指标信息
            TeamDimensionScoreResponse response = new TeamDimensionScoreResponse();
            response.setDimensionCode(listEntry.getKey());
            if (dimensionMap.containsKey(listEntry.getKey())) {
                response.setDimensionName(dimensionMap.get(listEntry.getKey()).getName());
            }

            //4.2获取每个团队的分数
            response.setRankList(getDimensionOverallStandings(listEntry.getValue(), dimensionMap, firstLevelDimensionMap));

            //4.3对象赋值给list
            responseList.add(response);
        }

        return responseList;
    }

    /**
     * 获取每个团队的排名数据
     *
     * @param integralList list
     * @return rank
     */
    private List<TeamOverallStandingsResponse> getDimensionOverallStandings(List<TDDimensionIntegral> integralList
            , Map<String, TDDimension> dimensionMap, Map<String, TDDimension> firstLevelDimensionMap) {
        List<TeamOverallStandingsResponse> responseList = new ArrayList<>();
        //1.首先按照团队维度进行区分
        Map<String, List<TDDimensionIntegral>> teamTDDimensionMap = commonService.getCodeDimensionIntegral(integralList);

        //2.计算各个团队的数据
        for (Map.Entry<String, List<TDDimensionIntegral>> entry : teamTDDimensionMap.entrySet()) {
            //2.1获取团队数据
            LambdaQueryWrapper<TDTeam> tdTeamLambdaQueryWrapper = new LambdaQueryWrapper<>();
            tdTeamLambdaQueryWrapper.eq(TDTeam::getCode, entry.getKey());
            TDTeam team = teamService.getOne(tdTeamLambdaQueryWrapper);
            if (ObjectUtils.isEmpty(team)) {
                continue;
            }

            TeamOverallStandingsResponse response = new TeamOverallStandingsResponse();
            //2.2团队数据进行赋值
            response.setTeamCode(team.getCode());
            response.setTeamName(team.getName());
            response.setScore(commonService.calTeamOverallStandings(entry.getValue(), dimensionMap, firstLevelDimensionMap));

            //2.3环比数据设置
            response.setHistoricalComparison(0);
            response.setDirection(ScoreDirectionEnum.CONSTANT.getCode());
            responseList.add(response);
        }

        //3.按照积分进行排序
        responseList = responseList.stream().sorted(Comparator.comparingDouble(TeamOverallStandingsResponse::getScore)
                .reversed()).collect(Collectors.toList());

        //4.添加序号
        for (int i = 1; i <= responseList.size(); i++) {
            responseList.get(i - 1).setIndex(i);
        }

        return responseList;
    }

    /**
     * 获取冲刺中每个指标团队的排名对比
     * @param currentPeriod  当前冲刺
     * @param responseList   list
     */
    private void getPeriodHistoryComparison(String currentPeriod, List<TeamDimensionScoreResponse> responseList) {
        //1.获取当前周期上一个周期编码
        String historyPeriod = tableService.getLastPeriodByPeriod(currentPeriod, TimeUtil.getCurrentYear());

        //2.计算环比变化
        getHistoricalComparison(historyPeriod, responseList);
    }

    /**
     * 针对团队每项指标与上一年数据进行比对
     * @param responseList  list
     */
    private void getYearHistoryComparison(List<TeamDimensionScoreResponse> responseList) {
        //1.获取上一年周期
        int year = Integer.parseInt(TimeUtil.getCurrentYear());
        String lastYear = String.valueOf(year - 1);

        //2.计算环比变化
        getHistoricalComparison(lastYear, responseList);
    }

    /**
     * 获取当前周期指标团队排名的环比数据
     * @param period         上个周期
     * @param responseList   指标团队积分排名列表
     */
    private void getHistoricalComparison(String period, List<TeamDimensionScoreResponse> responseList) {
        //1.查询历史周期的快照数据
        LambdaQueryWrapper<TDSnapshotTable> snapshotTableLambdaQueryWrapper = new LambdaQueryWrapper<>();
        snapshotTableLambdaQueryWrapper.eq(TDSnapshotTable::getPeriodFlag, period)
                .eq(TDSnapshotTable::getType, 1)
                .eq(TDSnapshotTable::getCodeFlag, COMMON_CODE_FLAG)
                .eq(TDSnapshotTable::getModelFlag, TeamModelDataEnum.TEAM_DIMENSION_SCORE.getCode());
        TDSnapshotTable snapshotTable = tableService.getOne(snapshotTableLambdaQueryWrapper);
        if(ObjectUtils.isEmpty(snapshotTable)) {
            //前一周期数据未获取到
            return;
        }

        //2.计算环比增长数据
        //2.1首先获取出各个指标的团队排名情况
        if(StringUtils.isEmpty(snapshotTable.getContent())) {
            return;
        }
        List<TeamDimensionScoreResponse> scoreResponseList = JSON.parseArray(snapshotTable.getContent(), TeamDimensionScoreResponse.class);
        if(CollectionUtils.isEmpty(scoreResponseList)) {
            return;
        }
        //2.2将其转换成以指标code为key的map
        Map<String, TeamDimensionScoreResponse> scoreResponseMap = scoreResponseList.stream().collect(Collectors
                .toMap(TeamDimensionScoreResponse::getDimensionCode, Function.identity()));

        //2.3循环遍历当前周期的数据
        for(TeamDimensionScoreResponse scoreResponse : responseList) {
            //2.3.1首先获取历史的指标数据
            if(!scoreResponseMap.containsKey(scoreResponse.getDimensionCode())) {
                //当前指标在历史数据中未获取
                continue;
            }
            TeamDimensionScoreResponse historyResponse = scoreResponseMap.get(scoreResponse.getDimensionCode());

            //2.3.2计算历史数据和当前数据的环比
            calHistoryComparison(historyResponse, scoreResponse);
        }
    }

    /**
     * 计算相邻两个周期的数据变化情况
     * @param historyResponse   历史数据
     * @param scoreResponse     当前数据
     */
    private void calHistoryComparison(TeamDimensionScoreResponse historyResponse, TeamDimensionScoreResponse scoreResponse) {
        //1历史数据团队排名转成map
        List<TeamOverallStandingsResponse> historyStandingsResponseList = historyResponse.getRankList();
        Map<String, TeamOverallStandingsResponse> historyStandingsResponseMap = historyStandingsResponseList
                .stream().collect(Collectors.toMap(TeamOverallStandingsResponse::getTeamCode, Function.identity()));

        //2遍历当前周期的数据，计算与历史数据的排名变化
        List<TeamOverallStandingsResponse> standingsResponseList = scoreResponse.getRankList();
        List<TeamOverallStandingsResponse> newStandingsResponseList = new ArrayList<>();
        for(TeamOverallStandingsResponse standingsResponse : standingsResponseList) {
            //2.1.获取当前团队历史排名
            if(!historyStandingsResponseMap.containsKey(standingsResponse.getTeamCode())) {
                newStandingsResponseList.add(standingsResponse);
                continue;
            }
            TeamOverallStandingsResponse historyStandingsResponse = historyStandingsResponseMap.get(standingsResponse.getTeamCode());

            //2.2.比较当前排名和历史排名，设置环比字段和排名上升或下降
            if(standingsResponse.getIndex() > historyStandingsResponse.getIndex()) {
                standingsResponse.setHistoricalComparison(standingsResponse.getIndex() - historyStandingsResponse.getIndex());
                standingsResponse.setDirection(ScoreDirectionEnum.UP.getCode());
            }else if (standingsResponse.getIndex() < historyStandingsResponse.getIndex()) {
                standingsResponse.setHistoricalComparison(historyStandingsResponse.getIndex() - standingsResponse.getIndex());
                standingsResponse.setDirection(ScoreDirectionEnum.DOWN.getCode());
            }else {
                standingsResponse.setHistoricalComparison(ZERO_SCORE);
                standingsResponse.setDirection(ScoreDirectionEnum.CONSTANT.getCode());
            }
            newStandingsResponseList.add(standingsResponse);
        }
        scoreResponse.setRankList(newStandingsResponseList);
    }
}
