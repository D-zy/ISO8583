package com.example.iso8583.util;

public class SecretUtils {

    public static String bytes2HexStr(byte[] byteArr) {
        String hexString = "0123456789ABCDEF";
        StringBuilder sb = new StringBuilder(byteArr.length * 2);
        for (byte b : byteArr) {
            sb.append(hexString.charAt((b & 0xF0) >> 4));
            sb.append(hexString.charAt((b & 0xF)));
        }
        return sb.toString();
    }

    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr == null || hexStr.length() < 1) {
            return null;
        }
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }


    public static String xor(String com1, String com2) {
        byte[] b1 = parseHexStr2Byte(com1);
        byte[] b2 = parseHexStr2Byte(com2);
        byte[] tXor = new byte[Math.min(b1.length, b2.length)];
        for (int i = 0; i < tXor.length; i++)
            tXor[i] = (byte) (b1[i] ^ b2[i]); // 异或(Xor)
        return bytes2HexStr(tXor);
    }

    public static String bcdhex_to_aschex(byte[] bcdhex) {
        byte[] aschex = {0, 0};
        StringBuilder res = new StringBuilder();
        String tmp = "";
        for (byte b : bcdhex) {
            aschex[1] = hexLowToAsc(b);
            aschex[0] = hexHighToAsc(b);
            tmp = new String(aschex);
            res.append(tmp);
        }
        return res.toString();
    }

    private static byte hexLowToAsc(byte xxc) {
        xxc &= 0x0f;
        if (xxc < 0x0a)
            xxc += '0';
        else
            xxc += 0x37;
        return (byte) xxc;
    }

    private static byte hexHighToAsc(int xxc) {
        xxc &= 0xf0;
        xxc = xxc >> 4;
        if (xxc < 0x0a)
            xxc += '0';
        else
            xxc += 0x37;
        return (byte) xxc;
    }

    public static byte[] aschex_to_bcdhex(String aschex) {
        byte[] aschexByte = aschex.getBytes();
        int j = 0;
        if (aschexByte.length % 2 == 0) {
            j = aschexByte.length / 2;
            byte[] resTmp = new byte[j];
            for (int i = 0; i < j; i++) {
                resTmp[i] = ascToHex(aschexByte[2 * i], aschexByte[2 * i + 1]);
            }
            return resTmp;

        } else {
            j = aschexByte.length / 2 + 1;
            byte[] resTmp = new byte[j];
            for (int i = 0; i < j - 1; i++) {
                resTmp[i] = ascToHex((byte) aschexByte[2 * i],
                        (byte) aschexByte[2 * i + 1]);
            }
            resTmp[j - 1] = ascToHex((byte) aschexByte[2 * (j - 1)], (byte) 0);
            return resTmp;
        }
    }

    private static byte ascToHex(byte ch1, byte ch2) {
        byte ch;
        if (ch1 >= 'A')
            ch = (byte) ((ch1 - 0x37) << 4);
        else
            ch = (byte) ((ch1 - '0') << 4);
        if (ch2 >= 'A')
            ch |= (byte) (ch2 - 0x37);
        else
            ch |= (byte) (ch2 - '0');
        return ch;
    }
}
