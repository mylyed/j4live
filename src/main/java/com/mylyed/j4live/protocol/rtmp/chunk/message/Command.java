package com.mylyed.j4live.protocol.rtmp.chunk.message;

import com.mylyed.j4live.protocol.amf.AMF0Object;
import com.mylyed.j4live.protocol.rtmp.chunk.header.ChunkHeader;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 字段	类型	说明
 * Command Name(命令名字)	String	命令的名字，如"connect"
 * Transaction ID(事务ID)	Number	恒为1
 * Command Object(命令包含的参数对象)	Object	键值对集合表示的命令参数
 * Optional User Arguments（额外的用户参数)	Object	用户自定义的额外信息
 *
 * @author lilei
 * created at 2020/4/25
 */
@Getter
@Setter
@ToString
public abstract class Command extends AbstractMessage implements Cloneable {
    //命令名字
    protected String commandName;
    //事务ID
    protected Integer transactionId;
    //命令包含的参数对象
    protected AMF0Object object;
    //额外的用户参数
    protected Object[] args;

    public Command() {
    }

    public Command(String commandName, int transactionId, AMF0Object object, Object[] args) {
        this.commandName = commandName;
        this.transactionId = transactionId;
        this.object = object;
        this.args = args;
    }

    public Command(ChunkHeader header, ByteBuf in) {
        super(header, in);
    }


}
