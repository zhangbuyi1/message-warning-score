package com.emdata.messagewarningscore.data.transfer;/**
 * Created by zhangshaohu on 2021/1/25.
 */

import java.util.List;
import java.util.Map;

/**
 * @author: zhangshaohu
 * @date: 2021/1/25
 * @description: 转移文件适配器 以后编写接口 以监控文件夹的形式  但是不同机场 文件夹并未署名 所以需要当前适配器 将文件或拉取 或cp 到当前文件来并且给文件署名
 */
public interface Transfer {
    /**
     * 递归寻找当前文件夹下的文件  并且修改文件名 统一放入指定文件内
     *
     * @param path    从path
     * @param endPath 拷贝到endPath
     * @return
     */
    List<String> transfer(String path, String endPath);

    /**
     * @param path
     * @param endPath
     * @param field   时间单位
     * @param num     时间长度
     * @return
     */
    List<String> transfer(String path, String endPath, int field, int num);


    /**
     * @param files   需要移动的文件
     * @param endPath 移动地址
     * @return
     */
    List<String> transfer(List<String> files, String endPath);

    String reRadarName(String filePath);

    /**
     * @param path       从path
     * @param endPath    拷贝到endPath
     * @param mapPattern 查找指定文件
     * @return
     */
    List<String> transfer(String path, String endPath, Map<String, String> mapPattern);
}