package com.spider.web.entity;

import lombok.Data;

import java.util.Date;

/**
 * @Description 开庭公告
 * @Author: fk
 * @Date: 2020/2/29 12:07
 */
@Data
public class Ktgg {

    private String id;// id
    private String fyId;// 法院id
    private String ah;// 案号
    private String ahdm;// 案号代码
    private String ay;// 案由
    private String cbbm;// 承办部门
    private String cbr;// 承办人
    private String yg;// 原告
    private String bg;// 被告
    private String dsr;// 第三人
    private String fymc;// 法院名称
    private String ktft;// 开庭法庭
    private String ktrq;// 开庭日期
    private Date kssj;// 开庭开始时间
    private Date jssj;// 开庭结束时间
    private Date createTime;// 创建时间

}
