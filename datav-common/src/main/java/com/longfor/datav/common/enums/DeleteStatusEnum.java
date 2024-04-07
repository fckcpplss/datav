package com.longfor.datav.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author zhaoyalong
 * @description: 删除状态枚举
 */
@AllArgsConstructor
@Getter
public enum DeleteStatusEnum {

    NO(0, "未删除"),
    YES(1, "已删除"),
    ;
    ;


    private Integer code;

    private String msg;

    public static DeleteStatusEnum fromCode(Integer code){
        return Optional.ofNullable(code)
                .map(c -> {
                    return Arrays.stream(DeleteStatusEnum.values())
                            .filter(x -> x.getCode().equals(code))
                            .findFirst()
                            .orElse(null);
                })
                .orElse(null);
    }
}
