package com.fanjun.httpclient.httpcenter;

import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;

import java.io.File;

public class Request{
	public static final int POST = 0;
	public static final int GET = 1;
	public static final int UPLOAD = 2;
	public static final int DOWNLOAD = 3;
	/**
	 * POST_JSON提交方式
	 */
	public static final int JSON = 0;
	/**
	 * POST_FORM提交方式
	 */
	public static final int FORM = 1;
	/**
	 * 请求类型
	 */
	private int requestType;
	/**
	 * post提交方式
	 */
	private int postType;
	/**
	 * 回调
	 */
	private RequestListener requestListener;
	/**
	 * 请求地址
	 */
	private String url;
	/**
	 * 请求参数
	 */
	private ArrayMap<String, Object> params;
	/**
	 * 请求头
	 */
	private ArrayMap<String, String> header;
	/**
	 * 上传的文件
	 */
	private File file;
	/**
	 * 返回数据类型
	 */
	private Class<? extends Response> responseCls;
	/**
	 * 是否停止
	 */
	private boolean stop = false;

	
	private Request(Class<? extends Response> cls) {
		super();
		this.responseCls = cls;
	}
	private Request() {
		super();
	}
	public static Request ini(@NonNull Class<? extends Response> cls) {
		return new Request(cls);
	}
	public Request requestListener(RequestListener requestListener) {
		this.requestListener = requestListener;
		return this;
	}
	public Request url(@NonNull String url){
		this.url = url;
		return this;
	}
	public Request file(File file){
		this.file = file;
		return this;
	}
	public Request postType(int postType){
		this.postType = postType;
		return this;
	}
	protected Request requestType(int requestType){
		this.requestType = requestType;
		return this;
	}
	public Request putParams(String key, Object obj){
		if (this.params == null) {
			this.params = new ArrayMap<String, Object>();
		}
		this.params.put(key, obj);
		return this;
	}
	public Request putHeader(String key, String obj){
		if (this.header == null) {
			this.header = new ArrayMap<String, String>();
		}
		this.header.put(key, obj);
		return this;
	}
	
	public File getFile() {
		return file;
	}
	public int getPostType() {
		return postType;
	}
	public void setPostType(int postType) {
		this.postType = postType;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public int getRequestType() {
		return requestType;
	}
	public RequestListener getRequestListener() {
		return requestListener;
	}
	public String getUrl() {
		return url;
	}
	public ArrayMap<String, Object> getParams() {
		return params;
	}
	public ArrayMap<String, String> getHeader() {
		return header;
	}
	public Class<? extends Response> getResponseCls() {
		return responseCls;
	}
	public void stop(){
		stop = true;
	}
	protected void start() {
		stop = false;
	}
	protected boolean isStop() {
		return stop;
	}
}
