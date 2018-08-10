package com.fanjun.httpclient.httpcenter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class HttpCenter {
	private static HttpCenter httpCenter;
	/**
	 * 参数配置
	 */
	private Config config;
	/**
	 * 请求队列
	 */
	private BlockingQueue<Request> requests;
	/**
	 * 请求执行器
	 */
	private List<RequestDispatcher> requestDispatchers;
	/**
	 * 网络请求工具
	 */
	private HttpClient httpUtils;
	
	private HttpCenter(Config config) {
		super();
		requests = new LinkedBlockingQueue<Request>();
		requestDispatchers = new ArrayList<RequestDispatcher>();
		httpUtils = new HttpClient();
		initConfig(config);
		initHttpUtilsConfig();
		initRequestDispatcher();
	}

	/**
	 * 启动Http中心
	 * @param config 配置信息
	 */
	public static void create(Config config) {
		if (httpCenter == null) {
			httpCenter = new HttpCenter(config);
		}
	}
	public static void create() {
		create(null);
	}
	/**
	 * 初始化配置信息
	 */
	private void initConfig(Config config) {
		this.config = config;
		if (this.config == null) {
			this.config = Config.ini();
		}
		
	}
	/**
	 * 初始化任务执行器
	 */
	private void initRequestDispatcher() {
		for (int i = 0; i < config.getThreadPool(); i++) {
			RequestDispatcher requestDispatcher = new RequestDispatcher(requests, httpUtils);
			requestDispatcher.setConfig(config);
			requestDispatchers.add(requestDispatcher);
			requestDispatcher.start();
		}
	}
	/**
	 * 初始化网络请求参数
	 */
	private void initHttpUtilsConfig() {
		httpUtils.setCharset(config.getCharset());
		httpUtils.setConnectTimeout(config.getConnectTimeout());
		httpUtils.setProxyHost(config.getProxyHost());
		httpUtils.setProxyPort(config.getProxyPort());
		httpUtils.setDownloadPath(config.getDownloadPath());
		httpUtils.setResponseInterceptors(config.getResponseInterceptors());
	}

	/**
	 * POST请求
	 * @param request 请求参数
	 */
	public static void POST(Request request) {
		request.requestType(Request.POST);
		request.start();
		if (httpCenter != null) {
			httpCenter.requests.offer(request);
		}
	}
	/**
	 * GET请求
	 * @param request 请求参数
	 */
	public static void GET(Request request) {
		request.requestType(Request.GET);
		request.start();
		if (httpCenter != null) {
			httpCenter.requests.offer(request);
		}
	}
	/**
	 * 上传文件
	 * @param request 请求参数
	 */
	public static void UploadFile(Request request) {
		request.requestType(Request.UPLOAD);
		request.start();
		if (httpCenter != null) {
			httpCenter.requests.offer(request);
		}
	}
	/**
	 * 下载文件
	 * @param request 请求参数
	 */
	public static void DownloadFile(Request request) {
		request.requestType(Request.DOWNLOAD);
		request.start();
		if (httpCenter != null) {
			httpCenter.requests.offer(request);
		}
	}

	/**
	 * 关闭消息中心
	 */
	public static void shutDown(){
		try {
			if (httpCenter!=null){
				httpCenter.stopService();
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	private void stopService(){
		for(RequestDispatcher requestDispatcher: requestDispatchers){
			if (requestDispatcher.isAlive()){
				requestDispatcher.interrupt();
			}
		}
		requests.clear();
		httpUtils = null;
		requestDispatchers = null;
		requests = null;
		config = null;
		httpCenter = null;
	}
}
