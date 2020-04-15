package com.spider.web.entity;

import lombok.Data;

import java.util.Date;

/**
 * @Description 日志
 * @Author: fk
 * @Date: 2020/2/29 14:07
 */
@Data
public class Log {

    private String id;// id
    private String message;// 日志消息
    private Date createTime;// 创建时间

}
