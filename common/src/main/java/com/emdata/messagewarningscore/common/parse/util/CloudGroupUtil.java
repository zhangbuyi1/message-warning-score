package com.emdata.messagewarningscore.common.parse.util;


import com.emdata.messagewarningscore.common.exception.MetarParseException;
import com.emdata.messagewarningscore.common.parse.element.CloudGroup;
import com.emdata.messagewarningscore.common.parse.element.ElementContainer;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 云组解析工具类
 *
 * @author pupengfei
 * @version 1.0
 * @date 2020/9/11 14:50
 */
public class CloudGroupUtil {

    private static final String CAVOK = "CAVOK";

    private static final String NSC = "NSC";

    private static final String CLOUD_REGEX = "^(FEW|SCT|BKN|OVC|VV)(\\d{3})$";

    /**
     * 使用简语FEW、SCT、BKN 和OVC，分别表示少云（1～2 个八分量）、疏云（3～4 个八分量）、多云（5～7 个八分量）和阴天（8 个八分量）<br/>
     * NSC表示1500 米或最高的最低扇区高度（两者取其大）以下无云、天空没有积雨云或浓积云、CAVOK不适用<br/>
     * VV表示垂直能见度
     */
    private static final List<String> CLOUDINESS_LIST = Arrays.asList("FEW", "SCT", "BKN", "OVC", "VV");

    /**
     * 当预报有积雨云或浓积云时，应不加空格紧接云组之后编报简语CB 或TCU说明。当预报有相同云底高的积雨云和浓积云时，则云状只编报CB，云量按CB 和TCU 的总和编报。
     */
    private static final List<String> CLOUD_SHAPE_LIST = Arrays.asList("CB", "TCU");

    public static void main(String[] args) {
        String[] ss = new String[]{"FEW033", "BKN010CB"};

        ElementContainer<List<CloudGroup>> parse = parse(ss, 0, true);
        System.out.println(parse.get());
    }

    /**
     * 解析报文云组
     *
     * @param eles  报文要素数组
     * @param index 天气现象开始索引
     * @return 云组，没有云组，返回空集合
     */
    public static ElementContainer<List<CloudGroup>> parse(String[] eles, int index) throws MetarParseException {
        return parse(eles, index, true);
    }

    /**
     * 解析报文云组
     *
     * @param eles  报文要素数组
     * @param index 天气现象开始索引
     * @param check 是否进行格式检查，true：检查不通过时，抛出异常，false：检查不通过时返回空集合
     * @return 云组，没有云组，返回空集合
     */
    public static ElementContainer<List<CloudGroup>> parse(String[] eles, int index, boolean check) throws MetarParseException {
        if (index >= eles.length) {
            if (!check) {
                return new ElementContainer<>(null, index);
            }
            throw new MetarParseException("请检查METAR报文（要素不足）");
        }

        String w = eles[index];

        List<CloudGroup> cloudGroups = new ArrayList<>();

        // 如果是CAVOK、NSC，则没有云组
        if (CAVOK.equals(w) || "//////".equals(w)) {
            index++;
            return new ElementContainer<>(cloudGroups, index);
        }

        if (NSC.equals(w)) {
            CloudGroup cloudGroup = new CloudGroup();
            cloudGroup.setCloudHeight(9999);
            cloudGroup.setCloudiness(0);
            cloudGroups.add(cloudGroup);

            index++;
            return new ElementContainer<>(cloudGroups, index);
        }

        // 判断云状开头
        CloudGroup cloudGroup = null;
        do {
            w = eles[index];
            try {
                cloudGroup = parseCloud(w);
            } catch (MetarParseException e) {
                if (check) {
                    throw e;
                }
            }
            if (cloudGroup != null) {
                cloudGroups.add(cloudGroup);
                index++;
            }
        } while (cloudGroup != null && index < eles.length);

        return new ElementContainer<>(cloudGroups, index);
    }

    public static CloudGroup parseCloud(String cloudStr) {
        // 判断开头
        for (String ci : CLOUDINESS_LIST) {
            if (!cloudStr.startsWith(ci)) {
                continue;
            }

            String cloudShape = null;
            // 判断是不是云状结尾的
            for (String shape : CLOUD_SHAPE_LIST) {
                if (cloudStr.endsWith(shape)) {
                    cloudShape = shape;
                    // 将云状截去
                    cloudStr = cloudStr.replace(shape, "");
                }
            }

            if (!cloudStr.matches(CLOUD_REGEX)) {
                throw new MetarParseException("云组格式错误: " + cloudStr);
            }

            // 截去云高
            String hStr = cloudStr.substring(ci.length());

            CloudGroup cloudGroup = new CloudGroup();
            cloudGroup.setCloudinessType(ci);
            cloudGroup.setCloudHeight(Integer.parseInt(hStr) * 30);
            cloudGroup.setCloudShape(cloudShape);

            return cloudGroup;
        }
        return null;
    }

    /**
     * 拼接云组
     *
     * @param cloudGroups 云组集合
     * @return 拼接后的云组字符串
     */
    public static String combineCloudGroup(List<CloudGroup> cloudGroups) {
        String cloudGroupStr = "";
        StringBuilder builder = new StringBuilder();
        // 如果云组是NSC
        if (cloudGroups.size() == 1 && "NSC".equals(cloudGroups.get(0).getCloudinessType())) {
            builder.append(cloudGroups.get(0).getCloudinessType());
            cloudGroupStr = builder.toString().trim();
            return cloudGroupStr;
        }
        DecimalFormat format = new DecimalFormat("000");
        for (CloudGroup cloudGroup : cloudGroups) {
            String cloudinessType = cloudGroup.getCloudinessType();
            Integer cloudHeight = cloudGroup.getCloudHeight() / 30;
            String cloudShape = cloudGroup.getCloudShape();
            String cloud = cloudinessType + format.format(cloudHeight);
            if (cloudShape != null) {
                cloud = cloud + cloudShape;
            }
            builder.append(cloud).append(" ");
        }
        cloudGroupStr = builder.toString().trim();
        return cloudGroupStr;
    }
}
