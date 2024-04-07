package com.longfor.datav.common.enums;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author zhaoyalong
 * @description: 岗位类型枚举
 */
@AllArgsConstructor
@Getter
public enum PostTypeEnum {

    BACKEND_DEVELOPER(1, "后端开发"),
    FRONTEND_DEVELOPER(2, "前端开发"),
    OPERATIONS_STAFF(3, "运营员工"),
    TESTER(4, "测试员工"),
    PRODUCT_STAFF(5, "产品员工");
    ;


    private Integer code;

    private String msg;

    public static PostTypeEnum fromCode(Integer code){
        return Optional.ofNullable(code)
                .map(c -> {
                    return Arrays.stream(PostTypeEnum.values())
                            .filter(x -> x.getCode().equals(code))
                            .findFirst()
                            .orElse(null);
                })
                .orElse(null);
    }
}
