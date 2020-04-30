package com.mylyed.j4live.protocol.rtmp.chunk.message.pcm;

import com.mylyed.j4live.protocol.rtmp.chunk.enums.MessageType;
import com.mylyed.j4live.protocol.rtmp.chunk.header.ChunkHeader;
import com.mylyed.j4live.protocol.rtmp.chunk.message.AbstractMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Getter;
import lombok.ToString;

/**
 * 该协议信息其实就是一个 ACK 包，
 * <p>
 * 在实际使用是并没有用到，它主要是用来作为一个 ACK 包，
 * <p>
 * 来表示两次 ACK 间，接收端所能接收的最大字节数。
 * <p>
 * 该包在实际应用中，没有多高的出现频率。
 */
@Getter
@ToString
public class Acknowledgement extends AbstractMessage {
    private int sequenceNumber;

    public Acknowledgement(ChunkHeader header, ByteBuf in) {
        super(header, in);
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.ACKNOWLEDGEMENT;
    }

    @Override
    public ByteBuf encode() {
        return Unpooled.buffer(4).writeInt(sequenceNumber);
    }

    @Override
    public void decode(ByteBuf in) {
        sequenceNumber = in.readInt();
    }
}