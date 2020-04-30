package com.mylyed.j4live.protocol.amf;

import com.mylyed.j4live.common.ValueToEnum;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * AMF0支持的数据类型
 * 维基:https://en.wikipedia.org/wiki/Action_Message_Format
 * <pre>
 * Number - 0x00 (Encoded as IEEE 64-bit double-precision floating point number)
 * Boolean - 0x01 (Encoded as a single byte of value 0x00 or 0x01)
 * String - 0x02 (16-bit integer string length with UTF-8 string)
 * Object - 0x03 (Set of key/value pairs)
 * Null - 0x05
 * ECMA Array - 0x08 (32-bit entry count)
 * Object End - 0x09 (preceded by an empty 16-bit string length)
 * Strict Array - 0x0a (32-bit entry count)
 * Date - 0x0b (Encoded as IEEE 64-bit double-precision floating point number with 16-bit integer timezone offset)
 * Long String - 0x0c (32-bit integer string length with UTF-8 string)
 * XML Document - 0x0f (32-bit integer string length with UTF-8 string)
 * Typed Object - 0x10 (16-bit integer name length with UTF-8 name, followed by entries)
 * Switch to AMF3 - 0x11
 * </pre>
 */
public enum AMF0Type implements ValueToEnum.NumberValue {

    NUMBER(0x00),
    BOOLEAN(0x01),
    STRING(0x02),
    OBJECT(0x03),
    NULL(0x05),

    UNDEFINED(0x06),//
    REFERENCE(0x07),//

    MAP(0x08),
    OBJECT_END(0x09),
    ARRAY(0x0A),
    DATE(0x0B),
    LONG_STRING(0x0C),
    UNSUPPORTED(0x0D),

    RECORDSET(0x0E),//

    XML_DOCUMENT(0x0F),
    TYPED_OBJECT(0x10);

    private final Byte value;

    AMF0Type(Integer value) {
        this.value = value.byteValue();
    }

    @Override
    public Byte value() {
        return value;
    }

    private static final ValueToEnum<AMF0Type> converter = new ValueToEnum<>(AMF0Type.values());

    public static AMF0Type valueToEnum(final Number value) {
        return converter.valueToEnum(value);
    }

    static AMF0Type getType(final Object value) {
        if (value == null) {
            return NULL;
        } else if (value instanceof String) {
            if (((String) value).getBytes(AMF0.AMF0_CHARSET).length > Short.MAX_VALUE) {
                return LONG_STRING;
            }
            return STRING;
        } else if (value instanceof Number) {
            return NUMBER;
        } else if (value instanceof Boolean) {
            return BOOLEAN;
        } else if (value instanceof AMF0Object) {
            AMF0Object obj = (AMF0Object) value;
            if (obj.containsKey(AMF0Object.TYPED_OBJECT_KEY))
                return TYPED_OBJECT;
            else
                return OBJECT;
        } else if (value instanceof Map) {
            return MAP;
        } else if (value instanceof Object[]) {
            return ARRAY;
        } else if (value instanceof Collection) {
            return ARRAY;
        } else if (value instanceof Date) {
            return DATE;
        } else {
            throw new RuntimeException("不支持的类型: " + value.getClass());
        }
    }

}