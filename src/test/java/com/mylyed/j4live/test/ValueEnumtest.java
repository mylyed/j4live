package com.mylyed.j4live.test;


import com.mylyed.j4live.protocol.amf.AMF0Type;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author lilei
 * created at 2020/4/24
 */
public class ValueEnumtest {
    @Test
    public void testValueEnum() {
        AMF0Type[] values = AMF0Type.values();
        for (AMF0Type value : values) {
            AMF0Type type = AMF0Type.valueToEnum(value.value());
            Assert.assertEquals("反序列化失败", type, value);
        }
    }
}
