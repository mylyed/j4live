package com.mylyed.j4live.stream;

/**
 * @author lilei
 * created at 2020/4/30
 */
public interface StreamSubscriber {

    boolean canWrite();

    void write(Stream message);

    void close();

}
