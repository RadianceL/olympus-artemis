package com.el.common.mail.data;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 邮件上下文内容 <br/>
 *
 * @author eddie.lys
 * @since 2021/1/22
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MailContext {

    /**
     * 业务渠道
     */
    private String bizChannel;
    /**
     * 公司ID
     */
    private String companyId;
    /**
     * 主题
     */
    private String subject;
    /**
     * 模版ID
     */
    private String templateContext;
    /**
     * 邮箱类型；内部：inner；外部：outer；
     */
    private String mailType = "inner";
    /**
     * 目标
     */
    private String to;
    /**
     * 多接收人列表
     */
    private List<String> toList;
    /**
     * 多抄送人列表
     */
    private List<String> ccList;
    /**
     * 是否需要打包发送附件
     */
    private Boolean needZipFile;
    /**
     * 附件列表
     */
    private Map<String, InputStream> fileMap;
    /**
     * 内容
     */
    private Map<String, String> context;

    public static final List<String> NON_NULL_FIELD_LOAD = Lists.newArrayList(
            "bizChannel", "companyId", "mailType"
    );
    public static final List<String> NON_NULL_FIELD = Lists.newArrayList(
            "bizChannel", "companyId", "subject"
    );

    public Boolean getNeedZipFileIfPresent() {
        if (Objects.isNull(this.needZipFile)) {
            return Boolean.FALSE;
        }
        return needZipFile;
    }
}
