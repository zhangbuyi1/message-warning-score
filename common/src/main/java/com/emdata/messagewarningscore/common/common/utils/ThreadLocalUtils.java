package com.emdata.messagewarningscore.common.common.utils;/**
 * Created by zhangshaohu on 2020/7/8.
 */

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: zhangshaohu
 * @date: 2020/7/8
 * @description:
 */
public class ThreadLocalUtils {

    private final static String AIRPORY_UUID_KEY = "airportUuid";

    private final static String USER_Uuid_KEY = "userUuid";

    private final static String AIRPORT_CODE_KEY = "airportCode";

    private static ThreadLocal<Map<String, String>> concurrentThreandLocal = new ThreadLocal<Map<String, String>>() {
        @Override
        protected Map<String, String> initialValue() {
            return new ConcurrentHashMap<String, String>();
        }
    };

    public static void setAirportUuid(String airportUuid) {
        concurrentThreandLocal.get().put(AIRPORY_UUID_KEY, airportUuid);
    }

    public static void setAirportCode(String airportCode) {
        concurrentThreandLocal.get().put(AIRPORT_CODE_KEY, airportCode);
    }

    public static void setUserUuid(String userId) {
        concurrentThreandLocal.get().put(USER_Uuid_KEY, userId);
    }

    public static String getAirportUuid() {
        return concurrentThreandLocal.get().get(AIRPORY_UUID_KEY);
    }

    public static String getUserUuid() {
        return concurrentThreandLocal.get().get(USER_Uuid_KEY);
    }

    public static String getAirportCode() {
        return concurrentThreandLocal.get().get(AIRPORT_CODE_KEY);
    }

}