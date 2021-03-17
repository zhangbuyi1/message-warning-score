package com.emdata.messagewarningscore.data.parse.interfase;


import com.emdata.messagewarningscore.common.dao.entity.AlertContentResolveDO;

import java.io.InputStream;
import java.util.List;

public interface ParseMdrsInterface {

    /**
     * 解析mdrs文本
     *
     * @param in 输入流
     * @return 解析后的do实体类集合
     */
    List<AlertContentResolveDO> parseMdrsText(InputStream in);
}
