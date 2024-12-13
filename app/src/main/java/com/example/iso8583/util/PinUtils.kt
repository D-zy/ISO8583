package com.example.iso8583.util

import java.nio.charset.StandardCharsets
import kotlin.random.Random

object PinUtils {

    /*加密*/
    fun encrypt(pin: String): String {
        val pin1 = (ConvertUtil.toHex(pin.length, 2) + pin).padEnd(16, 'F')
        var hexPin2 = ConvertUtil.byte2HexString(pin1.toByteArray(StandardCharsets.UTF_8))
        for (i in 0 until 512 - hexPin2.length) {
            hexPin2 += Integer.toHexString(Random.nextInt(1, 16))
        }
        return RSAUtils.encryptWithRsaPublicKey(hexPin2, SpPos.pinPublicKey)
    }

    /*解密*/
    fun encrypt(pan: String, pin: String, pinPlainKey: String): String {
        val pinBlock = calculatePinBlock(pin, pan)
        val encryptedPin = DES3.encryptECB3Des(pinPlainKey, pinBlock)
        println("key(encrpt_pin):$pinPlainKey")
        return encryptedPin
    }

    private fun calculatePinBlock(pin: String, pan: String): String? {
        val paddedPIN = (ConvertUtil.toHex(pin.length, 2) + pin).padEnd(16, 'F')
        val paddedPAN = (if (pan.length > 13) pan.substring(pan.length - 13, pan.length - 1) else pan.substring(0, pan.length - 1))
            .padStart(16, '0')
        println("paddedPIN: $paddedPIN")
        println("paddedPAN: $paddedPAN")
        return SecretUtils.xor(paddedPIN, paddedPAN)
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val downloadKey = "0F019763FFEF3AC529EFD302BBE2AAD2" //0F019763FFEF3AC529EFD302BBE2AAD2
        val pan = "5457210001000860"
        val pin = "1234"
        // 2. 使用两个分量亦或得到终端TMK
        val com1 = "1234567890ABCDEF1234567890ABCDEF"
        val com2 = "01234567891234560123456789123456"
        val tmk = SecretUtils.xor(com1, com2)
        println("TMK(传输密钥):$tmk")
        // 3. 使用终端TMK解密终端PIN KEY
        val pinKey = DES3.decryptECB3Des(tmk, downloadKey)
        println("pin Key:$pinKey")
        // 4. 使用终端PIN KEY加密终端PIN
        val pinBlock = calculatePinBlock(pin, pan)
        println("pinBlock:$pinBlock")
        val encryptedPin = DES3.encryptECB3Des(pinKey, pinBlock)
        println("encryptedPin:$encryptedPin")
    }

}