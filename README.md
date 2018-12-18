# Httpclient for Android 安卓网络中心组件
## 支持常用的GET/POST/UPLOAD/DOWNLOAD，支持http/https,默认支持断点续传\断点下载，可自由定义Headers、Params，可自由定义FORM、JSON提交方式，支持Request及Response拦截
## 支持自动重连
## 支持自动过滤重复请求
## 支持批量关闭请求或关闭单个请求
## 更新日志
### 【1.1.0稳定版】 2018-12-18 
#### 1.新增Response拦截器开关功能，可对单个Request进行设置（Request.enableResponseDispatcherParams），默认为开启
### 【1.0.19】 2018-12-17 
#### 1.新增自动过滤重复请求特性，如相同的请求已存放于等待请求队列，正在进行请求队列，重连请求队列中时，则自动过滤。有效规避暴力请求操作、有效规避多次onResume造成网络浪费的等问题，有效优化网络负载性能
### 【1.0.15】 2018-12-07 
#### 1.修复部分文件无法下载的bug
#### 2.修复部分手机重连机制失效的bug
#### 3.标记Request.ini(Class cls)方法过时，新增Request.ini(Object context, Class cls)方法，用以标记Request来源，便于批量关闭请求
#### 4.新增request.stop()方法，便于关闭单个请求
#### 5.新增HttpCenter.stopRequests(Object context)方法，调用该方法将批量关闭与该context关联的所有Request请求
#### 6.新增HttpCenter.shutDown()方法，调用该方法彻底关闭所有网络请求及停止HttpCenter，如需重启则需要重新调用HttpCenter.create()，！！！慎用
### 【1.0.8】 2018-12-06 
#### 1.Request新增是否启用自动重连特性（网络不通时，不会重连，会等到通了之后自动重连）
#### 2.Request默认关闭自动重连机制，可通过单独设置request.enableReconnection(true)启用
#### 3.Request新增是否启用Request请求拦截器特性（若不启用，则不会执行RequestInterceptor）
#### 4.Request默认启用Request请求拦截机制，可通过单独设置request.enableRequestDispatcherParams(false)关闭
### 【1.0.7】 2018-12-05 
#### 1.修复url地址无效造成无法回调的bug,默认抛出-99999的错误码及Exception信息
### 【1.0.5】 2018-11-16 
#### 1.修复Request.ini(class)中class对requestListener泛型约束无效的问题
### 【1.0.4】 2018-10-25 
#### 1.修复了中文路径无法下载的bug
### 使用方式
```Java
        //初始化网络中心
        //HttpCenter.create();//默认方式,但不推荐，这样和普通的http框架没什么区别
        HttpCenter.create(
                context, Config.ini()
                        .charset("你的编码格式")
                        .connectTimeout(20 * 1000)
                        .downloadPath("您的文件下载目录")
                        //如需要代理
                        .proxyHost("").proxyPort(0)
                        //你的线程池大小
                        .threadPool(10)
                        //在请求发出前的拦截器,一般可以在这里集中处理加密、验签，或针对不同请求方式做特殊处理；可添加多个
                        .addRequestInterceptor(new MyRequestInterceptor())
                        //在服务器返回后的拦截器,一般可以在这里集中处理解密、数据拆装，或针对不同请求方式做特殊处理；可添加多个
                        .addResponseInterceptor(new MyResponseInterceptor()));

        //POST 请求 注意：用到泛型的地方均为Response的子类，Request.ini(Response.class)中的class必须和泛型一个类，下面不再赘述
        HttpCenter.POST(Request.ini(Response.class)
                .url("您restApi地址")
                //可以任意设置请求头
                .putHeader("", "")
                //您的请求参数
                .putParams("", "")
                //默认值为JSON，可供选择的还有FORM
                .postType(Request.JSON)
                //回调
                .requestListener(new RequestListener<Response>() {
                    @Override
                    public void response(Response response) {

                    }
                }));
        //GET请求
        //HttpCenter.GET();
        //上传文件请求，默认为支持断点上传
        HttpCenter.UploadFile(Request.ini(Response.class)
                .url("您restApi地址")
                //可以任意设置请求头
                .putHeader("", "")
                //您的请求参数
                .putParams("", "")
                //默认值为JSON，可供选择的还有FORM
                .postType(Request.FORM)
                //上传的文件
                .file(new File("你的文件"))
                //回调
                .requestListener(new RequestListener<Response>() {
                    @Override
                    public void response(Response response) {

                    }
                    //可以跟踪上传进度
                    @Override
                    public void upload(long current, long size, File file) {
                        super.upload(current, size, file);
                    }
                }));
        //下载文件请求，默认为支持断点下载
        HttpCenter.DownloadFile(Request.ini(Response.class)
                .url("您restApi地址")
                //可以任意设置请求头
                .putHeader("", "")
                //您的请求参数
                .putParams("", "")
                //回调
                .requestListener(new RequestListener<Response>() {
                    @Override
                    public void response(Response response) {

                    }
                    //可跟踪下载进度
                    @Override
                    public void download(long current, long size, File file) {
                        super.download(current, size, file);
                    }
                }));
```
### 自定义Request拦截器
```Java
public class MyRequestInterceptor implements RequestInterceptor {

        @Override
        public Request paramsMachining(Request request) {
            return null;
        }
    }
```
### 自定义Response拦截器
```Java
public class MyResponseInterceptor implements ResponseInterceptor {

        @Override
        public <T extends Response> T paramsMachining(Request request, T response) {
            //response中包含了状态、头、数据包、错误消息等，你可以根据自己的需求重组，但一定要返回一个继承自Response的类
            return null;
        }
    }
```
### 依赖
#### Maven
```Xml
<dependency>
  <groupId>com.fanjun</groupId>
  <artifactId>httpclient</artifactId>
  <version>1.1.0</version>
  <type>pom</type>
</dependency>
```
#### Gradle
```Xml
implementation 'com.fanjun:httpclient:1.1.0'
```
