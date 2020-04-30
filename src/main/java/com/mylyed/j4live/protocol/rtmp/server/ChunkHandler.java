package com.mylyed.j4live.protocol.rtmp.server;


import com.mylyed.j4live.protocol.rtmp.chunk.message.RtmpMessage;
import com.mylyed.j4live.protocol.rtmp.chunk.message.pcm.SetChunkSize;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lilei
 * created at 2020/4/26
 */
@Slf4j
public class ChunkHandler extends SimpleChannelInboundHandler<RtmpMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RtmpMessage msg) throws Exception {
        log.trace("RtmpMessage: {}", msg);
        if (msg instanceof SetChunkSize) {
            log.debug("SetChunkSize on ChunkDecoder");
            ctx.writeAndFlush(msg);
            return;
        }
        ctx.fireChannelRead(msg);
    }
}
