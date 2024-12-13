package com.example.iso8583.enums

import com.example.iso8583.util.Constants
import com.example.iso8583.util.ConvertUtil

/**
 * 解析域长度类型
 * 命名格式：L+x+1/2/3/...
 * L 表示是否具有长度描述，N/HEX/... 表示数据内容格式，N表示普通字符，无须解析，HEX表示hexStr格式，其他具体语意
 *
 *
 * 长度表示说明：
 * 目前遇到实际奇数表示实际后续N类型的长度，如0019，实际只截取后续19位字符串，所以无法使用字节表示长度
 *
 *
 */
enum class AnalysisFieldLengthType(l: Int, type: String) {
    /**
     * 固定长2内容长度类型，无长度描述
     */
    N_2(2, Constants.TYPE_N),

    /**
     * 固定长2内容长度类型，无长度描述
     */
    N_3(3, Constants.TYPE_N),

    /**
     * 固定长4内容长度类型，无长度描述
     */
    N_4(4, Constants.TYPE_N),

    /**
     * 固定长6内容长度类型，无长度描述
     */
    N_6(6, Constants.TYPE_N),

    /**
     * 固定长7内容长度类型，无长度描述
     */
    N_HEX_16(16, Constants.TYPE_N_HEX),

    /**
     * 固定长2内容长度类型
     */
    L_N_2(2, Constants.TYPE_L_N),

    /**
     * 固定长4内容长度类型
     */
    L_N_4(4, Constants.TYPE_L_N),

    /**
     * 固定长6内容长度类型
     */
    L_N_6(6, Constants.TYPE_L_N),

    /**
     * hex内容且为4长度类型
     */
    L_HEX_4(4, Constants.TYPE_L_NEX),

    /**
     * hex内容且为8长度类型
     */
    L_HEX_8(8, Constants.TYPE_L_NEX),
    L_HEX_20(20, Constants.TYPE_L_NEX);

    private var length = 0
    private var type = ""

    /**
     * 获取类型长度
     *
     * @return
     */
    fun l(): Int {
        return length
    }

    /**
     * 获取长度类型
     *
     * @return
     */
    fun lType(): String {
        return type
    }

    /**
     * 根据域长度类型解析数据占位长度
     *
     * @param val
     * @return
     */
    fun analysisLength(`val`: String): String {
        return if (Constants.TYPE_N == lType()) {
            "0"
        } else if (Constants.TYPE_L_N == lType()) {
            `val`.substring(0, l())
        } else if (Constants.TYPE_L_NEX == lType()) {
            `val`.substring(0, l())
        } else {
            "0"
        }
    }

    /**
     * 根据域长度类型解析数据占位数据内容
     *
     * @param len
     * @param val
     * @return
     */
    fun analysisValue(len: String, `val`: String): String {
        return if (Constants.TYPE_N == lType()) {
            `val`.substring(0, l())
        } else if (Constants.TYPE_N_HEX == lType()) {
            val v = `val`.substring(0, l())
            try {
                String(ConvertUtil.hex2Byte(v))
            } catch (e: Throwable) {
                v
            }
        } else if (Constants.TYPE_L_N == lType()) {
            val currentIndex = l() + len.toInt()
            `val`.substring(l(), currentIndex)
        } else if (Constants.TYPE_L_NEX == lType()) {
            val currentIndex = l() + len.toInt()
            //            String fVal = val.substring(l(), currentIndex);
            val fVal = `val`.substring(l(), currentIndex * 2 - l())
            try {
                String(ConvertUtil.hex2Byte(fVal))
            } catch (e: Throwable) {
                fVal
            }
        } else {
            ""
        }
    }

    /**
     * 根据域长度类型获取当前占位数据内容下标
     *
     * @param len
     * @return
     */
    fun getCurrentAnalysisIndex(len: String): Int {
        return l() + len.toInt()
    }

    /**
     * 解析域长度类型
     *
     * @param l    长度定义
     * @param type 长度类型
     */
    init {
        length = l
        this.type = type
    }
}