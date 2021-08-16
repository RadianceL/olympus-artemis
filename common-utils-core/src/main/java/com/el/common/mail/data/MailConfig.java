package com.el.common.mail.data;

import com.el.base.utils.support.utils.Md5Utils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 邮件配置
 * since 8/16/21
 *
 * @author eddie
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MailConfig {

    /**
     * 业务渠道
     */
    private String bizChannel;
    /**
     * 公司Id
     */
    private String companyId;
    /**
     * 协议
     */
    private String mailAgreement;
    /**
     * 认证类型
     */
    private Integer mailVerifyType;
    /**
     * 邮箱账户配置
     */
    private MailAccountConfig mailAccountConfigData;


    public String getConfigKey() {
        return Md5Utils.encode(this.bizChannel + this.companyId);
    }
}
