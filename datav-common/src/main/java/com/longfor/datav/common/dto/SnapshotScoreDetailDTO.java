package com.longfor.datav.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 得分明细快照数据DTO
 * @author zhaoyl
 * @date 2024/1/30 17:14
 * @since 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SnapshotScoreDetailDTO {
    /**
     * 得分日期
     */
    private String date;
    /**
     * 纬度编码
     */
    private String dimensionCode;

    /**
     * 纬度名称
     */
    private String dimensionName;

    /**
     * 纬度描述
     */
    private String dimensionDesc;

    /**
     * 父纬度编码
     */
    private String parentDimensionCode;

    /**
     * 父纬度名称
     */
    private String parentDimensionName;

    /**
     * 初始分数
     */
    private double initialScore;

    /**
     * 快照内容
     */
    private double score;

    /**
     * 分值
     */
    private String value;
}
