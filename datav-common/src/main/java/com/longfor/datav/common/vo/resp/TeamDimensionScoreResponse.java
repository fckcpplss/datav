package com.longfor.datav.common.vo.resp;

import lombok.Data;

import java.util.List;

/**
 * 各个指标的团队排名响应实体
 * @author zyh
 * @since jdk 1.8
 * @date 2024-01-29
 */

@Data
public class TeamDimensionScoreResponse {

    /**
     * 指标编码
     */
    private String dimensionCode;

    /**
     * 指标名称
     */
    private String dimensionName;

    /**
     * 排名列表
     */
    private List<TeamOverallStandingsResponse> rankList;
}
