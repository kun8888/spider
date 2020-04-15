package com.spider.web.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.spider.vo.ResultMap;
import com.spider.web.service.SpiderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Description 爬虫首页--controller
 * @Author: fk
 * @Date: 2020/2/29 14:00
 */
@CrossOrigin(origins = "localhost",maxAge = 3600)
@Controller
@RequestMapping("/")
public class SpiderIndexController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SpiderService spiderService;

    /**
     * @Author: fk
     * @Description: 跳转爬虫首页
     * @Date: 18:36 2020/3/2
     * @Param: []
     * @return: java.lang.String
     **/
    @GetMapping("/index")
    public String toIndex() {
        System.out.println("222");
        return "index";
    }

    /**
     * @Author: fk
     * @Description: 爬取开庭公告数据
     * @Date: 16:56 2020/2/29
     * @Param: []
     * @return: java.lang.String
     **/
    @RequestMapping("/getKtggDatas")
    @ResponseBody
    public Object getKtggDatas() {
        ResultMap result = spiderService.getKtggDatas();
        return result;
    }

}
