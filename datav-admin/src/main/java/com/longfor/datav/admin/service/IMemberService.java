package com.longfor.datav.admin.service;

import com.longfor.datav.common.vo.PageResponse;
import com.longfor.datav.common.vo.req.MemberInfoRequest;
import com.longfor.datav.common.vo.req.MemberListRequest;
import com.longfor.datav.common.vo.resp.MemberDimensionListResponse;
import com.longfor.datav.common.vo.resp.MemberListResponse;
import com.longfor.datav.common.vo.resp.MemberScoreHistoryResponse;
import com.longfor.datav.common.vo.resp.MemberInfoResponse;

import java.util.List;

/**
 * 成员管理接口类
 * @author zhaoyl
 * @date 2024/1/26 13:46
 * @since 1.0
 */
public interface IMemberService {
    /**
     * 成员列表
     * @param request
     * @return
     */
    PageResponse<List<MemberListResponse>> memberList(MemberListRequest request);

    /**
     * 成员详情
     * @param request
     * @return
     */
    MemberInfoResponse memberInfo(MemberInfoRequest request);

    /**
     * 成员得分历史
     * @param request
     * @return
     */
    List<MemberScoreHistoryResponse> scoreHistory(MemberInfoRequest request);

    /**
     * 成员指标明细
     * @param request
     * @return
     */
    List<MemberDimensionListResponse> dataInfo(MemberInfoRequest request);

    /**
     * 得分详情
     * @param request
     * @return
     */
    List<MemberScoreHistoryResponse> scoreInfo(MemberInfoRequest request);

}
