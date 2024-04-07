package com.longfor.datav.admin.service.impl;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.longfor.datav.admin.service.TeamService;
import com.longfor.datav.common.constants.CommonConstant;
import com.longfor.datav.common.enums.SnapshotAccountTypeEnum;
import com.longfor.datav.common.enums.TeamModelDataEnum;
import com.longfor.datav.common.utils.TimeUtil;
import com.longfor.datav.common.vo.req.BaseTeamRequest;
import com.longfor.datav.common.vo.req.TeamDetailRequest;
import com.longfor.datav.common.vo.resp.*;
import com.longfor.datav.dao.entity.TDAccountTeamRelation;
import com.longfor.datav.dao.entity.TDSnapshotTable;
import com.longfor.datav.dao.entity.TDTimeSprintRelation;
import com.longfor.datav.dao.service.ITDAccountTeamRelationService;
import com.longfor.datav.dao.service.ITDSnapshotTableService;

import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

import com.longfor.datav.dao.service.ITDTimeSprintRelationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;

/**
 * 团队相关服务实现
 *
 * @author zyh
 * @date 2024-01-29
 * @since jdk 1.8
 */

@Slf4j
@Service
public class TeamServiceImpl implements TeamService {

    @Resource
    private ITDSnapshotTableService itdSnapshotTableService;

    @Resource
    private ITDTimeSprintRelationService sprintRelationService;

    @Resource
    private ITDAccountTeamRelationService accountTeamRelationService;

    private static final String ALL = "全部";

    /**
     * 周期枚举(本年度)
     *
     * @return list
     */
    @Override
    public List<PeriodEnumResponse> periodEnum() {

        //1.查询周期数据
        LambdaQueryWrapper<TDSnapshotTable> tdSnapshotTableLambdaQueryWrapper = new LambdaQueryWrapper<>();
        tdSnapshotTableLambdaQueryWrapper.select(TDSnapshotTable::getPeriodFlag)
                .eq(TDSnapshotTable::getType, SnapshotAccountTypeEnum.TEAM.getCode())
                .eq(TDSnapshotTable::getYear, TimeUtil.getCurrentYear())
                .orderByAsc(TDSnapshotTable::getPeriodFlag);

        List<Object> periodList = itdSnapshotTableService.listObjs(tdSnapshotTableLambdaQueryWrapper);
        if (CollectionUtils.isEmpty(periodList)) {
            return Collections.emptyList();
        }

        List<PeriodEnumResponse> periodEnumResponses = new ArrayList<>();
        Map<String, String> periodMap = new HashMap<>();

        //2.全部周期处理,全部周期不参与赋值，且能排在第一的位置
        periodMap.put(TimeUtil.getCurrentYear(), TimeUtil.getCurrentYear());
        PeriodEnumResponse allResponse = new PeriodEnumResponse();
        allResponse.setPeriod(TimeUtil.getCurrentYear());
        allResponse.setName(ALL);
        periodEnumResponses.add(allResponse);

        //3.处理其他周期数据
        for (Object o : periodList) {
            String period = String.valueOf(o);
            if (periodMap.containsKey(period)) {
                continue;
            }
            PeriodEnumResponse response = new PeriodEnumResponse();
            response.setName(period);
            response.setPeriod(period);

            periodMap.put(period, period);
            periodEnumResponses.add(response);
        }

        return periodEnumResponses;
    }

    /**
     * 团队评分排名
     *
     * @param request req
     * @return list
     */
    @Override
    public List<TeamOverallStandingsResponse> overallStandings(BaseTeamRequest request) {
        //获取快照表数据
        TDSnapshotTable tdSnapshotTable = getTeamSnapshotTable(request.getPeriod(), TeamModelDataEnum.TEAM_OVERALL_STANDING.getCode(), null);
        if (ObjectUtils.isEmpty(tdSnapshotTable)) {
            return Collections.emptyList();
        }

        return JSONUtil.toList(tdSnapshotTable.getContent(), TeamOverallStandingsResponse.class);
    }

    /**
     * 团队详情介绍
     *
     * @param request req
     * @return obj
     */
    @Override
    public TeamDetailResponse teamDetail(TeamDetailRequest request) {
        //获取快照表数据
        TDSnapshotTable tdSnapshotTable = getTeamSnapshotTable(request.getPeriod(), TeamModelDataEnum.TEAM_DETAIL.getCode(), request.getTeamCode());
        if (ObjectUtils.isEmpty(tdSnapshotTable)) {
            return null;
        }

        return JSONUtil.toBean(tdSnapshotTable.getContent(), TeamDetailResponse.class);
    }

    /**
     * 团队各个指标评分排名
     *
     * @param request req
     * @return obj
     */
    @Override
    public List<TeamDimensionScoreResponse> dimensionScore(BaseTeamRequest request) {
        //获取快照表数据
        TDSnapshotTable tdSnapshotTable = getTeamSnapshotTable(request.getPeriod(), TeamModelDataEnum.TEAM_DIMENSION_SCORE.getCode(), null);
        if (ObjectUtils.isEmpty(tdSnapshotTable)) {
            return Collections.emptyList();
        }

        return JSONUtil.toList(tdSnapshotTable.getContent(), TeamDimensionScoreResponse.class);
    }

    /**
     * 团队历史分数(当年)
     *
     * @param request req
     * @return list
     */
    @Override
    public TeamHistoryResponse teamHistory(TeamDetailRequest request) {
        //获取快照表数据
        TDSnapshotTable tdSnapshotTable = getTeamSnapshotTable(null, TeamModelDataEnum.TEAM_HISTORY.getCode(), request.getTeamCode());
        if (ObjectUtils.isEmpty(tdSnapshotTable)) {
            return null;
        }

        return JSONUtil.toBean(tdSnapshotTable.getContent(), TeamHistoryResponse.class);
    }

    /**
     * 团队账户信息
     *
     * @param request req
     * @return list
     */
    @Override
    public List<TeamAccountResponse> teamAccount(TeamDetailRequest request) {
        //1.校验输入周期
        Date startDate = new Date(TimeUtil.getCurrentFirstTimeByYear().toInstant(ZoneOffset.UTC).toEpochMilli());
        Date endDate = new Date();
        if(!TimeUtil.getCurrentYear().equals(request.getPeriod())) {
            //1.1.获取当前周期的时间数据
            LambdaQueryWrapper<TDTimeSprintRelation> relationLambdaQueryWrapper = new LambdaQueryWrapper<>();
            relationLambdaQueryWrapper.eq(TDTimeSprintRelation::getPeriod, request.getPeriod())
                    .eq(TDTimeSprintRelation::getYear, TimeUtil.getCurrentYear());
            TDTimeSprintRelation relation = sprintRelationService.getOne(relationLambdaQueryWrapper);
            if(ObjectUtils.isEmpty(relation)) {
                return Collections.emptyList();
            }
            startDate = relation.getStartTime();
            endDate = relation.getEndTime();
        }


        //2.获取当前团队在改周期时间的用户数据
        LambdaQueryWrapper<TDAccountTeamRelation> accountTeamRelationLambdaQueryWrapper = new LambdaQueryWrapper<>();
        accountTeamRelationLambdaQueryWrapper.eq(TDAccountTeamRelation::getTeamCode, request.getTeamCode()).or()
                .ge(TDAccountTeamRelation::getStartTime, startDate)
                .le(TDAccountTeamRelation::getEndTime, endDate);
        List<TDAccountTeamRelation> tdAccountTeamRelationList = accountTeamRelationService.list(accountTeamRelationLambdaQueryWrapper);
        if(CollectionUtils.isEmpty(tdAccountTeamRelationList)) {
            return Collections.emptyList();
        }

        //3.获取用户在当前周期中的排名
        List<String> accountList = tdAccountTeamRelationList.stream().map(TDAccountTeamRelation::getAccount).collect(Collectors.toList());
        LambdaQueryWrapper<TDSnapshotTable> snapshotTableLambdaQueryWrapper = new LambdaQueryWrapper<>();
        snapshotTableLambdaQueryWrapper.eq(TDSnapshotTable::getType, 0)
                .eq(TDSnapshotTable::getYear, TimeUtil.getCurrentYear())
                .eq(TDSnapshotTable::getPeriodFlag, request.getPeriod())
                .in(TDSnapshotTable::getCodeFlag, accountList);
        List<TDSnapshotTable> tableList = itdSnapshotTableService.list(snapshotTableLambdaQueryWrapper);
        if(CollectionUtils.isEmpty(tableList)) {
            return Collections.emptyList();
        }

        //4.解析返回的字段
        List<TeamAccountResponse> teamAccountResponseList = new ArrayList<>();
        for(TDSnapshotTable tdSnapshotTable : tableList) {
            JSONObject jsonObject = JSON.parseObject(tdSnapshotTable.getContent());
            String info = jsonObject.getString(CommonConstant.SNAPSHOT_DATA_OF_PERSON_INFO);
            if(StringUtils.isEmpty(info)) {
                continue;
            }
            MemberListResponse memberListResponse = JSON.parseObject(info, MemberListResponse.class);
            TeamAccountResponse teamAccountResponse = new TeamAccountResponse();
            teamAccountResponse.setAccount(memberListResponse.getOaAccount());
            teamAccountResponse.setName(memberListResponse.getName());
            teamAccountResponse.setPostName(memberListResponse.getPostName());
            teamAccountResponse.setScore(Double.parseDouble(memberListResponse.getScore()));
            teamAccountResponseList.add(teamAccountResponse);
        }

        //5.按照分数排序
        List<TeamAccountResponse> teamAccountResponseListSort = teamAccountResponseList.stream()
                .sorted(Comparator.comparingDouble(TeamAccountResponse::getScore).reversed()).collect(Collectors.toList());
        for (int i = 0; i < teamAccountResponseListSort.size(); i++) {
            teamAccountResponseListSort.get(i).setIndex(i + 1);
        }

        return teamAccountResponseListSort;
    }

    /**
     * 团队每项指标得分
     *
     * @param request req
     * @return obj
     */
    @Override
    public List<TeamDimensionDetailResponse> teamDimensionScore(TeamDetailRequest request) {
        //获取快照表数据
        TDSnapshotTable tdSnapshotTable = getTeamSnapshotTable(request.getPeriod(), TeamModelDataEnum.TEAM_DIMENSION_DETAIL.getCode(), request.getTeamCode());
        if (ObjectUtils.isEmpty(tdSnapshotTable)) {
            return Collections.emptyList();
        }

        return JSONUtil.toList(tdSnapshotTable.getContent(), TeamDimensionDetailResponse.class);
    }

    /**
     * 根据周期和model获取快照数据
     *
     * @param period 周期
     * @param model  模块
     * @return 快照数据
     */
    private TDSnapshotTable getTeamSnapshotTable(String period, String model, String teamCode) {
        LambdaQueryWrapper<TDSnapshotTable> tdSnapshotTableLambdaQueryWrapper = new LambdaQueryWrapper<>();
        tdSnapshotTableLambdaQueryWrapper.eq(TDSnapshotTable::getType, SnapshotAccountTypeEnum.TEAM.getCode())
                .eq(TDSnapshotTable::getModelFlag, model)
                .eq(TDSnapshotTable::getYear, TimeUtil.getCurrentYear());

        if (StringUtils.isNotEmpty(period)) {
            tdSnapshotTableLambdaQueryWrapper.eq(TDSnapshotTable::getPeriodFlag, period);
        } else {
            tdSnapshotTableLambdaQueryWrapper.eq(TDSnapshotTable::getPeriodFlag, TimeUtil.getCurrentYear());
        }

        if (StringUtils.isNotEmpty(teamCode)) {
            tdSnapshotTableLambdaQueryWrapper.eq(TDSnapshotTable::getCodeFlag, teamCode);
        }

        return itdSnapshotTableService.getOne(tdSnapshotTableLambdaQueryWrapper);
    }
}
