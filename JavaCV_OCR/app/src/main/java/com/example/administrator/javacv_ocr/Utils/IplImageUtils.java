package com.example.administrator.javacv_ocr.Utils;

import android.graphics.Bitmap;

import java.nio.ByteBuffer;

import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;
import static com.googlecode.javacv.cpp.opencv_core.IplImage;
import static com.googlecode.javacv.cpp.opencv_core.cvAddWeighted;
import static com.googlecode.javacv.cpp.opencv_core.cvCopy;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateImage;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSize;
import static com.googlecode.javacv.cpp.opencv_core.cvRect;
import static com.googlecode.javacv.cpp.opencv_core.cvResetImageROI;
import static com.googlecode.javacv.cpp.opencv_core.cvSetImageROI;
import static com.googlecode.javacv.cpp.opencv_core.cvSize;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_BGR2GRAY;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvErode;

/**
 * Created by zhanglei on 2017/1/5.
 * 图片处理工具类
 */

public class IplImageUtils {

    /**
     * @param bitmap 要裁剪的图片
     * @param x      左上角x轴坐标
     * @param y      左上角y轴坐标
     * @param width  裁剪后图片的宽度
     * @param height 裁剪后图片的高度
     * @return
     */
    public static Bitmap TailorImage(Bitmap bitmap, int x, int y, int width, int height) {

        //首先将Bitmap转化为IplImage
        IplImage src = bitmapToIplImage(bitmap);
        //1.选择需要裁剪的区域进行裁剪
        cvSetImageROI(src, cvRect(x, y, width, height));
        //2.新建一个和裁剪区域一样大小的空白区
        IplImage dst = cvCreateImage(cvSize(width, height), IPL_DEPTH_8U, src.nChannels());
        //3.将裁剪后的图片复制到第二步新建的空白区
        cvCopy(src, dst);
        //4.释放原始图片
        cvResetImageROI(src);
        //5.将IplImage转化为Bitmap
        Bitmap image = IplImageToBitmap(dst);

        return image;
    }

    /**
     * IplImage转化为Bitmap
     *
     * @param iplImage
     * @return
     */
    private static Bitmap IplImageToBitmap(IplImage iplImage) {
        Bitmap bitmap = null;
        bitmap = Bitmap.createBitmap(iplImage.width(), iplImage.height(),
                Bitmap.Config.ARGB_8888);
        ByteBuffer byteBuffer = iplImage.getByteBuffer();
        bitmap.copyPixelsFromBuffer(byteBuffer);
        byteBuffer.position(0);
        return bitmap;
    }

    /**
     * Bitmap转化为IplImage
     *
     * @param bitmap
     * @return
     */
    private static IplImage bitmapToIplImage(Bitmap bitmap) {
        IplImage iplImage;
        iplImage = IplImage.create(bitmap.getWidth(), bitmap.getHeight(),
                IPL_DEPTH_8U, 4);
        bitmap.copyPixelsToBuffer(iplImage.getByteBuffer());
        return iplImage;
    }

    /**
     * 对图片进行灰度化处理
     *
     * @param bitmap
     * @return
     */
    public static Bitmap GrayImage(Bitmap bitmap) {
        IplImage img = bitmapToIplImage(bitmap);
        IplImage img1 = cvCreateImage(cvGetSize(img), IPL_DEPTH_8U, 1);//创建目标图像
        cvCvtColor(img, img1, CV_BGR2GRAY);
        Bitmap bitmap1 = IplImageToBitmap(img1);
        return bitmap1;
    }

    /**
     * 对图片进行腐蚀处理
     *
     * @param bitmap
     * @return
     */
    public static Bitmap ErodeImage(Bitmap bitmap) {
        IplImage img = bitmapToIplImage(bitmap);
        IplImage img1 = cvCreateImage(cvGetSize(img), IPL_DEPTH_8U, 1);//创建目标图像
        cvErode(img, img1, null, 0);
        Bitmap bitmap1 = IplImageToBitmap(img1);
        return bitmap1;
    }

    /**
     * 融合图片
     *
     * @param bitmap1
     * @param bitmap2
     * @return
     */
    public static Bitmap FuseImage(Bitmap bitmap1, Bitmap bitmap2) {
        //背景图
        IplImage srciImageOne = bitmapToIplImage(bitmap1);
        //挂件
        IplImage srciImageTwo = bitmapToIplImage(bitmap2);

        if (srciImageOne == null || srciImageTwo == null) {
            return null;
        }
        //定义
        int x = 0;
        int y = 0;
        int height = srciImageTwo.height() ;
        int width = srciImageTwo.width();
        double alpha = 0.8;
        double beta = 1.0 - alpha;
        IplImage dst = cvCreateImage(cvSize(width, height), IPL_DEPTH_8U, srciImageOne.nChannels());
        //设置图像的感兴趣区域：二者的感兴趣区域大小要一样
        cvSetImageROI(srciImageOne, cvRect(x, y, width, height));
        //设置srciImageTwo感兴趣的区域
        cvSetImageROI(srciImageTwo, cvRect(x, y, width, height));

        cvAddWeighted(
                //图像1，所占权重
                srciImageOne, 1,
                //图像2，所占权重
                srciImageTwo, 1,
                //常数值对输出结果有影响关系为：dst(I)=src1(I)*alpha+src2(I)*beta+gamma
                0.6,
                //输出结果
                dst);
        Bitmap bitmap = IplImageToBitmap(dst);
        //将结果恢复为原来的大小
        cvResetImageROI(srciImageOne);
        cvResetImageROI(srciImageTwo);
        return bitmap;
    }

}
