package com.example.iso8583.util;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class DES3 {

    public static byte[] encryptDes(byte[] key, byte[] src) {
        try {
            // 创建一个DESKeySpec对象
            DESKeySpec desKey = new DESKeySpec(key);
            // 创建一个密匙工厂
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            // 将DESKeySpec对象转换成SecretKey对象
            SecretKey secretKey = keyFactory.generateSecret(desKey);
            // Cipher对象实际完成解密操作
            Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");
            // 用密匙初始化Cipher对象
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            // 现在，获取数据并加密
            // 正式执行加密操作
            return cipher.doFinal(src);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * des解密
     *
     * @param key
     * @param src
     * @return
     */
    public static byte[] decryptDes(byte[] key, byte[] src) {
        try {
            // DES算法要求有一个可信任的随机数源
            SecureRandom random = new SecureRandom();
            // 创建一个DESKeySpec对象
            DESKeySpec desKey = new DESKeySpec(key);
            // 创建一个密匙工厂
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            // 将DESKeySpec对象转换成SecretKey对象
            SecretKey secretKey = keyFactory.generateSecret(desKey);
            // Cipher对象实际完成解密操作
            Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");
            // 用密匙初始化Cipher对象
            cipher.init(Cipher.DECRYPT_MODE, secretKey, random);
            // 现在，获取数据并加密
            // 正式执行加密操作
            return cipher.doFinal(src);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String encECB3Des(String key, String src) {
        byte[] temp = null;
        byte[] temp1 = null;
        temp1 = encryptDes(SecretUtils.aschex_to_bcdhex(key.substring(0, 16)), SecretUtils.aschex_to_bcdhex(src));
        temp = decryptDes(SecretUtils.aschex_to_bcdhex(key.substring(16, 32)), temp1);
        temp1 = encryptDes(SecretUtils.aschex_to_bcdhex(key.substring(0, 16)), temp);
        return SecretUtils.bcdhex_to_aschex(temp1);
    }

    public static String decECB3Des(String key, String src) {
        byte[] temp2 = decryptDes(SecretUtils.aschex_to_bcdhex(key.substring(0, 16)), SecretUtils.aschex_to_bcdhex(src));
        byte[] temp1 = encryptDes(SecretUtils.aschex_to_bcdhex(key.substring(16, 32)), temp2);
        byte[] dest = decryptDes(SecretUtils.aschex_to_bcdhex(key.substring(0, 16)), temp1);
        return SecretUtils.bcdhex_to_aschex(dest);
    }

    /**
     * 3DES(双倍长) 加密
     *
     * @param key
     * @param src
     * @return
     */
    public static String encryptECB3Des(String key, String src) {
        int len = key.length();
        if (src == null) {
            return null;
        }
        if (src.length() % 16 != 0) {
            return null;
        }
        if (len == 32) {
            StringBuilder outData = new StringBuilder();
            String str = "";
            for (int i = 0; i < src.length() / 16; i++) {
                str = src.substring(i * 16, (i + 1) * 16);
                outData.append(encECB3Des(key, str));
            }
            return outData.toString();
        } else {
            System.err.println("key length is not 32");
        }
        return null;
    }

    /**
     * 3DES(双倍长) 解密
     *
     * @param key
     * @param src
     * @return
     */
    public static String decryptECB3Des(String key, String src) {
        if (key == null || src == null) {
            return null;
        }
        if (src.length() % 16 != 0) {
            return null;
        }
        if (key.length() == 32) {
            StringBuilder outData = new StringBuilder();
            String str = "";
            for (int i = 0; i < src.length() / 16; i++) {
                str = src.substring(i * 16, (i + 1) * 16);
                outData.append(decECB3Des(key, str));
            }
            return outData.toString();
        }
        return null;
    }


}
