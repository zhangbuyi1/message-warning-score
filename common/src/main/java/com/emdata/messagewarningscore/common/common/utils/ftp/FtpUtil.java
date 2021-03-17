package com.emdata.messagewarningscore.common.common.utils.ftp;

import java.io.File;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.net.ftp.FTPClient;

/**
 * @Title: FtpUtil.java
 * @Package: com.emdata.common.util.ftp
 * @Description: FTP工具类
 * @author: Minko liuming@em-data.com.cn
 * @date 2018年7月20日 下午2:03:56
 * @version V1.0
 */
public interface FtpUtil {

    /**
     * 判断远程文件是否存在
     * @param fileName
     * @return
     */
    boolean isExists(String fileName);

    /**
     * 下载远程文件
     * @param remotePath
     * @param fileName
     * @return
     */
    boolean downLoad(String remotePath, String fileName);

    /**
     * 删除远程文件
     * @param fileName
     * @return
     */
    boolean deleteFile(String fileName);

    /**
     * 删除远程目录
     * @param directory
     * @return
     */
    boolean deleteDir(String directory);

    /**
     * 上传本地文件到远程目录
     * @param fileName
     * @param remoteFileName
     * @param isDelete
     * @return
     */
    boolean putFile(String fileName, String remoteFileName, boolean isDelete);

    /**
     * 上传本地文件到远程目录
     * @param fileName
     * @param inputStream
     * @param isDelete
     * @return
     */
    boolean putFile(String fileName, InputStream inputStream, boolean isDelete);

    /**
     * 上传本地文件到远程目录
     * @param file
     * @param remoteFileName
     * @param isDelete
     * @return
     */
    public boolean putFile(File file, String remoteFileName, boolean isDelete);

    /**
     * 上传本地目录到远程
     * @param fileName
     * @param remoteDir
     * @return
     */
    boolean putDir(String fileName, String remoteDir);

    /**
     * 上传本地目录到远程
     * @param file
     * @param remoteDir
     * @return
     */
    public boolean putDir(File file, String remoteDir);

    /**
     * 创建文件夹
     * @param directory
     * @return
     */
    boolean mkDir(String directory);

    /**
     * 获取远程文件列表
     * @param directory
     * @return
     */
    List<String> listFile(String directory);

    /**
     * 获取远程文件夹的目录结构
     * @param directory
     * @return
     */
    LinkedList<String> listDir(String directory);

    /**
     * 获取远程文件属性以Map形式返回
     * @param directory
     * @return
     */
    Map<String,FileAttr> listFileAttr(String directory);

    /**
     * 改变FTP连接的工作目录
     * @param directory
     * @return
     */
    boolean changeWorkDir(String directory);

    /**
     * 获取当前连接的工作目录
     * @return
     */
    String getWorkDir();

    /**
     * 返回FTPCliend对象(已经打开连接)
     * @return
     */
    FTPClient client();

    /**
     * 释放所有的资源
     */
    void destory();

}