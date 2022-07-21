package com.olympus.excel.support;

/**
 * 多语言扩展 <br/>
 * since 2020/8/10
 *
 * @author eddie.lys
 */
@FunctionalInterface
public interface MultilingualExtend {

    /**
     * 列获取对应的多语言文案
     * @param declarationField      excel头名称
     * @return                      对应的多语言
     */
    String buildExcelHeadName(String declarationField);
}
