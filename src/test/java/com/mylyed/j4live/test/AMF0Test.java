package com.mylyed.j4live.test;

import com.mylyed.j4live.protocol.amf.AMF0;
import com.mylyed.j4live.protocol.amf.AMF0Object;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * AMF0测试
 *
 * @author lilei
 * created at 2020/4/26
 */
@Slf4j
public class AMF0Test {

    @Test
    public void testNumber() {
        ByteBuf out = Unpooled.buffer();
        Integer number = 123;
        log.debug("encode number: {}", number);
        AMF0.encode(out, number);
        System.out.println(ByteBufUtil.prettyHexDump(out));
        Number test = (Number) AMF0.decode(out);
        log.debug("decode number: {}", test);
        assertTrue(number == test.intValue());
    }

    @Test
    public void testDate() {
        ByteBuf out = Unpooled.buffer();
        Date date = new Date();
        log.debug("encode date: {}", date);
        AMF0.encode(out, date);
        System.out.println(ByteBufUtil.prettyHexDump(out));
        Date test = (Date) AMF0.decode(out);
        log.debug("decode date: {}", test);
        assertEquals(date, test);
    }

    @Test
    public void testString() {
        ByteBuf out = Unpooled.buffer();
        String str = "我爱中国";
        log.debug("encode Str: {} , str length:{}", str, str.getBytes(StandardCharsets.UTF_8).length);
        AMF0.encode(out, str);
        System.out.println(ByteBufUtil.prettyHexDump(out));
        String test = (String) AMF0.decode(out);
        log.debug("decode Str: {}", test);
        assertEquals(str, test);
    }

    @Test
    public void testMap() {
        ByteBuf out = Unpooled.buffer();
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("name", "map");
        map.put("date", new Date());
        //
        map.put("num", 123.0);
        Map<String, Object> map2 = new LinkedHashMap<>();
        map2.put("map2", "map2");
        map.put("map", map2);
        log.debug("encode map: {}", map);

        AMF0.encode(out, map);
        System.out.println(ByteBufUtil.prettyHexDump(out));
        Object decode = AMF0.decode(out);
        log.debug("decode map: {}", decode);

        assertEquals(map, decode);
    }

    @Test
    public void testObject() {
        ByteBuf out = Unpooled.buffer();

        AMF0Object object = new AMF0Object();

        object.put("name", "object");
        object.put("date", new Date());
        //
        object.put("num", 123.0);
        Map<String, Object> map2 = new LinkedHashMap<>();
        map2.put("map2", "map2");
        object.put("map", map2);
        log.debug("encode object: {}", object);
        AMF0.encode(out, object);
        System.out.println(ByteBufUtil.prettyHexDump(out));
        Object decode = AMF0.decode(out);
        log.debug("decode object: {}", decode);
        assertEquals(object, decode);
    }

    @Test
    public void testArray() {
        ByteBuf out = Unpooled.buffer();
        Object[] objects = new Object[]{"aaa", "bbb", new Date(), 1.1};
        log.debug("encode objects: {}", Arrays.toString(objects));
        AMF0.encode(out, objects);
        System.out.println(ByteBufUtil.prettyHexDump(out));
        Object[] decode = (Object[]) AMF0.decode(out);
        log.debug("decode object: {}", Arrays.toString(decode));
        assertArrayEquals(objects, decode);
    }
}
