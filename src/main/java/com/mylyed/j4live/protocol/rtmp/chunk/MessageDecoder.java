package com.mylyed.j4live.protocol.rtmp.chunk;


import com.mylyed.j4live.protocol.rtmp.chunk.enums.MessageType;
import com.mylyed.j4live.protocol.rtmp.chunk.header.ChunkHeader;
import com.mylyed.j4live.protocol.rtmp.chunk.message.*;
import com.mylyed.j4live.protocol.rtmp.chunk.message.pcm.*;
import com.mylyed.j4live.protocol.rtmp.chunk.message.ucm.UserControlMessage;
import io.netty.buffer.ByteBuf;

/**
 * 消息解码器
 *
 * @author lilei
 * created at 2020/4/25
 */
public class MessageDecoder {

    public static RtmpMessage decode(ChunkHeader header, ByteBuf in) {
        MessageType messageType = header.getMessageType();
        switch (messageType) {
            //协议控制消息 PCM
            case SET_CHUNK_SIZE:
                return new SetChunkSize(header, in);
            case ABORT_MESSAGE:
                return new Abort(header, in);
            case ACKNOWLEDGEMENT:
                return new Acknowledgement(header, in);
            case WINDOW_ACKNOWLEDGEMENT_SIZE:
                return new WindowAckSize(header, in);
            case SET_PEER_BANDWIDTH:
                return new SetPeerBw(header, in);
            //命令
            case COMMAND_AMF0:
                return new CommandAMF0(header, in);
            //元数据
            case META_AFM0:
                return new MetadataAMF0(header, in);
            //用户控制信息 UCM
            case USER_CONTROL_MESSAGE:
                return new UserControlMessage(header, in);
            //媒体类型
            case AUDIO_MESSAGE:
                return new AudioMessage(header, in);
            case VIDEO_MESSAGE:
                return new VideoMessage(header, in);
            case AGGREGATE_MESSAGE:
                return new AggregateMessage(header, in);
            default:
                throw new RuntimeException("消息类型错误: " + messageType);
        }

    }

}
