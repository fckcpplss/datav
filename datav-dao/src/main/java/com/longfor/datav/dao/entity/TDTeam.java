package com.longfor.datav.dao.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 团队表
 * </p>
 *
 * @author zhaoyalong
 * @since 2024-01-29
 */
@Getter
@Setter
@TableName("t_d_team")
public class TDTeam implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增主键
     */
    @TableId(value = "id", type = IdType.AUTO)
      private Long id;

    /**
     * 团队编码(ALM产品编码)
     */
    @TableField("`code`")
    private String code;

    /**
     * 团队名称
     */
    @TableField("`name`")
    private String name;

    /**
     * 团队负责人
     */
    private String principal;

    /**
     * SDM
     */
    private String sdm;

    /**
     * 团队介绍
     */
    @TableField("`desc`")
    private String desc;

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
     * 修改时间
     */
      @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 修改人
     */
    private String updateUser;
}
