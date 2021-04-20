package com.el.excel.reptile.data;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * excel导入返回值 <br/>
 * since 2021/2/17
 *
 * @author eddie.lys
 */
@Data
public class ExcelImportResult {

    /**
     * 导入成功 原数据
     */
    private List<Map<String, String>> importSuccessList;
    /**
     * 导入失败 原数据 + 失败原因
     */
    private List<Map<String, String>> importFailureList;

    public boolean isSuccess() {
        return Objects.nonNull(importFailureList) && importFailureList.size() == 0;
    }

    public static ExcelImportResult ofEmpty() {
        ExcelImportResult excelImportResult = new ExcelImportResult();
        excelImportResult.setImportSuccessList(new ArrayList<>());
        excelImportResult.setImportFailureList(new ArrayList<>());
        return excelImportResult;
    }
}
