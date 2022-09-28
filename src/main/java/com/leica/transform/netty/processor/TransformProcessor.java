package com.leica.transform.netty.processor;

import com.leica.transform.config.TransformConfig;
import com.leica.transform.context.HttpRequestWrapper;

/**
 * 流转换处理器
 *
 * @author <a href="mailto:705463446@qq.com">ikun</a>
 * @date 2022/9/28 14:17
 */
public interface TransformProcessor {

    /**
     * 处理流转换
     *
     * @param requestWrapper request请求包装
     * @param config         转换配置
     */
    void transform(HttpRequestWrapper requestWrapper, TransformConfig config) throws Exception;
}
