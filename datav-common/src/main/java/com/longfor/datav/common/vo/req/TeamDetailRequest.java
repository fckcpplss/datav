package com.longfor.datav.common.vo.req;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 查询团队相关信息请求实体
 * @author zyh
 * @since jdk 1.8
 * @date 2024-01-29
 */

@EqualsAndHashCode(callSuper = true)
@Data
public class TeamDetailRequest extends BaseTeamRequest{

    /**
     * 团队编码
     */
    private String teamCode;
}
