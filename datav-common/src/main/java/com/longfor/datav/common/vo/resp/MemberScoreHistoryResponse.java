package com.longfor.datav.common.vo.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 成员得分历史返回结果
 * @author zhaoyl
 * @date 2024/1/26 10:34
 * @since 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberScoreHistoryResponse {
    /**
     * 冲刺周期
     */
    private String sprintCycle;

    /**
     * 指标编码
     */
    private String code;

    /**
     * 指标名称
     */
    private String metrics;

    /**
     * 指标说明
     */
    private String desc;
    /**
     * 得分/个数
     */
    private String score;
    /**
     * 分值
     */
    private String value;
    /**
     * 排名
     */
    private Integer rank;

}
