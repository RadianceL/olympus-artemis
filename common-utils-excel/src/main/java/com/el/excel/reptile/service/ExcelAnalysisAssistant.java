package com.el.excel.reptile.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.el.base.utils.collection.CollectionUtils;
import com.el.excel.reptile.core.analysis.CustomAnalyzerScheme;
import com.el.excel.reptile.core.analysis.CustomOutputScheme;
import com.el.excel.reptile.core.support.CustomWriteEventListener;
import com.el.excel.reptile.data.*;
import com.el.excel.reptile.core.support.CustomAnalyzerRegister;
import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.writer.CsvWriter;
import jdk.internal.util.xml.impl.Input;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * excel分析助手 <br/>
 * since 2021/2/5
 *
 * @author eddie.lys
 */
@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ExcelAnalysisAssistant {

    private static final String EXPORT_TYPE_EXCEL = "EXCEL";

    private static final String EXPORT_TYPE_CSV = "CSV";

    /**
     * 解析excel
     * @param fileResource      文件资源
     */
    public ExcelImportResult analysis(AnalysisChannel analysisChannel, InputStream excelFileStream, String companyId, String fileResource, Charset cs) {
        CustomAnalyzerScheme excelAnalysisAssistant = getCustomAnalyzerScheme(analysisChannel);
        ExcelDefine excelDefine = excelAnalysisAssistant.getExcelDefine(companyId);
        List<Map<Integer,String>> data = new ArrayList<>();
        Integer startLine = excelDefine.getStartLine();
        CsvReader.builder().build(new InputStreamReader(excelFileStream, cs)).forEach(csvRow -> {
            if (csvRow.getFieldCount() < 3) {
                return;
            }
            long originalLineNumber = csvRow.getOriginalLineNumber();
            if (originalLineNumber <= startLine) {
                return;
            }
            int fieldCount = csvRow.getFieldCount();
            Map<Integer, String> fieldMap = new HashMap<>(8);
            for (int i = 0; i < fieldCount; i ++) {
                String field = csvRow.getField(i);
                if (StringUtils.isNotBlank(field)) {
                    field = field.replaceAll("\\u0000","");
                }else {
                    field = null;
                }
                fieldMap.put(i, field);
            }
            data.add(fieldMap);
        });
        List<Map<String, String>> resultMapList = new ArrayList<>();
        for (Map<Integer, String> datum : data) {
            Map<String, String> resultMap = findExcelFieldMap(excelDefine, datum);
            if (Objects.isNull(resultMap)) {
                continue;
            }
            resultMapList.add(resultMap);
        }
        return excelAnalysisAssistant.saveData(companyId, resultMapList);
    }

    /**
     * 解析excel 并提交保存
     * @param fileResource      文件资源
     */
    public ExcelImportResult analysisSyncWithSaveData(ExcelImportInfo excelImportInfo, InputStream fileResource) {
        CustomAnalyzerScheme excelAnalysisAssistant = getCustomAnalyzerScheme(excelImportInfo.getAnalysisChannel());
        List<Map<String, String>> analysisSync = analysisSync(excelImportInfo, fileResource);
        return excelAnalysisAssistant.saveData(excelImportInfo.getCompanyId(), analysisSync);
    }

    /**
     * 解析excel
     * @param fileResource      文件资源
     */
    public List<Map<String, String>> analysisSync(ExcelImportInfo excelImportInfo, InputStream fileResource) {
        AnalysisChannel analysisChannel = excelImportInfo.getAnalysisChannel();
        CustomAnalyzerScheme excelAnalysisAssistant = getCustomAnalyzerScheme(analysisChannel);
        String companyId = excelImportInfo.getCompanyId();
        ExcelDefine excelDefine = excelAnalysisAssistant.getExcelDefine(companyId);
        String fileType = excelImportInfo.getFileType();
        List<Map<Integer, String>> data;
        if (StringUtils.equalsIgnoreCase(fileType, EXPORT_TYPE_EXCEL)) {
            data = EasyExcel.read(fileResource)
                    .sheet(excelDefine.getSheetIndex())
                    .headRowNumber(excelDefine.getStartLine())
                    .doReadSync();
        }else if (StringUtils.equalsIgnoreCase(fileType, EXPORT_TYPE_CSV)){
            InputStreamReader reader = new InputStreamReader(fileResource, StandardCharsets.UTF_8);
            data = new ArrayList<>();
            Integer startLine = excelDefine.getStartLine();
            CsvReader.builder().build(reader).forEach(csvRow -> {
                long originalLineNumber = csvRow.getOriginalLineNumber();
                if (originalLineNumber <= startLine) {
                    return;
                }
                int fieldCount = csvRow.getFieldCount();
                Map<Integer, String> fieldMap = new HashMap<>(16);
                for (int i = 0; i < fieldCount; i ++) {
                    String field = csvRow.getField(i);
                    if (StringUtils.isBlank(field)) {
                        field = null;
                    }
                    fieldMap.put(i, field);
                }
                data.add(fieldMap);
            });
        }else {
            throw new RuntimeException("不能识别的文件类型，请使用csv 或 excel文件导入");
        }
        List<Map<String, String>> resultMapList = new ArrayList<>();
        for (Map<Integer, String> datum : data) {
            Map<String, String> resultMap = findExcelFieldMap(excelDefine, datum);
            if (Objects.isNull(resultMap)) {
                continue;
            }
            resultMapList.add(resultMap);
        }
        return resultMapList;
    }

    /**
     * 通过解析后的json导入数据
     * @param excelImportInfo       导入数据信息
     * @return                      导入结果
     */
    public ExcelImportResult analysisSyncWithSaveData(ExcelImportInfo excelImportInfo) {
        AnalysisChannel analysisChannel = excelImportInfo.getAnalysisChannel();
        CustomAnalyzerScheme excelAnalysisAssistant = getCustomAnalyzerScheme(analysisChannel);
        return excelAnalysisAssistant.saveData(excelImportInfo.getCompanyId(), excelImportInfo.getDataList());
    }

    /**
     * 文件导出
     * @param outputStream          输出流
     * @param exportType            输出类型excel ｜ csv
     * @param excelExportDataList   数据集
     */
    public void export(OutputStream outputStream, String exportType, List<ExcelExportData> excelExportDataList) {
        if (EXPORT_TYPE_EXCEL.equalsIgnoreCase(exportType)) {
            exportExcel(outputStream, excelExportDataList);
        }else if (EXPORT_TYPE_CSV.equalsIgnoreCase(exportType)) {
            exportCsv(outputStream, excelExportDataList);
        }
    }

    /**
     * 文件导出
     * @param outputStream          输出流
     * @param excelExportData       数据集
     */
    public void exportOrderTemplate(OutputStream outputStream, InputStream baseExcelHeader, ExcelExportData excelExportData) {
        CustomOutputScheme excelOutPutAssistant = findCustomOutputScheme(excelExportData);
        ExcelDefine excelDefine = excelOutPutAssistant.getExcelDefine(excelExportData.getCompanyId());
        ExcelWriter excelWriter = EasyExcel.write(outputStream)
                .withTemplate(baseExcelHeader)
                .inMemory(true)
                .relativeHeadRowIndex(excelDefine.getStartLine())
                .build();
        try {
            Map<String, List<String>> dataMap = excelExportData.getDataMap();
            dataMap = excelOutPutAssistant.filterUselessField(excelDefine, dataMap, excelExportData.getIncludeColumnFiledNames());
            List<List<String>> bodyList = excelOutPutAssistant.getBodyList(excelDefine, dataMap);
            WriteSheet writeSheet = EasyExcel.writerSheet(0, excelExportData.getSheetName()).build();
            excelWriter.write(bodyList, writeSheet);
        }finally {
            excelWriter.finish();
        }
    }


    /**
     * 导出excel流
     * @param outputStream          输出流
     * @param excelExportDataList   数据集
     */
    private void exportExcel(OutputStream outputStream, List<ExcelExportData> excelExportDataList) {
        int size = excelExportDataList.size();
        HorizontalCellStyleStrategy horizontalCellStyleStrategy = getHorizontalCellStyleStrategy();
        ExcelWriter excelWriter = EasyExcel.write(outputStream)
                .registerWriteHandler(horizontalCellStyleStrategy)
                .registerWriteHandler(new CustomWriteEventListener())
                .build();
        try {
            for (int i = 0; i < size; i ++) {
                ExcelExportData excelExportData = excelExportDataList.get(i);
                CustomOutputScheme excelOutPutAssistant = findCustomOutputScheme(excelExportData);
                ExcelDefine excelDefine = excelOutPutAssistant.getExcelDefine(excelExportData.getCompanyId());
                Map<String, List<String>> dataMap = excelExportData.getDataMap();
                dataMap = excelOutPutAssistant.filterUselessField(excelDefine, dataMap, excelExportData.getIncludeColumnFiledNames());
                List<List<String>> headers = excelOutPutAssistant.getHeaders(excelDefine, dataMap);
                List<List<String>> bodyList = excelOutPutAssistant.getBodyList(excelDefine, dataMap);
                WriteSheet writeSheet = EasyExcel.writerSheet(i, excelExportData.getSheetName())
                        .head(headers).build();
                writeSheet.setHead(headers);
                excelWriter.write(bodyList, writeSheet);
            }
        }finally {
            excelWriter.finish();
        }
    }

    /**
     * 导出csv流
     * @param outputStream          输出流
     * @param excelExportDataList   数据集
     */
    private void exportCsv(OutputStream outputStream, List<ExcelExportData> excelExportDataList) {
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
        try (CsvWriter csv = CsvWriter.builder().build(outputStreamWriter)) {
            for (ExcelExportData excelExportData : excelExportDataList) {
                CustomOutputScheme excelOutPutAssistant = findCustomOutputScheme(excelExportData);
                ExcelDefine excelDefine = excelOutPutAssistant.getExcelDefine(excelExportData.getCompanyId());
                Map<String, List<String>> dataMap = excelExportData.getDataMap();
                dataMap = excelOutPutAssistant.filterUselessField(excelDefine, dataMap, excelExportData.getIncludeColumnFiledNames());
                List<String> headers = excelOutPutAssistant.getHeadersCrosswise(excelDefine, dataMap);
                List<List<String>> bodyList = excelOutPutAssistant.getBodyListCrosswise(excelDefine, dataMap);
                csv.writeRow(headers);
                bodyList.forEach(csv::writeRow);
            }
        } catch (IOException e) {
            throw new RuntimeException("未知csv导出异常: " + e.getMessage());
        }
    }

    @NotNull
    private CustomOutputScheme findCustomOutputScheme(ExcelExportData excelExportData) {
        AnalysisChannel analysisChannel = excelExportData.getAnalysisChannel();
        CustomOutputScheme excelOutPutAssistant = CustomAnalyzerRegister.getExcelOutPutAssistant(analysisChannel);
        if (Objects.isNull(excelOutPutAssistant)) {
            throw new RuntimeException("未配置的渠道，请配置后重新解析");
        }
        return excelOutPutAssistant;
    }

    /**
     * 导出excel头样式策略
     * @return              策略
     */
    private HorizontalCellStyleStrategy getHorizontalCellStyleStrategy() {
        // 头的策略
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        // 背景色
        headWriteCellStyle.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
        WriteFont headWriteFont = new WriteFont();
        headWriteFont.setFontHeightInPoints((short) 12);
        headWriteCellStyle.setWriteFont(headWriteFont);
        // 内容的策略
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        WriteFont contentWriteFont = new WriteFont();
        // 字体大小
        contentWriteFont.setFontHeightInPoints((short) 12);
        contentWriteCellStyle.setWriteFont(contentWriteFont);
        //设置 自动换行
        contentWriteCellStyle.setWrapped(true);
        //设置 垂直居中
        contentWriteCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        return new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);
    }

    /**
     * 映射头名称
     * @param excelDefine       excel定义
     * @param datum             数据
     * @return                  映射结果
     */
    private Map<String, String> findExcelFieldMap(ExcelDefine excelDefine, Map<Integer, String> datum) {
        if (CollectionUtils.isEmpty(datum)) {
            return null;
        }
        List<ExcelFieldDefine> excelFieldDefines = excelDefine.getExcelFieldDefines();
        Map<Integer, String> excelHeadMap = excelFieldDefines.stream().collect(
                Collectors.toMap(ExcelFieldDefine::getFieldIndex,
                        ExcelFieldDefine::getDataMap,
                        (k1, k2) -> k1));
        Map<String, String> resultMap = new HashMap<>(8);
        datum.forEach((k, v) -> {
            String dateMapField = excelHeadMap.get(k);
            if (StringUtils.isBlank(dateMapField)) {
                return;
            }
            resultMap.put(dateMapField, v);
        });
        return resultMap;
    }

    /**
     * 获取自定义分析计划
     * @param analysisChannel           渠道
     * @return                          自定义分析计划
     */
    private CustomAnalyzerScheme getCustomAnalyzerScheme(AnalysisChannel analysisChannel) {
        CustomAnalyzerScheme excelAnalysisAssistant = CustomAnalyzerRegister.getExcelAnalysisAssistant(analysisChannel);
        if (Objects.isNull(excelAnalysisAssistant)) {
            throw new RuntimeException("未配置的渠道，请配置后重新解析");
        }
        return excelAnalysisAssistant;
    }

}
