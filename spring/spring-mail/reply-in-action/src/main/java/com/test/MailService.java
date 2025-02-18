package com.test;

import jakarta.mail.*;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.search.ComparisonTerm;
import jakarta.mail.search.ReceivedDateTerm;
import jakarta.mail.search.SearchTerm;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class MailService {

    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.port}")
    private int port;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;
    private static final Set<String> SEARCH_FOLDERS = Set.of("INBOX", "Sent Messages", "Drafts");
    private static final Pattern MESSAGE_ID_PATTERN = Pattern.compile("<([^>]+)>");
    private final int searchTime = -90;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    // 提取公共的获取 Store 的方法
    private Store getStore() throws MessagingException {
        Properties properties = new Properties();
        properties.put("mail.imaps.host", host);
        properties.put("mail.imaps.port", port);
        properties.put("mail.imaps.ssl.enable", "true");

        Session session = Session.getDefaultInstance(properties);
        return session.getStore("imaps");
    }

    // 提取公共的获取搜索时间的方法
    private Date getSearchTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, searchTime);
        return calendar.getTime();
    }

    /**
     * 收取最近半小时内的邮件，并解析正文与历史记录部分
     */
    public void fetchMails() {
        try (Store store = getStore()) {
            store.connect(host, username, password);

            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            SearchTerm searchTerm = new ReceivedDateTerm(ComparisonTerm.GT, getSearchTime());
            Message[] messages = inbox.search(searchTerm);

            for (Message message : messages) {
                if (message instanceof MimeMessage) {
                    MimeMessage mimeMessage = (MimeMessage) message;
                    String[] parts = extractBodyAndHistory(mimeMessage);
                    String body = parts[0];
                    String history = parts[1];

                    log.info("邮件主题: {}", mimeMessage.getSubject());
                    log.info("正文信息: {}", body);
                    log.info("历史记录: {}", history);
                    log.info("消息ID: {}", mimeMessage.getMessageID());
                    log.info("发送日期: {}", DATE_FORMAT.format(mimeMessage.getSentDate()));
                    log.info("\n====================================================");
                }
            }
            inbox.close(false);
        } catch (Exception e) {
            log.error("收取邮件时发生错误", e);
        }
    }

    public void fetchMailChain() {
        try (Store store = getStore()) {
            store.connect(host, username, password);

            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            SearchTerm searchTerm = new ReceivedDateTerm(ComparisonTerm.GT, getSearchTime());
            int i = 1;
            for (Message message : inbox.search(searchTerm)) {
                if (message instanceof MimeMessage) {
                    MimeMessage mimeMessage = (MimeMessage) message;
                    List<MimeMessage> chain = getMailChain(mimeMessage, store);
                    log.info("开始解析第{}封邮件的链路:\n", i++);
                    log.info("邮件主题: {}", mimeMessage.getSubject());
                    log.info("完整邮件链（{} 封）:", chain.size());
                    log.info("\n**************************************");
                    int j = 1;
                    for (MimeMessage msg : chain) {
                        try {
                            String body = getTextContent(msg);
                            log.info("| 历史邮件:{}, 主题: {}", j++, msg.getSubject());
                            log.info("| 消息ID: {}", cleanMessageId(msg.getMessageID()));
                            log.info("| 发送日期: {}", DATE_FORMAT.format(msg.getSentDate()));
                            log.info("| 正文内容: {}", body);
                            log.info("|----------------------");
                        } catch (MessagingException | IOException e) {
                            log.error("获取邮件信息时发生错误", e);
                        }
                    }
                    log.info("\n**************************************");
                }
            }
            inbox.close(false);
        } catch (Exception e) {
            log.error("获取邮件链时发生错误", e);
        }
    }

    private List<MimeMessage> getMailChain(MimeMessage message, Store store) throws MessagingException {
        LinkedHashMap<String, MimeMessage> chainMap = new LinkedHashMap<>();
        Set<String> processedIds = new HashSet<>();

        // 添加当前邮件
        addToChain(message, chainMap);

        // 处理In-Reply-To链
        String inReplyTo = getFirstMessageId(message.getHeader("In-Reply-To", null));
        while (inReplyTo != null && !processedIds.contains(inReplyTo)) {
            processedIds.add(inReplyTo);
            MimeMessage parent = findParentMessage(store, inReplyTo);
            if (parent != null) {
                addToChain(parent, chainMap);
                inReplyTo = getFirstMessageId(parent.getHeader("In-Reply-To", null));
            } else {
                break;
            }
        }

        // 处理References链
        String[] references = message.getHeader("References", null) != null ?
                message.getHeader("References", null).split("\\s+") : new String[0];
        for (String ref : references) {
            String cleanRef = cleanMessageId(ref);
            if (!chainMap.containsKey(cleanRef)) {
                MimeMessage refMsg = findParentMessage(store, cleanRef);
                if (refMsg != null) {
                    addToChain(refMsg, chainMap);
                }
            }
        }

        return new ArrayList<>(chainMap.values());
    }

    private void addToChain(MimeMessage message, LinkedHashMap<String, MimeMessage> chainMap)
            throws MessagingException {
        String messageId = cleanMessageId(message.getMessageID());
        if (messageId != null && !chainMap.containsKey(messageId)) {
            chainMap.put(messageId, message);
        }
    }

    private MimeMessage findParentMessage(Store store, String messageId) throws MessagingException {
        if (messageId == null || messageId.isEmpty()) return null;

        String cleanId = cleanMessageId(messageId);
        if (cleanId == null) return null;
        String id = "<" + cleanId + ">";
        for (String folderName : SEARCH_FOLDERS) {
            Folder folder = store.getFolder(folderName);
            if (!folder.exists()) continue;
            folder.open(Folder.READ_ONLY);
            Message[] allMessages = folder.getMessages();
            for (Message m : allMessages) {
                if (m instanceof MimeMessage) {
                    String messageID = ((MimeMessage) m).getMessageID();
                    if (id.equals(messageID)) {
                        return (MimeMessage) m;
                    }
                }
            }
        }
        log.info("未找到ID为 [{}] 的父邮件", cleanId);
        return null;
    }

    private String cleanMessageId(String messageId) {
        if (messageId == null) return null;
        Matcher matcher = MESSAGE_ID_PATTERN.matcher(messageId);
        return matcher.find() ? matcher.group(1) : messageId.trim();
    }

    private String getFirstMessageId(String header) {
        if (header == null) return null;
        String[] ids = header.split("\\s+");
        return ids.length > 0 ? cleanMessageId(ids[0]) : null;
    }

    /**
     * 分离邮件正文与历史记录
     * 采用两种分隔规则：使用正则表达式匹配 "On ... wrote:" 或 "-----Original Message-----"
     * 如果匹配失败且内容为 HTML，则尝试通过 Jsoup 解析特定的 div.history 节点（需根据实际邮件格式调整）
     *
     * @param message 邮件对象
     * @return String[0] 为正文，String[1] 为历史记录（如果有）
     */
    private String[] extractBodyAndHistory(MimeMessage message) throws MessagingException, IOException {
        String content = getTextContent(message);
        String body = content;
        String history = "";

        // 定义正则表达式，注意使用非贪婪匹配
        String[] regexSeparators = {
                "On\\s+.*?wrote:",
                "-----Original Message-----"
        };

        for (String regex : regexSeparators) {
            Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
            Matcher matcher = pattern.matcher(content);
            if (matcher.find()) {
                int index = matcher.start();
                body = content.substring(0, index).trim();
                history = content.substring(index).trim();
                break;
            }
        }

        // 如果未匹配到分隔符且内容包含 HTML，则尝试通过 Jsoup 查找特定节点（根据实际情况调整选择器）
        if (history.isEmpty() && content.toLowerCase().contains("<html")) {
            Document doc = Jsoup.parse(content);
            Elements historyElements = doc.select("div.history");
            if (!historyElements.isEmpty()) {
                Element historyElement = historyElements.first();
                history = historyElement.outerHtml();
                // 移除历史部分，保留正文
                body = doc.body().html().replace(history, "").trim();
            }
        }

        return new String[]{body, history};
    }

    /**
     * 递归提取 Part 中的文本内容。
     * 支持 text/plain、text/html、multipart/* 以及嵌套的 message/rfc822 类型。
     *
     * @param part 邮件的 Part 对象
     * @return 解析后的纯文本内容
     */
    private String getTextContent(Part part) throws MessagingException, IOException {
        if (part.isMimeType("text/plain")) {
            return (String) part.getContent();
        } else if (part.isMimeType("text/html")) {
            String html = (String) part.getContent();
            return Jsoup.parse(html).text();
        } else if (part.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) part.getContent();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < multipart.getCount(); i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                sb.append(getTextContent(bodyPart));
            }
            return sb.toString();
        } else if (part.isMimeType("message/rfc822")) {
            return getTextContent((Part) part.getContent());
        }
        return "";
    }
}