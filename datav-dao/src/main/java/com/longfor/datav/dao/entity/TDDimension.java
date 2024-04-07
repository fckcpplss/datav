package com.longfor.datav.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 维度
 * </p>
 *
 * @author zhaoyalong
 * @since 2024-01-29
 */
@Getter
@Setter
@TableName("t_d_dimension")
public class TDDimension implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增主键
     */
    @TableId(value = "id", type = IdType.AUTO)
      private Long id;

    /**
     * 维度code
     */
    private String code;

    /**
     * code路径，'-'间隔
     */
    private String path;

    /**
     * 层级
     */
    private Integer level;

    /**
     * 维度名称
     */
    private String name;

    /**
     * name路径，'-'间隔
     */
    private String namePath;

    /**
     * 父节点
     */
    private String parentCode;

    /**
     * 是否是第一层节点(0.是，1.否)
     */
    private Integer isFirstNode;

    /**
     * 维度类别(1.产品，2.个人，3.其他)
     */
    private Integer type;

    /**
     * 权重，默认是1
     */
    private double weights;

    /**
     * 备注
     */
    private String remark;

    /**
     * 维度状态(0.无效，1.有效)
     */
    private Integer status;

    /**
     * 是否被删除(0.删除，2.有效)
     */
    private Integer isDelete;

    /**
     * 负责人
     */
    private String responsible;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 创建时间
     */
    private LocalDateTime createDate;

    /**
     * 更新人
     */
    private String updateUser;

    /**
     * 更新时间
     */
    private LocalDateTime updateDate;

    /**
     * 初始分数
     *
     */
    private double initialScore;

    /**
     * 规则引擎编码
     */
    private String  ruleCode;

    /**
     * 分值上限
     */
    private double upperLimit;
}
