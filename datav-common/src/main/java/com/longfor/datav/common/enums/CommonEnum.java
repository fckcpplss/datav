package com.longfor.datav.common.enums;

import com.longfor.datav.common.vo.IResponseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 公共枚举类
 * @author zhaoyl
 * @date 2024/1/29 14:26
 * @since 1.0
 */
@Getter
@AllArgsConstructor
public enum  CommonEnum implements IResponseEnum {
    SUCCESS(1,"操作成功"),
    FAIL(0,"操作失败"),
    BIZ_ERROR(10001, "业务异常"),
    UNAUTHORIZED(10002, "未登录或登录已过期"),
    FORBIDDEN(10003, "没有权限"),
    SYS_ERROR(10004, "系统异常");
    ;
    private int code;
    private String msg;
}
