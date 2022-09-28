package com.leica.transform.common.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.*;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @author leica
 * @date 2022/3/23 23:01
 */
public class ResponseHelper {

    public static FullHttpResponse getHttpResponse(HttpResponseStatus responseCode, String content) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                responseCode,
                Unpooled.wrappedBuffer(content.getBytes(StandardCharsets.UTF_8)));
        response.headers()
                .add(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON + ";charset=utf-8")
                .add(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        return response;
    }
}
