package com.emdata.messagewarningscore.data.util;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.rtf.RTFEditorKit;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @description: 读取rtf文件内容工具类
 * @date: 2020/12/9
 * @author: sunming
 */
public class ReadFromRtfUtil {


    /**
     * 获取rtf文件表格上边的文本内容
     *
     * @param rtf
     * @return
     */
    public static String readTitleFromRtf(String rtf) {
        String rtfHeaderText = "";
        if (rtf.contains("\\trowd") && rtf.contains("\\row") && rtf.contains("\\cell")) {
            String rtfHeader = "";
            String row = "";
            Pattern pat = Pattern.compile("(\\\\trowd(.*?)\\\\row)");//获得行
            Matcher mat = pat.matcher(rtf);
            if (mat.find()) {
                row = mat.group(1);
                if (rtfHeader.equals("")) {
                    rtfHeader = rtf.substring(0, rtf.indexOf(row)) + " ";//RTFEditorKit 需要这些内容才能解析
                }
                rtfHeaderText = ReadFromRtfUtil.toPlainText(rtfHeader);
            }
        }
        return rtfHeaderText;
    }


    /**
     * 读取rtf文件表格内容
     *
     * @param rtf
     */
    public static List<String> getTextFromRtf(String rtf) {
        List<String> list = new ArrayList<>();
        if (rtf.contains("\\trowd") && rtf.contains("\\row") && rtf.contains("\\cell")) {
            String rtfHeader = "";
            String row = "";
            Pattern pat = Pattern.compile("(\\\\trowd(.*?)\\\\row)");//获得行
            Matcher mat = pat.matcher(rtf);
            while (mat.find()) {
                row = mat.group(1);
                if (rtfHeader.equals("")) {
                    rtfHeader = rtf.substring(0, rtf.indexOf(row)) + " ";//RTFEditorKit 需要这些内容才能解析
                }
                if (row.contains("\\cellx")) {
                    row = row.replace("\\cellx", "\\celx");
                }
                String[] cells = row.split("\\\\cell");//获得列
                StringBuilder sb = new StringBuilder();
                String rtfHeaderText = ReadFromRtfUtil.toPlainText(rtfHeader);

                for (int i = 0; i < cells.length; i++) {
                    String cell = cells[i];
                    if (cell.equalsIgnoreCase("\\row")) {
                        continue;
                    }
                    if (cell.contains("\\par{")) {
                        cell = cell.replace("\\par{", "{</br>");
                    }
                    if (cell.contains("\\par ")) {
                        cell = cell.replace("\\par ", "</br>");
                    }
                    cell = rtfHeader + cell + " }";

                    String cellText = ReadFromRtfUtil.toPlainText(cell);
                    String replace = cellText.replace(rtfHeaderText, "");
                    if (StringUtils.isBlank(replace)) {
                        sb.append("NA").append("\n");
                    } else {
                        sb.append(replace);
                    }
                }
                list.add(sb.toString());
            }
        }
        return list;
    }


    /**
     * 将rtf转换为纯文本内容
     *
     * @param cell rtf格式内容
     * @return 纯文本内容
     */
    public static String toPlainText(String cell) {
        String result = "";
        Reader input = new StringReader(cell);
        DefaultStyledDocument doc = new DefaultStyledDocument();
        RTFEditorKit kit = new RTFEditorKit();
        try {
            kit.read(input, doc, 0);
            result = doc.getText(0, doc.getLength());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        return result;
    }
}
