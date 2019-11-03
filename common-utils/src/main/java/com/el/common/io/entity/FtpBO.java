package com.el.common.io.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ftp链接常量
 * @Author jia
 * @Date 2019/6/6 17:16
 * @Description
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FtpBO {
    /**
     * 商户编码
     */
    private String mchCode;
    /**
     * 企业编码
     */
    private String companyCode;
    /**
     * ip地址
     */
    private String ipAddr;
    /**
     * 端口号
     */
    private Integer port;
    /**
     * 用户名
     */
    private String userName;
    /**
     * 密码
     */
    private String passWord;
    /**
     * 路径
     */
    private String uploadPath;
}
