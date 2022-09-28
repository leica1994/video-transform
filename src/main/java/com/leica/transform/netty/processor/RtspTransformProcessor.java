package com.leica.transform.netty.processor;

import com.leica.transform.config.TransformConfig;
import com.leica.transform.context.HttpRequestWrapper;
import lombok.extern.slf4j.Slf4j;

/**
 * rtsp流转换处理器
 *
 * @author <a href="mailto:705463446@qq.com">ikun</a>
 * @date 2022/9/28 14:07
 */
@Slf4j
public class RtspTransformProcessor implements TransformProcessor {

    @Override
    public void transform(HttpRequestWrapper requestWrapper, TransformConfig config) throws Exception {
        MediaTransferFlvByJavacv flv = new MediaTransferFlvByJavacv(requestWrapper);
        new Thread(flv).start();
        flv.addClient(requestWrapper.getCtx());
    }

}
