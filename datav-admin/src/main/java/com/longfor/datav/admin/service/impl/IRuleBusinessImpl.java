package com.longfor.datav.admin.service.impl;

import cn.hutool.core.util.StrUtil;
import com.longfor.datav.admin.service.IRuleBusiness;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Service;

import java.util.HashMap;

/**
 * 规则
 *
 * @Auther liying
 * @Date 2024/3/4
 */
@Service
public class IRuleBusinessImpl implements IRuleBusiness {


    @Override
    public String bizDecisionRule(HashMap<String,Object> params) {

        // 获取KieServices实例
        KieServices ks = KieServices.Factory.get();
        // 获取KieContainer，该容器包含规则引擎的知识库
        KieContainer kContainer = ks.getKieClasspathContainer();
        // 创建KieSession，该会话用于执行规则
        KieSession kieSession = kContainer.newKieSession("all-rules");
        // 插入参数
        kieSession.insert(params);
        // 执行指定规则
        kieSession.fireAllRules(rule -> rule.getRule().getName().equals(params.get("dimensionalName")));
        // 关闭规则引擎
        kieSession.dispose();
        return params.get("score").toString();
    }
}
