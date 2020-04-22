package com.spider.util.spider;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.spider.constant.KtggConstant;
import com.spider.mapper.FyMapper;
import com.spider.mapper.KtggMapper;
import com.spider.util.http.HttpUtils;
import com.spider.vo.KtggVo;
import com.spider.web.entity.Fy;
import com.spider.web.entity.Ktgg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @Description 爬虫类
 * @Author: fk
 * @Date: 2020/2/29 16:34
 */
@Component
public class KtggSpider {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private FyMapper fyMapper;

    @Autowired
    private KtggMapper ktggMapper;

    /**
     * @Author: fk
     * @Description: 获取法院
     * @Date: 17:46 2020/2/29
     * @Param: []
     * @return: java.util.List<com.spider.web.entity.Fy>
     **/
    public List<Fy> getFys() {
        Map<String, String> params = new HashMap<>();
        params.put("cid", "1001");
        params.put("fydm", "320500");
        params.put("jb", "2");
        logger.info("查询法院开始！");
        JSONObject jsonObject = HttpUtils.postRequest(KtggConstant.URL_FY, params);
        if (null == jsonObject || !jsonObject.get("code").equals("0")) {
            logger.info("查询法院结束！无数据");
            return null;
        } else {
            logger.info("查询法院结束，开始解析！", jsonObject);
            List<Fy> fys = new ArrayList<>();
            JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONObject("zy").getJSONArray("jcfy");
            for (Object key : jsonArray) {
                JSONObject k = (JSONObject) key;
                Fy fy = new Fy();
                fy.setFymc(k.getString("fymc"));
                fy.setFydm(k.getString("fydm"));
                fys.add(fy);
            }
            logger.info("解析法院结束！共 " + fys.size() + " 条数据！");
            // 保存数据
            fyMapper.batchInsert(fys);
            // 批量插入后无id，重新查
            return fyMapper.findAll();
        }
    }

    /**
     * @Author: fk
     * @Description: 爬取所有法院数据，保存新增数据并返回
     * @Date: 17:53 2020/2/29
     * @Param: []
     * @return: java.lang.String
     **/
    public Object getDatas() {
        List<Ktgg> list = new ArrayList<>();// 爬取的所有数据
        List<Fy> fys = null;
        List<String> ahs = new ArrayList<>();// 存放爬取到的案号
        // 数据库中查法院，没有则先爬取法院信息
        fys = fyMapper.findAll();
        if (CollectionUtil.isEmpty(fys)) {
            fys = getFys();
        }
        Map<String, String> params = new HashMap<>();
        params.put("cid", "1001");// 固定
        params.put("fydm", "320500");// 固定
        params.put("ktrq1", "20200101");// 开庭日期
        params.put("pagerows", "5");// 每页条数
        params.put("postion", "1");// 页码

        KtggVo ktggVo = new KtggVo();// 开庭公告查询vo
        // 遍历法院
        for (Fy fy : fys) {
            List<Ktgg> fyKtgg = new ArrayList<>();// 各法院的所有数据
            params.put("ktfydm", fy.getFydm());
            logger.info("开始查询" + fy.getFymc() + "（" + fy.getFydm() + "）开庭数据");
            // 先查一遍总数，再将每页条数设置成总数，一次性查出所有
            JSONObject jsonObject = HttpUtils.postRequest(KtggConstant.URL_KTGG, params);
            if (null == jsonObject || !jsonObject.getString("code").equals("0") || jsonObject.getInteger("count") <= 0) {
                logger.info(fy.getFymc() + "（" + fy.getFydm() + "）无数据 ");
                continue;
            } else {
                logger.info(fy.getFymc() + "（" + fy.getFydm() + "）共查出： " + jsonObject.getInteger("count") + " 条数据");
                double total = jsonObject.getDouble("count");
                // 数据2000条时，一次性查会超时，因此改为一次查1000条
                int interval = (int) Math.ceil(total / (double) 500);
                for (int i = 1; i <= interval; i++) {
                    params.put("pagerows", "500");// 每页条数
                    params.put("postion", String.valueOf(i));// 页码
                    logger.info("开始查询" + fy.getFymc() + "（" + fy.getFydm() + "）第： " + i + " 页数据");
                    // 分页数据
                    JSONObject jsonObject_all = HttpUtils.postRequest(KtggConstant.URL_KTGG, params);
                    // 解析数据
                    logger.info("开始解析" + fy.getFymc() + "（" + fy.getFydm() + "）第： " + i + " 页数据");
                    if (jsonObject_all == null) {
                        logger.info(fy.getFymc() + "（" + fy.getFydm() + "）第： " + i + " 页数据获取失败");
                        continue;
                    }
                    JSONArray jsonArray = jsonObject_all.getJSONArray("data");
                    for (Object key : jsonArray) {
                        JSONObject k = (JSONObject) key;
                        // 案号中有刑初的排除
                        String ah = k.getString("ah");
                        if (ah.indexOf("刑") != -1) {
                            continue;
                        }
                        // 中院只要有 "初" 的数据
                        if("320500".equals(fy.getFydm()) && ah.indexOf("初") == -1) {
                            continue;
                        }
                        // 只取原告、被告、第三人，且排除个人信息
                        // 格式：上诉人/原告:xxx;被上诉人/被告:xxx;原审被告/第三人:xxx
                        String dsrc = k.getString("dsrc");
                        String yg = null;// 原告（判断用）
                        String yg1 = null;// 原告
                        String bg = null;// 被告（判断用）
                        String bg1 = null;// 被告
                        String dsr = null;// 第三人（判断用）
                        String dsr1 = null;// 第三人
                        if (StringUtils.isEmpty(dsrc)) {
                            continue;
                        } else if (dsrc.indexOf("上诉") != -1) {
                            continue;
                        } else {
                            for (String s : dsrc.split(";")) {
                                try {
                                    String k1 = s.split(":")[0];// 原告/被告/第三人
                                    String v1 = s.split(":")[1];// 原告人名称/被告名称/第三人名称
                                    if (k1.indexOf("原告") != -1) {
                                        yg1 = v1;
                                        yg = excludeNatural(v1);
                                    } else if (k1.indexOf("被告") != -1) {
                                        bg1 = v1;
                                        bg = excludeNatural(v1);
                                    } else if (k1.indexOf("第三人") != -1) {
                                        dsr1 = v1;
                                        dsr = excludeNatural(v1);
                               /* } else if (k1.indexOf("上诉") != -1) {
                                    yg1 = v1;
                                    yg = excludeNatural(v1);
                                } else if (k1.indexOf("被上诉") != -1) {
                                    bg1 = v1;
                                    bg = excludeNatural(v1);*/
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    yg = null;
                                    bg = null;
                                    dsr = null;
                                }
                            }
                        }
                        // 原告、被告、第三人都是个人的排除
                        if (StringUtils.isEmpty(yg) && StringUtils.isEmpty(bg) && StringUtils.isEmpty(dsr)) {
                            continue;
                        }
                        ktggVo.setAh(ah);
                        // 数据库中存在的排除（根据案号判断）
                        List<Ktgg> exists = ktggMapper.findByConditions(ktggVo);
                        if (CollectionUtil.isNotEmpty(exists)) {
                            continue;
                        }
                        // 爬取到的数据中是否存在相同案号
                        if(ahs.contains(ah)) {
                            continue;
                        }
                        ahs.add(ah);
                        Ktgg ktgg = new Ktgg();
                        ktgg.setFyId(fy.getId());
                        ktgg.setYg(yg1);
                        ktgg.setBg(bg1);
                        ktgg.setDsr(dsr1);
                        ktgg.setAh(ah);
                        ktgg.setAhdm(k.getString("ahdm"));
                        ktgg.setAy(k.getString("ay"));
                        ktgg.setCbbm(k.getString("cbbm"));
                        ktgg.setCbr(k.getString("cbr"));
                        ktgg.setFymc(k.getString("fymc"));
                        ktgg.setKtft(k.getString("ktft"));
                        ktgg.setKtrq(k.getString("ktrq"));
                        String ktrq = k.getString("ktrq");
                        String year = null;
                        String month = null;
                        String day = null;
                        if (!StringUtils.isEmpty(ktrq)) {
                            year = ktrq.substring(0, 4);
                            month = ktrq.substring(4, 6);
                            day = ktrq.substring(6);
                        }
                        try {
                            ktgg.setKssj(DateUtil.parse(year + "-" + month + "-" + day + " " + k.getString("kssj"), "yyyy-MM-dd HH:mm"));
                        } catch (Exception e) {
                            ktgg.setKssj(null);
                        }
                        try {
                            ktgg.setJssj(DateUtil.parse(year + "-" + month + "-" + day + " " + k.getString("jssj"), "yyyy-MM-dd HH:mm"));
                        } catch (Exception e) {
                            ktgg.setJssj(null);
                        }
                        ktgg.setCreateTime(new Date());
                        list.add(ktgg);
                        fyKtgg.add(ktgg);
                    }
                }
                if (CollectionUtil.isNotEmpty(fyKtgg)) {
                    // 保存数据
                    ktggMapper.batchInsert(fyKtgg);
                }
                logger.info(fy.getFymc() + "（" + fy.getFydm() + "）共有： " + total + " 条数据！，解析入库" + fyKtgg.size() + " 条数据！");
            }
        }
        logger.info("所有法院共新增： " + list.size() + " 条数据！");
        return list;
    }

    /**
     * @Author: fk
     * @Description: 排除自然人
     * @Date: 18:22 2020/2/29
     * @Param: [names]
     * @return: java.lang.String
     **/
    private String excludeNatural(String names) {
        String result = "";
        for (String name : names.split(",")) {
            if (name.length() <= 4) {
                continue;
            } else {
                result += "," + name;
            }
        }
        if (result.length() > 0) {
            result = result.substring(1);
        }
        return result;
    }

}
