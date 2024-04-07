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
 * 快照表
 * </p>
 *
 * @author zhaoyalong
 * @since 2024-01-29
 */
@Getter
@Setter
@TableName("t_d_snapshot_table")
public class TDSnapshotTable implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增主键
     */
    @TableId(value = "id", type = IdType.AUTO)
      private Long id;

    /**
     * 快照账户类型(0.个人，1.团队)
     */
    private Integer type;

    /**
     * 快照标识code(account或者team_code)
     */
    private String codeFlag;

    /**
     * 快照标识名称(用户名或团队名)
     */
    private String nameFlag;

    /**
     * 周期标记，与流水表字段对应
     */
    private String periodFlag;

    /**
     * 快照内容
     */
    private String content;

    /**
     * 快照时间
     */
    private LocalDateTime snapshotTime;

    /**
     * 快照模块标识
     */
    private String modelFlag;

    /**
     * 快照数据的年份
     */
    private String year;
}
