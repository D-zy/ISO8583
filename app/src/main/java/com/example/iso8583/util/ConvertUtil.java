package com.example.iso8583.util;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConvertUtil {

    static final byte[] HEX_TABLE = {0, 0x1, 0x2, 0x3, 0x4, 0x5, 0x6, 0x7, 0x8, 0x9, 0xA, 0xB, 0xC, 0xD, 0xE, 0xF};
    static final char[] HEX_CHAR_TABLE = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};


    /**
     * 将16进制转换为二进制
     *
     * @param hexString
     * @return
     */
    public static String hexString2binaryString(String hexString) {
        //16进制转10进制
        BigInteger sint = new BigInteger(hexString, 16);
        //10进制转2进制
        //字符串反转
        return sint.toString(2);
    }


    public static String byte2HexString(byte[] data) {
        if (data == null || data.length == 0) {
            return null;
        }
        byte[] hex = new byte[data.length * 2];
        int index = 0;
        for (byte b : data) {
            int v = b & 0xFF;
            hex[index++] = (byte) HEX_CHAR_TABLE[v >>> 4];
            hex[index++] = (byte) HEX_CHAR_TABLE[v & 0xF];
        }
        return new String(hex);
    }

    public static byte[] hex2Byte(String hexString) {
        if (hexString == null || hexString.length() == 0) {
            return null;
        }
        hexString = hexString.toUpperCase();
        if (hexString.length() % 2 != 0) {
            throw new RuntimeException();
        }
        byte[] data = new byte[hexString.length() / 2];
        char[] chars = hexString.toCharArray();
        for (int i = 0; i < hexString.length(); i = i + 2) {
            data[i / 2] = (byte) (HEX_TABLE[getHexCharValue(chars[i])] << 4 | HEX_TABLE[getHexCharValue(chars[i + 1])]);
        }
        return data;
    }

    private static int getHexCharValue(char c) {
        int index = 0;
        for (char c1 : HEX_CHAR_TABLE) {
            if (c == c1) {
                return index;
            }
            index++;
        }
        return 0;
    }

    public static short byte2short(byte[] b) {
        return (short) ((b[0] & 0xFF << 8) | b[1] & 0xff);
    }

    public static List<Byte> char2Byte(char c) {
        List<Byte> list = new ArrayList<Byte>();
        int i1 = (c & 0xFF00) >> 8;
        int i2 = c & 0xFF;
        if (i1 > 0) {
            list.add((byte) i1);
        } else {
            list.add((byte) i2);
        }
        return list;
    }

    public static char byteToChar(byte b) {
        return (char) (((b & 0xFF) << 8) | (b & 0xFF));
    }

    public static String byte2char2(byte[] bytes) {
        char[] chars = new char[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            chars[i] = byteToChar(bytes[i]);
        }
        return new String(chars);
    }

    public static byte[] char2Byte(char[] chars) {
        List<Byte> list = new ArrayList<>();
        for (char c : chars) {
            list.addAll(char2Byte(c));
        }
        byte[] ret = new byte[list.size()];
        for (int i = 0; i < list.size(); i++) {
            ret[i] = list.get(i);
        }
        return ret;
    }

    public static String byte2BinaryString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(byte2BinaryString(b));
        }
        return sb.toString();
    }

    public static String byte2BinaryString(byte b) {
        StringBuilder sb = new StringBuilder();
        for (int i = 7; i >= 0; i--) {
            sb.append((b >> i & 1) == 1 ? "1" : "0");
        }
        return sb.toString();
    }

    public static byte[] str2Bcd(String str) {
        int len = str.length();
        byte[] bytes = str.getBytes();
        if (len >= 2) {
            len = len / 2;
        }
        byte[] ret = new byte[len];
        int i1, i2;
        for (int i = 0; i < str.length() / 2; i++) {
            if ((bytes[2 * i] >= 'a') && (bytes[2 * i] <= 'f')) {
                i1 = bytes[2 * i] - 'a' + 10;
            } else if ((bytes[2 * i] >= 'A')
                    && (bytes[2 * i] <= 'F')) {
                i1 = bytes[2 * i] - 'A' + 10;
            } else {
                i1 = bytes[2 * i] & 0x0f;
            }
            if ((bytes[2 * i + 1] >= 'a')
                    && (bytes[2 * i + 1] <= 'f')) {
                i2 = bytes[2 * i + 1] - 'a' + 10;
            } else if ((bytes[2 * i + 1] >= 'A')
                    && (bytes[2 * i + 1] <= 'F')) {
                i2 = bytes[2 * i + 1] - 'A' + 10;
            } else {
                i2 = bytes[2 * i + 1] & 0x0f;
            }
            ret[i] = (byte) ((i1 << 4) | i2);
        }
        return ret;
    }


    public static String bcd2Str(byte[] bytes) {
        char[] ret = new char[bytes.length * 2];
        char c = 0;
        for (int i = 0; i < bytes.length; i++) {
            c = (char) ((bytes[i] & 0xf0) >> 4);
            ret[i * 2] = (char) (c > 9 ? c + 'A' - 10 : c + '0');
            c = (char) (bytes[i] & 0x0f);
            ret[i * 2 + 1] = (char) (c > 9 ? c + 'A' - 10 : c + '0');
        }
        return new String(ret);
    }

    /**
     * byte合并，顺序执行
     *
     * @param values
     * @return
     */
    public static byte[] byteMergerAll(byte[]... values) {
        int length_byte = 0;
        for (byte[] value : values) {
            length_byte += value.length;
        }
        byte[] all_byte = new byte[length_byte];
        int countLength = 0;
        for (byte[] b : values) {
            System.arraycopy(b, 0, all_byte, countLength, b.length);
            countLength += b.length;
        }
        return all_byte;
    }

    public static byte[] StringToBytes(String data) {
        String hexString = data.toUpperCase().trim();
        if (hexString.length() % 2 != 0) {
            return null;
        }
        byte[] retData = new byte[hexString.length() / 2];
        for (int i = 0; i < hexString.length(); i++) {
            int int_ch; // 两位16进制数转化后的10进制数
            char hex_char1 = hexString.charAt(i); // //两位16进制数中的第一位(高位*16)
            int int_ch1;
            if (hex_char1 >= '0' && hex_char1 <= '9')
                int_ch1 = (hex_char1 - 48) * 16; // // 0 的Ascll - 48
            else if (hex_char1 >= 'A' && hex_char1 <= 'F')
                int_ch1 = (hex_char1 - 55) * 16; // // A 的Ascll - 65
            else
                return null;
            i++;
            char hex_char2 = hexString.charAt(i); // /两位16进制数中的第二位(低位)
            int int_ch2;
            if (hex_char2 >= '0' && hex_char2 <= '9')
                int_ch2 = (hex_char2 - 48); // // 0 的Ascll - 48
            else if (hex_char2 >= 'A' && hex_char2 <= 'F')
                int_ch2 = hex_char2 - 55; // // A 的Ascll - 65
            else
                return null;
            int_ch = int_ch1 + int_ch2;
            retData[i / 2] = (byte) int_ch;// 将转化后的数放入Byte里
        }
        return retData;
    }

    /**
     * @param: [content]
     * @return: int
     * @description: 十六进制转十进制
     */
    public static int hexto10(String content) {
        int number = 0;
        String[] HighLetter = {"A", "B", "C", "D", "E", "F"};
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i <= 9; i++) {
            map.put(i + "", i);
        }
        for (int j = 10; j < HighLetter.length + 10; j++) {
            map.put(HighLetter[j - 10], j);
        }
        String[] str = new String[content.length()];
        for (int i = 0; i < str.length; i++) {
            str[i] = content.substring(i, i + 1);
        }
        for (int i = 0; i < str.length; i++) {
            number += map.get(str[i]) * Math.pow(16, str.length - 1 - i);
        }
        return number;
    }

    /**
     * 16进制转10进制
     *
     * @param bytes
     * @return
     */
    public static String bytes2hex03(byte[] bytes) {
        final String HEX = "0123456789abcdef";
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(HEX.charAt((b >> 4) & 0x0f));
            sb.append(HEX.charAt(b & 0x0f));
        }

        return sb.toString();
    }

    /**
     * 10进制转16进制
     *
     * @param num
     * @param totalLenght 需要的字符串总长度
     * @return
     */
    public static String toHex(int num, int totalLenght) {
        String str = Integer.toHexString(num);
        str = splicingZero(str, totalLenght);
        return str;
    }

    public static String toHex(long num, int totalLenght) {
        String str = Long.toHexString(num);
        str = splicingZero(str, totalLenght);
        return str;
    }

    /**
     * 字符串前面补零操作
     *
     * @param str         字符串本体
     * @param totalLenght 需要的字符串总长度
     * @return
     */
    private static String splicingZero(String str, int totalLenght) {
        int strLenght = str.length();
        StringBuilder strReturn = new StringBuilder(str);
        for (int i = 0; i < totalLenght - strLenght; i++) {
            strReturn.insert(0, "0");
        }
        return strReturn.toString();
    }

    public static String asciiToString(String hex) {
        StringBuilder sb = new StringBuilder();
        StringBuilder temp = new StringBuilder();
        //49204c6f7665204a617661 split into two characters 49, 20, 4c...
        for (int i = 0; i < hex.length() - 1; i += 2) {
            //grab the hex in pairs
            String output = hex.substring(i, (i + 2));
            //convert hex to decimal
            int decimal = Integer.parseInt(output, 16);
            //convert the decimal to character
            sb.append((char) decimal);
            temp.append(decimal);
        }
        return sb.toString();
    }

    public static String stringToAscii(String str) {
        char[] chars = str.toCharArray();
        StringBuilder hex = new StringBuilder();
        for (char aChar : chars) {
            hex.append(Integer.toHexString((int) aChar));
        }
        return hex.toString();
    }

    /**
     * 判断字符串是否为16进制
     *
     * @param str
     * @return
     */
    public static boolean isHexString(String str) {
        char[] cs = str.toCharArray();
        int maxL = cs.length;
        int isHex = 0;
        for (int i = 0; i < maxL; i++) {
            if (Math.abs(isHex) != i) {
                return true;
            }
            char c = cs[i];
            if ('0' <= c && '9' >= c) {
                ++isHex;
            }
            boolean AF = ('A' <= c && 'F' >= c);
            boolean af = ('a' <= c && 'f' >= c);
            if (AF || af) {
                --isHex;
            }
        }
        return false;
    }

}
