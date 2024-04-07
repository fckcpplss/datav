package com.longfor.datav.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 快照纬度信息
 * @author zhaoyl
 * @date 2024/2/1 17:47
 * @since 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberSnapshotDimensionInfoDTO {
    /**
     * 纬度编码
     */
    private String dimensionCode;
    /**
     *纬度名称
     */
    private String dimensionName;

    /**
     * 父纬度编码
     */
    private String parentDimensionCode;

    /**
     * 父纬度编码
     */
    private String parentDimensionName;
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
