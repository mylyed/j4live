package com.mylyed.j4live.protocol.rtmp.chunk.message.pcm;

import com.mylyed.j4live.protocol.rtmp.chunk.enums.MessageType;
import com.mylyed.j4live.protocol.rtmp.chunk.header.ChunkHeader;
import com.mylyed.j4live.protocol.rtmp.chunk.message.AbstractMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 设置块大小
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class SetChunkSize extends AbstractMessage {

    private int chunkSize;

    public SetChunkSize(ChunkHeader header, ByteBuf in) {
        super(header, in);
    }

    public SetChunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.SET_CHUNK_SIZE;
    }

    @Override
    public ByteBuf encode() {
        return Unpooled.buffer(4).writeInt(chunkSize);
    }

    @Override
    public void decode(ByteBuf in) {
        chunkSize = in.readInt();
    }

}