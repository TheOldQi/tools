package com.xiafei.tools.img;

import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.IOUtils;

import java.io.*;

/**
 * <P>Description: 图片操作工具. </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   齐霞飞 </P>
 * <P>CREATE AT: 2019/1/3 14:46</P>
 * <P>UPDATE AT: 2019/1/3 14:46</P>
 *
 * @author 齐霞飞
 * @version 1.0
 * @since java 1.8.0
 */
@Slf4j
public class ImgUtils {


    /**
     * 限制图片不大于给定大小.
     *
     * @param srcImg    原图字节数组
     * @param maxKB     图片最大不超过多少kb
     * @param precision 递归压缩精度，这个值越小处理越快，但是可能压缩后的误差就越大
     * @return 不大于maxKb的压缩后的图片字节数组
     */
    public static byte[] limitSize(final byte[] srcImg, final int maxKB, final float precision) throws IOException {
        log.info("将图片处理为不大于{}KB的图片，递归精度={}", maxKB, precision);
        if (precision > 1 || precision <= 0) {
            log.error("压缩精度大于1或小于等于0");
            throw new IllegalArgumentException("压缩精度不能大于1或小于等于0");
        }
        if (srcImg == null) {
            log.error("图片文件字节流为空");
            throw new IllegalArgumentException("图片文件字节流不能为空");
        }
        // kb转换成byte获得图片最大byte数
        final int maxB = maxKB << 10;

        byte[] currentImg = srcImg;
        while (true) {
            int currentSize = currentImg.length;
            log.info("当前图片={}KB", currentSize >>> 10);
            if (currentSize <= maxB) {
                log.info("转化完成");
                return currentImg;
            }
            final ByteArrayInputStream bais = new ByteArrayInputStream(currentImg);
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            compress(bais, baos, precision, 1);
            currentImg = baos.toByteArray();

        }

    }

    private static void compress(final InputStream imgStream, final OutputStream outputStream,
                                 final float rate, final float quality) throws IOException {
        if (rate > 1.0 || quality > 1.0) {
            log.error("图片压缩比率或质量不可以大于1");
            throw new IllegalArgumentException("图片压缩比率或质量大于1");
        }
        Thumbnails.of(imgStream).scale(rate).outputQuality(quality).toOutputStream(outputStream);
    }

    public static void main(String[] args) throws IOException {
        final InputStream is = new FileInputStream(new File("D:\\照片视频等\\20141112宝宝照片\\1--白裙烟雾\\IMG_2923副本.jpg"));
        byte[] bytes = IOUtils.toByteArray(is);
        final OutputStream os = new FileOutputStream(new File("D:\\照片视频等\\20141112宝宝照片\\1--白裙烟雾\\compress-5KB.jpg"));

        byte[] bytes1 = limitSize(bytes, 5, 0.2f);
        os.write(bytes1);
        os.flush();
        is.close();
        os.close();
    }
}
