package com.mylyed.j4live.test;


import com.mylyed.j4live.protocol.rtmp.chunk.UserControlMessageFactory;
import com.mylyed.j4live.protocol.rtmp.chunk.header.ChunkHeader;
import com.mylyed.j4live.protocol.rtmp.chunk.message.ucm.UserControlMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * RtmpMessage 消息测试
 *
 * @author lilei
 * created at 2020/4/26
 */
public class RtmpMessageTest {

    @Test
    public void testUserControlMessages() {
        UserControlMessage userControlMessages = UserControlMessageFactory.streamBegin(1);
        ByteBuf encode = userControlMessages.encode();

        UserControlMessage decode = new UserControlMessage(UserControlMessage.EventType.STREAM_DRY, null);
        decode.decode(encode);

        Assert.assertEquals(userControlMessages, decode);
    }

    @Test
    public void testHeader() {

        ChunkHeader chunkHeader = ChunkHeader.buildFMT3BH(123);
        ByteBuf encode = chunkHeader.encode();
        System.out.println(ByteBufUtil.prettyHexDump(encode));

        ChunkHeader chunkHeaderDecode = new ChunkHeader();
        chunkHeaderDecode.decodeBasicHeader(encode);
        Assert.assertEquals(chunkHeader.getFmt(), chunkHeaderDecode.getFmt());
        Assert.assertEquals(chunkHeader.getCsid(), chunkHeaderDecode.getCsid());

    }


}
