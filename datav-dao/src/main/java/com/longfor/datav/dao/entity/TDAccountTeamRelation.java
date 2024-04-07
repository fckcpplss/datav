package com.longfor.datav.dao.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 账号和项目关系
 * </p>
 *
 * @author zhaoyalong
 * @since 2024-01-29
 */
@Getter
@Setter
@TableName("t_d_account_team_relation")
public class TDAccountTeamRelation implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增主键
     */
    @TableId(value = "id", type = IdType.AUTO)
      private Long id;

    /**
     * 团队code
     */
    private String teamCode;

    /**
     * oa账号
     */
    private String account;

    /**
     * 当前状态(0.离编，1.在编)
     */
    private Integer status;

    /**
     * 起始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 类型(0.员工，2.负责人)
     */
    private Integer type;

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
     * 更新时间
     */
      @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 更新人
     */
    private String updateUser;
}
