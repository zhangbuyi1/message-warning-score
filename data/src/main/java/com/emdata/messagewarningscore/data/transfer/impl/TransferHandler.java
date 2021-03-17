package com.emdata.messagewarningscore.data.transfer.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.emdata.messagewarningscore.common.warning.util.MatchUtil;
import com.emdata.messagewarningscore.data.transfer.RadarEnum;
import com.emdata.messagewarningscore.data.transfer.Transfer;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author: zhangshaohu
 * @date: 2021/1/25
 * @description:
 */
@Service
public class TransferHandler implements Transfer {
    private static Map<String, String> pattern = new HashMap<String, String>() {{
        //SHA210120012505.RAWS90N
        //SHA210120012808.RAWS90P
        //SHA210125102504.RAWSBVG
        put("ZSSS", "(SHA\\d{12}.)");
        // 20200904.000449.00.37.592
        put("ZSAM", "\\d{8}.\\d{6}.\\d{2}.\\d{2}.([^!]+)");
        //QZSCNBPT200707000150.019
        put("ZSCN", "QZSCNBPT\\d{12}.([^!]+)");
        //QZSFZBVT200908115452.009
        put("ZSFZ", "QZSFZBVT\\d{12}.([^!]+)");
        //ZSJN QZSJNBVT200815175454.010
        put("ZSJN", "QZSJNBVT\\d{12}.([^!]+)");
        //ZSNB Z9574_20200826050001Z_CR_00_37
        put("ZSNB", "Z\\d{4}_\\d{14}([^!]+)");
        //ZSNJ QZSNJBPT20200609200010.015
        put("ZSNJ", "([^!]{8}|[!]{8})\\d{14}.([^!]+)");
        //ZSOF QZSOFBPT200410113516.013
        put("ZSOF", "([^!]{8}|[!]{8})\\d{12}.([^!]+)");
        //ZSQD 2020091617010.11V
        put("ZSQD", "\\d{13}.11V");
        //ZSWZ Z9577_20201223070918Z_CR_00_37
        put("ZSWZ", "Z\\d{4}_\\d{14}Z([^!]+)");
        //ZYTX 2020080104230.05V 2020080104230.05V
        put("ZYTX", "\\d{13}.([^!]+)");
    }};

    /**
     * @param path    从path
     * @param endPath 拷贝到endPath
     * @return
     */
    @Override
    public List<String> transfer(String path, String endPath) {
        /**
         * 获得文件夹下所有文件
         */
        List<File> fileList = recursionFind(path, new ArrayList<>());
        /**
         *    过滤数据
         *    得到文件夹下有用数据
         */
        List<File> filterFiles = filterFiles(fileList, pattern);


        /**
         * 修改文件名并且将数据copy到指定位置
         */
        return reNameAndMv(filterFiles, endPath);

    }

    @Override
    public List<String> transfer(String path, String endPath, int field, int num) {

        /**
         * 获得文件夹下所有文件
         */
        List<File> fileList = recursionFind(path, new ArrayList<>());
        /**
         *    过滤数据
         *    得到文件夹下有用数据
         */
        List<File> filterFiles = filterFiles(fileList, pattern);
        /**
         * 过滤时间
         */
        filterTime(filterFiles, field, num);

        /**
         * 修改文件名并且将数据copy到指定位置
         */
        return reNameAndMv(filterFiles, endPath);
    }

    @Override
    public List<String> transfer(List<String> files, String endPath) {
        List<File> fileList = files.stream().map(s -> {
            return new File(s);
        }).collect(Collectors.toList());
        /**
         *    过滤数据
         *    得到文件夹下有用数据
         */
        List<File> filterFiles = filterFiles(fileList, pattern);
        /**
         * 修改文件名并且将数据copy到指定位置
         */
        return reNameAndMv(filterFiles, endPath);
    }

    @Override
    public String reRadarName(String filePath) {
        File file = new File(filePath);
        if (filterAnFile(pattern, file)) {
            return reName(file);
        }
        return null;
    }

    @Override
    public List<String> transfer(String path, String endPath, Map<String, String> mapPattern) {
        /**
         * 获得文件夹下所有文件
         */
        List<File> fileList = recursionFind(path, new ArrayList<>());
        /**
         *    过滤数据
         *    得到文件夹下有用数据
         */
        List<File> filterFiles = filterFiles(fileList, mapPattern);
        /**
         * 修改文件名并且将数据copy到指定位置
         */
        return reNameAndMv(filterFiles, endPath);
    }

    /**
     * 重命名 并且移动到指定目录
     *
     * @param filterFiles 所有文件
     * @param output      输出路劲
     */
    private static List<String> reNameAndMv(List<File> filterFiles, String output) {
        return filterFiles.stream().map(s -> {
            String reName = reName(s);
            String endPath = output + File.separatorChar + reName;
            File file = new File(endPath);
            if (!file.exists()) {
                mv(s, endPath);
            }
            return endPath;
        }).filter(s -> {
            return s != null;
        }).collect(Collectors.toList());
    }

    /**
     * 判断当前文件是否有效
     *
     * @param file
     * @return
     */
    private static boolean isVaild(File file) {
        try {
            FileInputStream inputStream = new FileInputStream(file);
            int start = inputStream.available();
            TimeUnit.SECONDS.sleep(1);
            int end = inputStream.available();
            if (end == start) {
                return true;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 重新定义名称
     *
     * @param file 需要重命名的文件
     * @return 返回一个名称
     */
    private static String reName(File file) {
        String name = file.getName();
        Optional<Map.Entry<String, String>> first = pattern.entrySet().stream().filter(s -> {
            if (!StringUtils.isEmpty(MatchUtil.get(file.getAbsolutePath(), s.getKey() + "|" + s.getKey().toLowerCase()))) {
                return true;
            }
            return false;
        }).findFirst();
        if (first.isPresent()) {
            String airportcode = first.get().getKey();
            RadarEnum radarEnum = RadarEnum.get(airportcode);
            Date apply = radarEnum.getDataFun().apply(name, radarEnum);
            return airportcode + "_" + DateUtil.format(apply, "yyyyMMddHHmmss");
        }
        return null;
    }

    /**
     * @param file    需要移动的文件
     * @param outPath 移动输出路劲
     * @return
     */
    private static void mv(File file, String outPath) {
        FileInputStream inputStream = null;
        FileOutputStream outputStream = null;
        try {
            inputStream = new FileInputStream(file);
            outputStream = new FileOutputStream(outPath);
            FileChannel outputChannel = outputStream.getChannel();
            FileChannel inputChannel = inputStream.getChannel();
            try {
                // mv
                outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
                // 关流
                outputStream.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
                // 关流
                try {
                    outputStream.close();
                    inputStream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            }
        } catch (FileNotFoundException e) {
            Optional.ofNullable(outputStream).ifPresent(o -> {
                try {
                    o.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            });
            Optional.ofNullable(inputStream).ifPresent(i -> {
                try {
                    i.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            });

            e.printStackTrace();
        } finally {
            Optional.ofNullable(outputStream).ifPresent(o -> {
                try {
                    o.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            });
            Optional.ofNullable(inputStream).ifPresent(i -> {
                try {
                    i.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            });
        }
    }

    public static void main(String[] args) {
        String s = MatchUtil.get("SHA210125001308.RAWSBM2", "(SHA\\d{12}.)");
        System.out.println(s);
    }

    /**
     * 递归寻找当前文件夹下所有文件
     * 并且根据正则过滤文件
     *
     * @param path  查找路径
     * @param files 输出文件
     * @return 返回查找到的所有文件
     */
    private static List<File> recursionFind(String path, List<File> files) {
        File file = new File(path);
        if (file.isDirectory()) {
            Arrays.stream(file.listFiles()).peek(s -> {
                recursionFind(path + File.separatorChar + s.getName(), files);
            }).collect(Collectors.toList());
        } else {
            files.add(file);
        }
        return files;
    }

    /**
     * 过滤时间
     *
     * @param files
     * @param field
     * @param num
     * @return
     */
    private static List<File> filterTime(List<File> files, Integer field, Integer num) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(0);
        instance.set(field, num);
        return files.stream().filter(s -> {
            String s1 = reName(s);
            String fileTime = MatchUtil.get(s1, "yyyyMMddHHmmss");
            DateTime fileDate = DateUtil.parse(fileTime, "yyyyMMddHHmmss");
            if (System.currentTimeMillis() - fileDate.getTime() > instance.getTimeInMillis()) {
                return false;
            }
            return true;
        }).collect(Collectors.toList());

    }

    /**
     * @param files   需要过滤的文件
     * @param pattern 需要过滤文件所需要的规则
     * @return 返回过滤后的文件
     */
    private static List<File> filterFiles(List<File> files, Map<String, String> pattern) {
        return files.stream().filter(f -> {
            return filterAnFile(pattern, f);

        }).collect(Collectors.toList());

    }

    private static boolean filterAnFile(Map<String, String> pattern, File f) {
        // 过滤有用数据
        String fileName = f.toString();
        Optional<Map.Entry<String, String>> first = pattern.entrySet().stream().filter(s -> {
            if (!StringUtils.isEmpty(MatchUtil.get(fileName, s.getKey().toLowerCase() + "|" + s.getKey()))) {
                String patt = s.getValue();
                if (!StringUtils.isEmpty(MatchUtil.get(f.getName(), patt))) {
                    return true;
                }
            }
            return false;
        }).findFirst();
        return first.isPresent();
    }

}