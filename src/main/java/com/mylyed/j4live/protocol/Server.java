package com.mylyed.j4live.protocol;

import java.util.Properties;

/**
 * @author lilei
 * created at 2020/4/30
 */
public interface Server {
    /**
     * 配置
     *
     * @param config 配置数据
     */
    void config(Properties config);

    /**
     * 启动
     */
    void start();

    /**
     * 关闭
     */
    void shutdown();
}
