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
    public static final String image_path_id_number = image_file_path + "/name.JPG";

    public static final String TESSBASE_PATH = Environment
            .getExternalStorageDirectory().getPath() + "/tesseract/";
    public static final String DEFAULT_LANGUAGE = "id";
    public static final String CHINESE_LANGUAGE = "chi";
    public static final String TESSDATA_PATH = TESSBASE_PATH + "tessdata/";

    //身份证号码距离上边的比例
    public static final double top_percent = 0.7;
    //身份证号码区域占据整张照片高度的比例
    public static final double height_percent = 0.1;
    //身份证号码区域占据整张照片宽度的比例
    public static final double width_percent = 0.4138;
    //身份证号码区域距离照片左边缘宽度的比例
    public static final double left_percent = 0.3940;

    public static final double name_top_percent = 0.21;
    public static final double name_left_percent = 0.29;
    public static final double name_width_percent = 0.15;
    public static final double name_height_percent = 0.1;

    public static final String PERSON_NAME = "name";
    public static final String PERSON_NUMBER = "number";
    public static final String PERSON_BIRTHDAY = "birthday";
    public static final String PERSON_SEX = "sex";
    public static final String PERSON_NATION = "nation";
    public static final String PERSON_ADDRESS = "address";
    public static final int DATA_NULL = 1;
    public static final int INIT_ERROR = 2;
    public static final int NUMBER_READ_OK = 3;
}
