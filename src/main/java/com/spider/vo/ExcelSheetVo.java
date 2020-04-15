package com.spider.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @Description excel中的sheet
 * @Author: fk
 * @Date: 2020/3/1 14:14
 */
@Data
public class ExcelSheetVo {

    private String sheetName;
    private String titles;
    private String columns;
    private String cellWidths;
    private List<Map<String, Object>> list;


}
