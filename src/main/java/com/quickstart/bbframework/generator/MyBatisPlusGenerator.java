package com.quickstart.bbframework.generator;

import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.dialect.Props;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.LikeTable;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;
import org.apache.commons.lang3.StringUtils;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * MyBatisPlus代码生成器
 */
public class MyBatisPlusGenerator {

    public static void main(String[] args) {
        String projectPath = System.getProperty("user.dir");
        String moduleName = scanner("模块名");
        String[] tableNames = scanner("表名，多个英文逗号分割").split(",");
        // 代码生成器
        AutoGenerator autoGenerator = new AutoGenerator();
        autoGenerator.setGlobalConfig(initGlobalConfig(projectPath));
        autoGenerator.setDataSource(initDataSourceConfig());
        autoGenerator.setPackageInfo(initPackageConfig(moduleName));
        autoGenerator.setCfg(initInjectionConfig(projectPath, moduleName));
        autoGenerator.setTemplate(initTemplateConfig());
        autoGenerator.setStrategy(initStrategyConfig(tableNames));
        autoGenerator.setTemplateEngine(new VelocityTemplateEngine());
        autoGenerator.execute();
        String modulePath = autoGenerator.getGlobalConfig().getOutputDir() + "/" + autoGenerator.getPackageInfo().getParent().replace(".", "/");
        Props props = new Props("generator.properties");
        List<String> dtoTypes = Arrays.stream(props.getStr("dto.types").split(",")).collect(Collectors.toList());
        dtoTypes.forEach(d-> generateDTO(modulePath, d, tableNames));
    }

    /**
     * 读取控制台内容信息
     */
    private static String scanner(String tip) {
        Scanner scanner = new Scanner(System.in);
        System.out.println(("请输入" + tip + "："));
        if (scanner.hasNext()) {
            String next = scanner.next();
            if (StrUtil.isNotEmpty(next)) {
                return next;
            }
        }
        throw new MybatisPlusException("请输入正确的" + tip + "！");
    }

    /**
     * 初始化全局配置
     */
    private static GlobalConfig initGlobalConfig(String projectPath) {
        GlobalConfig globalConfig = new GlobalConfig();
        Props props = new Props("generator.properties");
        globalConfig.setOutputDir(projectPath + "/src/main/java");
        globalConfig.setOpen(false);
        globalConfig.setAuthor(props.getStr("author"));
        globalConfig.setSwagger2(true);
        globalConfig.setBaseResultMap(true);
        globalConfig.setFileOverride(true);
        globalConfig.setDateType(DateType.ONLY_DATE);
        globalConfig.setEntityName("%s");
        globalConfig.setMapperName("%sMapper");
        globalConfig.setXmlName("%sMapper");
        globalConfig.setServiceName("%sService");
        globalConfig.setServiceImplName("%sServiceImpl");
        globalConfig.setControllerName("%sController");
        return globalConfig;
    }

    /**
     * 初始化数据源配置
     */
    private static DataSourceConfig initDataSourceConfig() {
        Props props = new Props("generator.properties");
        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setUrl(props.getStr("dataSource.url"));
        dataSourceConfig.setDriverName(props.getStr("dataSource.driverName"));
        dataSourceConfig.setUsername(props.getStr("dataSource.username"));
        dataSourceConfig.setPassword(props.getStr("dataSource.password"));
        return dataSourceConfig;
    }

    /**
     * 初始化包配置
     */
    private static PackageConfig initPackageConfig(String moduleName) {
        Props props = new Props("generator.properties");
        PackageConfig packageConfig = new PackageConfig();
        packageConfig.setModuleName(moduleName);
        packageConfig.setParent(props.getStr("package.base"));
        packageConfig.setEntity("model");
        return packageConfig;
    }

    /**
     * 初始化模板配置
     */
    private static TemplateConfig initTemplateConfig() {
        TemplateConfig templateConfig = new TemplateConfig();
        //可以对controller、service、entity模板进行配置
        //mapper.xml模板需单独配置
        templateConfig.setXml(null);
        return templateConfig;
    }

    /**
     * 初始化策略配置
     */
    private static StrategyConfig initStrategyConfig(String[] tableNames) {
        StrategyConfig strategyConfig = new StrategyConfig();
        strategyConfig.setNaming(NamingStrategy.underline_to_camel);
        strategyConfig.setColumnNaming(NamingStrategy.underline_to_camel);
        strategyConfig.setEntityLombokModel(true);
        strategyConfig.setRestControllerStyle(true);
        //当表名中带*号时可以启用通配符模式
        if (tableNames.length == 1 && tableNames[0].contains("*")) {
            String[] likeStr = tableNames[0].split("_");
            String likePrefix = likeStr[0] + "_";
            strategyConfig.setLikeTable(new LikeTable(likePrefix));
        } else {
            strategyConfig.setInclude(tableNames);
        }
        return strategyConfig;
    }

    /**
     * 初始化自定义配置
     */
    private static InjectionConfig initInjectionConfig(String projectPath, String moduleName) {
        // 自定义配置
        InjectionConfig injectionConfig = new InjectionConfig() {
            @Override
            public void initMap() {
                // 可用于自定义属性
            }
        };
        // 模板引擎是Velocity
        String templatePath = "/templates/mapper.xml.vm";
        // 自定义输出配置
        List<FileOutConfig> focList = new ArrayList<>();
        // 自定义配置会被优先输出
        focList.add(new FileOutConfig(templatePath) {
            @Override
            public String outputFile(TableInfo tableInfo) {
                // 自定义输出文件名 ， 如果你 Entity 设置了前后缀、此处注意 xml 的名称会跟着发生变化！！
                return projectPath + "/src/main/resources/mapper/" + moduleName
                        + "/" + tableInfo.getEntityName() + "Mapper" + StringPool.DOT_XML;
            }
        });
        injectionConfig.setFileOutConfigList(focList);
        return injectionConfig;
    }


    /**
     * 根据表名生成DTO对象,用于mybatis-plus代码生成以后再生成
     *
     * @param tableNames
     */
    private static void generateDTO(String modulePath,String dtoType, String... tableNames) {
        for (int i = 0; i < tableNames.length; i++) {
            String tableName = tableNames[i];
            String subPackage = tableName.toLowerCase();
            String outPutDir =modulePath + "/dto/"+ subPackage +"/";
            String baseFileName = underline2Camel(tableName, false);
            try {
                File outFile = new File(outPutDir);
                if (!outFile.exists()) {
                    outFile.mkdirs();
                }

                File voFile = new File(outFile,  dtoType.split("_")[0] + baseFileName +  dtoType.split("_")[1] + ".java");
                if (!voFile.exists()) {
                    voFile.createNewFile();
                }
                BufferedReader reader = new BufferedReader(new FileReader(modulePath  + "/model/" + baseFileName + ".java"));
                FileWriter fw = new FileWriter(voFile);
                String line;
                while ((line = reader.readLine()) != null) {
                    line = convert2Dto(line,subPackage, dtoType);
                    if(StringUtils.isNotEmpty(line)) line += "\r\n";
                    fw.write(line);
                }
                fw.close();
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static String convert2Dto(String line,String subPackage, String dtoType) {
        // 替换包名
        if(line.contains("package")) {
            line = line.replaceAll("model", "dto."+subPackage);
        }
        // 替换类名
        String regex = "public class (.*) implements Serializable";
        Pattern pattern = Pattern.compile(regex);
        Matcher m = pattern.matcher(line);
        if(m.find()) {
             line = line.replaceAll(m.group(1), dtoType.split("_")[0] + m.group(1) + dtoType.split("_")[1]);
        }
        // 移除Mybaits相关的注解和包引用
        if(line.contains("TableName")) line = "";
        return  line;
    }

    private static String underline2Camel(String line, boolean... smallCamel) {
        if (StringUtils.isBlank(line)) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        Pattern pattern = Pattern.compile("([A-Za-z\\d]+)(_)?");
        Matcher matcher = pattern.matcher(line);
        // 匹配正则表达式
        while (matcher.find()) {
            String word = matcher.group();
            // 当是true 或则是空的情况
            if ((smallCamel.length == 0 || smallCamel[0]) && matcher.start() == 0) {
                sb.append(Character.toLowerCase(word.charAt(0)));
            } else {
                sb.append(Character.toUpperCase(word.charAt(0)));
            }

            int index = word.lastIndexOf('_');
            if (index > 0) {
                sb.append(word.substring(1, index).toLowerCase());
            } else {
                sb.append(word.substring(1).toLowerCase());
            }
        }
        return sb.toString();
    }
}