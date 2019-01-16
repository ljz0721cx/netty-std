package com.janle.std.cases.http;

import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;

/**
 * Created by lijianzhen1 on 2019/1/16.
 */
public class HttpResponse {
    private HttpHeaders header;

    private FullHttpResponse response;

    private byte [] body;

    public HttpResponse(FullHttpResponse response)
    {
        this.header = response.headers();
        this.response = response;
        if (response.content() != null)
        {
            body = new byte[response.content().readableBytes()];
            response.content().getBytes(0, body);
        }
    }

//	public HttpResponse(FullHttpResponse response)
//	{
//		this.header = response.headers();
//		this.response = response;
//	}

    public HttpHeaders header()
    {
        return header;
    }

	/*public byte [] body()
	{
        //对直接内存操作不支持array方法
		return body = response.content() != null ?
				response.content().array() : null;
	}
*/
    public byte[] body() {
        return body;
    }
}
