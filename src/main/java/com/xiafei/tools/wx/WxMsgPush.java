package com.xiafei.tools.wx;

import com.alibaba.fastjson.JSON;
import com.xiafei.tools.common.StringUtils;
import com.xiafei.tools.common.ValidateUtils;
import com.xiafei.tools.httpclient.PostUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * <P>Description: 微信消息推送. </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   齐霞飞 </P>
 * <P>CREATE AT: 2019/1/14 17:26</P>
 * <P>UPDATE AT: 2019/1/14 17:26</P>
 *
 * @author 齐霞飞
 * @version 1.0
 * @since java 1.8.0
 */
@Slf4j
public class WxMsgPush {


    private static final String URL_PARTTERN = "https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token={0}";

    public static String pushTemplate(final String accessToken, final TemplateReq templateReq) {
        if (StringUtils.isBlank(accessToken)) {
            log.error("推送微信模板，accessToken必传");
            throw new IllegalArgumentException("accessToken必传");
        }
        ValidateUtils.validate(templateReq);

        final String url = MessageFormat.format(URL_PARTTERN, accessToken);
        final String body = JSON.toJSONString(templateReq);
        log.info("url={},body={}", url, body);
        final String s = PostUtil.postJson(url, body);
        log.info("微信接口返回={}", s);

        return s;

    }


    public static void main(String[] args) {
        final TemplateReq req = new TemplateReq();
        req.setTouser("touser");
        req.setTemplate_id("templateId");
        Map<String, Object> data = new HashMap<>();
        Map<String, Object> keyWord1 = new HashMap<>();
        keyWord1.put("value", "测试审核结果");
        data.put("keyword1", keyWord1);
        Map<String, Object> keyWord2 = new HashMap<>();
        keyWord2.put("value", "测试拒绝理由");
        data.put("keyword2", keyWord2);
        Map<String, Object> keyWord3 = new HashMap<>();
        keyWord3.put("value", "测试关键词3");
        data.put("keyword3", keyWord3);
        req.setData(data);
        req.setForm_id("1547461434925");

        WxAccessToken.Resp Resp = WxAccessToken.getAccessToken("appId", "secret");
        String s = pushTemplate(Resp.getAccess_token(), req);
    }

    /**
     * 模板请求参数.
     */
    @Data
    public static class TemplateReq {

        /**
         * 接收方在appid下的openId.
         */
        @NotBlank
        private String touser;

        /**
         * 模板id.
         */
        @NotBlank
        private String template_id;

        /**
         * 点击模板卡片后的跳转页面，仅限本小程序内的页面。支持带参数,（示例index?foo=bar）。该字段不填则模板无跳转。.
         */
        private String page;

        /**
         * 表单提交场景下，为 submit 事件带上的 formId；支付场景下，为本次支付的 prepay_id.
         */
        @NotBlank
        private String form_id;

        /**
         * 模板内容，不填则下发空模板.
         */
        @NotNull
        private Map<String, Object> data;

        /**
         * 模板内容字体的颜色，不填默认黑色.
         */
        private String color;


        /**
         * 模板需要放大的关键词，不填则默认无放大.
         */
        private String emphasis_keyword;
    }
}
