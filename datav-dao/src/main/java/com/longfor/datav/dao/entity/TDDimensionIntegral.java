package com.longfor.datav.dao.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 积分表
 * </p>
 *
 * @author zhaoyalong
 * @since 2024-02-01
 */
@Getter
@Setter
@TableName("t_d_dimension_integral")
public class TDDimensionIntegral implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增主键
     */
      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 维度code
     */
    private String dimensionCode;

    /**
     * 积分账户类型(0.团队，1.个人)
     */
    private Integer type;

    /**
     * code标识，与type联动，个人积分记录则为account，团队则为team表中的code
     */
    private String codeFlag;

    /**
     * 周期类型：(1.sprint，2.月份，3.年份)
     */
    private Integer periodType;

    /**
     * 周期标记，若为sprint,则为sprint1等，若为月份，则2024年1月。若为年份则为2024年
     */
    private String periodFlag;

    /**
     * 录入年份
     */
    private String year;

    /**
     * 分值
     */
    private String value;

    /**
     * 分数
     */
    private double fraction;

    /**
     * 创建人员
     */
    private String createUser;

    /**
     * 创建时间
     */
      @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新人员
     */
    private String updateUser;

    /**
     * 更新时间
     */
      @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
