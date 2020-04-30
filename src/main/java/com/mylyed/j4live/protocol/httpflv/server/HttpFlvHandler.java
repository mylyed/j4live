/*
 * Copyright 2013 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.mylyed.j4live.protocol.httpflv.server;

import com.google.common.base.Splitter;
import com.mylyed.j4live.common.Constants;
import com.mylyed.j4live.protocol.httpflv.HttpFlvSubscriber;
import com.mylyed.j4live.stream.StreamHandler;
import com.mylyed.j4live.stream.StreamHandlerManager;
import com.mylyed.j4live.stream.StreamHandlerName;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.List;

import static io.netty.handler.codec.http.HttpHeaderNames.*;
import static io.netty.handler.codec.http.HttpHeaderValues.KEEP_ALIVE;
import static io.netty.handler.codec.http.HttpHeaderValues.*;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

@Slf4j
public class HttpFlvHandler extends SimpleChannelInboundHandler<HttpObject> {
    StreamHandlerManager streamManager;

    public HttpFlvHandler(StreamHandlerManager streamManager) {
        this.streamManager = streamManager;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        if (msg instanceof HttpRequest) {
            HttpRequest req = (HttpRequest) msg;
            String uri = req.uri();
            log.debug("uri:{}", uri);
            List<String> infos = Splitter.on("/").omitEmptyStrings().splitToList(uri);
            String app = infos.get(0);
            String name = infos.get(1);

            StreamHandlerName streamName = new StreamHandlerName(app, name);
            StreamHandler streamHandler = streamManager.getStreamHandler(streamName);
            if (streamHandler == null) {
                responseMsg(ctx, name + "还未开播");
                return;
            }
            log.info("stream id :{}", streamHandler.getId());
            boolean keepAlive = HttpUtil.isKeepAlive(req);

            HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
            if (keepAlive) {
                response.headers().set(CONNECTION, KEEP_ALIVE);
            }
            response.headers().set(CONTENT_TYPE, "video/x-flv");
            response.headers().set(EXPIRES, -1);
            response.headers().set(SERVER, "Black8");
            response.headers().set(TRANSFER_ENCODING, CHUNKED);
            //允许跨域请求
            response.headers().set(ACCESS_CONTROL_ALLOW_ORIGIN, "*");
            response.headers().set(ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
            response.headers().set(DATE, new Date());

            ctx.writeAndFlush(response);

            streamHandler.addSubscriber(new HttpFlvSubscriber(ctx.channel()));
        } else {
            responseMsg(ctx, "不支持的请求");
        }
    }


    private void responseMsg(ChannelHandlerContext ctx, String msg) {
        ByteBuf body = Unpooled.wrappedBuffer((msg).getBytes(Constants.CHARSET));
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                HttpResponseStatus.NOT_FOUND, body);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, TEXT_PLAIN + ";charset=" + Constants.CHARSET.toString());
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }


}
