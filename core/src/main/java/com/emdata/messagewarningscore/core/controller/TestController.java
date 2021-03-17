package com.emdata.messagewarningscore.core.controller;/**
 * Created by zhangshaohu on 2021/1/26.
 */

import com.emdata.messagewarningscore.common.common.utils.PicUtil;
import com.emdata.messagewarningscore.data.schedule.PullFTPTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: zhangshaohu
 * @date: 2021/1/26
 * @description:
 */
@RestController
public class TestController {
    @Autowired
    private PullFTPTask pullFTPTask;

    @GetMapping("/pull/radar")
    public void pullRadar() {
        pullFTPTask.pullRadarData();
    }

    @GetMapping("/pull/all")
    public void pullAll(@RequestParam(name = "ftpPath") String ftpPath, @RequestParam(name = "airportCode") String airportCode) {
        pullFTPTask.pullZsam(ftpPath, airportCode);
    }

    @GetMapping("/pull/pic")
    public void picToColoering(@RequestParam(name = "path") String path, @RequestParam(name = "endPath") String endPath) {
        // 得到所有图片文件
        List<File> fileList = recursionFind(path, new ArrayList<>());
        System.out.println("得到所有图片");
        fileList.stream().peek(s -> {
            String path1 = s.getPath();
            BufferedImage read = PicUtil.read(path1);
            if (read != null) {
                BufferedImage coloring = PicUtil.coloring(read, true);
                File file = new File(path);
                File file1 = new File(endPath);
                String replace = path1.replace(file.getPath(), file1.getPath());
                System.out.println("处理完毕" + replace);
                PicUtil.writeImage(coloring, replace);
            }
        }).collect(Collectors.toList());

    }

    /**
     * 递归寻找当前文件夹下所有文件
     * 并且根据正则过滤文件
     *
     * @param path  查找路径
     * @param files 输出文件
     * @return 返回查找到的所有文件
     */
    private List<File> recursionFind(String path, List<File> files) {
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
}