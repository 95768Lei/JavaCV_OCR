package com.example.administrator.javacv_ocr.Utils;

import android.os.Environment;

/**
 * Created by ZhangLei on 2017/3/24 0024.
 */

public class API {
    //图片存储父路径
    public static final String image_file_path = Environment.getExternalStorageDirectory().getPath() + "/JavaCV_Image/";
    //拍摄完成后的照片
    public static final String image_path_name = image_file_path + "/image.JPG";
    //裁剪后得到的身份证号码条图片
    public static final String image_path_id_number = image_file_path + "/id_number.JPG";

    public static final String TESSBASE_PATH = Environment
            .getExternalStorageDirectory().getPath() + "/tesseract/";
    public static final String DEFAULT_LANGUAGE = "id";
    public static final String TESSDATA_PATH = TESSBASE_PATH + "tessdata/";

    //身份证号码距离上边的比例
    public static double top_percent = 0.7;
    //身份证号码区域占据整张照片高度的比例
    public static double height_percent = 0.1;
}
