package com.el.common.mail.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * 协议枚举 <br/>
 *
 * @author eddie.lys
 * @since 2021/1/22
 */
@Getter
@AllArgsConstructor
public enum MailAgreementEnum {

    /**
     * 协议类型
     */
    POP3("POP3"),
    SMTP("SMTP");

    private final String agreement;

    public static MailAgreementEnum findAgreement(String agreementCode) {
        if (StringUtils.isBlank(agreementCode)) {
            throw new RuntimeException("协议编码不能为空");
        }

        for (MailAgreementEnum agreementEnum : values()) {
            if (agreementEnum.getAgreement().equals(agreementCode)) {
                return agreementEnum;
            }
        }

        throw new RuntimeException("未找到对应协议，支持[POP3, SMTP]协议");
    }
}
