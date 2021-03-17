package com.emdata.messagewarningscore.common.exception;


import com.emdata.messagewarningscore.common.result.ResultData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.util.List;

/**
 * @author: pupengfei
 * @date: 2018/12/26
 * @description controller层，异常统一处理
 */
@Slf4j
@ControllerAdvice
@Component
public class ControllerException {

    @Autowired
    private Environment environment;

    /**
     * 异常处理
     * @param request
     * @param ex
     * @return
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResultData<Object> handleException(HttpServletRequest request, Exception ex) throws Exception {
        log.error("发生异常", ex);
        log.error("发生异常URL"+request.getRequestURL());

        // 先校验自定义异常
        if (ex instanceof BusinessException) {
            return ResultData.error(((BusinessException) ex).getCode());
        }
        StringBuilder sb = new StringBuilder();
        // 如果是拦截参数验证失败的异常
        if (ex instanceof MethodArgumentNotValidException) {
            // 将所有的验证异常信息进行拼接后返回
            BindingResult bindingResult = ((MethodArgumentNotValidException) ex).getBindingResult();
            List<ObjectError> errors = bindingResult.getAllErrors();
            for (ObjectError error : errors) {
                sb.append("参数校验失败").append("[").append(error.getDefaultMessage()).append("]");
            }
        } else if (ex instanceof ConstraintViolationException) {
            ConstraintViolationException e = (ConstraintViolationException) ex;
            e.getConstraintViolations().forEach(constraintViolation -> {
                String s = constraintViolation.getMessage();
                sb.append("参数校验失败").append("[").append(s).append("]");
            });
        } else if (ex instanceof BindException) {
            //
            BindException bindException = (BindException) ex;
            List<ObjectError> errors = bindException.getAllErrors();
            for (ObjectError error : errors) {
                sb.append("参数校验失败").append("[").append(error.getDefaultMessage()).append("]");
            }
        } else if (ex instanceof HttpRequestMethodNotSupportedException) {
            // 请求方法不支持
            String method = request.getMethod();
            sb.append("Request method '").append(method).append("' is not supported");
        } else if (ex instanceof MissingPathVariableException) {
            // 缺少路径参数
            sb.append("缺少路径参数[").append(ex.getMessage()).append("]");
        } else if (ex instanceof MissingServletRequestParameterException) {
            // 缺少必须的请求参数
            sb.append("缺少必须的请求参数[").append(ex.getMessage()).append("]");
        }
//        else if (ex instanceof BusinessException) {
//            // 自定义异常
//            return new ResultData<>(((BusinessException) ex).getCode(), ex.getMessage());
//        }
        else  {
            // 其他异常（待处理），抛出
            log.error("系统异常", ex);
            sb.append("系统异常，请稍后重试");
        }

        String name = environment.getProperty("spring.application.name");
        if (StringUtils.hasText(name)) {
            sb.append("(").append(name).append(")");
        }
        return ResultData.error(sb.toString());
    }

}
