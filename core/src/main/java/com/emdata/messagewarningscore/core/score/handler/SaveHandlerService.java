package com.emdata.messagewarningscore.core.score.handler;/**
 * Created by zhangshaohu on 2021/1/12.
 */

/**
 * @author: zhangshaohu
 * @date: 2021/1/12
 * @description:
 */
public interface SaveHandlerService {
    <T> int  insart(Class<T> clazz, T t);
}