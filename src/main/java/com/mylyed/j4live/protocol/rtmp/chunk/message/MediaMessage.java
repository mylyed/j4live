package com.mylyed.j4live.protocol.rtmp.chunk.message;

import com.mylyed.j4live.protocol.rtmp.chunk.header.ChunkHeader;
import com.mylyed.j4live.util.ByteBufAllocatorUtil;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.ToString;

/**
 * 媒体消息
 *
 * @author lilei
 * created at 2020/4/26
 */
@ToString(exclude = "raw", callSuper = true)
public abstract class MediaMessage extends AbstractMessage {

    public MediaMessage(ChunkHeader header, ByteBuf in) {
        super(header, in);
    }

    //原始数据
    @Getter
    protected byte[] raw;

    @Override
    public ByteBuf encode() {
        return ByteBufAllocatorUtil.wrappedBuffer(raw);
    }


    @Override
    public void decode(ByteBuf in) {
        raw = new byte[in.readableBytes()];
        in.readBytes(raw);
    }

    public abstract boolean isConfig();


}
