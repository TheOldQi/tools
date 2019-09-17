package com.xiafei.tools.common.encrypt.rsa;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * <P>Description: RSA工具类，公私钥来源于参数. </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   齐霞飞 </P>
 * <P>CREATE DATE: 2017/5/27</P>
 * <P>UPDATE DATE: 2017/6/28</P>
 *
 * @author qixiafei
 * @version 0.0.3-SNAPSHOT
 * @since java 1.7.0
 */
@Slf4j
public class RsaUseParam {

    // 签名算法
    private static final String SIGNATURE_ALGORITHM = "SHA1withRSA";
    private static final String SIGNATURE_ALGORITHM_2 = "SHA256WithRSA";
    // cipher 算法
    private static final String CIPHER_ALGORITHM = "RSA/ECB/PKCS1Padding";
    // 钥匙加密算法
    private static final String KEY_ALGORITHM = "Rsa";

    static {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }


    /**
     * 用公钥加密.
     *
     * @param src     原内容，明文
     * @param pubKStr rsa公钥字符串
     * @param keySize 密钥位数
     * @return 加密后的Base64字符串
     */
    public static String encrypt(final String src, final String pubKStr, final int keySize) {

        try {
            byte[] bytes = doEncrypt(src.getBytes("UTF-8"), pubKStr, keySize);
            return Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            log.error("Rsa encrypt failure:src={}", src, e);
        }
        return null;
    }

    /**
     * 使用私钥解密.
     *
     * @param encryptedStr 已经加密的内容Base64字符串
     * @param priKeyStr    私钥字符串
     * @param keySize      密钥位数
     * @return 明文
     */
    public static String decrypt(final String encryptedStr, final String priKeyStr, final int keySize) {
        try {
            byte[] bytes = doDecrypt(Base64.getDecoder().decode(encryptedStr), priKeyStr, keySize);
            return new String(bytes, "UTF-8");
        } catch (Exception e) {
            log.error("Rsa decrypt failure:encryptedStr={}", encryptedStr, e);
        }
        return null;
    }


    /**
     * 使用本系统私钥加签名.
     *
     * @param plain     加签前字符串
     * @param priKeyStr 私钥字符串
     * @return 加签后字符串
     */
    public static String sign(final String plain, final String priKeyStr) {
        try {
            return doSign(plain, SIGNATURE_ALGORITHM, priKeyStr);
        } catch (Exception e) {
            log.error("Rsa sign failure:plain={}", plain, e);
        }
        return null;
    }

    /**
     * 使用本系统私钥加签名（rsa2）.
     *
     * @param plain     加签前字符串
     * @param priKeyStr 私钥字符串
     * @return 加签后字符串
     */
    public static String sign256(final String plain, final String priKeyStr) {
        try {
            return doSign(plain, SIGNATURE_ALGORITHM_2, priKeyStr);
        } catch (Exception e) {
            log.error("Rsa2 sign failure:plain={}", plain, e);
        }
        return null;
    }

    /**
     * 利用第三方公钥验签.
     *
     * @param sign      签名字符串
     * @param plain     签名前字符串
     * @param pubKeyStr rsa公钥字符串
     * @return true - 验签通过，false-验签未通过
     */
    public static boolean verify(final String sign, final String plain, final String pubKeyStr) {
        try {
            return doVerify(sign, plain, pubKeyStr, SIGNATURE_ALGORITHM);
        } catch (Exception e) {
            log.error("Rsa jsCode failure:sign={},plain={},pubKey={}", sign, plain, pubKeyStr, e);
        }
        return false;
    }

    /**
     * 利用第三方公钥验签（rsa2）.
     *
     * @param sign      签名字符串
     * @param plain     签名前字符串
     * @param pubKeyStr rsa公钥字符串
     * @return true - 验签通过，false-验签未通过
     */
    public static boolean verify256(final String sign, final String plain, final String pubKeyStr) {
        try {
            return doVerify(sign, plain, pubKeyStr, SIGNATURE_ALGORITHM_2);
        } catch (Exception e) {
            log.error("Rsa2 jsCode failure:sign={},plain={},pubKey={}", sign, plain, pubKeyStr, e);
        }
        return false;
    }


    /**
     * 用私钥分段解密.
     *
     * @param encryptedData 已加密字节流
     * @param i
     * @return 明文字节流
     */
    private static byte[] doDecrypt(final byte[] encryptedData, final String priKey, final int i) throws Exception {

        // 初始化密码操作器
        final RSAPrivateKey prik = getPrik(priKey);
        final Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM, BouncyCastleProvider.PROVIDER_NAME);
        cipher.init(Cipher.DECRYPT_MODE, prik);
        // 分段处理
        return segmentDeal(cipher, i >>> 3, encryptedData);
    }

    /**
     * 公钥分段加密.
     *
     * @param data    源数据字节流
     * @param pubKStr rsa公钥字符串
     * @param i
     */
    private static byte[] doEncrypt(final byte[] data, final String pubKStr, final int i) throws Exception {
        // 初始化key对象
        final PublicKey publicK = getPubK(pubKStr);
        // 初始化密码操作器
        final Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM, BouncyCastleProvider.PROVIDER_NAME);
        cipher.init(Cipher.ENCRYPT_MODE, publicK);
        // 分段处理
        return segmentDeal(cipher, (i >>> 3) - 11, data);
    }


    /**
     * 执行验签.
     *
     * @param sign      签名字符串
     * @param plain     签名内容明文
     * @param pubKeyStr rsa公钥前缀
     * @param algorithm 签名算法
     * @return true - 验签通过，false-验签失败
     * @throws Exception
     */
    private static boolean doVerify(final String sign, final String plain, final String pubKeyStr, final String algorithm) throws Exception {
        final PublicKey publicK = getPubK(pubKeyStr);
        final Signature signature = Signature.getInstance(algorithm);
        signature.initVerify(publicK);
        signature.update(getContentBytes(plain, "UTF-8"));
        return signature.verify(Base64.getDecoder().decode(sign));

    }

    /**
     * 执行签名.
     *
     * @param plain     待签名明文.
     * @param algorithm 签名算法
     * @param priKeyStr
     * @return 签名后字符串
     * @throws Exception
     */
    private static String doSign(final String plain, final String algorithm, final String priKeyStr) throws Exception {
        final PrivateKey privateK = getPrik(priKeyStr);
        final Signature signature = Signature.getInstance(algorithm);
        signature.initSign(privateK);
        signature.update(getContentBytes(plain, "UTF-8"));
        final byte[] result = signature.sign();
        return Base64.getEncoder().encodeToString(result);

    }

    /**
     * 分段处理.
     *
     * @param cipher    密码操作对象
     * @param blockSize 分段大小
     * @param data      数据字节流
     * @return 处理后的字节流
     */
    private static byte[] segmentDeal(final Cipher cipher, final int blockSize, final byte[] data) throws Exception {
        final int dataLength = data.length;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream(dataLength)) {
            // 对数据分段加密或解密
            for (int offSet = 0; offSet < dataLength; offSet += blockSize) {
                final byte[] cache;
                if (offSet + blockSize < dataLength) {
                    cache = cipher.doFinal(data, offSet, blockSize);
                } else {
                    cache = cipher.doFinal(data, offSet, dataLength - offSet);

                }
                out.write(cache);
            }
            return out.toByteArray();
        }
    }

    /**
     * 使用指定字符集将内容转成字节流.
     *
     * @param content 内容
     * @param charset 字符集名字
     * @return 字节流
     */
    private static byte[] getContentBytes(String content, String charset) {
        if (charset == null || "".equals(charset)) {
            return content.getBytes();
        }
        try {
            return content.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("签名过程中出现错误,指定的编码集不对,您目前指定的编码集是:" + charset);
        }
    }

    /**
     * 获取平台私钥.
     *
     * @param priKeyStr 私钥
     * @return 私钥
     * @throws NoSuchAlgorithmException 找不到此算法异常
     * @throws InvalidKeySpecException  私钥格式校验失败
     */
    private static RSAPrivateKey getPrik(final String priKeyStr) throws InvalidKeySpecException, NoSuchAlgorithmException {
        final RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance(KEY_ALGORITHM).
                generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(priKeyStr)));
        return priKey;
    }

    /**
     * 根据合作方编码拿到对接系统的公钥.
     *
     * @param pubKStr 公钥字符串
     * @return 公钥
     * @throws NoSuchAlgorithmException 找不到此算法异常
     * @throws InvalidKeySpecException  私钥格式校验失败
     */
    private static RSAPublicKey getPubK(final String pubKStr) throws InvalidKeySpecException, NoSuchAlgorithmException {
        final RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance(KEY_ALGORITHM).
                generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(pubKStr)));
        return pubKey;
    }

}
