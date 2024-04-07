package com.longfor.datav.common.enums;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

/**
 * 快照账号类型
 * @author zyh
 * @since jdk 1.8
 * @date 2024-01-30
 */

@Getter
@AllArgsConstructor
public enum SnapshotAccountTypeEnum {
    PERSON(0, "个人"),
    TEAM(1, "团队");

    private int code;

    private String message;

    public static SnapshotAccountTypeEnum fromMsg(String msg){
        return Optional.ofNullable(StrUtil.blankToDefault(msg,null))
                .map(c -> {
                    return Arrays.stream(SnapshotAccountTypeEnum.values())
                            .filter(x -> x.getMessage().equals(msg))
                            .findFirst()
                            .orElse(null);
                })
                .orElse(null);
    }
}
