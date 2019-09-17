package com.xiafei.tools.data;

/**
 * <P>Description: Message的工具类. </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   齐霞飞 </P>
 * <P>CREATE DATE: 2017/12/27</P>
 * <P>UPDATE DATE: 2017/12/27</P>
 *
 * @author 齐霞飞
 * @version 1.0
 * @since java 1.8.0
 */
public class Messages {

    /**
     * 构造一个成功的消息.
     *
     * @param data 数据。
     * @param <D>  消息中数据的类型
     * @return 消息实例。
     */
    public static <D> Message<D> success(final D data) {
        return new Message<D>(Result.SUCCESS, ErrorCode.SUCCESS.code, ErrorCode.SUCCESS.desc, data);
    }

    /**
     * 构造一个不带数据的成功的消息.
     *
     * @param <D> 消息中数据的类型
     * @return 消息实例。
     */
    public static <D> Message<D> success() {
        return success(null);
    }

    /**
     * 构造一个失败的消息。 表示逻辑上失败.
     *
     * @param code 编码,表示失败原因。
     * @param <D>  消息中数据的类型
     * @return 消息实例。
     */
    public static <D> Message<D> failed(final String code) {
        return new Message<D>(Result.FAILED, code);
    }

    /**
     * 构造一个失败的消息。 表示逻辑上失败.
     *
     * @param code 编码,表示失败原因。
     * @param msg  错误信息
     * @param <D>  消息中数据的类型
     * @return 消息实例。
     */
    public static <D> Message<D> failed(final String code, final String msg) {
        return new Message<D>(Result.FAILED, code, msg);
    }

    /**
     * 构造一个错误的消息.
     *
     * @param <D> 消息中数据的类型
     * @return 消息实例。表示错误
     */
    public static <D> Message<D> error() {
        return new Message<D>(Result.ERROR, ErrorCode.SYSTEM_EXCEPTION.code, ErrorCode.SYSTEM_EXCEPTION.desc, null);
    }

    /**
     * 构造一个带错误错误的消息.
     *
     * @param code 错误编码
     * @param <D>  消息中数据的类型
     * @return 消息实例。表示错误
     */
    public static <D> Message<D> error(final String code) {
        return new Message<D>(Result.ERROR, code);
    }

    /**
     * 判断消息是否成功.
     *
     * @param message 消息对象实例
     * @return 成功真值。
     */
    public static boolean isSuccess(Message<?> message) {

        return Result.SUCCESS.equals(message.getResult());
    }

    private Messages() {
    }
}
