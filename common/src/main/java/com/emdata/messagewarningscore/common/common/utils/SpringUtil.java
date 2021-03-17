package com.emdata.messagewarningscore.common.common.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangshaohu on 2020/2/27.
 */
@Component
public class SpringUtil implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringUtil.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) throws BeansException {
        return (T) applicationContext.getBean(name);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(Class<T> clz) throws BeansException {
        return (T) applicationContext.getBean(clz);
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> getBeanList(Class<T>[] clzs) throws BeansException {
        List<T> ts = new ArrayList<>();
        for (Class<T> clz : clzs) {
            T bean = getBean(clz);
            ts.add(bean);
        }
        return ts;
    }
}
