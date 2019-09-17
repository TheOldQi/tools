/*
 * Copyright (c) 2019. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.xiafei.tools.enums;

/**
 * <P>Description: （0-否，1-是）类型的是否枚举 </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   齐霞飞 </P>
 * <P>CREATE DATE: 2017/6/29</P>
 * <P>UPDATE DATE: 2017/6/29</P>
 *
 * @author qixiafei
 * @since java 1.7.0
 */
public enum YnEnum {

    Y(1, (byte) 1, 1L, "1", "是"),
    N(0, (byte) 0, 0L, "0", "否");

    public final Integer intCode;
    public final Byte byteCode;
    public final Long longCode;
    public final String stringCode;
    public final String desc;

    YnEnum(Integer intCode, Byte byteCode, Long longCode, String stringCode, String desc) {
        this.intCode = intCode;
        this.byteCode = byteCode;
        this.longCode = longCode;
        this.stringCode = stringCode;
        this.desc = desc;
    }
}
