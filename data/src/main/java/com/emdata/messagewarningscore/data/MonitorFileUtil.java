package com.emdata.messagewarningscore.data;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * @author: zhangshaohu
 * @date: 2021/2/25
 * @description:
 */
public class MonitorFileUtil {
    public interface BiCounsumer<T, M, W> {
        void accept(T t, M m, W w);

    }

    public static void main(String[] args) {
        monitor("D:\\2t", (f, m) -> {
            // 输出文件
            System.out.println(f);
            // 输出监控事件
            System.out.println(m);
        }, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_CREATE);
    }

    /**
     * @param filePath     监控文件路径
     * @param fileConsumer 监控到消费方法
     * @param event        监控事件
     */
    public static void monitor(String filePath, BiConsumer<File, WatchEvent.Kind<?>> fileConsumer, WatchEvent.Kind<?>... event) {
        monitor(filePath, MonitorFileUtil::defaultRegisterConsumer, fileConsumer, event);
    }

    /**
     * 监控文件夹
     *
     * @param filePath         监控文件路径
     * @param registerConsumer 注册方法
     * @param fileConsumer     消费方法
     * @param event            监控事件
     */
    public static void monitor(String filePath,
                               BiCounsumer<String, WatchService, Map<WatchKey, String>> registerConsumer,
                               BiConsumer<File, WatchEvent.Kind<?>> fileConsumer,
                               WatchEvent.Kind<?>... event) {
        baseMonitor(filePath, registerConsumer, fileConsumer, event);
    }

    /**
     * 当前有一个bug 删除文件夹应该WatchService 注册也删除 但是没有找到删除方法
     *
     * @param filePath         监控文件夹路径
     * @param event            监控文件事件  StandardWatchEventKinds.ENTRY_CREATE
     * @param registerConsumer 注册方法
     * @param fileConsumer     消费方法
     */
    private static void baseMonitor(String filePath, BiCounsumer<String, WatchService, Map<WatchKey, String>> registerConsumer, BiConsumer<File, WatchEvent.Kind<?>> fileConsumer, WatchEvent.Kind<?>... event) {
        Map<WatchKey, String> watchKeyMap = new ConcurrentHashMap<>();
        try {
            // 实例化watchService
            WatchService watchService = FileSystems.getDefault().newWatchService();
            // 子文件夹注册监控事件
            registerConsumer.accept(filePath, watchService, watchKeyMap);
            // 将path注册到watchService中
            for (; ; ) {
                WatchKey poll = watchService.poll();
                if (poll != null) {
                    String s = watchKeyMap.get(poll);
                    List<WatchEvent<?>> watchEvents = poll.pollEvents();
                    for (WatchEvent<?> watchEvent : watchEvents) {
                        WatchEvent.Kind<Path> kind = (WatchEvent.Kind<Path>) watchEvent.kind();
                        // 如果是创建文件
                        if (kind.equals(StandardWatchEventKinds.ENTRY_CREATE)) {
                            // 添加文件需要注册到监视器 WatchService 并添加到watchKeyMap中
                            baseCreate(watchKeyMap, watchService, s, (WatchEvent<Path>) watchEvent);
                        }
                        // 删除事件
                        if (kind.equals(StandardWatchEventKinds.ENTRY_DELETE)) {
                            // 需要删除注册到WatchService的文件夹(暂未找到删除WatchService方法) 并删除watchKeyMap中对应的事件
                            baseDelete(watchKeyMap, s, (WatchEvent<Path>) watchEvent);
                        }
                        // 触发事件
                        WatchEvent<Path> watchEvent1 = (WatchEvent<Path>) watchEvent;
                        String fileName = s + File.separatorChar + watchEvent1.context().getFileName();
                        File file = new File(fileName);
                        for (WatchEvent.Kind<?> kindM : event) {
                            if (kind.equals(kindM)) {
                                fileConsumer.accept(file, kindM);
                                break;
                            }
                        }
                    }
                    poll.reset();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除基本操作
     *
     * @param watchKeyMap 监控事件map 主要存放注册事件 以及对应文件路径
     * @param s           s为文件路径
     * @param watchEvent  监控文件事件
     * @return
     */
    private static File baseDelete(Map<WatchKey, String> watchKeyMap, String s, WatchEvent<Path> watchEvent) {
        WatchEvent<Path> watchEvent1 = watchEvent;
        String fileName = s + File.separatorChar + watchEvent1.context().getFileName();
        File file = new File(fileName);
        if (file.isDirectory()) {
            // 获得需要删除的key
            List<WatchKey> collect = watchKeyMap.entrySet().stream().filter(f -> {
                return f.getValue().equals(fileName);
            }).map(f -> {
                return f.getKey();
            }).collect(Collectors.toList());
            // 删除
            for (WatchKey key : collect) {
                watchKeyMap.remove(key);
            }
        }
        return file;
    }

    /**
     * 创建文件基本操作
     *
     * @param watchKeyMap  监控事件map 主要存放注册事件 以及对应文件路径
     * @param watchService 监视器
     * @param s            文件路径
     * @param watchEvent   监控文件事件
     * @return
     * @throws IOException
     */
    private static File baseCreate(Map<WatchKey, String> watchKeyMap,
                                   WatchService watchService,
                                   String s,
                                   WatchEvent<Path> watchEvent) throws IOException {
        WatchEvent<Path> watchEvent1 = watchEvent;
        String fileName = s + File.separatorChar + watchEvent1.context().getFileName();
        File file = new File(fileName);
        if (file.isDirectory()) {
            // 注册事件
            WatchKey register = Paths.get(fileName).register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);
            watchKeyMap.put(register, fileName);
        }
        return file;
    }

    /**
     * 过滤需要监控的文件夹
     *
     * @param filePath     主文件夹路劲
     * @param watchService 监视器
     * @param watchKeyMap  监听事件与文件目录map
     */
    private static void defaultRegisterConsumer(String filePath, WatchService watchService, Map<WatchKey, String> watchKeyMap) {
        // 利用path实例化监控对象watchAble
        List<String> allDir = getAllDir(filePath, new ArrayList<>());
        // 将需要监控的文件注册到 WatchService
        allDir.forEach(s -> {
            try {
                WatchKey register = Paths.get(s).register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);
                watchKeyMap.put(register, s);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 返回当前文件夹下所有子文件夹
     *
     * @param filePath 文件夹路劲
     * @param re       返回值
     * @return
     */
    private static List<String> getAllDir(String filePath, List<String> re) {
        File file = new File(filePath);
        if (file.isDirectory()) {
            re.add(file.getPath());
            File[] files = file.listFiles();
            for (File file1 : files) {
                getAllDir(file1.getPath(), re);
            }
        }
        return re;
    }
}