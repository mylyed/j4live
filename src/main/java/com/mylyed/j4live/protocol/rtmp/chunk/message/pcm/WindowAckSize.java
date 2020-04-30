package com.mylyed.j4live.protocol.rtmp.chunk.message.pcm;

import com.mylyed.j4live.protocol.rtmp.chunk.enums.MessageType;
import com.mylyed.j4live.protocol.rtmp.chunk.header.ChunkHeader;
import com.mylyed.j4live.protocol.rtmp.chunk.message.AbstractMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Getter;
import lombok.ToString;

/**
 * Window Acknowledgement Size（5）
 * 这是用来协商发送包的大小的。
 * <p>
 * 主要针对的是客户端可接受的最大数据包的值，
 * 一般电脑设置的大小都是 500000B。
 *
 * @author lilei
 * created at 2020/4/25
 */
@Getter
@ToString
public class WindowAckSize extends AbstractMessage {

    private int acknowledgementWindowSize;

    public WindowAckSize(ChunkHeader header, ByteBuf in) {
        super(header, in);
    }

    public WindowAckSize(int acknowledgementWindowSize) {
        super();
        this.acknowledgementWindowSize = acknowledgementWindowSize;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.WINDOW_ACKNOWLEDGEMENT_SIZE;
    }

    @Override
    public ByteBuf encode() {
        return Unpooled.buffer(4).writeInt(acknowledgementWindowSize);
    }

    @Override
    public void decode(ByteBuf in) {
        acknowledgementWindowSize = in.readInt();
    }
}
