package com.xiafei.tools.sftp;

/**
 * <P>Description: 合作方类型枚举. </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   齐霞飞 </P>
 * <P>CREATE DATE: 2018/3/5</P>
 * <P>UPDATE DATE: 2018/3/5</P>
 *
 * @author qixiafei
 * @version 1.0
 * @since java 1.8.0
 */
public enum CoopTypeEnum {

    FUND((byte) 0, "资金方"),
    CSTM((byte) 1, "客户方");

    /**
     * 合作方类型编码.
     */
    public final Byte code;

    /**
     * 合作方类型描述.
     */
    public final String desc;

    CoopTypeEnum(final Byte code, final String desc) {
        this.code = code;
        this.desc = desc;
    }
}
