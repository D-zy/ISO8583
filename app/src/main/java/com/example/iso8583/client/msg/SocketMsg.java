package com.example.iso8583.client.msg;


import com.example.iso8583.util.LogUtils;
import com.wbb.egtwo.iso8583.client.msg.SocketMsgTagEnum;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * 以TLV形式表示map的key和value
 * tag:第一个字节bcd码表示tag所占的字节长度，tag以ascill表示（UTF-8)，最大99个字节
 * length:固定四个字节的bcd码表示，不够左补0
 * value:纯纯的byte数组
 */
public class SocketMsg {

    private static final String FLAG_STR = "##JFSocketMsg##";
    public static final byte[] FLAG = FLAG_STR.getBytes();
    public static final String charset = "UTF-8";

    private static final int TAG_LENGTH_BYTE = 1;//tag的长度表示位占用几个字节
    private static final int VALUE_LENGTH_BYTE = 4;//value的长度表示位占用几个字节
    private static final int TAG_BYTES_MAX_LENGTH = TAG_LENGTH_BYTE * 100 - 1;//tag序列化后最大多少字节
    private static final int VALUE_BYTES_MAX_LENGTH = (int) Math.pow(100d, VALUE_LENGTH_BYTE) - 1;//value最大多少个字节

    private Map<String, byte[]> msgMap = new HashMap<>();

    private SocketMsg() {

    }

    public SocketMsg(byte[] msg) {
        msgMap.put(SocketMsgTagEnum.TAG_BODY.getTagName(), msg);
    }

    private void putMsgMap(String key, byte[] value) {
        msgMap.put(key, value);
    }

    public void addAttachment(String key, byte[] value) {
        if (isEmpty(key)) {
            LogUtils.e("key is null.");
            return;
        }
        if (isSystemTag(key)) {
            LogUtils.e("this tag is used by system. not open for user");
            return;
        }
        try {
            if (key.getBytes(charset).length > TAG_BYTES_MAX_LENGTH) {
                return;
            }
        } catch (UnsupportedEncodingException e) {
            LogUtils.e(e.toString());
            return;
        }
        if (value == null || value.length == 0) {
            LogUtils.e("value is null");
            return;
        }
        if (value.length > VALUE_BYTES_MAX_LENGTH) {
            LogUtils.e("value.length must < 99999999. tag[%s] length is %s", key, value.length);
            return;
        }
        msgMap.put(key, value);
    }

    public void addAttachments(Map<String, byte[]> map) {
        for (Map.Entry<String, byte[]> entry : map.entrySet()) {
            addAttachment(entry.getKey(), entry.getValue());
        }
    }

    public byte[] getAttachment(String key) {
        return msgMap.get(key);
    }

    public byte[] getMsg() {
        return getAttachment(SocketMsgTagEnum.TAG_BODY.getTagName());
    }

    public byte[] serializeToBytes() throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(FLAG);
        for (Map.Entry<String, byte[]> entry : msgMap.entrySet()) {
            byte[] key = entry.getKey().getBytes(charset);
            byte[] value = entry.getValue();
            outputStream.write(int2bcd(key.length));
            outputStream.write(key);
            String valueLength = value.length + "";
            while (valueLength.length() < VALUE_LENGTH_BYTE * 2) {
                valueLength = "0" + valueLength;
            }
            for (int i = 0; i < valueLength.length(); i += 2) {
                outputStream.write(int2bcd(Integer.valueOf(valueLength.substring(i, i + 2))));
            }
            outputStream.write(value);
        }
        return outputStream.toByteArray();
    }

    public static SocketMsg serializeFromBytes(byte[] bytes) throws Exception {
        if (bytes.length < FLAG.length) {
            return null;
        }
        byte[] flagBytes = new byte[FLAG.length];
        System.arraycopy(bytes, 0, flagBytes, 0, FLAG.length);
        if (!FLAG_STR.equals(new String(flagBytes))) {
            return null;
        }

        SocketMsg socketMsg = new SocketMsg();
        int current = FLAG.length;
        while (current < bytes.length - 1) {
            //read tag length
            byte[] tagLengthBytes = new byte[TAG_LENGTH_BYTE];
            System.arraycopy(bytes, current, tagLengthBytes, 0, tagLengthBytes.length);
            current += TAG_LENGTH_BYTE;

            //read tag
            int tagLength = bcd2int(tagLengthBytes[0]);
            byte[] tagBytes = new byte[tagLength];
            System.arraycopy(bytes, current, tagBytes, 0, tagBytes.length);
            String tag = new String(tagBytes, "UTF-8");
            current += tagLength;

            //read value length
            byte[] valueLengthBytes = new byte[VALUE_LENGTH_BYTE];
            System.arraycopy(bytes, current, valueLengthBytes, 0, valueLengthBytes.length);
            current += VALUE_LENGTH_BYTE;


            //read value
            int valueLength = 0;
            for (int i = 0; i < valueLengthBytes.length; i++) {
                valueLength += bcd2int(valueLengthBytes[i]) * Math.pow(100d, (VALUE_LENGTH_BYTE - i - 1));
            }

            byte[] valueBytes = new byte[valueLength];
            System.arraycopy(bytes, current, valueBytes, 0, valueBytes.length);
            current += valueLength;
            socketMsg.putMsgMap(tag, valueBytes);
        }
        return socketMsg;
    }

    public static byte int2bcd(int i) throws Exception {
        if (i < 0 || i >= 100) {
            LogUtils.e("int2BCD ERROR. i<=0 || i>=100. i=%s", i);
            throw new Exception("int2BCD ERROR. i<=0 || i>=100. i=" + i);
        }
        return (byte) ((((i / 10) & 0x0f) << 4) | ((i % 10) & 0x0f));
    }

    public static int bcd2int(byte b) {
        return ((b & 0xf0) >> 4) * 10 + (b & 0x0f);
    }

    private boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    private boolean isSystemTag(String tagName) {
        SocketMsgTagEnum[] array = SocketMsgTagEnum.values();
        for (SocketMsgTagEnum tagEnum : array) {
            if (tagEnum.getTagName().equals(tagName)) {
                return true;
            }
        }
        return false;
    }

}
