package com.longfor.datav.admin.service.impl;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.longfor.datav.admin.service.SnapshotTeamOverallStandingsService;
import com.longfor.datav.admin.service.TeamCommonService;
import com.longfor.datav.common.enums.ScoreDirectionEnum;
import com.longfor.datav.common.enums.SnapshotAccountTypeEnum;
import com.longfor.datav.common.enums.TeamModelDataEnum;
import com.longfor.datav.common.utils.TimeUtil;
import com.longfor.datav.common.vo.resp.TeamOverallStandingsResponse;
import com.longfor.datav.dao.entity.TDDimension;
import com.longfor.datav.dao.entity.TDDimensionIntegral;
import com.longfor.datav.dao.entity.TDSnapshotTable;
import com.longfor.datav.dao.entity.TDTeam;
import com.longfor.datav.dao.service.ITDDimensionService;
import com.longfor.datav.dao.service.ITDSnapshotTableService;
import com.longfor.datav.dao.service.ITDTeamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 团队排名快照服务接口实现
 *
 * @author zyh
 * @date 2024-02-01
 * @since jdk 1.8
 */

@Service
@Slf4j
public class SnapshotTeamOverallStandingsServiceImpl implements SnapshotTeamOverallStandingsService {

    @Resource
    private ITDTeamService teamService;

    @Resource
    private ITDSnapshotTableService tableService;

    @Resource
    private TeamCommonService commonService;

    @Resource
    private ITDDimensionService dimensionService;

    private static final String COMMON_CODE_FLAG = "zonghemokuai";

    private static final String COMMON_NAME_FLAG = "综合模块";

    private static final int ZERO_SCORE = 0;


    /**
     * 按照周期计算团队的排名数据
     *
     * @param integralList list
     */
    @Override
    public void snapshotPeriodData(List<TDDimensionIntegral> integralList) {
        //1.按照周期字段进行分组
        Map<String, List<TDDimensionIntegral>> integralMap = commonService.getPeriodDimensionIntegral(integralList);

        //2.获取code对应的实体map以及子节点code与父节点的关系，统计分数时需要使用
        Map<String, TDDimension> firstLevelDimensionMap = dimensionService.getFirstDimensionCodeEntry();
        Map<String, TDDimension> dimensionMap = dimensionService.getDimensionCodeNameMap();

        //3.按照周期处理每个团队的指标得分
        for (Map.Entry<String, List<TDDimensionIntegral>> entry : integralMap.entrySet()) {
            //3.1.计算各个团队的得分情况
            List<TeamOverallStandingsResponse> responseList = getAllTeamOverallStandings(entry.getValue()
                    , dimensionMap, firstLevelDimensionMap);
            if (CollectionUtils.isEmpty(responseList)) {
                continue;
            }

            //3.2.构建快照数据，存入快照表
            TDSnapshotTable snapshotTable = new TDSnapshotTable();
            snapshotTable.setType(SnapshotAccountTypeEnum.TEAM.getCode());
            snapshotTable.setCodeFlag(COMMON_CODE_FLAG);
            snapshotTable.setNameFlag(COMMON_NAME_FLAG);
            snapshotTable.setPeriodFlag(entry.getKey());
            snapshotTable.setContent(JSON.toJSONString(calculateIndexAndIncrease(entry.getKey(), responseList)));
            snapshotTable.setSnapshotTime(LocalDateTime.now());
            snapshotTable.setModelFlag(TeamModelDataEnum.TEAM_OVERALL_STANDING.getCode());
            snapshotTable.setYear(TimeUtil.getCurrentYear());
            tableService.saveAndUpdate(snapshotTable);
        }
    }

    @Override
    public void snapshotYearData(List<TDDimensionIntegral> integralList) {
        //1.获取code对应的实体map以及子节点code与父节点的关系，统计分数时需要使用
        Map<String, TDDimension> firstLevelDimensionMap = dimensionService.getFirstDimensionCodeEntry();
        Map<String, TDDimension> dimensionMap = dimensionService.getDimensionCodeNameMap();

        //2.获取保存数据
        TDSnapshotTable snapshotTable = new TDSnapshotTable();
        snapshotTable.setType(SnapshotAccountTypeEnum.TEAM.getCode());
        snapshotTable.setCodeFlag(COMMON_CODE_FLAG);
        snapshotTable.setNameFlag(COMMON_NAME_FLAG);
        snapshotTable.setPeriodFlag(TimeUtil.getCurrentYear());
        snapshotTable.setSnapshotTime(LocalDateTime.now());
        snapshotTable.setContent(JSON.toJSONString(calculateIndexAndIncreaseYear(getAllTeamOverallStandings(integralList
                , dimensionMap, firstLevelDimensionMap))));
        snapshotTable.setModelFlag(TeamModelDataEnum.TEAM_OVERALL_STANDING.getCode());
        snapshotTable.setYear(TimeUtil.getCurrentYear());
        tableService.saveAndUpdate(snapshotTable);
    }

    /**
     * 获取每个周期的所有团队总分
     *
     * @param integralList list
     * @return list
     */
    private List<TeamOverallStandingsResponse> getAllTeamOverallStandings(List<TDDimensionIntegral> integralList
            , Map<String, TDDimension> dimensionMap, Map<String, TDDimension> firstLevelDimensionMap) {
        //1.按照团队去获取每个维度的数据
        Map<String, List<TDDimensionIntegral>> teamTDDimensionMap = commonService.getCodeDimensionIntegral(integralList);

        //2.2.计算各个团队的得分情况
        List<TeamOverallStandingsResponse> responseList = new ArrayList<>();
        for (Map.Entry<String, List<TDDimensionIntegral>> listEntry : teamTDDimensionMap.entrySet()) {
            //2.2.1.获取团队的所有指标得分
            TeamOverallStandingsResponse response = getTeamOverallStandings(listEntry.getKey(), listEntry.getValue()
                    , dimensionMap, firstLevelDimensionMap);
            if (ObjectUtils.isEmpty(response)) {
                continue;
            }
            responseList.add(response);
        }

        //2.3.对获取的所有团队进行排名，并计算历史排名
        return responseList.stream().sorted(Comparator.comparingDouble(TeamOverallStandingsResponse::getScore).reversed())
                .collect(Collectors.toList());
    }

    /**
     * 计算每个团队的得分情况
     *
     * @param teamCode     团队code
     * @param integralList 积分数据
     * @return response
     */
    private TeamOverallStandingsResponse getTeamOverallStandings(String teamCode, List<TDDimensionIntegral> integralList
            , Map<String, TDDimension> dimensionMap, Map<String, TDDimension> firstLevelDimensionMap) {
        //1.根据teamCode获取团队数据
        LambdaQueryWrapper<TDTeam> tdTeamLambdaQueryWrapper = new LambdaQueryWrapper<>();
        tdTeamLambdaQueryWrapper.eq(TDTeam::getCode, teamCode);

        TDTeam team = teamService.getOne(tdTeamLambdaQueryWrapper);
        if (ObjectUtils.isEmpty(team)) {
            log.info("[snapshotTeamOverallStandingsData]--获取团队等分情况时，根据团队code获取团队数据为空，团队code:{}", teamCode);
            return null;
        }

        TeamOverallStandingsResponse response = new TeamOverallStandingsResponse();
        response.setTeamName(team.getName());
        response.setTeamCode(team.getCode());
        response.setScore(commonService.calTeamOverallStandings(integralList, dimensionMap, firstLevelDimensionMap));
        return response;
    }

    /**
     * 计算排名以及与历史对比
     *
     * @param responseList response
     * @return list
     */
    private List<TeamOverallStandingsResponse> calculateIndexAndIncrease(String period
            , List<TeamOverallStandingsResponse> responseList) {
        String lastPeriod = tableService.getLastPeriodByPeriod(period, TimeUtil.getCurrentYear());
        return calculateIndexAndIncreaseCommon(lastPeriod, responseList);
    }

    /**
     * 计算当年的快照数据
     *
     * @param responseList list
     * @return list
     */
    private List<TeamOverallStandingsResponse> calculateIndexAndIncreaseYear(List<TeamOverallStandingsResponse> responseList) {
        int year = Integer.parseInt(TimeUtil.getCurrentYear());
        String lastYear = String.valueOf(year - 1);

        return calculateIndexAndIncreaseCommon(lastYear, responseList);
    }

    /**
     * 公共排名统计与历史数据对比
     *
     * @param period       上个周期
     * @param responseList list
     * @return list
     */
    private List<TeamOverallStandingsResponse> calculateIndexAndIncreaseCommon(String period
            , List<TeamOverallStandingsResponse> responseList) {
        List<TeamOverallStandingsResponse> oldResponseList = new ArrayList<>();
        LambdaQueryWrapper<TDSnapshotTable> tableLambdaQueryWrapper = new LambdaQueryWrapper<>();
        tableLambdaQueryWrapper.eq(TDSnapshotTable::getType, SnapshotAccountTypeEnum.TEAM.getCode())
                .eq(TDSnapshotTable::getModelFlag, TeamModelDataEnum.TEAM_OVERALL_STANDING.getCode())
                .eq(TDSnapshotTable::getYear, TimeUtil.getCurrentYear())
                .eq(TDSnapshotTable::getPeriodFlag, period);
        TDSnapshotTable tdSnapshotTable = tableService.getOne(tableLambdaQueryWrapper);
        if (!ObjectUtils.isEmpty(tdSnapshotTable)) {
            oldResponseList = JSONUtil.toList(tdSnapshotTable.getContent(), TeamOverallStandingsResponse.class);
        }

        Map<String, TeamOverallStandingsResponse> standingsResponseMap = oldResponseList.stream()
                .collect(Collectors.toMap(TeamOverallStandingsResponse::getTeamCode, Function.identity()));
        if (CollectionUtils.isEmpty(standingsResponseMap)) {
            //若无历史数据，则map初始化为空实体
            standingsResponseMap = new HashMap<>();
        }

        //2.对比历史数据
        for (int i = 1; i <= responseList.size(); i++) {
            int index = i - 1;
            responseList.get(index).setIndex(i);
            //如果历史数据不存在，则默认为0
            if (standingsResponseMap.containsKey(responseList.get(index).getTeamCode())) {
                int lastIndex = standingsResponseMap.get(responseList.get(index).getTeamCode()).getIndex();
                if (lastIndex > responseList.get(index).getIndex()) {
                    responseList.get(index).setDirection(ScoreDirectionEnum.UP.getCode());
                    responseList.get(index).setHistoricalComparison(lastIndex - responseList.get(index).getIndex());
                } else if (lastIndex < responseList.get(index).getIndex()) {
                    responseList.get(index).setDirection(ScoreDirectionEnum.DOWN.getCode());
                    responseList.get(index).setHistoricalComparison(responseList.get(index).getIndex() - lastIndex);
                } else {
                    responseList.get(index).setHistoricalComparison(ZERO_SCORE);
                    responseList.get(index).setDirection(ScoreDirectionEnum.CONSTANT.getCode());
                }
            } else {
                responseList.get(index).setHistoricalComparison(ZERO_SCORE);
                responseList.get(index).setDirection(ScoreDirectionEnum.CONSTANT.getCode());
            }
        }

        return responseList;
    }
}
