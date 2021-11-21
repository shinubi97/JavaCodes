package com.shinubi.week03.homework02;

import io.netty.handler.codec.http.HttpHeaders;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

/**
 * @author shinubi
 * Date    2021/11/21
 * @version 1.0.0
 * @Description
 */
public class HttpClientUtils {

    private static CloseableHttpClient HTTP_CLIENT = HttpClients.createDefault();

    private static final String CHARSET = "utf-8";

    private static final String CONTENT_TYPE = "application/json;charset=UTF-8";


    //GET
    public static String getAsString(String url, HttpHeaders headers) {
        HttpGet httpGet = new HttpGet(url);

        //添加请求头
        if (headers != null && !headers.isEmpty()) {
            Iterator<Map.Entry<String, String>> iterator = headers.entries().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> value = iterator.next();
                httpGet.addHeader(value.getKey(),value.getValue());
            }
        }
        CloseableHttpResponse response = null;

        try {
            response = HTTP_CLIENT.execute(httpGet);
//            System.out.println(response.getStatusLine());
            HttpEntity entity = response.getEntity();
            return EntityUtils.toString(entity, CHARSET);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (Objects.nonNull(response)) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    //POST
    public static String postAsJson(String url, String json, Map<String, String> headers) {

        HttpPost httpPost = new HttpPost(url);
        //设置自定义请求头
        if (!headers.isEmpty()) {
            for (String headName : headers.keySet()) {
                httpPost.setHeader(headName,headers.get(headName));
            }
        }
        httpPost.setHeader(HTTP.CONTENT_TYPE,CONTENT_TYPE);

        CloseableHttpResponse response = null;
        try {
            //设置请求体
            StringEntity entity = new StringEntity(json);
            httpPost.setEntity(entity);

            //发送请求
            response = HTTP_CLIENT.execute(httpPost);

            //解析请求
            System.out.println(response.getStatusLine());
            HttpEntity resEntity = response.getEntity();
            return EntityUtils.toString(resEntity, CHARSET);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (Objects.nonNull(response)) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static void main(String[] args) {
        String url = "http://localhost:8808";
        String response = HttpClientUtils.getAsString(url, null);
        System.out.println(response);
    }
}
