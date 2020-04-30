package com.mylyed.j4live.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * Byte分配工具
 *
 * @author lilei
 * created at 2020/4/30
 */
public class ByteBufAllocatorUtil {

    public static ByteBuf buffer() {
        return Unpooled.buffer();
    }

    public static ByteBuf buffer(int initialCapacity) {
        return Unpooled.buffer(initialCapacity);
    }

    public static ByteBuf wrappedBuffer(byte[] array) {
        return Unpooled.wrappedBuffer(array);
    }
}
