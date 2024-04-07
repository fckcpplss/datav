package com.longfor.datav.common.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

/**
 * @author zhaoyl
 * @date 2024/1/26 10:34
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageInfo {
    @Range(min = 1, max = 99999999, message = "页码超出范围[1-99999999]")
    private Integer pageNum = 1;
    @Range(min = 1, max = 200,message = "每页条数超出范围[1-200]")
    private Integer pageSize = 10;
}
