package com.emdata.messagewarningscore.data.util;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.util.Enumeration;

/**
 * @description: 读取mht文件内容工具类
 * @date: 2020/12/9
 * @author: sunming
 */
public class ReadFromMhtUtil {

    public static void main(String[] args) {

        String filePath = "D:/2020080501.jcjb.doc";
        File file = new File(filePath);
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            String fromMht = readFromMht(in);
            System.out.println(fromMht);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取mht文件内容
     *
     * @param in 输入流
     * @return 文本内容
     */
    public static String readFromMht(InputStream in) {
        String result = "";
        try {
            Session mailSession = Session.getDefaultInstance(
                    System.getProperties(), null);
            MimeMessage mimeMessage = new MimeMessage(mailSession, in);
            Object content = mimeMessage.getContent();
            if (content instanceof Multipart) {
                Multipart mp = (Multipart) content;
                MimeBodyPart bp1 = (MimeBodyPart) mp.getBodyPart(0);
                String encoding = getEncoding(bp1);
                String html = getHtmlText(bp1, encoding);
                // 转为纯文本内容
                result = toPlainText(html);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 将html内容转为纯文本内容
     *
     * @param html html内容
     * @return 纯文本内容
     */
    public static String toPlainText(String html) {
        if (html == null) {
            return "";
        }
        Document document = Jsoup.parse(html);
        Document.OutputSettings outputSettings = new Document.OutputSettings().prettyPrint(false);
        document.outputSettings(outputSettings);
        document.select("br").append("\\n");
        document.select("p").prepend("\\n");
        document.select("p").append("\\n");
        String newHtml = document.html().replaceAll("\\\\n", "\n");
        String plainText = Jsoup.clean(newHtml, "", Whitelist.none(), outputSettings);
        String result = StringEscapeUtils.unescapeHtml4(plainText.trim());
        return result;
    }


    /**
     * 获取mht文件中的内容代码
     *
     * @param bp
     * @param strEncoding 该mht文件的编码
     * @return
     */
    private static String getHtmlText(MimeBodyPart bp, String strEncoding) {
        InputStream textStream = null;
        BufferedInputStream buff = null;
        BufferedReader br = null;
        Reader r = null;
        try {
            textStream = bp.getInputStream();
            buff = new BufferedInputStream(textStream);
            r = new InputStreamReader(buff, strEncoding);
            br = new BufferedReader(r);
            StringBuffer strHtml = new StringBuffer("");
            String strLine = null;
            while ((strLine = br.readLine()) != null) {
                strHtml.append(strLine + "\r\n");
            }
            br.close();
            r.close();
            textStream.close();
            return strHtml.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
                if (buff != null) {
                    buff.close();
                }
                if (textStream != null) {
                    textStream.close();
                }
            } catch (Exception e) {
            }
        }
        return null;
    }

    /**
     * 获取mht网页文件中内容代码的编码
     *
     * @param bp
     * @return
     */
    private static String getEncoding(MimeBodyPart bp) {
        if (bp == null) {
            return null;
        }
        try {
            Enumeration list = bp.getAllHeaders();
            while (list.hasMoreElements()) {
                javax.mail.Header head = (javax.mail.Header) list.nextElement();
                if (head.getName().equalsIgnoreCase("Content-Type")) {
                    String strType = head.getValue();
                    int pos = strType.indexOf("charset=");
                    if (pos >= 0) {
                        String strEncoding = strType.substring(pos + 8,
                                strType.length());
                        if (strEncoding.startsWith("\"")
                                || strEncoding.startsWith("\'")) {
                            strEncoding = strEncoding.substring(1,
                                    strEncoding.length());
                        }
                        if (strEncoding.endsWith("\"")
                                || strEncoding.endsWith("\'")) {
                            strEncoding = strEncoding.substring(0,
                                    strEncoding.length() - 1);
                        }
                        if (strEncoding.toLowerCase().compareTo("gb2312") == 0) {
                            strEncoding = "gbk";
                        }
                        return strEncoding;
                    }
                }
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return null;
    }

}
