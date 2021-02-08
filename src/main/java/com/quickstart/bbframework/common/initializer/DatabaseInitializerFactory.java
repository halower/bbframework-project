package com.quickstart.bbframework.common.initializer;

/**
 * @Description:
 * @Date: 2021/2/3  14:08
 **/
public class DatabaseInitializerFactory
{
    public static DatabaseInitializer getInitializer(String initializer_name) {
        if(initializer_name.equalsIgnoreCase("dm_initializer")){
            return new DmDatabaseInitializer();
        }
        return new MysqlDatabaseInitializer();
    }
}
