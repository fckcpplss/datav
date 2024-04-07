package com.longfor.datav.admin.controller;

import com.longfor.datav.admin.service.IMemberService;
import com.longfor.datav.common.enums.EmployeeRoleEnum;
import com.longfor.datav.common.vo.PageResponse;
import com.longfor.datav.common.vo.Response;
import com.longfor.datav.common.vo.req.MemberInfoRequest;
import com.longfor.datav.common.vo.req.MemberListRequest;
import com.longfor.datav.common.vo.resp.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 成员管理
 * @author zhaoyl
 * @date 2024/1/26 10:30
 * @since 1.0
 */
@CrossOrigin
@RestController
@RequestMapping("/admin/datav")
public class MemberController {

    @Autowired
    private IMemberService memberService;
    /**
     * 成员列表
     * @param request
     * @return
     */
    @PostMapping("/v1/member/list")
    public PageResponse<List<MemberListResponse>> memberList(@Valid @RequestBody MemberListRequest request) {
        return memberService.memberList(request);
    }
    /**
     * 成员类型
     * @param request
     * @return
     */
    @PostMapping("/v1/member/type/list")
    public Response<List<MemberTypeListResponse>> memberTypeList() {
        return Response.ok(Arrays.stream(EmployeeRoleEnum.values()).map(r -> new MemberTypeListResponse(r.getMsg(),String.valueOf(r.getCode()))).collect(Collectors.toList()));
    }

    /**
     * 成员详情
     * @param request
     * @return
     */
    @PostMapping("/v1/member/info")
    public Response<MemberInfoResponse> memberList(@Valid @RequestBody MemberInfoRequest request) {
        return Response.ok(memberService.memberInfo(request));
    }

    /**
     * 成员得分历史
     * @param request
     * @return
     */
    @PostMapping("/v1/member/score/history")
    public Response<List<MemberScoreHistoryResponse>> scoreHistory(@Valid @RequestBody MemberInfoRequest request) {
        return Response.ok(memberService.scoreHistory(request));
    }

    /**
     * 成员得分详情
     * @param request
     * @return
     */
    @PostMapping("/v1/member/score/info")
    public Response<List<MemberScoreHistoryResponse>> scoreInfo(@Valid @RequestBody MemberInfoRequest request) {
        return Response.ok(memberService.scoreInfo(request));
    }


    /**
     * 成员数据详情
     * @param request
     * @return
     */
    @PostMapping("/v1/member/data/info")
    public Response<List<MemberDimensionListResponse>> dataInfo(@Valid @RequestBody MemberInfoRequest request) {
        return Response.ok(memberService.dataInfo(request));
    }
}
