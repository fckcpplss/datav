package com.longfor.datav.common.utils;

import cn.hutool.core.util.StrUtil;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 规则引擎工具类
 * @author zhaoyl
 * @date 2024/1/25 14:43
 * @since 1.0
 */
public class RuleUtil {
    /**
     * 执行规则，获取分数
     * @param params 执行参数
     * @return Integer 执行规则后的分数
     */
    public static Integer execute(Map<String,Object> params){
        String ruleName = Optional.ofNullable(params)
                .map(param -> param.get("ruleName"))
                .map(String::valueOf)
                .orElseThrow(() -> new RuntimeException("规则名称不能为空"));
        return execute(ruleName,params);
    }
    /**
     * 执行规则，获取分数
     * @param ruleName 规则名称
     * @param params 执行参数
     * @return Integer 执行规则后的分数
     */
    public static Integer execute(String ruleName,Map<String,Object> params){
        if(StringUtils.isBlank(ruleName)){
            throw new RuntimeException("规则名称不能为空");
        }
        params.put("score",0);
        // 获取KieServices实例
        KieServices ks = KieServices.Factory.get();
        // 获取KieContainer，该容器包含规则引擎的知识库
        KieContainer kContainer = ks.getKieClasspathContainer();
        // 创建KieSession，该会话用于执行规则
        KieSession kieSession = kContainer.newKieSession("all-rules");
        // 插入参数
        kieSession.insert(params);
        // 执行指定规则
        kieSession.fireAllRules(rule -> rule.getRule().getName().equals(ruleName));

        kieSession.fireAllRules();
        // 打印规则执行后的结果
        System.out.println(StrUtil.format("规则执行后，得分由{}变为: {}",params.get("oldScore"), params.get("score")));
        // 关闭规则引擎
        kieSession.dispose();
        return Optional.ofNullable(params.get("score")).map(String::valueOf).map(Integer::parseInt).orElse(null);
    }

    /**
     * 批量执行规则，获取分数累计计算
     * @Param params 批量规则和参数
     * @return Integer 批量执行规则后的分数
     */
    public static Integer execute(List<Map> params){

        if(CollectionUtils.isEmpty(params)){
            throw new RuntimeException("规则名称不能为空");
        }
        //初始分数
        Integer oldScore = Optional.ofNullable(params.get(0).get("oldScore")).map(String::valueOf).map(Integer::parseInt).orElse(0);
        //上次执行后的分数
        AtomicInteger prevScore = new AtomicInteger(oldScore);
        return params
                .stream()
                .map(param -> {
                    param.put("oldScore",prevScore.get());
                    prevScore.set(execute(param));
                    return prevScore.get();
                })
                .map(String::valueOf)
                .map(Integer::parseInt)
                .reduce((a,b) -> b).orElse(null);
    }

    /**
     * 执行规则，获取分数
     * @param ruleName 规则名称
     * @param dimensional 规则值
     * @param oldScore 旧分数
     * @return Integer 执行规则后的分数
     */
    public static Integer execute(String ruleName,String dimensional){
        Map<String,Object> params = Maps.newHashMap();
        params.put("dimensional",dimensional);
        return execute(ruleName,params);
    }

    /**
     * 执行规则，获取分数
     * @param ruleName 规则名称
     * @param dimensional 规则值
     * @param oldScore 旧分数
     * @return Integer 执行规则后的分数
     */
    public static Integer execute(String ruleName,String dimensional,Integer oldScore){
        Map<String,Object> params = Maps.newHashMap();
        params.put("dimensional",dimensional);
        params.put("oldScore",oldScore);
        return execute(ruleName, params);
    }

    /**
     * 执行规则，获取分数
     * @param ruleName 规则名称
     * @param value 规则值
     * @param times 规则次数
     * @param oldScore 旧分数
     * @return Integer 执行规则后的分数
     */
    public static Integer execute(String ruleName,String dimensional,Integer times,Integer oldScore){
        Map<String,Object> params = Maps.newHashMap();
        params.put("dimensional",dimensional);
        params.put("oldScore",oldScore);
        params.put("times",times);
        return execute(ruleName, params);
    }
    public static void main(String[] args) {
        List<Map> list = new ArrayList(){{
            add(new HashMap<String,Object>(){{
                put("ruleName","person-kygxjtpm");
                put("value",1);
                put("times",10);
                put("oldScore",100);
            }});
            add(new HashMap<String,Object>(){{
                put("ruleName","person-grzbwd");
                put("value",2);
                put("times",10);
                put("oldScore",100);
            }});
            add(new HashMap<String,Object>(){{
                put("ruleName","person-hx");
                put("value",2);
                put("times",10);
                put("oldScore",100);
            }});
        }};
        System.out.println(execute(list));
    }
}
