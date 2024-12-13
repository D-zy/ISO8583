package com.example.iso8583.util;

import org.bouncycastle.asn1.ASN1Sequence;

import java.io.ByteArrayOutputStream;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public class RSAUtils {

    public static final String RSA_TYPE = "RSA/ECB/PKCS1Padding";

    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 245;


    public static byte[] encryptByPublicKey(byte[] data, String publicKeyStr) throws Exception {
        ASN1Sequence sequence = ASN1Sequence.getInstance(SecretUtils.parseHexStr2Byte(publicKeyStr));
        org.bouncycastle.asn1.pkcs.RSAPublicKey rsaPublicKey = org.bouncycastle.asn1.pkcs.RSAPublicKey.getInstance(sequence);

        RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(rsaPublicKey.getModulus(), rsaPublicKey.getPublicExponent());
        KeyFactory factory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = factory.generatePublic(publicKeySpec);

        Cipher cipher = Cipher.getInstance(RSA_TYPE);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        int i = 0;
        while (inputLen - offSet > 0) {
            byte[] cache;
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }

            out.write(cache, 0, cache.length);
            ++i;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }

        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;
    }

    public static String encryptWithRsaPublicKey(String data, String rsaPubKeyString) throws Exception {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(ConvertUtil.hex2Byte(rsaPubKeyString));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        RSAPublicKey publicKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);
        Cipher cipher = Cipher.getInstance("RSA/ECB/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedData = cipher.doFinal(ConvertUtil.hex2Byte(data));
        return ConvertUtil.byte2HexString(encryptedData);
    }


}