package com.leica.transform.netty;

import com.leica.transform.Lifecycle;
import com.leica.transform.common.util.RemotingHelper;
import com.leica.transform.common.util.RemotingUtil;
import com.leica.transform.config.TransformConfig;
import com.leica.transform.netty.hanler.NettyTransformHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerExpectContinueHandler;
import io.netty.handler.codec.http.cors.CorsConfigBuilder;
import io.netty.handler.codec.http.cors.CorsHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * netty 服务器
 *
 * @author <a href="mailto:705463446@qq.com">ikun</a>
 * @date 2022/9/28 11:10
 */
@Slf4j
public class NettyHttpServer implements Lifecycle {

    private final TransformConfig config;

    private int port = 8888;

    private ServerBootstrap serverBootstrap;

    private EventLoopGroup bossGroup;

    private EventLoopGroup workerGroup;

    public NettyHttpServer(TransformConfig config) {
        this.config = config;
        if (config.getPort() > 0 && config.getPort() < 65535) {
            port = config.getPort();
        }
        init();
    }

    @Override
    public void init() {
        this.serverBootstrap = new ServerBootstrap();
        if (useEpoll()) {
            this.bossGroup = new EpollEventLoopGroup(this.config.getEventLoopGroupWorkerNumber());
            this.workerGroup = new EpollEventLoopGroup(this.config.getEventLoopGroupWorkerNumber());
        } else {
            this.bossGroup = new NioEventLoopGroup(this.config.getEventLoopGroupBoosNumber());
            this.workerGroup = new NioEventLoopGroup(this.config.getEventLoopGroupWorkerNumber());
        }
    }

    @Override
    public void start() {
        ServerBootstrap bootstrap = this.serverBootstrap
                .channel(useEpoll() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                .group(bossGroup, workerGroup)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .option(ChannelOption.SO_REUSEADDR, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, false)
                .childOption(ChannelOption.SO_RCVBUF, 128 * 1024)
                .childOption(ChannelOption.SO_SNDBUF, 1024 * 1024)
                .childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(1024 * 1024 / 2, 1024 * 1024))
                .localAddress(this.port)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        ChannelPipeline pipeline = channel.pipeline();
                        pipeline.addLast(new HttpServerCodec());
                        pipeline.addLast(new HttpObjectAggregator(config.getMaxContentLength()));
                        pipeline.addLast(new CorsHandler(CorsConfigBuilder.forAnyOrigin().allowNullOrigin().allowCredentials().build()));
                        pipeline.addLast(new HttpServerExpectContinueHandler());
                        pipeline.addLast(new NettyServerConnectManagerHandler());
                        pipeline.addLast(new NettyTransformHandler(config));
                    }
                });

        if (this.config.isNettyAllocator()) {
            bootstrap.option(ChannelOption.ALLOCATOR, ByteBufAllocator.DEFAULT);
        }

        try {
            this.serverBootstrap.bind().sync();
            log.info("< ============= Transform Server StartUp On Port: " + this.port + "================ >");
        } catch (InterruptedException e) {
            throw new RuntimeException("this.serverBootstrap.bind().sync() fail!", e);
        }
    }

    @Override
    public void shutdown() {
        if (this.bossGroup != null) {
            this.bossGroup.shutdownGracefully();
        }

        if (this.workerGroup != null) {
            this.workerGroup.shutdownGracefully();
        }
    }

    private boolean useEpoll() {
        return this.config.isUseEpoll() && RemotingUtil.isLinuxPlatform() && Epoll.isAvailable();
    }

    private static class NettyServerConnectManagerHandler extends ChannelDuplexHandler {

        @Override
        public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
            log.debug("#NettyServerConnectManagerHandler#channelRegistered {}", RemotingHelper.parseChannelRemoteAddr(ctx.channel()));
            super.channelRegistered(ctx);
        }

        @Override
        public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
            log.debug("#NettyServerConnectManagerHandler#channelUnregistered {}", RemotingHelper.parseChannelRemoteAddr(ctx.channel()));
            super.channelUnregistered(ctx);
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            log.debug("#NettyServerConnectManagerHandler#channelActive {}", RemotingHelper.parseChannelRemoteAddr(ctx.channel()));
            super.channelActive(ctx);
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            log.debug("#NettyServerConnectManagerHandler#channelInactive {}", RemotingHelper.parseChannelRemoteAddr(ctx.channel()));
            super.channelInactive(ctx);
        }

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            if (evt instanceof IdleStateEvent) {
                IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
                if (Objects.equals(idleStateEvent.state(), IdleState.ALL_IDLE)) {
                    log.info("#NettyServerConnectManagerHandler#userEventTriggered IdleStateEvent {} ", RemotingHelper.parseChannelRemoteAddr(ctx.channel()));
                    ctx.channel().close();
                }
            }
            super.userEventTriggered(ctx, evt);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            log.warn("#NettyServerConnectManagerHandler#exceptionCaught {} cause: {}", RemotingHelper.parseChannelRemoteAddr(ctx.channel()), cause.getMessage(), cause);
            super.exceptionCaught(ctx, cause);
        }
    }
}
