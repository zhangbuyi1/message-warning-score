package com.emdata.messagewarningscore.common.parse.element;

import lombok.Data;

import java.util.Objects;

/**
 * 风组
 *
 * @author pupengfei
 * @version 1.0
 * @date 2020/8/28 15:59
 */
@Data
public class WindGroup {

    /**
     * 风向，为空则代表VRB，不定风向
     */
    private Double windDirection;

    /**
     * 风速
     */
    private Double windSpeed;

    /**
     * 阵风风速
     */
    private Double gustSpeed;

    /**
     * 风向区间，开始
     */
    private Double windDirectionStart;

    /**
     * 风向区间，结束
     */
    private Double windDirectionEnd;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WindGroup windGroup = (WindGroup) o;
        return Objects.equals(windDirection, windGroup.windDirection) &&
                Objects.equals(windSpeed, windGroup.windSpeed) &&
                Objects.equals(gustSpeed, windGroup.gustSpeed) &&
                Objects.equals(windDirectionStart, windGroup.windDirectionStart) &&
                Objects.equals(windDirectionEnd, windGroup.windDirectionEnd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(windDirection, windSpeed, gustSpeed, windDirectionStart, windDirectionEnd);
    }
}

















