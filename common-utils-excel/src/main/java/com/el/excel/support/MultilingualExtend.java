package com.el.excel.support;

/**
 * 多语言扩展 <br/>
 * since 2020/8/10
 *
 * @author eddie.lys
 */
public interface MultilingualExtend {

    /**
     * 哥怒声明列获取对应的多语言文案
     * @param declarationField      excel头名称
     * @return                      对应的多语言
     */
    String buildExcelHeadName(String declarationField);
}
