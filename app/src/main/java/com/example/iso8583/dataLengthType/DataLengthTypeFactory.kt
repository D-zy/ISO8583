package com.example.iso8583.dataLengthType

import com.example.iso8583.enums.Field

object DataLengthTypeFactory {
    /**
     *
     * @param field
     * @return
     * @throws RuntimeException
     */
    @JvmStatic
    @Throws(RuntimeException::class)
    fun create(field: Field): DataLengthTypeInterface {
        return when (field.lengthType) {
            "LLVAR_BCD" -> DataLengthType_LLVAR_BCD().also { it.setField(field) }
            "LLLVAR_BCD" -> DataLengthType_LLLVAR_BCD().also { it.setField(field) }
            "LLVAR_ASCII" -> DataLengthType_LLVAR_ASCII().also { it.setField(field) }
            "LLLVAR_ASCII" -> DataLengthType_LLLVAR_ASCII().also { it.setField(field) }
            "FIXED" -> DataLengthType_Fixed().also { it.setField(field) }
            else -> throw RuntimeException("invalid data_length_type:" + field.lengthType)
        }
    }
}