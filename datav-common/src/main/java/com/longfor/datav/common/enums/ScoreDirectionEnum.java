package com.longfor.datav.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


/**
 * 评分排名方向
 * @author zyh
 * @since jdk 1.8
 * @date 2024-02-01
 */

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum ScoreDirectionEnum {
    UP(1, "上升"),
    DOWN(0, "下降"),
    CONSTANT(2, "不变");

    private int code;

    private String msg;
}
