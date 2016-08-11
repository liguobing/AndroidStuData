package cn.lixyz.androidstudata.ui.activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

import cn.lixyz.androidstudata.R;
import cn.lixyz.androidstudata.basis.BasisActivity;

/**
 * “关于”界面
 * Created by LGB on 2016/5/13.
 */
public class AboutActivity extends BasisActivity {

    private TextView tv_version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        tv_version = (TextView) findViewById(R.id.tv_version);

        try {
            tv_version.setText("当前版本：V" + getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
