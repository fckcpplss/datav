package com.longfor.datav.dao;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.converts.MySqlTypeConvert;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.config.rules.IColumnType;
import com.baomidou.mybatisplus.generator.fill.Column;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhaoyl
 * @date 2021/9/29 上午11:12
 * @since 1.0
 */
@Slf4j
public class MybatisPlusGenerator {
    //项目路径
    private static final String PROJECT_PATH = System.getProperty("user.dir");
    //包名
    private static final String PACKAGE_NAME = "com.longfor.datav";

    @Test
    public void run(){
        FastAutoGenerator.create(new DataSourceConfig
                  .Builder("jdbc:mysql://10.12.80.14:3306/cms_uat?serverTimezone=GMT%2b8&useUnicode=true&characterEncoding=UTF8&useSSL=false&allowMultiQueries=true", "cms_uat_ddl", "vnefXN$@KIbjV9J!")


                //tinyint不自动转boolean
                .typeConvert(new ITypeConvert(){

                    @Override
                    public IColumnType processTypeConvert(GlobalConfig globalConfig, String fieldType) {
                        String t = fieldType.toLowerCase();
                        if (t.contains("tinyint")) {
                            return DbColumnType.INTEGER;
                        }
                        if(t.contains("datetime")){
                            return DbColumnType.DATE;
                        }
                        return new MySqlTypeConvert().processTypeConvert(globalConfig, fieldType);
                    }
                }))
                .globalConfig((scanner,builder) -> builder
                            //设置作者
                            .author("zhaoyalong")
                            //开启 swagger 模式
                            //.enableSwagger()
                            .commentDate("yyyy-MM-dd")
                            //禁止打开输出目录
                            .disableOpenDir()
                            // 指定输出目录
                            .outputDir(StrUtil.concat(true,PROJECT_PATH ,"/src/main/java")).build()
                )
                // 策略配置
                //all所有表，指定表逗号分隔开
                .strategyConfig((scanner, builder) -> builder
                        //多个英文逗号分隔？所有输入 all
                        .addInclude(getTables(
                                "t_d_dimension_integral"
                        ))
//                        .addTablePrefix("t_d_")
//                        .controllerBuilder()
//                        .enableRestStyle()
//                        .enableHyphenStyle()
//                        .logicDeletePropertyName("deleteStatus")
                        .entityBuilder()
                        .enableLombok()
                        .enableRemoveIsPrefix()
                        //逻辑删除属性名称
                        .addTableFills(
                                new Column("create_time", FieldFill.INSERT),
                                new Column("update_time", FieldFill.INSERT_UPDATE)
                        ).build())
                .packageConfig(builder -> {
                    builder
                            //设置父包名
                            .parent(PACKAGE_NAME)
                            //设置父包模块名
                            .moduleName("")
                            .entity("dao.entity")
                            .mapper("dao.mapper")
                            .service("dao.service")
                            .serviceImpl("dao.service.impl")
                            //设置mapperXml生成路径
                            .pathInfo(ImmutableMap.of(OutputFile.xml, StrUtil.concat(true,PROJECT_PATH,"/src/main/resources/",PACKAGE_NAME.replace(".","/"),"/dao/orm/mybatis/mapping"), OutputFile.entity,StrUtil.concat(true,PROJECT_PATH,"/src/main/java/",PACKAGE_NAME.replace(".", "/"),"/dao/entity")));
                })
                .injectionConfig(builder -> {
                    builder.beforeOutputFile((tableInfo, objectMap) -> {
                        System.out.println("tableInfo: " + tableInfo.getEntityName() + " objectMap: " + objectMap.size());
                    }).build();
                })
                .templateConfig(builder -> {
                    builder.disable(TemplateType.CONTROLLER);
                })
                // 使用Freemarker引擎模板，默认的是Velocity引擎模板
//                .templateEngine(new FreemarkerTemplateEngine())
                .execute();
    }
    protected static List<String> getTables(String tables) {
        return "all".equals(tables) ? Collections.emptyList() : Arrays.asList(tables.split(","));
    }
    protected static List<String> getTables(String... table) {
        return getTables(Arrays.stream(table).collect(Collectors.joining(",")));
    }

    public static void main(String[] args) {
        String concat = StrUtil.concat(true, PROJECT_PATH, "/src/main/resources/", PACKAGE_NAME.replace(".", "/"), "/dao/mapping");
    }

}
