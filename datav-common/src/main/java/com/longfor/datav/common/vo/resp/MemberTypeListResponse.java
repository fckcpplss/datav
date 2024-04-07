package com.longfor.datav.common.vo.resp;

import com.longfor.datav.common.vo.PageInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 成员类型列表返回结果
 * @author zhaoyl
 * @date 2024/1/26 10:34
 * @since 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberTypeListResponse{
    /**
     * 类型名称
     */
    private String name;
    /**
     * 类型值
     */
    private String value;
}
