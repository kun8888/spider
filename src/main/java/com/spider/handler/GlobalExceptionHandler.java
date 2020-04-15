package com.spider.handler;

import com.spider.web.entity.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;

/**
 * @Description 全局异常处理
 * @Author: fk
 * @Date: 2020/2/29 14:03
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler(value = Exception.class)
    public ModelAndView exceptionHandler(Exception e) {
        logger.error(e.getMessage(), e);
        Log log = new Log();
        log.setMessage(e.getMessage());
        log.setCreateTime(new Date());

        ModelAndView mv = new ModelAndView("error");
        return mv;
    }

}
