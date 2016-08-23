package cn.lixyz.androidstudata.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.PushService;
import com.orhanobut.logger.Logger;

import java.util.List;

import cn.lixyz.androidstudata.R;
import cn.lixyz.androidstudata.ui.fragment.AndroidFragment;
import cn.lixyz.androidstudata.ui.fragment.JavaFragment;
import cn.lixyz.androidstudata.ui.fragment.SettingFragment;

/**
 * 主界面
 * 包含三个Fragment
 * 点击按钮，切换Fragment
 */

public class MainActivity extends Activity {

    private ImageView bt_android, bt_java, bt_setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //保存 Installation，用于推送消息
        AVInstallation.getCurrentInstallation().saveInBackground();

        //点击推送消息，跳转到主界面
        PushService.setDefaultPushCallback(this, MainActivity.class);

        //将手机信息存入云端，以便查看用户机型分布
        AVQuery<AVObject> query = new AVQuery<>("PhoneInfo");
        query.whereEqualTo("InstallationID", AVInstallation.getCurrentInstallation().getInstallationId());
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if(e == null){
                    if(list.size() == 0){
                        AVObject phoneInfo = new AVObject("PhoneInfo");
                        phoneInfo.put("InstallationID",AVInstallation.getCurrentInstallation().getInstallationId());//存入Installation id
                        phoneInfo.put("PhoneInfo",android.os.Build.MODEL);//存入手机信息
                        phoneInfo.saveInBackground();
                    }
                }else{
                    Logger.e(e.toString(),e);
                }
            }
        });


//       初始化组件
        initView();
//      最先显示一个Fragment
        getFragmentManager().beginTransaction().add(R.id.main_root, AndroidFragment.newInstance(), "Android").commit();

    }


    /**
     * 初始化组件
     */
    private void initView() {
        bt_android = (ImageView) findViewById(R.id.bt_android);
        bt_java = (ImageView) findViewById(R.id.bt_java);
        bt_setting = (ImageView) findViewById(R.id.bt_setting);
    }

    /**
     * 导航栏按钮点击事件
     *
     * @param v
     */
    public void imageOnClick(View v) {
        switch (v.getId()) {
            case R.id.bt_android:
                getFragmentManager().beginTransaction().replace(R.id.main_root, AndroidFragment.newInstance(), "Android").commit();
                bt_java.setImageResource(R.mipmap.java);
                bt_setting.setImageResource(R.mipmap.setting);
                bt_android.setImageResource(R.mipmap.android_s);
                break;
            case R.id.bt_java:
                getFragmentManager().beginTransaction().replace(R.id.main_root, JavaFragment.newInstance(), "Java").commit();
                bt_android.setImageResource(R.mipmap.android);
                bt_setting.setImageResource(R.mipmap.setting);
                bt_java.setImageResource(R.mipmap.java_s);
                break;
            case R.id.bt_setting:
                getFragmentManager().beginTransaction().replace(R.id.main_root, SettingFragment.newInstance(), "Setting").commit();
                bt_android.setImageResource(R.mipmap.android);
                bt_setting.setImageResource(R.mipmap.setting_s);
                bt_java.setImageResource(R.mipmap.java);
                break;
        }
    }

//    按退出键，弹出提示是否退出系统
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            new AlertDialog.Builder(this)
                    .setMessage("确定吗退出？")
                    .setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton("否", null)
                    .show();

        }
        return super.onKeyDown(keyCode, event);
    }
}
