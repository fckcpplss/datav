package com.longfor.datav.common.enums;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author zhaoyalong
 * @description: 员工状态枚举
 */
@AllArgsConstructor
@Getter
public enum EmployeeStatusEnum {

    /**
     * 在职
     */
    ON(1,"在职"),
    /**
     * 离职
     */
    OFF(2,"离职"),
    ;


    private Integer code;

    private String msg;

    public static EmployeeStatusEnum fromCode(Integer code){
        return Optional.ofNullable(code)
                .map(c -> {
                    return Arrays.stream(EmployeeStatusEnum.values())
                            .filter(x -> x.getCode().equals(code))
                            .findFirst()
                            .orElse(null);
                })
                .orElse(null);
    }
}
