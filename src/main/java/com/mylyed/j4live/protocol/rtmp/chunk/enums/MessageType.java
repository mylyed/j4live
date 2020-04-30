package com.mylyed.j4live.protocol.rtmp.chunk.enums;

import com.mylyed.j4live.common.ValueToEnum;

/**
 * https://segmentfault.com/img/remote/1460000010519580
 *
 * @author lilei
 * created at 2020/4/24
 */
public enum MessageType implements ValueToEnum.NumberValue {

    // 设置类型
    SET_CHUNK_SIZE(1),//设置块类型
    ABORT_MESSAGE(2),//终止消息
    ACKNOWLEDGEMENT(3),//确认
    WINDOW_ACKNOWLEDGEMENT_SIZE(5),// 窗口确认大小
    SET_PEER_BANDWIDTH(6),//设置对端带宽
    // 命令消息
    COMMAND_AMF0(20),
    COMMAND_AMF3(17),
    // 数据消息
    META_AFM0(18),
    META_AFM3(15),
    // 共享对象消息
    //　　所谓共享对象其实是一个 Flash 对象 (一个名值对的集合)，
    // 这个对象在多个不同客户端、应用实例中保持同步。
    // 消息类型 19 用于 AMF0 编码、
    // 16 用于 AMF3 编码都被为共享对象事件保留。
    // 每个消息可以包含有不同事件。
    SHARED_OBJECT_MESSAGE_AMF0(19),
    SHARED_OBJECT_MESSAGE_AMF3(16),

    AUDIO_MESSAGE(8),
    VIDEO_MESSAGE(9),//视频消息
    AGGREGATE_MESSAGE(22),//统计消息

    USER_CONTROL_MESSAGE(4);// 用户控制信息

    private final int value;


    MessageType(final int value) {
        this.value = value;
    }

    @Override
    public Integer value() {
        return value;
    }

    private static final ValueToEnum<MessageType> converter = new ValueToEnum<>(MessageType.values());

    public static MessageType valueToEnum(final int value) {
        return converter.valueToEnum(value);
    }

}
