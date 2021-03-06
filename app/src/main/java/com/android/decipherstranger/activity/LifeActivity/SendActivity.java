package com.android.decipherstranger.activity.LifeActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.decipherstranger.Network.NetworkService;
import com.android.decipherstranger.R;
import com.android.decipherstranger.activity.Base.BaseActivity;
import com.android.decipherstranger.activity.Base.MyApplication;
import com.android.decipherstranger.util.GlobalMsgUtils;
import com.android.decipherstranger.util.MyStatic;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;

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
 * @version V1.1
 * @Date 2015/7/28 11:29
 * @e-mail 785351408@qq.com
 */
public class SendActivity extends BaseActivity {

    private MyApplication application = null;
    private ProgressDialog progressDialog = null;
    private LifeSendBroadcastReceiver receiver = null;

    private RadioGroup classRadio = null;
    private EditText nameEdit = null;
    private EditText spaceEdit = null;
    private Button timeButton = null;
    private EditText numPeople = null;
    private Button endTime = null;
    private EditText rallySpace = null;
    private Button rallyTime = null;
    private EditText passwordEdit = null;

    private double mLatitude;
    private double mLongtitude;
    private LocationClient mLocationClient;
    private MyLocationListener mLocationListener;

    private int classRadInt = 3;
    private String timeBtnString = "";
    private int timeBtnInt = 0;
    private String endTimeString = "";
    private int endTimeInt = 30000000;
    private String rallyTimeString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_life_send);
        application = MyApplication.getInstance();
        initLocation();
        System.out.println("### 赋值完了 " + mLocationClient);
        this.init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("### 正在start " + mLocationClient);
        if (!mLocationClient.isStarted()) {
            mLocationClient.start();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        super.unregisterReceiver(SendActivity.this.receiver);
        mLocationClient.stop();
        classRadio.removeAllViews();
        receiver = null;
        mLocationClient = null;
        application = null;
        classRadio = null;
        mLocationListener = null;
    }

    private void init() {
        this.progressDialog = new ProgressDialog(this);

        this.classRadio = (RadioGroup) super.findViewById(R.id.classRadio);
        this.nameEdit = (EditText) super.findViewById(R.id.lifeName);
        this.spaceEdit = (EditText) super.findViewById(R.id.lifeSpace);
        this.timeButton = (Button) super.findViewById(R.id.lifeTime);
        this.numPeople = (EditText) super.findViewById(R.id.lifePeople);
        this.endTime = (Button) super.findViewById(R.id.timeEnd);
        this.rallySpace = (EditText) super.findViewById(R.id.rallySpace);
        this.rallyTime = (Button) super.findViewById(R.id.rallyTime);
        this.passwordEdit = (EditText) super.findViewById(R.id.lifePassword);

        this.LifeSendBroadcas();

        /*锁定聚焦到顶部*/
        classRadio.setFocusable(true);
        classRadio.setFocusableInTouchMode(true);
        classRadio.requestFocus();

    }

    private void initLocation() {
        mLocationClient = new LocationClient(this);
        mLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mLocationListener);

        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);
        mLocationClient.setLocOption(option);
    }

    /**
     * 发送数据到服务器
     */
    private void send() {
        if (NetworkService.getInstance().getIsConnected()) {
            String Msg = "type" + ":" + Integer.toString(GlobalMsgUtils.msgSendActivity) + ":" +
                    "account" + ":" + application.getAccount() + ":" + "activity_type" + ":" + classRadInt + ":" + "activity_name" + ":" +
                    nameEdit.getText().toString() + ":" + "activity_place" + ":" + spaceEdit.getText().toString() + ":" + "activity_time" +
                    ":" + timeBtnString + ":" + "people_num" + ":" + Integer.parseInt(numPeople.getText().toString()) + ":" + "end_time" + ":" +
                    endTimeString + ":" + "set_place" + ":" + rallySpace.getText().toString() + ":" + "set_time" + ":" + rallyTimeString + ":" + "activity_password" +
                    ":" + passwordEdit.getText().toString() + ":" + "mLatitude" + ":" + mLatitude + ":" + "mLongtitude" + ":" + mLongtitude;
            Log.v("aaaaa", Msg);
            NetworkService.getInstance().sendUpload(Msg);
        } else {
            NetworkService.getInstance().closeConnection();
            Log.v("发布", "服务器连接失败");
        }
    }

    private boolean check() {
        if (classRadInt == -1) {
            classRadio.requestFocus();
            Toast.makeText(this, "请选择活动类别", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (nameEdit.getText().toString().equals("")) {
            nameEdit.requestFocus();
            Toast.makeText(this, "请输入活动名称", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (spaceEdit.getText().toString().equals("")) {
            spaceEdit.requestFocus();
            Toast.makeText(this, "请输入活动地点", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (timeBtnString.equals("")) {
            classRadio.requestFocus();
            Toast.makeText(this, "请选择活动日期", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (numPeople.getText().toString().equals("")) {
            numPeople.requestFocus();
            Toast.makeText(this, "请输入活动人数", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (endTimeString.equals("")) {
            classRadio.requestFocus();
            Toast.makeText(this, "请选择截止日期", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (rallySpace.getText().toString().equals("")) {
            rallySpace.requestFocus();
            Toast.makeText(this, "请输入集合地点", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (rallyTimeString.equals("")) {
            rallyTime.requestFocus();
            Toast.makeText(this, "请选择集合时间", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (passwordEdit.getText().toString().equals("")) {
            passwordEdit.requestFocus();
            Toast.makeText(this, "请输入活动口令", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void LifeSendOnClick(View view) {
        switch (view.getId()) {
            /*返回*/
            case R.id.life_back_button:
                onBackPressed();
                break;
             /*美食*/
            case R.id.food:
                classRadInt = 1;
                break;
            /*旅行*/
            case R.id.travel:
                classRadInt = 2;
                break;
            /*休闲娱乐*/
            case R.id.others:
                classRadInt = 3;
                break;
            /*活动日期*/
            case R.id.lifeTime:
                Dialog dialog0 = new DatePickerDialog(this,
                        new DatePickerDialog.OnDateSetListener() {
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                month += 1;
                                timeButton.setTextColor(Color.parseColor("#ffa79c88"));
                                timeButton.setText(year + "-" + month + "-" + day);
                                timeBtnString = timeButton.getText().toString();
                                timeBtnInt = (year * 100 + month) * 100 + day;
                            }
                        }, 2015, 9, 22);
                dialog0.show();
                break;
            /*截止日期*/
            case R.id.timeEnd:
                Dialog dialog1 = new DatePickerDialog(this,
                        new DatePickerDialog.OnDateSetListener() {
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                month += 1;
                                endTime.setTextColor(Color.parseColor("#ffa79c88"));
                                endTime.setText(year + "-" + month + "-" + day);
                                endTimeString = endTime.getText().toString();
                                endTimeInt = (year * 100 + month) * 100 + day;
                            }
                        }, 2015, 9, 22);
                dialog1.show();
                break;
            /*集合时间*/
            case R.id.rallyTime:
                Dialog dialog2 = new TimePickerDialog(this,
                        new TimePickerDialog.OnTimeSetListener() {
                            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                                rallyTime.setTextColor(Color.parseColor("#ffa79c88"));
                                rallyTime.setText(hour + " ： " + minute);
                                rallyTimeString = rallyTime.getText().toString();
                            }
                        }, 9, 0, true);
                dialog2.show();
                break;
            /*发布*/
            case R.id.publish:
                if (this.check()) {
                    progressDialog.setMessage("正在提交数据,请稍后...");
                    progressDialog.onStart();
                    progressDialog.show();
                    this.send();
                }
                break;
        }
    }

    private void LifeSendBroadcas() {
        //动态方式注册广播接收者
        this.receiver = new LifeSendBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(MyStatic.LIFE_SEND);
        this.registerReceiver(receiver, filter);
    }

    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            mLatitude = location.getLatitude();
            mLongtitude = location.getLongitude();
        }
    }

    public class LifeSendBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(MyStatic.LIFE_SEND)) {
                if (intent.getStringExtra("reResult").equals("true")) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            onBackPressed();
                            SendActivity.this.finish();
                        }
                    }, 1500);
                }
            }
        }
    }

}
