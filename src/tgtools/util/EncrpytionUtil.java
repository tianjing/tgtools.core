 package tgtools.util;
 
 import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

import tgtools.exceptions.APPRuntimeException;
import tgtools.exceptions.APPWarningException;

import sun.misc.*;
 
 public class EncrpytionUtil
 {
   public static String suffix = "PIEncrypt";
 
   private static byte[] salt = { 84, 94, -60, 118, 67, -20, -55, -70 };
 
   private static byte[] iv = { 79, 76, -120, -36, -61, -72, 91, 50 };
 
   private static final String[] hexDigits = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };
 /**
  * 字节数组转换成16进制的字符串
  * @param b 字节数组
  * @return
  */
   public static String byteArrayToHexString(byte[] b)
   {
     StringBuffer resultSb = new StringBuffer();
     for (int i = 0; i < b.length; i++) {
       resultSb.append(byteToHexString(b[i]));
     }
     return resultSb.toString();
   }
/**
 * 字节转换成16进制字符串 
 * @param b 字节
 * @return
 */
   private static String byteToHexString(byte b) {
     int n = b;
     if (n < 0)
       n = 256 + n;
     int d1 = n / 16;
     int d2 = n % 16;
     return hexDigits[d1] + hexDigits[d2];
   }
 /**
  * 字符串转换成MD5
  * @param str 需要加密的字符串
  * @return
  */
   public static String str2MD5(String str)
   {
     try
     {
       MessageDigest md = MessageDigest.getInstance("MD5");
       return byteArrayToHexString(md.digest(str.getBytes()));
     } catch (NoSuchAlgorithmException e) {
     }
     throw new APPRuntimeException("不支持MD5算法");
   }
 /**
  * 将字符串转换成MD5字节数组
  * @param str  需要加密的字符串
  * @return
  */
   public static byte[] str2MD5Bytes(String str)throws APPWarningException
   {
     try
     {
       MessageDigest md = MessageDigest.getInstance("MD5");
       return md.digest(str.getBytes());
     } catch (NoSuchAlgorithmException e) {
     }
     throw new APPWarningException("不支持MD5算法");
   }
 /**
  * des加密
  * @param p_value 需要加密的文笔
  * @return
  */
   public static String encryptString(String p_value)throws APPWarningException
   {
     try
     {
       BASE64Encoder encoder=new BASE64Encoder();
       Cipher ecipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
       IvParameterSpec ivp = new IvParameterSpec(iv);
       DESKeySpec desKeySpec = new DESKeySpec(salt);
       SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
       SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
       ecipher.init(1, secretKey, ivp);
 
       byte[] utf8 = p_value.getBytes("UTF8");
       byte[] enc = ecipher.doFinal(utf8);
 
       return new String(encoder.encode(enc)) + suffix;
     } catch (Exception e) {
     throw new APPWarningException(e);
     }
   }
 /**
  * des解密
  * @param p_value 需要解密的文本
  * @return
  */
   public static String decryptString(String p_value)throws APPWarningException
   {
     try
     {
    	 BASE64Decoder decoder=new BASE64Decoder();
       Cipher dcipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
       IvParameterSpec ivp = new IvParameterSpec(iv);
       DESKeySpec desKeySpec = new DESKeySpec(salt);
       SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
       SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
       dcipher.init(2, secretKey, ivp);
 
       p_value = p_value.substring(0, p_value.length() - suffix.length());
 
       byte[] dec =decoder.decodeBuffer(p_value);
 
       byte[] utf8 = dcipher.doFinal(dec);
       String result = new String(utf8, "UTF8");
 
       if (result.endsWith("\r\n"))
         result = result.substring(0, result.length() - 2);
       return result; } catch (Exception e) {
     throw new APPWarningException(e);
     }
   }
   public static void main(String[] args)
   {
     try {
       String passwod =encryptString("BQ_SYS123"+"BINFO==");
       System.out.println(passwod);
       System.out.println("DECYM93I26DTArpMtdVTLuklhjvc06aCojhPIEncrypt".substring(3));
     } catch (APPWarningException e) {
       e.printStackTrace();
     }
   }
 }
