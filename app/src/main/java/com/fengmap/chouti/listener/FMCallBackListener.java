package com.fengmap.chouti.listener;

import java.net.HttpURLConnection;

/**
 * HttpURLConnection网络请求返回监听器
 */
public interface FMCallBackListener {
    // 网络请求成功
    void onFinish(HttpURLConnection urlConnection, byte[] response);

    // 网络请求失败
    void onError(int error, byte[] bytes);
}
