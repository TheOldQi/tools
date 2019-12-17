package com.xiafei.tools.page;

import java.util.List;
import java.util.Map;

/**
 * <P>Description: 可以进行分页查询的Dao基类. </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   齐霞飞 </P>
 * <P>CREATE DATE: 2017/10/16</P>
 * <P>UPDATE DATE: 2017/10/16</P>
 *
 * @param <T> 数据库表持久化对象
 * @author qixiafei
 * @version 1.0
 * @since java 1.7.0
 */
public interface PageAbleDao<T> {

    /**
     * 统计分页查询条件下总条数.
     *
     * @param paramMap 分页查询条件map
     * @return 查询条件下总条数
     */
    Integer getPageCount(Map<String, Object> paramMap);

    /**
     * 查询分页实体数据列表.
     *
     * @param paramMap 分页查询条件
     * @return 查询分页实体数据
     */
    List<T> getPageList(Map<String, Object> paramMap);
}
