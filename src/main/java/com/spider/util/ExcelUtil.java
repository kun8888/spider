package com.spider.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import com.spider.vo.ExcelSheetVo;
import com.spider.vo.ExcelVo;
import org.apache.commons.collections4.MapUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description excel工具类
 * @Author: fk
 * @Date: 2020/3/1 12:18
 */
public class ExcelUtil {

    private static Logger logger = LoggerFactory.getLogger(ExcelUtil.class);

    public static final String LOACL_EXCEL_PATH_KTGG = "c:\\lingangSpider\\files\\ktgg";
    public static final int CLUMN_WIDTH_SET = 100;

    private ExcelUtil() {
    }

    /**
     * @Author: fk
     * @Description: 本地创建excel
     * @Date: 14:00 2020/3/1
     * @Param: [vo]
     * @return: void
     **/
    public static void makeExcelOnLocal(ExcelVo vo) throws Exception {
        File ktggDir = new File(LOACL_EXCEL_PATH_KTGG);
        if(!ktggDir.exists()) {
            ktggDir.mkdirs();
        }
        int i = ktggDir.listFiles().length;
        String filePath = LOACL_EXCEL_PATH_KTGG + "\\开庭公告" + DateUtil.format(new Date(), "yyyy-MM-dd") + "(" + (i + 1) + ")" + ".xlsx";
        File file = new File(filePath);
        file.createNewFile();
        vo.setExcelName(filePath);
        OutputStream os = new FileOutputStream(file);
        makeExcel(os, vo);
    }

    /**
     * @Author: fk
     * @Description: 网页导出excel
     * @Date: 14:06 2020/3/1
     * @Param: [response, vo]
     * @return: void
     **/
    public static void exportExcelFromWeb(HttpServletResponse response, ExcelVo vo) throws Exception {
        String excelName = LOACL_EXCEL_PATH_KTGG + DateUtil.format(new Date(), "yyyy-MM-dd") + ".xlsx";
        response.reset();
        response.setHeader("Content-disposition",
                "attachment;filename*=utf-8''" + URLEncoder.encode(excelName, "UTF-8") + ";filename=\""
                        + URLEncoder.encode(excelName, "UTF-8") + "\"");
        response.setContentType("application/vnd.ms-excel; charset=UTF-8");
        vo.setExcelName(excelName);
        OutputStream os = response.getOutputStream();
        makeExcel(os, vo);
    }

    /**
     * @Author: fk
     * @Description: 生成excel
     * @Date: 13:56 2020/3/1
     * @Param: [response, vo]
     * @return: void
     **/
    public static void makeExcel(OutputStream os, ExcelVo vo) throws Exception {
        // 先生成excel，再循环生成sheet
        XSSFWorkbook workbook = new XSSFWorkbook();
        int idx = 0;// sheet下标
        List<ExcelSheetVo> sheets = vo.getSheets();
        for (ExcelSheetVo sheet : sheets) {
            if (!StringUtils.isEmpty(sheet.getTitles()) && !StringUtils.isEmpty(sheet.getColumns())) {
                String sheetName = sheet.getSheetName();// sheet名称
                List<List<String>> values = new ArrayList<List<String>>();// 对应sheet中所有行的数据
                String[] titles = sheet.getTitles().split(",");// 标题
                String[] columns = sheet.getColumns().split(",");// 字段key

                List<Map<String, Object>> list = sheet.getList();// 字段value
                for (Map<String, Object> map : list) {
                    List<String> valueList = new ArrayList<String>();// 对应一行的数据
                    for (int i = 0; i < columns.length; i++) {
                        String value = MapUtil.getStr(map, columns[i]);
                        valueList.add(value);
                    }
                    values.add(valueList);
                }
                // sheet中每个属性必须设置
                makeExcelSheet(sheetName, titles, values, os, sheet.getCellWidths().split(","), workbook, idx);
                idx++;
            }
        }
        try {
            workbook.write(os);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public static void makeExcelSheet(String sheetName, String[] titles, List<List<String>> datas
            , OutputStream os, String[] cellWidths, XSSFWorkbook workbook, int idx) {
        List<Integer> collength = new ArrayList();

        XSSFSheet sheet = workbook.createSheet();
        workbook.setSheetName(idx, sheetName);
        XSSFCellStyle titleStyle = workbook.createCellStyle();
        titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        titleStyle.setFillForegroundColor(IndexedColors.TAN.getIndex());
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        titleStyle.setFont(font);
        XSSFCellStyle cellStyle = workbook.createCellStyle();

        /*
         * 设定合并单元格区域范围
         *  firstRow  0-based
         *  lastRow   0-based
         *  firstCol  0-based
         *  lastCol   0-based
         */
//        CellRangeAddress cra1 = new CellRangeAddress(0, 0, 2, 5);
//        CellRangeAddress cra2 = new CellRangeAddress(0, 0, 6, 8);

        //在sheet里增加合并单元格
//        sheet.addMergedRegion(cra1);
//        sheet.addMergedRegion(cra2);

        //  一级标题
        XSSFRow row0 = sheet.createRow(0);
        for (int i = 0; i < titles.length; ++i) {
            XSSFCell cell = row0.createCell(i);
            cell.setCellType(1);
            cell.setCellStyle(titleStyle);
            cell.setCellValue(titles[i]);

            collength.add(Integer.valueOf(cellWidths[i]));
        }

        //  数据集
        String tempCellContent = "";

        int i;
        for (i = 0; i < datas.size(); ++i) {
            List<String> tmpRow = (List) datas.get(i);
            XSSFRow row = sheet.createRow(i + 1);
            for (int j = 0; j < tmpRow.size(); ++j) {
                XSSFCell cell = row.createCell(j);
                cell.setCellType(1);
                cell.setCellStyle(cellStyle);
                tempCellContent = tmpRow.get(j) == null ? "" : (String) tmpRow.get(j);
                cell.setCellValue(tempCellContent);
            }
        }
        for (i = 0; i < titles.length; ++i) {
            sheet.setColumnWidth(i, (int) ((double) (Integer) collength.get(i) * 256.0D) + 184);
        }
    }

}
