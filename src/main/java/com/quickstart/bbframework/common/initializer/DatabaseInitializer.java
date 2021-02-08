package com.quickstart.bbframework.common.initializer;

import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;

/**
 * @Description:
 * @Date: 2021/2/2  17:32
 **/
public interface DatabaseInitializer {
    void runSchemaScript(ConfigurableApplicationContext context) throws IOException;
    void runDataScript(ConfigurableApplicationContext context) throws IOException;
}
