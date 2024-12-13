package com.example.iso8583.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * DES 加解密
 */
public class DesUtils {

    private final static String CIPHER_ALGORITHM = "DES/ECB/NoPadding";

    public static String encryptDoubleDes(String value, String key) throws Exception {
        return ConvertUtil.byte2HexString(encryptDoubleDes(ConvertUtil.hex2Byte(value), ConvertUtil.hex2Byte(key)));
    }

    public static String decryptDoubleDes(String value, String key) throws Exception {
        return ConvertUtil.byte2HexString(decryptDoubleDES(ConvertUtil.hex2Byte(value), ConvertUtil.hex2Byte(key)));
    }

    /**
     * 双倍长des加密
     */
    public static byte[] encryptDoubleDes(byte[] value, byte[] key) throws Exception {
        byte[] key1 = new byte[8];
        System.arraycopy(key, 0, key1, 0, 8);
        byte[] key2 = new byte[8];
        System.arraycopy(key, 8, key2, 0, 8);
        //使用密钥的前16字节，对数据DATA进行加密，得到加密的结果TMP1;
        byte[] temp1 = encryptDES(value, key1);
        //使用密钥的后16字节，对第一的计算结果TMP1，进行解密，得到解密的结果TMP2
        byte[] temp2 = decryptDES(temp1, key2);
        //再次使用密钥的前16字节，对第二次的计算结果TMP2，进行加密，得到加密的结果DEST。DEST就为最终的结果
        return encryptDES(temp2, key1);
    }

    /**
     * 双倍长des解密
     */
    public static byte[] decryptDoubleDES(byte[] decryptString, byte[] key)
            throws Exception {
        byte[] key1 = new byte[8];
        System.arraycopy(key, 0, key1, 0, 8);
        byte[] key2 = new byte[8];
        System.arraycopy(key, 8, key2, 0, 8);
        byte[] temp1 = decryptDES(decryptString, key1);
        byte[] temp2 = encryptDES(temp1, key2);
        return decryptDES(temp2, key1);
    }

    /**
     * @param encryptString 需要加密的明文
     * @param encryptKey    秘钥
     * @return 加密后的密文
     */
    public static byte[] encryptDES(byte[] encryptString, byte[] encryptKey) throws Exception {
        // 实例化IvParameterSpec对象，使用指定的初始化向量
        // 实例化SecretKeySpec类，根据字节数组来构造SecretKey
        SecretKeySpec key = new SecretKeySpec(encryptKey, "DES");
        // 创建密码器
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        // 用秘钥初始化Cipher对象
        cipher.init(Cipher.ENCRYPT_MODE, key);
        // 执行加密操作
        return cipher.doFinal(encryptString);
    }

    /****
     *
     * @param decryptString 密文
     * @param decryptKey 解密密钥
     */
    public static byte[] decryptDES(byte[] decryptString, byte[] decryptKey) throws Exception {

        // 实例化IvParameterSpec对象，使用指定的初始化向量
        // 实例化SecretKeySpec类，根据字节数组来构造SecretKey
        SecretKeySpec key = new SecretKeySpec(decryptKey, "DES");
        // 创建密码器
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        // 用秘钥初始化Cipher对象
        cipher.init(Cipher.DECRYPT_MODE, key);
        // 执行解密操作

        return cipher.doFinal(decryptString);
    }

}