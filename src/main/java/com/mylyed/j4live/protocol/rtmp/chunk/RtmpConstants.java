package com.mylyed.j4live.protocol.rtmp.chunk;

import com.mylyed.j4live.stream.StreamHandlerName;
import io.netty.util.AttributeKey;

/**
 * RTMP 的一些常量数据
 */
public class RtmpConstants {

    /**
     * 时间戳最大值 用的24位
     */
    public static final int MAX_TIMESTAMP = 0X00FFFFFF;


    public static final byte RTMP_VERSION = 0x03;

    //
    public static final int DEFAULT_STREAM_ID = 5;

    //块流 ID 64 - 65599 可以编码在这个字段的三字节版本中。ID 计算为 ((第三个字节) * 256 + (第二个字节) + 64)。
    public static final int MAX_CHUNK_STREAM_ID = 65600;

    //数据块大小：客户端发送最大128字节，服务端和客户端可以修改这个值，并升级另一端的大小
    public static final int DEFAULT_CLIENT_CHUNK_SIZE = 128;

    /**
     * 流名称
     */
    public static final AttributeKey<StreamHandlerName> STREAM_NAME = AttributeKey.newInstance("stream_name");


}
