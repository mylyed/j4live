package com.mylyed.j4live.protocol.rtmp.chunk.message;


import com.mylyed.j4live.protocol.rtmp.chunk.enums.MessageType;
import com.mylyed.j4live.protocol.rtmp.chunk.header.ChunkHeader;
import io.netty.buffer.ByteBuf;
import lombok.Getter;

/**
 * 视频
 *
 * @author lilei
 * created at 2020/4/25
 */
@Getter
public class VideoMessage extends MediaMessage {

    public VideoMessage(ChunkHeader header, ByteBuf in) {
        super(header, in);
    }

    @Override
    public boolean isConfig() {
        return raw.length > 3 && raw[0] == 0x17 && raw[1] == 0x00;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.VIDEO_MESSAGE;
    }

}
