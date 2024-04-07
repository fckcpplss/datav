package com.longfor.datav.common.vo.resp;

import com.longfor.datav.common.vo.TeamHistoryVo;
import lombok.Data;

import java.util.List;

/**
 * 团队历史分数响应实体
 * @author zyh
 * @since jdk 1.8
 * @date 2024-01-29
 */

@Data
public class TeamHistoryResponse {

    private String teamName;

    private List<TeamHistoryVo> historyDataList;
}
