package com.xiafei.tools.wx;

import com.alibaba.fastjson.JSON;
import com.xiafei.tools.httpclient.GetUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.text.MessageFormat;

/**
 * <P>Description: . </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   齐霞飞 </P>
 * <P>CREATE AT: 2019/1/14 16:35</P>
 * <P>UPDATE AT: 2019/1/14 16:35</P>
 *
 * @author 齐霞飞
 * @version 1.0
 * @since java 1.8.0
 */
@Slf4j
public class WxAccessToken {

    private static final String URL_PATTEREN = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid={0}&secret={1}";

    /**
     * 获取微信accessToken.
     *
     * @param appId  微信程序appId
     * @param secret 微信程序密钥
     * @return token
     */
    public static Resp getAccessToken(final String appId, final String secret) {
        final String url = MessageFormat.format(URL_PATTEREN, appId, secret);
        log.info("获取微信accessToken，请求url={}", url);
        final String s = GetUtils.get(url);
        log.info("微信接口返回={}", s);
        final Resp resp = JSON.parseObject(s, Resp.class);
        if (resp.getErrcode() == null || resp.getErrcode().equals("0")) {
            return resp;
        } else {
            log.error("微信返回状态码错误");
            throw new RuntimeException(resp.getErrmsg());

        }

    }

    public static void main(String[] args) {
        Resp resp = getAccessToken("wxe64a21f09e3afe33", "fd62ce60ea3e8b967a1660ef1c2c8d19");
        System.out.println("resp:" + resp);
    }

    /**
     * 微信响应数据对象.
     */
    @Data
    public static class Resp {

        /**
         * 错误码，只要成功返回了，肯定是空.
         */
        private String errcode;

        /**
         * 错误信息，只要成功返回了，肯定是空.
         */
        private String errmsg;

        /**
         * token.
         */
        private String access_token;

        /**
         * 超时时间，单位秒.
         */
        private Integer expires_in;
    }

}
