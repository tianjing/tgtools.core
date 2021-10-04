package tgtools.util;

import tgtools.exceptions.APPErrorException;
import tgtools.exceptions.APPRuntimeException;
import tgtools.exceptions.APPWarningException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * @author 田径
 * @title 加密帮助类
 * @description 提供MD5 DES AES加密/解密
 * @date 2017/10/19
 */
public class EncrpytionUtil {

    /**
     * HEX 转换时常量
     */
    private static final String[] HEX_DIGITS = {
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"
    };

    /**
     * 加密类型AES
     */
    private static final String AES = "AES";

    /**
     * 加密模式AES/CBC
     */
    private static final String AES_CBC_CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";

    /**
     * 默认16位 IV常量
     */
    private static final byte[] AES_IVPARAMETERS = new byte[]{
            1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16
    };

    /**
     * DES 加密尾标识
     */
    public static String DES_SUFFIX = "PIEncrypt";

    /**
     * DES 的 salt
     */
    private static byte[] DES_SALT = {
            84, 94, -60, 118, 67, -20, -55, -70
    };

    /**
     * Field description
     */
    private static byte[] DES_IV = {
            79, 76, -120, -36, -61, -72, 91, 50
    };

    /**
     * Field description
     */
    private static org.apache.commons.codec.binary.Base64 m_Base64 = new org.apache.commons.codec.binary.Base64();

    /**
     * 字节数组转换成16进制的字符串
     *
     * @param b 字节数组
     *
     * @return
     */
    public static String byteArrayToHexString(byte[] b) {
        StringBuffer resultSb = new StringBuffer();

        for (int i = 0; i < b.length; i++) {
            resultSb.append(byteToHexString(b[i]));
        }

        return resultSb.toString();
    }

    /**
     * 字节转换成16进制字符串
     *
     * @param b 字节
     *
     * @return
     */
    private static String byteToHexString(byte b) {
        int n = b;

        if (n < 0) {
            n = 256 + n;
        }

        int d1 = n / 16;
        int d2 = n % 16;

        return HEX_DIGITS[d1] + HEX_DIGITS[d2];
    }

    /**
     * AES CBC 解密
     *
     * @param pContent      待解密内容
     * @param pKey         解密密钥
     * @param pIvParameter IV 变量（byte[16]）
     *
     * @return
     *
     * @throws APPErrorException
     */
    public static byte[] decodeCbcAes(byte[] pContent, SecretKey pKey, byte[] pIvParameter) throws APPErrorException {
        try {
            IvParameterSpec ivParameterSpec = new IvParameterSpec(pIvParameter);
            Cipher cipher = Cipher.getInstance(AES_CBC_CIPHER_ALGORITHM);

            cipher.init(Cipher.DECRYPT_MODE, pKey, ivParameterSpec);

            return cipher.doFinal(pContent);
        } catch (Exception e) {
            throw new APPErrorException("AES CBC 加密失败", e);
        }
    }

    /**
     * des解密
     *
     * @param pValue 需要解密的文本
     *
     * @return
     *
     * @throws APPWarningException
     */
    public static String decryptString(String pValue) throws APPWarningException {
        try {
            Cipher dcipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            IvParameterSpec ivp = new IvParameterSpec(DES_IV);
            DESKeySpec desKeySpec = new DESKeySpec(DES_SALT);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(desKeySpec);

            dcipher.init(2, secretKey, ivp);
            pValue = pValue.substring(0, pValue.length() - DES_SUFFIX.length());

            byte[] dec = m_Base64.decode(pValue);
            byte[] utf8 = dcipher.doFinal(dec);
            String result = new String(utf8, "UTF8");

            if (result.endsWith(StringUtil.NEW_LINE_WINDOWS)) {
                result = result.substring(0, result.length() - 2);
            }

            return result;
        } catch (Exception e) {
            throw new APPWarningException(e);
        }
    }

    /**
     * AES CBC 加密
     *
     * @param pContent     需要加密的内容
     * @param pKey         加密密码
     * @param pIvParameter IV 变量（byte[16]）
     *
     * @return
     *
     * @throws APPErrorException
     */
    public static byte[] encodeCbcAes(byte[] pContent, SecretKey pKey, byte[] pIvParameter)
            throws APPErrorException {
        try {
            IvParameterSpec ivParameterSpec = new IvParameterSpec(pIvParameter);
            Cipher cipher = Cipher.getInstance(AES_CBC_CIPHER_ALGORITHM);

            cipher.init(Cipher.ENCRYPT_MODE, pKey, ivParameterSpec);

            return cipher.doFinal(pContent);
        } catch (Exception e) {
            throw new APPErrorException("AES CBC 加密失败", e);
        }
    }

    /**
     * des加密
     *
     * @param pValue 需要加密的文笔
     *
     * @return
     *
     * @throws APPWarningException
     */
    public static String encryptString(String pValue) throws APPWarningException {
        try {
            Cipher ecipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            IvParameterSpec ivp = new IvParameterSpec(DES_IV);
            DESKeySpec desKeySpec = new DESKeySpec(DES_SALT);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(desKeySpec);

            ecipher.init(1, secretKey, ivp);

            byte[] utf8 = pValue.getBytes("UTF8");
            byte[] enc = ecipher.doFinal(utf8);

            return new String(m_Base64.encode(enc)) + DES_SUFFIX;
        } catch (Exception e) {
            throw new APPWarningException(e);
        }
    }

    public static void main(String[] args) {
        try {
            String passwod = encryptString("BQ_SYS123" + "BINFO==");

            System.out.println(passwod);
            System.out.println("DECYM93I26DTArpMtdVTLuklhjvc06aCojhPIEncrypt".substring(3));

            String key = "advgbhnjmkiuytre";
            String con =
                    "飞溅的水会计法卡戴珊解放军阿克拉大数据分FJKDASJFKLAJ;FDJA;KSDJFKJJFKDSJFLKADJSLFJfjkdsjfkldasjfkldjaslfjasldjf厘卡睡觉了副科级啊第三轮克][]';';';';',,.己复礼卡戴珊金克拉大数据法兰姬大司空立即付款鲁大师";

            con += con;
            con += con;
            con += con;

            String secr = strEncodeCbcAes(con, key);
            String res = strDecodeCbcAes(secr, key);

            System.out.println("11:" + secr);
            System.out.println("22:" + con.equals(res));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 字符串转换成MD5
     *
     * @param str 需要加密的字符串
     *
     * @return
     */
    public static String str2MD5(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");

            return byteArrayToHexString(md.digest(str.getBytes()));
        } catch (NoSuchAlgorithmException e) {
        }

        throw new APPRuntimeException("不支持MD5算法");
    }

    /**
     * 将字符串转换成MD5字节数组
     *
     * @param str 需要加密的字符串
     *
     * @return
     *
     * @throws APPWarningException
     */
    public static byte[] str2MD5Bytes(String str) throws APPWarningException {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");

            return md.digest(str.getBytes());
        } catch (NoSuchAlgorithmException e) {
        }

        throw new APPWarningException("不支持MD5算法");
    }

    /**
     * AES CBC 解密
     *
     * @param pContent   待解密内容
     * @param pKeyWord 解密密钥
     *
     * @return
     *
     * @throws APPErrorException
     */
    public static String strDecodeCbcAes(String pContent, String pKeyWord) throws APPErrorException {
        try {
            byte[] content1 = m_Base64.decode(pContent);
            KeyGenerator kgen = KeyGenerator.getInstance(AES);

            kgen.init(128, new SecureRandom(pKeyWord.getBytes()));

            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, AES);

            return new String(decodeCbcAes(content1, key, AES_IVPARAMETERS));
        } catch (Exception e) {
            if (e instanceof APPErrorException) {
                throw (APPErrorException) e;
            }

            throw new APPErrorException("AES CBC 加密失败", e);
        }
    }

    /**
     * AES CBC 加密
     *
     * @param pContent   需要加密的内容
     * @param pKeyWord 加密密码
     *
     * @return
     *
     * @throws APPErrorException
     */
    public static String strEncodeCbcAes(String pContent, String pKeyWord) throws APPErrorException {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance(AES);

            kgen.init(128, new SecureRandom(pKeyWord.getBytes()));

            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, AES);

            // 创建密码器
            Cipher cipher = Cipher.getInstance(AES_CBC_CIPHER_ALGORITHM);
            byte[] byteContent = pContent.getBytes("utf-8");

            return m_Base64.encodeToString(encodeCbcAes(byteContent, key, AES_IVPARAMETERS));
        } catch (Exception e) {
            if (e instanceof APPErrorException) {
                throw (APPErrorException) e;
            }

            throw new APPErrorException("AES CBC 加密失败", e);
        }
    }
}

