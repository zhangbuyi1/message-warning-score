package com.emdata.messagewarningscore.common.common.utils;/**
 * Created by zhangshaohu on 2021/1/15.
 */

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

/**
 * @author: zhangshaohu
 * @date: 2021/1/15
 * @description:
 */
public class Grayscale {
    /**
     * 获得一个一维灰度图像素点
     */
    private int[] pixels;
    private BufferedImage br;
    private int height;
    private int width;

    /**
     * 构造器
     *
     * @param br
     */
    public Grayscale(BufferedImage br) {
        this.br = br;
        WritableRaster raster = br.getRaster();
        int temp[] = new int[raster.getHeight() * raster.getWidth() * raster.getNumBands()];
        int[] pixels = raster.getPixels(raster.getMinX(), raster.getMinY(), raster.getWidth(), raster.getHeight(), temp);
        this.pixels = pixels;
        this.height = br.getHeight();
        this.width = br.getWidth();
    }

    /**
     * 获得灰度像素点
     *
     * @param x
     * @param y
     * @return
     */
    public int getDbz(int x, int y) {
        int r = pixels[(y * width + x) * br.getColorModel().getPixelSize() / 8];
        return r / 2 - 32;
    }

    /**
     * 获得灰度
     *
     * @param x
     * @param y
     * @return
     */
    public int getR(int x, int y) {
        return pixels[(y * width + x) * br.getColorModel().getPixelSize() / 8];
    }

    public static void main(String[] args) {
        BufferedImage read = PicUtil.read("./a.jpg");
        BufferedImage coloring = PicUtil.coloring(read, false);
        PicUtil.writeImage(coloring, "./b.jpg");
    }
}