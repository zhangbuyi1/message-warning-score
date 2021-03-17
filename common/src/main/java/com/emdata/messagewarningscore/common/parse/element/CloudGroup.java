package com.emdata.messagewarningscore.common.parse.element;

import lombok.Data;

import java.util.Objects;

/**
 * 云组
 *
 * @author pupengfei
 * @version 1.0
 * @date 2020/8/28 16:30
 */
@Data
public class CloudGroup {

    /**
     * 云量，8分量，（9：代表VV-垂直能见度， 0：NSC）
     */
    private Integer cloudiness;

    /**
     * 云量编码：FEW、SCT、BKN 和OVC VV
     */
    private String cloudinessType;

    /**
     * 云高
     */
    private Integer cloudHeight;

    /**
     * 云状，云状的预报仅限于积雨云和浓积云
     */
    private String cloudShape;

    public void setCloudinessType(String type) {
        int iness = 0;
        switch (type) {
            case "NSC":
                iness = 0;
                break;
            case "FEW":
                iness = 2;
                break;
            case "SCT":
                iness = 4;
                break;
            case "BKN":
                iness = 7;
                break;
            case "OVC":
                iness = 8;
                break;
            case "VV":
                iness = 9;
                break;
            default:
                throw new IllegalArgumentException("云量字符串错误: " + type);
        }
        this.cloudiness = iness;
    }

    public String getCloudinessType() {
        if (this.cloudiness == 0) {
            return "NSC";
        } else if (this.cloudiness <= 2) {
            return "FEW";
        } else if (this.cloudiness <= 4) {
            return "SCT";
        } else if (this.cloudiness <= 7) {
            return "BKN";
        } else if (this.cloudiness <= 8) {
            return "OVC";
        } else if (this.cloudiness <= 9) {
            return "VV";
        } else {
            throw new IllegalStateException("云量值必须在 0- 9 之间：" + this.cloudiness);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CloudGroup that = (CloudGroup) o;
        return Objects.equals(cloudiness, that.cloudiness) &&
                Objects.equals(cloudinessType, that.cloudinessType) &&
                Objects.equals(cloudHeight, that.cloudHeight) &&
                Objects.equals(cloudShape, that.cloudShape);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cloudiness, cloudinessType, cloudHeight, cloudShape);
    }
}
