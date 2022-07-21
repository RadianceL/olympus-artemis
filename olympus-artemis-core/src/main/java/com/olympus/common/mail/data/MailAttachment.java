package com.olympus.common.mail.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.InputStream;

/**
 * 邮件附件
 * since 8/23/21
 *
 * @author eddie
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MailAttachment {
    /**
     * 附件名称
     */
    private String fileName;
    /**
     * 附件内容
     */
    private InputStream attachment;
}
