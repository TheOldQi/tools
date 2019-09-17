package com.xiafei.tools.wx;

import com.alibaba.fastjson.JSON;
import com.xiafei.tools.data.ErrorCode;
import com.xiafei.tools.exceptions.BizException;
import com.xiafei.tools.httpclient.GetUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.text.MessageFormat;

/**
 * <P>Description: 微信小程序微信登录验证. </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   齐霞飞 </P>
 * <P>CREATE DATE: 2018/9/26</P>
 * <P>UPDATE DATE: 2018/9/26</P>
 *
 * @author qixiafei
 * @version 1.0
 * @since java 1.8.0
 */
@Slf4j
public class WxLoginVerify {

    private static final String URL_PATTERN = "https://api.weixin.qq.com/sns/jscode2session?appid={0}&secret={1}&js_code={2}&grant_type=authorization_code";

    public static void main(String[] args) throws IOException {

    }

    private WxLoginVerify() {

    }

    /**
     * 调用微信开放平台jsCode接口验证用户登录状态.
     *
     * @param appid  小程序id
     * @param secret 小程序密钥
     * @param jsCode 请求唯一标识码
     * @return Resp对象，如果errcode不为空，则登录验证失败，否则登录验证成功
     */
    public static Resp jsCode(final String appid, final String secret, final String jsCode) {
        final String url = MessageFormat.format(URL_PATTERN, appid, secret, jsCode);
        log.info("调用微信开放平台验证登录，请求={}", url);

        final String respJson = GetUtils.get(url);
        log.info("调用微信开放平台验证登录，返回={}", respJson);

        final Resp resp = JSON.parseObject(respJson, Resp.class);
        if (resp == null) {
            log.error("返回结果格式异常");
            throw new RuntimeException("微信开放平台登录验证返回结果异常");
        }
        if (StringUtils.isNotBlank(resp.getErrcode())) {
            log.error("微信开放接口验证失败");
            throw new BizException(ErrorCode.PARAMETER_ILLEGAL.code, "用户身份非法");
        }

        return resp;
    }


    @Data
    public static class Resp {

        /**
         * 会话id.
         */
        private String session_key;

        /**
         * 微信用户唯一标识.
         */
        private String openid;

        /**
         * 错误码，如果没有错误该字段应该为空.
         */
        private String errcode;

        /**
         * 如果错误码不为空，这里描述错误信息.
         */
        private String errmsg;
    }
}
