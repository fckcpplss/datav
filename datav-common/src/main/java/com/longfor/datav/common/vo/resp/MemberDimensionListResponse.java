package com.longfor.datav.common.vo.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 成员纬度得分明细返回结果
 * @author zhaoyl
 * @date 2024/1/26 10:34
 * @since 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberDimensionListResponse {

    /**
     * 纬度编码
     */
    private String dimensionCode;
    /**
     * 纬度名称
     */
    private String dimensionName;

    /**
     * 得分明细列表
     */
    private List<MemberScoreHistoryResponse> childList;
    /**
     * 纬度说明
     */
    private String dimensionDesc;

    /**
     * 是否父节点,1.是，0.否
     */
    private Integer isParent;
    /**
     * 得分
     */
    private String score;

}
