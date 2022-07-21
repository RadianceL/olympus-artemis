package com.olympus.base.utils.support.io.ftp.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ftp链接所需信息 <br/>
 * 2019/6/5
 *
 * @author eddie.lys
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FtpInfo {

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
