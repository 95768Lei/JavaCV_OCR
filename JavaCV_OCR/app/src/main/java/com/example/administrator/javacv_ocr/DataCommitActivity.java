package com.example.administrator.javacv_ocr;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.administrator.javacv_ocr.Utils.API;
import com.example.administrator.javacv_ocr.Utils.IplImageUtils;
import com.googlecode.tesseract.android.TessBaseAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.example.administrator.javacv_ocr.Utils.API.CHINESE_LANGUAGE;
import static com.example.administrator.javacv_ocr.Utils.API.DATA_NULL;
import static com.example.administrator.javacv_ocr.Utils.API.DEFAULT_LANGUAGE;
import static com.example.administrator.javacv_ocr.Utils.API.INIT_ERROR;
import static com.example.administrator.javacv_ocr.Utils.API.NUMBER_READ_OK;
import static com.example.administrator.javacv_ocr.Utils.API.TESSBASE_PATH;

/**
 * @author zhanglei
 * @date 17/2/10
 * 身份证识别功能页面（暂时不用）
 */
public class DataCommitActivity extends BaseActivity implements View.OnClickListener {

    private EditText person_name, address, id_number, jg_name;
    private View title;
    private ImageView back;
    private TextView title_name;
    private ImageView icon1;
    private ImageView icon;
    private TextView date;
    private PopupWindow popupWindow;
    private DatePicker picker;
    private RadioGroup rg_sex;
    private String string_name;
    private String string_address;
    private String string_date, string_race;
    private String string_number;
    private String string_jp_name;
    private String sex;
    private AlertDialog dialog;
    private ProgressDialog progressDialog;
    private static final int PHOTO_RESOULT = 3;// 结果
    private static final String IMAGE_UNSPECIFIED = "image/*";
    private String issued_by;
    private EditText mz_edit;
    //处理身份证识别时返回的消息
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.e("msg==", "handleMessage..");
            switch (msg.what) {
                case DATA_NULL:
                    show("身份证号码读取失败");
                    progressDialog.dismiss();
                    break;
                case INIT_ERROR:
                    show("身份证识别引擎初始化失败");
                    progressDialog.dismiss();
                    break;
                case NUMBER_READ_OK:
                    show("身份证读取成功");
                    progressDialog.dismiss();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_commit);
        initView();
        initListener();
    }

    public void initView() {
        picker = new DatePicker(mActivity);
        popupWindow = new PopupWindow(picker, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.commit_edit_shape));
        popupWindow.setTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);

        title = findViewById(R.id.commit_title);

        back = (ImageView) title.findViewById(R.id.base_back_icon);
        title_name = (TextView) title.findViewById(R.id.base_title_name);
        icon1 = (ImageView) title.findViewById(R.id.base_icon_right);
        //设置title内容
        title_name.setText("信息录入");
        icon1.setImageResource(R.mipmap.ok);

        person_name = (EditText) findViewById(R.id.commit_name_edit);
        icon = (ImageView) findViewById(R.id.commit_icon);
        date = (TextView) findViewById(R.id.commit_date_text);
        address = (EditText) findViewById(R.id.commit_address_edit);
        jg_name = (EditText) findViewById(R.id.commit_jg_edit);
        id_number = (EditText) findViewById(R.id.commit_number_edit);
        rg_sex = (RadioGroup) findViewById(R.id.commit_rg_sex);
        mz_edit = (EditText) findViewById(R.id.commit_mz_edit);
    }

    public void initListener() {
        back.setOnClickListener(this);
        icon1.setOnClickListener(this);
        icon.setOnClickListener(this);
        date.setOnClickListener(this);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                int year = picker.getYear();
                int month = picker.getMonth() + 1;
                int day = picker.getDayOfMonth();
                date.setText(year + "-" + month + "-" + day);
                setAlpha(1f);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //返回键
            case R.id.base_back_icon:
                finish();
                break;
            //确认按钮（打开信息预览界面）
            case R.id.base_icon_right:
                openDataUi();
                break;
            //打开相机照相
            case R.id.commit_icon:
                startActivityForResult(new Intent(mActivity, CameraActivity.class), CameraActivity.CAMERA_CODE);
                break;
            //选择时间
            case R.id.commit_date_text:
                openDatePopWindow();
                break;
        }
    }

    /**
     * 打开信息预览界面
     */
    private void openDataUi() {
        if (checkData()) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            builder.setTitle("请核实信息");
            TextView textView = new TextView(mActivity);
            textView.setText(getData());
            builder.setView(textView);
            dialog = builder.setNeutralButton("确认无误", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    commitData();
                }
            }).setPositiveButton("返回修改", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                //meven
                }
            }).show();

        }
    }

    /**
     * 提交数据
     */
    private void commitData() {
        dialog.dismiss();
    }

    /**
     * 拼接界面数据用于展示
     */
    private String getData() {
        StringBuilder builder = new StringBuilder();
        builder.append(" 姓名：")
                .append(string_name + "\n")
                .append(" 性别：")
                .append(sex + "\n")
                .append(" 民族：")
                .append(string_race + "\n")
                .append(" 住址：")
                .append(string_address + "\n")
                .append(" 出生日期：")
                .append(string_date + "\n")
                .append(" 身份证号：")
                .append(string_number + "\n")
                .append(" 身份证签发机关：")
                .append(string_jp_name);

        return builder.toString();

    }

    /**
     * 打开时间选择窗口
     */
    private void openDatePopWindow() {
        popupWindow.showAtLocation(date, Gravity.CENTER, 0, 0);
        setAlpha(0.6f);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CameraActivity.CAMERA_CODE && resultCode == CameraActivity.CAMERA_CODE) {
            //从相机界面成功返回数据（数据已经被储存在固定的文件夹下，路径也是固定的直接去此路径下去拿出来就好）
            final Bitmap bitmap = BitmapFactory.decodeFile(API.image_path_name);
            icon.setImageBitmap(bitmap);
            if (progressDialog == null) {
                progressDialog = new ProgressDialog(mActivity);
            }
            progressDialog.setMessage("信息识别中...");
            progressDialog.show();

            //开启线程获取身份证信息
            new Thread() {
                @Override
                public void run() {
                    getPersonData(bitmap);
                }
            }.start();
        }

    }

    /**
     * 获取身份证上面的信息
     *
     * @param bitmap 要识别的身份证照片
     */
    private void getPersonData(Bitmap bitmap) {
        //获取图片的宽高
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        //计算身份证号的位置
        int number_top = (int) (height * API.top_percent);
        int number_height = (int) (height * API.height_percent);
        int number_left = (int) (width * API.left_percent);
        int number_length = (int) (width * API.width_percent);

        int name_top = (int) (height * API.name_top_percent);
        int name_height = (int) (height * API.name_height_percent);
        int name_left = (int) (width * API.name_left_percent);
        int name_length = (int) (width * API.name_width_percent);
        //对图片进行裁剪(获取身份证号码区域)
        final Bitmap number_bitmap = IplImageUtils.TailorImage(bitmap, number_left, number_top, number_length, number_height);
        final Bitmap name_bitmap = IplImageUtils.TailorImage(bitmap, name_left, name_top, name_length, name_height);
        //识别图像信息
        final Map<String, String> map = getTextUtf_8(number_bitmap);
        final String name = getName(name_bitmap);
        //对裁剪后的图片进行储存
        try {
            FileOutputStream outputStream = new FileOutputStream(API.image_path_id_number);
            name_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //将识别后的信息显示在界面上
        mHandler.post(new Runnable() {
            @Override
            public void run() {

                if (map.size() >= 0) {
                    try {
                        String number = map.get(API.PERSON_NUMBER);
                        String birthDay = map.get(API.PERSON_BIRTHDAY);
                        String sex = map.get(API.PERSON_SEX);
                        if (!TextUtils.isEmpty(number)) {
                            id_number.setText(number);
                        }
                        if (!TextUtils.isEmpty(birthDay)) {
                            date.setText(birthDay);
                        }
                        if (!TextUtils.isEmpty(sex)) {
                            switch (sex) {
                                case "男":
                                    rg_sex.check(R.id.commit_rb_nan);
                                    break;
                                case "女":
                                    rg_sex.check(R.id.commit_rb_nv);
                                    break;
                            }
                        }
                        if (!TextUtils.isEmpty(name)) {
                            person_name.setText(name);
                        }
                    } catch (NullPointerException e) {

                    }
                }
            }
        });


    }

    /**
     * 获取身份证中的姓名
     *
     * @param name_bitmap
     */
    private String getName(Bitmap name_bitmap) {
        TessBaseAPI idApi = new TessBaseAPI();
        boolean isInit = idApi.init(TESSBASE_PATH, CHINESE_LANGUAGE);
        if (isInit) {
            idApi.setImage(name_bitmap);
            //去除首尾空格
            String str = idApi.getUTF8Text().trim();
            Log.e("tag==", "读取的结果:" + str);
            idApi.clear();
            idApi.end();
            return str;
        } else {
            //OCR识别引擎初始化失败
            mHandler.sendEmptyMessage(INIT_ERROR);
        }
        return null;
    }

    /**
     * 获取识别出来的身份信息
     *
     * @param bitmap
     */
    private Map<String, String> getTextUtf_8(Bitmap bitmap) {
        //用来储存识别出来的信息
        Map<String, String> map = new HashMap<>();
        TessBaseAPI idApi = new TessBaseAPI();
        boolean isInit = idApi.init(TESSBASE_PATH, DEFAULT_LANGUAGE);
        if (isInit) {
            idApi.setImage(bitmap);
            //去除首尾空格
            String str = idApi.getUTF8Text().trim();
            String trim;
            if (str.contains(" ")) {
                trim = str.substring(0, str.lastIndexOf(" ")).trim();
            } else {
                trim = str;
            }
            Log.e("tag==", "读取的结果:" + str);
            idApi.clear();
            idApi.end();
            //处理结果
            if (trim.length() == 18) {
                //身份证号
                map.put(API.PERSON_NUMBER, trim);
                //性别
                int sex = new Integer(trim.substring(16, 17));
                if (sex % 2 == 0) {
                    //女性
                    map.put(API.PERSON_SEX, "女");
                } else {
                    //男性
                    map.put(API.PERSON_SEX, "男");
                }
                //出生日期
                String year = trim.substring(6, 10);
                String month = trim.substring(10, 12);
                String day = trim.substring(12, 14);
                map.put(API.PERSON_BIRTHDAY, year.concat("-").concat(month).concat("-").concat(day));
                //身份证读取成功，发送消息通知Handler身份证信息读取成功
                mHandler.sendEmptyMessage(NUMBER_READ_OK);

            } else {
                //身份证读取失败，发送消息通知Handler身份证信息读取失败
                mHandler.sendEmptyMessage(DATA_NULL);
            }
        } else {
            //OCR识别引擎初始化失败
            mHandler.sendEmptyMessage(INIT_ERROR);
        }

        return map;
    }

    /**
     * 给界面填充内容
     *
     * @param body 需要做处理的JSON字符串
     */
    private void setData(String body) {
        //解析数据
        try {
            JSONObject object = new JSONObject(body);
            JSONArray array = object.optJSONArray("cards");
            if (array != null) {
                JSONObject object1 = array.optJSONObject(0);
                String side = object1.optString("side");
                //获取身份证正面信息
                if (side.equals("front")) {
                    sex = object1.optString("gender");
                    string_name = object1.optString("name");
                    string_number = object1.optString("id_card_number");
                    string_date = object1.optString("birthday");
                    string_race = object1.optString("race");
                    string_address = object1.optString("address");

                    //解析完数据，将数据显示在界面上
                    if (sex.equals("男")) {
                        rg_sex.check(R.id.commit_rb_nan);
                    } else if (sex.equals("女")) {
                        rg_sex.check(R.id.commit_rb_nv);
                    } else {
                        rg_sex.check(R.id.commit_rb_nan);
                    }

                    person_name.setText(string_name);
                    address.setText(string_address);
                    date.setText(string_date);
                    id_number.setText(string_number);
                    mz_edit.setText(string_race);

                }
                //获取身份证背面信息
                else {
                    issued_by = object1.optString("issued_by");
                    jg_name.setText(issued_by);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * 获取数据并判断数据是否为空
     */
    private boolean checkData() {
        //获取数据
        string_name = person_name.getText().toString();
        string_address = address.getText().toString();
        string_date = date.getText().toString();
        string_number = id_number.getText().toString();
        string_jp_name = jg_name.getText().toString();
        string_race = mz_edit.getText().toString();
        int id = rg_sex.getCheckedRadioButtonId();
        //判断内容是否有空缺
        if (id == -1) {
            show("请选择性别");
            return false;
        }
        if (TextUtils.isEmpty(string_name)) {
            show("请填写姓名");
            return false;
        }
        if (TextUtils.isEmpty(string_race)) {
            show("请填写民族");
            return false;
        }
        if (TextUtils.isEmpty(string_address)) {
            show("请填写地址");
            return false;
        }
        if (TextUtils.isEmpty(string_date) && !string_date.equals("出生日期")) {
            show("请选择出生日期");
            return false;
        }
        if (TextUtils.isEmpty(string_number)) {
            show("请填写身份证号");
            return false;
        }
        if (TextUtils.isEmpty(string_jp_name)) {
            show("请填写身份证签发机关");
            return false;
        }

        //获取性别
        switch (id) {
            case R.id.commit_rb_nan:
                sex = "男";
                break;
            case R.id.commit_rb_nv:
                sex = "女";
                break;
        }

        return true;
    }
}
