package com.quickstart.bbframework.config;

import com.quickstart.bbframework.common.config.BBFrameworkProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;


@ComponentScan("com.quickstart.bbframework")
@EnableConfigurationProperties({BBFrameworkProperties.class})
@PropertySource("classpath:META-INF/bbframework.properties")
public class BBFrameworkAutoConfiguration {
}
