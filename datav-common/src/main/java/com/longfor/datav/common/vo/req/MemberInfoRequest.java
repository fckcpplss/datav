package com.longfor.datav.common.vo.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * 成员详情请求参数
 * @author zhaoyl
 * @date 2024/1/26 10:34
 * @since 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberInfoRequest {
    /**
     * 冲刺周期
     */
    private String period;
    /**
     * 成员oa
     */
    @NotBlank(message = "成员oa不能为空")
    private String oaAccount;

    /**
     * 纬度编码
     */
    private String dimensionCode;
}
