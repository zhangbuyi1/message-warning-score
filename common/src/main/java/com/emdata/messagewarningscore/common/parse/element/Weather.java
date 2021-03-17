package com.emdata.messagewarningscore.common.parse.element;

import lombok.Data;

import java.util.Objects;

/**
 * 天气现象
 *
 * @author pupengfei
 * @version 1.0
 * @date 2020/8/28 16:22
 */
@Data
public class Weather {

    /**
     * 强度，默认：中，
     * 弱/小（-）：用于弱降水的描述；
     * 中: 用于中度降水或中度沙暴和尘暴，不使用符号表示；
     * 强/大（+）: 用于强降水、强的沙暴和尘暴的描述。
     */
    private Strength strength = Strength.MIDDLE;

    /**
     * 特征描述，如：TS、FZ
     */
    private String feature;

    /**
     * 天气类型，降水类，视程障碍现象类，其他类
     */
    private String type;

    /**
     * 天气现象排列顺序
     */
    private Integer order;

    /**
     * 拼接天气现象
     *
     * @return 最终天气现象
     */
    public String splice() {
        String s = "";
        s = s + this.strength.value;
        if (this.feature != null) {
            s += this.feature;
        }
        if (this.type != null) {
            s += this.type;
        }
        return s;
    }


    /**
     * 天气现象强度枚举
     */
    public enum Strength {

        /**
         * 弱/小（-）：用于弱降水的描述；
         */
        WEAK("-"),

        /**
         * 中: 用于中度降水或中度沙暴和尘暴，不使用符号表示；
         */
        MIDDLE(""),

        /**
         * 强/大（+）: 用于强降水、强的沙暴和尘暴的描述。
         */
        STRONG("+");

        private String value;

        Strength(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Weather weather = (Weather) o;
        return strength == weather.strength &&
                Objects.equals(feature, weather.feature) &&
                Objects.equals(type, weather.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(strength, feature, type);
    }
}
