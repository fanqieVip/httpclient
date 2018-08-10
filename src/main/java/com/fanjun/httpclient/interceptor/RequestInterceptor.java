package com.fanjun.httpclient.interceptor;


import com.fanjun.httpclient.httpcenter.Request;

/**
 * 请求拦截器
 * 建议根据请求类型做相应的拦截处理
 * @author Administrator
 *
 */
public interface RequestInterceptor {
	/**
	 * 参数加工
	 * @param request 请求参数
	 * @return 返回重新解析后的请求参数
	 */
	Request paramsMachining(Request request);
}
