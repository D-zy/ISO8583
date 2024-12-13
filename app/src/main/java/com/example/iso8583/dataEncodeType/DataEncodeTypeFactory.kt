package com.example.iso8583.dataEncodeType

object DataEncodeTypeFactory {
    /**
     *
     * @param name
     * @return
     * @throws RuntimeException
     */
    @JvmStatic
    @Throws(RuntimeException::class)
    fun create(name: String): DataEncodeTypeInterface {
        return when (name) {
            "ASCII" -> DataEncodeType_ASCII()
            "BCD" -> DataEncodeType_BCD()
            "BINARY" -> DataEncodeType_BINARY()
            "HEX" -> DataEncodeType_HEX()
            "BHEX" -> DataEncodeType_BHEX()
            else -> throw RuntimeException("invalid encodeType:$name")
        }
    }
}