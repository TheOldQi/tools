package com.xiafei.tools.barcode.qr;

import com.google.zxing.*;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.EnumMap;
import java.util.Map;

/**
 * <P>Description: 二维码解析. </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   齐霞飞 </P>
 * <P>CREATE DATE: 2017/11/25</P>
 * <P>UPDATE DATE: 2017/11/25</P>
 *
 * @author 齐霞飞
 * @version 1.0
 * @since java 1.7.0
 */
public class QrCodeResolver {

    private static final String CHAR_SET = "utf-8";

    private QrCodeResolver() {

    }

    public static void main(String[] args) throws IOException {
        InputStream is = new FileInputStream(new File("D:\\老丁创业公司\\自己\\孖牌\\二维码拦截转发\\微信图片_20190111162452.png"));
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int bufferLen = 1024 << 3;
        byte[] buffer = new byte[bufferLen];
        int remain;
        while ((remain = is.read(buffer, 0, bufferLen)) > 0) {
            bos.write(buffer, 0, remain);
        }
        final String s = resolveFromBytes(bos.toByteArray());
        System.out.println("识别内容：" + s);
    }

    /**
     * 解析字节流中的二维码图片.
     *
     * @param bytes 字节流
     * @return 二维码中包含的信息
     */
    public static String resolveFromBytes(final byte[] bytes) {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes, 0, bytes.length);
        return resolveFromStream(bis);
    }

    /**
     * 解析流中的二维码图片.
     *
     * @param inputStream 二维码图片信息流
     * @return 二维码信息
     */
    public static String resolveFromStream(final InputStream inputStream) {
        try {
            BufferedImage image = ImageIO.read(inputStream);
            return resolveImage(image);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 解析电脑上的二维码图片.
     *
     * @param path 二维码文件全路径
     * @return 二维码信息
     */
    public static String resolveFromPath(final String path) {
        try {
            File file = new File(path);
            BufferedImage image = ImageIO.read(file);
            return resolveImage(image);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String resolveImage(BufferedImage image) throws NotFoundException, FormatException, ChecksumException {
        final QRCodeReader formatReader = new QRCodeReader();
        final LuminanceSource source = new QrCodeResolveHelper(image);
        final Binarizer binarizer = new HybridBinarizer(source);
        final BinaryBitmap binaryBitmap = new BinaryBitmap(binarizer);
        final Map<DecodeHintType, Object> hints = new EnumMap<DecodeHintType, Object>(
                DecodeHintType.class);
        hints.put(DecodeHintType.CHARACTER_SET, CHAR_SET);
        hints.put(DecodeHintType.POSSIBLE_FORMATS, BarcodeFormat.QR_CODE);
        //优化精度
        hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
        return formatReader.decode(binaryBitmap, hints).getText();
    }

}
