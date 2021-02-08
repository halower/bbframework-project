package com.quickstart.bbframework.common.initializer;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;

/**
 * @Description:
 * @Date: 2021/2/3  9:53
 **/
@Log4j2
public class DefaultDatabaseInitializer implements ApplicationContextInitializer {
    @Override
    public void initialize(ConfigurableApplicationContext context) {
        boolean init = String.valueOf(context.getEnvironment().getProperty("application.database.initialization")).equals("true");
        String initializer_name = String.valueOf(context.getEnvironment().getProperty("application.database.initializer"));
        if (init) {
            try {
                log.info("正在初始化数据库");
                DatabaseInitializer initializer = DatabaseInitializerFactory.getInitializer(initializer_name);
                initializer.runSchemaScript(context);
                initializer.runDataScript(context );
            } catch (IOException e) {
                e.printStackTrace();
            }
            log.info("数据库初始化完成");
        }
    }
}

