package com.leica.transform;

import com.leica.transform.config.TransformConfig;
import com.leica.transform.netty.NettyHttpServer;

/**
 * 转换容器
 *
 * @author <a href="mailto:705463446@qq.com">ikun</a>
 * @date 2022/9/28 11:00
 */
public class TransformContainer implements Lifecycle {

    private final TransformConfig config;

    private NettyHttpServer nettyHttpServer;

    public TransformContainer(TransformConfig config) {
        this.config = config;
        init();
    }

    @Override
    public void init() {
        this.nettyHttpServer = new NettyHttpServer(config);
    }

    @Override
    public void start() {
        nettyHttpServer.start();
    }

    @Override
    public void shutdown() {
        nettyHttpServer.shutdown();
    }
}
