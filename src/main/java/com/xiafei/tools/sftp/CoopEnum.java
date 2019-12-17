package com.xiafei.tools.sftp;

/**
 * <P>Description: 合作方枚举. </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   齐霞飞 </P>
 * <P>CREATE DATE: 2018/3/5</P>
 * <P>UPDATE DATE: 2018/3/5</P>
 *
 * @author qixiafei
 * @version 1.0
 * @since java 1.8.0
 */
public enum CoopEnum {

    JIANGXI("FUND_JX", "江西金租系统", CoopTypeEnum.FUND.code, "AGENCY_YX"),
    KERY("CSTM_KRY", "客如云系统", CoopTypeEnum.CSTM.code, "AGENCY_YX");

    /**
     * 合作方编码.
     */
    public final String code;

    /**
     * 合作方描述.
     */
    public final String desc;

    /**
     * 合作方类型.
     */
    public final Byte type;

    /**
     * 合作方为平台分配的系统id.
     */
    public final String systemId;


    CoopEnum(final String code, final String desc, final Byte type, final String systemId) {
        this.code = code;
        this.desc = desc;
        this.type = type;
        this.systemId = systemId;
    }

    /**
     * 根据code值匹配枚举值方法.
     *
     * @param code 枚举编码字段
     * @return code匹配到的枚举值或null
     */
    public static CoopEnum instance(final String code) {
        if (code == null) {
            return null;
        }
        for (CoopEnum e : values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }

        return null;
    }
}
