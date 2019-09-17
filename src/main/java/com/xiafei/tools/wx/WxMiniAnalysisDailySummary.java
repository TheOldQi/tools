package com.xiafei.tools.wx;

import com.alibaba.fastjson.JSON;
import com.xiafei.tools.httpclient.PostUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <P>Description: 微信小程序概况数据. </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   齐霞飞 </P>
 * <P>CREATE AT: 2019/1/14 16:30</P>
 * <P>UPDATE AT: 2019/1/14 16:30</P>
 *
 * @author 齐霞飞
 * @version 1.0
 * @since java 1.8.0
 */
@Slf4j
public class WxMiniAnalysisDailySummary {


    private static final String URL_PATTERN = "https://api.weixin.qq.com/datacube/getweanalysisappiddailysummarytrend?access_token={0}";

    /**
     * 获取小程序概况统计.
     *
     * @param accessToken 微信验证token，可以通过WxAccessToken获取
     * @param date        统计日期，必须小于今天，格式yyyyMMdd
     * @return 概况统计数据
     */
    public static Summary getSummary(final String accessToken, final String date) {
        log.info("统计小程序概况,accessToken={},date={}", accessToken, date);

        final String url = MessageFormat.format(URL_PATTERN, accessToken);
        final Map<String, String> reqBody = new HashMap<>();
        reqBody.put("begin_date", date);
        reqBody.put("end_date", date);
        final String body = JSON.toJSONString(reqBody);
        log.info("请求url={},body={}", url, body);
        final String s = PostUtil.postJson(url, body);
        log.info("微信接口返回={}", s);
        return JSON.parseObject(s, Summary.class);
    }

    public static void main(String[] args) {
        Summary summary = getSummary("123123", "20190116");
        System.out.println(summary);
    }

    /**
     * 概况.
     */
    @Data
    public static class Summary {

        /**
         * 错误码，如果成功调用会为null.
         */
        private String errcode;

        /**
         * 错误消息.
         */
        private String errmsg;

        /**
         * 日期维度的数据列表.
         */
        private List<SummaryData> list;


    }

    /**
     * 概况数据.
     */
    @Data
    public static class SummaryData {

        /**
         * 日期，格式为yyyyMMdd.
         */
        private String ref_date;

        /**
         * 累计用户数.
         */
        private Integer visit_total;

        /**
         * 转发次数.
         */
        private Integer share_pv;

        /**
         * 转发用户数.
         */
        private Integer share_uv;

    }
}
