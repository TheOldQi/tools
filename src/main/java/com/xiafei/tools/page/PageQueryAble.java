package com.xiafei.tools.page;

import java.io.Serializable;

/**
 * <P>Description: 可分页查询的查询条件对象父类. </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   齐霞飞 </P>
 * <P>CREATE DATE: 2017/10/13</P>
 * <P>UPDATE DATE: 2017/10/13</P>
 *
 * @author qixiafei
 * @version 1.0
 * @since java 1.7.0
 */
public class PageQueryAble implements Serializable {

    private static final long serialVersionUID = 1633741903998913906L;
    /**
     * 当前页码，从1开始.
     */
    private Integer currentPageNo;

    /**
     * 一页包含的数据条数
     */
    private Integer pageSize;

    public final Integer getCurrentPageNo() {
        return currentPageNo;
    }

    public final void setCurrentPageNo(final Integer currentPageNo) {
        this.currentPageNo = currentPageNo;
    }

    public final Integer getPageSize() {
        return pageSize;
    }

    public final void setPageSize(final Integer pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public String toString() {
        return "PageQueryable{" +
                "currentPageNo=" + currentPageNo +
                ", pageSize=" + pageSize +
                '}';
    }
}
