/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.mylyed.j4live.stream;

import io.netty.buffer.ByteBuf;

/**
 * 流的定义
 * <p>
 * 可以被编码
 *
 * @author lilei
 * created at 2020/4/30
 */
public interface Stream {
    /**
     * 编码
     *
     * @return
     */
    ByteBuf encode();
}
