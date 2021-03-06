package com.xiafei.tools.generatesource;

import com.xiafei.tools.common.Db;
import com.xiafei.tools.file.FileUtils;
import com.xiafei.tools.common.StringUtils;
import com.xiafei.tools.generatesource.enums.DataBaseTypeEnum;
import com.xiafei.tools.generatesource.template.DaoTemplate;
import com.xiafei.tools.generatesource.template.DomainTemplate;
import com.xiafei.tools.generatesource.template.MapperTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * <P>Description: 资源文件生成器. </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   齐霞飞 </P>
 * <P>CREATE DATE: 2017年7月13日</P>
 * <P>UPDATE DATE: 2017年7月13日</P>
 *
 * @author qixiafei
 * @version 1.0
 * @since java 1.7.0
 */
public final class SourceGenerator {

    private static String DB_USER = "root";
    private static String DB_USER_PWD = "root";

    /**
     * log4j.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SourceGenerator.class);

    /**
     * mysql search table structrue sql.
     */
    private static final String MYSQL_SQL_TEMPLATE = "SELECT t.COLUMN_NAME,upper(t.DATA_TYPE),t.COLUMN_COMMENT,upper(t.COLUMN_KEY) FROM information_schema.COLUMNS t WHERE t.TABLE_NAME = ''{0}'' AND t.TABLE_SCHEMA = ''{1}'' ORDER BY t.ORDINAL_POSITION";

    /**
     * 连接字符串模板.
     */
    private static final String CON_STR_TEMPLATE = "jdbc:mysql://127.0.0.1:3306/{0}?useUnicode=true&characterEncoding=UTF-8&allowMultiQueries=true&rewriteBatchedStatements=true&useSSL=true";
    private static final String CON_STR_LOCAL_TEMPLATE = "jdbc:mysql://127.0.0.1:3306/{0}?useUnicode=true&characterEncoding=UTF-8&allowMultiQueries=true&rewriteBatchedStatements=true&useSSL=false";


    /**
     * 工具类不允许实例化.
     */
    private SourceGenerator() {

    }

    /**
     * 示例程序.
     *
     * @param args system boot argument
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("生成资源文件....请输入N/n之外的任意字符继续，输入N/n将退出");
        final String s = scanner.nextLine();
        if ("n".equalsIgnoreCase(s)) {
            System.out.println("已经安全退出....");
            return;
        }

    }

    private static void generateKryRisk() {
        final GenerateSourceParam param = new GenerateSourceParam();
        param.setCommentsUser("齐霞飞");
        param.setCommentsSince("JDK 1.8.0");
        param.setCommentsVersion("1.0.0");
        param.setDomainDirectory("C:\\directory\\src\\main\\java\\com\\virgo\\finance\\sample\\dao\\domains");
        param.setDomainPackage("com.virgo.finance.sample.dao.domains");
        param.setDomainSuffix("Po");
        param.setMapperDirectory("C:\\directory\\src\\main\\resources\\mapper\\risk");
        param.setDaoDirectory("C:\\directory\\src\\main\\java\\com\\virgo\\finance\\kry\\risk\\dao");
        param.setDaoPackage("com.virgo.finance.sample.dao");
        // is cover ori file's content
        param.setCoverFile(true);
        param.setJavaTime(false);
        param.setLombok(true);
        final String schema = "test_schema";
        final String jdbcUrl = MessageFormat.format(CON_STR_TEMPLATE, schema);
        String dbUser = DB_USER;
        String pwd = DB_USER_PWD;

        List<GenerateSourceParamItem> itemList = new ArrayList<>();

        GenerateSourceParamItem table1 = new GenerateSourceParamItem(DataBaseTypeEnum.MYSQL, jdbcUrl, dbUser, pwd,
                "table_1", schema,
                "Table1", "表1");
        itemList.add(table1);

        param.setItems(itemList);
        exec(param);


    }


    /**
     * 执行资源文件生成.
     *
     * @param param 生成参数
     */
    private static void exec(final GenerateSourceParam param) {
        // 校验参数合法，必要校验都放在这里
        if (!validParam(param)) {
            return;
        }

        // 循环要生成资源的项目
        for (GenerateSourceParamItem item : param.getItems()) {
            // 从数据库解析出的字段信息列表
            final List<ColumnInfo> columnInfoList = new ArrayList<>(16);
            // 访问数据库，查询表结构
            if (DataBaseTypeEnum.MYSQL == item.getDataBaseType()) {

                try {
                    // 建立数据库连接
                    final Connection conn = Db.getMysqlConn(item.getUrl(), item.getUser(), item.getPassword());
                    // 执行sql
                    String sql = MessageFormat.format(MYSQL_SQL_TEMPLATE, item.getTableName(), item.getTableSchema());
                    LOGGER.info("查询表结构，sql：{}", sql);
                    final PreparedStatement preparedStatement = conn.prepareStatement(sql);
                    // 解析结果集拼装数据库字段对象
                    final ResultSet resultSet = preparedStatement.executeQuery();
                    if (resultSet == null) {
                        throw new RuntimeException("找不到数据库表信息");
                    }
                    while (resultSet.next()) {
                        final ColumnInfo columnInfo = new ColumnInfo();
                        columnInfo.setName(resultSet.getString(1));
                        columnInfo.setType(resultSet.getString(2));
                        columnInfo.setComment(resultSet.getString(3));
                        columnInfo.setKey(resultSet.getString(4));
                        columnInfoList.add(columnInfo);
                    }
                } catch (SQLException e) {
                    LOGGER.error("查询mysql数据库表结构出错", e);
                    return;
                }
            } else if (DataBaseTypeEnum.ORACLE == item.getDataBaseType()) {
                LOGGER.error("暂时不支持oracle数据库的资源自动生成");
                return;
            }

            if (columnInfoList.isEmpty()) {
                LOGGER.error("没有找到数据库表结构");
                return;
            }

            // 生成domain文件
            if (StringUtils.isNotBlank(param.getDomainDirectory())) {
                generateDomain(param, item, columnInfoList);
            }

            // 生成mapper文件
            if (StringUtils.isNotBlank(param.getMapperDirectory())) {
                generateMapper(param, item, columnInfoList);
            }

            // 生成dao文件
            if (StringUtils.isNotBlank(param.getDaoDirectory())) {
                generateDao(param, item, columnInfoList);
            }

        }

    }

    /**
     * 参数正确性做全方位校验.
     *
     * @param param 参数
     * @return true-通过,false-不通过
     */
    private static boolean validParam(final GenerateSourceParam param) {
        if (param.getItems() == null || param.getItems().isEmpty()) {
            LOGGER.error("参数中明细列表items为空，退出方法");
            return false;
        }

        if (StringUtils.isBlank(param.getDomainDirectory()) && StringUtils.isBlank(param.getDaoDirectory()) && StringUtils.isBlank(param.getMapperDirectory())) {
            LOGGER.error("参数中domian路径、dao路径、mapper路径都为空，退出方法");
            return false;
        }
        for (GenerateSourceParamItem item : param.getItems()) {
            if (item.getDataBaseType() == null) {
                LOGGER.error("必须传递数据库类字段，所传递参数：{}", param);
                return false;
            }
        }
        return true;
    }

    /**
     * 生成domian文件.
     *
     * @param param          生成参数
     * @param item           参数项
     * @param columnInfoList 数据库字段信息列表
     */
    private static void generateDomain(final GenerateSourceParam param, final GenerateSourceParamItem item, final List<ColumnInfo> columnInfoList) {
        // 输出文件内容
        final List<String> fileContent = new ArrayList<>(500);
        // 填充文件内容
        DomainTemplate.addContent(param, item, columnInfoList, fileContent);
        // 输出到文件
        String filePath = param.getDomainDirectory().replace("\\", "/");
        if (!filePath.endsWith("/")) {
            filePath += "/";
        }
        filePath += item.getClassName() + (param.getDomainSuffix() == null ? "" : param.getDomainSuffix()) + ".java";
        FileUtils.outPutToFileByLine(filePath, fileContent, param.isCoverFile());
        LOGGER.info("成功生成domain文件：{}", filePath);
    }


    /**
     * 生成mapper文件.
     *
     * @param param          生成参数
     * @param item
     * @param columnInfoList 数据库字段信息列表
     */
    private static void generateMapper(final GenerateSourceParam param, final GenerateSourceParamItem item, final List<ColumnInfo> columnInfoList) {
        // 输出文件内容
        final List<String> fileContent = new ArrayList<>(500);
        // 从模板加载文件内容
        MapperTemplate.addContent(param, item, columnInfoList, fileContent);
        // 输出到文件
        String filePath = param.getMapperDirectory().replace("\\", "/");
        ;
        if (!filePath.endsWith("/")) {
            filePath += "/";
        }
        filePath += item.getClassName() + "Mapper.xml";
        FileUtils.outPutToFileByLine(filePath, fileContent, param.isCoverFile());
        LOGGER.info("成功生成mapper文件：{}", filePath);
    }

    /**
     * 生成dao文件.
     *
     * @param param          生成参数
     * @param item
     * @param columnInfoList 数据库字段信息列表
     */
    private static void generateDao(final GenerateSourceParam param, final GenerateSourceParamItem item, final List<ColumnInfo> columnInfoList) {
        // 输出文件内容
        final List<String> fileContent = new ArrayList<>(500);
        // 从模板加载文件内容
        DaoTemplate.addContent(param, item, columnInfoList, fileContent);
        // 输出到文件
        String filePath = param.getDaoDirectory().replace("\\", "/");
        ;
        if (!filePath.endsWith("/")) {
            filePath += "/";
        }
        filePath += item.getClassName() + "Dao.java";
        FileUtils.outPutToFileByLine(filePath, fileContent, param.isCoverFile());
        LOGGER.info("成功生成dao文件：{}", filePath);
    }

}
