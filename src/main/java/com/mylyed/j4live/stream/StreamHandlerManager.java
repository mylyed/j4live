package com.mylyed.j4live.stream;

import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;


/**
 * @author lilei
 * created at 2020/4/30
 */
@Getter
@Setter
@ToString(callSuper = true)
public class StreamHandlerManager extends AutoIncrementID {

    Map<StreamHandlerName, StreamHandler> streamHandlers = Maps.newConcurrentMap();

    public StreamHandler getStreamHandler(StreamHandlerName streamName) {
        return streamHandlers.get(streamName);
    }

    public void addStreamHandler(StreamHandler streamHandler) {
        streamHandlers.put(streamHandler.getStreamName(), streamHandler);
    }

    public void removeStreamHandler(StreamHandler streamHandler) {
        streamHandler.close();
        streamHandlers.remove(streamHandler.getStreamName());
    }

}
