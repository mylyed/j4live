package com.mylyed.j4live.protocol.rtmp.chunk.message.ucm;


import com.mylyed.j4live.common.ValueToEnum;
import com.mylyed.j4live.protocol.rtmp.chunk.enums.MessageType;
import com.mylyed.j4live.protocol.rtmp.chunk.header.ChunkHeader;
import com.mylyed.j4live.protocol.rtmp.chunk.message.AbstractMessage;
import com.mylyed.j4live.util.ByteBufAllocatorUtil;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.ToString;

import java.util.Arrays;
import java.util.Objects;

/**
 * <pre>
 *  +------------------------------+-------------------------
 *  | Event Type (16 bits) | Event Data
 *  +------------------------------+-------------------------
 *  Payload for the ‘User Control’ protocol message
 * </pre>
 *
 * @author lilei
 * created at 2020/4/25
 */
@Getter
@ToString
public class UserControlMessage extends AbstractMessage {
    private EventType eventType;
    private byte[] eventData;

    /**
     * 它的 Event Type 一共有 6 种格式+
     * Stream Begin(0)，
     * Stream EOF(1)，
     * StreamDry(2)，
     * SetBuffer Length(3)，
     * StreamIs Recorded(4)，
     * PingRequest(6)，
     * PingResponse(7)。
     */
    public enum EventType implements ValueToEnum.NumberValue {
        /**
         * 服务器发送这个事件来通知客户端一个流已就绪并可以用来通信。默认情况下，这一事件在成功接收到客户端的应用连接命令之后以 ID 0 发送。这一事件数据为 4 字节，代表了已就绪流的流 ID。
         */
        STREAM_BEGIN(0),
        /**
         * 服务器端发送这一事件来通知客户端请求的流的回放数据已经结束。在发送额外的命令之前不再发送任何数据。客户端将丢弃接收到的这个流的消息。这一事件数据为 4 字节，代表了回放已结束的流的流 ID。
         */
        STREAM_EOF(1),
        /**
         * 服务器端发送这一事件来通知客户端当前流中已没有数据。当服务器端在一段时间内没有检测到任何消息，它可以通知相关客户端当前流已经没数据了。这一事件数据为 4 字节，代表了已没数据的流的流 ID。
         */
        STREAM_DRY(2),
        /**
         * 客户端发送这一事件来通知服务器端用于缓存流中任何数据的缓存大小 (以毫秒为单位)。这一事件在服务器端开始处理流之前就发送。这一事件数据的前 4 个字节代表了流 ID 后 4 个字节代表了以毫秒为单位的缓存的长度。
         */
        SET_BUFFER_LENGTH(3),
        /**
         * 服务器端发送这一事件来通知客户端当前流是一个录制流。这一事件数据为 4 字节，代表了录制流的流 ID。
         */
        STREAM_IS_RECORDED(4),
        /**
         * 服务器端发送这一事件用于测试是否能够送达客户端。时间数据是为一个 4 字节的 timestamp，
         * <p>
         * 代表了服务器端发送这一命令时的服务器本地时间。客户端在接收到这一消息后会立即发送 PingResponse 回复。
         */
        PING_REQUEST(6),
        /**
         * 客户端作为对 ping 请求的回复发送这一事件到服务器端。这一事件数据是为一个 4 字节的 timestamp，就是接收自 PingRequest 那个。
         */
        PING_RESPONSE(7);

        private final short value;

        EventType(Integer value) {
            this.value = value.shortValue();
        }

        @Override
        public Short value() {
            return value;
        }

        private static final ValueToEnum<EventType> converter = new ValueToEnum<EventType>(EventType.values());

        public static EventType valueToEnum(final int value) {
            return converter.valueToEnum(value);
        }

    }


    public UserControlMessage(ChunkHeader header, ByteBuf in) {
        super(header, in);
    }

    public UserControlMessage(EventType eventType, byte[] eventData) {
        this.eventType = eventType;
        this.eventData = eventData;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.USER_CONTROL_MESSAGE;
    }

    @Override
    public ByteBuf encode() {
        ByteBuf out = ByteBufAllocatorUtil.buffer(16 + eventData.length);
        out.writeShort(eventType.value);
        out.writeBytes(eventData);
        return out;
    }

    @Override
    public void decode(ByteBuf in) {
        eventType = EventType.valueToEnum(in.readShort());
        eventData = new byte[in.readableBytes()];
        in.readBytes(eventData);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserControlMessage that = (UserControlMessage) o;
        return eventType == that.eventType &&
                Arrays.equals(eventData, that.eventData);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(eventType);
        result = 31 * result + Arrays.hashCode(eventData);
        return result;
    }
}
