package com.mylyed.j4live.protocol.rtmp.chunk.enums;


import com.mylyed.j4live.common.ValueToEnum;

/**
 * Basic Header 的格式  format
 */
public enum Format implements ValueToEnum.NumberValue {
    //这一类型必须用在块流的起始位置
    FMT_0(0),
    //视频格式
    FMT_1(1),
    //音频和一般数据
    FMT_2(2),
    //类型 3 的块没有消息头。
    // 流 ID、消息长度以及 timestamp delta 等字段都不存在；
    // 这种类型的块使用前面块一样的块流 ID。
    // 当单一一个消息被分割为多块时，除了第一块的其他块都应该使用这种类型。
    // 参考例 2 (5.3.2.2 小节)。组成流的消息具有同样的大小，
    // 流 ID 和时间间隔应该在类型 2 之后的所有块都使用这一类型。参考例 1 (5.3.2.1 小节)。
    // 如果第一个消息和第二个消息之间的 delta 和第一个消息的 timestamp 一样的话，
    // 那么在类型 0 的块之后要紧跟一个类型 3 的块，因为无需再来一个类型 2 的块来注册 delta 了。
    // 如果一个类型 3 的块跟着一个类型 0 的块，
    // 那么这个类型 3 块的 timestamp delta 和类型 0 块的 timestamp 是一样的。
    FMT_3(3);
    private final int value;

    Format(int value) {
        this.value = value;
    }


    @Override
    public Number value() {
        return value;
    }

    private static final ValueToEnum<Format> converter = new ValueToEnum<Format>(Format.values());

    public static Format valueToEnum(final int value) {
        return converter.valueToEnum(value);
    }

}