package com.longfor.datav.admin.service;

import com.longfor.datav.common.vo.req.BaseTeamRequest;
import com.longfor.datav.common.vo.req.TeamDetailRequest;
import com.longfor.datav.common.vo.resp.*;

import java.util.List;

/**
 * 团队相关服务接口
 * @author zyh
 * @since jdk 1.8
 * @date 2024-01-29
 */

public interface TeamService {

    /**
     * 周期枚举列表(本年度)
     * @return   list
     */
    List<PeriodEnumResponse> periodEnum();

    /**
     * 团队数据排名接口
     * @param request  req
     * @return         resp
     */
    List<TeamOverallStandingsResponse> overallStandings(BaseTeamRequest request);

    /**
     * 团队详情
     * @param request  req
     * @return         resp
     */
    TeamDetailResponse teamDetail(TeamDetailRequest request);

    /**
     * 各项指标团队得分排名
     * @param request  req
     * @return         resp
     */
    List<TeamDimensionScoreResponse> dimensionScore(BaseTeamRequest request);

    /**
     * 历史得分排名
     * @param request  req
     * @return         resp
     */
    TeamHistoryResponse teamHistory(TeamDetailRequest request);

    /**
     * 团队成员
     * @param request  req
     * @return         resp
     */
    List<TeamAccountResponse> teamAccount(TeamDetailRequest request);

    /**
     * 团队各个指标的分数
     * @param request  req
     * @return         resp
     */
    List<TeamDimensionDetailResponse> teamDimensionScore(TeamDetailRequest request);
}
