package com.xiafei.tools.exceptions;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 业务异常，不强制捕获(需要决定是否需要事务回滚)
 */
@Getter
@Slf4j
public class BizException extends RuntimeException {

    /**
     * 序列化标识
     */
    private static final long serialVersionUID = -659935883673661594L;

    private String code;

    public BizException(String msg) {
        super(msg);
    }

    public BizException(String code, String msg) {
        super(msg);
        this.code = code;
    }

    public BizException(String code, String msg, Exception e) {
        super(msg, e);
        this.code = code;
    }

}
