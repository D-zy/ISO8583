package com.example.iso8583.util

import java.io.UnsupportedEncodingException

object ParseUtils {
    @Throws(UnsupportedEncodingException::class)
    @JvmStatic
    fun main(args: Array<String>) {
//        String s =
//        "007660116F00170110303801000E80020030000000000500000000004613380111060017373331303837323137303031313735343235303031313030303030310012910AC426FECE97B065173030";
//        Map<String, String> parse48 = parseFieldSignIn48(ConvertUtil.hex2Byte
//        ("030C53595354454D040C53595354454D"));
//        Map<String, String> parse48 = parseField(ConvertUtil.hex2Byte(
//                "030653595354454D04085445524D494E414C"));
//        System.out.println(parse48);
//        String a = "00020101021226330015sg.com.dash.www0110200000470027560014A00000076200010109sg" +
//                ".lq.www02154000351000000010302135204539953037025406134" +
//                ".505802SG5921A" +
//                "-Kdb_0652107050000016009Singapore6106123456622901252021090906024386976655068630435D3";
//        Map<String, String> stringStringMap = parseTest(a);
        val stringStringMap1 = parseField60("00null")
        println(stringStringMap1.toString())
    }

    /**
     * 20303030303231303438343332303032303235383206313233353538
     * 索引01：sn号   索引02：批次号
     */
    fun parseField60(f: String): Map<String, String> {
        val res: MutableMap<String, String> = HashMap()
        var index = 0 //索引
        var key = 0
        while (index < f.length) {
            key++
            val i = f.substring(index, index + 2).toInt()
            val length = i * 2
            val nextIndex = index + length + 2
            val value = f.substring(index + 2, nextIndex)
            res[key.toString().padStart(2, '0')] = ConvertUtil.asciiToString(value)
            index = nextIndex
        }
        return res
    }

    /**
     * 签到时结算标记
     */
    fun parseFieldSignIn48(b: ByteArray): Map<String, String> {
        val res: MutableMap<String, String> = HashMap()
        var pos = 0
        if (b.isNotEmpty()) {
            while (pos < b.size) {
                val tag = ConvertUtil.byte2HexString(byteArrayOf(b[pos]))
                val lengthHex = ConvertUtil.byte2HexString(byteArrayOf(b[pos + 1]))
                val length = lengthHex.toInt(16) / 2
                val value = ByteArray(length)
                System.arraycopy(b, pos + 2, value, 0, length)
                val valueStr = ConvertUtil.byte2HexString(value)
                res[tag] = valueStr
                pos += length + 2
            }
        }
        return res
    }

    fun parseField(b: ByteArray): Map<String, String> {
        val res: MutableMap<String, String> = HashMap()
        var pos = 0
        if (b.isNotEmpty()) {
            while (pos < b.size) {
                val tag = ConvertUtil.byte2HexString(byteArrayOf(b[pos]))
                val lengthHex = ConvertUtil.byte2HexString(byteArrayOf(b[pos + 1]))
                val length = lengthHex.toInt(16)
                val value = ByteArray(length)
                System.arraycopy(b, pos + 2, value, 0, length)
                val valueStr = ConvertUtil.byte2HexString(value)
                res[tag] = valueStr
                pos += length + 2
            }
        }
        return res
    }

    /**
     * 00 02 01
     * 01 02 12
     * 26 33 0015sg.com.dash.www01102000004700
     * 27 56 0014A00000076200010109sg.lq.www0215400035100000001030213
     * 52 04 5399
     * 53 03 702
     * 54 05 80.48
     * 58 02 SG
     * 59 21 A-Kdb_065210705000001
     * 60 09 Singapore
     * 61 06 123456
     * 62 29 01252021090903273324776155844
     * 63 04 E154
     */
    fun parseTest(str: String): Map<String, String> {
        val res: MutableMap<String, String> = HashMap()
        var pos = 0
        while (pos < str.length) {
            val tag = str.substring(pos, pos + 2)
            val length = str.substring(pos + 2, pos + 4).toInt()
            val value = str.substring(pos + 4, pos + 4 + length)
            res[tag] = value
            pos += 2 + length + 2
        }
        return res
    }
}