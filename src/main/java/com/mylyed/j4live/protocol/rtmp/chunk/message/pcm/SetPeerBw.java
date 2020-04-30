package com.mylyed.j4live.protocol.rtmp.chunk.message.pcm;

import com.mylyed.j4live.common.ValueToEnum;
import com.mylyed.j4live.protocol.rtmp.chunk.enums.MessageType;
import com.mylyed.j4live.protocol.rtmp.chunk.header.ChunkHeader;
import com.mylyed.j4live.protocol.rtmp.chunk.message.AbstractMessage;
import com.mylyed.j4live.util.ByteBufAllocatorUtil;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.ToString;

/**
 * 根据网速来改变发送包的大小。它的格式和 WAS 类似，不过后面带上了一个 Type 用来标明当前带宽限制算法。当一方接收到该信息后，如果设置的 window size 和前面的 WAS 不一致，需要返回一个 WAS 来进行显示改变。
 *
 * @author lilei
 * created at 2020/4/25
 */
@Getter
@ToString
public class SetPeerBw extends AbstractMessage {
    private Integer acknowledgementWindowSize;
    private LimitType limitType;

    /**
     * 0: Hard，表示当前带宽需要和当前设置的 window size 匹配
     * <p>
     * 1: Soft，将当前宽带设置为该信息定义的 window size，或者已经生效的 window size。
     * 主要取决于谁的 window size 更小
     * <p>
     * 2: Dynamic，如果前一个 Limit Type 为 Hard 那么，继续使用 Hard 为基准，否则忽略该次协议信息。
     */
    enum LimitType implements ValueToEnum.NumberValue {
        HARD(0),
        SOFT(1),
        DYNAMIC(2);


        Byte value;

        LimitType(Integer value) {
            this.value = value.byteValue();
        }

        @Override
        public Byte value() {
            return value;
        }

        private static final ValueToEnum<LimitType> converter = new ValueToEnum<>(LimitType.values());

        public static LimitType valueToEnum(final int value) {
            return converter.valueToEnum(value);
        }

    }

    public SetPeerBw(ChunkHeader header, ByteBuf in) {
        super(header, in);
    }

    public SetPeerBw(Integer acknowledgementWindowSize, LimitType limitType) {
        this.acknowledgementWindowSize = acknowledgementWindowSize;
        this.limitType = limitType;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.SET_PEER_BANDWIDTH;
    }

    @Override
    public ByteBuf encode() {
        ByteBuf buffer = ByteBufAllocatorUtil.buffer(5);
        buffer.writeInt(acknowledgementWindowSize);
        buffer.writeByte(limitType.value);
        return buffer;
    }

    @Override
    public void decode(ByteBuf in) {
        acknowledgementWindowSize = in.readInt();
        limitType = LimitType.valueToEnum(in.readByte());
    }


    //工厂方法

    public static SetPeerBw hard(Integer acknowledgementWindowSize) {
        return new SetPeerBw(acknowledgementWindowSize, LimitType.HARD);
    }

    public static SetPeerBw soft(Integer acknowledgementWindowSize) {
        return new SetPeerBw(acknowledgementWindowSize, LimitType.SOFT);
    }

    public static SetPeerBw dynamic(Integer acknowledgementWindowSize) {
        return new SetPeerBw(acknowledgementWindowSize, LimitType.DYNAMIC);
    }
}
