package com.longfor.datav.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 纬度信息DTO
 * @author zhaoyl
 * @date 2024/2/1 17:47
 * @since 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DimensionInfoDTO {
    /**
     * 父纬度编码
     */
    private String dimensionCode;

    /**
     * 父纬度编码
     */
    private String dimensionName;

    /**
     * 纬度说明
     */
    private String dimensionDesc;

    /**
     * 得分
     */
    private String score;


}
