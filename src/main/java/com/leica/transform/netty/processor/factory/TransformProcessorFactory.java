package com.leica.transform.netty.processor.factory;

import com.leica.transform.netty.processor.RtspTransformProcessor;
import com.leica.transform.netty.processor.TransformProcessor;
import com.leica.transform.netty.processor.type.StreamType;

import java.util.EnumMap;

import static com.leica.transform.netty.processor.type.StreamType.RTSP;

/**
 * 转换处理器工厂
 *
 * @author <a href="mailto:705463446@qq.com">ikun</a>
 * @date 2022/9/28 14:23
 */
public class TransformProcessorFactory {

    private static final EnumMap<StreamType, TransformProcessor> PROCESSOR_MAP = new EnumMap<>(StreamType.class);

    static {
        PROCESSOR_MAP.put(RTSP, new RtspTransformProcessor());
    }

    public TransformProcessor findTransformProcessor(StreamType type) {
        return PROCESSOR_MAP.get(type);
    }
}
