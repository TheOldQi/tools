package com.xiafei.tools;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * <P>Description: 阿里云短信服务. </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   齐霞飞 </P>
 * <P>CREATE DATE: 2018/4/16</P>
 * <P>UPDATE DATE: 2018/4/16</P>
 *
 * @author qixiafei
 * @version 1.0
 * @since java 1.8.0
 */
@Slf4j
public class AliyunSms {

    /**
     * 产品名称:云通信短信API产品,开发者无需替换
     */
    private static final String PRODUCT = "Dysmsapi";
    /**
     * 产品域名,开发者无需替换
     */
    private static final String DOMAIN = "dysmsapi.aliyuncs.com";
    private static final String REGION = "cn-hangzhou";

    static {
        // 设置URLConnection超时配置
        // 连接主机的超时时间,单位ms
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        // 从主机获取数据的超时时间，单位ms
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");

    }

    public static void main(String[] args) {
        final Map<String, String> smsParam = new HashMap<>();
        smsParam.put("code", String.valueOf(new Random().nextInt(899999) + 100000));
        sendSms("15210254805", smsParam, SmsTypeEnum.replace_file);
    }

    /**
     * 发送短信验证码
     *
     * @param phone    接收短信的手机号
     * @param smsParam 短信模板替换参数，如模板为亲爱的${name}，您的验证码为${code}时，map中应含有key为Name和code项
     */
    public static void sendSms(final String phone, final Map<String, String> smsParam, final SmsTypeEnum smsTypeEnum) {

        if (StringUtils.isBlank(phone)) {
            log.error("阿里云短信接口，手机号为空");
            throw new RuntimeException("手机号空");
        }

        final SmsConfig smsConfig = smsTypeEnum.getConfig();

        //初始化acsClient,暂不支持多region（请勿修改）
        final IClientProfile profile = DefaultProfile.getProfile(REGION, smsConfig.getAccessKeyId(), smsConfig.getAccessKeySecret());
        try {
            DefaultProfile.addEndpoint(REGION, REGION, PRODUCT, DOMAIN);
        } catch (ClientException e) {
            log.error("阿里云短信接口，设置端点失败", e);
            throw new RuntimeException(e);
        }
        final IAcsClient acsClient = new DefaultAcsClient(profile);

        //组装请求对象-具体描述见控制台-文档部分内容
        final SendSmsRequest request = new SendSmsRequest();
        //必填:待发送手机号
        request.setPhoneNumbers(phone);
        //必填:短信签名-可在短信控制台中找到
        request.setSignName(smsConfig.getSignName());
        //必填:短信模板-可在短信控制台中找到
        request.setTemplateCode(smsConfig.getTemplateCode());
        //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
        request.setTemplateParam(JSON.toJSONString(smsParam));
        //选填-上行短信扩展码(无特殊需求用户请忽略此字段)
        //request.setSmsUpExtendCode("90997");
        //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
        //request.setOutId("yourOutId");
        //请求失败这里会抛ClientException异常
        final SendSmsResponse sendSmsResponse;
        try {
            sendSmsResponse = acsClient.getAcsResponse(request);
        } catch (ClientException e) {
            log.error("阿里云短信接口，发送短信失败", e);
            throw new RuntimeException(e);
        }
        final String code = sendSmsResponse.getCode();
        final Code instance = Code.instance(code);
        switch (instance) {
            case SUCCESS:
                log.info("阿里云短信发送成功");
                break;
            case MOBILE_NUMBER_ILLEGAL:
                log.error("阿里云短信,手机号=[{}]非法", phone);
                throw new IllegalArgumentException("非法手机号");
            case AMOUNT_NOT_ENOUGH:
                log.error("阿里云短信服务该充钱了,msg={}", instance.desc);
                throw new RuntimeException("阿里云短信没钱了");
            case BUSINESS_LIMIT_CONTROL:
                log.error("阿里云短信发送太频繁了,msg={}", instance.desc);
                throw new IllegalArgumentException("短信发送过于频繁");
            default:
                log.error("阿里云短信发送异常,msg={}", instance.desc);
                throw new RuntimeException("短信发送异常");
        }
    }

    /**
     * 短信配置.
     */
    @Data
    public static class SmsConfig {

        /**
         * 账号id.
         */
        private String accessKeyId;

        /**
         * 账号密钥.
         */
        private String accessKeySecret;

        /**
         * 短信签名
         */
        private String signName;

        /**
         * 短信模板编号.
         */
        private String templateCode;
    }


    /**
     * 配置.
     */
    private static final Map<SmsTypeEnum, SmsConfig> configMap = new HashMap<>();

    /**
     * 短信类型枚举.
     */
    public enum SmsTypeEnum {

        replace_file {
            {
                final SmsConfig config = new SmsConfig();
                config.setAccessKeyId("accesskeyid");
                config.setAccessKeySecret("woshikeysecret");
                config.setSignName("齐霞飞");
                config.setTemplateCode("SMS_xxxx");
                configMap.put(this, config);
            }

        };// 替换文件

        SmsConfig getConfig() {
            return configMap.get(this);
        }
    }


    /**
     * 阿里云短信验证状态码枚举.
     */
    private enum Code {

        SUCCESS("OK", "请求成功"),
        NO_AUTH("isp.RAM_PERMISSION_DENY", "RAM权限DENY"),
        OUT_OF_SERVICE("isv.OUT_OF_SERVICE", "业务停机"),
        PRODUCT_UN_SUBSCRIPT("isv.PRODUCT_UN_SUBSCRIPT", "未开通云通信产品的阿里云客户"),
        ACCOUNT_NOT_EXISTS("isv.ACCOUNT_NOT_EXISTS", "账户不存在"),
        ACCOUNT_ABNORMAL("isv.ACCOUNT_ABNORMAL", "账户异常"),
        SMS_TEMPLATE_ILLEGAL("isv.SMS_TEMPLATE_ILLEGAL", "短信模板不合法"),
        SMS_SIGNATURE_ILLEGAL("isv.SMS_SIGNATURE_ILLEGAL", "短信签名不合法"),
        INVALID_PARAMETERS("isv.INVALID_PARAMETERS", "参数异常"),
        SYSTEM_ERROR("isp.SYSTEM_ERROR", "系统错误"),
        MOBILE_NUMBER_ILLEGAL("isv.MOBILE_NUMBER_ILLEGAL", "非法手机号"),
        MOBILE_COUNT_OVER_LIMIT("isv.MOBILE_COUNT_OVER_LIMIT", "手机号码数量超过限制"),
        TEMPLATE_MISSING_PARAMETERS("isv.TEMPLATE_MISSING_PARAMETERS", "模板缺少变量"),
        BUSINESS_LIMIT_CONTROL("isv.BUSINESS_LIMIT_CONTROL", "业务限流"),
        INVALID_JSON_PARAM("isv.INVALID_JSON_PARAM", "JSON参数不合法，只接受字符串值"),
        BLACK_KEY_CONTROL_LIMIT("isv.BLACK_KEY_CONTROL_LIMIT", "黑名单管控"),
        PARAM_LENGTH_LIMIT("isv.PARAM_LENGTH_LIMIT", "参数超出长度限制"),
        PARAM_NOT_SUPPORT_URL("isv.PARAM_NOT_SUPPORT_URL", "不支持URL"),
        AMOUNT_NOT_ENOUGH("isv.AMOUNT_NOT_ENOUGH", "账户余额不足");


        /**
         * 状态码.
         */
        public final String code;
        /**
         * 状态描述.
         */
        public final String desc;

        Code(final String code, final String desc) {
            this.code = code;
            this.desc = desc;
        }

        private static Code instance(final String code) {
            if (code == null) {
                return null;
            }

            for (Code e : values()) {
                if (e.code.equals(code)) {
                    return e;
                }
            }
            return null;

        }
    }

}
