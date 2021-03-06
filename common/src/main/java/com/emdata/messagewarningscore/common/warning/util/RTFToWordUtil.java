package com.emdata.messagewarningscore.common.warning.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;

import java.io.*;
import java.util.Map;


@Slf4j
public class RTFToWordUtil {


    /**
     * 字符串转换为rtf编码
     *
     * @param content
     * @return
     */
    public String strToRtf(String content) {

        StringBuffer sb = new StringBuffer("");
        try {
            char[] digital = "0123456789ABCDEF".toCharArray();
            byte[] bs = null;
            bs = content.getBytes("GB2312");
            int bit;
            for (int i = 0; i < bs.length; i++) {
                bit = (bs[i] & 0x0f0) >> 4;
                sb.append("\\'");
                sb.append(digital[bit]);
                bit = bs[i] & 0x0f;
                sb.append(digital[bit]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * 替换文档的可变部分
     *
     * @param content        文档内容
     * @param key            标识
     * @param replacecontent 替换内容
     * @return
     */
    public String replaceRTF(String content, String key, String replacecontent) {
        String rc = strToRtf(replacecontent);

        String target = content.replace(key, rc);

        return target;
    }

    /**
     * 半角转为全角
     */
    public String ToSBC(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 32) {
                c[i] = (char) 12288;
                continue;
            }
            if (c[i] < 127) {
                c[i] = (char) (c[i] + 65248);
            }
        }
        return new String(c);
    }

    /**
     * 替换模板
     *
     * @param map            标识与替换内容
     * @param sourceFilePath 模板路径
     * @param targetFilePath 生成Word文档路径
     */
    public void rgModel(Map<String, String> map, String sourceFilePath, String targetFilePath) {  
          
        /* 字节形式读取模板文件内容,将结果转为字符串 */
        String sourcecontent = "";
        InputStream ins = null;
        try {
            ins = new FileInputStream(sourceFilePath);
            byte[] b = new byte[1024];
            if (ins == null) {
                log.info(RTFToWordUtil.class.getName() + ":源模板文件不存在");
            }
            int bytesRead = 0;
            while (true) {
                bytesRead = ins.read(b, 0, 1024); // return final read bytes  
                // counts  
                if (bytesRead == -1) {// end of InputStream  
                    log.info(RTFToWordUtil.class.getName() + ":读取模板文件结束");
                    break;
                }
                // convert to string using bytes  
                sourcecontent += new String(b, 0, bytesRead);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }  
  
        /* 修改变化部分 */
        String targetcontent = "";
        int i = 0;
        for (String key : map.keySet()) {
            String value = map.get(key);
            if (i == 0) {
                targetcontent = replaceRTF(sourcecontent, key, value);
            } else {
                targetcontent = replaceRTF(targetcontent, key, value);
            }
            i++;
        }  
        /* 结果输出保存到文件 */
        try {
            FileWriter fw = new FileWriter(targetFilePath,
                    true);
            PrintWriter out = new PrintWriter(fw);

            if (targetcontent.equals("") || targetcontent == "") {
                out.println(sourcecontent);
            } else {
                out.println(targetcontent);
            }
            out.close();
            fw.close();
            log.info(RTFToWordUtil.class.getName() + ":生成文件 " + targetFilePath + " 成功");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public static void main(String[] args) {
//        RTFToWordUtil oRTF = new RTFToWordUtil();
//        Map<String, String> map = new HashMap<String, String>();
//        oRTF.rgModel(map, "D:\\数据\\PWSMR9E07.161.doc", "D:\\数据\\test1.doc");
//    }


}  