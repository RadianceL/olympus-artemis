package com.olympus.excel.reptile.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * excel定义 <br/>
 * since 2021/2/4
 *
 * @author eddie.lys
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExcelDefine {

    /**
     * excel名称
     */
    private String excelName;
    /**
     * 公司Id
     */
    private String companyId;
    /**
     * 开始解析的行数
     */
    private Integer startLine;
    /**
     * sheet页
     */
    private Integer sheetIndex;
    /**
     * excel列定义
     */
    private List<ExcelFieldDefine> excelFieldDefines;
}
