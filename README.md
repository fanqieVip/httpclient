# Httpclient for Android 安卓网络中心组件
## 支持常用的GET/POST/UPLOAD/DOWNLOAD，支持http/https,默认支持断点续传\断点下载，可自由定义Headers、Params，可自由定义FORM、JSON提交方式，支持Request及Response拦截
# 更新日志
## 1.0.2修复了中文路径无法下载的bug
### 使用方式
```Java
        //初始化网络中心
        //HttpCenter.create();//默认方式,但不推荐，这样和普通的http框架没什么区别
        HttpCenter.create(
                Config.ini()
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
  <version>1.0.2</version>
  <type>pom</type>
</dependency>
```
#### Gradle
```Xml
implementation 'com.fanjun:httpclient:1.0.2'
```
