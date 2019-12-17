/*
 * Copyright (c) 2016-2020 LEJR.COM All Right Reserved
 */

package com.xiafei.tools.data;

import lombok.Getter;

/**
 * <P>Description: Message的Result编码枚举. </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   齐霞飞 </P>
 * <P>CREATE DATE: 2017/12/27</P>
 * <P>UPDATE DATE: 2017/12/27</P>
 *
 * @author 齐霞飞
 * @version 1.0
 * @since java 1.8.0
 */
@Getter
public enum Result {

    /**
     * 成功.
     */
    SUCCESS(1),

    /**
     * 失败。(表示逻辑上的错误).
     */
    FAILED(0),

    /**
     * 错误.
     */
    ERROR(-1),

    /**
     * 修改密码.
     */
    UPDATEPWD(2);

    private int value;

    Result(final int value) {
        this.value = value;
    }

    public static boolean isSuccess(Result result) {
        if (result == Result.SUCCESS) {
            return true;
        }
        return false;
    }

}
