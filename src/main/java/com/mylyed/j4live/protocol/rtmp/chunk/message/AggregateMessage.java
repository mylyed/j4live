package com.mylyed.j4live.protocol.rtmp.chunk.message;


import com.mylyed.j4live.protocol.rtmp.chunk.enums.MessageType;
import com.mylyed.j4live.protocol.rtmp.chunk.header.ChunkHeader;
import io.netty.buffer.ByteBuf;

/**
 * 聚合消息
 *
 * @author lilei
 * created at 2020/4/25
 */
public class AggregateMessage extends MediaMessage {
    public AggregateMessage(ChunkHeader header, ByteBuf in) {
        super(header, in);
    }

    @Override
    public boolean isConfig() {
        return false;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.AGGREGATE_MESSAGE;
    }

}
