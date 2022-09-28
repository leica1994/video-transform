package com.leica.transform;

import com.leica.transform.config.TransformConfig;
import com.leica.transform.config.TransformConfigLoader;

/**
 * 视频转换服务入口
 *
 * @author <a href="mailto:705463446@qq.com">ikun</a>
 * @date 2022/9/28 10:45
 */
public class BootStrap {

    public static void main(String[] args) {

        // ==================== 加载配置 ====================
        TransformConfig config = TransformConfigLoader.getInstance().load(args);

        // ==================== 启动容器 ====================
        TransformContainer container = new TransformContainer(config);
        container.start();

        // ==================== 注册关闭事件 ====================
        Runtime.getRuntime().addShutdownHook(new Thread(container::shutdown));
    }

}
