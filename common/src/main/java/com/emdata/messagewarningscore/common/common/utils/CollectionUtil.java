package com.emdata.messagewarningscore.common.common.utils;/**
 * Created by zhangshaohu on 2020/12/24.
 */


import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author: zhangshaohu
 * @date: 2020/12/24
 * @description: 集合类处理工具类 此工具类全部使用stram流完成
 * 因为stream已经足够简单 所以使用起来代码量不一定会减少多少
 * 但是可以通过当前类进一步了解Strem
 */
public class CollectionUtil {

    /**
     * 去重
     *
     * @param collection 去重对象
     * @param comparator 去重比较器  比较器可以使用创建 Comparator.comparing() 内部需要传入一个Function 入参是当前集合的元素
     *                   输出为 当前集合去重元素比如String类型
     *                   String 类型已经重写过hashCode 与equals方法
     *                   比如像想通过集合中的userId与userName去重
     *                   表达式方式 直接入参
     *                   e->{
     *                   return e.getUserId+e.getUserName;
     *                   }
     *                   // 方法模式 需要通过方法的引用传参
     *                   public String comparator(User u){
     *                   return u.getUserId+u.getUserName;
     *                   }
     * @param <E>        当前集合的元素类型
     * @return
     */
    public static <E> List<E> distinct(Collection<E> collection, Comparator<? super E> comparator) {
        return collection.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> {
            return new TreeSet<E>(comparator);
        }), ArrayList::new));
    }

    /**
     * 使用map 修改当前集合内部对象
     *
     * @param collection        需要修改的集合对象
     * @param map               传入一个Function 输入一个为当前集合元素对象类型 输出为需要转化的对象
     *                          将userid 转换为User对象
     *                          使用方法的引用 new User()::map
     *                          如果是静态方法直接使用 User::map
     *                          class User{
     *                          public User map(Integer userid){
     *                          return new User(userid);
     *                          }
     *                          }
     *                          传参时也可以使用表达式
     *                          userid->{
     *                          return new User(userid);
     *                          }
     * @param collectionFactory 输出集合类型 如果输出想为ArrayList 则使用ArrayList::new
     * @param <E>               集合中元素本来的类型
     * @param <R>               返回的类型
     * @return
     */
    public static <E, R, C extends Collection<R>> C map(Collection<E> collection, Function<E, R> map, Supplier<C> collectionFactory) {
        C collect = collection.stream().map(s -> {
            return map.apply(s);
        }).collect(Collectors.toCollection(collectionFactory));
        return collect;
    }

    /**
     * @param filter            过滤方法 传入一个Function
     * @param collectionFactory 返回值类型Collection类型  直接调用集合构造器即可
     * @param <E>               集合中的元素
     * @param <R>               返回类型
     * @return
     */
    public static <E, R extends Collection<E>> R filter(Collection<E> collection, Function<E, Boolean> filter, Supplier<R> collectionFactory) {
        return collection.stream().filter(s -> {
            return filter.apply(s);
        }).collect(Collectors.toCollection(collectionFactory));
    }

    /**
     * 集合分组方法
     *
     * @param collection 需要分组的集合
     * @param group      传入一个Function 输入一个为当前集合元素对象类型 输出为需要转化的对象
     *                   将userid 转换为User对象
     *                   使用方法的引用 new User()::map
     *                   如果是静态方法直接使用 User::map
     *                   class User{
     *                   public User map(Integer userid){
     *                   return new User(userid);
     *                   }
     *                   }
     *                   传参时也可以使用表达式
     *                   userid->{
     *                   return new User(userid);
     *                   }
     * @param <E>        集合元素
     * @param <K>        使用什么分组 作为k
     * @return
     */
    public static <E, K> Map<K, List<E>> groupBy(Collection<E> collection, Function<E, K> group) {
        return collection.stream().collect(Collectors.groupingBy(group));
    }

    /**
     * 求一个集合中的最大值
     *
     * @param collection 需要求的集合
     * @param comparator 比较器  如果是简单包装类 包装类已经实现比较器 Integer::compareTo 如果当前数据没有
     *                   String max = CollectionUtil.max(strings, String::compareTo);
     * @param <E>
     * @return
     */
    public static <E> E max(Collection<E> collection, Comparator<E> comparator) {

        Optional<E> max = collection.stream().max(comparator);
        if (max.isPresent()) {
            return max.get();
        }
        return null;

    }

    /**
     * 求一个集合的最小值
     *
     * @param collection 需要求的集合
     * @param comparator 比较器  如果是简单包装类 包装类已经实现比较器 Integer::compareTo 如果当前数据没有
     *                   String min = CollectionUtil.min(strings, String::compareTo);
     * @param <E>        返回值
     * @return
     */
    public static <E> E min(Collection<E> collection, Comparator<E> comparator) {
        Optional<E> max = collection.stream().min(comparator);
        if (max.isPresent()) {
            return max.get();
        }
        return null;
    }

    /**
     * 排序
     *
     * @param collection        需要排序的集合
     * @param comparator        比较器的类型
     * @param collectionFactory 需要聚合的集合 ArrayList::new
     * @param <E>
     * @param <R>
     * @return
     */
    private static <E, R extends Collection<E>> R sorte(Collection<E> collection, Comparator<E> comparator, Supplier<R> collectionFactory) {
        return collection.stream().sorted(comparator).collect(Collectors.toCollection(collectionFactory));
    }

    /**
     * 将map 转换为List
     *
     * @param map
     * @param <T>
     * @return
     */
    public static <T> List<T> toList(Map<String, List<T>> map) {
        List<T> ts = new ArrayList<>();
        for (Map.Entry<String, List<T>> stringListEntry : map.entrySet()) {
            ts.addAll(stringListEntry.getValue());
        }
        return ts;
    }
}