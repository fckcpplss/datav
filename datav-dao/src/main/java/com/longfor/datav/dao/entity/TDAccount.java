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
 * 账号表
 * </p>
 *
 * @author zhaoyalong
 * @since 2024-01-29
 */
@Getter
@Setter
@TableName("t_d_account")
public class TDAccount implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键字增
     */
      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * oa账号
     */
    private String account;

    /**
     * 姓名
     */
    @TableField("`name`")
    private String name;

    /**
     * 状态(在职、离职等)
     */
    @TableField("`status`")
    private Integer status;

    /**
     * 用户类型(内部、散包、整包等)
     */
    private Integer type;

    /**
     * 角色(1.SDM,SDE,QA,PM)
     */
    private Integer role;

    /**
     * 岗位(1.后端开发，2.前端开发，3.运营员工，4.测试员工，5.产品员工)
     */
    private Integer job;

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

    /**
     * 是否删除(0.删除，1.正常)
     */
    private Integer isDelete;
}
