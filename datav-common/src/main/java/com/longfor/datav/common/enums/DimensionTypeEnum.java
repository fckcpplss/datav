package com.longfor.datav.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

/**
 * 团队指标类型枚举
 * @author zyh
 * @since jdk 1.8
 * @date 2024-02-05
 */

@AllArgsConstructor
@Getter
public enum DimensionTypeEnum {

    TEAM(1, "团队"),
    PERSON(2, "个人"),
    OTHER(3, "其他");

    private int code;

    private String name;

    public static DimensionTypeEnum fromCode(Integer code){
        return Optional.ofNullable(code)
                .map(c -> {
                    return Arrays.stream(DimensionTypeEnum.values())
                            .filter(x -> x.getCode() == code)
                            .findFirst()
                            .orElse(null);
                })
                .orElse(null);
    }
}
