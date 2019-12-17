package com.xiafei.tools.data;

/**
 * <P>Description: 错误码枚举. </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   齐霞飞 </P>
 * <P>CREATE DATE: 2018/7/19</P>
 * <P>UPDATE DATE: 2018/7/19</P>
 *
 * @author qixiafei
 * @version 1.0
 * @since java 1.8.0
 */
public enum ErrorCode {
    // ===========================通用 5位及以下 ================================
    SUCCESS("200", "成功"),
    FAIL("500", "失败"),
    SYSTEM_EXCEPTION("1000", "系统异常"),
    DATA_ERROR("1001", "数据错误，请联系管理员"),
    CALL_SERVICE_EXCEPTION("1101", "调用接口异常"),
    CALL_SERVICE_FAILED("1102", "调用接口失败"),
    INTERNAL_EXCEPTION("1103", "内部系统异常"),
    PARAMETER_IS_NULL("2000", "参数为空"),
    PARAMETER_FORMAT_ERROR("2001", "参数格式错误"),
    PARAMETER_ILLEGAL("2002", "参数非法"),
    STATE_CHANGE("2003", "状态已变更"),
    USER_NOT_REGISTER("3000", "用户未注册"),
    USER_PASSWORD_ERROR("3001", "用户密码错误"),
    SYSTEM_BUSY("3002", "系统繁忙,请稍后重试"),
    NO_USER_TOKEN("3003", "获取不到TOKEN,用户未登录"),
    TOKEN_EXPIRED("3004", "TOKEN已过期,请重新登陆"),
    WX_LOGIN_EXPIRED("4000", "微信登录状态已过期，请重新登录"),
    DUPLICATE_KEY("5000", "插入信息重复"),
    MERCHANT_NOT_EXIST("6000", "商户不存在"),
    SIGN_VERIFY_FALIED("7000", "验签失败"),
    DECRYPT_FAILED("7001", "解密失败"),

    ;
    public final String code;
    public final String desc;

    ErrorCode(final String code, final String desc) {
        this.code = code;
        this.desc = desc;
    }
}
