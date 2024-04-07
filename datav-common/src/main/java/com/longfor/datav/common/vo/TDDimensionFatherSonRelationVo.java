package com.longfor.datav.common.vo;

import lombok.Data;

/**
 * 父子关系实体
 * @author zyh
 * @since jdk 1.8
 * @date 2024-02-06
 */

@Data
public class TDDimensionFatherSonRelationVo {

    private String pName;

    private String pValue;

    private String cName;

    private String cValue;
}
