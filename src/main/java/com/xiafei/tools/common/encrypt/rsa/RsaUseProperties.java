package com.xiafei.tools.common.encrypt.rsa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <P>Description: RSA工具类，公私钥来源于配置文件. </P>
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
@Component
public class RsaUseProperties {

    // 签名算法
    private static final String SIGNATURE_ALGORITHM = "SHA1withRSA";
    private static final String SIGNATURE_ALGORITHM_2 = "SHA256WithRSA";
    // cipher 算法
    private static final String CIPHER_ALGORITHM = "RSA/ECB/PKCS1Padding";
    // 钥匙加密算法
    private static final String KEY_ALGORITHM = "Rsa";
    // Rsa密钥缓存池
    private static final ConcurrentHashMap<String, Key> KEY_MAP = new ConcurrentHashMap<>();
    // 平台私钥在配置文件和缓存池中的key
    private static final String PRIVATE_KEY_MAP_KEY = "pt_pri";
    // 公钥在配置文件和缓存池中的key后缀
    private static final String PUB_KEY_SUFFIX = "_pub";

    @Resource
    private Environment environment;

    @PostConstruct
    public void initPrik() {
        try {
            loadPrik();
            log.info("平台私钥加载成功");
        } catch (Throwable e) {
            log.error("平台私钥加载失败，请查找原因");
        }
    }

    /**
     * 用公钥加密.
     *
     * @param src     原内容，明文
     * @param idCode  标识编码
     * @param keySize rsa公钥位数
     * @return 加密后的Base64字符串
     */
    public String encrypt(final String src, final String idCode, final int keySize) {

        try {
            byte[] bytes = doEncrypt(src.getBytes("UTF-8"), idCode, keySize);
            return Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            log.error("Rsa encrypt failure:src={}", src, e);
        }
        return null;
    }

    /**
     * 使用本系统私钥解密.
     *
     * @param encryptedStr 已经加密的内容Base64字符串
     * @param keySize      rsa密钥位数
     * @return 明文
     */
    public String decrypt(String encryptedStr, final int keySize) {
        try {
            byte[] bytes = doDecrypt(Base64.getDecoder().decode(encryptedStr), keySize);
            return new String(bytes, "UTF-8");
        } catch (Exception e) {
            log.error("Rsa decrypt failure:encryptedStr={}", encryptedStr, e);
        }
        return null;
    }


    /**
     * 使用本系统私钥加签名.
     *
     * @param plain 加签前字符串
     * @return 加签后字符串
     */
    public String sign(final String plain) {
        try {
            return doSign(plain, SIGNATURE_ALGORITHM);
        } catch (Exception e) {
            log.error("Rsa sign failure:plain={}", plain, e);
        }
        return null;
    }

    /**
     * 使用本系统私钥加签名（rsa2）.
     *
     * @param plain 加签前字符串
     * @return 加签后字符串
     */
    public String sign256(final String plain) {
        try {
            return doSign(plain, SIGNATURE_ALGORITHM_2);
        } catch (Exception e) {
            log.error("Rsa2 sign failure:plain={}", plain, e);
        }
        return null;
    }

    /**
     * 利用第三方公钥验签.
     *
     * @param sign   签名字符串
     * @param plain  签名前字符串
     * @param preKey rsa配置前缀
     * @return true - 验签通过，false-验签未通过
     */
    public boolean verify(final String sign, final String plain, final String preKey) {
        try {
            return doVerify(sign, plain, preKey, SIGNATURE_ALGORITHM);
        } catch (Exception e) {
            log.error("Rsa jsCode failure:sign={},plain={},coopCode={}", sign, plain, preKey, e);
        }
        return false;
    }

    /**
     * 利用第三方公钥验签（rsa2）.
     *
     * @param sign   签名字符串
     * @param plain  签名前字符串
     * @param preKey rsa配置前缀
     * @return true - 验签通过，false-验签未通过
     */
    public boolean verify256(final String sign, final String plain, final String preKey) {
        try {
            return doVerify(sign, plain, preKey, SIGNATURE_ALGORITHM_2);
        } catch (Exception e) {
            log.error("Rsa2 jsCode failure:sign={},plain={},coopCode={}", sign, plain, preKey, e);
        }
        return false;
    }


    /**
     * 用私钥分段解密.
     *
     * @param encryptedData 已加密字节流
     * @param keySize
     * @return 明文字节流
     */
    private byte[] doDecrypt(final byte[] encryptedData, final int keySize) throws Exception {

        // 初始化密码操作器
        final RSAPrivateKey prik = getPrik();
        final Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, prik);
        // 分段处理
        return segmentDeal(cipher, keySize >>> 3, encryptedData);
    }

    /**
     * 公钥分段加密.
     *
     * @param data    源数据字节流
     * @param preKey  rsa配置前缀
     * @param keySize
     */
    private byte[] doEncrypt(final byte[] data, final String preKey, final int keySize) throws Exception {
        // 初始化key对象
        final PublicKey publicK = getPubK(preKey);
        // 初始化密码操作器
        final Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicK);
        // 分段处理
        return segmentDeal(cipher, (keySize >>> 3) - 11, data);
    }


    /**
     * 执行验签.
     *
     * @param sign      签名字符串
     * @param plain     签名内容明文
     * @param preKey    rsa配置前缀
     * @param algorithm 签名算法
     * @return true - 验签通过，false-验签失败
     * @throws Exception
     */
    private boolean doVerify(final String sign, final String plain, final String preKey, final String algorithm) throws Exception {
        final PublicKey publicK = getPubK(preKey);
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
     * @return 签名后字符串
     * @throws Exception
     */
    private String doSign(final String plain, final String algorithm) throws Exception {
        final PrivateKey privateK = getPrik();
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
     * @return 私钥
     * @throws NoSuchAlgorithmException 找不到此算法异常
     * @throws InvalidKeySpecException  私钥格式校验失败
     */
    private RSAPrivateKey getPrik() throws InvalidKeySpecException, NoSuchAlgorithmException {
        if (KEY_MAP.containsKey(PRIVATE_KEY_MAP_KEY)) {
            return (RSAPrivateKey) KEY_MAP.get(PRIVATE_KEY_MAP_KEY);
        } else {
            synchronized (KEY_MAP) {
                if (KEY_MAP.containsKey(PRIVATE_KEY_MAP_KEY)) {
                    return (RSAPrivateKey) KEY_MAP.get(PRIVATE_KEY_MAP_KEY);
                } else {
                    loadPrik();
                    return (RSAPrivateKey) KEY_MAP.get(PRIVATE_KEY_MAP_KEY);
                }
            }
        }
    }

    /**
     * 根据合作方编码拿到对接系统的公钥.
     *
     * @param idCode 标识 用于找配置
     * @return 公钥
     * @throws NoSuchAlgorithmException 找不到此算法异常
     * @throws InvalidKeySpecException  私钥格式校验失败
     */
    private RSAPublicKey getPubK(final String idCode) throws InvalidKeySpecException, NoSuchAlgorithmException {
        final String key = idCode.concat(PUB_KEY_SUFFIX);
        if (KEY_MAP.containsKey(key)) {
            return (RSAPublicKey) KEY_MAP.get(key);
        } else {
            synchronized (KEY_MAP) {
                if (KEY_MAP.containsKey(key)) {
                    return (RSAPublicKey) KEY_MAP.get(key);
                } else {
                    loadPubK(key);
                    return (RSAPublicKey) KEY_MAP.get(key);

                }
            }
        }
    }

    /**
     * 加载私钥.
     *
     * @throws NoSuchAlgorithmException 找不到此算法异常
     * @throws InvalidKeySpecException  私钥格式校验失败
     */
    private RSAPrivateKey loadPrik() throws NoSuchAlgorithmException, InvalidKeySpecException {
        final String priKStr = environment.getProperty(PRIVATE_KEY_MAP_KEY);
        final RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance(KEY_ALGORITHM).
                generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(priKStr)));
        KEY_MAP.putIfAbsent(PRIVATE_KEY_MAP_KEY, priKey);
        return priKey;
    }

    /**
     * 加载公钥.
     *
     * @throws NoSuchAlgorithmException 找不到此算法异常
     * @throws InvalidKeySpecException  公钥格式校验失败
     */
    private RSAPublicKey loadPubK(final String key) throws NoSuchAlgorithmException, InvalidKeySpecException {
        final String pubKStr = environment.getProperty(key);
        final RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance(KEY_ALGORITHM).
                generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(pubKStr)));
        KEY_MAP.putIfAbsent(key, pubKey);
        return pubKey;
    }
}
