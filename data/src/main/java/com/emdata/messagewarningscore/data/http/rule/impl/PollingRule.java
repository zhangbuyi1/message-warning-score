package com.emdata.messagewarningscore.data.http.rule.impl;/**
 * Created by zhangshaohu on 2021/1/19.
 */

import cn.hutool.http.HttpStatus;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.emdata.messagewarningscore.data.http.config.source.Server;
import com.emdata.messagewarningscore.data.http.rule.IRule;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author: zhangshaohu
 * @date: 2021/1/19
 * @description:
 */
@Slf4j
public class PollingRule implements IRule {

    public PollingRule(Map<String, List<Server>> allServer) {
        allServer.entrySet().stream().map(s -> {
            List<Server> value = s.getValue();
            Map<Boolean, List<Server>> collect = value.stream().collect(Collectors.groupingBy(g -> {
                return isConnServerByHttp(g);
            }));
            okServer.put(s.getKey(), Optional.ofNullable(collect.get(true)).orElse(new ArrayList<>()));
            log.info("当前可用服务{}有:{}个 详细信息：{}", s.getKey(), Optional.ofNullable(collect.get(true)).orElse(new ArrayList<>()).size(), Optional.ofNullable(collect.get(true)).orElse(new ArrayList<>()));
            failServer.put(s.getKey(), Optional.ofNullable(collect.get(false)).orElse(new ArrayList<>()));
            return s;
        }).collect(Collectors.toList());

    }


    private AtomicInteger count = new AtomicInteger(0);
    /**
     * 存活的服务
     */
    private static Map<String, List<Server>> okServer = new HashMap<>();
    /**
     * 失败的服务
     */
    private static Map<String, List<Server>> failServer = new HashMap<>();
    /**
     * 失败服务每1分钟重试连接
     */
    private static Long failReConnectTime = 1000 * 60L;
    /**
     * 最后一次
     */
    private static Long lastReConnectTime = System.currentTimeMillis();

    @Override
    public Server choose(String key) {
        List<Server> servers = okServer.get(key);
        if (CollectionUtils.isEmpty(servers)) {
            return null;
        }
        for (; ; ) {
            if (count.compareAndSet(count.get() >= Integer.MAX_VALUE ? 0 : count.get(), count.get() + 1)) {
                if (System.currentTimeMillis() - lastReConnectTime > failReConnectTime) {
                    reConnect(key);
                }
                Server server = servers.get((count.get() % servers.size()));
                log.info("当前调用:{}", server);
                return server;
            }
        }
    }

    @Override
    public Server chooseUsable(String key) {
        reConnect(key);
        return choose(key);
    }

    /**
     * 判断服务是否可用
     *
     * @param server
     * @return
     */
    public static boolean isConnServerByHttp(Server server) {
        // 服务器是否开启
        boolean connFlag = false;
        URL url;
        HttpURLConnection conn = null;
        try {
            url = new URL(server.getSurvivalUrl());
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(3 * 1000);
            // 如果连接成功则设置为true
            if (conn.getResponseCode() == HttpStatus.HTTP_OK) {
                connFlag = true;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (SocketTimeoutException e) {
            return false;
        } catch (ConnectException e) {
            return false;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
        return connFlag;
    }

    public synchronized static void rmOkconnnect(String key, Server server) {
        List<Server> okServers = okServer.get(key);
        okServers.remove(server);
        List<Server> failServers = failServer.get(key);
        failServers.add(server);
    }

    /**
     * 宕机服务中 查询恢复服务
     *
     * @param key
     * @return
     */
    private void reConnect(String key) {
        List<Server> servers = failServer.get(key);
        if (CollectionUtils.isNotEmpty(servers)) {
            return;
        }
        List<Server> okServer = servers.stream().filter(s -> {
            return isConnServerByHttp(s);
        }).collect(Collectors.toList());
        failServer.remove(okServer);
        okServer.addAll(okServer);
    }
}