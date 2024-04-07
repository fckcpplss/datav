package com.longfor.datav.admin;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 技术指标系统启动类
 * @author zyh
 * @since jdk 1.8
 * @date 2024-01-24
 */

@Slf4j
@SpringBootApplication(exclude = DruidDataSourceAutoConfigure.class)
@EnableApolloConfig
@ComponentScan("com.longfor.**")
@MapperScan({"com.longfor.datav.dao.mapper"})
public class Application {

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        SpringApplication.run(Application.class, args);
        log.info("应用已启动, 耗时 {} ms", (System.currentTimeMillis() - start));
    }
}
