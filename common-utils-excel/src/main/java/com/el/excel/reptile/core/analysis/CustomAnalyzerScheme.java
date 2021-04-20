package com.el.excel.reptile.core.analysis;


import com.el.excel.reptile.data.ExcelDefine;
import com.el.excel.reptile.data.ExcelImportResult;

import java.util.List;
import java.util.Map;

/**
 * 自定义分析器 <br/>
 * since 2021/2/5
 *
 * @author eddie.lys
 */
public interface CustomAnalyzerScheme {

    /**
     * excel分析
     * @param companyId           企业ID
     * @param dataMapList         excel行数据
     * @return                    成功失败数据
     */
    ExcelImportResult saveData(String companyId, List<Map<String, String>> dataMapList);

    /**
     * 获取excel
     * @param companyId           企业ID
     * @return    定义
     */
    ExcelDefine getExcelDefine(String companyId);
}
