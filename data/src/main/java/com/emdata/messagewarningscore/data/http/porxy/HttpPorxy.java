package com.emdata.messagewarningscore.data.http.porxy;/**
 * Created by zhangshaohu on 2021/1/19.
 */

import com.emdata.messagewarningscore.data.http.rule.impl.PollingRule;

/**
 * @author: zhangshaohu
 * @date: 2021/1/19
 * @description:
 */
public interface HttpPorxy {
    Object createPorxy(Class<?> objectType, PollingRule pollingRule);
}