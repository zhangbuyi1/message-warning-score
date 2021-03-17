package com.emdata.messagewarningscore.common.common.utils;/**
 * Created by zhangshaohu on 2020/8/13.
 */

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author: zhangshaohu
 * @date: 2020/8/13
 * @description:
 */
@Slf4j
public class FtpUtils {
    public static List<String> readMessage(InputStream is) throws IOException {
        List<String> messageList = new ArrayList<>();

        StringBuilder baowen = null;
        boolean start = false;

        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line = null;
        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line.length() == 0) {
                continue;
            }

            if (line.startsWith("METAR") || line.startsWith("SPECI")) {
                baowen = new StringBuilder();
                start = true;
                baowen.append(line);
            } else if (start) {
                baowen.append(" ");
                baowen.append(line);
            }

            if (start && line.endsWith("=")) {
                messageList.add(baowen.toString());
                start = false;
            }
        }

        return messageList;
    }


    /**
     * @param url     文件url
     * @param isBlock 是否一直阻塞监控
     * @return
     */
    public static boolean isFtpFileOk(String url, boolean isBlock) {
        for (int i = 0; i < 30 || isBlock; i++) {
            int contentLength = headLength(url);
            try {
                TimeUnit.MILLISECONDS.sleep(500);
                int nextContentLength = headLength(url);
                if (contentLength == nextContentLength) {
                    return true;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();

            }
        }
        return false;
    }

    public static int headLength(String url) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders startHttpHeaders = restTemplate.headForHeaders(url);
        return Integer.parseInt(startHttpHeaders.get("Content-Length").get(0));
    }

    public static void main(String[] args) throws IOException {

        boolean ftpFileOk = isFtpFileOk("http://192.168.90.10:8510/lighting/LPD_20200813095401.txt", false);

    }

    /**
     * 将httpInputStream转换为String
     *
     * @param in
     * @return
     */
    public static String readInputSeream(InputStream in) {
        // httpIputStream不能通过in.available() 获得全部字节 然后用read去读取完 网络传输的坑
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            while ((len = in.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            bos.close();
            byte[] bytes = bos.toByteArray();
            // 这样可以设置编码  因为没有乱码所有我就没有设置  如果以后出现乱码 可以在当前方法多加一个参数为 CharsetUtil.UTF_8  String charset
            String source = new String(bytes);
            return source;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 通关url获得inputStream
     *
     * @param url
     * @return
     */
    public static InputStream getInputStream(String url) {
        try {
            URL thisUrl = new URL(url);
            try {
                InputStream inputStream = thisUrl.openStream();
                return inputStream;
            } catch (IOException e) {
                e.printStackTrace();
                log.error("获得当前url流出现异常:{}", url);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            log.error("创建Url异常：{}", url);
        }
        return null;
    }
}