package com.longfor.datav.common.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *  得分数据导入DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScoreDataImportDTO extends DataImportDTO {

    /**
     * 维度编码
     */
    @Excel(name = "维度编码",width= 20,orderNum = "0")
    private String dimensionCode;

    /**
     * 积分类型
     */
    @Excel(name = "积分类型",width= 20,orderNum = "1")
    private String type;

    /**
     * 账户编码
     */
    @Excel(name = "账户编码",width= 20,orderNum = "2")
    private String account;

    /**
     * 周期类型
     */
    @Excel(name = "周期类型",width= 20,orderNum = "3")
    private String periodType;

    /**
     * 周期
     */
    @Excel(name = "周期",width= 20,orderNum = "4")
    private String period;

    /**
     * 年份
     */
    @Excel(name = "年份",width= 20,orderNum = "5")
    private String year;

    /**
     * 分数
     */
    @Excel(name = "分数",width= 20,orderNum = "6")
    private String fraction;

    /**
     * 分值
     */
    @Excel(name = "分值",width= 20,orderNum = "7")
    private String value;
}
