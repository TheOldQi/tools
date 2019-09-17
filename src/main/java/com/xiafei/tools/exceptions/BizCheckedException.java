package com.xiafei.tools.exceptions;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 业务异常，强制捕获(需要决定是否需要事务回滚)
 */
@Getter
@Slf4j
public class BizCheckedException extends Exception {

    private String code;

    public BizCheckedException(String msg) {
        super(msg);
    }

    public BizCheckedException(String code, String msg) {
        super(msg);
        this.code = code;
    }

    public BizCheckedException(String code, String msg, Exception e) {
        super(msg, e);
        this.code = code;
    }

}
