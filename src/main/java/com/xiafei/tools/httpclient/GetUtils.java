package com.xiafei.tools.httpclient;


import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.manager.Constants;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Map;

/**
 * <P>Description: 使用Http-post方式发送数据. </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   齐霞飞 </P>
 * <P>CREATE DATE: 2017/8/11</P>
 * <P>UPDATE DATE: 2017/8/11</P>
 *
 * @author qixiafei
 * @version 1.0
 * @since java 1.7.0
 */
@Slf4j
@Component
public class GetUtils implements Serializable {


    @Resource
    private Environment env;

    /**
     * 工具类.
     */
    private GetUtils() {

    }


    /**
     * 执行发送post请求并拿到结果的方法.
     *
     * @param url 发送请求地址
     * @return 响应
     */
    public static String get(final String url) {
        final CloseableHttpClient client = HttpClientPool.getHttpClient();
        // multipart/form-data不能指定请求头，因为我们不知道边界怎么设置，由工具去自动计算及设置
        final HttpGet httpGet = new HttpGet(url);

        try {
            return client.execute(httpGet, new StringResponseHandler());
        } catch (IOException e) {
            log.error("调用http异常", e);
            throw new RuntimeException("调用http异常");
        }

    }


    /**
     * 执行发送post请求并拿到结果的方法.
     *
     * @param url    发送请求地址
     * @param params 请求参数，方法里会进行urlEncoding
     * @return 响应
     */
    public static String get(final String url, final Map<String, Object> params) throws UnsupportedEncodingException {
        log.info("get请求，请求url={},参数={}", url, params);
        final CloseableHttpClient client = HttpClientPool.getHttpClient();
        final String actualUrl;
        final StringBuilder paramSb = new StringBuilder();
        // multipart/form-data不能指定请求头，因为我们不知道边界怎么设置，由工具去自动计算及设置
        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                Object value = entry.getValue();
                if (value != null) {
                    paramSb.append(URLEncoder.encode(entry.getKey(), Constants.CHARSET)).append("=").
                            append(URLEncoder.encode(value.toString(), Constants.CHARSET)).append("&");
                }
            }
            if (paramSb.length() > 0) {
                paramSb.deleteCharAt(paramSb.length() - 1);
                actualUrl = url + "?" + paramSb.toString();
            } else {
                actualUrl = url;

            }
        } else {
            actualUrl = url;
        }
        log.info("get请求，实际请求url={}", actualUrl);
        final HttpGet httpGet = new HttpGet(actualUrl);
        try {
            return client.execute(httpGet, new StringResponseHandler());
        } catch (IOException e) {
            log.error("调用http异常", e);
            throw new RuntimeException("调用http异常");
        }

    }


    public static void main(String[] args) throws UnsupportedEncodingException {
        System.out.println(get("http://pis.baiwang.com/bwmg/titlecloud/company/search", Collections.singletonMap("key", "北京")));
    }

}
