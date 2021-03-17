package com.emdata.messagewarningscore.data.http.handler;/**
 * Created by zhangshaohu on 2021/1/19.
 */

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.emdata.messagewarningscore.data.http.config.source.Server;
import com.emdata.messagewarningscore.data.http.entity.MethodInfo;
import com.emdata.messagewarningscore.data.http.entity.ServerInfo;
import com.emdata.messagewarningscore.data.http.exceptions.RequestException;
import com.emdata.messagewarningscore.data.http.rule.impl.PollingRule;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * @author: zhangshaohu
 * @date: 2021/1/19
 * @description:
 */
@Slf4j
public class RestHandler extends RestTemplate {
    /**
     * 本想维持一个队列 后来简单处理  这个队列放在这里  一会有空使用的队列
     */
    /**
     * 雷达数据
     */
    public static DelayQueue<FaildRequest> FAILD_REQUST_QUEUE = new DelayQueue<>();

    @Data
    static class FaildRequest implements Delayed {
        /**
         * 当前对象重试次数
         */
        private int num;
        /**
         * 请求方法
         */
        private MethodInfo methodInfo;
        /**
         * 负载均衡策略
         */
        private PollingRule pollingRule;
        /**
         * 构建对象时间
         */
        private long time;

        /**
         * 延迟队列超时时间
         */
        private long timeOut = 1000 * 20;

        @Override
        public long getDelay(TimeUnit unit) {
            if (System.currentTimeMillis() - this.time > this.timeOut) {
                return -1;
            }
            return 1;
        }

        @Override
        public int compareTo(Delayed o) {
            // 根据时间取值
            return (int) (o.getDelay(TimeUnit.MILLISECONDS) - this.time);
        }

        public static FaildRequest build(MethodInfo methodInfo, PollingRule pollingRule, int num) {
            FaildRequest faildRequest = new FaildRequest();
            faildRequest.num = num;
            faildRequest.methodInfo = methodInfo;
            faildRequest.pollingRule = pollingRule;
            return faildRequest;
        }
    }

    private ServerInfo serverInfo;

    public void init(ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
    }

    public Object invoke(MethodInfo methodInfo, PollingRule pollingRule) {
        StringBuilder stringUrl = new StringBuilder("");
        // 得到基本的
        String url = extractUrl(pollingRule, methodInfo);
        stringUrl.append(url);
        extractRequstParam(methodInfo, stringUrl);
        HttpEntity<Object> httpEntity = Optional.ofNullable(new HttpEntity<>(methodInfo.getBody())).orElse((HttpEntity<Object>) HttpEntity.EMPTY);
        // 调用雷达数据服务 如果雷达服务不可用 重新定义雷达服务是否可用  如果依旧报错 重试三次则抛出异常 RequestException
        return requestRadar(url, methodInfo, httpEntity, pollingRule, new int[]{3}).getBody();
    }

    /**
     * @param url         请求url
     * @param methodInfo  方法
     * @param httpEntity  请求body
     * @param pollingRule 负载均衡策略
     * @param retryNum    重试次数
     * @return
     */
    public ResponseEntity<?> requestRadar(String url, MethodInfo methodInfo, HttpEntity<Object> httpEntity, PollingRule pollingRule, int[] retryNum) {
        try {
            return exchange(URI.create(url), methodInfo.getHttpMethod(), httpEntity, methodInfo.getReturnType());
        } catch (Exception e) {
            // 得到可用的
            String url1 = extractUrlUsable(pollingRule, methodInfo);
            // 如果没有可用服务 直接抛出异常
            if (url1 == null) {
                throw new RequestException("无可用雷达解析服务", 500);
            }
            // 如果当前对象
            if (retryNum[0] > 0) {
                retryNum[0] = retryNum[0] - 1;
                return requestRadar(url1, methodInfo, httpEntity, pollingRule, retryNum);
            } else {
                log.error("methodInfo :{},url:{}，httpEntity：{}", methodInfo, url1, httpEntity);
                e.printStackTrace();
                throw new RequestException("雷达解析服务 重试3次  无法解析", 500);
            }
        }

    }

    /**
     * 构建requestParam
     *
     * @param methodInfo
     * @param stringUrl
     */
    private void extractRequstParam(MethodInfo methodInfo, StringBuilder stringUrl) {
        if (CollectionUtils.isNotEmpty(methodInfo.getRequstParam())) {
            stringUrl.append("?");
            Map<String, Object> urlParam = methodInfo.getUrlParam();
            for (Map.Entry<String, Object> stringObjectEntry : urlParam.entrySet()) {
                stringUrl.append(stringObjectEntry.getKey());
                stringUrl.append("=");
                stringUrl.append(stringObjectEntry.getValue());
            }
        }
    }

    /**
     * 返回可用的url
     *
     * @param pollingRule
     * @param methodInfo
     * @return
     */
    public String extractUrlUsable(PollingRule pollingRule, MethodInfo methodInfo) {
        pollingRule.chooseUsable(serverInfo.getUrl());
        return extractUrl(pollingRule, methodInfo);
    }

    /**
     * 构建PathUrl
     *
     * @param methodInfo
     * @return
     */
    private String extractUrl(PollingRule pollingRule, MethodInfo methodInfo) {
        Server choose = pollingRule.choose(serverInfo.getUrl());
        String url = serverInfo.getUrl() + methodInfo.getUrl();
        if (choose != null) {
            url = choose.getUrl() + methodInfo.getUrl();
        }
        if (CollectionUtils.isNotEmpty(methodInfo.getUrlParam())) {
            Map<String, Object> urlParam = methodInfo.getUrlParam();
            for (Map.Entry<String, Object> stringObjectEntry : urlParam.entrySet()) {
                url.replace("{" + stringObjectEntry.getKey() + "}", stringObjectEntry.getValue().toString());
            }
        }
        return url;
    }
}