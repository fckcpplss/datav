package com.longfor.datav.common.vo.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 计算得分入参
 *
 * @Auther liying
 * @Date 2024/3/5
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalculateScoreReq {
    /**
     * 积分账户类型 0.个人，1.团队
     */
    @NotNull(message = "积分账户类型不能为空")
    private Integer type;

    /**
     * code标识，与type联动，个人积分记录则为oa账号，团队则为团队编码
     */
    @NotBlank(message = "codeFlag不能为空")
    private String codeFlag;

    /**
     * 周期类型：(1.冲刺，2.月份，3.年份)
     */
    @NotNull(message = "周期类型不能为空")
    private Integer periodType;

    /**
     * 周期标记，若为冲刺,则为S1等，若为月份，则2024年1月。若为年份则为2024年
     */
    @NotBlank(message = "周期标记不能为空")
    private String periodFlag;

    /**
     * 录入年份
     */
    @NotBlank(message = "录入年份不能为空")
    private String year;

    /**
     *  指标项名称，如：生产率[需求交付]
     */
    @NotBlank(message = "指标项名称不能为空")
    private String dimensionalName;

    /**
     * 指标数值，如：0.8
     */
    @NotBlank(message = "指标数值不能为空")
    private String dimensional;
}
