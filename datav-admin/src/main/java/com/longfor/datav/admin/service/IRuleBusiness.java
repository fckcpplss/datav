package com.longfor.datav.admin.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;

/**
 * 规则
 *
 * @Auther liying
 * @Date 2024/3/4
 */
public interface IRuleBusiness {
    /**
     * 规则计算得分
     * @param map
     * @return
     */
    String bizDecisionRule(HashMap<String,Object> map);
}
