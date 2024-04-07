package com.longfor.datav.common.vo.resp;

import com.longfor.datav.common.vo.TeamChildDimensionVo;
import lombok.Data;

import java.util.List;

/**
 * 团队指标明细接口响应实体
 * @author zyh
 * @since jdk 1.8
 * @date 2024-01-29
 */

@Data
public class TeamDimensionDetailResponse {

    /**
     * 指标编码
     */
    private String dimensionCode;

    /**
     * 指标名称
     */
    private String dimensionName;

    /**
     * 指标描述
     */
    private String desc;

    /**
     * 子节点列表
     */
    private List<TeamChildDimensionVo> childList;
}
