package com.mylyed.j4live.protocol.httpflv.server;

import com.mylyed.j4live.protocol.NettyServer;
import com.mylyed.j4live.stream.StreamHandlerManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.cors.CorsConfigBuilder;
import io.netty.handler.codec.http.cors.CorsHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lilei
 * created at 2020/4/28
 */
@Slf4j
public class HttpFlvServer extends NettyServer {

    private StreamHandlerManager streamManager;

    public HttpFlvServer(StreamHandlerManager streamManager) {
        this.streamManager = streamManager;
    }


    @Override
    public String configPrefix() {
        return "httpflv";
    }

    @Override
    public int defaultPort() {
        return 8080;
    }

    @Override
    public void initChannel(Channel channel) {
        ChannelPipeline p = channel.pipeline();
        p.addLast(new HttpServerCodec());
        p.addLast(new HttpObjectAggregator(65536));
        p.addLast(new CorsHandler(CorsConfigBuilder.forAnyOrigin().allowCredentials().allowNullOrigin().build()));
        p.addLast(new HttpFlvHandler(streamManager));
    }
}
