package com.janle.std.cases.http;

import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;

/**
 * 构建响应参数
 * Created by lijianzhen1 on 2019/1/16.
 */
public class HttpResponse {
    private HttpHeaders header;

    private FullHttpResponse response;

    private byte[] body;

    public HttpResponse(FullHttpResponse response) {
        this.header = response.headers();
        this.response = response;
        //调整为在构造时候就直接放入body中
        if (response.content() != null) {
            body = new byte[response.content().readableBytes()];
            response.content().getBytes(0, body);
        }
    }

    public byte[] body() {
        return body;
    }

	/*public HttpResponse(FullHttpResponse response)
    {
		this.header = response.headers();
		this.response = response;
	}

    public byte [] body()
    {
        //对直接内存操作不支持array方法
        return body = response.content() != null ?
                response.content().array() : null;
    }

    public byte [] body()
    {
        body=new byte[response.content().readableBytes()];
        response.content().getBytes(0,body);
        return body;
    }*/

    public HttpHeaders header() {
        return header;
    }


}
