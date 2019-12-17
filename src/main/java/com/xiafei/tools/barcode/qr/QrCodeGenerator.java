package com.xiafei.tools.barcode.qr;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.xiafei.tools.common.FontUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;

/**
 * <P>Description: 二维码生成器. </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   齐霞飞 </P>
 * <P>CREATE DATE: 2017/11/25</P>
 * <P>UPDATE DATE: 2018/9/29</P>
 *
 * @author 齐霞飞
 * @version 1.0
 * @since java 1.7.0
 */
@Slf4j
public class QrCodeGenerator {

    private static final int QRCOLOR = 0xFF000000; // 默认是黑色
    private static final int BGWHITE = 0xFFFFFFFF; // 背景颜色

    private static final int DEFAULT_WIDTH = 400;
    private static final int DEFAULT_HEIGHT = 400;

    private static final String FILE_TYPE = "png";
    private static final String FONT_PATH = "/fonts/msyh.ttf";
    private static final int NOTE_FONT_SIZE = 30;
    private static final int NOTE_MARGIN_TOP = 10;
    private static final int NOTE_MARGIN_BOTTOM = 5;

    /**
     * 默认配置.
     */
    private static final Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>() {
        {
            put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);// 设置QR二维码的纠错级别（H为最高级别）具体级别信息
            put(EncodeHintType.CHARACTER_SET, "utf-8");// 设置编码方式
            put(EncodeHintType.MARGIN, 0);

        }
    };

    private QrCodeGenerator() {

    }

    public static void main(String[] args) throws IOException, FontFormatException, WriterException {
        final String classPath = Thread.currentThread().getContextClassLoader().getResource("./").getPath();
        System.out.println(classPath);
        g("测试", new FileInputStream(new File(classPath + "images/zhifubao.png")), "推荐使用支付宝",
                classPath + "temp/支付宝付款码.png", 100, 100);

    }


    /**
     * 生成二维码图片,采用默认宽高，写入输出流.
     *
     * @param data   二维码内数据
     * @param logoIn logo输入流
     * @param note   二维码文字描述
     * @param out    二维码图片输出流
     * @param width  最终图片宽
     * @param height 最终图片高
     * @throws WriterException     生成二维码异常
     * @throws IOException         读取logo图片异常
     * @throws FontFormatException 加载字体失败
     */
    public static void g(final String data, final InputStream logoIn, final String note, final OutputStream out,
                         final Integer width, final Integer height) throws WriterException, IOException, FontFormatException {
        log.info("生成二维码图片输出到输出流，内容={}，文件路径={}", data);
        drawLogoQRCode(data, logoIn, note, out, width == null ? DEFAULT_WIDTH : width,
                height == null ? DEFAULT_HEIGHT : height);
    }

    /**
     * 生成二维码图片,采用默认宽高，写入指定路径文件.
     *
     * @param data     二维码内数据
     * @param logoIn   logo输入流
     * @param note     二维码文字描述
     * @param fullPath 二维码图片输出完整路径
     * @param width    最终图片宽
     * @param height   最终图片高
     * @throws WriterException     生成二维码异常
     * @throws IOException         读取logo图片异常
     * @throws FontFormatException 加载字体失败
     */
    public static void g(final String data, final InputStream logoIn, final String note, final String fullPath,
                         final Integer width, final Integer height) throws WriterException, IOException, FontFormatException {
        log.info("生成二维码图片输出到指定文件路径，内容={}，文件路径={}", data, fullPath);
        if (StringUtils.isBlank(fullPath)) {
            log.error("输出文件路径不能为空");
            throw new IllegalArgumentException("文件路径错误");
        }

        final File file = new File(fullPath);
        if (file.isDirectory()) {
            log.error("路径代表的是文件夹");
            throw new IllegalArgumentException("文件路径错误");
        }
        if (file.exists()) {
            file.delete();
        } else if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        file.createNewFile();
        try (OutputStream out = new FileOutputStream(file)) {
            drawLogoQRCode(data, logoIn, note, out, width == null ? DEFAULT_WIDTH : width,
                    height == null ? DEFAULT_HEIGHT : height);
        }
    }


    /**
     * 生成带logo,带文字的二维码，文字logo采用微软雅黑字体、粗体、字号30.
     *
     * @param data   二维码内数据
     * @param logoIn logo输入流
     * @param note   二维码文字描述
     * @param out    生成的二维码输出流
     * @param width  最终图片宽
     * @param height 最终图片高
     * @throws WriterException     生成二维码异常
     * @throws IOException         读取logo图片异常
     * @throws FontFormatException 加载字体失败
     */
    private static void drawLogoQRCode(final String data, final InputStream logoIn, final String note, final OutputStream out,
                                       final int width, final int height) throws WriterException, IOException, FontFormatException {
        // 根据是否需要备注文字决定图片中二维码占比
        final int qrWidth;
        final int qrHeight;
        if (StringUtils.isNotBlank(note)) {
            // 二维码保持宽高一致，高度由图片总高度去掉备注文字大小减去文字margin得出
            int usableHeight = height - NOTE_MARGIN_TOP - NOTE_MARGIN_BOTTOM;
            qrWidth = qrHeight = width > usableHeight ? usableHeight : width;
        } else {
            qrWidth = qrHeight = width > height ? height : width;
        }

        // 根据数据和宽高生成二维码点阵比特map
        final MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        final BitMatrix bm = multiFormatWriter.encode(data, BarcodeFormat.QR_CODE, qrWidth, qrHeight, hints);

        // 先画出二维码图片
        final BufferedImage qrImage = new BufferedImage(qrWidth, qrHeight, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < qrWidth; x++) {
            for (int y = 0; y < qrHeight; y++) {
                // 设置每个像素点是黑是白
                qrImage.setRGB(x, y, bm.get(x, y) ? QRCOLOR : BGWHITE);
            }
        }

        // 若有logo，在二维码图片上添加logo图片
        if (logoIn != null) {
            // 构建绘图对象
            final Graphics2D logoG = qrImage.createGraphics();
            // 读取Logo图片
            final BufferedImage logo = ImageIO.read(logoIn);
            // 开始绘制logo图片，上下左右各留五分之二
            logoG.drawImage(logo, qrWidth * 2 / 5, qrHeight * 2 / 5, qrWidth / 5, qrHeight / 5, null);
            logoG.dispose();
            logo.flush();
        }

        final BufferedImage result;
        if (StringUtils.isNotBlank(note)) {
            final BufferedImage withNoteImg = new BufferedImage(width,
                    height + NOTE_FONT_SIZE + NOTE_MARGIN_TOP + NOTE_MARGIN_BOTTOM, BufferedImage.TYPE_4BYTE_ABGR);
            final Graphics2D withNoteG = withNoteImg.createGraphics();
            // 画二维码到新的面板
            withNoteG.drawImage(qrImage, (width - qrWidth) / 2, 0, qrImage.getWidth(), qrImage.getHeight(), null);

            // 画文字到新的面板
            withNoteG.setColor(Color.BLACK);
            withNoteG.setFont(FontUtils.getFont(FONT_PATH, Font.BOLD, NOTE_FONT_SIZE)); // 字体、字型、字号

            // 计算文字宽度，超长部分隐藏
            int strWidth = withNoteG.getFontMetrics().stringWidth(note);
            if (strWidth > width) {
                strWidth = width;
            }
            withNoteG.drawString(note, (width / 2) - (strWidth / 2), qrHeight + NOTE_MARGIN_TOP); // 画文字
            withNoteG.dispose();
            withNoteImg.flush();
            result = withNoteImg;
        } else {
            result = qrImage;
        }


        result.flush();
        ImageIO.write(result, "png", out);
    }

}
