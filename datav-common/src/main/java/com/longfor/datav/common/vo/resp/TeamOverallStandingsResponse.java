package com.longfor.datav.common.vo.resp;

import lombok.Data;

/**
 * 团队数据排名接口响应实体
 * @author zyh
 * @since jdk 1.8
 * @date 2024-01-29
 */

@Data
public class TeamOverallStandingsResponse {

    /**
     * 排名序号
     */
    private int index;

    /**
     * 团队名称
     */
    private String teamName;

    /**
     * 分数
     */
    private double score;

    /**
     * 团队编码
     */
    private String teamCode;

    /**
     * 历史环比
     */
    private int historicalComparison;

    /**
     * 排名方向，0下降，1上升
     */
    private int direction;
}
