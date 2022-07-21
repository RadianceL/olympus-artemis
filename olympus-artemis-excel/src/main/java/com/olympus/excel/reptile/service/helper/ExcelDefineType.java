package com.olympus.excel.reptile.service.helper;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 导入导出类型 <br/>
 * since 2021/4/6
 *
 * @author eddie.lys
 */
@Getter
@AllArgsConstructor
public enum ExcelDefineType {
    /**
     * 导入
     */
    INTRODUCE("1"),
    /**
     * 导出
     */
    EXPORT("2");

    private final String type;
}
