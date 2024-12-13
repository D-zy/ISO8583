package com.example.iso8583.util

object TlvUtils {
    @JvmStatic
    fun main(args: Array<String>) {
//        int i = parseTag8E("000000000000000042015E031F00");
        val tlvs = builderTlvList("9F2608AE7361AAFC4FC0829F2701809F10080106A04001401F009F370485E2E7459F360202FE950500000000009A032110259C01009F02060000000021005F2A020702820279009F1A0208409F03060000000000009F3303E0B9C89F34035E03009F3501229F1E0831323334353637388407A00000015230109F090200019F4103000100")
    }

    data class LPosition(var vL: Int, val position: Int, val hexVl: String)
    data class Tlv(var tag: String, val length: String, val value: String)

    /**
     * 将16进制字符串转换为TLV对象列表
     *
     * @param hexString
     * @return
     */
    fun builderTlvList(hexString: String): List<Tlv>? {
        return try {
            val tlvs: MutableList<Tlv> = ArrayList()
            var position = 0
            while (position != hexString.length) {
                val _hexTag = getTag(hexString, position)
                position += _hexTag.length
                val l_position = getLengthAndPosition(hexString, position)
                val _vl = l_position.vL
                position = l_position.position
                val _value = hexString.substring(position, position + _vl * 2)
                position += _value.length
                tlvs.add(Tlv(_hexTag, _vl.toString(), _value))
            }
            tlvs
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 将16进制字符串转换为TLV对象MAP
     *
     * @param hexString
     * @return
     */
    fun builderTlvMap(hexString: String): Map<String, Tlv> {
        val tlvs: MutableMap<String, Tlv> = HashMap()
        var position = 0
        while (position != hexString.length) {
            val _hexTag = getTag(hexString, position)
            position += _hexTag.length
            val l_position = getLengthAndPosition(hexString, position)
            val _vl = l_position.vL
            position = l_position.position
            val _value = hexString.substring(position, position + _vl * 2)
            position += _value.length
            tlvs[_hexTag] = Tlv(_hexTag, l_position.hexVl, _value)
        }
        return tlvs
    }

    fun builderTlvToMap(hexString: String): Map<String, String> {
        val map: MutableMap<String, String> = HashMap()
        var position = 0
        while (position != hexString.length) {
            val _hexTag = getTag(hexString, position)
            position += _hexTag.length
            val l_position = getLengthAndPosition(hexString, position)
            val _vl = l_position.vL
            position = l_position.position
            val _value = hexString.substring(position, position + _vl * 2)
            position += _value.length
            map[_hexTag] = _value
        }
        return map
    }

    /**
     * tlv  l:长度固定4位
     */
    fun builderTlvToMap2(hexString: String): Map<String, String> {
        val map: MutableMap<String, String> = HashMap()
        var position = 0
        while (position != hexString.length) {
            val _hexTag = getTag(hexString, position)
            position += _hexTag.length
            val _length = hexString.substring(position, position + 4)
            position += _length.length
            val _vl = ConvertUtil.hexto10(_length)
            val _value = hexString.substring(position, position + _vl)
            position += _value.length
            map[_hexTag] = _value
        }
        return map
    }

    fun builderTlvToMap3(hexString: String): Map<String, String> {
        val map: MutableMap<String, String> = HashMap()
        var position = 0
        while (position != hexString.length) {
            val _hexTag = getTag(hexString, position)
            position += _hexTag.length
            val _length = hexString.substring(position, position + 4)
            position += _length.length
            val _vl = ConvertUtil.hexto10(_length)
            val _value = hexString.substring(position, position + _vl * 2)
            position += _value.length
            map[_hexTag] = _value
        }
        return map
    }

    //    private static String[] icDataTagsArray = new String[]{"9F26","9F27","9F10","9F37","9F36","95","9A","9C","9F02","5F2A","82","9F1A","9F03","9F33","9F34","9F35","9F1E","84","9F09","9F41"};
    private val icDataTagsArray = arrayOf("9F26", "9F27", "9F10", "9F37", "9F36", "95", "9A", "9C", "9F02", "5F2A", "82", "9F1A", "9F03", "9F33", "9F35", "9F1E", "84", "9F09", "9F63")

    /**
     * icData我们客户端送过来的太长，会导致交易失败，所以要处理一下，抽取出一些必须的tlv
     *
     * @return
     */
    fun initIcData(tlvMap: Map<String?, Tlv?>): String {
        val sb = StringBuffer()
        for (tag in icDataTagsArray) {
            val tlv = tlvMap[tag] ?: continue
            sb.append(tlv.tag)
            sb.append(tlv.length)
            sb.append(tlv.value)
        }
        return sb.toString()
    }

    fun buildRemark(hexString: String): Map<String, String> {
        val map: MutableMap<String, String> = HashMap()
        return try {
            val remarkTags = arrayOf(
                "9F26", "95", "84", "9B", "9F36", "5F34",
                "82", "9F37", "9F10", "9F12", "50", "57"
            )
            val remarkDesc = arrayOf(
                "APP_CRYPT", "TVR", "AID", "TSI", "ATC", "PAN_SN",
                "AIP", "UMPRMUM", "IAD", "APP_NAME", "APP_ID", "TRACK2"
            )
            val tlvMap = builderTlvMap(hexString)
            val size = remarkTags.size
            for (i in 0 until size) {
                val tag = remarkTags[i]
                val tlv = tlvMap[tag]
                var value = ""
                if (null != tlv) {
                    value = tlv.value
                }
                map[remarkDesc[i]] = value
            }
            map
        } catch (e: Exception) {
            e.printStackTrace()
            map
        }
    }

    /**
     * 返回最后的Value的长度
     *
     * @param hexString
     * @param position
     * @return
     */
    private fun getLengthAndPosition(hexString: String, position: Int): LPosition {
        var newPosition = position
        val firstByteString = hexString.substring(newPosition, newPosition + 2)
        val i = firstByteString.toInt(16)
        var hexLength = ""
        if (i ushr 7 and 1 == 0) {
            hexLength = hexString.substring(newPosition, newPosition + 2)
            newPosition += 2
        } else {
            // 当最左侧的bit位为1的时候，取得后7bit的值，
            val _L_Len = i and 127
            newPosition += 2
            hexLength = hexString.substring(newPosition, newPosition + _L_Len * 2)
            // position表示第一个字节，后面的表示有多少个字节来表示后面的Value值
            newPosition += _L_Len * 2
        }
        return LPosition(hexLength.toInt(16), newPosition, hexLength)
    }

    /**
     * 取得子域Tag标签
     *
     * @param hexString
     * @param position
     * @return
     */
    private fun getTag(hexString: String, position: Int): String {
        val firstByte = hexString.substring(position, position + 2)
        val i = firstByte.toInt(16)
        return if (i and 0x1f == 0x1f) {
            hexString.substring(position, position + 4)
        } else {
            hexString.substring(position, position + 2)
        }
    }

    fun parseTag8E(value: String): Int {
        val cvm = value.substring(16)
        val count = cvm.length / 4
        for (i in 0 until count) {
            val subS = cvm.substring(i * 4, i * 4 + 4)
            if (subS.endsWith("03")) {
                val bytes = ConvertUtil.hex2Byte(subS.substring(0, 2))
                val b1_6 = ConvertUtil.byte2BinaryString(bytes)
                if (b1_6.endsWith("000001") || b1_6.endsWith("000011")) { //明文
                    return 1
                } else if (b1_6.endsWith("000010")) { //密文
                    return 2
                }
            }
        }
        return 0
    }
}