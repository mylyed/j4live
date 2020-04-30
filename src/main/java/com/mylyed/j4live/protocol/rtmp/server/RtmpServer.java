package com.mylyed.j4live.protocol.rtmp.server;

import com.mylyed.j4live.protocol.NettyServer;
import com.mylyed.j4live.stream.StreamHandlerManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lilei
 * created at 2020/4/24
 */
@Slf4j
public class RtmpServer extends NettyServer {
    private StreamHandlerManager streamManager;

    public RtmpServer(StreamHandlerManager streamManager) {
        this.streamManager = streamManager;
    }

    @Override
    public String configPrefix() {
        return "rtmp";
    }

    @Override
    public int defaultPort() {
        return 1935;
    }

    @Override
    public void initChannel(Channel channel) {
        ChannelPipeline pipeline = channel.pipeline();
        //握手
        pipeline.addLast(new HandshakeHandler());
        //解码
        pipeline.addLast(new ChunkDecoder());
        //编码
        pipeline.addLast(new ChunkEncoder());
        //块处理
        pipeline.addLast(new ChunkHandler());
        //命令处理
        pipeline.addLast("commandHandler", new CommandHandler(streamManager));
        //元数据处理
        pipeline.addLast("metadataHandler", new MetadataHandler(streamManager));
        //媒体数据
        pipeline.addLast("mediaHandler", new MediaHandler(streamManager));
    }
}
