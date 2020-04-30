package com.mylyed.j4live.protocol.rtmp.chunk.message;

import com.mylyed.j4live.protocol.rtmp.chunk.enums.MessageType;
import com.mylyed.j4live.protocol.rtmp.chunk.header.ChunkHeader;
import com.mylyed.j4live.stream.Stream;
import io.netty.buffer.ByteBuf;

/**
 * 消息接口
 * <p>
 * 头消息和数据消息两种
 *
 * @author lilei
 * created at 2020/4/26
 */
public abstract class RtmpMessage implements Stream {

    /**
     * 消息头
     * <p>
     * 解析过程需要用到头信息
     *
     * @return
     */
    public abstract ChunkHeader getHeader();

    /**
     * 消息类型
     *
     * @return
     */
    public abstract MessageType getMessageType();



    /**
     * 解码
     *
     * @param in
     */
    public abstract void decode(ByteBuf in);
}
