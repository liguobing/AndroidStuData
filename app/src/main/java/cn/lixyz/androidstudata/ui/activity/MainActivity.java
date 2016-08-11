package cn.lixyz.androidstudata.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import cn.lixyz.androidstudata.R;
import cn.lixyz.androidstudata.basis.BasisActivity;
import cn.lixyz.androidstudata.basis.BasisFragment;
import cn.lixyz.androidstudata.ui.fragment.SettingFragment;
import cn.lixyz.androidstudata.ui.fragment.AndroidFragment;
import cn.lixyz.androidstudata.ui.fragment.JavaFragment;

public class MainActivity extends BasisActivity {

    private ImageView bt_android, bt_java, bt_setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

//        getFragmentManager().beginTransaction().add(R.id.main_root, new AndroidFragment(this), "Android").commit();
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
//                getFragmentManager().beginTransaction().replace(R.id.main_root, new AndroidFragment(this), "Android").commit();
                bt_java.setImageResource(R.mipmap.java);
                bt_setting.setImageResource(R.mipmap.setting);
                bt_android.setImageResource(R.mipmap.android_s);
                break;
            case R.id.bt_java:
                getFragmentManager().beginTransaction().replace(R.id.main_root, JavaFragment.newInstance(), "Java").commit();
//                getFragmentManager().beginTransaction().replace(R.id.main_root, new JavaFragment(this), "Java").commit();
                bt_android.setImageResource(R.mipmap.android);
                bt_setting.setImageResource(R.mipmap.setting);
                bt_java.setImageResource(R.mipmap.java_s);
                break;
            case R.id.bt_setting:
                getFragmentManager().beginTransaction().replace(R.id.main_root, SettingFragment.newInstance(), "Setting").commit();
//                getFragmentManager().beginTransaction().replace(R.id.main_root, new SettingFragment(this), "Setting").commit();
                bt_android.setImageResource(R.mipmap.android);
                bt_setting.setImageResource(R.mipmap.setting_s);
                bt_java.setImageResource(R.mipmap.java);
                break;
        }
    }

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
