package com.longfor.datav.common.enums;

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
public enum EmployeeRoleEnum {

    /**
     * SDM
     */
    SDM(1,"SDM"),
    /**
     * SDE
     */
    SDE(2,"SDE"),
    /**
     * QA
     */
    QA(3,"QA"),
    /**
     * QAM
     */
    QAM(4,"QAM"),
    /**
     * TPM
     */
    TPM(5,"TPM"),

    /**
     * TPO
     */
    TPO(6,"TPO"),
    ;


    private Integer code;

    private String msg;

    public static EmployeeRoleEnum fromCode(Integer code){
        return Optional.ofNullable(code)
                .map(c -> {
                    return Arrays.stream(EmployeeRoleEnum.values())
                            .filter(x -> x.getCode().equals(code))
                            .findFirst()
                            .orElse(null);
                })
                .orElse(null);
    }
}
