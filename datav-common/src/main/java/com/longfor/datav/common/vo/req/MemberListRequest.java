package com.longfor.datav.common.vo.req;

import com.longfor.datav.common.vo.PageInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 成员列表请求参数
 * @author zhaoyl
 * @date 2024/1/26 10:34
 * @since 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberListRequest extends PageInfo {
    /**
     * 成员名称
     */
    private String name;
    /**
     * 所属角色
     */
    private String role;

    /**
     * 成员账号集合
     */
    private List<String> accounts;
}
