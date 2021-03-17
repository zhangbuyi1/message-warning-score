package com.emdata.messagewarningscore.core.score.handler.data;

import com.emdata.messagewarningscore.common.accuracy.entity.ForcastWeather;
import com.emdata.messagewarningscore.common.dao.entity.SeriousDO;
import com.emdata.messagewarningscore.common.warning.util.MatchUtil;
import com.emdata.messagewarningscore.core.service.bo.VisCbaseRvrBO;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * @description:
 * @date: 2021/1/13
 * @author: sunming
 */
public class CommonMethed {
    /**
     * 获取能见度、云底高、RVR的数值
     *
     * @param main
     * @param w
     * @param function
     * @param <T>
     * @return
     */
    public static <T> Integer getValue(List<ForcastWeather> main, T w, Function<T, String> function) {
        Optional<Integer> first = main.stream().filter(s -> s.getCode().equals(function.apply(w)))
                .map(s -> Optional.ofNullable(s.getMin()).orElse(0)).findFirst();
        return first.orElse(0);
    }


    /**
     * 赋值能见度，云底高，rvr
     *
     * @param seriousDO
     * @param t
     */
    public static <T extends VisCbaseRvrBO> void setMetarVisCbaseRvr(SeriousDO seriousDO, T t) {
        if (seriousDO.getWeather().contains("能见度") || seriousDO.getWeather().contains("云底高") || seriousDO.getWeather().contains("RVR")) {
            String[] split = seriousDO.getWeather().split("；");
            for (String ele : split) {
                Integer value = Optional.ofNullable(MatchUtil.get(ele, "\\d+")).map(Integer::parseInt).orElse(-1);
                if (ele.contains("能见度")) {
                    t.setMetarVis(value);
                } else if (ele.contains("云底高")) {
                    t.setMetarCbase(value);
                } else if (ele.contains("RVR")) {
                    t.setMetarRvr(value);
                }
            }
        }
    }
}
