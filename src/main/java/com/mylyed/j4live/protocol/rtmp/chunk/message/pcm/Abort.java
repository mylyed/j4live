package com.mylyed.j4live.protocol.rtmp.chunk.message.pcm;

import com.mylyed.j4live.protocol.rtmp.chunk.enums.MessageType;
import com.mylyed.j4live.protocol.rtmp.chunk.header.ChunkHeader;
import com.mylyed.j4live.protocol.rtmp.chunk.message.AbstractMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Getter;
import lombok.ToString;

/**
 * 该类 PCM 是用来告诉 client，
 * <p>
 * 丢弃指定的 stream 中，已经加载到一半或者还未加载完成的 Chunk Message。
 * <p>
 * 它需要指定一个 chunk stream ID。
 */
@Getter
@ToString
public class Abort extends AbstractMessage {

    private int chunkStreamID;

    public Abort(ChunkHeader header, ByteBuf in) {
        super(header, in);
    }


    @Override
    public MessageType getMessageType() {
        return MessageType.ABORT_MESSAGE;
    }

    @Override
    public ByteBuf encode() {
        return Unpooled.buffer(4).writeInt(chunkStreamID);
    }

    @Override
    public void decode(ByteBuf in) {
        chunkStreamID = in.readInt();
    }
}