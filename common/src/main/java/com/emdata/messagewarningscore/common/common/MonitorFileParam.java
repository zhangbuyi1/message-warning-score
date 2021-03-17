package com.emdata.messagewarningscore.common.common;

import lombok.Data;

import java.nio.file.Path;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @date: 2020/12/29
 * @author: sunming
 */
@Data
public class MonitorFileParam implements Delayed {

    /**
     * 当前对象重试次数
     */
    private int num;
    /**
     * 延迟队列超时时间
     */

    private long timeOut = 1000 * 1;
    /**
     * 构建对象时间
     */
    private long time;

    private Path path;


    @Override
    public long getDelay(TimeUnit unit) {
        if (System.currentTimeMillis() - this.time > this.timeOut) {
            return -1;
        }
        return 1;
    }

    @Override
    public int compareTo(Delayed o) {
        // 根据时间取值
        return (int) (o.getDelay(TimeUnit.MILLISECONDS) - this.time);
    }

    public static MonitorFileParam build(Path path, int num) {
        MonitorFileParam monitorFileParam = new MonitorFileParam();
        monitorFileParam.setNum(num);
        monitorFileParam.setPath(path);
        monitorFileParam.setTime(System.currentTimeMillis());
        return monitorFileParam;
    }

}
