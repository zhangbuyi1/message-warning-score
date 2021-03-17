package com.emdata.messagewarningscore.data.http.rule;/**
 * Created by zhangshaohu on 2021/1/19.
 */


import com.emdata.messagewarningscore.data.http.config.source.Server;

/**
 * @author: zhangshaohu
 * @date: 2021/1/19
 * @description:
 */
public interface IRule {
    /**
     * 负载均衡得到可用服务
     *
     * @param key
     * @return
     */
    Server choose(String key);

    /**
     * 直接返回可用服务 并且刷新可用服务 与不可用服务
     *
     * @return
     */
    Server chooseUsable(String key);

}