package com.longfor.datav.common.enums;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

/**
 * 周期类型枚举类
 * @author zyh
 * @since jdk 1.8
 * @date 2024-01-30
 */

@Getter
@AllArgsConstructor
public enum PeriodTypeEnum {
    SPRINT(1, "冲刺"),
    MONTH(2, "月份"),
    YEAR(3, "年份"),
    ;

    private Integer code;

    private String message;

    public static PeriodTypeEnum fromCode(Integer code){
        return Optional.ofNullable(code)
                .map(c -> {
                    return Arrays.stream(PeriodTypeEnum.values())
                            .filter(x -> x.getCode().equals(code))
                            .findFirst()
                            .orElse(null);
                })
                .orElse(null);
    }
    public static PeriodTypeEnum fromMsg(String msg){
        return Optional.ofNullable(StrUtil.blankToDefault(msg,null))
                .map(c -> {
                    return Arrays.stream(PeriodTypeEnum.values())
                            .filter(x -> x.getMessage().equals(msg))
                            .findFirst()
                            .orElse(null);
                })
                .orElse(null);
    }


}
