package com.mylyed.j4live.protocol.rtmp.chunk.message;

import com.mylyed.j4live.protocol.rtmp.chunk.header.ChunkHeader;
import io.netty.buffer.ByteBuf;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = false)
public abstract class AbstractMessage extends RtmpMessage {

    protected final ChunkHeader header;

    public AbstractMessage(ChunkHeader header, ByteBuf in) {
        this.header = header;
        decode(in);
    }

    public AbstractMessage() {
        header = new ChunkHeader();
        header.setCsid(getOutboundCsid());
        header.setMessageType(getMessageType());
    }

    /**
     * rtmp协议头
     *
     * @return
     */
    public ChunkHeader getHeader() {
        return header;
    }

    /**
     * 默认出站的csid
     *
     * @return
     */
    public int getOutboundCsid() {
        switch (getMessageType()) {
            case SET_CHUNK_SIZE:
            case ABORT_MESSAGE:
            case ACKNOWLEDGEMENT:
            case WINDOW_ACKNOWLEDGEMENT_SIZE:
            case SET_PEER_BANDWIDTH:
            case USER_CONTROL_MESSAGE:
                return 2;
            case SHARED_OBJECT_MESSAGE_AMF0:
            case SHARED_OBJECT_MESSAGE_AMF3:
            case COMMAND_AMF0:
            case COMMAND_AMF3:
                return 3;
            case META_AFM0:
            case META_AFM3:
            case AUDIO_MESSAGE:
            case VIDEO_MESSAGE:
            case AGGREGATE_MESSAGE:
            default:
                return 5;
        }
    }

}