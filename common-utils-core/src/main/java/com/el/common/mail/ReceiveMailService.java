package com.el.common.mail;

import javax.mail.URLName;

/**
 * 邮件接受服务
 *
 * @author eddie.lys
 * @since 2021/7/14
 */
public interface ReceiveMailService {

    /**
     * 启动监听器
     */
    void startListener(URLName url);
}
