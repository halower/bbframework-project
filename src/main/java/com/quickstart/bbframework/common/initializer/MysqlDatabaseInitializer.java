package com.quickstart.bbframework.common.initializer;

import cn.hutool.core.io.resource.ResourceUtil;
import org.apache.commons.io.FileUtils;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.SQLExec;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @Description:
 * @Date: 2021/2/3  9:46
 **/
@Service("mysql_initializer")
public class MysqlDatabaseInitializer implements DatabaseInitializer{
    @Override
    public void runSchemaScript(ConfigurableApplicationContext context) throws IOException {
        SQLExec sqlExec = buildSQLExec(context);
        try( InputStream stream = ResourceUtil.getStream("sql/mysql-schema.sql")) {
            File targetFile = File.createTempFile("initializer-schema-mysql",".sql");
            FileUtils.copyInputStreamToFile(stream, targetFile);
            sqlExec.setSrc(targetFile);
            sqlExec.setProject(new Project());
            sqlExec.execute();
        }
    }

    @Override
    public void runDataScript(ConfigurableApplicationContext context) throws IOException {
        SQLExec sqlExec = buildSQLExec(context);
        try( InputStream stream =ResourceUtil.getStream("sql/mysql-data.sql")) {
            File targetFile = File.createTempFile("initializer-data-mysql",".sql");
            FileUtils.copyInputStreamToFile(stream, targetFile);
            sqlExec.setSrc(targetFile);
            sqlExec.setProject(new Project());
            sqlExec.execute();
        }
    }

    private SQLExec buildSQLExec(ConfigurableApplicationContext context) {
        SQLExec sqlExec = new SQLExec();
        String driverName = context.getEnvironment().getProperty("spring.datasource.driver-class-name");
        String url = context.getEnvironment().getProperty("spring.datasource.url");
        String username = context.getEnvironment().getProperty("spring.datasource.username");
        String password = context.getEnvironment().getProperty("spring.datasource.password");
        sqlExec.setDriver(driverName);
        sqlExec.setUrl(url);
        sqlExec.setUserid(username);
        sqlExec.setPassword(password);
        return sqlExec;
    }
}
