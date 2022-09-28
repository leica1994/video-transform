package com.leica.transform.netty.processor.type;

import lombok.Getter;

import java.util.Arrays;

/**
 * 视频流类型
 *
 * @author <a href="mailto:705463446@qq.com">ikun</a>
 * @date 2022/9/28 14:20
 */
@Getter
public enum StreamType {

    RTSP("rtsp");

    /**
     * 流类型
     */
    private final String type;

    StreamType(String type) {
        this.type = type;
    }

    public static StreamType deduceStreamType(String url) {
        return Arrays.stream(values()).filter(t -> url.toLowerCase().startsWith(t.getType())).findAny().orElseThrow(() -> new RuntimeException("unsupported stream type!"));
    }
}
