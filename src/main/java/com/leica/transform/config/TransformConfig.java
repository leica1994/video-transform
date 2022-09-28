package com.leica.transform.config;

import lombok.Data;

/**
 * 转换基本配置
 *
 * @author <a href="mailto:705463446@qq.com">ikun</a>
 * @date 2022/9/28 10:49
 */
@Data
public class TransformConfig {

    /**
     * 端口号,默认8888
     */
    private int port = 8888;

    /**
     * 是否开启epoll
     */
    private boolean useEpoll = true;

    /**
     * 服务器的CPU核数映射的线程数
     */
    private int processThread = Runtime.getRuntime().availableProcessors();

    /**
     * Netty的Boss线程数
     */
    private int eventLoopGroupBoosNumber = 1;

    /**
     * Netty的worker线程数
     */
    private int eventLoopGroupWorkerNumber =200;

    /**
     * 是否使用netty Allocator
     */
    private boolean nettyAllocator = true;

    /**
     * 最大内容长度
     */
    private Integer maxContentLength = 65535;
}
