package com.spider.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @Description excelvo类
 * @Author: fk
 * @Date: 2020/3/1 12:32
 */
@Data
public class ExcelVo {

    private String excelName;
    private List<ExcelSheetVo> sheets;
}
