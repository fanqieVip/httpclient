package com.fanjun.httpclient.httpcenter;

import com.fanjun.httpclient.interceptor.RequestInterceptor;
import com.fanjun.httpclient.interceptor.ResponseInterceptor;

import java.util.ArrayList;
import java.util.List;


public class Config {
	/**
	 * 下载路径
	 */
	private String downloadPath = "files";
	/**
	 * 线程池大小
	 */
	private int threadPool = 20;
	/**
	 * Request拦截器
	 */
	private List<RequestInterceptor> requestInterceptors;
	/**
	 * Response拦截器
	 */
	private List<ResponseInterceptor> responseInterceptors;
	/**
	 * 连接超时时间
	 */
	private int connectTimeout = 20*1000;
	/**
	 * 代理地址
	 */
	private String proxyHost;
	/**
	 * 代理端口
	 */
	private Integer proxyPort;
	/**
	 * 编码类型
	 */
	private String charset = "utf-8";
	
	private Config() {
		super();
	}

	public static Config ini() {
		return new Config();
	}
	public String getDownloadPath() {
		return downloadPath;
	}
	public Config downloadPath(String downloadPath) {
		this.downloadPath = downloadPath;
		return this;
	}
	public int getThreadPool() {
		return threadPool;
	}
	public Config threadPool(int threadPool) {
		this.threadPool = threadPool;
		return this;
	}
	public List<RequestInterceptor> getRequestInterceptors() {
		return requestInterceptors;
	}
	public List<ResponseInterceptor> getResponseInterceptors() {
		return responseInterceptors;
	}
	public int getConnectTimeout() {
		return connectTimeout;
	}
	public Config connectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
		return this;
	}
	public String getProxyHost() {
		return proxyHost;
	}
	public Config proxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
		return this;
	}
	public Integer getProxyPort() {
		return proxyPort;
	}
	public Config proxyPort(Integer proxyPort) {
		this.proxyPort = proxyPort;
		return this;
	}
	public String getCharset() {
		return charset;
	}
	public Config charset(String charset) {
		this.charset = charset;
		return this;
	}
	public Config addRequestInterceptor(RequestInterceptor requestInterceptor) {
		if(requestInterceptors == null){
			this.requestInterceptors = new ArrayList<RequestInterceptor>();
		}
		this.requestInterceptors.add(requestInterceptor);
		return this;
	}
	public Config addResponseInterceptor( ResponseInterceptor responseInterceptor) {
		if(this.responseInterceptors == null){
			this.responseInterceptors = new ArrayList<ResponseInterceptor>();
		}
		this.responseInterceptors.add(responseInterceptor);
		return this;
	}
	
	
}
