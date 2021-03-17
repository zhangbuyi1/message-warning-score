package com.emdata.messagewarningscore.data.http.porxy;/**
 * Created by zhangshaohu on 2021/1/19.
 */

import com.emdata.messagewarningscore.data.http.annotation.ApiServer;
import com.emdata.messagewarningscore.data.http.entity.MethodInfo;
import com.emdata.messagewarningscore.data.http.entity.ServerInfo;
import com.emdata.messagewarningscore.data.http.handler.RestHandler;
import com.emdata.messagewarningscore.data.http.rule.impl.PollingRule;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Optional;

/**
 * @author: zhangshaohu
 * @date: 2021/1/19
 * @description:
 */
public class RestTemplateProxy implements HttpPorxy {
    @Override
    public Object createPorxy(Class<?> type, PollingRule pollingRule) {
        ServerInfo serverInfo = extractServiceInfo(type);
        RestHandler handler = new RestHandler();
        handler.init(serverInfo);
        return Proxy.newProxyInstance(type.getClassLoader(), new Class[]{type}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                MethodInfo methodInfo = extractMethodInfo(method, args);
                return handler.invoke(methodInfo,pollingRule);
            }
        });
    }

    private MethodInfo extractMethodInfo(Method method, Object[] args) {
        MethodInfo methodInfo = new MethodInfo();
        methodInfo.setReturnType(method.getReturnType());
        Optional.ofNullable(method.getAnnotation(GetMapping.class)).ifPresent(s -> {
            String[] value = s.value();
            methodInfo.setUrl(Optional.ofNullable(value[0]).orElse(""));
            methodInfo.setHttpMethod(HttpMethod.GET);
        });
        Optional.ofNullable(method.getAnnotation(PostMapping.class)).ifPresent(s -> {
            String[] value = s.value();
            methodInfo.setUrl(Optional.ofNullable(value[0]).orElse(""));
            methodInfo.setHttpMethod(HttpMethod.POST);
        });
        Optional.ofNullable(method.getAnnotation(DeleteMapping.class)).ifPresent(s -> {
            String[] value = s.value();
            methodInfo.setUrl(Optional.ofNullable(value[0]).orElse(""));
            methodInfo.setHttpMethod(HttpMethod.DELETE);
        });
        Optional.ofNullable(method.getAnnotation(PutMapping.class)).ifPresent(s -> {
            String[] value = s.value();
            methodInfo.setUrl(Optional.ofNullable(value[0]).orElse(""));
            methodInfo.setHttpMethod(HttpMethod.PUT);
        });
        Parameter[] parameters = method.getParameters();
        // 存放PathVariable
        HashMap<String, Object> pathUrl = new HashMap<>();
        // 存放requestParam
        HashMap<String, Object> requestParam = new HashMap<>();
        for (int i = 0; i < parameters.length; i++) {
            RequestBody annotation = parameters[i].getAnnotation(RequestBody.class);
            if (annotation != null) {
                methodInfo.setBody(args[i]);
            }
            PathVariable pathVariable = parameters[i].getAnnotation(PathVariable.class);
            if (pathVariable != null) {
                String name = pathVariable.name();
                pathUrl.put(name, args[i]);
                break;
            }
            RequestParam requestParamA = parameters[i].getAnnotation(RequestParam.class);
            if (requestParamA != null) {
                String name = requestParamA.name();
                requestParam.put(name, args[i]);
            }
        }

        return methodInfo;
    }

    private ServerInfo extractServiceInfo(Class<?> type) {
        ApiServer annotation = type.getAnnotation(ApiServer.class);
        ServerInfo serverInfo = new ServerInfo();
        serverInfo.setUrl(annotation.url());
        return serverInfo;
    }
}