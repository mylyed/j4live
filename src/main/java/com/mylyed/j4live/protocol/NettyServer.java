package com.mylyed.j4live.protocol;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.unix.PreferredDirectByteBufAllocator;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.util.Objects;
import java.util.Properties;

/**
 * netty 抽象类 （模板方法设计模式）
 *
 * @author lilei
 * created at 2020/4/30
 */
@Slf4j
public abstract class NettyServer implements Server {

    protected ServerBootstrap bootstrap;
    protected EventLoopGroup bossGroup;
    protected EventLoopGroup workerGroup;
    protected int port;

    @Override
    public void config(Properties config) {
        log.info("config");
        port = Integer.parseInt(config.getProperty(configPrefix() + ".port", String.valueOf(defaultPort())));
        bootstrap = new ServerBootstrap();
        bossGroup = new NioEventLoopGroup(1);
        //默认线程池大小位CPU核数*2
        workerGroup = new NioEventLoopGroup();
    }

    public void start() {
        log.info("start");
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                //
                .option(ChannelOption.SO_BACKLOG, 128)
                //首选直接内存
                .option(ChannelOption.ALLOCATOR, PreferredDirectByteBufAllocator.DEFAULT)
                //
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) {
                        MDC.put("server", configPrefix());
                        log.debug("initChannel :{}", ch.id().asLongText());
                        NettyServer.this.initChannel(ch);
                        ch.pipeline().addFirst(new LoggingHandler(LogLevel.TRACE));
                    }
                });
        try {
            bootstrap.bind(port).sync();
            log.info("{} server start , listen on :{}", configPrefix(), port);
        } catch (InterruptedException e) {
            log.error(configPrefix() + " server start Exception ", e);
            throw new RuntimeException(e);
        }
    }


    @Override
    public void shutdown() {
        log.info("shutdown");
        if (Objects.nonNull(bossGroup)) {
            bossGroup.shutdownGracefully();
        }
        if (Objects.nonNull(workerGroup)) {
            workerGroup.shutdownGracefully();
        }
    }

    /**
     * 配置前缀
     *
     * @return
     */
    public abstract String configPrefix();

    /**
     * 默认端口
     *
     * @return 端口号
     */
    public abstract int defaultPort();

    /**
     * 初始化管道
     *
     * @param channel 通信管道
     */
    public abstract void initChannel(Channel channel);

}
