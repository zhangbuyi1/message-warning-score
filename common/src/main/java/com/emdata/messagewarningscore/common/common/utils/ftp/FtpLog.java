package com.emdata.messagewarningscore.common.common.utils.ftp;

import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 〈一句话功能简述〉<br>
 * 〈ftp日志类〉
 *
 * @author pupengfei
 * @create 2020/1/20
 * @since 1.0.0
 */
@Data
public class FtpLog {

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private String host;

    private String operation;

    private int replyCode;

    private String localFile;

    private String remoteFile;

    private String replyCodeDesc;

    private String createTime = SIMPLE_DATE_FORMAT.format(new Date());

}