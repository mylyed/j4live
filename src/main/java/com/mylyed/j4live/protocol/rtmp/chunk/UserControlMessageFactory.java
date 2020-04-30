package com.mylyed.j4live.protocol.rtmp.chunk;

import com.mylyed.j4live.protocol.rtmp.chunk.message.ucm.UserControlMessage;
import com.mylyed.j4live.util.ByteBufAllocatorUtil;
import io.netty.buffer.ByteBuf;
import lombok.experimental.UtilityClass;

/**
 * @author lilei
 * created at 2020/4/27
 */
@UtilityClass
public class UserControlMessageFactory {

    public static UserControlMessage streamBegin(Integer csid) {
        ByteBuf buffer = ByteBufAllocatorUtil.buffer(4);
        buffer.writeInt(csid);
        byte[] eventData = new byte[buffer.readableBytes()];
        buffer.readBytes(eventData);
        return new UserControlMessage(UserControlMessage.EventType.STREAM_BEGIN, eventData);
    }
}
