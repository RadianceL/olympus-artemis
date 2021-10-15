package com.el.common.mail;

import com.el.base.utils.collection.CollectionUtils;
import com.el.base.utils.support.utils.InputStreamUtils;
import com.el.common.mail.data.MailAccountConfig;
import com.el.common.mail.data.MailAgreementEnum;
import com.el.common.mail.data.MailConfig;
import com.el.common.mail.data.MailContext;
import com.el.common.mail.utils.HtmlUtils;
import com.el.common.time.LocalTimeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.javatuples.Pair;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 邮件发送执行器 <br/>
 *
 * @author eddie.lys
 * @since 2021/1/22
 */
@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public abstract class MailSendExecutorService extends MailSendExecutor {
    /**
     * 邮件发送者缓存
     */
    private static final Map<String, JavaMailSenderImpl> JAVA_MAIL_SENDER_CACHE = new ConcurrentHashMap<>(16);
    /**
     * 邮件端口配置
     */
    private static final Properties PROPERTIES;

    static {
        PROPERTIES = new Properties();
        PROPERTIES.setProperty("mail.smtp.auth", "true");
        PROPERTIES.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
    }

    @Override
    public void handle(Pair<String, MailContext> mission) {
        MailContext mailContext = mission.getValue1();
        this.commonSendEmail(mailContext);
    }

    public void invalidJavaMailSenderString(String configKey) {
        JAVA_MAIL_SENDER_CACHE.remove(configKey);
    }

    /**
     * 获取邮件配置
     * @return  邮件配置
     */
    public abstract MailConfig getMailConfig();

    public void commonSendEmail(MailContext mailContext) {
        MailConfig mailConfig = getMailConfig();
        if (Objects.isNull(mailConfig)) {
            throw new RuntimeException("未找到对应的邮件配置");
        }
        String mailAgreement = mailConfig.getMailAgreement();
        MailAgreementEnum agreement = MailAgreementEnum.findAgreement(mailAgreement);
        MailAccountConfig mailConfigData = mailConfig.getMailAccountConfigData();
        JavaMailSenderImpl javaMailSender;
        if (MailAgreementEnum.SMTP.equals(agreement)) {
            javaMailSender = createMailSender(mailConfig);
        }else if (MailAgreementEnum.POP3.equals(agreement))  {
            throw new RuntimeException("暂时不支持POP3协议");
        }else {
            throw new RuntimeException("未定义的协议，请联系系统管理员");
        }
        Map<String, String> contextMap = mailContext.getContext();

        String context;
        if (StringUtils.isNotBlank(mailContext.getTemplateContext())) {
            context = HtmlUtils.getHtmlByStr(mailContext.getTemplateContext(), contextMap);
        } else {
            context = contextMap.get("context");
        }
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(mailConfigData.getAccountIfPresent(), mailConfigData.getAccountIfPresent());
            if (CollectionUtils.isNotEmpty(mailContext.getToList())) {
                InternetAddress[] toInternetAddressArray = new InternetAddress[mailContext.getToList().size()];
                for (int i = 0; i < mailContext.getToList().size(); i++) {
                    toInternetAddressArray[i] = new InternetAddress(mailContext.getToList().get(i));
                }
                message.setRecipients(MimeMessage.RecipientType.TO, toInternetAddressArray);
            }else {
                helper.setTo(mailContext.getTo());
            }
            if (CollectionUtils.isNotEmpty(mailContext.getCcList())) {
                InternetAddress[] ccInternetAddressArray = new InternetAddress[mailContext.getCcList().size()];
                for (int i = 0; i < mailContext.getCcList().size(); i++) {
                    ccInternetAddressArray[i] = new InternetAddress(mailContext.getCcList().get(i));
                }
                message.setRecipients(MimeMessage.RecipientType.TO, ccInternetAddressArray);
            }
            helper.setSubject(mailContext.getSubject());
            helper.setText(context, true);
            if (CollectionUtils.isNotEmpty(mailContext.getFileMap())) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream);
                zipOutputStream.setMethod(ZipOutputStream.DEFLATED);
                mailContext.getFileMap().forEach((fileName, fileStream) -> {
                    try {
                        byte[] bytes = InputStreamUtils.toByteArray(fileStream);
                        if (Objects.isNull(bytes)) {
                            return;
                        }
                        zipOutputStream.putNextEntry(new ZipEntry(fileName));
                        zipOutputStream.write(bytes);
                    }catch (Throwable throwable) {
                        log.error("邮件发送打包附件异常", throwable);
                    }
                });
                zipOutputStream.closeEntry();
                zipOutputStream.close();

                String fileName;
                if (StringUtils.isNotBlank(contextMap.get("businessOrderId"))) {
                    fileName = "附件-" + contextMap.get("businessOrderId") + ".zip";;
                }else  {
                    fileName = "附件-" + LocalTimeUtils.nowTime("yyyy-MM-dd") + ".zip";
                }
                // 转换流 修复引起流不匹配异常
                helper.addAttachment(fileName, new ByteArrayResource(IOUtils.toByteArray(parse(byteArrayOutputStream))));
            }
            javaMailSender.testConnection();
            javaMailSender.send(message);
            TimeUnit.SECONDS.sleep(1);
        } catch (Exception e) {
            throw new RuntimeException("邮件发送失败", e);
        }
    }

    @NotNull
    private JavaMailSenderImpl createMailSender(MailConfig mailConfiguration) {
        JavaMailSenderImpl javaMailSender;
        javaMailSender = JAVA_MAIL_SENDER_CACHE.get(mailConfiguration.getConfigKey());
        if (Objects.isNull(javaMailSender)) {
            MailAccountConfig mailAccountConfigData = mailConfiguration.getMailAccountConfigData();
            javaMailSender = new JavaMailSenderImpl();
            javaMailSender.setHost(mailAccountConfigData.getHostIfPresent());
            javaMailSender.setPort(mailAccountConfigData.getPortIfPresent());
            javaMailSender.setUsername(mailAccountConfigData.getAccountIfPresent());
            javaMailSender.setPassword(mailAccountConfigData.getPasswordIfPresent());
            javaMailSender.setDefaultEncoding("UTF-8");

            boolean authIfPresent = mailAccountConfigData.getAuthIfPresent();
            if (authIfPresent && mailAccountConfigData.getPortIfPresent() != 25) {
                PROPERTIES.setProperty("mail.smtp.socketFactory.port", String.valueOf(mailAccountConfigData.getPortIfPresent()));
                javaMailSender.setJavaMailProperties(PROPERTIES);
            }
            JAVA_MAIL_SENDER_CACHE.put(mailConfiguration.getConfigKey(), javaMailSender);
        }
        return javaMailSender;
    }

    public ByteArrayInputStream parse(final OutputStream out) {
        ByteArrayOutputStream byteArrayOutputStream = (ByteArrayOutputStream) out;
        return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
    }
}
