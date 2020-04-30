package com.mylyed.j4live.protocol.rtmp.server;

import com.mylyed.j4live.protocol.rtmp.chunk.RtmpConstants;
import com.mylyed.j4live.protocol.rtmp.chunk.enums.Format;
import com.mylyed.j4live.protocol.rtmp.chunk.header.ChunkHeader;
import com.mylyed.j4live.protocol.rtmp.chunk.message.RtmpMessage;
import com.mylyed.j4live.protocol.rtmp.chunk.message.pcm.SetChunkSize;
import com.mylyed.j4live.protocol.rtmp.chunk.message.ucm.UserControlMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * 块编码器 messageToByte
 *
 * @author lilei
 * created at 2020/4/26
 */
@Slf4j
public class ChunkEncoder extends MessageToByteEncoder<RtmpMessage> {


    private int chunkSize = RtmpConstants.DEFAULT_CLIENT_CHUNK_SIZE;

    private ChunkHeader[] prevHeaders = new ChunkHeader[RtmpConstants.MAX_CHUNK_STREAM_ID];

    @Override
    protected void encode(ChannelHandlerContext ctx, RtmpMessage msg, ByteBuf out) throws Exception {
        log.debug("encode message to byte: {}", msg);
        if (msg instanceof SetChunkSize) {
            chunkSize = ((SetChunkSize) msg).getChunkSize();
            log.info("server set chunk size =>{}", chunkSize);
        } else if (msg instanceof UserControlMessage) {
            UserControlMessage.EventType eventType = ((UserControlMessage) msg).getEventType();
            if (UserControlMessage.EventType.STREAM_BEGIN.equals(eventType)) {
                //客户端可以能在测试连接 OBS 在第一次连接时回反复调用 所以需要清理
                prevHeaders = new ChunkHeader[RtmpConstants.MAX_CHUNK_STREAM_ID];
            }
        }
        encode(msg, out);
    }

    /**
     * 编码
     * <p>
     * 按照协议规则反序列化回去
     *
     * @param message
     * @return
     */
    private void encode(final RtmpMessage message, final ByteBuf out) {
        final ByteBuf chunkData = message.encode();
        final ChunkHeader chunkHeader = message.getHeader();
        //块数据长度
        chunkHeader.setMessageLength(chunkData.readableBytes());
        //设置
        if (message instanceof UserControlMessage) {
            chunkHeader.setMessageStreamId(0);
        } else {
            chunkHeader.setMessageStreamId(RtmpConstants.DEFAULT_STREAM_ID);
        }

        Integer csid = chunkHeader.getCsid();
        final ChunkHeader prevHeader = prevHeaders[csid];
        //设置 FMT 和 TimestampDelta
        if (prevHeader != null && chunkHeader.getMessageStreamId() > 0 && chunkHeader.getTimestamp() > 0) {
            if (chunkHeader.getMessageLength().equals(prevHeader.getMessageLength())) {
                chunkHeader.setFmt(Format.FMT_2);
            } else {
                chunkHeader.setFmt(Format.FMT_1);
            }
            final int deltaTime = chunkHeader.getTimestamp() - prevHeader.getTimestamp();
            chunkHeader.setTimestampDelta(Math.max(deltaTime, 0));
        } else {
            chunkHeader.setFmt(Format.FMT_0);
        }

        prevHeaders[csid] = message.getHeader();
        //开始写入 写入长度
        log.debug("write chunkHeader");
        out.writeBytes(chunkHeader.encode());
        //分段写入数据
        boolean first = true;
        while (chunkData.isReadable()) {
            log.info("chunkSize: {} , chunkData length: {}", chunkSize, chunkData.readableBytes());
            if (first) {
                first = false;
            } else {
                log.debug("write FMT3BH");
                //块之间用FMT3分隔
                out.writeBytes(ChunkHeader.buildFMT3BH(chunkHeader.getCsid()).encode());
            }
            log.debug("write chunkData");
            final int size = Math.min(chunkSize, chunkData.readableBytes());
            //chunkData => out
            out.writeBytes(chunkData, size);
        }
        chunkData.release();
    }

}
