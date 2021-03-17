package com.emdata.messagewarningscore.common.common.utils;

import com.alibaba.fastjson.JSONArray;
import lombok.Data;
import sun.misc.BASE64Decoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PicUtil {
    @Data
    public static class LatLng {
        /**
         * 经度 x轴
         */
        private Double longitude;
        /**
         * 纬度  Y轴
         */
        private Double latitude;

        public LatLng() {
        }

        public LatLng(Double longitude, Double latitude) {
            this.longitude = longitude;
            this.latitude = latitude;
        }
    }

    public static void main(String[] args) throws IOException {
        // 绘制了一个五边形 绘制是一个点一个点绘制点所以需要按照顺序添加
//        ArrayList<Point> points = new ArrayList<Point>() {{
//            add(new Point(40, 22));
//            add(new Point(40, 70));
//            add(new Point(250, 90));
//            add(new Point(400, 70));
//            add(new Point(400, 22));
//        }};
        BufferedImage image = ImageIO.read(new File("D:\\radar\\20210114\\2101140003\\0000.png"));
        ArrayList<LatLng> latLngArrayList = new ArrayList<LatLng>() {{
            add(new LatLng(121.334106, 31.217486));
            add(new LatLng(121.338706, 31.218103));
            add(new LatLng(121.339137, 31.223168));
            add(new LatLng(121.349917, 31.224897));
            add(new LatLng(121.351066, 31.216991));
            add(new LatLng(121.353222, 31.216003));
            add(new LatLng(121.354372, 31.211062));
            add(new LatLng(121.35854, 31.209456));
            add(new LatLng(121.358828, 31.206985));
            add(new LatLng(121.35236, 31.19846));
            add(new LatLng(121.352647, 31.188451));
            add(new LatLng(121.342443, 31.181778));
            add(new LatLng(121.334538, 31.192653));
            add(new LatLng(121.333532, 31.211062));
        }};

        String s = JSONArray.toJSONString(latLngArrayList);
        System.out.println(s);
        List<Point> points = LaLongitudeToPixels(latLngArrayList, new LatLng(124.012678, 33.457045),
                new LatLng(118.628416, 28.965465)
                , image);
        // 验证是否正确 所有可以将当前图片输出
        BufferedImage bi = sketch(points, image.getWidth(), image.getHeight());
        ImageIO.write(bi, "JPEG", new FileOutputStream("./a.jpg"));
        List<Point> circlePointPixel = findCirclePointPixel(new LatLng(121.334106, 31.217486), new LatLng(121.404174, 31.150477), new LatLng(121.274675, 31.226843),
                image, 0.2);
        BufferedImage bufferedImage = drawByPoint(circlePointPixel, image);

        ImageIO.write(bufferedImage, "JPEG", new FileOutputStream("./b.jpg"));
    }

    /**
     * @param latLngs       需要绘制航路的经纬度
     * @param startLatLng   雷达图片开始的经纬度
     * @param endLatLng     雷达图片结束的经纬度
     * @param bufferedImage 雷达图片
     * @param routeWide     航线的宽度 公里
     * @return
     */
    public static List<Point> findLinePointPixel(List<LatLng> latLngs, LatLng startLatLng, LatLng endLatLng, BufferedImage bufferedImage, Double routeWide) {
        /**
         * 航线宽度像素点
         */
        Integer routeWidePiels = kilometreToPixels(routeWide, startLatLng, endLatLng, bufferedImage);
        /**
         * 将经纬度转为像素点
         */
        List<Point> points = LaLongitudeToPixels(latLngs, startLatLng, endLatLng, bufferedImage);

        BufferedImage line = drawLine(points, bufferedImage.getWidth(), bufferedImage.getHeight(), routeWidePiels);
        /**
         * 得到航线经过的所有像素点
         */
        List<Point> linePixels = findPoint(line);

        return linePixels;
    }

    /**
     * 根据圆点和半径 获得圆形中的所有像素点
     *
     * @param latLng        经纬度
     * @param startLatLng   雷达图片开始的经纬度
     * @param endLatLng     雷达图片结束的经纬度
     * @param bufferedImage 图片
     * @param radius        圆的半径 多少公里 单位为公里 纬度与公里的比为 111公里等于一度 将公里换算为纬度 在由纬度换算出像素点 在用半径为多少像素点画圆
     * @return
     */
    public static List<Point> findCirclePointPixel(LatLng latLng, LatLng startLatLng, LatLng endLatLng, BufferedImage bufferedImage, Double radius) {
        /**
         * 半径像素点 半径根据纬度
         */
        Integer integer = kilometreToPixels(radius, startLatLng, endLatLng, bufferedImage);
        /**
         * 得到圆心像素点
         */
        Point point = LaLongitudeToPixel(latLng, startLatLng, endLatLng, bufferedImage);
        /**
         * 将圆内面积设置为白色
         */
        BufferedImage circle = drawCircle(bufferedImage, integer, point);
        /**
         * 得到圆类面积的像素点
         */
        List<Point> allPoint = findPoint(circle);
        return allPoint;
    }

    /**
     * 主要用于测试
     * 根据像素点绘制将范围类改为白色
     *
     * @param points        像素点
     * @param bufferedImage 原始图片
     * @return
     */
    public static BufferedImage drawByPoint(List<Point> points, BufferedImage bufferedImage) {
        points.stream().map(s -> {
            bufferedImage.setRGB(s.x, s.y, 0xffffffff);
            return s;
        }).collect(Collectors.toList());
        return bufferedImage;

    }

    /**
     * 画圆
     *
     * @param bufferedImage 原始图片
     * @param integer       半径像素点
     * @param point         中心像素点
     */
    private static BufferedImage drawCircle(BufferedImage bufferedImage, Integer integer, Point point) {
        // 画圆
        BufferedImage bi = new BufferedImage
                (bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        // 获得画笔
        Graphics2D g2d = (Graphics2D) bi.getGraphics();
        //设置画笔黑色
        g2d.setColor(Color.BLACK);
        //全图填充黑色
        g2d.fillRect(0, 0, bi.getWidth(), bi.getHeight());
        // 设置画笔为白色
        g2d.setColor(Color.WHITE);
        // 3. 填充一个圆形
        g2d.fillArc(point.x - integer, point.y - integer, integer * 2, integer * 2, 0, 360);
        g2d.dispose();
        return bi;
    }

    /**
     * 将将公里转为像素点
     *
     * @param radius        公里数
     * @param startLatLng   图片开始经纬度
     * @param endLatLng     图片结束经纬度
     * @param bufferedImage 雷达图片
     * @return
     */


    public static Integer kilometreToPixels(Double radius, LatLng startLatLng, LatLng endLatLng, BufferedImage bufferedImage) {
        double wihthProportion = bufferedImage.getWidth() / (Math.abs(endLatLng.getLongitude() - startLatLng.getLongitude()));
        Double pixe = (radius / 111.0) * wihthProportion;
        return pixe.intValue();

    }

    /**
     * 转换单个点
     *
     * @param latLng        需要转化的点
     * @param startLatLng   图片开始的经纬度
     * @param endLatLng     图片结束的经纬度
     * @param bufferedImage 图片
     * @return
     */

    public static Point LaLongitudeToPixel(LatLng latLng, LatLng startLatLng, LatLng endLatLng, BufferedImage bufferedImage) {
        List<Point> points = LaLongitudeToPixels(new ArrayList<LatLng>() {{
            add(latLng);
        }}, startLatLng, endLatLng, bufferedImage.getWidth(), bufferedImage.getHeight());
        return points.get(0);
    }

    /**
     * @param laLongitude   需要转化的经纬度点
     * @param startLatLng   图片开始的经纬度
     * @param endLatLng     图片结束的经纬度
     * @param bufferedImage 图片
     * @return 当前未考虑经读0问题
     */
    public static List<Point> LaLongitudeToPixels(List<LatLng> laLongitude, LatLng startLatLng, LatLng endLatLng, BufferedImage bufferedImage) {
        List<Point> points = LaLongitudeToPixels(laLongitude, startLatLng, endLatLng, bufferedImage.getWidth(), bufferedImage.getHeight());
        return points;
    }

    /**
     * @param laLongitude 需要转化的经纬度点
     * @param startLatLng 图片开始的经纬度
     * @param endLatLng   图片结束的经纬度
     * @param wihth       图片的宽
     * @param height      图片的高
     * @return 当前未考虑经读0问题
     */
    public static List<Point> LaLongitudeToPixels(List<LatLng> laLongitude, LatLng startLatLng, LatLng endLatLng, Integer wihth, Integer height) {
        //y轴
        Double startLatitude = startLatLng.getLatitude();
        //x轴
        Double startLongitude = startLatLng.getLongitude();
        // 结束y轴
        Double endLatitude = endLatLng.getLatitude();
        // 结束的x轴
        Double endLongitude = endLatLng.getLongitude();
        // 经纬度 高
        double heightLo = Math.abs(startLatitude - endLatitude);
        // 经纬度 宽
        double wihthLo = Math.abs(endLongitude - startLongitude);
        // 得到经纬度与当前像素点宽的比例 得到一个像素点占多少个纬度
        double x = wihthLo / wihth;
        // 得到经纬度与当前像素点高的比例  得到一个像素点占多少个经度
        double y = heightLo / height;
        List<Point> collect = laLongitude.stream().map(s -> {
            //x
            Double latitude = s.getLatitude();
            //y
            Double longitude = s.getLongitude();

            Double pixelsY = Math.abs(startLatitude - latitude) / y;

            Double pixelsX = Math.abs(longitude - startLongitude) / x;
            return new Point(pixelsX.intValue(), pixelsY.intValue());
        }).collect(Collectors.toList());
        return collect;
    }

    /**
     * 判断点是否在多边形内部
     *
     * @param pointList     需要绘制的范围 因为三个点才为一个面 所以最少传3个点
     * @param bufferedImage 原始图片
     * @param x             需要判断点的坐标
     * @param y             需要判断点的坐标
     * @return
     */
    public static boolean isExist(List<Point> pointList, BufferedImage bufferedImage, Integer x, Integer y) {
        try {
            // 先根据原始图片比例画出与之相同的图片 并且绘画出多边形 把多边形颜色搞成白色
            BufferedImage bi = sketch(pointList, bufferedImage.getWidth(), bufferedImage.getHeight());
            return isExist(bi, x, y);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 查找白色区域的像素点
     *
     * @param bi 图片
     * @return
     */
    public static List<Point> findPoint(BufferedImage bi) {
        ArrayList<Point> re = new ArrayList<>();
        for (int x = 0; x < bi.getWidth(); x++) {
            for (int y = 0; y < bi.getHeight(); y++) {
                if (isExist(bi, x, y)) {
                    re.add(new Point(x, y));
                }
            }
        }
        return re;
    }

    /**
     * 得到范围内的所有像素点
     *
     * @param pointList 多边形点 用于绘制多边形
     * @param original  原始图片 需要在原始图片上寻找 多边形内部的点
     * @return
     */
    public static List<Point> findAllPoint(List<Point> pointList, BufferedImage original) {
        ArrayList<Point> re = new ArrayList<>();
        try {
            BufferedImage bi = sketch(pointList, original.getWidth(), original.getHeight());
            for (int x = 0; x < bi.getWidth(); x++) {
                for (int y = 0; y < bi.getHeight(); y++) {
                    if (isExist(bi, x, y)) {
                        re.add(new Point(x, y));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return re;
    }

    /**
     * 判断该点是否是在多边形内
     *
     * @param bi 图片对象
     * @param x  坐标x
     * @param y  坐标y
     * @return
     */
    private static boolean isExist(BufferedImage bi, Integer x, Integer y) {
        if (((bi.getRGB(x, y) >> 16) & 0xff) == 255) {
            return true;
        }
        return false;
    }


    /**
     * 画线
     *
     * @param points    需要画的线的点
     * @param width     图片的宽度
     * @param height    图片的高度
     * @param lineWidth 线的宽度
     * @return
     */
    public static BufferedImage drawLine(List<Point> points, Integer width, Integer height, Integer lineWidth) {
        BufferedImage bi = new BufferedImage
                (width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = (Graphics2D) bi.getGraphics();
        //设置画笔黑色
        g2d.setColor(Color.BLACK);
        //全图填充黑色
        g2d.fillRect(0, 0, bi.getWidth(), bi.getHeight());
        // 设置线的宽度
        g2d.setStroke(new BasicStroke(lineWidth));
        // 将画笔转为白色
        g2d.setColor(Color.WHITE);
        // 画线
        Optional<Point> reduce = points.stream().reduce((Point s1, Point s2) -> {
            g2d.drawLine(s1.x, s1.y, s2.x, s2.y);
            return s2;
        });
        g2d.dispose();
        return bi;
    }

    /**
     * 传入point点必须按照顺序来
     *
     * @param pointList 最少传3个点  因为三个点才能成为一个面
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static BufferedImage sketch(List<Point> pointList, Integer width, Integer height) throws FileNotFoundException, IOException {
        List<Point> sketchPointList = pointList.stream().collect(Collectors.toList());
        //得到图片缓冲区
        //INT精确度达到一定,RGB三原色,宽度512，高度512
        BufferedImage bi = new BufferedImage
                (width, height, BufferedImage.TYPE_INT_RGB);
        //得到它的绘制环境(这张图片的笔)
        Graphics2D g2 = (Graphics2D) bi.getGraphics();
        //设置画笔黑色
        g2.setColor(Color.BLACK);
        //全图填充黑色
        g2.fillRect(0, 0, bi.getWidth(), bi.getHeight());
        //shape的子类，表示一个形状
        GeneralPath gp = new GeneralPath();
        // 先画一条线
        Point p1 = sketchPointList.remove(0);
        Point p2 = sketchPointList.remove(0);
        //两个点画第一条直线
        gp.append(new Line2D.Double(p1.x, p1.y, p2.x, p2.y), true);
        //直线分别与余下的点相连
        for (Point point : sketchPointList) {
            gp.lineTo(point.x, point.y);
        }
        //闭合图形
        gp.closePath();
        //设置画笔白色
        g2.setColor(Color.WHITE);
        //填充图形
        g2.fill(gp);
        return bi;
    }

    /**
     * 读取本地图片
     *
     * @param filePath
     * @return
     */
    public static BufferedImage read(String filePath) {
        File file = new File(filePath);
        try {
            BufferedImage read = ImageIO.read(file);
            return read;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将base64转为图片
     *
     * @param base64String
     * @return
     */
    public static BufferedImage readBase64(String base64String) {
        try {
            byte[] bytes1 = new BASE64Decoder().decodeBuffer(base64String);
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes1);
            BufferedImage bi1 = ImageIO.read(bais);
            return bi1;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将雷达图片写入
     *
     * @param bi
     * @param path
     */
    public static void writeImage(BufferedImage bi, String path) {
        try {
            File file = new File(path);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            ImageIO.write(bi, "JPEG", new FileOutputStream(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 上色方法
     *
     * @param br          图片
     * @param isGrayscale 当前图片是否已经是灰度图 还是dbz
     * @return
     */
    public static BufferedImage coloring(BufferedImage br, boolean isGrayscale) {
        int[] colors = new int[]{0x0, 0x19ff6, 0xecec, 0x1d800, 0x19000, 0xffff00, 0xe7c000, 0xff9000,
                0xfe0000, 0xd60000, 0xc00000, 0xff00f0, 0x9500b4, 0xae90f0};
        BufferedImage re = new BufferedImage
                (br.getWidth(), br.getHeight(), BufferedImage.TYPE_INT_RGB);
        Grayscale grayscale = new Grayscale(br);
        for (int x = 0; x < br.getWidth(); x++) {
            for (int y = 0; y < br.getHeight(); y++) {
                int r = grayscale.getDbz(x, y);
                // 如果不是灰度就是雷达反射率 默认dbz
                if (!isGrayscale) {
                    r = grayscale.getR(x, y);
                }
                int index = r / 5 - 1;
                index = index < 0 ? 0 : index;
                index = index > 13 ? 13 : index;
                int nrgb;
                if (index == 0) {
                    nrgb = colors[index];
                } else {
                    nrgb = 0xff000000 | colors[index];
                }
                re.setRGB(x, y, nrgb);
            }
        }
        return re;
    }


}
