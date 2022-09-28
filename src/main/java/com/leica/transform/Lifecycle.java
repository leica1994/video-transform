package com.leica.transform;

/**
 * 基础声明周期
 *
 * @author <a href="mailto:705463446@qq.com">ikun</a>
 * @date 2022/9/28 10:45
 */
public interface Lifecycle {

    /**
     * 初始化
     */
    void init();

    /**
     * 启动
     */
    void start();

    /**
     * 关闭
     */
    void shutdown();
}
