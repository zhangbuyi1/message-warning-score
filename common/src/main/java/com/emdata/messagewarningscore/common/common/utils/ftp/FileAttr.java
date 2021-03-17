package com.emdata.messagewarningscore.common.common.utils.ftp;

import lombok.Data;

import java.util.Date;

/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author pupengfei
 * @create 2020/1/20
 * @since 1.0.0
 */
@Data
public class FileAttr {

    private String fileName;

    private Date modifyTime;

    private Long size;
}