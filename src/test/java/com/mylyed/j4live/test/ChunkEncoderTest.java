package com.mylyed.j4live.test;

import com.mylyed.j4live.protocol.rtmp.chunk.MessageDecoder;
import com.mylyed.j4live.protocol.rtmp.chunk.header.ChunkHeader;
import com.mylyed.j4live.protocol.rtmp.chunk.message.RtmpMessage;
import com.mylyed.j4live.protocol.rtmp.chunk.message.pcm.SetChunkSize;
import com.mylyed.j4live.protocol.rtmp.server.ChunkEncoder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author lilei
 * created at 2020/4/27
 */
@Slf4j
public class ChunkEncoderTest extends ChunkEncoder {

    @Test
    @SneakyThrows
    public void testEncoder() {
        ChunkEncoderTest chunkEncoder = new ChunkEncoderTest();
        SetChunkSize setChunkSize = new SetChunkSize(5000);
        ByteBuf buffer = Unpooled.buffer();
        chunkEncoder.encode(null, setChunkSize, buffer);
        log.debug("setChunkSize:{}", setChunkSize);
        System.out.println(ByteBufUtil.prettyHexDump(buffer));
        ChunkHeader chunkHeader = new ChunkHeader();

        chunkHeader.decodeBasicHeader(buffer);
        chunkHeader.decodeMessageHeader(buffer, null);
        chunkHeader.decodeExtendedTimestamp(buffer);
        System.out.println(ByteBufUtil.prettyHexDump(buffer));
        RtmpMessage decode = MessageDecoder.decode(chunkHeader, buffer);
        log.debug("decode:{}", decode);

        Assert.assertEquals(setChunkSize, decode);

    }
}
