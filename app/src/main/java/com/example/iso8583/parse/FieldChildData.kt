package com.example.iso8583.parse

import com.example.iso8583.enums.AnalysisFieldLengthType

/**
 * 域-子数据源
 */
class FieldChildData {
    var fieldId = ""
    var parentFieldId = ""
    var fieldLength = ""
    var value = ""
    private var lType: AnalysisFieldLengthType? = null
    fun getlType(): AnalysisFieldLengthType? {
        return lType
    }

    fun setlType(lType: AnalysisFieldLengthType?) {
        this.lType = lType
    }

    override fun toString(): String {
        return "FieldChildData{" +
                "fieldId='" + fieldId + '\'' +
                ", parentFieldId='" + parentFieldId + '\'' +
                ", fieldLength='" + fieldLength + '\'' +
                ", val='" + value + '\'' +
                ", lType=" + lType +
                '}'
    }
}