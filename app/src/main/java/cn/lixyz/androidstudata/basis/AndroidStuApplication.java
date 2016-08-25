package cn.lixyz.androidstudata.basis;

import android.app.Application;

import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.PushService;

import cn.lixyz.androidstudata.ui.activity.MainActivity;

/**
 * Created by LGB on 2016/8/25.
 */
public class AndroidStuApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AVOSCloud.initialize(this, "Au0HvDfl8tAFViLQPYmXvIcX-gzGzoHsz", "67SNck7cGavK4yKAqIB3ThaP");
        //保存 Installation，用于推送消息
        AVInstallation.getCurrentInstallation().saveInBackground();

        //点击推送消息，跳转到主界面
        PushService.setDefaultPushCallback(this, MainActivity.class);
    }
}
