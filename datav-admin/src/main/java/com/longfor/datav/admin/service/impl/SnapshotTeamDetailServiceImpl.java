package com.longfor.datav.admin.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.longfor.datav.admin.service.SnapshotTeamDetailService;
import com.longfor.datav.admin.service.TeamCommonService;
import com.longfor.datav.common.enums.SnapshotAccountTypeEnum;
import com.longfor.datav.common.enums.TeamModelDataEnum;
import com.longfor.datav.common.utils.TimeUtil;
import com.longfor.datav.common.vo.TeamDimensionScoreListVo;
import com.longfor.datav.common.vo.resp.TeamDetailResponse;
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
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 快照团队详情的服务接口实现
 *
 * @author zyh
 * @date 2024-02-01
 * @since jdk 1.8
 */

@Service
@Slf4j
public class SnapshotTeamDetailServiceImpl implements SnapshotTeamDetailService {

    @Resource
    private ITDTeamService teamService;

    @Resource
    private ITDSnapshotTableService tableService;

    @Resource
    private ITDDimensionService dimensionService;

    @Resource
    private TeamCommonService commonService;

    private static final String COMMON_CODE_FLAG = "zonghemokuai";

    @Override
    public void snapshotPeriodData(List<TDDimensionIntegral> integralList) {
        //1.按照周期字段进行分组
        Map<String, List<TDDimensionIntegral>> integralMap = commonService.getPeriodDimensionIntegral(integralList);

        //2.1.获取指标对应名称编码
        Map<String, TDDimension> dimensionMap = dimensionService.getDimensionCodeNameMap();
        Map<String, TDDimension> firstLevelDimensionMap = dimensionService.getFirstDimensionCodeEntry();

        //2.按照周期处理每个团队的指标得分
        for (Map.Entry<String, List<TDDimensionIntegral>> entry : integralMap.entrySet()) {
            getAllTeamOverallStandings(entry.getKey(), entry.getValue(), dimensionMap, firstLevelDimensionMap);
        }
    }

    @Override
    public void snapshotYearData(List<TDDimensionIntegral> integralList) {
        //1.获取指标对应名称编码
        Map<String, TDDimension> dimensionMap = dimensionService.getDimensionCodeNameMap();
        Map<String, TDDimension> firstLevelDimensionMap = dimensionService.getFirstDimensionCodeEntry();
        //2.处理数据
        getAllTeamOverallStandings(null, integralList, dimensionMap, firstLevelDimensionMap);
    }

    /**
     * 获取每个周期的所有团队总分
     *
     * @param integralList list
     */
    private void getAllTeamOverallStandings(String period, List<TDDimensionIntegral> integralList
            , Map<String, TDDimension> dimensionMap, Map<String, TDDimension> firstLevelDimensionMap) {
        //1.按照团队去获取每个维度的数据
        Map<String, List<TDDimensionIntegral>> teamTDDimensionMap = commonService.getCodeDimensionIntegral(integralList);

        //2.计算各个团队得分情况
        List<TeamDetailResponse> teamDetailResponses = new ArrayList<>();
        boolean isReComparison = false;
        for (Map.Entry<String, List<TDDimensionIntegral>> entry : teamTDDimensionMap.entrySet()) {
            TeamDetailResponse teamDetailResponse = new TeamDetailResponse();
            //2.1首先查询团队信息
            LambdaQueryWrapper<TDTeam> teamLambdaQueryWrapper = new LambdaQueryWrapper<>();
            teamLambdaQueryWrapper.eq(TDTeam::getCode, entry.getKey());
            TDTeam team = teamService.getOne(teamLambdaQueryWrapper);
            if (ObjectUtils.isEmpty(team)) {
                continue;
            }
            teamDetailResponse.setTeamName(team.getName());
            teamDetailResponse.setTeamCode(team.getCode());
            teamDetailResponse.setDesc(team.getDesc());
            teamDetailResponse.setPrincipal(team.getPrincipal());
            teamDetailResponse.setSdm(team.getSdm());

            //2.2获取各个指标得分
            teamDetailResponse.setDimensionScoreList(getDimensionScore(entry.getValue(), dimensionMap
                    , firstLevelDimensionMap));

            //2.3获取总得分或者排名
            isReComparison = getTotalScoreAndRank(teamDetailResponse, period, entry.getKey(), entry.getValue(), dimensionMap
                    , firstLevelDimensionMap);

            teamDetailResponses.add(teamDetailResponse);
        }

        //3.校验是否需要重新计算一次排名(为获取到快照数据)
        if (Boolean.TRUE.equals(isReComparison)) {
            teamDetailResponses = teamDetailResponses.stream().sorted(Comparator
                    .comparingDouble(TeamDetailResponse::getScore).reversed()).collect(Collectors.toList());
            for (int i = 1; i <= teamDetailResponses.size(); i++) {
                teamDetailResponses.get(i - 1).setIndex(i);
            }
        }

        //4.构建实体并保存
        for (TeamDetailResponse response : teamDetailResponses) {
            //4.1.构建快照数据，存入快照表
            TDSnapshotTable snapshotTable = new TDSnapshotTable();
            snapshotTable.setType(SnapshotAccountTypeEnum.TEAM.getCode());
            snapshotTable.setCodeFlag(response.getTeamCode());
            snapshotTable.setNameFlag(response.getTeamName());
            if (StringUtils.isNotEmpty(period)) {
                snapshotTable.setPeriodFlag(period);
            } else {
                snapshotTable.setPeriodFlag(TimeUtil.getCurrentYear());
            }
            snapshotTable.setContent(JSON.toJSONString(response));
            snapshotTable.setSnapshotTime(LocalDateTime.now());
            snapshotTable.setYear(TimeUtil.getCurrentYear());
            snapshotTable.setModelFlag(TeamModelDataEnum.TEAM_DETAIL.getCode());

            tableService.saveAndUpdate(snapshotTable);
        }
    }

    /**
     * 获取各个维度的得分
     *
     * @param integralList list
     * @return dimension
     */
    private List<TeamDimensionScoreListVo> getDimensionScore(List<TDDimensionIntegral> integralList
            , Map<String, TDDimension> dimensionMap, Map<String, TDDimension> firstLevelDimensionMap) {
        //1.根据指标积分，按照父指标进行分类
        Map<String, List<TDDimensionIntegral>> dimensionIntegralListMap = new HashMap<>();
        for (TDDimensionIntegral dimensionIntegral : integralList) {
            //1.1获取指标积分对应的指标
            if (!dimensionMap.containsKey(dimensionIntegral.getDimensionCode())) {
                //当前指标积分未匹配对应指标数据
                continue;
            }
            TDDimension childDimension = dimensionMap.get(dimensionIntegral.getDimensionCode());

            //1.2根据子指标获取父指标
            if (!firstLevelDimensionMap.containsKey(childDimension.getParentCode())) {
                //根据获取的子指标未匹配上父指标数据
                continue;
            }
            TDDimension parentDimension = firstLevelDimensionMap.get(childDimension.getParentCode());

            //1.3对指标积分按照父指标进行归集
            List<TDDimensionIntegral> tdDimensionIntegralList = new ArrayList<>();
            if (dimensionIntegralListMap.containsKey(parentDimension.getCode())) {
                tdDimensionIntegralList = dimensionIntegralListMap.get(parentDimension.getCode());
            }
            tdDimensionIntegralList.add(dimensionIntegral);
            dimensionIntegralListMap.put(parentDimension.getCode(), tdDimensionIntegralList);
        }

        //2.开始计算各个父节点的积分
        Map<String, Double> scoreMap = getParentScore(dimensionIntegralListMap, firstLevelDimensionMap, dimensionMap);

        //3.对所有的父指标进行赋值
        List<TeamDimensionScoreListVo> scoreListVos = new ArrayList<>();
        for (Map.Entry<String, TDDimension> entry : firstLevelDimensionMap.entrySet()) {
            TeamDimensionScoreListVo scoreListVo = new TeamDimensionScoreListVo();
            TDDimension tdDimension = entry.getValue();
            scoreListVo.setDimensionCode(entry.getKey());
            scoreListVo.setScore(String.valueOf(0));
            if (scoreMap.containsKey(entry.getKey())) {
                scoreListVo.setScore(String.valueOf(scoreMap.get(entry.getKey())));
            }
            scoreListVo.setDimensionName(tdDimension.getName());

            scoreListVos.add(scoreListVo);
        }

        return scoreListVos;
    }

    /**
     * 计算父节点的积分
     * @param dimensionIntegralListMap   map
     * @param firstLevelDimensionMap     map
     * @param dimensionMap               map
     * @return  map
     */
    private Map<String, Double> getParentScore(Map<String, List<TDDimensionIntegral>> dimensionIntegralListMap
            , Map<String, TDDimension> firstLevelDimensionMap, Map<String, TDDimension> dimensionMap) {
        Map<String, Double> scoreMap = new HashMap<>();
        for (Map.Entry<String, List<TDDimensionIntegral>> entry : dimensionIntegralListMap.entrySet()) {
            double score = 0.0;
            TDDimension parentDimension = firstLevelDimensionMap.get(entry.getKey());
            //1遍历该父指标下的所有子指标的指标积分
            for (TDDimensionIntegral dimensionIntegral : entry.getValue()) {
                //1.1先获取对应指标
                TDDimension childDimension = dimensionMap.get(dimensionIntegral.getDimensionCode());

                //1.2特殊处理满分100的，且含有加分的项的数据特殊规则(该规则必须包含上限分数)，其他情况需要根据实际指标考虑，这里针对代码管理指标。
                double fraction = dimensionIntegral.getFraction();
                if(parentDimension.getInitialScore() > 0) {
                    fraction = fraction - childDimension.getUpperLimit();
                }

                //1.3.根据积分权重计算得分
                score = score + fraction * childDimension.getWeights();
            }

            //2获取父节点对应的初始分数，按照加分项还是减分项进行统计，父指标初始分数为100则表示为减分项，为0则为加分项
            score = score + parentDimension.getInitialScore();
            if (parentDimension.getInitialScore() > 0) {
                //2.1减分项的情况下，小于0分按照0分展示
                if (score > 0) {
                    scoreMap.put(entry.getKey(), score);
                }
            } else {
                //2.2加分项情况下，大于100分则按照100分展示
                if(score > 100) {
                    scoreMap.put(entry.getKey(), 100.0);
                }else {
                    scoreMap.put(entry.getKey(), score);
                }
            }


        }

        return scoreMap;
    }

    /**
     * 获取总得分或者排名
     *
     * @return 是否需要重新排名(false 不需要 ， true 需要)
     */
    private boolean getTotalScoreAndRank(TeamDetailResponse teamDetailResponse, String period, String teamCode
            , List<TDDimensionIntegral> integralList, Map<String, TDDimension> dimensionMap, Map<String, TDDimension> firstLevelDimensionMap) {
        //1.首先查询当前该周期的团队数据是否已经快照了
        LambdaQueryWrapper<TDSnapshotTable> snapshotTableLambdaQueryWrapper = new LambdaQueryWrapper<>();
        snapshotTableLambdaQueryWrapper.eq(TDSnapshotTable::getType, SnapshotAccountTypeEnum.TEAM.getCode())
                .eq(TDSnapshotTable::getModelFlag, TeamModelDataEnum.TEAM_DETAIL.getCode())
                .eq(TDSnapshotTable::getCodeFlag, COMMON_CODE_FLAG)
                .eq(TDSnapshotTable::getYear, TimeUtil.getCurrentYear());
        if (StringUtils.isNotEmpty(period)) {
            snapshotTableLambdaQueryWrapper.eq(TDSnapshotTable::getPeriodFlag, period);
        } else {
            snapshotTableLambdaQueryWrapper.eq(TDSnapshotTable::getPeriodFlag, TimeUtil.getCurrentYear());
        }

        TDSnapshotTable tdSnapshotTable = tableService.getOne(snapshotTableLambdaQueryWrapper);

        //2.如果为空，则标识未快照，需要重新计算
        if (ObjectUtils.isEmpty(tdSnapshotTable)) {
            teamDetailResponse.setScore(commonService.calTeamOverallStandings(integralList, dimensionMap, firstLevelDimensionMap));
            return true;
        }

        //3.不为空，则标识已经快照了，直接取值
        JSONArray rankArray = JSON.parseArray(tdSnapshotTable.getContent());
        for (int i = 0; i < rankArray.size(); i++) {
            TeamOverallStandingsResponse response = JSON.parseObject(rankArray.getString(i), TeamOverallStandingsResponse.class);
            if (teamCode.equals(response.getTeamCode())) {
                teamDetailResponse.setScore(response.getScore());
                teamDetailResponse.setIndex(response.getIndex());
            }
        }
        return false;
    }
}
