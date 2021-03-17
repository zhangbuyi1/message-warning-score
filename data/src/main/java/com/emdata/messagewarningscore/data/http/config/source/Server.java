package com.emdata.messagewarningscore.data.http.config.source;/**
 * Created by zhangshaohu on 2021/1/19.
 */

/**
 * @author: zhangshaohu
 * @date: 2021/1/19
 * @description:
 */
public class Server {
    /**
     * 主机
     */
    private String host;
    /**
     * 端口
     */
    private Integer port;
    /**
     * 存活url 判断是否存活的接口url
     */
    private String survival;

    public Server() {

    }

    public Server(String host, Integer port) {
        this.host = host;
        this.port = port;
    }

    public String getSurvivalUrl() {
        return getUrl() + (survival.startsWith("/") ? survival : "/" + survival);
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public void setSurvival(String survival) {
        this.survival = survival;
    }

    public String getUrl() {
        return "http://" + host + ":" + port;
    }

    public String getHost() {
        return host;
    }

    public Integer getPort() {
        return port;
    }

    public String getSurvival() {
        return survival;
    }

    @Override
    public String toString() {
        return "Server{" +
                "host='" + host + '\'' +
                ", port=" + port +
                '}';
    }
}