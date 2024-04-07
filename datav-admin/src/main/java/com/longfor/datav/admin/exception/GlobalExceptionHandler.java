package com.longfor.datav.admin.exception;
import com.longfor.datav.common.vo.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 统一异常处理
 * @author zhaoyalong
 * @date 2021-09-24 10:55
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    @ResponseBody
    public Object baseExceptionnHandler(Exception e) {
        log.error("业务校验异常:{}",e);
        BusinessException exception = (BusinessException)e;
        return Response.fail(exception.getData(),exception.getResponseEnum());
    }

    /**
     * 校验错误拦截处理
     *
     * @param exception 错误信息集合 
     * @return 错误信息
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public Object validationBodyException(MethodArgumentNotValidException exception){
        BindingResult result = exception.getBindingResult();
        String errorMsg = "参数错误";
        if (result.hasErrors()) {
            List<ObjectError> errors = result.getAllErrors();
            for (ObjectError p : errors) {
                FieldError error = (FieldError) p;
                errorMsg = error.getDefaultMessage();
                break;
            }
        }
        log.error("参数校验异常:{}",errorMsg);
        return Response.fail(errorMsg);
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Object exceptionHandler(Exception e) {
        log.error("系统异常:{}",e);
        return Response.fail("系统异常");
    }
}
