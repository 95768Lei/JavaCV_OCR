package com.example.administrator.javacv_ocr.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhanglei on 2017/3/7 0007.
 * 处理字符串的工具类
 */

public class StringUtils {

    /**
     * 判断字符串是否为 null
     * 若为null就返回空字符
     * 不为null就返回其本体
     *
     * @param str
     * @return
     */
    public static String getString(String str) {
        return str.equals("null") ? "" : str;
    }

    /**
     * 判断是否项
     *
     * @param tag
     * @return
     */
    public static String getIsOk(int tag) {
        return tag == 0 ? "是" : "否";
    }

    public static String getIsObject(Object object) {
        String str = object + "";
        return getIsOk(new Integer(getString(str)));
    }

    /**
     * 判断手机号是否合法
     *
     * @param mobiles
     * @return
     */
    public static boolean isMobileNO(String mobiles) {
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();

    }

    /**
     * 判断身份证是否合法
     *
     * @return
     */
    public static boolean isIdentityNo(String identity) {
        Pattern patternSfzhm1 = Pattern
                .compile("^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{4}$");
        Matcher matcherSfzhm1 = patternSfzhm1.matcher(identity);

        return matcherSfzhm1.matches();
    }

}
