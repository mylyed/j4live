package com.mylyed.j4live.protocol.rtmp.chunk.message;

import com.mylyed.j4live.protocol.amf.AMF0;
import com.mylyed.j4live.protocol.amf.AMF0Object;
import com.mylyed.j4live.protocol.rtmp.chunk.enums.MessageType;
import com.mylyed.j4live.protocol.rtmp.chunk.header.ChunkHeader;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * AMF0格式命令
 *
 * @author lilei
 * created at 2020/4/25
 */
@ToString(callSuper = true)
public class CommandAMF0 extends Command {


    public CommandAMF0(String commandName, Integer transactionId, AMF0Object object, Object... args) {
        super(commandName, transactionId, object, args);
    }

    public CommandAMF0(ChunkHeader header, ByteBuf in) {
        super(header, in);
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.COMMAND_AMF0;
    }

    @Override
    public ByteBuf encode() {
        ByteBuf out = Unpooled.buffer();
        AMF0.encodes(out, commandName, transactionId, object);
        if (args != null) {
            AMF0.encodes(out, args);
        }
        return out;
    }

    @Override
    public void decode(ByteBuf in) {
        commandName = (String) AMF0.decode(in);
        transactionId = ((Double) AMF0.decode(in)).intValue();
        object = (AMF0Object) AMF0.decode(in);
        List<Object> list = new ArrayList<>();
        while (in.isReadable()) {
            list.add(AMF0.decode(in));
        }
        args = list.toArray();
    }


}
