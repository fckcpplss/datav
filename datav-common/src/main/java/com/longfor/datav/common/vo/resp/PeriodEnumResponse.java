package com.longfor.datav.common.vo.resp;

import lombok.Data;

/**
 * 周期枚举接口响应实体
 * @author zyh
 * @since jdk 1.8
 * @date 2024-01-31
 */

@Data
public class PeriodEnumResponse {

    /**
     * 名称
     */
    private String name;

    /**
     * 周期编码
     */
    private String period;
}
