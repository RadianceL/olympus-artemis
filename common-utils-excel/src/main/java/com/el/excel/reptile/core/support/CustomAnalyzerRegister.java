package com.el.excel.reptile.core.support;


import com.el.excel.reptile.core.analysis.CustomAnalyzerScheme;
import com.el.excel.reptile.core.analysis.CustomOutputScheme;
import com.el.excel.reptile.data.AnalysisChannel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 自定义分析助手注册机 <br/>
 * since 2021/2/5
 *
 * @author eddie.lys
 */
public class CustomAnalyzerRegister {

    /**
     * 自定义解析器注册池
     */
    private static final Map<AnalysisChannel, CustomAnalyzerScheme> EXCEL_ANALYSIS_ASSISTANT_CACHE = new ConcurrentHashMap<>(8);

    private static final Map<AnalysisChannel, CustomOutputScheme> EXCEL_OUTPUT_ASSISTANT_CACHE = new ConcurrentHashMap<>(8);

    public static void register(AnalysisChannel bizChannel, CustomAnalyzerScheme analysisAssistant) {
        EXCEL_ANALYSIS_ASSISTANT_CACHE.put(bizChannel, analysisAssistant);
    }

    public static CustomAnalyzerScheme getExcelAnalysisAssistant(AnalysisChannel bizChannel) {
        return EXCEL_ANALYSIS_ASSISTANT_CACHE.get(bizChannel);
    }

    public static void register(AnalysisChannel bizChannel, CustomOutputScheme analysisAssistant) {
        EXCEL_OUTPUT_ASSISTANT_CACHE.put(bizChannel, analysisAssistant);
    }

    public static CustomOutputScheme getExcelOutPutAssistant(AnalysisChannel bizChannel) {
        return EXCEL_OUTPUT_ASSISTANT_CACHE.get(bizChannel);
    }
}
