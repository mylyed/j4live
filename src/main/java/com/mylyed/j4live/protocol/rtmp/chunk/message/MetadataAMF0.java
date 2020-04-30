package com.mylyed.j4live.protocol.rtmp.chunk.message;


import com.mylyed.j4live.protocol.amf.AMF0;
import com.mylyed.j4live.protocol.rtmp.chunk.enums.MessageType;
import com.mylyed.j4live.protocol.rtmp.chunk.header.ChunkHeader;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * AMF0 编码的元数据
 *
 * @author lilei
 * created at 2020/4/25
 */
@ToString(callSuper = true)
public class MetadataAMF0 extends Metadata {
    public MetadataAMF0(ChunkHeader header, ByteBuf in) {
        super(header, in);
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.META_AFM0;
    }

    @Override
    public ByteBuf encode() {
        ByteBuf buffer = Unpooled.buffer();
        AMF0.encode(buffer, name);
        AMF0.encode(buffer, data);
        return buffer;
    }

    @Override
    public void decode(ByteBuf in) {
        name = (String) AMF0.decode(in);
        List<Object> list = new ArrayList<Object>();
        while (in.isReadable()) {
            list.add(AMF0.decode(in));
        }
        data = list;
    }
}
