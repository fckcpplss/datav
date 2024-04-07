package com.longfor.datav.common.vo.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 计算得分出参
 *
 * @Auther liying
 * @Date 2024/3/5
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalculateScoreResp {

    /**
     * 指标得分
     */
    private String fraction;

    /**
     * 积分账户类型 0.个人，1.团队
     */
    private Integer type;

    /**
     * code标识，与type联动，个人积分记录则为oa账号，团队则为团队编码
     */
    private String codeFlag;

    /**
     * 周期类型：(1.冲刺，2.月份，3.年份)
     */
    private Integer periodType;

    /**
     * 周期标记，若为冲刺,则为S1等，若为月份，则2024年1月。若为年份则为2024年
     */
    private String periodFlag;

    /**
     * 录入年份
     */
    private String year;

    /**
     *  指标项名称，如：生产率[需求交付]
     */
    private String dimensionalName;

    /**
     * 指标数值，如：0.8
     */
    private String dimensional;
}
