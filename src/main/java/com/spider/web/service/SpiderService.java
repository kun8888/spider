package com.spider.web.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.spider.constant.KtggConstant;
import com.spider.mapper.FyMapper;
import com.spider.mapper.KtggMapper;
import com.spider.util.ExcelUtil;
import com.spider.util.spider.KtggSpider;
import com.spider.vo.ExcelSheetVo;
import com.spider.vo.ExcelVo;
import com.spider.vo.KtggVo;
import com.spider.vo.ResultMap;
import com.spider.web.entity.Fy;
import com.spider.web.entity.Ktgg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @Description 爬虫 -- service
 * @Author: fk
 * @Date: 2020/3/2 18:35
 */
@Service
@Transactional
public class SpiderService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private KtggSpider ktggSpider;

    @Autowired
    private KtggMapper ktggMappe;

    @Autowired
    private FyMapper fyMapper;

    /**
     * @Author: fk
     * @Description: 爬取开庭公告数据
     * @Date: 18:40 2020/3/2
     * @Param: []
     * @return: com.spider.vo.ResultMap
     **/
    public ResultMap getKtggDatas() {
        ResultMap resultMap = new ResultMap();
        List<Ktgg> ktggs = null;// 爬取到的数据
        try {
            logger.info(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss") + "  开始爬取数据");
            ktggs = (List<Ktgg>) ktggSpider.getDatas();
//            ktggs = ktggMappe.findAll();
            logger.info(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss") + "  结束爬取数据，共爬取 " + ktggs.size() + " 条数据，现在开始生成excel");
            String ids = "";
            for (Ktgg ktgg : ktggs) {
                ids += ",'" + ktgg.getId() + "'";
            }
            KtggVo ktggVo = new KtggVo();
            if(!StringUtils.isEmpty(ids)) {
                ids = ids.substring(1);
            } else {
                ids = "'-1'";
            }
            ktggVo.setIds(ids);
            // 根据开庭日期排序，倒序
            List<Ktgg> list = ktggMappe.findByConditions(ktggVo);
            // 查询所有法院，每个法院对应一个sheet
            List<Fy> fys = fyMapper.findAll();
            Map<String, String> fyMap = new HashMap<>();
            for (Fy fy : fys) {
                fyMap.put(fy.getId(), fy.getFymc());
            }
            ExcelVo excelVo = new ExcelVo();// excel
            List<ExcelSheetVo> sheets = new ArrayList<>();// sheet集合
            List<Map<String, Object>> datas = null;// 字段对应的值
            boolean hasMade = false;// 是否已生成sheet
            ExcelSheetVo sheetVo = null;
            for (Ktgg ktgg : list) {
                String fymc = fyMap.get(ktgg.getFyId());
                hasMade = false;
                for (ExcelSheetVo sheet : sheets) {
                    if(fymc.equals(sheet.getSheetName())) {
                        hasMade = true;
                        sheetVo = sheet;
                        break;
                    }
                }
                if (!hasMade) {// 未创建sheet，则新建一个
                    sheetVo = new ExcelSheetVo();
                    sheetVo.setSheetName(fyMap.get(ktgg.getFyId()));
                    sheetVo.setTitles(KtggConstant.EXCEL_TITLES_KTGG);
                    sheetVo.setColumns(KtggConstant.EXCEL_COLUMNS_KTGG);
                    sheetVo.setCellWidths(KtggConstant.EXCEL_CELL_WIDTH_KTGG);
                    datas = new ArrayList<>();
                    sheetVo.setList(datas);
                    sheets.add(sheetVo);
                } else {
                    // 给sheet添加数据
                    datas = sheetVo.getList();
                }
                Map<String, Object> map = new HashMap<>();
                map.put("ktrq", ktgg.getKtrq());
                map.put("bg", ktgg.getBg());
                map.put("ah", ktgg.getAh());
                map.put("yg", ktgg.getYg());
                map.put("dsr", ktgg.getDsr());
                map.put("ay", ktgg.getAy());
                map.put("fymc", ktgg.getFymc());
                map.put("ktft", ktgg.getKtft());
                map.put("kssj", DateUtil.format(ktgg.getKssj(), "yyyy-MM-dd HH:mm"));
                map.put("jssj", DateUtil.format(ktgg.getKssj(), "yyyy-MM-dd HH:mm"));
                datas.add(map);
            }
            excelVo.setSheets(sheets);
            if(CollectionUtil.isNotEmpty(sheets)) {
                try {
                    ExcelUtil.makeExcelOnLocal(excelVo);
                    logger.info(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss") + "  成功导出excel");
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss") + "  导出excel失败！", e);
                }
            }
        } catch (Exception e) {
            logger.error(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss") + "  爬取数据失败！", e);
            resultMap.setSuccess(false);
        }
        return resultMap;
    }
}
