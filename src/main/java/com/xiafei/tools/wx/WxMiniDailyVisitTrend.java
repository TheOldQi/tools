package com.xiafei.tools.wx;

import com.alibaba.fastjson.JSON;
import com.xiafei.tools.common.StringUtils;
import com.xiafei.tools.httpclient.PostUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.text.MessageFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <P>Description: 微信日访问趋势数据查询. </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   齐霞飞 </P>
 * <P>CREATE AT: 2019/1/22 17:00</P>
 * <P>UPDATE AT: 2019/1/22 17:00</P>
 *
 * @author 齐霞飞
 * @version 1.0
 * @since java 1.8.0
 */
@Slf4j
public class WxMiniDailyVisitTrend {


    private static final String URL_PATTERN = "https://api.weixin.qq.com/datacube/getweanalysisappiddailyvisittrend?access_token={0}";

    public static void main(String[] args) throws ParseException {
        WxAccessToken.Resp accessToken = WxAccessToken.getAccessToken("appId", "secret");
        DailyVisitTrend dailyVisitTrend = get(accessToken.getAccess_token(), "20190116");
        System.out.println(dailyVisitTrend);
    }

    /**
     * 获取小程序访问统计.
     *
     * @param accessToken 微信验证token，可以通过WxAccessToken获取
     * @param date        统计日期，必须小于今天，格式yyyyMMdd
     * @return 概况统计数据
     */
    public static DailyVisitTrend get(final String accessToken, final String date) {
        log.info("统计小程序日访问趋势数据,accessToken={},date={}", accessToken, date);

        final String url = MessageFormat.format(URL_PATTERN, accessToken);
        final Map<String, String> reqBody = new HashMap<>();
        reqBody.put("begin_date", date);
        reqBody.put("end_date", date);
        final String body = JSON.toJSONString(reqBody);
        log.info("请求url={},body={}", url, body);
        final String s = PostUtil.postJson(url, body);
        log.info("微信接口返回={}", s);
        final Resp resp = JSON.parseObject(s, Resp.class);
        final DailyVisitTrend result = new DailyVisitTrend();
        if (StringUtils.isNotBlank(resp.getErrcode())) {

            result.setErrcode(resp.getErrcode());
            result.setErrmsg(resp.getErrmsg());
        } else {
            TrendData trendData = resp.getList().get(0);
            result.setSessionCnt(trendData.getSession_cnt());
            result.setVisitPv(trendData.getVisit_pv());
            result.setVisitUv(trendData.getVisit_uv());
            result.setVisitUvNew(trendData.getVisit_uv_new());
            result.setStayTimeUv(trendData.getStay_time_uv());
            result.setStayTimeSession(trendData.getStay_time_session());
            result.setVisitDepth(trendData.getVisit_depth());

        }
        return result;
    }


    /**
     * 对外暴露的返回数据.
     */
    @Data
    public static class DailyVisitTrend {
        /**
         * 错误码，如果成功调用会为null.
         */
        private String errcode;

        /**
         * 错误消息.
         */
        private String errmsg;

        /**
         * 打开次数.
         */
        private Integer sessionCnt;

        /**
         * 访问次数.
         */
        private Integer visitPv;

        /**
         * 访问人数.
         */
        private Integer visitUv;

        /**
         * 新用户数.
         */
        private Integer visitUvNew;

        /**
         * 人均停留时长（单位秒）.
         */
        private Double stayTimeUv;

        /**
         * 次均停留时长（单位秒）.
         */
        private Double stayTimeSession;

        /**
         * 人均访问深度.
         */
        private Double visitDepth;
    }

    @Data
    private static class Resp {

        /**
         * 错误码，如果成功调用会为null.
         */
        private String errcode;

        /**
         * 错误消息.
         */
        private String errmsg;

        /**
         * 数据列表.
         */
        private List<TrendData> list;

    }

    /**
     * 日新增访问用户数据.
     */
    @Data
    private static class TrendData {

        /**
         * 请求日期.
         */
        private String ref_date;

        /**
         * 打开次数.
         */
        private Integer session_cnt;

        /**
         * 访问次数.
         */
        private Integer visit_pv;

        /**
         * 访问人数.
         */
        private Integer visit_uv;

        /**
         * 新用户数.
         */
        private Integer visit_uv_new;

        /**
         * 人均停留时长（单位秒）.
         */
        private Double stay_time_uv;

        /**
         * 次均停留时长（单位秒）.
         */
        private Double stay_time_session;

        /**
         * 人均访问深度.
         */
        private Double visit_depth;
    }
}
