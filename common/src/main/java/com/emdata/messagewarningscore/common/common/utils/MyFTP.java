package com.emdata.messagewarningscore.common.common.utils;/**
 * Created by zhangshaohu on 2020/12/24.
 */

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author: zhangshaohu
 * @date: 2020/12/24
 * @description:
 */
public class MyFTP extends FTPClient {
    public static void main1(String[] args) throws IOException {
        MyFTP myFTP = new MyFTP("192.168.90.10", 21, "ykftp", "em2019", "/cac", "/data/metar");
        List<String> pull = myFTP.pull(null);
        System.out.println(pull);

    }

    /**
     * FTPIp
     */
    private String ip;
    /**
     * 端口
     */
    private Integer port;
    /**
     * 用戶名
     */
    private String userneme;
    /**
     * 密码
     */
    private String password;
    /**
     * ftp拉取拉取路径前缀
     */
    private String ftpPath;

    private String ftpUrl;
    /**
     * 需要拉取到本地文件夹前缀
     */
    private String localPathPre;

    public MyFTP(String ip, Integer ftpPort, String userneme, String password, String ftpPath, String localPathPre) {
        this.ip = ip;
        this.ftpPath = ftpPath;
        this.port = ftpPort;
        this.userneme = userneme;
        this.password = password;
        this.localPathPre = localPathPre;
        this.ftpUrl = "ftp://" + userneme + ":" + password + "@" + ip + ftpPath + "/";
        // 初始化ftp连接
        initFtpClient();
    }

    /**
     * 初始化FTP连接
     */
    public void initFtpClient() {
        setControlEncoding("utf-8");
        try {
            System.out.println("connecting...ftp服务器:" + ip + ":" + port);
            //连接ftp服务器
            connect(ip, port);
            //登录ftp服务器
            login(userneme, password);
            setFileType(FTP.BINARY_FILE_TYPE);
            //是否成功登录服务器
            int replyCode = getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                System.out.println("connect failed...ftp服务器:" + ip + ":" + port);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            System.out.println("connect failed...ftp服务器:" + ip + ":" + port);
            if (isConnected()) {
                logout();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("connect failed...ftp服务器:" + ip + ":" + port);
            if (isConnected()) {
                logout();
            }
        }
    }

    /**
     * 拉取Ftp文件
     *
     * @param groupPolicy 拉取策略 如根據文件類型以及時間存儲
     *                    s -> {
     *                    String one = s.getName().substring(0, s.getName().indexOf("."));
     *                    String t = s.getName().substring(s.getName().indexOf(".") + 1, s.getName().indexOf(".f"));
     *                    return one + File.separatorChar + t;
     *                    }
     * @return
     */
    public List<String> pull(Function<FTPFile, String> groupPolicy) {
        List<String> pull = pull(groupPolicy, true, ftpPath, "", new ArrayList<>());
        return pull;
    }

    /**
     * 返回本地目录所有文件
     *
     * @return
     */
    private List<String> selectLocalFile() {
        File file = new File(this.localPathPre);
        List<String> select = select(file, new ArrayList<>());
        return select;
    }

    /**
     * 查看本地目录有哪些文件
     *
     * @return
     */
    private List<String> select(File file, List<String> all) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File file1 : files) {
                select(file1, all);
            }
        } else {
            all.add(file.getPath());
        }
        return all;
    }


    /**
     * @param groupPolicy 拉取策略
     * @param pullDir     是否拉取文件夹
     * @return
     */
    private List<String> pull(Function<FTPFile, String> groupPolicy, boolean pullDir, String ftpPath, String supperPath, ArrayList<String> allPull) {
        try {
            // 拉取只需要告诉ftp服务器
            enterLocalPassiveMode();
            // 得到所有的文件夹
            FTPFile[] ftpFiles = listFiles(ftpPath + "/" + supperPath);
            // 得到所有的文件
            List<FTPFile> files = Arrays.stream(ftpFiles).filter(s -> {
                return s.isFile();
            }).collect(Collectors.toList());
            if (groupPolicy != null) {
                // 根据拉取策略分组
                Map<String, List<FTPFile>> collect = files.stream().collect(Collectors.groupingBy(groupPolicy));
                // 拉取
                collect.keySet().stream().map(s -> {
                    // 同一个key下的文件夹
                    List<String> pull = pull(collect.get(s), ftpPath, supperPath, s);
                    allPull.addAll(pull);
                    return s;
                }).collect(Collectors.toList());
            } else {
                allPull.addAll(pull(files, ftpPath, supperPath, ""));
            }
            if (pullDir) {
                // 得到所有的dir文件
                List<FTPFile> dir = Arrays.stream(ftpFiles).filter(s -> {
                    return s.isDirectory();
                }).collect(Collectors.toList());
                // 如果没有文件
                if (null == dir || dir.size() == 0) {
                    return allPull;
                } else {
                    for (FTPFile ftpFile : dir) {
                        pull(groupPolicy, pullDir,
                                ftpPath, ftpFile.getName()
                                , allPull);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return allPull;
    }


    /**
     * 拉取文件
     *
     * @param ftpFiles
     * @param supperPath
     * @return
     */
    private List<String> pull(List<FTPFile> ftpFiles, String ftpPath, String supperPath, String key) {

        // 改变工作路劲
        try {
            changeWorkingDirectory(ftpPath);
            return ftpFiles.stream().map(s -> {
                try {
                    File localFile = null;
                    if (key != null) {
                        localFile = new File(localPathPre + File.separatorChar + supperPath + File.separatorChar + key + File.separatorChar + s.getName());
                    } else {
                        localFile = new File(localPathPre + File.separatorChar + supperPath + File.separatorChar + s.getName());
                    }
                    if (localFile.exists()) {
                        return null;
                    }
                    File parentFile = localFile.getParentFile();
                    if (!parentFile.exists()) {
                        // 这里不使用mkdirs 决定一点一点创建
                        // parentFile.mkdirs();
                        mkdirFile(parentFile);
                    }
                    FileOutputStream out = new FileOutputStream(localFile);
                    // 将远程数据写到FileOutputStream里面
                    retrieveFile(s.getName(), out);
                    out.close();
                    return localFile.getPath();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }).filter(s -> {
                return s != null;
            }).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }


    public static void mkdirFile(File file) {
        if (!file.exists()) {
            File parentFile = file.getParentFile();
            mkdirFile(parentFile);
            file.mkdir();
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            return;
        }
    }

    @Override
    public boolean logout() {
        try {
            if (isConnected()) {
                return super.logout();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }


}