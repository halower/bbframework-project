package com.quickstart.bbframework.common.initializer;

import cn.hutool.core.io.resource.ResourceUtil;
import org.apache.commons.io.FileUtils;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.SQLExec;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class MysqlDatabaseInitializerTest {
    @Test
    public void runSchemaScript() {
        SQLExec sqlExec = new SQLExec();
        String driverName = "com.mysql.cj.jdbc.Driver";
        String url = "jdbc:mysql://localhost:3306/demo?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai";
        String username ="root";
        String password ="123456";
        sqlExec.setDriver(driverName);
        sqlExec.setUrl(url);
        sqlExec.setUserid(username);
        sqlExec.setPassword(password);
        try {
            try( InputStream stream = ResourceUtil.getStream("sql/mysql-schema.sql")) {
                File targetFile = File.createTempFile("initializer-schema-mysql",".sql");
                FileUtils.copyInputStreamToFile(stream, targetFile);
                sqlExec.setSrc(targetFile);
                sqlExec.setPrint(true);
                sqlExec.setProject(new Project());
                sqlExec.execute();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void runDataScript() {
    }
}