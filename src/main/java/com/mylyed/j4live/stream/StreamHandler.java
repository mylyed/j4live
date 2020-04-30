/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.mylyed.j4live.stream;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * 流处理器
 *
 * @author lilei
 * created at 2020/4/30
 */
@Slf4j
public class StreamHandler extends AutoIncrementID {

    private final Collection<StreamSubscriber> subscribers = new ConcurrentLinkedDeque<>();

    private final StreamHandlerName streamName;
    //元信息
    private Stream metadata;
    //配置消息
    private List<Stream> configMessages;

    public StreamHandler(StreamHandlerName streamName) {
        this.streamName = streamName;
        configMessages = new ArrayList<>(2);
    }

    public StreamHandlerName getStreamName() {
        return streamName;
    }

    public void setMetadata(Stream metadata) {
        configMessages.clear();
        this.metadata = metadata;
    }

    public void addConfigMessage(Stream rtmpMessage) {
        configMessages.add(rtmpMessage);
    }

    /**
     * 注册writer
     */
    public void addSubscriber(StreamSubscriber streamSubscriber) {
        log.debug("addSubscriber:{}", streamSubscriber);
        log.debug("write metadata");
        streamSubscriber.write(metadata);
        for (Stream configMessage : configMessages) {
            log.debug("write configMessage :{}", configMessage);
            streamSubscriber.write(configMessage);
        }

        subscribers.add(streamSubscriber);
    }

    /**
     * 移除writer
     */
    public void removeSubscriber(StreamSubscriber streamSubscriber) {
        subscribers.remove(streamSubscriber);
    }

    /**
     * @param rtmpMessage
     */
    public void broadcast(Stream rtmpMessage) {
        //todo
        subscribers.parallelStream().filter(StreamSubscriber::canWrite).forEach(subscriber -> subscriber.write(rtmpMessage));
    }

    public void close() {
        subscribers.parallelStream().filter(StreamSubscriber::canWrite).forEach(StreamSubscriber::close);

    }

}
