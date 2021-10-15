package com.el.common.mail;

import com.el.common.constant.MailConfigConstants;
import com.el.common.executor.GlobalExecutor;
import com.el.common.mail.data.MailAttachment;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.mail.search.FlagTerm;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

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

    public AbstractReceiveMailService() {
        GlobalExecutor.submitDistroNotifyTask(this);
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
    public void parseMessage(Message... messages) throws Exception {
        if (messages == null || messages.length < 1) {
            throw new MessagingException("未找到要解析的邮件!");
        }
        for (Message message : messages) {
            MimeMessage msg = (MimeMessage) message;
            boolean isContainerAttachment = isContainAttachment(msg);
            StringBuffer content = new StringBuffer(30);
            getMailTextContent(msg, content);
            try {
                if (receiveMail(getSubject(msg), getFrom(msg), getReceiveAddress(msg, null), getSentDate(msg, null), isSeen(msg),
                        getPriority(msg), isReplySign(msg), content.toString(), isContainerAttachment, getFileInputStream(msg))) {
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

    /**
     * 获得邮件主题
     *
     * @param msg 邮件内容
     * @return 解码后的邮件主题
     */
    public static String getSubject(MimeMessage msg) throws UnsupportedEncodingException, MessagingException {
        return MimeUtility.decodeText(msg.getSubject());
    }

    /**
     * 获得邮件发件人
     *
     * @param msg 邮件内容
     * @return 姓名 <Email地址>
     */
    public static String getFrom(MimeMessage msg) throws MessagingException, UnsupportedEncodingException {
        String from;
        Address[] froms = msg.getFrom();
        if (froms.length < 1) {
            throw new MessagingException("没有发件人!");
        }

        InternetAddress address = (InternetAddress) froms[0];
        String person = address.getPersonal();
        if (person != null) {
            person = MimeUtility.decodeText(person) + " ";
        } else {
            person = "";
        }
        from = person + "<" + address.getAddress() + ">";

        return address.getAddress();
    }

    /**
     * 根据收件人类型，获取邮件收件人、抄送和密送地址。如果收件人类型为空，则获得所有的收件人
     * <p>Message.RecipientType.TO  收件人</p>
     * <p>Message.RecipientType.CC  抄送</p>
     * <p>Message.RecipientType.BCC 密送</p>
     *
     * @param msg  邮件内容
     * @param type 收件人类型
     * @return 收件人1 <邮件地址1>, 收件人2 <邮件地址2>
     */
    public static String getReceiveAddress(MimeMessage msg, Message.RecipientType type) throws MessagingException {
        StringBuilder receiveAddress = new StringBuilder();
        Address[] addressArray;
        if (type == null) {
            addressArray = msg.getAllRecipients();
        } else {
            addressArray = msg.getRecipients(type);
        }
        if (addressArray == null || addressArray.length < 1) {
            throw new MessagingException("没有收件人!");
        }
        for (Address address : addressArray) {
            InternetAddress internetAddress = (InternetAddress) address;
            receiveAddress.append(internetAddress.toUnicodeString()).append(",");
        }
        receiveAddress.deleteCharAt(receiveAddress.length() - 1);
        return receiveAddress.toString();
    }

    /**
     * 获得邮件发送时间
     *
     * @param msg 邮件内容
     * @return yyyy年mm月dd日 星期X HH:mm
     */
    public static String getSentDate(MimeMessage msg, String pattern) throws MessagingException {
        Date receivedDate = msg.getSentDate();
        if (receivedDate == null) {
            return "";
        }
        if (pattern == null || "".equals(pattern)) {
            pattern = "yyyy年MM月dd日 E HH:mm ";
        }
        return new SimpleDateFormat(pattern).format(receivedDate);
    }

    /**
     * 判断邮件中是否包含附件
     *
     * @param part 邮件内容
     * @return 邮件中存在附件返回true，不存在返回false
     */
    public static boolean isContainAttachment(Part part) throws MessagingException, IOException {
        boolean flag = false;
        if (part.isMimeType("multipart/*")) {
            MimeMultipart multipart = (MimeMultipart) part.getContent();
            int partCount = multipart.getCount();
            for (int i = 0; i < partCount; i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                String disp = bodyPart.getDisposition();
                if (disp != null && (disp.equalsIgnoreCase(Part.ATTACHMENT) || disp.equalsIgnoreCase(Part.INLINE))) {
                    flag = true;
                } else if (bodyPart.isMimeType("multipart/*")) {
                    flag = isContainAttachment(bodyPart);
                } else {
                    String contentType = bodyPart.getContentType();
                    if (contentType.contains("application")) {
                        flag = true;
                    }

                    if (contentType.contains("name")) {
                        flag = true;
                    }
                }
                if (flag) {
                    break;
                }
            }
        } else if (part.isMimeType("message/rfc822")) {
            flag = isContainAttachment((Part) part.getContent());
        }
        return flag;
    }

    /**
     * 判断邮件是否已读
     *
     * @param msg 邮件内容
     * @return 如果邮件已读返回true, 否则返回false
     */
    public static boolean isSeen(MimeMessage msg) throws MessagingException {
        return msg.getFlags().contains(Flags.Flag.SEEN);
    }

    /**
     * 判断邮件是否需要阅读回执
     *
     * @param msg 邮件内容
     * @return 需要回执返回true, 否则返回false
     */
    public static boolean isReplySign(MimeMessage msg) throws MessagingException {
        boolean replySign = false;
        String[] headers = msg.getHeader("Disposition-Notification-To");
        if (headers != null) {
            replySign = true;
        }
        return replySign;
    }

    /**
     * 获得邮件的优先级
     *
     * @param msg 邮件内容
     * @return 1(High):紧急  3:普通(Normal)  5:低(Low)
     */
    public static String getPriority(MimeMessage msg) throws MessagingException {
        String priority = "普通";
        String[] headers = msg.getHeader("X-Priority");
        if (headers != null) {
            String headerPriority = headers[0];
            if (headerPriority.contains("1") || headerPriority.contains("High")) {
                return "紧急";
            } else if (headerPriority.contains("5") || headerPriority.contains("Low")) {
                return "低";
            } else {
                return "普通";
            }
        }
        return priority;
    }

    /**
     * 获得邮件文本内容
     *
     * @param part    邮件体
     * @param content 存储邮件文本内容的字符串
     */
    public static void getMailTextContent(Part part, StringBuffer content) throws MessagingException, IOException {
        //如果是文本类型的附件，通过getContent方法可以取到文本内容，但这不是我们需要的结果，所以在这里要做判断
        boolean isContainTextAttach = part.getContentType().indexOf("name") > 0;
        if (part.isMimeType("text/*") && !isContainTextAttach) {
            content.append(part.getContent().toString());
        } else if (part.isMimeType("message/rfc822")) {
            getMailTextContent((Part) part.getContent(), content);
        } else if (part.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) part.getContent();
            int partCount = multipart.getCount();
            for (int i = 0; i < partCount; i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                getMailTextContent(bodyPart, content);
            }
        }
    }

    private MailAttachment getFileInputStream(Part part) throws Exception {
        String fileName;
        MailAttachment mailAttachment = new MailAttachment();
        if (part.isMimeType("multipart/*")) {
            Multipart mp = (Multipart) part.getContent();
            for (int i = 0; i < mp.getCount(); i++) {
                BodyPart mPart = mp.getBodyPart(i);
                String disposition = mPart.getDisposition();
                if (StringUtils.isBlank(disposition)) {
                    continue;
                }
                if (disposition.equals(Part.ATTACHMENT) || disposition.equals(Part.INLINE)) {
                    fileName = decodeText(mPart.getFileName());
                    mailAttachment.setFileName(fileName);
                    mailAttachment.setAttachment(mPart.getInputStream());
                    return mailAttachment;
                } else if (mPart.isMimeType("multipart/*")) {
                    return getFileInputStream(mPart);
                } else {
                    fileName = mPart.getFileName();
                    if ((fileName != null)) {
                        fileName = decodeText(fileName);
                        mailAttachment.setFileName(fileName);
                        mailAttachment.setAttachment(mPart.getInputStream());
                        return mailAttachment;
                    }
                }
            }
        } else if (part.isMimeType("message/rfc822")) {
            return getFileInputStream((Part) part.getContent());
        }
        return mailAttachment;
    }

    /**
     * 文本解码
     *
     * @param encodeText 解码MimeUtility.encodeText(String text)方法编码后的文本
     * @return 解码后的文本
     */
    public static String decodeText(String encodeText) throws UnsupportedEncodingException {
        if (encodeText == null || "".equals(encodeText)) {
            return "";
        } else {
            return MimeUtility.decodeText(encodeText);
        }
    }
}
