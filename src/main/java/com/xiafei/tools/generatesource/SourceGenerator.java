package com.xiafei.tools.generatesource;

import com.xiafei.tools.common.Db;
import com.xiafei.tools.common.FileUtils;
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

        // 生成客如云风险平台的资源文件
//        generateKryRisk();
        // 生成租赁核心的资源文件
//        generateLease();
        // 双子座支撑平台资源文件生成
//        gemini();
        // 聚合支付交易模块
//        trade();
//        securityCenter();
//        opsLogsEtl();
    }

    private static void opsLogsEtl() {
        final GenerateSourceParam param = new GenerateSourceParam();
        param.setCommentsUser("齐霞飞");
        param.setCommentsSince("JDK 1.8.0");
        param.setCommentsVersion("1.0.0");
        param.setDomainDirectory("D:\\self-study\\codes\\self\\ops-logs-etl\\src\\main\\java\\com\\qixiafei\\ops\\logs\\etl\\pos");
        param.setDomainPackage("com.qixiafei.ops.logs.etl.pos");
        param.setDomainSuffix("Po");
        param.setMapperDirectory("D:\\self-study\\codes\\self\\ops-logs-etl\\src\\main\\resources\\mapper\\ops-logs-etl");
        param.setDaoDirectory("D:\\self-study\\codes\\self\\ops-logs-etl\\src\\main\\java\\com\\qixiafei\\ops\\logs\\etl\\dao");
        param.setDaoPackage("com.qixiafei.ops.logs.etl.dao");
        // is cover ori file's content
        param.setCoverFile(true);
        param.setJavaTime(false);
        param.setLombok(true);
        final String schema = "ops_logs_etl";
        final String jdbcUrl = MessageFormat.format(CON_STR_LOCAL_TEMPLATE, schema);
        final String dbUser = DB_USER;
        final String pwd = DB_USER_PWD;

        final List<GenerateSourceParamItem> itemList = new ArrayList<>();

//        GenerateSourceParamItem table1 = new GenerateSourceParamItem(DataBaseTypeEnum.MYSQL, jdbcUrl, dbUser, pwd,
//                "task_type_def", schema,
//                "TaskTypeDef", "任务类型定义表");
//        itemList.add(table1);
        GenerateSourceParamItem table2 = new GenerateSourceParamItem(DataBaseTypeEnum.MYSQL, jdbcUrl, dbUser, pwd,
                "task_exec_record", schema,
                "TaskExecRecord", "任务执行记录表");
        itemList.add(table2);
//        GenerateSourceParamItem table3 = new GenerateSourceParamItem(DataBaseTypeEnum.MYSQL, jdbcUrl, dbUser, pwd,
//                "log_data_template", schema,
//                "LogData", "日志数据");
//        itemList.add(table3);
//
//        GenerateSourceParamItem table4 = new GenerateSourceParamItem(DataBaseTypeEnum.MYSQL, jdbcUrl, dbUser, pwd,
//                "log_data_group", schema,
//                "LogDataGroup", "日志数据分组表");
//        itemList.add(table4);

        param.setItems(itemList);
        exec(param);
    }

    private static void securityCenter() {
        final GenerateSourceParam param = new GenerateSourceParam();
        param.setCommentsUser("齐霞飞");
        param.setCommentsSince("JDK 1.8.0");
        param.setCommentsVersion("1.0.0");
        param.setDomainDirectory("C:\\code\\self\\crypto-center\\crypto-center-core\\src\\main\\java\\com\\qixiafei\\security\\center\\core\\domain");
        param.setDomainPackage("com.qixiafei.security.center.core.domain");
        param.setDomainSuffix("Po");
        param.setMapperDirectory("C:\\code\\self\\crypto-center\\crypto-center-core\\src\\main\\resources\\mapper\\crypto-center");
        param.setDaoDirectory("C:\\code\\self\\crypto-center\\crypto-center-core\\src\\main\\java\\com\\qixiafei\\security\\center\\core\\dao");
        param.setDaoPackage("com.qixiafei.security.center.core.dao");
        // is cover ori file's content
        param.setCoverFile(true);
        param.setJavaTime(false);
        param.setLombok(true);
        final String schema = "security_center";
        final String jdbcUrl = MessageFormat.format(CON_STR_TEMPLATE, schema);
        final String dbUser = DB_USER;
        final String pwd = DB_USER_PWD;

        final List<GenerateSourceParamItem> itemList = new ArrayList<>();

        GenerateSourceParamItem table1 = new GenerateSourceParamItem(DataBaseTypeEnum.MYSQL, jdbcUrl, dbUser, pwd,
                "metadata", schema,
                "Metadata", "元数据");
        itemList.add(table1);

        param.setItems(itemList);
        exec(param);
    }

    private static void trade() {
        final GenerateSourceParam param = new GenerateSourceParam();
        param.setCommentsUser("齐霞飞");
        param.setCommentsSince("JDK 1.8.0");
        param.setCommentsVersion("1.0.0");
        param.setDomainDirectory("C:\\code\\work\\yx\\pay-paas-trade\\pay-paas-trade-domain\\src\\main\\java\\com\\virgo\\finance\\pay\\paas\\trade\\domain");
        param.setDomainPackage("com.virgo.finance.pay.paas.trade.domain");
        param.setDomainSuffix("Po");
        param.setMapperDirectory("C:\\code\\work\\yx\\pay-paas-trade\\pay-paas-trade-web\\src\\main\\resources\\mapper\\pay-paas-trade");
        param.setDaoDirectory("C:\\code\\work\\yx\\pay-paas-trade\\pay-paas-trade-dao\\src\\main\\java\\com\\virgo\\finance\\pay\\paas\\trade\\dao");
        param.setDaoPackage("com.virgo.finance.pay.paas.trade.dao");
        // is cover ori file's content
        param.setCoverFile(true);
        param.setJavaTime(false);
        param.setLombok(true);
        final String schema = "pay_paas_trade";
        final String jdbcUrl = MessageFormat.format(CON_STR_TEMPLATE, schema);
        final String dbUser = DB_USER;
        final String pwd = DB_USER_PWD;

        final List<GenerateSourceParamItem> itemList = new ArrayList<>();

        GenerateSourceParamItem table1 = new GenerateSourceParamItem(DataBaseTypeEnum.MYSQL, jdbcUrl, dbUser, pwd,
                "trade_sub_order", schema,
                "TradeSubOrder", "交易子订单表");
        itemList.add(table1);
        GenerateSourceParamItem table2 = new GenerateSourceParamItem(DataBaseTypeEnum.MYSQL, jdbcUrl, dbUser, pwd,
                "trade_sub_order_goods", schema,
                "TradeSubOrderGoods", "交易子订单商品表");
        itemList.add(table2);

        param.setItems(itemList);
        exec(param);
    }


    private static void gemini() {
        final GenerateSourceParam param = new GenerateSourceParam();
        param.setCommentsUser("齐霞飞");
        param.setCommentsSince("JDK 1.8.0");
        param.setCommentsVersion("1.0.0");
        param.setDomainDirectory("C:\\code\\work\\yx\\gemini-support\\gemini-support-dao\\src\\main\\java\\com\\virgo\\finance\\gemini\\support\\dao\\domain");
        param.setDomainPackage("com.virgo.finance.gemini.support.dao.domain");
        param.setDomainSuffix("Po");
        param.setMapperDirectory("C:\\code\\work\\yx\\gemini-support\\gemini-support-web\\src\\main\\resources\\mapper\\gemini");
        param.setDaoDirectory("C:\\code\\work\\yx\\gemini-support\\gemini-support-dao\\src\\main\\java\\com\\virgo\\finance\\gemini\\support\\dao");
        param.setDaoPackage("com.virgo.finance.gemini.support.dao");
        // is cover ori file's content
        param.setCoverFile(true);
        param.setJavaTime(false);
        param.setLombok(true);
        final String schema = "gemini";
        final String jdbcUrl = MessageFormat.format(CON_STR_TEMPLATE, schema);
        final String dbUser = DB_USER;
        final String pwd = DB_USER_PWD;

        final List<GenerateSourceParamItem> itemList = new ArrayList<>();

//        GenerateSourceParamItem table1 = new GenerateSourceParamItem(DataBaseTypeEnum.MYSQL, jdbcUrl, dbUser, pwd,
//                "report_merchant", schema,
//                "ReportMerchant", "商户统计报告表");
//        itemList.add(table1);
//        GenerateSourceParamItem table2 = new GenerateSourceParamItem(DataBaseTypeEnum.MYSQL, jdbcUrl, dbUser, pwd,
//                "red_envelope", schema,
//                "RedEnvelope", "红包表");
//        itemList.add(table2);
//        GenerateSourceParamItem table3 = new GenerateSourceParamItem(DataBaseTypeEnum.MYSQL, jdbcUrl, dbUser, pwd,
//                "red_envelope_batch", schema,
//                "RedEnvelopeBatch", "红包批次表");
//        itemList.add(table3);
        GenerateSourceParamItem table4 = new GenerateSourceParamItem(DataBaseTypeEnum.MYSQL, jdbcUrl, dbUser, pwd,
                "lottery_define", schema,
                "LotteryDefine", "抽奖定义表");
        itemList.add(table4);
//        GenerateSourceParamItem table5 = new GenerateSourceParamItem(DataBaseTypeEnum.MYSQL, jdbcUrl, dbUser, pwd,
//                "lottery_prize", schema,
//                "LotteryPrize", "抽奖奖品表");
//        itemList.add(table5);
//        GenerateSourceParamItem table6 = new GenerateSourceParamItem(DataBaseTypeEnum.MYSQL, jdbcUrl, dbUser, pwd,
//                "lottery_prize_pool", schema,
//                "LotteryPrizePool", "抽奖奖池表");
//        itemList.add(table6);

        param.setItems(itemList);
        exec(param);
    }

    private static void generateLease() {
        final GenerateSourceParam param = new GenerateSourceParam();
        param.setCommentsUser("齐霞飞");
        param.setCommentsSince("JDK 1.8.0");
        param.setCommentsVersion("1.0.0");
        param.setDomainDirectory("C:\\code\\local\\yx\\lease-core\\lease-core-domain\\src\\main\\java\\com\\virgo\\finance\\lease\\core\\domain\\po");
        param.setDomainPackage("com.virgo.finance.lease.core.domain.po");
        param.setDomainSuffix("Po");
        param.setMapperDirectory("C:\\code\\local\\yx\\lease-core\\lease-core-web\\src\\main\\resources\\lease\\mappings");
        param.setDaoDirectory("C:\\code\\local\\yx\\lease-core\\lease-core-dao\\src\\main\\java\\com\\virgo\\finance\\lease\\core\\dao");
        param.setDaoPackage("com.virgo.finance.lease.core.dao");
        // is cover ori file's content
        param.setCoverFile(true);
        param.setJavaTime(false);
        param.setLombok(true);
        final String schema = "lease";
        final String jdbcUrl = MessageFormat.format(CON_STR_TEMPLATE, schema);
        final String dbUser = "root";
        final String pwd = "root";

        final List<GenerateSourceParamItem> itemList = new ArrayList<>();

//        GenerateSourceParamItem table1 = new GenerateSourceParamItem(DataBaseTypeEnum.MYSQL, jdbcUrl, dbUser, pwd,
//                "APPLY_INVOICE", schema,
//                "ApplyInvoice", "申请单发票信息");
//        itemList.add(table1);
        GenerateSourceParamItem table2 = new GenerateSourceParamItem(DataBaseTypeEnum.MYSQL, jdbcUrl, dbUser, pwd,
                "OVERDUE_RECORD", schema,
                "OverdueRecord", "逾期记录表");
        itemList.add(table2);

        param.setItems(itemList);
        exec(param);


    }

    private static void generateKryRisk() {
        final GenerateSourceParam param = new GenerateSourceParam();
        param.setCommentsUser("齐霞飞");
        param.setCommentsSince("JDK 1.8.0");
        param.setCommentsVersion("1.0.0");
        param.setDomainDirectory("C:\\code\\local\\yx\\kry-risk\\kry-risk-dao\\src\\main\\java\\com\\virgo\\finance\\kry\\risk\\dao\\domains");
        param.setDomainPackage("com.virgo.finance.kry.risk.dao.domains");
        param.setDomainSuffix("Po");
        param.setMapperDirectory("C:\\code\\local\\yx\\kry-risk\\kry-risk-api\\src\\main\\resources\\mapper\\risk");
        param.setDaoDirectory("C:\\code\\local\\yx\\kry-risk\\kry-risk-dao\\src\\main\\java\\com\\virgo\\finance\\kry\\risk\\dao");
        param.setDaoPackage("com.virgo.finance.kry.risk.dao");
        // is cover ori file's content
        param.setCoverFile(true);
        param.setJavaTime(false);
        param.setLombok(true);
        final String schema = "kry_risk";
        final String jdbcUrl = MessageFormat.format(CON_STR_TEMPLATE, schema);
        String dbUser = DB_USER;
        String pwd = DB_USER_PWD;

        List<GenerateSourceParamItem> itemList = new ArrayList<>();

//        GenerateSourceParamItem table1 = new GenerateSourceParamItem(DataBaseTypeEnum.MYSQL, jdbcUrl, dbUser, pwd,
//                "SEND_KRY_STATUS", schema,
//                "SendKryStatus", "发送客如云请求状态表");
//        itemList.add(table1);
//
//        GenerateSourceParamItem table2 = new GenerateSourceParamItem(DataBaseTypeEnum.MYSQL, jdbcUrl, dbUser, pwd,
//                "order_day", schema,
//                "OrderDay", "有效订单日统计表");
//        itemList.add(table2);
//
//        GenerateSourceParamItem table3 = new GenerateSourceParamItem(DataBaseTypeEnum.MYSQL, jdbcUrl, dbUser, pwd,
//                "device_visit_day", schema,
//                "DeviceVisitDay", "客户端访问信息表");
//        itemList.add(table3);
//
//        GenerateSourceParamItem table4 = new GenerateSourceParamItem(DataBaseTypeEnum.MYSQL, jdbcUrl, dbUser, pwd,
//                "device_log_day", schema,
//                "DeviceLogDay", "设备登录信息表-日统计");
//        itemList.add(table4);
//
//        GenerateSourceParamItem table5 = new GenerateSourceParamItem(DataBaseTypeEnum.MYSQL, jdbcUrl, dbUser, pwd,
//                "device_fault_day", schema,
//                "DeviceFaultDay", "设备故障信息日统计表");
//        itemList.add(table5);
//
//        GenerateSourceParamItem table6 = new GenerateSourceParamItem(DataBaseTypeEnum.MYSQL, jdbcUrl, dbUser, pwd,
//                "compitation_rank_day", schema,
//                "CompitationRankDay", "商铺竞争力信息日统计表");
//        itemList.add(table6);
//
//        GenerateSourceParamItem table7 = new GenerateSourceParamItem(DataBaseTypeEnum.MYSQL,jdbcUrl,dbUser, pwd,
//                "refound_order_day", schema,
//            "RefoundOrderDay", "退款订单日统计表");
//        itemList.add(table7);

//        GenerateSourceParamItem table8 = new GenerateSourceParamItem(DataBaseTypeEnum.MYSQL,jdbcUrl,dbUser, pwd,
//                "member_day", schema,
//            "MemberDay", "客户信息日统计表");
//        itemList.add(table8);
//        GenerateSourceParamItem table9 = new GenerateSourceParamItem(DataBaseTypeEnum.MYSQL,jdbcUrl,dbUser, pwd,
//                "CALL_BACK_IDEMPOTENT", schema,
//            "CallBackIdempotent", "回调幂等表");
//        itemList.add(table9);
//        GenerateSourceParamItem table10 = new GenerateSourceParamItem(DataBaseTypeEnum.MYSQL,jdbcUrl,dbUser, pwd,
//                "EFFECTIVE_COMMERCIAL", schema,
//            "EffectiveCommercial", "有效商户号表");
//        itemList.add(table10);
//        GenerateSourceParamItem table11 = new GenerateSourceParamItem(DataBaseTypeEnum.MYSQL,jdbcUrl,dbUser, pwd,
//                "risk_indicator_all", schema,
//            "RiskIndicatorAll", "风控指标主表");
//        itemList.add(table11);
//        GenerateSourceParamItem table12 = new GenerateSourceParamItem(DataBaseTypeEnum.MYSQL,jdbcUrl,dbUser, pwd,
//                "risk_indicator_WTW", schema,
//            "RiskIndicatorWtw", "风控指标环比表");
//        itemList.add(table12););
        GenerateSourceParamItem table13 = new GenerateSourceParamItem(DataBaseTypeEnum.MYSQL, jdbcUrl, dbUser, pwd,
                "EFFECTIVE_COMMERCIAL_ORDERS", schema,
                "EffectiveCommercialOrders", "有效商户号关联的申请单表");
        itemList.add(table13);
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
