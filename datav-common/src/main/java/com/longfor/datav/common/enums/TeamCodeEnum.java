package com.longfor.datav.common.enums;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

/**
 * 团队枚举类
 * @author zhaoyalong
 * @date 2024-01=2-19
 */

@Getter
@AllArgsConstructor
public enum TeamCodeEnum {
    C1U_XIANGJIA("C1Uhome", "C1U享家"),
    HUIYUAN_YUNYING("huiyuanyunying", "会员运营"),
    HUIYUAN_KAQUAN("huiyuankaquan", "会员卡券"),
    RIYUE_HU("riyuehu", "日月湖"),
    C4U_XIANGJIA("C4Uhome", "C4U享家"),
    TANG_E_APP("tangeApp", "塘鹅APP"),
    LONGZHU_JIFEN("longzhujifen", "珑珠积分"),
    KEFU_PINGTAI("kefupingtai", "客服平台"),
    MALL_COUPON_TEAM("MallCouponTeam", "商城卡券组"),
    OPERATIONS_AND_EVENTS_TEAM("OperationsAndEventsTeam", "运营平台及活动组"),
    C_END_SERVICE_AND_MEMBERCHANNEL_TEAM("CEndServiceAndMemberChannelTeam", "C端服务及会员频道组"),
    C_END_BUSINESS_FORMAT_HOMEPAGE_TEAM("CEndBusinessFormatHomepageTeam", "C端业态首页组"),
    MEMBERSHIP_OPERATIONS3("MembershipOperations3.5ProjectTeam", "会员运营3.5项目组"),
    CONTENT_DISCOVERY_CHANNEL_TEAM("ContentDiscoveryChannelTeam", "内容及发现频道组"),
    CUSTOMER_SERVICE_PLATFORM_TEAM("CustomerServicePlatformTeam", "客服平台组"),
    TEST("test", "测试组");
    ;

    private String code;

    private String message;

    public static TeamCodeEnum fromCode(String code){
        return Optional.ofNullable(code)
                .map(c -> {
                    return Arrays.stream(TeamCodeEnum.values())
                            .filter(x -> x.getCode().equals(code))
                            .findFirst()
                            .orElse(null);
                })
                .orElse(null);
    }
    public static TeamCodeEnum fromMsg(String msg){
        return Optional.ofNullable(StrUtil.blankToDefault(msg,null))
                .map(c -> {
                    return Arrays.stream(TeamCodeEnum.values())
                            .filter(x -> x.getMessage().equals(msg))
                            .findFirst()
                            .orElse(null);
                })
                .orElse(null);
    }


}
