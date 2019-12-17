package com.xiafei.tools.barcode.qr;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xiafei.tools.httpclient.HttpClientPool;
import com.xiafei.tools.httpclient.StringResponseHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * <P>Description: http://jiema.wwei.cn/平台偷偷解码. </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   齐霞飞 </P>
 * <P>CREATE AT: 2019/1/14 18:08</P>
 * <P>UPDATE AT: 2019/1/14 18:08</P>
 *
 * @author 齐霞飞
 * @version 1.0
 * @since java 1.8.0
 */
@Slf4j
public class WweiQrResolver {

    private static final String DOMAIN = "http://jiema.wwei.cn";
    private static final String UA = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36";

    /**
     * 解析二维码图片.
     *
     * @param fileName  文件名
     * @param fileBytes 文件字节数组
     * @return 解析二维码内容.
     * @throws IOException
     */
    public static String resolve(final String fileName, final byte[] fileBytes) throws IOException {
        final CloseableHttpClient httpClient = HttpClientPool.getHttpClient();

        final String uri = getUri(httpClient);
        final String url = DOMAIN + uri;

        //构建multipartEntity对象
        final MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
        entityBuilder.setCharset(Charset.forName("utf-8"));
        entityBuilder.addTextBody("id", "WU_FILE_0", ContentType.TEXT_PLAIN);
        entityBuilder.addTextBody("name", fileName, ContentType.TEXT_PLAIN);
        entityBuilder.addTextBody("size", String.valueOf(fileBytes.length), ContentType.TEXT_PLAIN);
        entityBuilder.addBinaryBody("file", fileBytes, ContentType.MULTIPART_FORM_DATA, fileName);

        // multipart/form-data不能指定请求头，因为我们不知道边界怎么设置，由工具去自动计算及设置
        final HttpPost post = new HttpPost(url);
        post.setEntity(entityBuilder.build());
        post.setHeader("User-Agent", UA);
        post.setHeader("Access-Sign", "*");
        post.setHeader("Referer", "http://jiema.wwei.cn");
        final String ret;
        try {
            ret = HttpClientPool.getHttpClient().execute(post, new StringResponseHandler());
        } catch (IOException e) {
            log.error("调用http异常", e);
            throw new RuntimeException("调用http异常");
        }

        log.info("微微二维码识别返回={}", ret);
        JSONObject jsonObject = JSON.parseObject(ret);
        if (jsonObject == null) {
            log.error("微微网站返回信息错误");
            throw new RuntimeException("微微网站服务不稳定");
        }
        return jsonObject.getString("data");
    }

    /**
     * 模拟页面打开获取上传文件后缀.
     *
     * @param httpClient
     * @return
     * @throws IOException
     */
    private static String getUri(final CloseableHttpClient httpClient) throws IOException {
        final HttpGet get = new HttpGet(DOMAIN);
        get.setHeader("User-Agent", UA);
        final String pageStr = httpClient.execute(get, new StringResponseHandler());
        final String keyWord = "upurl= ";
        int offset = pageStr.indexOf(keyWord) + keyWord.length() + 1;
        final StringBuilder sb = new StringBuilder();
        final char[] chars = pageStr.toCharArray();
        for (; offset < pageStr.length(); offset++) {
            char aChar = chars[offset];
            if (aChar == '"') {
                break;
            }
            sb.append(chars[offset]);
        }
        return sb.toString();
    }

}
