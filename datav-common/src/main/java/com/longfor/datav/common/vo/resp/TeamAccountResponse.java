package com.longfor.datav.common.vo.resp;

import lombok.Data;

/**
 * 团队成员列表数据
 * @author zyh
 * @since jdk 1.8
 * @date 2024-01-29
 */

@Data
public class TeamAccountResponse {

    /**
     * 排名
     */
    private int index;

    /**
     * 员工名称
     */
    private String name;

    /**
     * oa账号
     */
    private String account;

    /**
     * 员工状态
     */
    private int status;

    /**
     * 所属岗位
     */
    private String postName;

    /**
     * 员工类型
     */
    private int type;

    /**
     * 分数
     */
    private double score;
}
