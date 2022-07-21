package com.olympus.excel.reptile.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * excel导入导出解析方案 <br/>
 * since 2021/2/5
 *
 * @author eddie.lys
 */
@Getter
@AllArgsConstructor
public enum AnalysisChannel {

    /**
     * 获取物料清单
     */
    MATERIAL_BILLS_MAIN("MATERIAL_BILLS_MAIN", "物料主单信息"),

    MATERIAL_COURSE_ANALYZER("MATERIAL_COURSE_ANALYZER", "物料SKU会计科目分析"),
    /**
     * 物料供应商价格信息
     */
    MATERIAL_BILLS_SKU("MATERIAL_BILLS_SKU", "物料供应商价格信息"),
    /**
     * 获取供应商清单
     */
    SUPPLIER_BILLS("SUPPLIER_BILLS", "供应商信息"),
    /**
     * 自动获取供应商清单
     */
    SUPPLIER_AUTO_BILLS("SUPPLIER_AUTO_BILLS", "自动导入供应商信息"),
    /**
     * 获取供应商联系人清单
     */
    SUPPLIER_CONTACT_BILLS("SUPPLIER_CONTACT_BILLS", "供应商联系人信息"),
    /**
     * 获取订单详情清单
     */
    ORDER_PO_MAIN("ORDER_PO_MAIN", "获取订单详情清单"),
    /**
     * 订单模版导出
     */
    ORDER_TEMPLATE_MAIN("ORDER_TEMPLATE_MAIN", "订单模版导出"),
    /**
     * 订单模版导出
     */
    ORDER_LIST_SAP_ANALYSIS("ORDER_LIST_SAP_ANALYSIS", "订单模版导入");
    /**
     * 渠道编码
     */
    private final String channelCode;
    /**
     * 渠道名称
     */
    private final String channelName;

    public static AnalysisChannel getAnalysisChannel(String channelCode) {
        if (StringUtils.isBlank(channelCode)) {
            throw new RuntimeException("导入导出方案不能为空");
        }

        for (AnalysisChannel analysisChannel : values()) {
            if (analysisChannel.getChannelCode().equals(channelCode)) {
                return analysisChannel;
            }
        }
        throw new RuntimeException("未匹配的导入导出方案 [" + channelCode + "]");
    }
}
