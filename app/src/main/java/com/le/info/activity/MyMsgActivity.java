package com.le.info.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EdgeEffect;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bigkoo.pickerview.TimePickerView;
import com.huantansheng.easyphotos.EasyPhotos;
import com.huantansheng.easyphotos.callback.SelectCallback;
import com.huantansheng.easyphotos.models.album.entity.Photo;
import com.kongzue.dialog.listener.OnMenuItemClickListener;
import com.kongzue.dialog.v2.BottomMenu;
import com.kongzue.dialog.v2.SelectDialog;
import com.le.info.R;
import com.le.info.base.BaseActivity;
import com.le.info.base.GlideApp;
import com.le.info.bean.net.FileBean;
import com.le.info.net.ApiManager;
import com.le.info.net.CommonNetObserver;
import com.le.info.net.SimpleNetObserver;
import com.le.info.net.UploadApiManager;
import com.le.info.net.api.CommonApi;
import com.le.info.net.api.UserApi;
import com.le.info.utils.BitmapUtil;
import com.le.info.utils.Constant;
import com.le.info.utils.DateUtils;
import com.le.info.utils.GlideEngine;
import com.le.info.utils.PostUtils;
import com.le.info.utils.SPUtils;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;

public class MyMsgActivity extends BaseActivity {

    @BindView(R.id.tv_main_title)
    TextView mTvMainTitle;
    @BindView(R.id.tv_sub_title)
    TextView mTvSubTitle;
    @BindView(R.id.iv_head_pic)
    ImageView mIvHeadPic;
    @BindView(R.id.tv_birthday)
    TextView mTvBirthday;
    @BindView(R.id.tv_gender)
    TextView mTvGender;
    @BindView(R.id.et_username)
    EditText mEtUsername;

    private String headPicUrl = "";  //头像地址
    private String username = "";
    private String gender = "";
    private String birthday = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_msg);
        ButterKnife.bind(this);
        mTvMainTitle.setText(getIntent().getStringExtra("title"));

        setUserInfo();
    }

    public static void start(Context context, String title){
        Intent intent = new Intent(context,MyMsgActivity.class);
        intent.putExtra("title",title);
        context.startActivity(intent);
    }

    @OnClick({R.id.iv_back,R.id.iv_head_pic,R.id.linear_select_time,R.id.linear_select_gender})
    public void onClicked(View view){
        switch(view.getId()){
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.iv_head_pic:
                choosePic();
                break;
            case R.id.linear_select_time:
                timeSelect();
                break;
            case R.id.linear_select_gender:
                genderSelect();
                break;
        }
    }

    public void choosePic() {
        EasyPhotos.createAlbum(this, true, GlideEngine.getInstance())
                .isCrop(true)
                .setCircleDimmedLayer(true)
                .setCount(1)
                .setPuzzleMenu(false)
                .setGif(false)
                .setMinFileSize(1024 * 5)
                .start(new SelectCallback() {
                    @Override
                    public void onResult(ArrayList<Photo> photos, ArrayList<String> paths, boolean isOriginal) {
                        if (photos == null || photos.size() == 0) {
                            return;
                        }

                        String choosePath = photos.get(0).cropPath;

                        setImage(choosePath);

                        uploadThumb(0,choosePath);
                    }
                });
    }

    private void setImage( String path) {
        GlideApp.with(this)
                .load(path)
                .fitCenter()
                .circleCrop()
                .placeholder(R.drawable.ic_default_head)
                .into(mIvHeadPic);
    }

    private void uploadThumb(final int pic, String path) {
        Observable.just(path)
                .map(new Function<String, File>() {
                    @Override
                    public File apply(String s) throws Exception {
                        File file = new File(getCacheDir().getAbsolutePath(), UUID.randomUUID().toString() + ".jpg");
                        String absolutePath = file.getAbsolutePath();
                        BitmapUtil.compressBitmapAndSave(s, absolutePath, 1080, 1080, 500);
                        return file;
                    }
                })
                .subscribeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<File>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                        getLoadingDialog().show();
                    }

                    @Override
                    public void onNext(final File file) {
                        List<MultipartBody.Part> parts = PostUtils.toRequestBody(Collections.singletonList(file), Arrays.asList("files"));

                        UploadApiManager.getInstance().create(CommonApi.class)
                                .uploadFile(parts)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new SimpleNetObserver<FileBean>(MyMsgActivity.this,getLoadingDialog(), compositeDisposable) {
                                    @Override
                                    public void onSuccess(FileBean response) {
                                        String uploadUrl = response.getFile().get(0).getPath();

                                        switch (pic) {
                                            case 0:
                                                headPicUrl = uploadUrl;
                                                break;
                                        }
                                    }
                                });
                    }

                    @Override
                    public void onError(Throwable e) {
                        getLoadingDialog().dismiss();
                        e.printStackTrace();
                        Toasty.error(MyMsgActivity.this,"压缩图片失败").show();
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    /**
     * 时间选择
     */
    private void timeSelect() {
        TimePickerView pickerView = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                birthday = DateUtils.formatData(date);
                mTvBirthday.setText(birthday);
            }
        }).setSubmitColor(getResources().getColor(R.color.colorPrimary))
                .setCancelColor(getResources().getColor(R.color.gray_light))
                .setDividerColor(getResources().getColor(R.color.gray_light))
                .setTextColorCenter(getResources().getColor(R.color.colorPrimary))
                .gravity(Gravity.CENTER)
                .setType(new boolean[]{true, true, true, false, false, false})
                .setTitleText("时间选择")
                .build();
        pickerView.show();

    }

    /**
     * 性别选择
     */
    private void genderSelect() {
        List<String> list = new ArrayList<>();
        list.add("男");
        list.add("女");
        list.add("保密");
        BottomMenu.show(MyMsgActivity.this, list, new OnMenuItemClickListener() {
            @Override
            public void onClick(String text, int index) {
                gender = text;
                mTvGender.setText(gender);
            }
        },true).setTitle("选择性别");
    }

    private void saveMsg(){

        String username = mEtUsername.getText().toString().trim();

        String phoneNumber = SPUtils.getPrefString("phoneNumber", "");
        ApiManager.getInstance().create(UserApi.class)
                .modifyUser(phoneNumber,username, mTvGender.getText().toString(),mTvBirthday.getText().toString(),headPicUrl)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonNetObserver<ResponseBody>(this,getLoadingDialog(),compositeDisposable) {
                    @Override
                    public void onNext(ResponseBody responseBody) {
                        try {
                            JSONObject jsonObject = new JSONObject(responseBody.string());
                            if(jsonObject.optBoolean("success")){
                                Toasty.normal(MyMsgActivity.this,jsonObject.optString("tips")).show();
                            }
                            //Toasty.normal(MyMsgActivity.this,jsonObject.optString("tips")).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toasty.error(MyMsgActivity.this,MyMsgActivity.this.getResources().getString(R.string.parse_error)).show();
                        }
                    }
                });
    }

    private void setUserInfo() {
        String headPicAddress = Constant.fillPicPath(SPUtils.getPrefString("headPicAddress", ""));
        headPicUrl = SPUtils.getPrefString("headPicAddress", "");
        username = SPUtils.getPrefString("username", "用户100000");
        gender = SPUtils.getPrefString("gender", "");
        birthday = SPUtils.getPrefString("birthday", "");

        GlideApp.with(MyMsgActivity.this)
                .load(headPicAddress)
                .circleCrop()
                .placeholder(R.drawable.ic_default_head2)
                .into(mIvHeadPic);
        mEtUsername.setText(username);
        mTvGender.setText(gender);
        mTvBirthday.setText(birthday);
    }

    @Override
    public void onBackPressed() {

        username = mEtUsername.getText().toString().trim();

        /**
         * 判断是否修改，无更改则直接返回，更改则提交更后的数据
         */
        if(!headPicUrl.equals(SPUtils.getPrefString("headPicAddress", ""))
            ||!username.equals(SPUtils.getPrefString("username", "用户100000"))
            ||!gender.equals(SPUtils.getPrefString("gender", ""))
            ||!birthday.equals(SPUtils.getPrefString("birthday", ""))){
            SelectDialog.show(MyMsgActivity.this, "温馨提示", "是否保存更改？",
                    "确定",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(TextUtils.isEmpty(username)){
                                Toasty.warning(MyMsgActivity.this,"请填写用户昵称").show();
                            } else {
                                saveMsg();
                                MyMsgActivity.super.onBackPressed();
                            }
                        }
                    },
                    "取消",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MyMsgActivity.super.onBackPressed();
                        }
                    });
        } else {
            super.onBackPressed();
        }
    }
}
