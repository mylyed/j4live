package com.mylyed.j4live.protocol.rtmp.server;


import com.mylyed.j4live.protocol.rtmp.chunk.RtmpHandshake;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 处理握手
 * https://zhuanlan.zhihu.com/p/110030170
 *
 * @author lilei
 * created at 2020/4/24
 */
@Slf4j
public class HandshakeHandler extends ByteToMessageDecoder {
    //握手是否完毕
    private boolean handshakeDone = false;
    //c0c1 是否完成
    private boolean c0c1done = false;

    private final RtmpHandshake handshake;

    public HandshakeHandler() {
        handshake = new RtmpHandshake();
    }

    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (handshakeDone) {
            log.debug("handshakeDone !");
            ctx.fireChannelRead(in);
            return;
        }
        if (!c0c1done) {
            if (in.readableBytes() < (RtmpHandshake.VERSION_SIZE + RtmpHandshake.HANDSHAKE_SIZE)) {
                //可读取长度不够
                return;
            }
            handshake.decodeClient0And1(in);
            writeS0S1S2(ctx);
            c0c1done = true;
        } else {
            // 读取C2
            if (in.readableBytes() < RtmpHandshake.HANDSHAKE_SIZE) {
                return;
            }
            handshake.decodeClient2(in);
            log.debug("handshake success");
            handshakeDone = true;
            ctx.channel().pipeline().remove(this);
        }
    }

    /**
     * 响应S0S1S2
     *
     * @param ctx
     */
    private void writeS0S1S2(ChannelHandlerContext ctx) {
        ctx.write(handshake.encodeServer0());
        ctx.write(handshake.encodeServer1());
        ctx.write(handshake.encodeServer2());
        ctx.flush();
    }
}
