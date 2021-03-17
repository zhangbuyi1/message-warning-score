package com.emdata.messagewarningscore.common.parse.util;


import com.emdata.messagewarningscore.common.exception.MetarParseException;
import com.emdata.messagewarningscore.common.parse.element.ElementContainer;

import java.text.DecimalFormat;

/**
 * TODO
 *
 * @author pupengfei
 * @version 1.0
 * @date 2020/9/11 14:17
 */
public class VisibilityUtil {

    private static final String CAVOK = "CAVOK";

    private static final String VIS_REGEX = "^\\d{4}$";

    /**
     * 解析报文能见度
     *
     * @param eles  报文要素数组
     * @param index 能见度开始索引
     * @return 能见度，CAVOK=9999
     */
    public static ElementContainer<Double> parse(String[] eles, int index) throws MetarParseException {
        return parse(eles, index, true);
    }

    /**
     * 解析报文能见度
     *
     * @param eles  报文要素数组
     * @param index 能见度开始索引
     * @param check 是否进行格式检查，true：检查不通过时，抛出异常，false：检查不通过时返回null；
     * @return 能见度，CAVOK=9999
     */
    public static ElementContainer<Double> parse(String[] eles, int index, boolean check) throws MetarParseException {
        if (index >= eles.length) {
            if (!check) {
                return new ElementContainer<>(null, index);
            }
            throw new MetarParseException("请检查METAR报文（要素不足）");
        }

        String v = eles[index];

        Double vis = null;
        if (CAVOK.equals(v)) {
            vis = 9999.0;
        } else {
            if (!v.matches(VIS_REGEX)) {
                if (check) {
                    throw new MetarParseException("能见度格式错误: " + v);
                } else {
                    return new ElementContainer<>(null, index);
                }
            }
            vis = Double.parseDouble(v);
            index++;
        }

        return new ElementContainer<>(vis, index);
    }

    /**
     * 格式化能见度
     *
     * @param vis
     * @return
     */
    public static String combineVis(Double vis) {
        DecimalFormat format = new DecimalFormat("0000");
        String visStr = format.format(vis);
        return visStr;
    }
}
