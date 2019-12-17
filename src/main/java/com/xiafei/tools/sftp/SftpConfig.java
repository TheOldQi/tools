package com.xiafei.tools.sftp;

import com.jcraft.jsch.JSchException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * <P>Description: 初始化Sftp的bean. </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   齐霞飞 </P>
 * <P>CREATE DATE: 2017/12/26</P>
 * <P>UPDATE DATE: 2017/12/26</P>
 *
 * @author qixiafei
 * @version 1.0
 * @since java 1.8.0
 */
@Configuration
public class SftpConfig {

    /**
     * 平台自己的sftp的Bean名字.
     */
    private static final String PT_SFTP = "ptSftp";

    /**
     * 江西金租sftp.
     */
    private static final String JX_SFTP = "jxSftp";

//    /**
//     * 客如云sftp.
//     */
//    private static final String KRY_SFTP = "krySftp";

    private static final Map<String, Sftp> BEAN_MAP = new HashMap<>();
    @Resource
    private SftpProperties sftpProperties;

    /**
     * 根据合作方编码获取sftp操作对象.
     *
     * @param coopCodeEnum 合作方编码，传空代表本地sftp
     * @return sftp操作对象
     */
    public static Sftp getSftp(final CoopEnum coopCodeEnum) {
        if (coopCodeEnum == null) {
            return BEAN_MAP.get(PT_SFTP);
        }
        switch (coopCodeEnum) {
            case JIANGXI:
                return BEAN_MAP.get(JX_SFTP);
//            case KERY:
//                return BEAN_MAP.get(KRY_SFTP);
            default:
                throw new IllegalArgumentException("合作方编码无法识别");

        }

    }

    /**
     * 平台sftp.
     */
    @Bean(value = PT_SFTP)
    public Sftp ptSftp() throws JSchException {
        SftpProperties.Prop prop = sftpProperties.getPt();
        return createSftp(prop, PT_SFTP);
    }

    /**
     * 江西金租sftp.
     */
    @Bean(value = JX_SFTP)
    public Sftp jxSftp() throws JSchException {
        SftpProperties.Prop prop = sftpProperties.getJx();
        return createSftp(prop, JX_SFTP);
    }

//    /**
//     * 客如云sftp.
//     */
//    @Bean(value = KRY_SFTP)
//    public Sftp krySftp() throws JSchException {
//        SftpProperties.Prop prop = sftpProperties.getKry();
//        return createSftp(prop, KRY_SFTP);
//    }


    private static Sftp createSftp(SftpProperties.Prop prop, final String beanName) throws JSchException {
        Sftp sftp = new Sftp(prop.getHost(), prop.getPort(), prop.getUserName(),
                prop.getPassword(), prop.getTimeOut(), prop.getConnectionInitSize(),
                prop.getConnectionMaxSize(), prop.getHeartBeatInterval());
        BEAN_MAP.put(beanName, sftp);
        return sftp;
    }

    /**
     * 心跳位置sftp连接有效.
     */
    @Scheduled(fixedDelay = 10000)
    public void heartBeat() {
        for (Map.Entry<String, Sftp> entry : BEAN_MAP.entrySet()) {
            entry.getValue().keepAlive();
        }
    }
}
