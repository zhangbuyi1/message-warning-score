package com.emdata.messagewarningscore.common.common.utils;

/**
 * Created by zhangshaohu on 2020/5/28.
 */
@FunctionalInterface
public interface BiFunctionP<T, U, E, R> {
    R apply(T t, U u, E e);
}
