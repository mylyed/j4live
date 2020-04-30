package com.mylyed.j4live.protocol.rtmp.chunk.header;

import com.mylyed.j4live.protocol.rtmp.chunk.RtmpConstants;
import com.mylyed.j4live.protocol.rtmp.chunk.enums.Format;
import com.mylyed.j4live.protocol.rtmp.chunk.enums.MessageType;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;


/**
 * rtmp块头 Chunk Header
 * <p>
 * <pre>
 * +---------------+----------------+--------------------+----------------+
 * |  Basic Header | Message Header | Extended Timestamp |   Chunk Data   |
 * +---------------+----------------+--------------------+----------------+
 * |                                                     |
 * | <------------------ Chunk Header -----------------> |
 * Chunk Format
 * </pre>
 *
 * @author lilei
 * created at 2020/4/24
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class ChunkHeader {
    //格式
    private Format fmt;
    // 块流ID
    private Integer csid;
    //
    private Integer timestamp;
    private Integer timestampDelta;
    //
    private Integer messageLength;
    private MessageType messageType;
    private Integer messageStreamId;

    //扩展时间戳
    //当时间戳大于 16777215（0xFFFFFF）时使用
    private Integer extendedTimestamp;


    public ChunkHeader() {
        //初始化时间
        timestamp = timestampDelta = extendedTimestamp = 0;
    }

    /**
     * 解码 Basic Header
     *
     * @param in
     */
    public void decodeBasicHeader(ByteBuf in) {
        final int firstByteInt = in.readByte();
        final int typeAndChannel;
        final int headerTypeInt;
        //todo 解析过程注释
        if ((firstByteInt & 0x3f) == 0) {
            typeAndChannel = (firstByteInt & 0xff) << 8 | (in.readByte() & 0xff);
            csid = 64 + (typeAndChannel & 0xff);
            headerTypeInt = typeAndChannel >> 14;
        } else if ((firstByteInt & 0x3f) == 1) {
            typeAndChannel = (firstByteInt & 0xff) << 16 | (in.readByte() & 0xff) << 8 | (in.readByte() & 0xff);
            csid = 64 + ((typeAndChannel >> 8) & 0xff) + ((typeAndChannel & 0xff) << 8);
            headerTypeInt = typeAndChannel >> 22;
        } else {
            typeAndChannel = firstByteInt & 0xff;
            csid = (typeAndChannel & 0x3f);
            headerTypeInt = typeAndChannel >> 6;
        }
        fmt = Format.valueToEnum(headerTypeInt);
    }

    /**
     * 解码 Message Header
     *
     * @param in
     * @param prevHeader
     */
    public void decodeMessageHeader(ByteBuf in, final ChunkHeader prevHeader) {
        Objects.requireNonNull(in);
        if (fmt != Format.FMT_0) {
            Objects.requireNonNull(prevHeader);
        }
        switch (fmt) {
            case FMT_0:
                fmt0(in);
                break;
            case FMT_1:
                fmt1(in, prevHeader);
                break;
            case FMT_2:
                fmt2(in, prevHeader);
                break;
            case FMT_3:
                fmt3(prevHeader);
                break;
        }
    }

    /**
     * <pre>
     * 0                   1                   2                   3
     * 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * |                   timestamp                   | message length|
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * |   message length (cont)       |message type id| msg stream id |
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * |            message stream id (cont)           |
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * Chunk Message Header - Type 0
     * </pre>
     *
     * @param in
     */
    private void fmt0(ByteBuf in) {
        timestamp = in.readMedium();
        messageLength = in.readMedium();
        messageType = MessageType.valueToEnum(in.readByte());
        messageStreamId = in.readIntLE();
    }

    /**
     * Type 1：7字节
     * <p>
     * 0                   1                   2                   3
     * 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * |              timestamp delta                  | message length|
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * |   message length (cont)       |message type id|
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * Chunk Message Header - Type 1
     *
     * @param in
     */
    private void fmt1(ByteBuf in, ChunkHeader prevHeader) {
        timestampDelta = in.readMedium();
        messageLength = in.readMedium();
        messageType = MessageType.valueToEnum(in.readByte());
        //
        messageStreamId = prevHeader.messageStreamId;

    }

    /**
     * <pre>
     *     0                   1                   2
     *  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * |              timestamp delta                  |
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     *          Chunk Message Header - Type 2
     * </pre>
     *
     * @param in
     */
    private void fmt2(ByteBuf in, ChunkHeader prevHeader) {
        timestampDelta = in.readMedium();
        //
        messageLength = prevHeader.messageLength;
        messageType = prevHeader.messageType;
        messageStreamId = prevHeader.messageStreamId;
    }

    /**
     * Type 3：没有头
     *
     * @param prevHeader
     */
    private void fmt3(ChunkHeader prevHeader) {
        timestamp = prevHeader.timestamp;
        timestampDelta = prevHeader.timestampDelta;
        messageLength = prevHeader.messageLength;
        messageType = prevHeader.messageType;
        messageStreamId = prevHeader.messageStreamId;
    }


    public void decodeExtendedTimestamp(ByteBuf in) {
        if (Format.FMT_3.equals(this.getFmt())) {
            this.extendedTimestamp = null;
        } else if (
                Objects.equals(getTimestamp(), RtmpConstants.MAX_TIMESTAMP)
                        ||
                        Objects.equals(getTimestampDelta(), RtmpConstants.MAX_TIMESTAMP)
        ) {
            this.extendedTimestamp = in.readInt();
        }
    }

    //--------------------编码

    /**
     * 编码 BasicHeader
     *
     * @return
     */
    private ByteBuf encodeBasicHeader() {
        ByteBuf out = Unpooled.buffer();
        if (csid <= 63) {
            out.writeBytes(new byte[]{(byte) ((fmt.value().intValue() << 6) + csid)});
        } else if (csid <= 320) {
            out.writeBytes(new byte[]{(byte) (fmt.value().intValue() << 6), (byte) (csid - 64)});
        } else {
            out.writeBytes(new byte[]{(byte) ((fmt.value().intValue() << 6) | 1), (byte) ((csid - 64) & 0xff), (byte) ((csid - 64) >> 8)});
        }
        return out;
    }

    /**
     * 编码
     *
     * @return
     */
    public ByteBuf encode() {
        ByteBuf out = Unpooled.buffer();
        //---- Basic Header
        out.writeBytes(encodeBasicHeader());
        if (fmt == Format.FMT_3) {
            return out;
        }
        //--- Message Header

        boolean needExtraTime;
        if (fmt == Format.FMT_1) {
            needExtraTime = timestamp >= RtmpConstants.MAX_TIMESTAMP;
        } else {
            //当 fmt 不是FMT_1时 timestampDelta 从上一个块头取的 timestampDelta
            needExtraTime = timestampDelta >= RtmpConstants.MAX_TIMESTAMP;
        }
        //写入时间戳
        if (needExtraTime) {
            out.writeMedium(RtmpConstants.MAX_TIMESTAMP);
        } else {
            out.writeMedium(fmt == Format.FMT_1 ? timestamp : timestampDelta);
        }
        //消息长度
        out.writeMedium(messageLength);
        //消息类型
        out.writeByte(messageType.value().byteValue());
        //messageStreamId
        out.writeIntLE(messageStreamId);
        //写入扩展时间
        if (needExtraTime) {
            out.writeMedium(fmt == Format.FMT_1 ? timestamp : timestampDelta);
        }
        return out;
    }


    /**
     * 构造一个空白的FMT3的块头
     *
     * @param csid
     * @return
     */
    public static ChunkHeader buildFMT3BH(Integer csid) {
        ChunkHeader chunkHeader = new ChunkHeader();
        chunkHeader.setFmt(Format.FMT_3);
        chunkHeader.setCsid(csid);
        return chunkHeader;
    }


}
