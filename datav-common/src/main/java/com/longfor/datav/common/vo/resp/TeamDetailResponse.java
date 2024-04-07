package com.longfor.datav.common.vo.resp;

import com.longfor.datav.common.vo.TeamDimensionScoreListVo;
import lombok.Data;

import java.util.List;

/**
 * 团队详情数据响应实体
 * @author zyh
 * @since jdk 1.8
 * @date 2024-01-29
 */

@Data
public class TeamDetailResponse {

    /**
     * 团队名称
     */
    private String teamName;

    /**
     * 团队编码
     */
    private String teamCode;

    /**
     * 分数
     */
    private double score;

    /**
     * 排名
     */
    private int index;

    /**
     * 团队负责人
     */
    private String principal;

    /**
     * sdm
     */
    private String sdm;

    /**
     * 团队描述
     */
    private String desc;

    /**
     * 团队指标列表
     */
    private List<TeamDimensionScoreListVo> dimensionScoreList;
}
