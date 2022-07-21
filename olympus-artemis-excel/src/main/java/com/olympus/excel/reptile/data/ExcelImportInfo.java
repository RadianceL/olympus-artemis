package com.olympus.excel.reptile.data;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * <br/>
 * since 2021/2/17
 *
 * @author eddie.lys
 */
@Data
public class ExcelImportInfo {

    /**
     * 文件定义
     */
    private AnalysisChannel analysisChannel;
    /**
     * 企业id
     */
    private String companyId;
    /**
     * 文件类型 csv ｜ excel
     */
    private String fileType;
    /**
     * 数据集
     */
    private List<Map<String, String>> dataList;
}
