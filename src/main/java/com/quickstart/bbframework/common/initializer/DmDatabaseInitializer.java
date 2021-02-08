package com.quickstart.bbframework.common.initializer;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;

/**
 * @Description:
 * @Date: 2021/2/3  9:46
 **/
@Service("dm_initializer")
public class DmDatabaseInitializer implements DatabaseInitializer{

    @Override
    public void runSchemaScript(ConfigurableApplicationContext context) {

    }

    @Override
    public void runDataScript(ConfigurableApplicationContext context) {

    }
}
