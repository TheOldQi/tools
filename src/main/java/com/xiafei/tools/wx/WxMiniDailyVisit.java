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
 * <P>Description: 微信日访问数据查询. </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   齐霞飞 </P>
 * <P>CREATE AT: 2019/1/17 20:28</P>
 * <P>UPDATE AT: 2019/1/17 20:28</P>
 *
 * @author 齐霞飞
 * @version 1.0
 * @since java 1.8.0
 */
@Slf4j
public class WxMiniDailyVisit {


    private static final String URL_PATTERN = "https://api.weixin.qq.com/datacube/getweanalysisappiddailyretaininfo?access_token={0}";

    public static void main(String[] args) throws ParseException {
//        WxAccessToken.Resp accessToken = WxAccessToken.getAccessToken("wxe64a21f09e3afe33", "fd62ce60ea3e8b967a1660ef1c2c8d19");
        DailyVisit dailyVisit = get("test", "20190116");
        System.out.println(dailyVisit);
    }

    /**
     * 获取小程序访问统计.
     *
     * @param accessToken 微信验证token，可以通过WxAccessToken获取
     * @param date        统计日期，必须小于今天，格式yyyyMMdd
     * @return 概况统计数据
     */
    public static DailyVisit get(final String accessToken, final String date) {
        log.info("统计小程序访问数据,accessToken={},date={}", accessToken, date);

        final String url = MessageFormat.format(URL_PATTERN, accessToken);
        final Map<String, String> reqBody = new HashMap<>();
        reqBody.put("begin_date", date);
        reqBody.put("end_date", date);
        final String body = JSON.toJSONString(reqBody);
        log.info("请求url={},body={}", url, body);
        final String s = PostUtil.postJson(url, body);
        log.info("微信接口返回={}", s);
        final Resp resp = JSON.parseObject(s, Resp.class);
        final DailyVisit result = new DailyVisit();
        if (StringUtils.isNotBlank(resp.getErrcode())) {

            result.setErrcode(resp.getErrcode());
            result.setErrmsg(resp.getErrmsg());
        } else {

            result.setNewVisitUser(resp.getVisit_uv_new() == null ? 0 : resp.getVisit_uv_new().get(0).getValue());
            result.setTotalVisitUser(resp.getVisit_uv() == null ? 0 : resp.getVisit_uv().get(0).getValue());
        }
        return result;
    }


    /**
     * 对外暴露的返回数据.
     */
    @Data
    public static class DailyVisit {
        /**
         * 错误码，如果成功调用会为null.
         */
        private String errcode;

        /**
         * 错误消息.
         */
        private String errmsg;

        /**
         * 总访问用户数.
         */
        private Integer totalVisitUser;

        /**
         * 新增访问用户数.
         */
        private Integer newVisitUser;
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
         * 请求日期.
         */
        private String ref_date;

        /**
         * 日新增访问用户数据列表.
         */
        private List<VisitUvNew> visit_uv_new;

        /**
         * 日访问用户总数列表
         */
        private List<VisitUv> visit_uv;
    }

    /**
     * 日新增访问用户数据.
     */
    @Data
    private static class VisitUvNew {

        /**
         * 序号.
         */
        private Integer key;

        /**
         * 人数.
         */
        private Integer value;
    }

    /**
     * 日访问用户数据.
     */
    @Data
    private static class VisitUv {

        /**
         * 序号.
         */
        private Integer key;

        /**
         * 人数.
         */
        private Integer value;
    }
}
