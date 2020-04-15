package com.spider.vo;

import lombok.Data;

/**
 * @Description 方法返回bean
 * @Author: fk
 * @Date: 2020/3/2 18:38
 */
@Data
public class ResultMap {

    private boolean isSuccess = true;// 是否成功
    private String message;// 消息

}
