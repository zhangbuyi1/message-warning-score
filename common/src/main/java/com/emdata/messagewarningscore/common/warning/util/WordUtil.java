package com.emdata.messagewarningscore.common.warning.util;/**
 * Created by zhangshaohu on 2020/12/7.
 */

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.hwpf.usermodel.*;
import org.apache.poi.ooxml.POIXMLDocument;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.rtf.RTFEditorKit;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author: zhangshaohu
 * @date: 2020/12/7
 * @description:
 */
public class WordUtil {
    class ChildWord {
        private String string;
        private List<List<String>> table;

    }

    public static List<List<String>> readTable() {


        return null;
    }

    public static void main(String[] args) throws IOException {
        //载入文档
        FileInputStream in = new FileInputStream("D:\\数据\\PWSMR9E07.161.doc");
        POIFSFileSystem pfs = new POIFSFileSystem(in);
        HWPFDocument hwpf = new HWPFDocument(pfs);
        //得到文档的读取范围
        Range range = hwpf.getRange();
        StringBuilder stringBuilder = new StringBuilder();

        StringBuilder text = hwpf.getText();
        System.out.println(text);
        TableIterator it = new TableIterator(range);
        // 得到第一个表格
        if (it.hasNext()) {
            List<List<String>> reAll = new ArrayList<>();
            Table next = it.next();
            int i2 = range.numParagraphs();
            for (int i = 0; i < i2; i++) {
                Paragraph paragraph = range.getParagraph(i);
                if (range.getEndOffset() < next.getStartOffset()) {
                    stringBuilder.append(paragraph.text());

                }
            }

            // 循环所有行
            for (int j = 0; j < next.numRows(); j++) {
                List<String> rowRe = new ArrayList<>();
                TableRow row = next.getRow(j);
                for (int i = 0; i < row.numCells(); i++) {
                    // 得到当前行的第i个元素
                    TableCell cell = row.getCell(i);
                    StringBuilder rowDate = new StringBuilder();
                    for (int k = 0; k < cell.numParagraphs(); k++) {
                        // 得到行里面的数据
                        Paragraph para = cell.getParagraph(k);
                        String replace = para.text().replace("\r", "").replace("\n", "").replace("\u0007", "");
                        // 如果当前为第一个元素 并且当前元素
                        if (i == 0) {
                            if (StringUtils.isEmpty(replace)) {
                                TableCell cell1 = next.getRow(j - 1).getCell(0);
                                for (int i1 = 0; i1 < cell1.numParagraphs(); i1++) {
                                    rowDate.append(cell1.getParagraph(i1).text().replace("\r", "").replace("\n", "").replace("\u0007", ""));
                                }
                                break;
                            } else {
                                rowDate.append(replace);
                            }

                        } else {
                            rowDate.append(replace);
                        }
                    }
                    rowRe.add(rowDate.toString());

                }
                reAll.add(rowRe);

            }
            System.out.println(reAll);


        }


    }
}