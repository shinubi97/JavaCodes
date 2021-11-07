package com.shinubi.week01;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author shinubi
 * Date    2021/11/7
 * @version 1.0.0
 * @Description xlass文件自定义类加载器
 */
public class XlassLoader extends ClassLoader {

    public static void main(String[] args) {
        final String className = "Hello";
        final String methodName = "hello";

        try {
            Object hello = new XlassLoader().findClass(className).newInstance();
            //通过反射获取到hello方法
            Method helloMethod = hello.getClass().getDeclaredMethod(methodName);
            helloMethod.setAccessible(true);
            helloMethod.invoke(hello);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }


    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        String suffix = ".xlass";
        InputStream inputStream = XlassLoader.class.getClassLoader().getResourceAsStream(name + suffix);
        byte[] data = null;
        //缓存
        byte[] buffer = new byte[1024];
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            int count = 0;
            while ((count = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, count);
            }
            data = byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (byteArrayOutputStream != null) {
                try {
                    byteArrayOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        //对读取到的数据进行解码
        decode(data);
        return defineClass(name,data,0,data.length);
    }

    /**
     * 解码
     * @param data
     */
    private void decode(byte[] data) {
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte) (255 - data[i]);
        }
    }
}
