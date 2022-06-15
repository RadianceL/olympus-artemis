package com.el.common.mail;

import com.el.common.constant.MailConfigConstants;
import com.el.common.mail.core.ReceiveMailService;
import com.el.common.mail.data.MailAttachment;
import com.el.common.mail.utils.MailUtils;
import lombok.extern.slf4j.Slf4j;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import javax.mail.search.FlagTerm;
import java.util.Properties;

/**
 * @author eddie.lys
 * @since 2021/7/14
 */
@Slf4j
public abstract class AbstractReceiveMailService implements ReceiveMailService, Runnable {

    private static final Properties props = System.getProperties();

    static {
        props.setProperty(MailConfigConstants.MAIL_HOST_KEY, MailConfigConstants.MAIL_HOST_VALUE);
        props.setProperty(MailConfigConstants.SSL_SOCKET_FACTORY_CLASS, MailConfigConstants.SSL_SOCKET_FACTORY);
        props.setProperty(MailConfigConstants.SSL_SOCKET_FACTORY_FALLBACK, Boolean.FALSE.toString());
        props.setProperty(MailConfigConstants.MAIL_IMAP_PORT_KEY, MailConfigConstants.MAIL_IMAP_PORT_VALUE);
        props.setProperty(MailConfigConstants.SSL_SOCKET_FACTORY_PORT_KEY, MailConfigConstants.MAIL_IMAP_PORT_VALUE);
        props.setProperty(MailConfigConstants.MAIL_IMAP_AUTH_KEY, Boolean.TRUE.toString());
    }

    @Override
    public void receiveMail(URLName url) {
        Store store = null;
        Folder inbox = null;
        try {
            store = Session.getInstance(props, null).getStore(url);
            store.connect();
            inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE);
            FetchProfile profile = new FetchProfile();
            profile.add(FetchProfile.Item.ENVELOPE);
            FlagTerm ft = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
            Message[] messages = inbox.search(ft);
            inbox.fetch(messages, profile);
            int length = messages.length;
            log.info("定时任务执行麦克田邮件分拣，收件箱的邮件数：{}", length);
            for (Message message : messages) {
                parseMessage(message);
            }
        } catch (Exception e) {
            log.error("邮件解析消息异常", e);
        } finally {
            try {
                if (inbox != null) {
                    inbox.close(false);
                }
            } catch (MessagingException e) {
                log.error("邮件文件夹连接关闭异常", e);
            }
            try {
                if (store != null) {
                    store.close();
                }
            } catch (MessagingException e) {
                log.error("邮件Store连接关闭异常", e);
            }
        }
    }

    /**
     * 解析邮件
     *
     * @param messages 要解析的邮件列表
     */
    private void parseMessage(Message... messages) throws Exception {
        if (messages == null || messages.length < 1) {
            throw new MessagingException("未找到要解析的邮件!");
        }
        for (Message message : messages) {
            MimeMessage msg = (MimeMessage) message;

            StringBuffer content = new StringBuffer(30);
            MailUtils.getMailTextContent(msg, content);
            try {
                boolean isContainerAttachment = MailUtils.isContainAttachment(msg);
                String subject = MailUtils.getSubject(msg);
                String from = MailUtils.getFrom(msg);
                String receiveAddress = MailUtils.getReceiveAddress(msg, null);
                String sentDate = MailUtils.getSentDate(msg, null);
                boolean seen = MailUtils.isSeen(msg);
                String priority = MailUtils.getPriority(msg);
                boolean replySign = MailUtils.isReplySign(msg);
                MailAttachment mailAttachment = MailUtils.getFileInputStream(msg);

                if (receiveMail(subject, from, receiveAddress, sentDate, seen, priority,
                        replySign, content.toString(), isContainerAttachment, mailAttachment)) {
                    message.setFlag(Flags.Flag.SEEN, true);
                }
            } catch (Exception e) {
                throw new RuntimeException("邮件接收任务执行失败， case：", e);
            }
        }
    }


    /**
     * 接受邮件信息
     */
    abstract boolean receiveMail(String subject, String from, String receiveAddress, String sentDate, boolean seen, String priority, boolean replySign, String context, boolean isContainerAttachment, MailAttachment attachment) throws Exception;
}
