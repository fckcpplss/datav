/*

package com.longfor.datav.admin.config;

import com.longfor.lmember.platform.common.permission.function.interceptor.FunctionPermissionInterceptor;
import com.longfor.lmember.platform.common.sso.interceptor.SSOLoginInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

*/
/**
 * sso拦截登录
 * @author zyh
 * @since jdk 1.8
 * @date 2024-01-25
 *//*

@Configuration
public class SSOLoginFilterConfig implements WebMvcConfigurer {
    @Bean
    public SSOLoginInterceptor requestFilter() {
        return new SSOLoginInterceptor();
    }

    @Bean
    public FunctionPermissionInterceptor getFunctionPermissionInterceptor() {
        return new FunctionPermissionInterceptor();
    }

    */
/**
     * 配置拦截请求
     **//*

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        WebMvcConfigurer.super.addInterceptors(registry);
        //配置项目实际需要拦截的请求路径
        registry.addInterceptor(requestFilter()).addPathPatterns("/admin/datav/**")
                .excludePathPatterns("/admin/datav/healthy")
                .excludePathPatterns("/admin/datav/test/**",
                        "/admin/datav/pop/external/**");

        //配置功能权限拦截器
        registry.addInterceptor(getFunctionPermissionInterceptor()).addPathPatterns("/**");
    }
}*/
