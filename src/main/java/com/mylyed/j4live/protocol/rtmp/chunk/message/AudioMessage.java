package com.mylyed.j4live.protocol.rtmp.chunk.message;

import com.mylyed.j4live.protocol.rtmp.chunk.enums.MessageType;
import com.mylyed.j4live.protocol.rtmp.chunk.header.ChunkHeader;
import io.netty.buffer.ByteBuf;
import lombok.Getter;

/**
 * 音频
 *
 * @author lilei
 * created at 2020/4/25
 */
@Getter
public class AudioMessage extends MediaMessage {


    public AudioMessage(ChunkHeader header, ByteBuf in) {
        super(header, in);
    }

    @Override
    public boolean isConfig() {
        return raw.length > 1 && raw[1] == 0;
    }


    @Override
    public MessageType getMessageType() {
        return MessageType.AUDIO_MESSAGE;
    }


}
