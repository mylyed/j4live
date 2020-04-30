package com.mylyed.j4live.protocol.rtmp.chunk.message;


import com.mylyed.j4live.protocol.rtmp.chunk.header.ChunkHeader;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 客户端或者服务器端通过发送这些消息以发送元数据或者任何用户数据到对端。
 * <p>
 * 元数据包括数据 (音频，视频等等) 的详细信息，比如创建时间，时长，主题等等。
 * <p>
 * 这些消息被分配以消息类型为 18 以进行 AMF0 编码和消息类型 15 以进行 AMF3 编码。
 * <p>
 * 例如：
 * MetadataAMF0(super=Metadata(
 * name=@setDataFrame,
 * data=[
 * onMetaData,
 * {duration=0.0,
 * fileSize=0.0,
 * width=1920.0,
 * height=1080.0,
 * videocodecid=avc1,
 * videodatarate=10000.0,
 * framerate=30.0,
 * audiocodecid=mp4a,
 * audiodatarate=320.0,
 * audiosamplerate=44100.0,
 * audiosamplesize=16.0,
 * audiochannels=2.0,
 * stereo=true,
 * 2.1=false,
 * 3.1=false,
 * 4.0=false,
 * 4.1=false,
 * 5.1=false,
 * 7.1=false,
 * encoder=obs-output module (libobs version 25.0.4)}]))
 *
 * @author lilei
 * created at 2020/4/25
 */
@Getter
@Setter
@ToString
public abstract class Metadata extends AbstractMessage {

    protected String name;
    protected List<Object> data;

    public Metadata(ChunkHeader header, ByteBuf in) {
        super(header, in);
    }


}
