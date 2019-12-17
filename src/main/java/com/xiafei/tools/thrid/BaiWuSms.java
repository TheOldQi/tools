package com.xiafei.tools.thrid;

import com.xiafei.tools.httpclient.PostUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <P>Description: 百悟短信平台访问工具. </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   齐霞飞 </P>
 * <P>CREATE AT: 2019/1/11 17:24</P>
 * <P>UPDATE AT: 2019/1/11 17:24</P>
 *
 * @author 齐霞飞
 * @version 1.0
 * @since java 1.8.0
 */
@Slf4j
public class BaiWuSms {


    public static void main(String[] args) throws Exception {

    }

    /**
     * 发送短信.
     *
     * @param url         请求地址
     * @param msg         短信内容
     * @param mobile      目标手机号
     * @param corpId      合作方账户
     * @param corpPwd     合作方密码
     * @param corpService 合作方业务代码
     * @param corpMsgId   合作方对短信进行分类代码
     * @param ext         短信后6位数字，可以不足6位或不传，但是不能超过，百悟主叫号码是10657532521924，占用14位，号码总长不能超过20位。
     */
    public static void sendSms(final String url, final String msg, final String mobile, final String corpId, final String corpPwd,
                               final String corpService, final String corpMsgId, final String ext) {
        batchSendSms(url, msg, Collections.singletonList(mobile), corpId, corpPwd, corpService, corpMsgId, ext);
    }

    /**
     * 群发短信，相同内容.
     *
     * @param url         请求地址
     * @param msg         短信内容
     * @param mobiles     群发的手机号列表
     * @param corpId      合作方账户
     * @param corpPwd     合作方密码
     * @param corpService 合作方业务代码
     * @param corpMsgId   合作方对短信进行分类代码
     * @param ext         短信后6位数字，可以不足6位或不传，但是不能超过，百悟主叫号码是10657532521924，占用14位，号码总长不能超过20位。
     */
    public static void batchSendSms(final String url, final String msg, final List<String> mobiles, final String corpId, final String corpPwd,
                                    final String corpService, final String corpMsgId, final String ext) {
        log.info("调用百悟短信平台发送短信，msg={},mobiles={},corpId={},corpPwd={},corpService={},corpMsgId={},ext={}",
                msg, mobiles, corpId, corpPwd, corpService, corpMsgId, ext);
        if (msg == null || mobiles == null || corpId == null || corpPwd == null || corpService == null) {
            throw new IllegalArgumentException("必传参数[msg,mobiles,corpId,corpPwd,corpService]");

        }
        if (msg.length() >= 1000) {
            throw new IllegalArgumentException("短信内容不能大于等于1000字");
        }

        if (mobiles.size() > 200) {
            throw new IllegalArgumentException("一次发送手机号不能超过200");
        }

        if (corpMsgId != null && corpMsgId.length() > 50) {
            throw new IllegalArgumentException("corpMsgId不能超过50个字符");
        }

        if (ext != null) {
            if (ext.length() > 6) {
                throw new IllegalArgumentException("ext不能超过6位");
            }

            for (char c : ext.toCharArray()) {
                if (!Character.isDigit(c)) {
                    throw new IllegalArgumentException("ext必须由不超过6位的纯数字组成");
                }
            }
        }

        final Map<String, Object> reqMap = new HashMap<>();
        reqMap.put("corp_id", corpId);
        reqMap.put("corp_pwd", corpPwd);
        reqMap.put("corp_service", corpService);
        reqMap.put("mobile", buildMobile(mobiles));
        reqMap.put("msg_content", msg);
        reqMap.put("corp_msg_id", corpMsgId == null ? "" : corpMsgId);
        reqMap.put("ext", ext == null ? "" : ext);
        log.info("请求百悟接口，入参={}", reqMap);
        final String ret = PostUtil.postKv(url, reqMap);
        log.info("百悟返回={}", ret);
        if (ret != null && ret.startsWith("0#")) {
            int i = Integer.parseInt(ret.substring(2));
            log.info("成功发送{}条短信", i);
        } else {
            log.error("发送短信失败");
            RetCodeEnum instance = RetCodeEnum.instance(ret);
            if (instance == null) {
                throw new RuntimeException("短信通道位置错误");
            }

            throw new RuntimeException(instance.desc);
        }

    }


    /**
     * 将手机号列表转换成百悟接受的字符串格式.
     *
     * @param mobiles 手机号列表
     * @return 百悟接受的字符串格式
     */
    private static String buildMobile(final List<String> mobiles) {
        if (mobiles.isEmpty()) {
            log.error("电话号码不能为空");
        }

        if (mobiles.size() == 1) {
            return mobiles.get(0);
        }

        final StringBuilder sb = new StringBuilder();
        for (String s : mobiles) {
            sb.append(s).append(",");
        }

        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();

    }

    /**
     * 百悟返回码枚举.
     */
    private enum RetCodeEnum {
        ACCOUNT_NO_MONEY("100", "余额不足"),
        ACCOUNT_CLOSE("101", "账号关闭"),
        TOO_LONG("102", "短信内容超过1000字（包括1000字）或为空"),
        MOBILE_TO_MANY("103", "手机号码超过200个或合法手机号码为空或者与通道类型不匹配"),
        CORP_MSG_ID_ILLEGAL("104", "corp_msg_id超过50个字符或没有传corp_msg_id字段"),
        NO_USER("106", "用户名不存在"),
        PWD_ERROR("107", "密码错误"),
        IP_ERROR("108", "指定访问ip错误(接口支持绑定单IP，多IP，IP号段及无IP)"),
        SERVICE_CLOSE("109", "业务代码不存在或者通道关闭"),
        EXT_ILLEGAL("110", "扩展号不合法"),
        ADDRESS_NOT_EXIST("9", "访问地址不存在");

        private String code;
        private String desc;

        RetCodeEnum(String code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        private static RetCodeEnum instance(final String code) {
            if (code == null) {
                return null;
            }

            for (RetCodeEnum e : values()) {
                if (e.code.equals(code)) return e;
            }

            return null;
        }
    }
}
