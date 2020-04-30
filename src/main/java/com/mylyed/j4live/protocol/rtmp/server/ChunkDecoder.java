package com.mylyed.j4live.protocol.rtmp.server;

import com.mylyed.j4live.protocol.rtmp.chunk.RtmpConstants;
import com.mylyed.j4live.protocol.rtmp.chunk.MessageDecoder;
import com.mylyed.j4live.protocol.rtmp.chunk.enums.Format;
import com.mylyed.j4live.protocol.rtmp.chunk.header.ChunkHeader;
import com.mylyed.j4live.protocol.rtmp.chunk.message.RtmpMessage;
import com.mylyed.j4live.protocol.rtmp.chunk.message.pcm.SetChunkSize;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;


/**
 * 块解码器
 *
 * @author lilei
 * created at 2020/4/24
 */
@Slf4j
public class ChunkDecoder extends ReplayingDecoder<ChunkDecoder.DecodeState> {

    public enum DecodeState {
        DECODE_HEADER,
        DECODE_PAYLOAD
    }

    public ChunkDecoder() {
        super(DecodeState.DECODE_HEADER);
    }

    /**
     * 使用数组来提高查询效率，通过牺牲空间来换取时间
     */
    //未完成的中间变量
    private final ChunkHeader[] incompleteHeaders = new ChunkHeader[RtmpConstants.MAX_CHUNK_STREAM_ID];
    private final ByteBuf[] incompletePayloads = new ByteBuf[RtmpConstants.MAX_CHUNK_STREAM_ID];
    //已完成
    private final ChunkHeader[] completedHeaders = new ChunkHeader[RtmpConstants.MAX_CHUNK_STREAM_ID];

    private ChunkHeader header;
    private Integer csid;
    private ByteBuf payload;
    private int chunkSize = RtmpConstants.DEFAULT_CLIENT_CHUNK_SIZE;

    /**
     * 解码块头
     *
     * @param in
     * @return
     */
    private ChunkHeader decodeChunkHeader(ByteBuf in) {
        ChunkHeader chunkHeader = new ChunkHeader();
        chunkHeader.decodeBasicHeader(in);
        log.trace("fmt : {} csid : {}", chunkHeader.getFmt(), chunkHeader.getCsid());
        ChunkHeader prev = incompleteHeaders[chunkHeader.getCsid()];
        log.trace("prevHeader : {}", prev);
        chunkHeader.decodeMessageHeader(in, prev);
        chunkHeader.decodeExtendedTimestamp(in);
        log.trace("decodeHeader chunkHeader :{}", chunkHeader);
        return chunkHeader;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        DecodeState state = super.state();
        switch (state) {
            case DECODE_HEADER: {
                header = decodeChunkHeader(in);
                csid = header.getCsid();
                if (incompletePayloads[csid] == null) {
                    incompleteHeaders[csid] = header;
                    incompletePayloads[csid] = Unpooled.buffer(header.getMessageLength());
                }
                payload = incompletePayloads[csid];
                checkpoint(DecodeState.DECODE_PAYLOAD);
            }
            //注意没有break
            case DECODE_PAYLOAD: {
                final byte[] bytes = new byte[Math.min(payload.writableBytes(), chunkSize)];
                in.readBytes(bytes);
                payload.writeBytes(bytes);
                checkpoint(DecodeState.DECODE_HEADER);
                if (payload.isWritable()) {
                    //下一个应该是FMT3
                    log.trace("read next chunk");
                    return;
                }
                incompletePayloads[csid] = null;
                final ChunkHeader prevHeader = completedHeaders[csid];
                if (header.getFmt() != Format.FMT_0) {
                    //累加时间
                    header.setTimestamp(prevHeader.getTimestamp() + header.getTimestampDelta());
                }
                RtmpMessage message = MessageDecoder.decode(header, payload);
                if (message instanceof SetChunkSize) {
                    //设置块大小
                    chunkSize = ((SetChunkSize) message).getChunkSize();
                    log.debug("set chunkSize: {}", chunkSize);
                }

                completedHeaders[csid] = header;
                csid = null;
                payload = null;
                out.add(message);
                break;
            }
            default:
                throw new RuntimeException("see the ghost : " + state);
        }
    }
}
