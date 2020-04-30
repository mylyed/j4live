package com.mylyed.j4live.protocol.amf;

import com.mylyed.j4live.common.Constants;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.util.*;


/**
 * AMF0转码和解码解码工具
 * <p>
 * 注意：NUMBER 为浮点数 double
 */
@Slf4j
public class AMF0 {
    static final Charset AMF0_CHARSET = Constants.CHARSET;
    //布尔值 1真0假
    private static final byte BOOLEAN_TRUE = 0x01;
    private static final byte BOOLEAN_FALSE = 0x00;
    //结束标记
    private static final byte[] OBJECT_END_MARKER = new byte[]{0x00, 0x00, 0x09};

    private AMF0() {
    }

    //--------------解码----------------

    /**
     * 解码
     *
     * @param in
     * @return
     */
    public static Object decode(ByteBuf in) {
        final AMF0Type type = AMF0Type.valueToEnum(in.readByte());
        switch (type) {
            case NUMBER:
                // IEEE-754 浮点类型
                return Double.longBitsToDouble(in.readLong());
            case BOOLEAN:
                return in.readByte() == BOOLEAN_TRUE;
            case STRING:
                return tryDecodeString(in);
            case ARRAY:
                final int arraySize = in.readInt();
                final Object[] array = new Object[arraySize];
                for (int i = 0; i < arraySize; i++) {
                    array[i] = decode(in);
                }
                return array;
            case MAP:
                return decodeMap(in);
            case OBJECT:
                return decodeObject(in);
            case DATE:
                //64-位
                final long dateValue = in.readLong();
                //忽略时区
                in.readShort();
                return new Date((long) Double.longBitsToDouble(dateValue));
            case LONG_STRING:
                //字符串长度跟普通字符串（STRING）不一样
                final int stringSize = in.readInt();
                final byte[] bytes = new byte[stringSize];
                in.readBytes(bytes);
                return new String(bytes, AMF0_CHARSET);
            //
            case NULL:
            case UNDEFINED:
            case UNSUPPORTED:
                return null;
            case TYPED_OBJECT:
                String classname = tryDecodeString(in);
                AMF0Object object = decodeObject(in);
                object.put(AMF0Object.TYPED_OBJECT_KEY, classname);
                return object;
            default:
                throw new RuntimeException("不支持解码: " + type);
        }
    }

    /**
     * 解码成字符串
     * string就是字符类型，
     * 一个byte的amf类型，两个bytes的字符长度，和N个bytes的数据。
     * 比如：02 00 02 33 22，第一个byte为amf类型，其后两个bytes为长度，注意这里的00 02是大端模式，33 22是字符数据。
     * <p>
     * 注意字符集
     *
     * @param in 例如 00 02 33 22
     * @return
     */
    private static String decodeString(ByteBuf in) {
        final short size = in.readShort();
        if (size == 0) {
            return "";
        }
        final byte[] bytes = new byte[size];
        in.readBytes(bytes);
        return new String(bytes, AMF0_CHARSET);
    }

    /**
     * 解码字符串
     * 解码失败返回空字符串
     *
     * @param in
     * @return
     */
    private static String tryDecodeString(ByteBuf in) {
        try {
            return decodeString(in);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private static Map<String, Object> decodeMap(ByteBuf in) {
        int count = in.readInt();
        log.trace("map count :{}", count);
        Map<String, Object> map = new LinkedHashMap<>();
        //结束标记
        final byte[] endMarker = new byte[3];
        int i = 0;
        while (in.isReadable()) {
            in.getBytes(in.readerIndex(), endMarker);
            if (Arrays.equals(endMarker, OBJECT_END_MARKER)) {
                //跳过结束标记
                in.readBytes(endMarker);
                break;
            }
            if (count > 0 && i++ == count) {
                //
                break;
            }
            map.put(tryDecodeString(in), decode(in));
        }
        return map;
    }

    private static AMF0Object decodeObject(ByteBuf in) {
        AMF0Object object = new AMF0Object();
        //结束标记
        final byte[] endMarker = new byte[3];
        while (in.isReadable()) {
            in.getBytes(in.readerIndex(), endMarker);
            if (Arrays.equals(endMarker, OBJECT_END_MARKER)) {
                //跳过结束标记
                in.readBytes(endMarker);
                break;
            }
            object.addProperty(tryDecodeString(in), decode(in));
        }
        return object;
    }

    //-------------------编码----------------

    private static void encodeString(final ByteBuf out, final String value) {
        final byte[] bytes = value.getBytes(AMF0_CHARSET);
        out.writeShort((short) bytes.length);
        out.writeBytes(bytes);
    }

    @SuppressWarnings("unchecked")
    private static void encodeObject(final ByteBuf out, final Object value) {
        final Map<String, Object> map = (Map) value;
        for (final Map.Entry<String, Object> entry : map.entrySet()) {
            encodeString(out, entry.getKey());
            encode(out, entry.getValue());
        }
        out.writeBytes(OBJECT_END_MARKER);
    }

    //暴露给外部使用的方法

    /**
     * 编码单个值
     *
     * @param out
     * @param value
     */
    @SuppressWarnings("unchecked")
    public static void encode(ByteBuf out, Object value) {
        AMF0Type type = AMF0Type.getType(value);
        log.trace("type:{}", type);
        out.writeByte(type.value());
        switch (type) {
            case NUMBER:
                if (value instanceof Double) {
                    out.writeLong(Double.doubleToLongBits((Double) value));
                } else {
                    out.writeLong(Double.doubleToLongBits(Double.parseDouble(value.toString())));
                }
                return;
            case BOOLEAN:
                out.writeByte((Boolean) value ? BOOLEAN_TRUE : BOOLEAN_FALSE);
                return;
            case STRING: {
                final byte[] bytes = ((String) value).getBytes(AMF0_CHARSET);
                out.writeShort((short) bytes.length);
                out.writeBytes(bytes);
                return;
            }
            case LONG_STRING: {
                final byte[] bytes = ((String) value).getBytes(AMF0_CHARSET);
                out.writeInt(bytes.length);
                out.writeBytes(bytes);
                return;
            }
            case OBJECT:
                encodeObject(out, value);
                return;
            case NULL:
                return;
            case MAP:
                out.writeInt(0);
                encodeObject(out, value);
                return;
            case ARRAY:
                final Object[] array;
                if (value instanceof Collection) {
                    //处理集合
                    array = ((Collection) value).toArray();
                } else {
                    array = (Object[]) value;
                }
                out.writeInt(array.length);
                for (Object o : array) {
                    encode(out, o);
                }
                return;
            case DATE:
                final long time = ((Date) value).getTime();
                out.writeLong(Double.doubleToLongBits(time));
                //时区
                out.writeShort((short) 0);
                return;
            case TYPED_OBJECT:
                final Map<String, Object> map = (Map<String, Object>) value;
                encodeString(out, (String) map.remove(AMF0Object.TYPED_OBJECT_KEY));
                encodeObject(out, value);
                return;
            default:
                throw new RuntimeException("不支持编码: " + type);
        }

    }

    /**
     * 编码多个值
     *
     * @param out
     * @param values
     */
    public static void encodes(ByteBuf out, Object... values) {
        for (Object value : values) {
            encode(out, value);
        }
    }

}