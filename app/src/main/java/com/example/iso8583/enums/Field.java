package com.example.iso8583.enums;

/**
 * 域属性枚举
 */
public enum Field {
    F_TUDP("0", "n", "10", FieldEncodeType.BCD.name(), FieldLengthType.FIXED.name()),

    //域2
    F2("2", "n", "19", FieldEncodeType.BCD.name(), FieldLengthType.LLVAR_BCD.name()),
    //域3
    F3("3", "n", "6", FieldEncodeType.BCD.name(), FieldLengthType.FIXED.name()),
    //域4
    F4("4", "n", "12", FieldEncodeType.BCD.name(), FieldLengthType.FIXED.name()),
    //域5
    F5("5", "n", "11", FieldEncodeType.BCD.name(), FieldLengthType.FIXED.name()),
    //域11
    F11("11", "n", "6", FieldEncodeType.BCD.name(), FieldLengthType.FIXED.name()),
    //域12
    F12("12", "n", "6", FieldEncodeType.BCD.name(), FieldLengthType.FIXED.name()),
    //域13
    F13("13", "n", "4", FieldEncodeType.BCD.name(), FieldLengthType.FIXED.name()),
    //域14
    F14("14", "n", "4", FieldEncodeType.BCD.name(), FieldLengthType.FIXED.name()),
    //域15
    F15("15", "n", "4", FieldEncodeType.BCD.name(), FieldLengthType.FIXED.name()),
    //域18
    F18("18", "ans", "256", FieldEncodeType.ASCII.name(), FieldLengthType.LLLVAR_BCD.name()),
    //域22
    F22("22", "n", "3", FieldEncodeType.BCD.name(), FieldLengthType.FIXED.name()),
    //域23
    F23("23", "n", "3", FieldEncodeType.BCD.name(), FieldLengthType.FIXED.name()),
    //域24
    F24("24", "n", "3", FieldEncodeType.BCD.name(), FieldLengthType.FIXED.name()),
    //域25
    F25("25", "n", "2", FieldEncodeType.BCD.name(), FieldLengthType.FIXED.name()),
    //域26
    F26("26", "n", "2", FieldEncodeType.BCD.name(), FieldLengthType.FIXED.name()),
    //域30 TDK（密钥使用1.0版本参数）；已过时，目前使用密钥使用2.0版本，无该参数设置
    @Deprecated
    F30("30", "b", "16", FieldEncodeType.BINARY.name(), FieldLengthType.FIXED.name()),
    //域32
    F32("32", "n", "11", FieldEncodeType.BCD.name(), FieldLengthType.LLVAR_BCD.name()),
    //域35
    F35("35", "z", "48", FieldEncodeType.BCD.name(), FieldLengthType.LLVAR_BCD.name()),
    //域36
    F36("36", "z", "104", FieldEncodeType.BCD.name(), FieldLengthType.LLLVAR_BCD.name()),
    //域37
    F37("37", "an", "12", FieldEncodeType.ASCII.name(), FieldLengthType.FIXED.name()),
    //域38
    F38("38", "an", "6", FieldEncodeType.ASCII.name(), FieldLengthType.FIXED.name()),
    //域39
    F39("39", "an", "2", FieldEncodeType.ASCII.name(), FieldLengthType.FIXED.name()),
    //域41
    F41("41", "ans", "8", FieldEncodeType.ASCII.name(), FieldLengthType.FIXED.name()),
    //域42
    F42("42", "ans", "15", FieldEncodeType.ASCII.name(), FieldLengthType.FIXED.name()),
    //域43
    F43("43", "ans", "40", FieldEncodeType.ASCII.name(), FieldLengthType.FIXED.name()),
    //域44
    F44("44", "ans", "999", FieldEncodeType.HEX.name(), FieldLengthType.LLVAR_BCD.name()),
    //域45 一磁道
    F45("45", "ans", "76", FieldEncodeType.ASCII.name(), FieldLengthType.LLVAR_BCD.name()),
    //域46
    F46("46", "ans", "512", FieldEncodeType.ASCII.name(), FieldLengthType.LLLVAR_BCD.name()),
    //域47
    F47("47", "ans", "512", FieldEncodeType.ASCII.name(), FieldLengthType.LLLVAR_BCD.name()),
    //域48
    F48("48", "ans", "999", FieldEncodeType.HEX.name(), FieldLengthType.LLLVAR_BCD.name()),
    F48_Silverlake("48", "ans", "999", FieldEncodeType.ASCII.name(), FieldLengthType.LLLVAR_BCD.name()),
    //域49，汇币类型；156代表软妹币
    F49("49", "an", "3", FieldEncodeType.ASCII.name(), FieldLengthType.FIXED.name()),
    //域52
    F52("52", "b", "8", FieldEncodeType.BINARY.name(), FieldLengthType.FIXED.name()),
    //域53
    F53("53", "n", "16", FieldEncodeType.BCD.name(), FieldLengthType.FIXED.name()),
    //域54  小费
    F54("54", "an", "20", FieldEncodeType.ASCII.name(), FieldLengthType.LLVAR_BCD.name()),
    F54_Silverlake("54", "an", "20", FieldEncodeType.ASCII.name(), FieldLengthType.LLLVAR_BCD.name()),
    //域55
    F55("55", "ans", "255", FieldEncodeType.HEX.name(), FieldLengthType.LLLVAR_BCD.name()),
    //域56
    F56("56", "ans", "48", FieldEncodeType.ASCII.name(), FieldLengthType.LLVAR_BCD.name()),
    //域57
    F57("57", "n", "999", FieldEncodeType.HEX.name(), FieldLengthType.LLLVAR_BCD.name()),
    //域59
    F59("59", "ans", "5", FieldEncodeType.ASCII.name(), FieldLengthType.FIXED.name()),
    //域60
    F60("60", "ans", "999", FieldEncodeType.HEX.name(), FieldLengthType.LLLVAR_BCD.name()),
    F60_Silverlake("60", "ans", "999", FieldEncodeType.ASCII.name(), FieldLengthType.LLLVAR_BCD.name()),
    //域61
    F61("61", "ans", "999", FieldEncodeType.ASCII.name(), FieldLengthType.LLLVAR_BCD.name()),
    //域62
    F62("62", "ans", "999", FieldEncodeType.ASCII.name(), FieldLengthType.LLLVAR_BCD.name()),
    //域63
    F63("63", "ans", "999", FieldEncodeType.HEX.name(), FieldLengthType.LLLVAR_BCD.name()),
    F63_Silverlake("63", "ans", "999", FieldEncodeType.ASCII.name(), FieldLengthType.LLLVAR_BCD.name()),
    //域64，64域mac是10进制
    F64("64", "b", "8", FieldEncodeType.BINARY.name(), FieldLengthType.FIXED.name());

    private String fieldId;
    private String dataType;
    private String dataLength;
    private String encodeType;
    private String lengthType;

    /**
     * 补位类型，如左补、右补
     */
    private String direction = FieldDirection.R.name();
    /**
     * 补位填充数据源，如空格、0
     */
    private String fillStr = "";
    private String[][] directions = {
            //示例：2域 右补位 补位填充“0”
            {"2", FieldDirection.R.name(), FieldFillStr.ZERO.getVal()},
            {"5", FieldDirection.R.name(), FieldFillStr.SPACE.getVal()},
            {"22", FieldDirection.L.name(), FieldFillStr.ZERO.getVal()},
            {"23", FieldDirection.L.name(), FieldFillStr.ZERO.getVal()},
            {"24", FieldDirection.L.name(), FieldFillStr.ZERO.getVal()},
            {"35", FieldDirection.R.name(), FieldFillStr.F.getVal()},
            {"43", FieldDirection.R.name(), FieldFillStr.SPACE.getVal()},
//            {"60", FieldDirection.R.name(), FieldFillStr.SPACE.getVal()}
    };

    /**
     * 域属性构造
     *
     * @param fieldId    域描述
     * @param dataType   数据类型
     * @param dataLength 数据长度
     * @param encodeType 编码类型
     */
    Field(String fieldId, String dataType, String dataLength, String encodeType, String lengthType) {
        this.fieldId = fieldId;
        this.dataType = dataType;
        this.dataLength = dataLength;
        this.encodeType = encodeType;
        this.lengthType = lengthType;
        //初始化补位属性
        initDirection(fieldId);
    }

    /**
     * 初始化补位类型与填充数据
     */
    private void initDirection(String fieldId) {
        for (String[] ds : directions) {
            if (ds[0].equals(fieldId)) {
                this.direction = ds[1];
                this.fillStr = ds[2];
                break;
            }
        }
    }

    /**
     * 查询对应的域-属性
     *
     * @param id
     * @return
     */
    public static Field selectFieldForId(String id) {
        for (Field f : values()) {
            if (f.getFieldId().toString().equals(id)) {
//                LogUtils.p("selectFieldForId->"+id);
                return f;
            }
        }
        return null;
    }

    public Integer getFieldId() {
        return fieldId.isEmpty() ? 0 : Integer.parseInt(fieldId);
    }

    public String getFieldId2String() {
        return fieldId;
    }

    public String getDataType() {
        return dataType;
    }

    public String getDataLength() {
        return dataLength;
    }

    public String getEncodeType() {
        return encodeType;
    }

    public String getLengthType() {
        return lengthType;
    }

    public String getDirection() {
        return direction;
    }

    public String getFillStr() {
        return fillStr;
    }

    @Override
    public String toString() {
        return "Field{" +
                "fieldId='" + fieldId + '\'' +
                ", dataType='" + dataType + '\'' +
                ", dataLength='" + dataLength + '\'' +
                ", encodeType='" + encodeType + '\'' +
                ", lengthType='" + lengthType + '\'' +
                ", direction='" + direction + '\'' +
                ", fillStr='" + fillStr + '\'' +
                '}';
    }
}
