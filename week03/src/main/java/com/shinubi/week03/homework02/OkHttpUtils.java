package com.shinubi.week03.homework02;

import okhttp3.*;
import okhttp3.Request.Builder;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

/**
 * @author shinubi
 * Date    2021/11/21
 * @version 1.0.0
 * @Description
 */
public class OkHttpUtils {

    private static final OkHttpClient HTTP_CLIENT = new OkHttpClient.Builder().build();

    private static final String JSON_TYPE = "application/json;charset=UTF-8";
    //GET
    public static String getAsString(String url, Map<String, String> headers) {

        Builder builder = new Request.Builder();
        //设置请求头
        if (Objects.nonNull(headers) && !headers.isEmpty()) {
            for (String name : headers.keySet()) {
                builder.addHeader(name, headers.get(name));
            }
        }

        Request request = builder.url(url).build();
        Response response = null;
        try {
            response = HTTP_CLIENT.newCall(request).execute();
            if (Objects.nonNull(response.body())) {
                return response.body().toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            response.code();
        }
        return null;
    }

    //POST
    public static String postAsJson(String url, String json, Map<String, String> headers) {
        Builder builder = new Builder();

        //添加请求头
        builder.addHeader("Content-Type", JSON_TYPE);
        if (!headers.isEmpty()) {
            for (String name : headers.keySet()) {
                builder.addHeader(name, headers.get(name));
            }
        }

        Response response = null;
        try {
            RequestBody requestBody = RequestBody.create(json.getBytes());
            Request request = builder.url(url).post(requestBody).build();

            response = HTTP_CLIENT.newCall(request).execute();
            if (Objects.nonNull(response.body())) {
                return response.body().toString();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != response) {
                response.close();
            }
        }
        return null;
    }

    public static void main(String[] args) {
        String url = "http://localhost:8801";
        String response = OkHttpUtils.getAsString(url, null);
        System.out.println(response);
    }
}
