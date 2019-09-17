package com.xiafei.tools.page;

import java.io.Serializable;
import java.util.List;


/**
 * <P>Description: 分页对象，用于交互. </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   齐霞飞 </P>
 * <P>CREATE DATE: 2017/12/27</P>
 * <P>UPDATE DATE: 2017/12/27</P>
 *
 * @author 齐霞飞
 * @version 1.0
 * @since java 1.8.0
 */
public class Page<T> implements Serializable {


    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * 默认每页数据条数。
     */
    private final static int DEFAULT_PAGE_SIZE = 10;

    /**
     * 每页数据条数。
     */
    private int pageSize = DEFAULT_PAGE_SIZE;

    /**
     * 数据总条数。
     */
    private int totalCount;

    /**
     * 当前页号。
     */
    private int currentPageNo;

    /**
     * 数据列表。
     */
    private List<T> dataList;

    public Page() {
    }

    public Page(int currentPageNo) {
        this.currentPageNo = currentPageNo;
    }

    public Page(int currentPageNo, int pageSize) {
        this.currentPageNo = currentPageNo;
        this.pageSize = pageSize;
    }

    /**
     * 获取开始索引。
     *
     * @return
     */
    public int getStartIndex() {
        return (getCurrentPageNo() - 1) * this.pageSize;
    }

    /**
     * 获取结束索引。
     *
     * @return
     */
    public int getEndIndex() {
        return getCurrentPageNo() * this.pageSize;
    }

    /**
     * 是否首页。
     *
     * @return
     */
    public boolean isFirstPage() {
        return getCurrentPageNo() <= 1;
    }

    /**
     * 是否末页。
     *
     * @return
     */
    public boolean isLastPage() {
        return getCurrentPageNo() >= getTotalPageCount();
    }

    /**
     * 获取下一页的页号。
     *
     * @return 下一页的页号。
     */
    public int getNextPage() {
        if (isLastPage()) {
            return getCurrentPageNo();
        }
        return getCurrentPageNo() + 1;
    }

    /**
     * 获取上一页的页号。
     *
     * @return 上一页的页号。
     */
    public int getPreviousPageNo() {
        if (isFirstPage()) {
            return 1;
        }
        return getCurrentPageNo() - 1;
    }

    /**
     * 获取当前页号。
     *
     * @return 当前页号。
     */
    public int getCurrentPageNo() {
        if (currentPageNo == 0) {
            currentPageNo = 1;
        }
        return currentPageNo;
    }

    /**
     * 获取总页数。
     *
     * @return 总页数。
     */
    public int getTotalPageCount() {
        return totalCount % pageSize == 0 ? totalCount / pageSize : totalCount / pageSize + 1;
    }

    /**
     * 获取总数据条数。
     *
     * @return 总数据条数。
     */
    public int getTotalCount() {
        return this.totalCount;
    }

    /**
     * 设置当前页号。
     *
     * @param currentPageNo 当前页号。
     */
    public void setCurrentPageNo(int currentPageNo) {
        this.currentPageNo = currentPageNo;
    }

    /**
     * 获取每页数据条数。
     *
     * @return 每页数据条数。
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * 设置每页数据条数。
     *
     * @param pageSize 每页数据条数。
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * 判断是否有下一页。
     *
     * @return
     */
    public boolean hasNextPage() {
        return getCurrentPageNo() < getTotalPageCount();
    }

    /**
     * 判断是否有上一页。
     *
     * @return
     */
    public boolean hasPreviousPage() {
        return getCurrentPageNo() > 1;
    }

    /**
     * 获取数据列表。
     *
     * @return 数据列表。
     */
    public List<T> getDataList() {
        return this.dataList;
    }

    /**
     * 设置数据列表。
     *
     * @param dataList
     */
    public void setDataList(List<T> dataList) {
        this.dataList = dataList;
    }

    /**
     * 设置数据总条数。
     *
     * @param totalCount 数据总条数。
     */
    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    @Override
    public String toString() {
        return "Page{" +
                "pageSize=" + pageSize +
                ", totalCount=" + totalCount +
                ", currentPageNo=" + currentPageNo +
                ", dataList=" + dataList +
                '}';
    }
}
