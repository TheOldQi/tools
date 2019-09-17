package com.xiafei.tools.page;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <P>Description: 分页工具类，使用时让分页查询的
 * 1、数据Dao层继承PageAbleDao，该接口已经声明查询分页对象的两个方法，
 * 2、查询条件(Dto/VO)继承PageQueryAble，该父类实现了序列化，声明了分页必须的两个字段（当前页和页大小）
 * 查询条件会在使用时将字段转换成Map，所以mapper中声明方法应该如下：
 * <select id="getPageCount" parameterType="java.util.Map" resultType="java.lang.Integer">
 * <select id="getPageList" parameterType="java.util.Map" resultMap="BaseResultMap">
 * 注：maapper.xml中getPageList方法最后加
 * LIMIT #{low,jdbcType=INTEGER},#{high,jdbcType=INTEGER}
 * low和high都已经计算好
 * 然后直接调用getPage方法返回分页对象. </P>
 * <P>使用之前请在spring的扫包路径中加入"com.virgo.finance.fm.common.spring"</P>
 * <p>一个简短的mapper.xml例子:
 * <!-- 分页查询 start -->
 * <sql id="pageQueryCondition">
 * <where>
 * <if test="varchar != null and varchar != ''">
 * AND COLUMN_NAME like CONCAT('%',#{varchar,jdbcType=VARCHAR},'%')
 * </if>
 * <if test="  dateLow !=null">
 * AND <![CDATA[COLUMN_DATE >= #{dateLow,jdbcType=DATE} ]]>
 * </if>
 * <if test="  dateHigh != null">
 * AND <![CDATA[COLUMN_DATE <= #{dateHigh,jdbcType=DATE} ]]>
 * </if>
 * <if test=" integer != null">
 * AND integer = #{integer,jdbcType=INTEGER}
 * </if>
 * </where>
 * </sql>
 * <p>
 * <select id="getPageCount" parameterType="java.util.Map" resultType="java.lang.Integer">
 * select
 * count(*)
 * from TABLE_NAME
 * <include refid="pageQueryCondition"/>
 * </select>
 * <p>
 * <select id="getPageList" parameterType="java.util.Map" resultMap="BaseResultMap">
 * select
 * <include refid="BASE_COLUMNS"/>
 * from TABLE_NAME
 * <include refid="pageQueryCondition"/>
 * ORDER BY ID DESC
 * LIMIT #{low,jdbcType=INTEGER},#{high,jdbcType=INTEGER}
 * </select>
 * <!-- 分页查询 end --></p>
 * <p>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   齐霞飞 </P>
 * <P>CREATE DATE: 2017/10/16</P>
 * <P>UPDATE DATE: 2017/10/16</P>
 *
 * @author qixiafei
 * @version 1.0
 * @since java 1.7.0
 */
@Component
public class PageUtils {



    /**
     * 获取分页对象.
     *
     * @param queryVO 查询条件
     * @param dao     支持分页查询的Dao
     * @param clazz   分页对象数据列表中包含的实体类Class
     * @param <O>     输出分页对象包含的对象类型
     * @param <T>     查询表对应的持久化对象类型
     * @return 分页数据
     * @throws IllegalAccessException 反正创建对象实例访问权限错误
     * @throws InstantiationException 实例化错误
     */
    public <O, T> Page<O> getPage(final PageQueryAble queryVO, final PageAbleDao<T> dao, final Class<O> clazz)
            throws IllegalAccessException, InstantiationException {
        return getPage(queryVO, dao, clazz, null);
    }

    /**
     * 获取分页对象.
     *
     * @param queryVO  查询条件
     * @param dao      支持分页查询的Dao
     * @param clazz    分页对象数据列表中包含的实体类Class
     * @param extraMap 额外的参数
     * @param <O>      输出分页对象包含的对象类型
     * @param <T>      查询表对应的持久化对象类型
     * @return 分页数据
     * @throws IllegalAccessException 反正创建对象实例访问权限错误
     * @throws InstantiationException 实例化错误
     */
    public <O, T> Page<O> getPage(final PageQueryAble queryVO, final PageAbleDao<T> dao, final Class<O> clazz,
                                  final Map<String, Object> extraMap)
            throws IllegalAccessException, InstantiationException {
//        PageUtils.checkParam(queryVO);
        final Page<O> result = new Page<>();

        // 查询条件 bean to map
        final Map<String, Object> paramMap = bean2MapBean(queryVO);
        PageUtils.setPageParam(queryVO, paramMap);
        if (extraMap != null) {
            paramMap.putAll(extraMap);
        }

        final Integer count = dao.getPageCount(paramMap);
        final List<T> poList = dao.getPageList(paramMap);

        final List<O> pageDataList = new ArrayList<>(poList.size());
        // po 转 dto
        for (T po : poList) {
            O dto = clazz.newInstance();
            BeanUtils.copyProperties(po, dto);
            pageDataList.add(dto);
        }

        result.setTotalCount(count);
        result.setCurrentPageNo(queryVO.getCurrentPageNo());
        result.setPageSize(queryVO.getPageSize());
        result.setDataList(pageDataList);
        return result;
    }

//    /**
//     * 查询分页数据时验证.
//     *
//     * @param queryVO 查询条件
//     */
//    private static void checkParam(final PageQueryAble queryVO) throws BizException {
//
//        if (queryVO == null) {
//            throw new BizException(ErrorCode.PARAMETER_IS_NULL.code, ErrorCode.PARAMETER_IS_NULL.desc);
//        }
//
//        if (queryVO.getPageSize() == null) {
//            throw new BizException(ErrorCode.PARAMETER_ILLEGAL.code, "pageSize不允许为空");
//        }
//
//        if (queryVO.getCurrentPageNo() == null) {
//            throw new BizException(ErrorCode.PARAMETER_ILLEGAL.code, "currentPageNo不允许为空");
//        }
//
//    }

    /**
     * 设置分页查询参数，根据当前页号和页大小设置返回条目起始终点位置.
     *
     * @param searchVO 查询条件
     * @param paramMap 参数Map
     */
    private static void setPageParam(final PageQueryAble searchVO, final Map<String, Object> paramMap) {
        final int pageNo = searchVO.getCurrentPageNo();
        final int pageSize = searchVO.getPageSize();
        if (pageNo != 0 && pageSize != 0) {
            paramMap.put("low", (pageNo - 1) * pageSize);
            paramMap.put("high", pageSize);
        } else {
            paramMap.put("low", 0);
            paramMap.put("high", 0);
        }
    }

    /**
     * 对象转Map
     */
    private static Map<String, Object> bean2MapBean(Object obj) {
        if (obj == null) {
            return null;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();

                // 过滤class属性
                if (!key.equals("class")) {
                    // 得到property对应的getter方法
                    Method getter = property.getReadMethod();
                    Object value = getter.invoke(obj);
                    map.put(key, value);
                }

            }
        } catch (Exception e) {
            System.out.println("transBean2Map Error " + e);
        }
        return map;
    }
}
