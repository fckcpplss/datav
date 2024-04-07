package com.longfor.datav.common.dto;

import com.longfor.datav.common.vo.resp.MemberDimensionListResponse;
import com.longfor.datav.common.vo.resp.MemberListResponse;
import com.longfor.datav.common.vo.resp.MemberScoreHistoryResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 成员所有快照数据DTO
 * @author zhaoyl
 * @date 2024/1/30 17:14
 * @since 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberSnapshotAllDataDTO {
    /**
     * 信息快照
     */
    private MemberListResponse info;
    /**
     * 指标得分
     */
    private List<MemberScoreHistoryResponse> itemScore;

    /**
     * 得分历史
     */
    private String historyScore;

    /**
     * 得分明细
     */
    private List<SnapshotScoreDetailDTO> historyScoreDetail;

    /**
     * 指标详情
     */

    private List<MemberSnapshotDimensionInfoDTO> dimensionList;
}
