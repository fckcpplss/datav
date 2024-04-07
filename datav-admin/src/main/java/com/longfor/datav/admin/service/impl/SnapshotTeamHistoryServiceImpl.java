package com.longfor.datav.admin.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.longfor.datav.admin.service.SnapshotTeamHistoryService;
import com.longfor.datav.admin.service.TeamCommonService;
import com.longfor.datav.common.enums.SnapshotAccountTypeEnum;
import com.longfor.datav.common.enums.TeamModelDataEnum;
import com.longfor.datav.common.utils.TimeUtil;
import com.longfor.datav.common.vo.TeamHistoryVo;
import com.longfor.datav.common.vo.resp.TeamHistoryResponse;
import com.longfor.datav.dao.entity.*;
import com.longfor.datav.dao.service.ITDDimensionService;
import com.longfor.datav.dao.service.ITDSnapshotTableService;
import com.longfor.datav.dao.service.ITDTeamService;
import com.longfor.datav.dao.service.ITDTimeSprintRelationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * 快照团队历史数据实现
 *
 * @author zyh
 * @date 2024-02-01
 * @since jdk 1.8
 */

@Service
@Slf4j
public class SnapshotTeamHistoryServiceImpl implements SnapshotTeamHistoryService {
    @Resource
    private ITDTeamService teamService;

    @Resource
    private ITDTimeSprintRelationService relationService;

    @Resource
    private ITDSnapshotTableService tableService;

    @Resource
    private TeamCommonService commonService;

    @Resource
    private ITDDimensionService dimensionService;

    @Override
    public void snapshotPeriodData(List<TDDimensionIntegral> integralList) {
        //历史数据板块无冲刺周期概念
    }

    @Override
    public void snapshotYearData(List<TDDimensionIntegral> integralList) {
        //1.按照团队去获取每个维度的数据
        Map<String, List<TDDimensionIntegral>> teamTDDimensionMap = commonService.getCodeDimensionIntegral(integralList);
        Map<String, TDDimension> firstLevelDimensionMap = dimensionService.getFirstDimensionCodeEntry();
        Map<String, TDDimension> dimensionMap = dimensionService.getDimensionCodeNameMap();

        //2.按照团队遍历
        for (Map.Entry<String, List<TDDimensionIntegral>> entry : teamTDDimensionMap.entrySet()) {
            //2.1首先查询团队信息
            LambdaQueryWrapper<TDTeam> teamLambdaQueryWrapper = new LambdaQueryWrapper<>();
            teamLambdaQueryWrapper.eq(TDTeam::getCode, entry.getKey());
            TDTeam team = teamService.getOne(teamLambdaQueryWrapper);
            if (ObjectUtils.isEmpty(team)) {
                continue;
            }

            //2.2快照数据赋值团队信息
            TeamHistoryResponse response = new TeamHistoryResponse();
            response.setTeamName(team.getName());

            //2.3计算快照周期内数据
            Map<String, Double> scoreMap = getEveryPeriodScore(entry.getValue(), firstLevelDimensionMap, dimensionMap);

            //2.4所有周期。按照时间顺序排序
            LambdaQueryWrapper<TDTimeSprintRelation> relationLambdaQueryWrapper = new LambdaQueryWrapper<>();
            relationLambdaQueryWrapper.eq(TDTimeSprintRelation::getYear, TimeUtil.getCurrentYear())
                    .orderByAsc(TDTimeSprintRelation::getStartTime);
            List<TDTimeSprintRelation> relationList = relationService.list(relationLambdaQueryWrapper);
            if(CollectionUtils.isEmpty(relationList)) {
                continue;
            }

            List<TeamHistoryVo> teamHistoryVos = new ArrayList<>();
            for (TDTimeSprintRelation relation : relationList) {
                if (scoreMap.containsKey(relation.getPeriod())) {
                    TeamHistoryVo teamHistoryVo = new TeamHistoryVo();
                    teamHistoryVo.setScore(scoreMap.get(relation.getPeriod()));
                    teamHistoryVo.setTime(relation.getPeriod());
                    teamHistoryVos.add(teamHistoryVo);
                }
            }
            response.setHistoryDataList(teamHistoryVos);

            //2.5构建快照数据，存入快照表
            TDSnapshotTable snapshotTable = new TDSnapshotTable();
            snapshotTable.setType(SnapshotAccountTypeEnum.TEAM.getCode());
            snapshotTable.setCodeFlag(entry.getKey());
            snapshotTable.setNameFlag(response.getTeamName());
            snapshotTable.setPeriodFlag(TimeUtil.getCurrentYear());
            snapshotTable.setSnapshotTime(LocalDateTime.now());
            snapshotTable.setYear(TimeUtil.getCurrentYear());
            snapshotTable.setContent(JSON.toJSONString(response));
            snapshotTable.setModelFlag(TeamModelDataEnum.TEAM_HISTORY.getCode());

            tableService.saveAndUpdate(snapshotTable);
        }
    }

    /**
     * 根据指标积分列表获取每个周期的得分情况
     * @param integralList            指标积分列表
     * @param firstLevelDimensionMap  第一层指标map
     * @param dimensionMap            指标map
     * @return                        scoreMap
     */
    private Map<String, Double> getEveryPeriodScore(List<TDDimensionIntegral> integralList
            , Map<String, TDDimension> firstLevelDimensionMap, Map<String, TDDimension> dimensionMap) {
        Map<String, Double> scorePeriodScore = new HashMap<>();
        //1.首先根据周期进行分类
        Map<String, List<TDDimensionIntegral>> integralMap = commonService.getPeriodDimensionIntegral(integralList);

        //2.获取每个周期的分数
        for(Map.Entry<String, List<TDDimensionIntegral>> entry : integralMap.entrySet()) {
            scorePeriodScore.put(entry.getKey(), commonService.calTeamOverallStandings(entry.getValue()
                    , dimensionMap, firstLevelDimensionMap));
        }

        return scorePeriodScore;
    }
}
