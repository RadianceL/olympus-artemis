package com.el.excel.reptile.core.analysis;

import com.el.base.utils.collection.CollectionUtils;
import com.el.excel.reptile.data.ExcelDefine;
import com.el.excel.reptile.data.ExcelFieldDefine;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 自定义分析器 <br/>
 * since 2021/2/5
 *
 * @author eddie.lys
 */
public interface CustomOutputScheme {

    /**
     * 获取excel
     * @param companyId     公司ID
     * @return              excel定义
     */
    ExcelDefine getExcelDefine(String companyId);
    /**
     * excel分析
     * @param excelDefine         excel定义
     * @param dataMap             excel行数据
     * @param includeColumnFiledNames
     * @return                    过滤后的数据
     */
    default Map<String, List<String>> filterUselessField(ExcelDefine excelDefine, Map<String, List<String>> dataMap, List<String> includeColumnFiledNames){
        List<ExcelFieldDefine> excelFieldDefines = excelDefine.getExcelFieldDefines();
        List<String> fieldHeaders = excelFieldDefines.stream().map(ExcelFieldDefine::getDataMap).collect(Collectors.toList());
        Set<String> dataFieldHeaders = dataMap.keySet();
        List<String> differenceSet = dataFieldHeaders.stream()
                .filter(item -> !fieldHeaders.contains(item))
                .collect(Collectors.toList());
        differenceSet.addAll(dataFieldHeaders.stream()
                .filter(item -> !includeColumnFiledNames.contains(item))
                .collect(Collectors.toList()));
        if (CollectionUtils.isNotEmpty(differenceSet)) {
            differenceSet.forEach(dataMap::remove);
        }
        return dataMap;
    }
    /**
     * 获取excel头信息
     * @param excelDefine   excel定义
     * @param dataMap       excel数据
     * @return              头数据
     */
     default List<List<String>> getHeaders(ExcelDefine excelDefine, Map<String, List<String>> dataMap) {
         List<List<String>> headList = new ArrayList<>();
         Set<String> originalHeaderCodes = dataMap.keySet();
         Map<String, String> excelFieldMap = findExcelFieldMap(excelDefine, new ArrayList<>(originalHeaderCodes));
         List<String> sortedHeaderList = excelDefine.getExcelFieldDefines().stream()
                 .sorted(Comparator.comparingInt(ExcelFieldDefine::getFieldIndex))
                 .map(ExcelFieldDefine::getDataMap).collect(Collectors.toList());
         for (String key: sortedHeaderList) {
             String fieldName = excelFieldMap.get(key);
             if (StringUtils.isBlank(fieldName)) {
                 continue;
             }
             List<String> head = Lists.newArrayList(fieldName);
             headList.add(head);
         }
         return headList;
     }

     /**
      * 获取excel头信息
      * @param excelDefine   excel定义
      * @param excelDefine
      * @param dataMap       excel数据
      * @return              头数据
      */
     default List<String> getHeadersCrosswise(ExcelDefine excelDefine, Map<String, List<String>> dataMap) {
         List<String> originalHeaderCodes = new ArrayList<>(dataMap.keySet());
         List<String> sortedHeaderList = excelDefine.getExcelFieldDefines().stream()
                 .sorted(Comparator.comparingInt(ExcelFieldDefine::getFieldIndex))
                 .map(ExcelFieldDefine::getDataMap).collect(Collectors.toList());
         Map<String, String> excelFieldMap = findExcelFieldMap(excelDefine, originalHeaderCodes);

         List<String> headers = new ArrayList<>();
         sortedHeaderList.forEach(sortedHeader -> {
             String sortedHeaderName = excelFieldMap.get(sortedHeader);
             if (StringUtils.isNotBlank(sortedHeaderName)) {
                 headers.add(sortedHeaderName);
             }
         });
         return headers;
     }

    /**
     * 获取excel数据体
     * @param dataMap       excel数据
     * @return              excel数据体
     */
     default List<List<String>> getBodyList(ExcelDefine excelDefine, Map<String, List<String>> dataMap) {
         List<List<String>> list = new ArrayList<>();
         int max = 0;
         for (String key : dataMap.keySet()) {
             List<String> lineData = dataMap.get(key);
             if (max < lineData.size()) {
                 max = lineData.size();
             }
         }
         List<String> sortedHeaderList = excelDefine.getExcelFieldDefines().stream()
                 .sorted(Comparator.comparingInt(ExcelFieldDefine::getFieldIndex))
                 .map(ExcelFieldDefine::getDataMap).collect(Collectors.toList());
         for (int i = 0; i < max; i++) {
             List<String> line = new ArrayList<>();
             for (String key : sortedHeaderList) {
                 List<String> lineData = dataMap.get(key);
                 if (CollectionUtils.isEmpty(lineData)) {
                     continue;
                 }
                 line.add(lineData.get(i));
             }
             list.add(line);
         }
         return list;
     }

     /**
     * 获取csv数据体
     *
      * @param excelDefine
      * @param dataMap       excel数据
      * @return              excel数据体
     */
     default List<List<String>> getBodyListCrosswise(ExcelDefine excelDefine, Map<String, List<String>> dataMap) {
         List<List<String>> list = new ArrayList<>();
         Map<Integer, List<String>> tmpMap = new HashMap<>(16);
         List<String> sortedHeaderList = excelDefine.getExcelFieldDefines().stream()
                 .sorted(Comparator.comparingInt(ExcelFieldDefine::getFieldIndex))
                 .map(ExcelFieldDefine::getDataMap).collect(Collectors.toList());

         List<List<String>> tmpList = new ArrayList<>();
         for (String sortedHeader : sortedHeaderList) {
             List<String> data = dataMap.get(sortedHeader);
             if (CollectionUtils.isEmpty(data)) {
                 continue;
             }
             tmpList.add(data);
         }
         for (List<String> tmp : tmpList) {
             int itemSize = tmp.size();
             for (int j = 0; j < itemSize; j++) {
                 List<String> dataLine = tmpMap.get(j);
                 if (Objects.isNull(dataLine)) {
                     dataLine = new ArrayList<>();
                     tmpMap.put(j, dataLine);
                 }
                 dataLine.add(tmp.get(j));
             }
         }
         tmpMap.forEach((k, v) -> list.add(v));
         return list;
     }

    /**
     * 映射头名称
     * @param excelDefine       excel定义
     * @param header             数据
     * @return                  映射结果
     */
    default Map<String, String> findExcelFieldMap(ExcelDefine excelDefine, List<String> header) {
        List<ExcelFieldDefine> excelFieldDefines = excelDefine.getExcelFieldDefines();
        Map<String, String> excelHeadMap = excelFieldDefines.stream().collect(
                Collectors.toMap(ExcelFieldDefine::getDataMap,
                        ExcelFieldDefine::getFieldName,
                        (k1, k2) -> k1));
        Map<String, String> resultMap = new HashMap<>(16);
        header.forEach(v -> {
            String dateMapField = excelHeadMap.get(v);
            if (StringUtils.isBlank(dateMapField)) {
                return;
            }
            resultMap.put(v, dateMapField);
        });
        return resultMap;
    }

}
