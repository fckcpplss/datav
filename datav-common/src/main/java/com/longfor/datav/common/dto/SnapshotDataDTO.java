package com.longfor.datav.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.kie.api.definition.rule.All;

/**
 * 冲刺数据DTO
 * @author zhaoyl
 * @date 2024/1/30 17:14
 * @since 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SnapshotDataDTO {
    /**
     * 姓名或团队编码
     */
    private String code;
    /**
     * 姓名或团队名称
     */
    private String name;
    /**
     * 冲刺周期
     */
    private String sprintCycle;
    /**
     * 年份
     */
    private String year;

    /**
     * 快照内容
     */
    private String content;
}
