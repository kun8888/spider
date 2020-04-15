package com.spider.job;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.spider.constant.KtggConstant;
import com.spider.mapper.FyMapper;
import com.spider.mapper.KtggMapper;
import com.spider.mapper.LogMapper;
import com.spider.util.spider.KtggSpider;
import com.spider.util.ExcelUtil;
import com.spider.vo.ExcelSheetVo;
import com.spider.vo.ExcelVo;
import com.spider.vo.KtggVo;
import com.spider.vo.ResultMap;
import com.spider.web.entity.Fy;
import com.spider.web.entity.Ktgg;
import com.spider.web.service.SpiderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @Description 定时任务类
 * @Author: fk
 * @Date: 2020/3/1 11:17
 */
@Configuration
@EnableScheduling
public class SpiderJob {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private LogMapper logMapper;

    @Autowired
    private SpiderService spiderService;

    /**
     * @Author: fk
     * @Description: 爬取开庭公告job
     * @Date: 11:23 2020/3/1
     * @Param: []
     * @return: void
     **/
    @Scheduled(cron = "0 58 18 * * ? ")
    private void ktggSpider() {
        try {
            logger.info(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss") + "  开始爬取数据");
            ResultMap result = spiderService.getKtggDatas();
            if(result.isSuccess()) {
                logger.info(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss") + "  爬取数据结束");
            } else {
                logger.error(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss") + "  爬取数据失败！错误信息是： " + result.getMessage());
            }
        } catch (Exception e) {
            logger.error(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss") + "  爬取数据失败！", e);
        }

    }

}
