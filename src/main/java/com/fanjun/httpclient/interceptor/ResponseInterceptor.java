package com.fanjun.httpclient.interceptor;


import com.fanjun.httpclient.httpcenter.Request;
import com.fanjun.httpclient.httpcenter.Response;

/**
 * 返回拦截器
 * @author Administrator
 *
 */
public interface ResponseInterceptor {
	/**
	 * 返回数据加工
	 * @param request 对应的请求参数
	 * @param response 返回数据包
	 * @param <T> 必须是Response的基类
	 * @return 处理后的数据
	 */
	<T extends Response>T paramsMachining(Request request, T response);
}
