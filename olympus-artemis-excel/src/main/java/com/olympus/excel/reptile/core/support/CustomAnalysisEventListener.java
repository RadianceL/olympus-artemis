package com.olympus.excel.reptile.core.support;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.olympus.excel.reptile.core.analysis.CustomAnalyzerScheme;
import com.olympus.excel.reptile.data.ExcelDefine;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 自定义读取器 <br/>
 * since 2021/2/7
 *
 * @author eddie.lys
 */
public class CustomAnalysisEventListener extends AnalysisEventListener<Map<Integer, String>> {

    /**
     * 自定义excel映射方案
     */
    private final CustomAnalyzerScheme customAnalyzerScheme;
    /**
     * excel定义
     */
    private final ExcelDefine excelDefine;

    /**
     * 保存的map
     */
    private final List<Map<String, String>> resultMapList;

    private final boolean doAfterAllAnalysed;

    public CustomAnalysisEventListener(CustomAnalyzerScheme customAnalyzerScheme, ExcelDefine excelDefine, boolean doAfterAllAnalysed) {
        this.customAnalyzerScheme = customAnalyzerScheme;
        this.excelDefine = excelDefine;
        resultMapList = new ArrayList<>();
        this.doAfterAllAnalysed = doAfterAllAnalysed;
    }

    @Override
    public void invoke(Map<Integer, String> data, AnalysisContext context) {
//        if (CollectionUtils.isEmpty(data)) {
//            return;
//        }
//        List<ExcelFieldDefine> excelFieldDefines = excelDefine.getExcelFieldDefines();
//        Map<Integer, String> excelHeadMap = excelFieldDefines.stream().collect(
//                Collectors.toMap(ExcelFieldDefine::getFieldIndex,
//                        ExcelFieldDefine::getDataMap,
//                        (k1, k2) -> k1));
//        Map<String, String> resultMap = new HashMap<>(16);
//        data.forEach((k, v) -> {
//            String dateMapField = excelHeadMap.get(k);
//            if (StringUtils.isBlank(dateMapField)) {
//                return;
//            }
//            resultMap.put(dateMapField, v);
//        });
//        resultMapList.add(resultMap);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        if (doAfterAllAnalysed) {
            customAnalyzerScheme.saveData(excelDefine.getCompanyId(), this.resultMapList);
        }
    }
}
