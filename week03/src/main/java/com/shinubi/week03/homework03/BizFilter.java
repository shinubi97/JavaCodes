package com.shinubi.week03.homework03;

import io.github.kimmking.gateway.filter.HttpRequestFilter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;

import java.util.Objects;

/**
 * @author shinubi
 * Date    2021/11/21
 * @version 1.0.0
 * @Description
 */
public class BizFilter implements HttpRequestFilter {

    @Override
    public void filter(FullHttpRequest fullRequest, ChannelHandlerContext ctx) {
        String uri = fullRequest.uri();
        if (uri.contains("test")) {
            //通行
            System.out.println("通行");
        } else {
            throw new RuntimeException("不支持的uri:" + uri);
        }
        HttpHeaders headers = fullRequest.headers();
        if (Objects.isNull(headers)) {
            headers = new DefaultHttpHeaders();
        }
        headers.add("xjava", "shinubi");
    }
}
