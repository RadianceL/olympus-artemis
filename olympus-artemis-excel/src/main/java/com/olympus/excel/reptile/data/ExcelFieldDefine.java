package com.olympus.excel.reptile.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * excel数据列定义 <br/>
 * since 2021/2/4
 *
 * @author eddie.lys
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExcelFieldDefine {
    /**
     * 字段名称
     */
    private String fieldName;
    /**
     * 数据映射
     */
    private String dataMap;
    /**
     * 列索引
     */
    private Integer fieldIndex;
}
