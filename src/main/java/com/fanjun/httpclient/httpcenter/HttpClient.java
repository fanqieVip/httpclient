package com.fanjun.httpclient.httpcenter;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("unchecked")
public class HttpClient {
    private String charset = "utf-8";
    private int connectTimeout = 20 * 1000;
    private String proxyHost = null;
    private Integer proxyPort = null;
    private Gson gson = new Gson();
    //未知异常
    private final int UnknowException = -99999;
    //中断异常
    private final int InteraptException = -99998;

    /**
     * Do GET request
     *
     * @param request            请求参数
     * @param connectionListener 重连监听器
     * @param <T>                必须是Response的基类
     */
    protected <T extends Response> void doGet(Request request, ConnectionListener connectionListener) {
        T response = null;
        Exception exception = null;
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader reader = null;
        HttpURLConnection httpURLConnection = null;
        try {
            response = (T) request.getResponseCls().newInstance();
            String url = sortParams(request.getParams());
            url = url.equals("") ? request.getUrl() : request.getUrl() + "?" + url;
            StringBuffer resultBuffer = new StringBuffer();
            URL localURL = new URL(url);
            URLConnection connection = this.openConnection(localURL);
            connection.setReadTimeout(connectTimeout);
            httpURLConnection = (HttpURLConnection) connection;
            httpURLConnection.setRequestProperty("Accept-Charset", charset);
            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            httpURLConnection.setRequestMethod("GET");
            addHeader(httpURLConnection, request.getHeader());

            String tempLine = null;
            response.setCode(httpURLConnection.getResponseCode());
            response.setHeaders(httpURLConnection.getHeaderFields());

            if (response.getCode() < 300) {
                inputStream = httpURLConnection.getInputStream();
                inputStreamReader = new InputStreamReader(inputStream);
                reader = new BufferedReader(inputStreamReader);

                while ((tempLine = reader.readLine()) != null && !request.isStop()) {
                    resultBuffer.append(tempLine);
                }
            }
            if (reader != null) {
                reader.close();
            }

            if (inputStreamReader != null) {
                inputStreamReader.close();
            }

            if (inputStream != null) {
                inputStream.close();
            }
            response.setResult(resultBuffer.toString());
        } catch (Exception e) {
            exception = e;
            response.setResult("");
            response.setHeaders(new HashMap<String, List<String>>());
            response.setCode(UnknowException);
            response.setErrorMsg(e.getMessage());
        }
        try {
            if (reader != null) {
                reader.close();
            }
            if (inputStreamReader != null) {
                inputStreamReader.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (Exception e) {
        }
        if (httpURLConnection != null) {
            httpURLConnection.disconnect();
        }

        connectionListener.finish(exception, request, response);
    }

    /**
     * Do POST request
     *
     * @param request            请求参数
     * @param connectionListener 重连监听器
     * @param <T>必须是Response的基类
     */
    protected <T extends Response> void doPost(Request request, ConnectionListener connectionListener) {
        T response = null;
        Exception exception = null;
        HttpURLConnection httpURLConnection = null;
        OutputStream outputStream = null;
        OutputStreamWriter outputStreamWriter = null;
        StringBuffer resultBuffer = new StringBuffer();
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader reader = null;
        try {
            response = (T) request.getResponseCls().newInstance();
            String tempLine = null;
            URL localURL = new URL(request.getUrl());
            URLConnection connection = this.openConnection(localURL);
            connection.setReadTimeout(connectTimeout);
            httpURLConnection = (HttpURLConnection) connection;
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            httpURLConnection.setRequestProperty("Accept-Charset", charset);
            addHeader(httpURLConnection, request.getHeader());
            if (request.getPostType() == Request.JSON) {
                httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                httpURLConnection.setRequestProperty("accept", "application/json");
                outputStream = httpURLConnection.getOutputStream();
                outputStream.write(gson.toJson(request.getParams()).getBytes());
                outputStream.flush();
            } else {
                String params = sortParams(request.getParams());
                httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                httpURLConnection.setRequestProperty("Content-Length", String.valueOf(params.length()));
                outputStreamWriter = new OutputStreamWriter(outputStream);
                outputStreamWriter.write(params);
                outputStreamWriter.flush();
            }
            response.setCode(httpURLConnection.getResponseCode());
            response.setHeaders(httpURLConnection.getHeaderFields());
            if (response.getCode() < 300) {
                //接收响应流
                inputStream = httpURLConnection.getInputStream();
                inputStreamReader = new InputStreamReader(inputStream);
                reader = new BufferedReader(inputStreamReader);

                while ((tempLine = reader.readLine()) != null && !request.isStop()) {
                    resultBuffer.append(tempLine);
                }
            }
            response.setResult(resultBuffer.toString());
        } catch (Exception e) {
            exception = e;
            response.setResult("");
            response.setHeaders(new HashMap<String, List<String>>());
            response.setCode(UnknowException);
            response.setErrorMsg(e.getMessage());
        }
        try {
            if (outputStreamWriter != null) {
                outputStreamWriter.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
            if (reader != null) {
                reader.close();
            }
            if (inputStreamReader != null) {
                inputStreamReader.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (Exception e) {
        }
        if (httpURLConnection != null) {
            httpURLConnection.disconnect();
        }

        connectionListener.finish(exception, request, response);
    }

    /**
     * 下载文件
     *
     * @param request            请求参数
     * @param connectionListener 重连监听器
     * @param <T>必须是Response的基类
     */
    protected <T extends Response> void doDownload(final Request request, ConnectionListener connectionListener) {
        T response = null;
        Exception exception = null;
        HttpURLConnection con = null;
        InputStream in = null;
        RandomAccessFile out = null;
        File dic = new File(request.downloadPath());
        if (!dic.exists()) {
            dic.mkdirs();
        }
        try {
            response = (T) request.getResponseCls().newInstance();
            final File file = new File(request.downloadPath(), MD5Util.getMD5(request.getUrl()) + request.getUrl().substring(request.getUrl().lastIndexOf(".")));
            if (!file.exists()) {
                file.createNewFile();
            }
            long size = file.length();
            String url1 = sortParams(request.getParams());
            url1 = url1.equals("") ? getHost(request.getUrl()) : getHost(request.getUrl()) + "?" + url1;
            URL url = new URL(url1);
            con = (HttpURLConnection) this.openConnection(url);
            con.setReadTimeout(connectTimeout);
            con.setRequestMethod("GET");
            con.setRequestProperty("Accept-Charset", charset);
            con.setRequestProperty("Accept-Encoding", "identity");
            con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
            addHeader(con, request.getHeader());
            //设置下载区间
            con.setRequestProperty("range", "bytes=" + size + "-");

            response.setCode(con.getResponseCode());
            response.setHeaders(con.getHeaderFields());
            if (response.getCode() == 206 || response.getCode() == 200) {
                long contentLength = con.getContentLength();
                if ( response.getCode() == 200 && size == contentLength && size != 0){
                    //表示文件已经下载完了
                    connectionListener.download(request, size, size, file);
                }else{
                    final long serverSize = contentLength + size;
                    in = con.getInputStream();
                    //必须要使用
                    out = new RandomAccessFile(file, "rw");
                    out.seek(size);
                    byte[] b = new byte[1024];
                    int len = -1;
                    while ((len = in.read(b)) != -1) {
                        if ( !request.isStop()){
                            out.write(b, 0, len);
                            size += len;
                            connectionListener.download(request, size, serverSize, file);
                            if (size >= serverSize) {
                                response.setCode(200);
                                break;
                            }
                        }else{
                            response.setCode(InteraptException);
                            response.setErrorMsg("user has interapted download...");
                            break;
                        }
                    }
                }
            } else if (response.getCode() == 416) {
                response.setCode(200);
                connectionListener.download(request, size, size, file);
            }
        } catch (Exception e) {
            exception = e;
            response.setResult("");
            response.setHeaders(new HashMap<String, List<String>>());
            response.setCode(UnknowException);
            response.setErrorMsg(e.getMessage());
        }
        try {
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
        } catch (Exception e) {
        }
        if (con != null) {
            con.disconnect();
        }
        connectionListener.finish(exception, request, response);
    }

    /**
     * 上传文件
     *
     * @param request            请求参数
     * @param connectionListener 重连监听器
     * @param <T>必须是Response的基类
     */
    protected <T extends Response> void doUpload(final Request request, ConnectionListener connectionListener) {
        DataOutputStream dos = null;
        InputStream is = null;
        String PREFIX = "--";
        String LINE_END = "\r\n";
        final File file = request.getFile();
        HttpURLConnection conn = null;
        String BOUNDARY = UUID.randomUUID().toString(); //边界标识 随机生成 String PREFIX = "--" , LINE_END = "\r\n";
        String CONTENT_TYPE = "multipart/form-data"; //内容类型  
        T response = null;
        Exception exception = null;
        try {
            response = (T) request.getResponseCls().newInstance();
            URL url = new URL(request.getUrl());
            conn = (HttpURLConnection) this.openConnection(url);
            conn.setReadTimeout(connectTimeout);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Charset", charset);
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
            addHeader(conn, request.getHeader());
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            StringBuffer sb = new StringBuffer();
            if (file != null) {
                /** * 当文件不为空，把文件包装并且上传 */
                OutputStream outputSteam = conn.getOutputStream();
                dos = new DataOutputStream(outputSteam);
                sb.append(LINE_END);
                if (request.getParams() != null) {//根据格式，开始拼接文本参数
                    for (Object keys : request.getParams().keySet()) {
                        sb.append(PREFIX).append(BOUNDARY).append(LINE_END);//分界符
                        sb.append("Content-Disposition: form-data; name=\"" + (String) keys + "\"" + LINE_END);
                        sb.append("Content-Type: text/plain; charset=" + charset + LINE_END);
                        sb.append("Content-Transfer-Encoding: 8bit" + LINE_END);
                        sb.append(LINE_END);
                        Object value = request.getParams().get(keys);
                        if (!String.class.isAssignableFrom(value.getClass())) {
                            sb.append(gson.toJson(value));
                        } else {
                            sb.append(value);
                        }
                        sb.append(LINE_END);//换行！
                    }
                }
                sb.append(PREFIX);//开始拼接文件参数  
                sb.append(BOUNDARY);
                sb.append(LINE_END);
                /**
                 * 这里重点注意： 
                 * name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件 
                 * filename是文件的名字，包含后缀名的 比如:abc.png 
                 */
                sb.append("Content-Disposition: form-data; name=\"img\"; filename=\"" + file.getName() + "\"" + LINE_END);
                sb.append("Content-Type: application/octet-stream; charset=" + charset + LINE_END);
                sb.append(LINE_END);
                //写入文件数据  
                dos.write(sb.toString().getBytes());
                is = new FileInputStream(file);
                byte[] bytes = new byte[1024];
                final long totalbytes = file.length();
                long curbytes = 0;
                int len = 0;
                while ((len = is.read(bytes)) != -1 && !request.isStop()) {
                    curbytes += len;
                    dos.write(bytes, 0, len);
                    connectionListener.upload(request, curbytes, totalbytes, file);
                    if (curbytes >= totalbytes) {
                        break;
                    }
                }
                //一定还有换行
                dos.write(LINE_END.getBytes());
                byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
                dos.write(end_data);

                sb.setLength(0);
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                response.setResult(sb.toString());
                response.setCode(conn.getResponseCode());
                response.setHeaders(conn.getHeaderFields());
            }
        } catch (Exception e) {
            exception = e;
            response.setResult("");
            response.setHeaders(new HashMap<String, List<String>>());
            response.setCode(UnknowException);
            response.setErrorMsg(e.getMessage());
        }
        try {
            if (dos != null) {
                dos.flush();
            }
            if (is != null) {
                is.close();
            }
        } catch (Exception e) {
        }
        if (conn != null) {
            conn.disconnect();
        }

        connectionListener.finish(exception, request, response);
    }

    private URLConnection openConnection(URL localURL) throws IOException {
        URLConnection connection;
        if (proxyHost != null && proxyPort != null) {
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
            connection = localURL.openConnection(proxy);
        } else {
            connection = localURL.openConnection();
        }
        return connection;
    }

    private String sortParams(Map<String, Object> params) {
        StringBuffer parameterBuffer = new StringBuffer();
        if (params != null) {
            Iterator<String> iterator = params.keySet().iterator();
            String key = null;
            String value = null;
            while (iterator.hasNext()) {
                key = iterator.next();
                if (params.get(key) != null) {
                    if (!String.class.isAssignableFrom(params.get(key).getClass())) {
                        value = gson.toJson(params.get(key));
                    } else {
                        value = (String) params.get(key);
                    }

                } else {
                    value = "";
                }
                parameterBuffer.append(key).append("=").append(value);
                if (iterator.hasNext()) {
                    parameterBuffer.append("&");
                }
            }
        }
        return parameterBuffer.toString();
    }

    private void addHeader(HttpURLConnection httpURLConnection, Map<String, String> params) {
        if (params != null) {
            Iterator<String> iterator = params.keySet().iterator();
            String key = null;
            while (iterator.hasNext()) {
                key = iterator.next();
                if (params.get(key) != null) {
                    httpURLConnection.setRequestProperty(key, params.get(key));
                }
            }
        }
    }

    /*
     * Getter & Setter
     */
    public Integer getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(Integer connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public Integer getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(Integer proxyPort) {
        this.proxyPort = proxyPort;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }
    /**
     * 取出域名
     *
     * @param url 地址
     * @return 域名
     */
    private String getHost(String url) {
        if (url == null || "".equals(url)) {
            return "";
        }
        try {
            String temp = "", temp1 = "";
            int encodePosition = 0;
            int encodePosition1 = url.lastIndexOf("/");
            int encodePosition2 = url.lastIndexOf("=");
            if (encodePosition1 > encodePosition2) {
                encodePosition = encodePosition1;
            } else {
                encodePosition = encodePosition2;
            }
            temp = url.substring(encodePosition + 1, url.length());
            temp1 = url.substring(0, encodePosition + 1);
            return temp1 + (hasChinese(temp) ? java.net.URLEncoder.encode(temp, "utf-8") : temp);
        } catch (Exception ex) {
            return "";
        }
    }

    private boolean hasChinese(String str) {
        try {
            return str.getBytes("GBK").length == str.length() ? false : true;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return false;
    }
}
