package com.quickstart.bbframework.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application.database")
public class BBFrameworkProperties {
    // 是否初始化数据库
    private Boolean initialization = false;
    //默认初始化器
    private String initializer= "mysql_initializer";

    public Boolean getInitialization() {
        return initialization;
    }

    public void setInitialization(Boolean initialization) {
        this.initialization = initialization;
    }

    public String getInitializer() {
        return initializer;
    }

    public void setInitializer(String initializer) {
        this.initializer = initializer;
    }
}