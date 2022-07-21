package com.olympus.excel.reptile.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * excel导出信息 <br/>
 * since 2021/2/8
 *
 * @author eddie.lys
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExcelExportData {

    /**
     * 导出渠道code
     */
    private String analysisChannelCode;
    /**
     * 导出渠道
     */
    private AnalysisChannel analysisChannel;
    /**
     * 公司ID
     */
    private String companyId;
    /**
     * sheet名称
     */
    private String sheetName;
    /**
     * 包含字段列
     */
    private List<String> includeColumnFiledNames;
    /**
     * 数据集
     */
    private Map<String, List<String>> dataMap;

}
