package com.dq.itopic.activity.user;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dq.itopic.R;
import com.dq.itopic.activity.chat.ChatActivity;
import com.dq.itopic.activity.common.BaseActivity;
import com.dq.itopic.activity.common.BaseFragment;
import com.dq.itopic.activity.mine.edit.MyProfileEditActivity;
import com.dq.itopic.bean.ChatBean;
import com.dq.itopic.bean.HisInfoResponse;
import com.dq.itopic.bean.IntResponse;
import com.dq.itopic.bean.StringResponse;
import com.dq.itopic.bean.UserBean;
import com.dq.itopic.layout.SureOrCancelDialog;
import com.dq.itopic.tools.CompleteCallback;
import com.dq.itopic.tools.ServiceConstants;
import com.dq.itopic.tools.OkHttpHelper;
import com.dq.itopic.tools.ValueUtil;
import com.dq.itopic.tools.imageloader.GlideLoaderUtil;
import com.dq.itopic.tools.statusbar.ImmersionBar;
import com.dq.itopic.tools.statusbar.StatusBarUtils;
import com.google.android.material.appbar.AppBarLayout;
import com.dq.itopic.views.popup.QMUIListPopup;
import com.dq.itopic.views.popup.QMUIPopup;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewpager.widget.ViewPager;
import cc.shinichi.library.ImagePreview;
import okhttp3.Response;

public class PersonalHomeActivity extends BaseActivity implements View.OnClickListener {

    public static final String HIS_ID_KEY = "hisUserId";
    public static final String HIS_NAME_KEY = "hisName";
    public static final String HIS_AVATAR_KEY = "hisAvatar";

    private String hisUserID;
    private String hisName;
    private String hisAvatarString;//对方头像
    private TextView header_name_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_home);
        initView(savedInstanceState);
        requireUserInfo();
        ImmersionBar.with(this).fullScreen(true).init();
        StatusBarUtils.from(this)
                .setLightStatusBar(true)
                .process(this);
    }

    @Override
    protected void initStatusBar() {
    }

    private void initView(Bundle savedInstanceState) {
        initProgressDialog();

        Toolbar toolbar = findViewById(R.id.toolbar);
        ViewGroup.LayoutParams linearParams2 =(ViewGroup.LayoutParams) toolbar.getLayoutParams(); //取控件textView当前的布局参数
        linearParams2.height = (int)getResources().getDimension(R.dimen.navigation_height) + getStatusBarHeight();

        ImageView backgroundImageView = (ImageView)findViewById(R.id.cover_iv);
        ViewGroup.LayoutParams linearParams3 =(ViewGroup.LayoutParams) backgroundImageView.getLayoutParams(); //取控件textView当前的布局参数
        linearParams3.height = linearParams2.height + (int)getResources().getDimension(R.dimen.mine_parallax_height);

        View title_stay_nobg_layout = findViewById(R.id.title_stay_nobg_layout);
        RelativeLayout.LayoutParams linearParams4 =(RelativeLayout.LayoutParams) title_stay_nobg_layout.getLayoutParams(); //取控件textView当前的布局参数
        linearParams4.height = linearParams2.height;

        ImageView avatar = (ImageView) findViewById(R.id.avatar);
        header_name_tv = (TextView) findViewById(R.id.header_name_tv);

        hisName = getIntent().getStringExtra(PersonalHomeActivity.HIS_NAME_KEY);
        header_name_tv.setText(hisName);
        setTitleName(hisName);
        hisAvatarString = getIntent().getStringExtra(PersonalHomeActivity.HIS_AVATAR_KEY);
        GlideLoaderUtil.loadImage(this,ValueUtil.getQiniuUrlByFileName(hisAvatarString,true),R.drawable.user_photo,avatar);
        avatar.setOnClickListener(this);
        findViewById(R.id.back_nobg_iv).setOnClickListener(this);
        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.more_nobg_iv).setOnClickListener(this);
        findViewById(R.id.more_iv).setOnClickListener(this);

        hisUserID = getIntent().getStringExtra(PersonalHomeActivity.HIS_ID_KEY);
        if (!TextUtils.isEmpty(hisUserID)){
            resetButtonsGetUserid();
        }
    }

    private void requireUserInfo() {
        HashMap<String, String> params = new HashMap<String, String>();
        if (!TextUtils.isEmpty(hisUserID)){
            params.put("to_userid", hisUserID);
        } else {
            params.put("to_name", hisName);
        }

        OkHttpHelper.getInstance().get(ServiceConstants.IP + "user/profile", params, new CompleteCallback<HisInfoResponse>(HisInfoResponse.class,getITopicApplication()) {

            @Override
            public void onComplete(Response okhttpResponse, HisInfoResponse response) {
                // TODO Auto-generated method stub
                if (response.isSuccess()) {
                    UserBean bean = response.getData();
                    if (TextUtils.isEmpty(hisUserID)){
                        hisUserID = bean.getUserid();
                        resetButtonsGetUserid();
                    }
                    hisAvatarString = bean.getAvatar();

                    ImageView avatar = (ImageView) findViewById(R.id.avatar);
                    GlideLoaderUtil.loadImage(PersonalHomeActivity.this,ValueUtil.getQiniuUrlByFileName(hisAvatarString,true),R.drawable.user_photo,avatar);
                    TextView gender_tv = findViewById(R.id.gender_tv);
                    switch (bean.getGender()){
                        case 0:
                            gender_tv.setVisibility(View.GONE);
                            break;
                        case 1:
                            Drawable drawable = getResources().getDrawable(R.drawable.profile_icon_male_m_normal);
                            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                            gender_tv.setCompoundDrawables(drawable, null,null, null);
                            gender_tv.setText("男");
                            gender_tv.setVisibility(View.VISIBLE);
                            break;
                        case 2:
                            drawable = getResources().getDrawable(R.drawable.profile_icon_female_m_normal);
                            drawable.setBounds(0, 0, drawable.getMinimumWidth(),drawable.getMinimumHeight());
                            gender_tv.setCompoundDrawables(drawable, null,null, null);
                            gender_tv.setText("女");
                            gender_tv.setVisibility(View.VISIBLE);
                            break;
                    }

                    TextView age_tv = findViewById(R.id.age_tv);
                    if(bean.getAge() <= 0) age_tv.setVisibility(View.GONE);
                    else {
                        age_tv.setVisibility(View.VISIBLE);
                        age_tv.setText(bean.getAge()+"岁");
                    }

                    TextView city_tv = findViewById(R.id.city_tv);
                    if(TextUtils.isEmpty(bean.getCityname())) city_tv.setVisibility(View.GONE);
                    else {
                        city_tv.setVisibility(View.VISIBLE);
                        city_tv.setText(bean.getCityname());
                    }

                    TextView header_intro_tv = (TextView)findViewById(R.id.header_intro_tv);
                    if (TextUtils.isEmpty(bean.getIntro())){
                        header_intro_tv.setText("尚未填写个人介绍");
                    } else {
                        header_intro_tv.setText(bean.getIntro());
                    }

                    if (gender_tv.getVisibility() == View.GONE && age_tv.getVisibility() == View.GONE &&
                            city_tv.getVisibility() == View.GONE ){
                        findViewById(R.id.tags_ll).setVisibility(View.GONE);
                    }

                } else {
                    TextView header_intro_tv = (TextView)findViewById(R.id.header_intro_tv);
                    header_intro_tv.setText(response.getMessage());
                }
            }
        });
    }

    private void resetButtonsGetUserid(){
        if (hisUserID.equals(getUserID())) {
            //当前看的人是我自己，去掉关注，聊天的条
            findViewById(R.id.chat_ll).setVisibility(View.GONE);
            findViewById(R.id.edit_ll).setOnClickListener(this);
        } else {
            findViewById(R.id.edit_ll).setVisibility(View.GONE);
            findViewById(R.id.chat_ll).setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.chat_ll:
                if (checkLogined()){
                    Intent i = new Intent(PersonalHomeActivity.this, ChatActivity.class);
                    i.putExtra("targetid", hisUserID);
                    i.putExtra("userid", hisUserID);
                    i.putExtra("avatar", hisAvatarString);
                    i.putExtra("name", getIntent().getStringExtra(PersonalHomeActivity.HIS_NAME_KEY));
                    i.putExtra("type", ChatBean.TYPE_CHAT_SINGLE);
                    startActivity(i);
                }
                break;
            case R.id.edit_ll:
                Intent i = new Intent(PersonalHomeActivity.this , MyProfileEditActivity.class);
                startActivity(i);
                break;
            case R.id.avatar:
                ImagePreview.getInstance()
                        .setContext(PersonalHomeActivity.this)
                        .setIndex(0)
                        .setZoomTransitionDuration(400)
                        .setImage(ValueUtil.getQiniuUrlByFileName(hisAvatarString, false))
                        .setEnableDragClose(true)
                        .setLoadStrategy(ImagePreview.LoadStrategy.AlwaysOrigin)
                        .start();
                break;
            case R.id.back_nobg_iv:
            case R.id.back:
                finish();
                break;
            case R.id.more_iv:
            case R.id.more_nobg_iv:
                onMoreClick(view);
                break;
        }
    }

    private void onMoreClick(View v){
        if (hisUserID.equals(getUserID())){
            return;
        }
        final List<String> onlineList = new ArrayList<>();
        onlineList.add("拉入黑名单");
        onlineList.add("举报");
        ArrayAdapter arrayAdapter = new ArrayAdapter<>(PersonalHomeActivity.this, R.layout.popup_list_item, onlineList);
        QMUIListPopup onlinePopup = new QMUIListPopup(PersonalHomeActivity.this, QMUIPopup.DIRECTION_NONE, arrayAdapter);
        onlinePopup.setAnimStyle(QMUIPopup.ANIM_GROW_FROM_CENTER);
        onlinePopup.setPreferredDirection(QMUIPopup.DIRECTION_BOTTOM);
        onlinePopup.create(ValueUtil.dip2px(PersonalHomeActivity.this, 160), ValueUtil.dip2px(PersonalHomeActivity.this, 280), new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                onlinePopup.dismiss();
                switch (i){
                    case 0:
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("to_userid", hisUserID);
                        OkHttpHelper.getInstance().post(ServiceConstants.IP + "user/block", params,this, new CompleteCallback<StringResponse>(StringResponse.class,getITopicApplication()) {

                            @Override
                            public void onComplete(Response okhttpResponse, StringResponse response) {
                                // TODO Auto-generated method stub
                                SureOrCancelDialog followDialog = new SureOrCancelDialog(
                                        PersonalHomeActivity.this, "已将其拉黑，可以在 “我的”-“设置”-“黑名单列表” 中将其移除黑名单", "知道了",
                                        new SureOrCancelDialog.SureButtonClick() {

                                            @Override
                                            public void onSureButtonClick() {
                                                // TODO Auto-generated method stub
                                            }
                                        });
                                followDialog.show();
                            }
                        });
                        break;
                    case 1:
                        Intent intent = new Intent( PersonalHomeActivity.this, WarningReportActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });
        onlinePopup.show(v);
        onlinePopup.hideArrow();
    }


    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        ImmersionBar.with(this).destroy();
        super.onDestroy();
    }

    protected int getStatusBarHeight() {
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object object = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = (Integer) field.get(object);
            return getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            return 0;
        }
    }
}
