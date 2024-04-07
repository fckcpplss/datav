package com.longfor.datav.dao.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 规则表
 * </p>
 *
 * @author zhaoyalong
 * @since 2024-01-29
 */
@Getter
@Setter
@TableName("t_d_rules")
public class TDRules implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增主键
     */
      private Long id;

    /**
     * 维度code
     */
    private String dimensionCode;

    /**
     * 阈值
     */
    private Object threshold;

    /**
     * 数据影响(0.负面，1.正面)
     */
    private Integer effect;

    /**
     * 数据更新动作(0.累加，1.替换，2.平均，3.保留最大，4.保留最小)
     */
    private Integer action;

    /**
     * 排序规则(ASC 正序，DESC 倒序)
     */
    private String indexRule;

    /**
     * 分数
     */
    private Object fraction;

    /**
     * 规则说明
     */
    private String explanation;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 创建时间
     */
      @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新人
     */
    private String updateUser;

    /**
     * 更新时间
     */
      @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
