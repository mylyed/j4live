package com.mylyed.j4live.protocol.rtmp.server;

import com.mylyed.j4live.protocol.rtmp.chunk.message.MediaMessage;
import com.mylyed.j4live.stream.StreamHandler;
import com.mylyed.j4live.stream.StreamHandlerManager;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * 媒体消息
 *
 * @author lilei
 * created at 2020/4/27
 */
@Slf4j
public class MediaHandler extends RtmpMessageHandler<MediaMessage> {


    public MediaHandler(StreamHandlerManager streamManager) {
        super(streamManager);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MediaMessage mediaMessage) throws Exception {
        log.trace("media message : {}", mediaMessage);
        StreamHandler streamHandler = tryGetStreamHandler(ctx);
        if (mediaMessage.isConfig()) {
            streamHandler.addConfigMessage(mediaMessage);
        } else {
            streamHandler.broadcast(mediaMessage);
        }
    }
}
