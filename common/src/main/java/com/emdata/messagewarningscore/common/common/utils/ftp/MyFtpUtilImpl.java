package com.emdata.messagewarningscore.common.common.utils.ftp;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.*;
import java.util.*;

/**
 * @version V1.0
 * @Title: FTPUtilImpl.java
 * @Package: com.emdata.common.util.ftp
 * @Description: FTP工具类实现
 */
@Slf4j
public class MyFtpUtilImpl implements FtpUtil {

    private String split = "/";

    private FTPClient client;

    private FtpLoginInfo ftpLoginInfo;

    public MyFtpUtilImpl(FtpLoginInfo ftpLoginInfo) {
        this.ftpLoginInfo = ftpLoginInfo;
        client = createClient(ftpLoginInfo);
    }

    /**
     * @param @param  bo
     * @param @return
     * @return FTPClient
     * @throws
     * @Title: createClient
     * @Description: 创建并连接FTP
     * @author: Minko liuming@em-data.com.cn
     */
    private FTPClient createClient(FtpLoginInfo bo) {
        FTPClient client = new FTPClient();
        int reply = -1;
        try {
            client.setConnectTimeout(bo.getConnectTimeout());
            client.setDataTimeout(bo.getDataTimeout());
            client.connect(bo.getHostName(), bo.getPort());
            client.login(bo.getUsername(), bo.getPassword());
            client.setFileType(FTP.BINARY_FILE_TYPE);
            client.setControlEncoding(bo.getRemoteEncoding());

            client.setPassiveNatWorkaround(true);

            // 使用被动模式
            client.enterLocalPassiveMode();

            reply = client.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                client.disconnect();
                log.error("ftp登录失败: {}", reply);
                return null;
            }
            log.info("ftp登录成功");
            return client;
        } catch (IOException e) {
            log.error("ftp登录异常", e);
            return null;
        }
    }

    /**
     * @param @param  operation
     * @param @return
     * @return boolean
     * @throws
     * @Title: reply
     * @Description: 通过FTP响应吗判断是否操作成功
     * @author: Minko liuming@em-data.com.cn
     */
    public boolean reply(String operation) {
        int replyCode = client.getReplyCode();
        FtpLog FtpLog = new FtpLog();
        FtpLog.setHost(ftpLoginInfo.getHostName());
        FtpLog.setOperation(operation);
        FtpLog.setLocalFile("");
        FtpLog.setRemoteFile("");
        FtpLog.setReplyCode(replyCode);
        FtpLog.setReplyCodeDesc(FtpConstant.REPLYCODE.get(replyCode));
        log.info("reply FtpLog:" + FtpLog);
        return FTPReply.isPositiveCompletion(replyCode);
    }

    public boolean reply(String operation, String localFile, String remoteFile) {
        int replyCode = client.getReplyCode();
        FtpLog FtpLog = new FtpLog();
        FtpLog.setHost(ftpLoginInfo.getHostName());
        FtpLog.setOperation(operation);
        FtpLog.setLocalFile(localFile);
        FtpLog.setRemoteFile(remoteFile);
        FtpLog.setReplyCode(replyCode);
        FtpLog.setReplyCodeDesc(FtpConstant.REPLYCODE.get(replyCode));
        log.info("reply FtpLog:" + FtpLog);
        return FTPReply.isPositiveCompletion(replyCode);
    }

    @Override
    public boolean isExists(String fileName) {
        client.enterLocalPassiveMode();
        List<String> list = listFile(ftpLoginInfo.getRemoteDir());
        if (list.contains(fileName)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean downLoad(String remotePath, String fileName) {
        String localFileName = ftpLoginInfo.getLocalDir() + File.separator + fileName;
        try {
            // 使用被动模式
            client.enterLocalPassiveMode();
            // 转移到FTP服务器目录
            client.changeWorkingDirectory(remotePath);
            File localFile = new File(ftpLoginInfo.getLocalDir() + File.separator + fileName);
            log.debug("本地文件: {}", localFile.getAbsolutePath());
            OutputStream is = new FileOutputStream(localFile);
            boolean success = client.retrieveFile(fileName, is);
            log.debug("下载结果:{}", success);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return reply("DOWNLOAD", localFileName, fileName);
    }

    @Override
    public boolean deleteFile(String fileName) {
        // 使用被动模式
        client.enterLocalPassiveMode();
        if (isExists(fileName)) {
            try {
                client.deleteFile(fileName);
                return reply("DELETE", "", fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public boolean deleteDir(String directory) {
        // 使用被动模式
        client.enterLocalPassiveMode();
        List<String> files = listFile(directory);
        try {
            for (String s : files) {
                deleteFile(s);
            }
            List<String> dirs = listDir(directory);
            for (int i = dirs.size() - 1; i >= 0; i--) {
                client.removeDirectory(new String(dirs.get(i).getBytes(ftpLoginInfo.getRemoteEncoding()), "ISO-8859-1"));
            }
            client.removeDirectory(new String(directory.getBytes(ftpLoginInfo.getRemoteEncoding()), "ISO-8859-1"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return reply("DELETE", "", directory);
    }

    @Override
    public boolean putFile(String fileName, String remoteFileName, boolean isDelete) {
        File file = new File(fileName);
        return putFile(file, remoteFileName, isDelete);
    }

    @Override
    public boolean putFile(File file, String remoteFileName, boolean isDelete) {
        // 使用被动模式
        client.enterLocalPassiveMode();

        String fileName = remoteFileName;
        String path = "";
        // 切换目录
        if (remoteFileName.lastIndexOf(split) != -1) {
            path = remoteFileName.substring(0, remoteFileName.lastIndexOf(split));
            fileName = remoteFileName.substring(remoteFileName.lastIndexOf(split) + 1);
            try {
                if (!client.changeWorkingDirectory(path)) {
                    String[] dirs = path.split(split);
                    String tempPath = path;
                    for (String dir : dirs) {
                        if (StringUtils.isBlank(dir)) {
                            continue;
                        }
                        tempPath += split + dir;
                        if (!client.changeWorkingDirectory(tempPath)) {
                            return false;
                        } else {
                            client.changeWorkingDirectory(tempPath);
                        }
                    }
                }
            } catch (IOException e) {
                log.error("");
            }
        }
        try (InputStream in = new FileInputStream(file)) {
            if (isDelete) {
                deleteFile(fileName);
            }
            client.setFileType(FTP.BINARY_FILE_TYPE);
            client.storeFile(fileName, in);
            in.close();
            return reply("UPLOAD", file.getAbsoluteFile().toString(), remoteFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean putFile(String remoteFileName, InputStream in, boolean isDelete) {
        // 使用被动模式
        client.enterLocalPassiveMode();

        String fileName = remoteFileName;
        String path = "";
        // 切换目录
        if (remoteFileName.lastIndexOf(split) != -1) {
            path = remoteFileName.substring(0, remoteFileName.lastIndexOf(split));
            fileName = remoteFileName.substring(remoteFileName.lastIndexOf(split) + 1);
            try {
                if (!client.changeWorkingDirectory(path)) {
                    String[] dirs = path.split(split);
                    String tempPath = path;
                    for (String dir : dirs) {
                        if (StringUtils.isBlank(dir)) {
                            continue;
                        }
                        tempPath += split + dir;
                        if (!client.changeWorkingDirectory(tempPath)) {
                            return false;
                        } else {
                            client.changeWorkingDirectory(tempPath);
                        }
                    }
                }
            } catch (IOException e) {
                log.error("");
            }
        }
        boolean result = false;
        if (isDelete) {
            deleteFile(fileName);
        }
        try {
            // 设置文件路径权限
            client.setFileType(FTP.BINARY_FILE_TYPE);
            client.storeFile(fileName, in);
            in.close();
            client.logout();
            return reply("UPLOAD", remoteFileName, remoteFileName);
        } catch (IOException e) {
            log.info("FtpUtil uploadFile error:" + e.getMessage());
        } finally {
            try {
                client.disconnect();
            } catch (IOException e) {
                log.info("FtpUtil uploadFile IOException error:" + e.getMessage());
            }
        }
        return result;
    }

    @Override
    public boolean putDir(String fileName, String remoteDir) {
        File file = new File(fileName);
        return putDir(file, remoteDir);
    }

    @Override
    public boolean putDir(File file, String remoteDir) {
        // 使用被动模式
        client.enterLocalPassiveMode();

        List<File> list = listFile(file);
        for (File f : list) {
            String name = f.getAbsolutePath();
            name = name.substring(name.indexOf(file.getName())).replaceAll("\\\\", split);
            putFile(f, remoteDir + split + name, true);
        }
        return true;
    }

    @Override
    public boolean mkDir(String directory) {
        // 使用被动模式
        client.enterLocalPassiveMode();

        directory = directory.replaceAll("//", split);
        if (directory.startsWith(split)) {
            directory = directory.substring(1);
        }
        if (directory.endsWith(split)) {
            directory = directory.substring(0, directory.length() - 1);
        }
        try {
            String[] str = (new String(directory.getBytes(ftpLoginInfo.getRemoteEncoding()), "ISO-8859-1")).split(split);
            String t = "";
            String parnet = "";
            for (int i = 0; i < str.length; i++) {
                t += (split + str[i]);
                if (!isExists(t.substring(1))) {
                    client.makeDirectory(str[i]);
                }
                client.changeWorkingDirectory(str[i]);
                parnet += "../";
            }
            if (str.length >= 1) {
                client.changeWorkingDirectory(parnet);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<String> listFile(String directory) {
        // 使用被动模式
        client.enterLocalPassiveMode();

        List<String> list = new ArrayList<String>();
        try {
            FTPFile[] files = client.listFiles(directory);
            for (int i = 0; i < files.length; i++) {
                String t = (directory + split + files[i].getName()).replaceAll("//", split);
                if (files[i].isFile()) {
                    list.add(t);
                } else if (files[i].isDirectory()) {
                    list.addAll(listFile((t + split).replaceAll("//", split)));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public LinkedList<String> listDir(String directory) {
        // 使用被动模式
        client.enterLocalPassiveMode();

        LinkedList<String> list = new LinkedList<String>();
        try {
            FTPFile[] files = client.listFiles(directory);
            for (int i = 0; i < files.length; i++) {
                String t = (directory + split + files[i].getName()).replaceAll("//", split);
                if (files[i].isDirectory()) {
                    list.add(t);
                    list.addAll(listDir(t + split));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Map<String, FileAttr> listFileAttr(String directory) {
        // 使用被动模式
        client.enterLocalPassiveMode();

        Map<String, FileAttr> map = new HashMap<String, FileAttr>();
        try {
            FTPFile[] files = client.listFiles(directory);
            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile()) {
                    FTPFile file = files[i];
                    String fileName = directory + file.getName();
                    FileAttr attr = new FileAttr();
                    attr.setFileName(fileName);
                    attr.setModifyTime(file.getTimestamp().getTime());
                    attr.setSize(file.getSize());
                    map.put(fileName, attr);
                } else if (files[i].isDirectory()) {
                    map.putAll(listFileAttr(directory + files[i].getName() + split));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    @Override
    public boolean changeWorkDir(String directory) {
        // 使用被动模式
        client.enterLocalPassiveMode();

        try {
            client.cwd(directory);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public String getWorkDir() {
        try {
            return client.printWorkingDirectory();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public FTPClient client() {
        return client;
    }

    @Override
    public void destory() {
        if (client != null) {
            try {
                client.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 罗列指定路径下的全部文件
     *
     * @param path 需要处理的文件
     * @return 返回文件列表
     */
    private List<File> listFile(File path) {
        List<File> list = new ArrayList<>();
        File[] files = path.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    list.addAll(listFile(file));
                } else {
                    list.add(file);
                }
            }
        }
        return list;
    }


    public static void main(String[] args) throws FileNotFoundException {
        // 将文件传入FTP
//        FileInputStream inputStream = new FileInputStream(new File("D:\\data\\ftp\\1.png"));

        FtpLoginInfo ftpBo = new FtpLoginInfo();
        ftpBo.setHostName("192.168.90.10");
        ftpBo.setUsername("ykftp");
        ftpBo.setPassword("em2019");
        ftpBo.setPort(21);
        ftpBo.setConnectTimeout(1*1000);
        ftpBo.setRemoteDir("Camera_Weather/Pudong_08");
        ftpBo.setLocalDir("D:\\data\\ftp");

        FtpUtil ftpUtil = new MyFtpUtilImpl(ftpBo);
        ftpUtil.listDir("Camera_Weather/Pudong_08").forEach(System.out::println);
    }
}