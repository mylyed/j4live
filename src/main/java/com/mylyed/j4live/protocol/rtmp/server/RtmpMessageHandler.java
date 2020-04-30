/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.mylyed.j4live.protocol.rtmp.server;

import com.mylyed.j4live.protocol.rtmp.chunk.RtmpConstants;
import com.mylyed.j4live.protocol.rtmp.chunk.message.RtmpMessage;
import com.mylyed.j4live.stream.StreamHandler;
import com.mylyed.j4live.stream.StreamHandlerManager;
import com.mylyed.j4live.stream.StreamHandlerName;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.Attribute;

import java.util.Objects;

/**
 * @author lilei
 * created at 2020/4/30
 */
public abstract class RtmpMessageHandler<I extends RtmpMessage> extends SimpleChannelInboundHandler<I> {

    protected final StreamHandlerManager streamManager;

    public RtmpMessageHandler(StreamHandlerManager streamManager) {
        this.streamManager = streamManager;
    }

    /**
     * 获取流的名称
     *
     * @param ctx
     * @return
     */
    public StreamHandlerName tryGetStreamHandlerName(ChannelHandlerContext ctx) {
        Attribute<StreamHandlerName> attr = ctx.channel().attr(RtmpConstants.STREAM_NAME);
        StreamHandlerName streamName = attr.get();
        Objects.requireNonNull(streamName, "streamName");
        return streamName;
    }

    public void setStreamHandlerName(ChannelHandlerContext ctx, StreamHandlerName streamHandlerName) {
        Attribute<StreamHandlerName> attr = ctx.channel().attr(RtmpConstants.STREAM_NAME);
        attr.set(streamHandlerName);
    }

    /**
     * 获取当前流
     *
     * @param ctx
     * @return
     */
    public StreamHandler tryGetStreamHandler(ChannelHandlerContext ctx) {
        StreamHandler streamHandler = streamManager.getStreamHandler(tryGetStreamHandlerName(ctx));
        Objects.requireNonNull(streamHandler, "streamHandler");
        return streamHandler;
    }
}
