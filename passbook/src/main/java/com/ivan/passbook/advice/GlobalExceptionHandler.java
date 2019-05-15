package com.ivan.passbook.advice;

import com.ivan.passbook.vo.ErrorInfo;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * <h1>全局异常处理</h1>
 * @Author Ivan 16:22
 * @Description TODO
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public ErrorInfo<String> errorHandler(HttpServletRequest request,Exception e){

        ErrorInfo<String> info = new ErrorInfo<>();

        info.setCode(ErrorInfo.ERROR);
        info.setMessage(e.getMessage());
        info.setData("Do Not Return Data !");
        info.setUrl(request.getRequestURL().toString());

        return info;
    }

}
