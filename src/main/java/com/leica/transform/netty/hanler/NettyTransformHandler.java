package com.leica.transform.netty.hanler;

import com.leica.transform.common.util.ResponseHelper;
import com.leica.transform.config.TransformConfig;
import com.leica.transform.context.HttpRequestWrapper;
import com.leica.transform.netty.processor.TransformProcessor;
import com.leica.transform.netty.processor.factory.TransformProcessorFactory;
import com.leica.transform.netty.processor.type.StreamType;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;

/**
 * netty 视频转换处理器
 *
 * @author <a href="mailto:705463446@qq.com">ikun</a>
 * @date 2022/9/28 13:37
 */
@Slf4j
@ChannelHandler.Sharable
public class NettyTransformHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private static final String URL = "/live";

    private static final String URL_PARAM = "url";

    private final TransformConfig config;

    private final TransformProcessorFactory transformProcessorFactory = new TransformProcessorFactory();

    public NettyTransformHandler(TransformConfig config) {
        this.config = config;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception{
        String uri = request.uri();
        QueryStringDecoder decoder = new QueryStringDecoder(uri);
        List<String> urls = decoder.parameters().get(URL_PARAM);
        if (!Objects.equals(decoder.path(), URL) || urls.size() == 0) {
            // 快速失败
            log.error("#NettyTransformHandler#channelRead0 url error!");
            ResponseHelper.getHttpResponse(HttpResponseStatus.BAD_REQUEST, "请求地址有误!");
            return;
        }
        sendFlvResponse(ctx);
        doProcessTransform(ctx, request, urls.get(0));
    }

    /**
     * 转换流
     *
     * @param ctx     netty上下文
     * @param request http请求对象
     * @param url     视频流地址
     */
    private void doProcessTransform(ChannelHandlerContext ctx, FullHttpRequest request, String url) throws Exception {
        HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(ctx, request, url);
        TransformProcessor transformProcessor = transformProcessorFactory.findTransformProcessor(StreamType.deduceStreamType(url));
        transformProcessor.transform(httpRequestWrapper, config);
    }

    /**
     * 设置 flv 响应头
     *
     * @param ctx netty 上下文
     */
    private void sendFlvResponse(ChannelHandlerContext ctx) {
        HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);

        response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE)
                .set(HttpHeaderNames.CONTENT_TYPE, "video/x-flv").set(HttpHeaderNames.ACCEPT_RANGES, "bytes")
                .set(HttpHeaderNames.PRAGMA, "no-cache").set(HttpHeaderNames.CACHE_CONTROL, "no-cache")
                .set(HttpHeaderNames.TRANSFER_ENCODING, HttpHeaderValues.CHUNKED);
        ctx.writeAndFlush(response);
    }
}
