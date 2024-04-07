package com.longfor.datav.common.vo.resp;

import com.longfor.datav.common.vo.PageInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 成员列表返回结果
 * @author zhaoyl
 * @date 2024/1/26 10:34
 * @since 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberListResponse extends PageInfo {
    /**
     * 排名
     */
    private Integer rank;
    /**
     * 团队编码
     */
    private String teamCode;
    /**
     * 成员oa
     */
    private String oaAccount;

    /**
     * 成员名称
     */
    private String name;
    /**
     * 成员手机号
     */
    private String phone;

    /**
     * 成员角色
     */
    private String role;
    /**
     * 成员状态
     */
    private String status;

    /**
     * 所属团队
     */
    private String teamName;

    /**
     * 团队负责人
     */
    private String principalName;

    /**
     * 团队sdm
     */
    private String teamSdm;

    /**
     * 团队介绍
     */
    private String desc;

    /**
     * 所属岗位
     */
    private String postName;

    /**
     * 个人指标得分
     */
    private String score;
}
