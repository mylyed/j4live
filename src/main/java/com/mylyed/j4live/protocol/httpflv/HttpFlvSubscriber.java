/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.mylyed.j4live.protocol.httpflv;

import com.mylyed.j4live.protocol.amf.AMF0;
import com.mylyed.j4live.protocol.rtmp.chunk.enums.MessageType;
import com.mylyed.j4live.protocol.rtmp.chunk.header.ChunkHeader;
import com.mylyed.j4live.protocol.rtmp.chunk.message.MediaMessage;
import com.mylyed.j4live.protocol.rtmp.chunk.message.Metadata;
import com.mylyed.j4live.stream.AutoIncrementID;
import com.mylyed.j4live.stream.Stream;
import com.mylyed.j4live.stream.StreamSubscriber;
import com.mylyed.j4live.util.ByteBufAllocatorUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lilei
 * created at 2020/4/30
 */
@Slf4j
@ToString(callSuper = true)
public class HttpFlvSubscriber extends AutoIncrementID implements StreamSubscriber {
    private static final byte[] flvHeader = new byte[]{0x46, 0x4C, 0x56, 0x01, 0x05, 0x00, 0x00, 0x00, 0x09, 0x00, 0x00, 0x00, 0x00};

    private Channel channel;

    public HttpFlvSubscriber(Channel channel) {
        this.channel = channel;
    }

    @Override
    public boolean canWrite() {
        return channel.isActive();
    }

    @Override
    public void write(Stream message) {
        //编码
        if (message instanceof Metadata) {
            channel.writeAndFlush(encodeMetadata((Metadata) message));
        } else if (message instanceof MediaMessage) {
            ByteBuf byteBuf = encodeMediaMessage((MediaMessage) message);
            channel.writeAndFlush(byteBuf);
        } else {
            log.warn("not suport message :{}", message);
        }
    }

    private ByteBuf encodeMediaMessage(MediaMessage mediaMessage) {
        final ChunkHeader header = mediaMessage.getHeader();
        ByteBuf out = ByteBufAllocatorUtil.buffer(header.getMessageLength() + 15);
        if (MessageType.AGGREGATE_MESSAGE.equals(header.getMessageType())) {
            log.warn("AGGREGATE_MESSAGE!!!");
        } else {
            //类型1bit
            out.writeByte(mediaMessage.getMessageType().value().byteValue());
            //长度3bit
            out.writeMedium(header.getMessageLength());
            int timestamp = header.getTimestamp();
            int timestampbase = timestamp & 0x00ffffff;
            int timestampExt = (timestamp >> 24) & 0xff;
            log.trace("time:{}", timestamp);
            //时间戳3bit timestampEXT
            out.writeMedium(timestampbase);
            //1bit
            out.writeByte(timestampExt);
            //流ID 3bit
            out.writeMedium(0);
            int index = out.writerIndex();
            //tag data
            out.writeBytes(mediaMessage.getRaw());
            //
            out.writeInt(header.getMessageLength() + index);
        }
        return out;
    }

    private ByteBuf encodeMetadata(Metadata metadata) {

        ByteBuf metaBuf = Unpooled.buffer();
        AMF0.encodes(metaBuf, metadata.getData().get(0));
//        ((Map) metadata.getData().get(1)).put("duration", -1);
        AMF0.encode(metaBuf, metadata.getData().get(1));

        int length = metaBuf.readableBytes();

        ByteBuf out = ByteBufAllocatorUtil.buffer();
        out.writeBytes(flvHeader);
        //类型1bit
        out.writeByte(MessageType.META_AFM0.value().byteValue());
        //长度3bit
        out.writeMedium(length);
        //时间戳3bit
        out.writeMedium(0);
        //1bit
        out.writeByte(0);
        //流ID 3bit
        out.writeMedium(0);
        int index = out.writerIndex();
        //tag data
        out.writeBytes(metaBuf);
        //
        out.writeInt(length + index);
        return out;
    }

    @Override
    public void close() {
        channel.closeFuture().addListener(ChannelFutureListener.CLOSE);
    }
}
