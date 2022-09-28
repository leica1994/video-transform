package com.leica.transform.context;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * http 请求包装类
 *
 * @author <a href="mailto:705463446@qq.com">ikun</a>
 * @date 2022/9/28 14:09
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class HttpRequestWrapper {

    private ChannelHandlerContext ctx;

    private FullHttpRequest request;

    private String url;
}
