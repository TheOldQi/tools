package com.xiafei.tools.common;

import java.awt.*;
import java.io.IOException;

/**
 * <P>Description: 加载字体工具. </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   齐霞飞 </P>
 * <P>CREATE DATE: 2017/11/22</P>
 * <P>UPDATE DATE: 2018/09/29</P>
 *
 * @author qixiafei
 * @version 1.0
 * @since java 1.7.0
 */
public class FontUtils {

    /**
     * 加载字体.
     *
     * @param classPath 编译后相对路径
     * @param style     字体样式 例Font.BOLD -- 粗体,Font.ITALIC - 斜体
     * @param size      字号
     */
    public static Font getFont(final String classPath, final int style, final int size) throws IOException, FontFormatException {
        return Font.createFont(Font.TRUETYPE_FONT, FontUtils.class.getResourceAsStream(classPath)).
                deriveFont(style, 10);
    }
}
