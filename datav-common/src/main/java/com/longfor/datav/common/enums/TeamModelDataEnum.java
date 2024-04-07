package com.longfor.datav.common.enums;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

/**
 * 团队快照模块
 * @author zyh
 * @since jdk 1.8
 * @date 2024-01=30
 */

@AllArgsConstructor
@Getter
public enum TeamModelDataEnum {

    TEAM_ACCOUNT("teamAccount", "团队成员"),
    TEAM_DETAIL("teamDetail", "团队详情"),
    TEAM_OVERALL_STANDING("overallStandings", "团队排名"),
    TEAM_DIMENSION_SCORE("dimensionScore", "指标评分"),
    TEAM_HISTORY("history", "团队历史数据"),
    TEAM_DIMENSION_DETAIL("dimensionDetail", "指标得分明细");

    private String code;

    private String msg;

    public static TeamModelDataEnum fromCode(String code){
        return Optional.ofNullable(StrUtil.blankToDefault(code,null))
                .map(c -> {
                    return Arrays.stream(TeamModelDataEnum.values())
                            .filter(x -> x.getCode().equals(code))
                            .findFirst()
                            .orElse(null);
                })
                .orElse(null);
    }
}
