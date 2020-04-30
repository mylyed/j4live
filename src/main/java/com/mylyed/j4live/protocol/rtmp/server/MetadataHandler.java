package com.mylyed.j4live.protocol.rtmp.server;

import com.mylyed.j4live.protocol.rtmp.chunk.message.Metadata;
import com.mylyed.j4live.stream.StreamHandlerManager;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lilei
 * created at 2020/4/27
 */
@Slf4j
public class MetadataHandler extends RtmpMessageHandler<Metadata> {


    public MetadataHandler(StreamHandlerManager streamManager) {
        super(streamManager);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Metadata metadata) throws Exception {
        log.debug("metadata: {}", metadata);
        //etadataAMF0(super=Metadata(
        // name=@setDataFrame, data=[onMetaData,
        // {duration=0.0, fileSize=0.0, width=1920.0, height=1080.0,
        // videocodecid=avc1, videodatarate=10000.0, framerate=30.0,
        // audiocodecid=mp4a, audiodatarate=320.0, audiosamplerate=44100.0,
        // audiosamplesize=16.0, audiochannels=2.0,
        // stereo=true, 2.1=false, 3.1=false, 4.0=false,
        // 4.1=false, 5.1=false,
        // 7.1=false,
        // encoder=obs-output module
        // (libobs version 25.0.4)}]))
        if ("onMetaData".equals(metadata.getName()) || "@setDataFrame".equals(metadata.getName())) {
            tryGetStreamHandler(ctx).setMetadata(metadata);
        }
    }
}
