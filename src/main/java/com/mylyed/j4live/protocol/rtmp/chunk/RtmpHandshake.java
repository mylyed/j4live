package com.mylyed.j4live.protocol.rtmp.chunk;

import com.mylyed.j4live.util.ByteBufAllocatorUtil;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;

import static com.mylyed.j4live.protocol.rtmp.chunk.RtmpConstants.RTMP_VERSION;

/**
 * @author lilei
 * created at 2020/4/24
 * 官方协议地址：https://wwwimages2.adobe.com/content/dam/acom/en/devnet/rtmp/pdf/rtmp_specification_1.0.pdf
 */
@Slf4j
public class RtmpHandshake {
    public final static int HANDSHAKE_SIZE = 1536;
    public final static int VERSION_SIZE = 1;
    private static byte S0 = RTMP_VERSION;

    //
    byte client0;
    byte[] client1 = new byte[HANDSHAKE_SIZE];
    byte[] client2 = new byte[HANDSHAKE_SIZE];

    //生成随机
    private static ByteBuf generateRandomData(int size) {
        byte[] randomBytes = new byte[size];
        Random random = new Random();
        random.nextBytes(randomBytes);
        return ByteBufAllocatorUtil.wrappedBuffer(randomBytes);
    }

    /**
     * 解码c0c1
     *
     * @param in
     */
    public void decodeClient0And1(ByteBuf in) {
        decodeClient0(in.readBytes(1));
        decodeClient1(in.readBytes(HANDSHAKE_SIZE));
    }

    private void decodeClient0(ByteBuf in) {
        client0 = in.readByte();
        log.debug("client0: {}", client0);
    }

    //目前c1 c2 没有进行解码处理
    private void decodeClient1(ByteBuf in) {
        in.readBytes(client1);
    }

    public void decodeClient2(ByteBuf buf) {
        buf.readBytes(client2);
    }

    /**
     * C0和S0是一个8位组，被认为是一个8位整数段。
     * 对C0而言，这个字段被认为是客户端RTMP请求的版本；
     * 对S0而言，这个字段被认为是服务端选择的RTMP版本。
     * 默认是3，0-2被废弃，4-31被用于未来的版本，32-255没有被允许
     *
     * @return
     */
    public ByteBuf encodeServer0() {
        ByteBuf out = ByteBufAllocatorUtil.buffer(1);
        //默认是3
        out.writeByte(S0);
        return out;
    }

    /**
     * C1和S1是一个1536位的长度组，包含如下字段：
     * 字段名含义大小时间(Time)包含时间戳，可能是0也可能是任意的数值，
     * 用于时间标识4字节零(Zero)必须都是04字节任意数据(Random data)任意数值
     * ，并不需要加密1528字节
     * <pre>
     * 0                   1                   2                   3
     * 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * |                        time (4 bytes)                         |
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * |                        zero (4 bytes)                         |
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * |                         random bytes                          |
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * |                         random bytes                          |
     * |                             (cont)                            |
     * |                              ....                             |
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * </pre>
     *
     * @return
     */
    public ByteBuf encodeServer1() {
        ByteBuf out = ByteBufAllocatorUtil.buffer(HANDSHAKE_SIZE);
        // s1 time
        out.writeInt(0);
        // s1 zero
        out.writeInt(0);
        // 搞点随机数据
        out.writeBytes(generateRandomData(HANDSHAKE_SIZE - 8));
        return out;
    }

    /**
     * S2是一个1536位的长度组，对应S1和C1依次的回复，包含如下字段：
     * 字段名含义大小时间1(Time1)时间戳，
     * S1(C2发送)或C1(S2发送)4字节时间2(Time2)时间戳，
     * 之前S1或C14字节任意数据(Random data)任意数值，可快速验证连接带宽或延时1528字节
     * <pre>
     *      0                   1                   2                   3
     *  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * |                        time (4 bytes)                         |
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * |                        time2 (4 bytes)                        |
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * |                         random bytes                          |
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * |                         random bytes                          |
     * |                             (cont)                            |
     * |                              ....                             |
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * </pre>
     *
     * @return
     */
    public ByteBuf encodeServer2() {
        ByteBuf out = ByteBufAllocatorUtil.buffer(HANDSHAKE_SIZE);
        out.writeInt(0);
        out.writeInt(0);
        out.writeBytes(generateRandomData(HANDSHAKE_SIZE - 8));
        return out;
    }
}
