package com.longfor.datav.common.vo;

import lombok.Data;

/**
 * 团队子节点描述
 * @author zyh
 * @since jdk 1.8
 * @date 2024-01-29
 */

@Data
public class TeamChildDimensionVo {

    /**
     * 指标子节点编码
     */
    private String childDimensionCode;

    /**
     * 指标子节点名称
     */
    private String childDimensionName;

    /**
     * 得分
     */
    private double score;

    /**
     * 指标数值
     */
    private String value;

    /**
     * 描述
     */
    private String desc;
}
