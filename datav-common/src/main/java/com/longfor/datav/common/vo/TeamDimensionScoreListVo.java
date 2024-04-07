package com.longfor.datav.common.vo;

import lombok.Data;

/**
 * 团队指标评分
 * @author zyh
 * @since jdk 1.8
 * @date 2024-01-29
 */

@Data
public class TeamDimensionScoreListVo {

    /**
     * 指标编码
     */
    private String dimensionCode;

    /**
     * 指标名称
     */
    private String dimensionName;

    /**
     * 分数
     */
    private String score;
}
