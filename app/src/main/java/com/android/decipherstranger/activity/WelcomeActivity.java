package com.android.decipherstranger.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.android.decipherstranger.Network.NetworkService;
import com.android.decipherstranger.R;
import com.android.decipherstranger.activity.Base.BaseActivity;
import com.android.decipherstranger.activity.Base.MyApplication;
import com.android.decipherstranger.activity.MainPageActivity.MainPageActivity;
import com.android.decipherstranger.util.ChangeUtils;
import com.android.decipherstranger.util.GlobalMsgUtils;
import com.android.decipherstranger.util.MyStatic;
import com.android.decipherstranger.util.SharedPreferencesUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * へ　　　　　／|
 * 　　/＼7　　　 ∠＿/
 * 　 /　│　　 ／　／
 * 　│　Z ＿,＜　／　　 /`ヽ
 * 　│　　　　　ヽ　　 /　　〉
 * 　 Y　　　　　`　 /　　/
 * 　ｲ●　､　●　　⊂⊃〈　　/
 * 　()　 へ　　　　|　＼〈
 * 　　>ｰ ､_　 ィ　 │ ／／      去吧！
 * 　 / へ　　 /　ﾉ＜| ＼＼        比卡丘~
 * 　 ヽ_ﾉ　　(_／　 │／／           消灭代码BUG
 * 　　7　　　　　　　|／
 * 　　＞―r￣￣`ｰ―＿
 *
 * @author penghaitao
 * @version V1.0
 * @Date 2015/2/10
 * @e-mail 785351408@qq.com
 */
public class WelcomeActivity extends BaseActivity {

    private Handler handler = null;
    private String password = "";
    private Boolean isLogin = false;
    private MyApplication application = null;
    private MediaPlayer welcomeBgm = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        this.getLoginFlag();
        this.welcome();
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                if (isLogin && !password.equals("")) {
                    Intent intent = new Intent(WelcomeActivity.this, MainPageActivity.class);
                    startActivity(intent);
                    tellWebLogin(application.getAccount(), password);
                } else {
                    Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                WelcomeActivity.this.finish();//结束本Activity
            }
        }, 2000);
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.welcomeBgm.release();
        handler.removeCallbacksAndMessages(null);
        handler = null;
        application = null;
        welcomeBgm = null;
    }

    private void welcome() {
        /*如果使用此音效，时间为2s*/
        this.welcomeBgm = MediaPlayer.create(this, R.raw.welcome); //  获取资源
/*        DateFormat df = new SimpleDateFormat("HH");
        int hour = Integer.parseInt(df.format(new Date()));
        Log.v("WELCOME2", "### hour " + hour);
        *//*如果使用此音效，时间为3s*//*
        if (hour >= 5 && hour <= 11){
            this.welcomeBgm = MediaPlayer.create(this, R.raw.morning); //  获取资源
            Log.v("WELCOME3", "### hour " + hour);
        } else if(hour >= 12 && hour <= 18){
            this.welcomeBgm = MediaPlayer.create(this, R.raw.afternoon); //  获取资源
            Log.v("WELCOME4", "### hour " + hour);
        } else {
            this.welcomeBgm = MediaPlayer.create(this, R.raw.evening); //  获取资源
            Log.v("WELCOME5", "### hour " + hour);
        }*/
        this.welcomeBgm.start();
    }

    private void getLoginFlag() {
        application = MyApplication.getInstance();
        this.application.setInvSum(5);
        SharedPreferencesUtils sharedPreferencesUtils = new SharedPreferencesUtils(this, MyStatic.FILENAME_USER);
        this.isLogin = (Boolean) sharedPreferencesUtils.get(MyStatic.USER_LOGIN, false);
        if (isLogin) {
            this.application.setMoveFlag((boolean) sharedPreferencesUtils.get(MyStatic.USER_MOVE, true));
            this.application.setMusicFlag((boolean) sharedPreferencesUtils.get(MyStatic.USER_MUSIC, true));
            this.application.setAccount((String) sharedPreferencesUtils.get(MyStatic.USER_ACCOUNT, ""));
            this.password = (String) sharedPreferencesUtils.get(MyStatic.USER_PASSWORD, "");
            this.application.setName((String) sharedPreferencesUtils.get(MyStatic.USER_NAME, ""));
            this.application.setPortrait(ChangeUtils.toBitmap((String) sharedPreferencesUtils.get(MyStatic.USER_PORTRAIT, "")));
            this.application.setSex((int) sharedPreferencesUtils.get(MyStatic.USER_SEX, 0));
            this.application.setBirth((String) sharedPreferencesUtils.get(MyStatic.USER_BIRTH, ""));
            this.application.setEmail((String) sharedPreferencesUtils.get(MyStatic.USER_EMAIL, ""));
            this.application.setPhone((String) sharedPreferencesUtils.get(MyStatic.USER_PHONE, ""));
            this.application.setSignature((String) sharedPreferencesUtils.get(MyStatic.USER_SIGNATURE, ""));
        }
        sharedPreferencesUtils.recycle();
    }

    private void tellWebLogin(String account, String password) {

        NetworkService.getInstance().closeConnection();
        NetworkService.getInstance().onInit(WelcomeActivity.this);
        NetworkService.getInstance().setupConnection();
        if (NetworkService.getInstance().getIsConnected()) {
            String userInfo = "type" + ":" + Integer.toString(GlobalMsgUtils.msgLogin) + ":" + "account" + ":" + account + ":" + "password" + ":" + password;
            NetworkService.getInstance().sendUpload(userInfo);
        } else {
            NetworkService.getInstance().closeConnection();
            Toast.makeText(WelcomeActivity.this, "服务器连接失败~(≧▽≦)~啦啦啦", Toast.LENGTH_SHORT).show();
            Log.v("Login", "已经执行T（）方法");
        }

    }

}