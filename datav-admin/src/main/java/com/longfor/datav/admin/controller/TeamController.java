package com.longfor.datav.admin.controller;

import com.longfor.datav.admin.service.TeamService;
import com.longfor.datav.common.vo.Response;
import com.longfor.datav.common.vo.req.BaseTeamRequest;
import com.longfor.datav.common.vo.req.TeamDetailRequest;
import com.longfor.datav.common.vo.resp.*;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * 团队相关接口
 * @author zyh
 * @since jdk 1.8
 * @date 2024-01-29
 */

@CrossOrigin
@RestController
@RequestMapping("/admin/datav/v1/team")
public class TeamController {

    @Resource
    private TeamService service;

    /**
     * 获取周期枚举(本年度)
     * @return   list
     */
    @GetMapping("/periodEnum")
    public Response<List<PeriodEnumResponse>> periodEnum() {
        return Response.ok(service.periodEnum());
    }

    /**
     * 团队数据排名接口
     * @param request  req
     * @return         resp
     */
    @PostMapping("/overallStandings")
    public Response<List<TeamOverallStandingsResponse>> overallStandings(@Valid @RequestBody BaseTeamRequest request){
        return Response.ok(service.overallStandings(request));
    }

    /**
     * 团队详情
     * @param request  req
     * @return         resp
     */
    @PostMapping("/detail")
    public Response<TeamDetailResponse> teamDetail(@Valid @RequestBody TeamDetailRequest request){
        return Response.ok(service.teamDetail(request));
    }

    /**
     * 各项指标团队得分排名
     * @param request  req
     * @return         resp
     */
    @PostMapping("/dimensionScore")
    public Response<List<TeamDimensionScoreResponse>> dimensionScore(@Valid @RequestBody BaseTeamRequest request){
        return Response.ok(service.dimensionScore(request));
    }

    /**
     * 历史得分排名
     * @param request  req
     * @return         resp
     */
    @PostMapping("/history")
    public Response<TeamHistoryResponse> teamHistory(@Valid @RequestBody TeamDetailRequest request){
        return Response.ok(service.teamHistory(request));
    }

    /**
     * 团队成员
     * @param request  req
     * @return         resp
     */
    @PostMapping("/account")
    public Response<List<TeamAccountResponse>> teamAccount(@Valid @RequestBody TeamDetailRequest request){
        return Response.ok(service.teamAccount(request));
    }

    /**
     * 团队各个指标的分数
     * @param request  req
     * @return         resp
     */
    @PostMapping("/dimensionDetail")
    public Response<List<TeamDimensionDetailResponse>> teamDimensionScore(@Valid @RequestBody TeamDetailRequest request){
        return Response.ok(service.teamDimensionScore(request));
    }

}
