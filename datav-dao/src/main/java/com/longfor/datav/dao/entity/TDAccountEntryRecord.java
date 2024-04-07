package com.longfor.datav.dao.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 用户录入记录表
 * </p>
 *
 * @author zhaoyalong
 * @since 2024-01-29
 */
@Getter
@Setter
@TableName("t_d_account_entry_record")
public class TDAccountEntryRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增主键
     */
    @TableId(value = "id", type = IdType.AUTO)
      private Long id;

    /**
     * 账号维度录入id
     */
    private String accountDimensionId;

    /**
     * 数量
     */
    private Object amount;

    /**
     * 单位
     */
    private String unit;

    /**
     * 备注
     */
    private String remark;

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
