package com.fengmap.chouti.util;

import android.util.Log;

import com.fengmap.chouti.listener.FMCallBackListener;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;

/**
 * HttpURLConnection 网络请求工具类
 * <p>
 * 数据的请求都是基于HttpURLConnection的 请求成功与失败的回调都是在主线程
 * 可以直接更新UI
 */
public final class FMHttpUtils {

    private static FMHttpUtils FMHttpUtils;
    private static ExecutorService threadPool;

    public static FMHttpUtils getFMHttpUtils() {
        if (FMHttpUtils == null) {
            synchronized (FMHttpUtils.class) {
                FMHttpUtils = new FMHttpUtils();
                threadPool = Executors.newFixedThreadPool(6);
                return FMHttpUtils;
            }
        }
        return FMHttpUtils;
    }

    /**
     * GET方法 返回数据会解析成byte[]数组
     *
     * @param urlString 请求的url
     * @param listener  回调监听
     */
    public void doGet(final String urlString,
                      final FMCallBackListener listener) {
        // 因为网络请求是耗时操作，所以需要另外开启一个线程来执行该任务。
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                URL url = null;
                HttpURLConnection httpURLConnection = null;
                try {
                    // 根据URL地址创建URL对象
                    url = new URL(urlString);
                    if (url.getProtocol().toLowerCase().equals("https")) {
                        httpURLConnection= (HttpsURLConnection) url.openConnection();
                    } else {
                        httpURLConnection = (HttpURLConnection) url.openConnection();
                    }
                    httpURLConnection.setRequestProperty("connection", "Keep-Alive");
                    // 获取HttpURLConnection对象
//                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    // 设置请求方式，默认为GET
                    httpURLConnection.setRequestMethod("GET");
                    // 设置连接超时
                    httpURLConnection.setConnectTimeout(5000);
                    // 设置读取超时
                    httpURLConnection.setReadTimeout(5000);
                    // 响应码为200表示成功，否则失败。
                    if (httpURLConnection.getResponseCode() == 201 || httpURLConnection.getResponseCode() == 200) {
                        // 获取网络的输入流
                        InputStream is = httpURLConnection.getInputStream();
                        // 读取输入流中的数据
                        BufferedInputStream bis = new BufferedInputStream(is);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        byte[] bytes = new byte[1024];
                        int len = -1;
                        while ((len = bis.read(bytes)) != -1) {
                            baos.write(bytes, 0, len);
                        }
                        bis.close();
                        is.close();
                        // 响应的数据
                        listener.onFinish(httpURLConnection, baos.toByteArray());
                    } else {
                        // 获取网络的输入流
                        InputStream is = httpURLConnection.getErrorStream();
                        // 读取输入流中的数据
                        BufferedInputStream bis = new BufferedInputStream(is);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        byte[] bytes = new byte[1024];
                        int len = -1;
                        while ((len = bis.read(bytes)) != -1) {
                            baos.write(bytes, 0, len);
                        }
                        bis.close();
                        is.close();
                        // 响应的数据
                        listener.onError(httpURLConnection.getResponseCode(), baos.toByteArray());
                    }
                } catch (Exception e) {
                    Log.e("网络连接异常",e.toString());
                    listener.onError(404,null);
                } finally {
                    if (httpURLConnection != null) {
                        // 释放资源
                        httpURLConnection.disconnect();
                    }
                }
            }
        });
    }


    /**
     * POST方法 返回数据会解析成Byte[]数组
     *
     * @param urlString 请求的路径
     * @param params    参数列表
     * @param listener  回调监听
     */
    public void doPost(final String urlString,
                       final Map<String, Object> params,
                       final FMCallBackListener listener) {
        // 组织请求参数
        final JSONObject jsonObject = new JSONObject(params);
        final String string = jsonObject.toString();
        // 因为网络请求是耗时操作，所以需要另外开启一个线程来执行该任务。
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                URL url;
                HttpURLConnection httpURLConnection = null;
                try {
                    url = new URL(urlString);
                    if (url.getProtocol().toLowerCase().equals("https")) {
                        httpURLConnection = (HttpsURLConnection) url.openConnection();
                    } else {
                        httpURLConnection = (HttpURLConnection) url.openConnection();
                    }
                    httpURLConnection.setRequestProperty("connection", "Keep-Alive");
                    // this one must add ,else getContentLength is -1
//                    httpURLConnection.setRequestProperty("Accept-Encoding", "identity");

                    // 设置文件长度
                    httpURLConnection.setRequestProperty("Content-Length", String.valueOf(string.getBytes().length));
                    httpURLConnection.setRequestProperty("Content-Type", "application/json");

                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setConnectTimeout(5000);
                    httpURLConnection.setReadTimeout(5000);
                    // 设置运行输入
                    httpURLConnection.setDoInput(true);
                    // 设置运行输出
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.connect();

                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    // 发送请求参数
                    outputStream.write(string.getBytes());
                    // flush输出流的缓冲
                    outputStream.flush();
                    outputStream.close();
                    if (httpURLConnection.getResponseCode() == 200 || httpURLConnection.getResponseCode() == 201) {
                        // 获取网络的输入流
                        InputStream is = httpURLConnection.getInputStream();
                        // 读取输入流中的数据
                        BufferedInputStream bis = new BufferedInputStream(is);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        byte[] bytes = new byte[1024];
                        int len = 0;
                        long sum = 0;
                        while ((len = bis.read(bytes)) != -1) {
                            sum = sum +len;
                            baos.write(bytes, 0, len);
                        }
                        // 响应的数据

                        listener.onFinish(httpURLConnection,baos.toByteArray());

                        bis.close();
                        is.close();
                    } else {
                        // 获取网络的输入流
                        InputStream is = httpURLConnection.getErrorStream();
                        // 读取输入流中的数据
                        BufferedInputStream bis = new BufferedInputStream(is);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        byte[] bytes = new byte[1024];
                        int len = -1;
                        while ((len = bis.read(bytes)) != -1) {
                            baos.write(bytes, 0, len);
                        }
                        bis.close();
                        is.close();
                        // 响应的数据
                        listener.onError(httpURLConnection.getResponseCode(), baos.toByteArray());
                    }
                } catch (Exception e) {
                    Log.e("网络连接异常",e.toString());
                    listener.onError(404,null);
                } finally {
                    if (httpURLConnection != null) {
                        // 最后记得关闭连接
                        httpURLConnection.disconnect();
                    }
                }
            }
        });
    }

    public void release() {
        threadPool.shutdownNow();
        threadPool = null;
        FMHttpUtils = null;
    }
}