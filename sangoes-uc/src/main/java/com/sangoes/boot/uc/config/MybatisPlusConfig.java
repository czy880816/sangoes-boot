package com.sangoes.boot.uc.config;

import com.baomidou.mybatisplus.core.injector.ISqlInjector;
import com.baomidou.mybatisplus.extension.injector.LogicSqlInjector;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.PerformanceInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Copyright (c) 2018
 *
 * @author jerrychir
 * @date 2018/10/29 9:57 AM
 */
@EnableTransactionManagement
@Configuration
@MapperScan(basePackages = { "com.sangoes.boot.uc.modules.admin.mapper" })
public class MybatisPlusConfig {

    /**
     * sql注入
     *
     * @return
     */
    @Bean
    public ISqlInjector sqlInjector() {
        return new LogicSqlInjector();
    }

    /**
     * mybatis-plus分页插件<br>
     * 文档：http://mp.baomidou.com<br>
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        // /*
        // * 【测试多租户】 SQL 解析处理拦截器<br>
        // * 这里固定写成住户 1 实际情况你可以从cookie读取，因此数据看不到 【 麻花藤 】 这条记录（ 注意观察 SQL ）<br>
        // */
        // List<ISqlParser> sqlParserList = new ArrayList<>();
        // TenantSqlParser tenantSqlParser = new TenantSqlParser();
        // tenantSqlParser.setTenantHandler(new TenantHandler() {
        // @Override
        // public Expression getTenantId() {
        // return new LongValue(1L);
        // }
        //
        // @Override
        // public String getTenantIdColumn() {
        // return "tenant_id";
        // }
        //
        // @Override
        // public boolean doTableFilter(String tableName) {
        // // 这里可以判断是否过滤表
        // /*if ("user".equals(tableName)) {
        // return true;
        // }*/
        // return false;
        // }
        // });
        //
        // sqlParserList.add(tenantSqlParser);
        // paginationInterceptor.setSqlParserList(sqlParserList);
        // paginationInterceptor.setSqlParserFilter(new ISqlParserFilter() {
        // @Override
        // public boolean doFilter(MetaObject metaObject) {
        // MappedStatement ms = PluginUtils.getMappedStatement(metaObject);
        // // 过滤自定义查询此时无租户信息约束【 麻花藤 】出现
        // if
        // ("com.baomidou.springboot.mapper.UserMapper.selectListBySQL".equals(ms.getId()))
        // {
        // return true;
        // }
        // return false;
        // }
        // });
        return paginationInterceptor;
    }

    /**
     * 相当于顶部的： {@code @MapperScan("com.baomidou.springboot.mapper*")}
     * 这里可以扩展，比如使用配置文件来配置扫描Mapper的路径
     */
    // @Bean
    // public MapperScannerConfigurer mapperScannerConfigurer() {
    // MapperScannerConfigurer scannerConfigurer = new MapperScannerConfigurer();
    // scannerConfigurer.setBasePackage("com.baomidou.springboot.mapper*");
    // return scannerConfigurer;
    // }

    // @Bean
    // public H2KeyGenerator getH2KeyGenerator() {
    // return new H2KeyGenerator();
    // }

    /**
     * 性能分析拦截器，不建议生产使用 设置 dev test 环境开启
     */
    @Bean
    @Profile({ "dev", "test" })
    public PerformanceInterceptor performanceInterceptor() {
        return new PerformanceInterceptor();
    }
}