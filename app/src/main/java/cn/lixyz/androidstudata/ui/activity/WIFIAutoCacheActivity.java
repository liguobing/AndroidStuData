package cn.lixyz.androidstudata.ui.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import cn.lixyz.androidstudata.R;
import cn.lixyz.androidstudata.basis.BasisActivity;

/**
 * Created by LGB on 2016/5/13.
 */
public class WIFIAutoCacheActivity extends BasisActivity {

    private Switch cache_switch;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private boolean wifi_cache_status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_cache);

        cache_switch = (Switch) findViewById(R.id.cache_switch);

        sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
        wifi_cache_status = sharedPreferences.getBoolean("wifi_cache_status", false);

        cache_switch.setChecked(wifi_cache_status);

        cache_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor = sharedPreferences.edit();
                if (isChecked) {
                    editor.putBoolean("wifi_cache_status", true);
                } else {
                    editor.putBoolean("wifi_cache_status", false);
                }
                editor.commit();
            }
        });
    }
}
