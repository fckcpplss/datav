package com.longfor.datav.common.vo;

import lombok.Data;

/**
 * 团队历史分数响应实体
 * @author zyh
 * @since jdk 1.8
 * @date 2024-01-29
 */

@Data
public class TeamHistoryVo {

    private String time;

    private int index;

    private double score;
}
