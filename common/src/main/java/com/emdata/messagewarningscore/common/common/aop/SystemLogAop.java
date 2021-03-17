package com.emdata.messagewarningscore.common.common.aop;


import com.alibaba.fastjson.JSON;
import com.emdata.messagewarningscore.common.common.annotation.LogAnnotation;
import com.emdata.messagewarningscore.common.enums.LogTypeEnum;
import com.emdata.messagewarningscore.common.enums.ResultCodeEnum;
import com.emdata.messagewarningscore.common.exception.BusinessException;
import com.emdata.messagewarningscore.common.result.ResultData;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 〈一句话功能简述〉<br>
 * 〈系统业务记录日志切面〉
 *
 * @author pupengfei
 * @create 2020/2/24
 * @since 1.0.0
 */
@Component
@Aspect
@Slf4j
@Configuration
public class SystemLogAop {

    private static final String CHAR_SET = "utf-8";

    private static final String LOG_HEADER = "log";

    /**
     * 定义切点,控制层所有方法
     */
    @Pointcut("@annotation(com.emdata.messagewarningscore.common.common.annotation.LogAnnotation)")
    public void requestServer() {

    }

    @Around("requestServer()")
    public Object doAround(ProceedingJoinPoint point) throws Throwable {

        // 获取方法
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();

        // 获取类
        Class<?> clazz = point.getTarget().getClass();

        String methodName = method.getName();
        String clazzName = clazz.getSimpleName();

        // 看有没有日志注解
        LogAnnotation logAnnotation = method.getAnnotation(LogAnnotation.class);
        if (logAnnotation == null) {
            return point.proceed();
        }

        if (!logAnnotation.need()) {
            return point.proceed();
        }

        LogTypeEnum logType = logAnnotation.type();
        // 方法参数，需要记录的信息
        int argsIndex = logAnnotation.argsIndex();
        String[] prefixs = logAnnotation.prefix();
        String[] fields = logAnnotation.field();

        // 方法参数
        Object[] args = point.getArgs();
        if (args == null || args.length - 1 < argsIndex) {
            log.error("记录系统日志时，实际的方法参数和LogAnnotation中定义的方法参数索引不一致，类: {}, 方法: {}",
                    clazzName, methodName);
            return point.proceed();
        }

        // 日志的内容，下面进行拼接
        StringBuilder logContents = new StringBuilder();

        // 需要记录日志的参数对象
        Object arg = args[argsIndex];
        if (arg instanceof Collection) {
            Collection as = (Collection) arg;
            for (Object a : as) {
                if (logContents.length() > 0) {
                    logContents.append(";");
                }
                logContents.append(spliceLogContents(a, fields, prefixs));
            }
        } else {
            logContents.append(spliceLogContents(arg, fields, prefixs));
        }

        // 响应头中存放对象
        BaseLog baseLog = new BaseLog();
        baseLog.setLogType(logType);
        baseLog.setTime(new Date());
        baseLog.setContents(logContents.toString());
        baseLog.setSuccess(1);
        log.debug("记录日志: {}", baseLog);

        //获取请求信息
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (sra == null) {
            return point.proceed();
        }

        HttpServletResponse response = sra.getResponse();
        // 请求头中，放日志基本信息json字符串


        ResultData proceed = null;
        BusinessException ex = null;
        try {
            proceed = (ResultData) point.proceed();
            baseLog.setSuccess(ResultCodeEnum.SUCCESS.getCode().equals(proceed.getCode()) ? 1 : 0);
            baseLog.setErrorReason(proceed.getMsg());
        } catch (BusinessException e) {
            baseLog.setSuccess(0);
            baseLog.setErrorReason(e.getMessage());
            ex = e;
        }
        response.setHeader(LOG_HEADER, URLEncoder.encode(JSON.toJSONString(baseLog), CHAR_SET));

        log.debug("123:" + baseLog.toString());
        if (ex != null) {
            throw ex;
        }
        // 继续执行
//        return point.getArgs();
        return proceed;
    }

    /**
     * 从对象中，获取属性字段的值，拼接前缀。
     *
     * @param obj     对象
     * @param fields  字段名称集合
     * @param prefixs 前缀集合
     * @return 拼接内容
     * @throws NoSuchFieldException   找不字段异常
     * @throws IllegalAccessException 字段访问异常
     */
    private String spliceLogContents(Object obj, String[] fields, String[] prefixs) throws NoSuchFieldException, IllegalAccessException {
        log.info("参数对象: {}", obj);
        if (fields == null || fields.length == 0) {
            if (prefixs != null && prefixs.length > 0) {
                return prefixs[0] + ":" + obj.toString();
            }
            return obj.toString();
        }

        StringBuilder sb = new StringBuilder();

        boolean hasPre = prefixs.length > 0;
        int prefixMaxIndex = prefixs.length - 1;
        int prefixIndex = 0;

        Class<?> aClass = obj.getClass();

        // 如果该对象中找不到属性，则向上父类查找
        Map<String, Field> fieldMap = new HashMap<>();
        for (; aClass != Object.class; aClass = aClass.getSuperclass()) {
            for (Field f : aClass.getDeclaredFields()) {
                fieldMap.putIfAbsent(f.getName(), f);
            }
        }

        Field field = null;
        Object fieldValue = null;
        for (int i = 0, len = fields.length; i < len; i++) {
            field = fieldMap.get(fields[i]);
            if (field == null) {
                continue;
            }
            field.setAccessible(true);
            fieldValue = field.get(obj);

            if (sb.length() > 0) {
                sb.append(",");
            }
            if (hasPre) {
                prefixIndex = i < prefixMaxIndex ? i : prefixMaxIndex;
                sb.append(prefixs[prefixIndex]);
                if (!prefixs[prefixIndex].endsWith(":")) {
                    sb.append(":");
                }
            }
            sb.append(fieldValue == null ? "" : fieldValue);

        }
        return sb.toString();
    }
}
