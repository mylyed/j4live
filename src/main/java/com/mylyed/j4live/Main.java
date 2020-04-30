package com.mylyed.j4live;

import com.google.common.collect.Sets;
import com.mylyed.j4live.protocol.Server;
import com.mylyed.j4live.protocol.httpflv.server.HttpFlvServer;
import com.mylyed.j4live.protocol.rtmp.server.RtmpServer;
import com.mylyed.j4live.stream.StreamHandlerManager;

import java.util.Properties;
import java.util.Set;

/**
 * 程序入口
 *
 * @author lilei
 * created at 2020/4/30
 */
public class Main {
    public static void main(String[] args) {
        Properties config = new Properties();
        final StreamHandlerManager streamManager = new StreamHandlerManager();
        final Set<Server> servers = Sets.newHashSet(
                new RtmpServer(streamManager),
                new HttpFlvServer(streamManager));
        servers.forEach(server -> server.config(config));
        servers.forEach(Server::start);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            servers.forEach(Server::shutdown);
        }));

    }
}
