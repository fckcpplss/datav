package com.longfor.datav.dao.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 周期与冲刺时间关联表
 * @author zyh
 * @since jdk 1.8
 * @date 2024-01-30
 */

@Getter
@Setter
@TableName("t_d_time_sprint_relation")
public class TDTimeSprintRelation implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键字增
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 周期code
     */
    private String period;

    /**
     * 周期名称
     */
    private String name;

    /**
     * 起始时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date startTime;

    /**
     * 结束时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date endTime;

    /**
     * 年份标识
     */
    private String year;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 更新状态
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 更新人
     */
    private String updateUser;
}
