package com.emdata.messagewarningscore.common.parse.element;

/**
 * 要素封装对象
 *
 * @author pupengfei
 * @version 1.0
 * @date 2020/9/10 20:11
 */
public class ElementContainer<T> {

    private int index;

    private T t;

    public ElementContainer(T t, int index) {
        this.index = index;
        this.t = t;
    }

    /**
     * 获取在元素集合中的指针位置
     *
     * @return 下个元素的位置索引
     */
    public int getIndex() {
        return this.index;
    }

    /**
     * 获取对应的要素对象
     *
     * @return 要素
     */
    public T get() {
        return this.t;
    }

}
