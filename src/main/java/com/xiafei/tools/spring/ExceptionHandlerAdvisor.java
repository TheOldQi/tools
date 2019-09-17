package com.xiafei.tools.spring;

import com.xiafei.tools.data.ErrorCode;
import com.xiafei.tools.data.Message;
import com.xiafei.tools.data.Messages;
import com.xiafei.tools.exceptions.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <P>Description: 微信小程序Controller统一异常处理handler. </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   齐霞飞 </P>
 * <P>CREATE DATE: 2018/9/10</P>
 * <P>UPDATE DATE: 2018/9/10</P>
 *
 * @author qixiafei
 * @version 1.0
 * @since java 1.8.0
 */
@Slf4j
@ControllerAdvice(basePackages = {"com.virgo.finance.pay.paas.cashier.web.controller.publicpay"})
public class ExceptionHandlerAdvisor {


    @ExceptionHandler(value = Exception.class)
    public ModelAndView exception(Exception e, WebRequest webRequest, HttpServletResponse response) {
        final String requestedWith = webRequest.getHeader("x-requested-with");
        if (requestedWith.equalsIgnoreCase("XMLHttpRequest")) {
            // 如果是ajax请求，返回消息
            final Message msg;
            if (e instanceof BizException) {
                msg = Messages.failed(((BizException) e).getCode(), e.getMessage());
            } else {
                log.error("ajax请求，controller发生未捕获异常", e);
                msg = Messages.failed(ErrorCode.SYSTEM_EXCEPTION.code, "系统异常");
            }
            return render(msg, response);
        } else {
            // 如果不是ajax请求，响应错误页面
            ModelAndView modelAndView = new ModelAndView("publicpay/error");
            if (e instanceof BizException) {
                modelAndView.addObject("errorMsg", e.getMessage());
            } else {
                log.error("普通请求，controller发生未捕获异常");
                modelAndView.addObject("errorMsg", "哎呦~ 服务器居然累倒了!");
            }
            return modelAndView;
        }


    }


    public ModelAndView render(Object model, HttpServletResponse response) {
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();

        MediaType jsonMimeType = MediaType.APPLICATION_JSON;

        try {
            jsonConverter.write(model, jsonMimeType, new ServletServerHttpResponse(response));
        } catch (HttpMessageNotWritableException | IOException e) {
            log.error("json渲染失败", e);
        }

        return null;
    }
}
