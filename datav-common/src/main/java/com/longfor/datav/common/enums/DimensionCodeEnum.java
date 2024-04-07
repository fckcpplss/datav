package com.longfor.datav.common.enums;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

/**
 * 团队指标类型枚举
 * @author zyh
 * @since jdk 1.8
 * @date 2024-02-05
 */

@AllArgsConstructor
@Getter
public enum DimensionCodeEnum {
    /**
     * 个人纬度
     */
    SAFETY_RED_LINE("hongxian","aqhx","安全红线"),
    DATA_FAKING("hongxian","sjzj","数据造假"),
    OPEN_SOURCE_CONTRIBUTION("kygxjtpm1","kygxjtpm2","开源贡献集团排名"),
    KNOWLEDGE_SHARING_ZX("zscdfx1","zscdfx2-zx","知识沉淀分享-中心"),
    KNOWLEDGE_SHARING_DT("zscdfx1","zscdfx2-dt","知识沉淀分享-DT纬度"),
    KNOWLEDGE_SHARING_ZJSJ("zscdfx1","zscdfx2-zjsj","知识沉淀分享-最佳实践"),
    ORGANIZATION_CONTRIBUTION("zzgx1","zzgx2","组织贡献"),
    LATE_TESTING_STORIES("grzbwd","yqwtcgss","逾期提测故事数"),
    REOPENED_DEFECTS("grzbwd","qxckgs","缺陷重开个数"),
    LONG_DEFECT_FIX_TIME("grzbwd","qxxfsjdy2","缺陷修复时间 > 2"),
    FORGOTTEN_WORK_HOURS("grzbwd","gswjtb","工时忘记填报"),
    HUMAN_ERROR_CAUSED_FAULT("grzbwd","rwyydzgz","人为原因导致故障"),
    BRANCH_MERGE_ISSUES("grzbwd","fzhbwt","分支合并问题"),


    /**
     * 团队纬度
     */
    DEMAND_DELIVERY_PRODUCTIVITY("minjiezhixing","gongshifenxishengchanlv", "生产率[需求交付]"),
    WORK_HOURS_INVESTMENT("minjiezhixing","gongshifenxigongsitouru", "工时投入"),
    T2_T4_CODE_REVIEW("minjiezhixing","ttice", "T-2/T-4 提测"),
    ON_TIME_CODE_REVIEW("minjiezhixing","anshiticelvanshitice", "按时提测"),
    CODE_REVIEW_PLANNING("minjiezhixing","anshiticelvticeguihua", "提测规划"),
    UNPLANNED_STORY_RATIO("minjiezhixing","jihuawaigushizhanbi", "计划外故事占比"),
    EXCELLENT_LIST("minjiezhixing","ranjintuzhibiaoyouxiubang", "优秀榜"),
    TO_IMPROVE_LIST("minjiezhixing","ranjintuzhibiaodaitishengbang", "待提升榜"),
    SPRINT_OUTSIDE_CYCLE("minjiezhixing","gushichongciwaixunhuan", "故事冲刺外循环"),
    TEST_CASE_REVIEW("minjiezhixing","liuchengzhixingceshiyonglipengshen", "测试用例评审"),
    SPRINT_TECHNICAL_REVIEW("minjiezhixing","liuchengzhixingchongcijishupingshenqingkuang", "冲刺技术评审情况"),
    SPRINT_RETROSPECTIVE_MEETING("minjiezhixing","liuchengzhixingchongcifupanhui", "冲刺复盘会"),
    TECHNICAL_DEBT("jishuzhibiao","jishuzhai", "技术债"),
    TECHNICAL_DEBT_DENSITY("jishuzhibiao","jishuzhaimidu", "技术债密度"),
    INCREMENTAL_TECHNICAL_DEBT_DENSITY("jishuzhibiao","zengliangjishuzhaimidu", "增量技术债密度"),
    PERFORMANCE_ISSUES("jishuzhibiao","xingnengyinqiwenti", "性能引起的问题"),
    YANZHONG_TIMELY_SECURITY_FIXES("anquanguanli","yanzhonganquanloudongjishixiufu", "严重安全漏洞及时修复=0"),
    GAOWEI_TIMELY_SECURITY_FIXES("anquanguanli","gaoweianquanloudongjishixiufu", "高威安全漏洞及时修复=0"),
    ZHONGWEI_TIMELY_SECURITY_FIXES("anquanguanli","zhongweianquanloudongjishixiufu", "中威安全漏洞及时修复=0"),
    PRODUCT_SECURITY_RISK("anquanguanli","chanpinanquanfengxian", "产品安全风险"),
    AD_ARCHITECTURE_SECURITY_REVIEW_ISSUES("anquanguanli","adjiagoufushenanquan", "AD架构复审安全问题=0"),
    SECURITY_AUDIT_SYSTEM("anquanguanli","anquanshenjixitongjifengxian", "安全审计-系统级风险"),
    SECURITY_AUDIT_GREAT("anquanguanli","anquanshenjizhongdaquexian", "安全审计-重大缺陷"),
    SECURITY_AUDIT_GENERAL("anquanguanli","anquanshenjiyibanquexian", "安全审计-一般缺陷"),
    URGENT_CHANGES("biangengguanli","jinjibiangengshu", "紧急变更数=0"),
    TOTAL_CHANGES("biangengguanli","zongbiangengshu", "总变更数<3"),
    UNPLANNED_CHANGES("biangengguanli","jihuawaibiangeng", "计划外变更"),
    AVERAGE_RELEASE_TIME("biangengguanli","pingjinfabancishu", "平均发版时间"),
    TRUNK_DEVELOPMENT_BRANCHES("daimaguanli","zhugankaifahuofenzhishudayu5", "主干开发|分支数>5"),
    NON_STANDARD_BRANCH_NAMING("daimaguanli","feibiaozhunmingming", "非标准分支命名"),
    COMMIT_SPECIFICATION("daimaguanli","commitguifan", "commit 规范"),
    EFFECTIVE_COMMENT_TO_CODE_RATIO("daimaguanli","daimahangshupaiming", "有效comment数量/代码行数排名"),
    BUGS_FOUND_IN_REVIEW("daimaguanli","reviewbug", "Review 发现bug"),
    CODE_REVIEW_FOCUS("daimaguanli","jizhongdaimapingshen", "集中代码评审"),
    CODE_REVIEW_BUG_RATE("daimaguanli","daimapingshenquexianzhanbi", "代码评审阶段缺陷占比"),
    PROBING_MONITORING_INTEGRATION("jiankongguanli","bocejiankongjierubaifenbai", "拨测监控接入=100%"),
    MONITORING_ALERT_CONFIGURATION("jiankongguanli","jiankonggaojingpeizhi", "监控告警配置"),
    ERROR_LOG_MANAGEMENT("jiankongguanli","cuowurizhichuli", "错误日志管理"),
    SENSITIVE_LOG_MANAGEMENT("jiankongguanli","minganrizhizhili", "敏感日志治理"),
    PRODUCT_STABILITY("chanpinwending","chanpinwendingxingdayu99.95", "产品稳定性>99.95%"),
    GROUP_LEVEL_FAILURES_P0("chanpinwending","jituanguzhangp0", "集团级别故障-P0"),
    GROUP_LEVEL_FAILURES_P1("chanpinwending","jituanguzhangp1", "集团级别故障-P1"),
    GROUP_LEVEL_FAILURES_P2("chanpinwending","jituanguzhangp2", "集团级别故障-P2"),
    GROUP_LEVEL_FAILURES_CONCEAL("chanpinwending","jituanguzhangyinman", "集团级别故障-隐瞒"),
    CENTER_LEVEL_EVENTS_P0("chanpinwending","zhongxinjibieguzhangp0", "中心级别事件-P0"),
    CENTER_LEVEL_EVENTS_P1("chanpinwending","zhongxinjibieguzhangp1", "中心级别事件-P1"),
    CENTER_LEVEL_EVENTS_P2("chanpinwending","zhongxinjibieguzhangp2", "中心级别事件-P2"),
    CENTER_LEVEL_EVENTS_CONCEAL("chanpinwending","zhongxinjibieguzhangyinman", "中心级别事件-隐瞒"),
    INCREMENTAL_UNIT_TEST_COVERAGE("zhiliangguanli","zengliangdancedayu25", "增量单测>25%"),
    FULL_UNIT_TEST_COVERAGE("zhiliangguanli","quanliangdancedayu30", "全量单测>30%"),
    FAILED_UNIT_TESTS("zhiliangguanli","danceshaichabutongguo", "单测筛查不过"),
    SMOKE_TEST_PASS_RATE("zhiliangguanli","maoyantongguolv", "冒烟通过率"),
    AVERAGE_DEFECT_RESOLUTION_TIME("zhiliangguanli","quexianpingjunxiufushijian", "缺陷平均修复时间"),
    DEFECT_ESCAPE_RATE("zhiliangguanli","quexiantaoyilv", "缺陷逃逸率 （故障复盘确定）"),
    ACCEPTANCE_DEFECT_RATIO("zhiliangguanli","yanshouquexianzhanbi", "验收缺陷占比"),
    TECH_COMMUNITY_CONFERENCE("tuanduiguanli","jishushequdahui", "技术社区大会"),
    PRODUCT_COMMUNITY_CONFERENCE("tuanduiguanli","chanpinshequdahui", "产品社区大会"),
    PROJECT_MANAGEMENT_COMMUNITY_CONFERENCE("tuanduiguanli","xiangmuguanlishequdahui", "项目管理社区大会"),
    ;
    private String group;

    private String code;

    private String msg;

    public static DimensionCodeEnum fromCode(String code){
        return Optional.ofNullable(code)
                .map(c -> {
                    return Arrays.stream(DimensionCodeEnum.values())
                            .filter(x -> x.getCode() == code)
                            .findFirst()
                            .orElse(null);
                })
                .orElse(null);
    }

    public static DimensionCodeEnum fromMsg(String msg){
        return Optional.ofNullable(StrUtil.blankToDefault(msg,null))
                .map(c -> {
                    return Arrays.stream(DimensionCodeEnum.values())
                            .filter(x -> x.getMsg().equals(msg))
                            .findFirst()
                            .orElse(null);
                })
                .orElse(null);
    }
}
