package com.longfor.datav.common.vo.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 成员详情返回结果
 * @author zhaoyl
 * @date 2024/1/26 10:34
 * @since 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberInfoResponse {
    /**
     * 成员oa
     */
    private String oaAccount;

    /**
     * 成员名称
     */
    private String name;

    /**
     * 成员角色
     */
    private String role;

    /**
     * 排名
     */
    private Integer rank;

    /**
     * 得分
     */
    private Integer score;

    /**
     * 所属团队
     */
    private String teamName;

    /**
     * 团队编码
     */
    private String teamCode;

    /**
     * 团队得分
     */
    private Integer teamScore;

    /**
     * 指标说明
     */
    private String desc;


    /**
     * 指标得分
     */
    List<ScoreInfo> list;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ScoreInfo{
        /**
         * 指标名称
         */
        private String metrics;
        /**
         * 得分/个数
         */
        private Integer score;
        /**
         * 排名
         */
        private Integer rank;
    }

}
