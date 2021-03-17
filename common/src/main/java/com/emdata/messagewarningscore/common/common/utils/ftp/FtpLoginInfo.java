package com.emdata.messagewarningscore.common.common.utils.ftp;

import lombok.Data;

/**
 * 〈一句话功能简述〉<br>
 * 〈ftp登录信息〉
 *
 * @author pupengfei
 * @create 2020/1/20
 * @since 1.0.0
 */
@Data
public class FtpLoginInfo {

    private String hostName;

    private int port;

    private String username;

    private String password;

    private String remoteDir;

    private String localDir;

    private String remoteEncoding = "utf-8";

    private boolean passiveMode = true;

    /**
     * 连接超时时间(ms)
     */
    private int connectTimeout = 10 * 1000;

    /**
     * 数据传输时，陷入阻塞的超时时间(ms)
     */
    private int dataTimeout = 60 * 1000;
}