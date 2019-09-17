package com.xiafei.tools.data;

import lombok.Data;

import java.io.Serializable;

/**
 * <P>Description: 消息对象，通用于通讯层的封装. </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   齐霞飞 </P>
 * <P>CREATE DATE: 2017/12/27</P>
 * <P>UPDATE DATE: 2017/12/27</P>
 *
 * @author 齐霞飞
 * @version 1.0
 * @since java 1.8.0
 */
@Data
public class Message<D> implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 结果。
     */
    private Result result;
    /**
     * 编码(可表示错误码等)。
     */
    private String code;
    /**
     * 错误信息编码(可表示错误码等)。
     */
    private String message;
    /**
     * 数据。
     */
    private D data;

    public Message() {
    }

    public Message(Result result, String code) {
        super();
        this.result = result;
        this.code = code;
    }

    public Message(Result result, String code, String message) {
        super();
        this.result = result;
        this.code = code;
        this.message = message;
    }

    public Message(Result result, String code, String message, D data) {
        super();
        this.result = result;
        this.code = code;
        this.message = message;
        this.data = data;
    }
}
