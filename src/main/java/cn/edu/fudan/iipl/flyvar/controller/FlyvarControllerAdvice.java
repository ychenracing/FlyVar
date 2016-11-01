/**
 * ychen. Copyright (c) 2016年10月30日.
 */
package cn.edu.fudan.iipl.flyvar.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.google.common.collect.Maps;

import cn.edu.fudan.iipl.flyvar.AbstractController;
import cn.edu.fudan.iipl.flyvar.model.constants.ErrorEnum;

/**
 * Controller异常处理
 * 
 * @author racing
 * @version $Id: ControllerAdvice.java, v 0.1 2016年10月30日 下午3:02:30 racing Exp $
 */
@Controller
@ControllerAdvice
public class FlyvarControllerAdvice extends AbstractController {

    private static final Logger logger = LoggerFactory.getLogger(FlyvarControllerAdvice.class);

    @ExceptionHandler(value = { IllegalArgumentException.class })
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public Map<String, Object> processIllegalArgument(IllegalArgumentException error,
                                                      HttpServletRequest request,
                                                      HttpServletResponse response) {
        logger.warn(getClientIP(request) + " " + error.getMessage());
        Map<String, Object> map = Maps.newHashMap();
        map.put("code", ErrorEnum.INVALID_ACCESS.getCode());
        map.put("message", ErrorEnum.INVALID_ACCESS.getMsg());
        return map;
    }

    @ExceptionHandler(value = { HttpRequestMethodNotSupportedException.class })
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public Map<String, Object> processMethodNotSupported(HttpRequestMethodNotSupportedException error,
                                                         HttpServletRequest request,
                                                         HttpServletResponse response) {
        logger.info(getClientIP(request) + " " + error.getMessage());
        Map<String, Object> map = Maps.newHashMap();
        map.put("code", ErrorEnum.NOT_SUPPORTED.getCode());
        map.put("message", ErrorEnum.NOT_SUPPORTED.getMsg());
        return map;
    }

    @ExceptionHandler(value = { Exception.class })
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public Map<String, Object> processException(Exception error, HttpServletRequest request,
                                                HttpServletResponse response) {
        logger.error(ErrorEnum.SYSTEM_ERROR.getMsg(), error);
        Map<String, Object> map = Maps.newHashMap();
        map.put("code", ErrorEnum.SYSTEM_ERROR.getCode());
        map.put("message", ErrorEnum.SYSTEM_ERROR.getMsg());
        return map;
    }
}
