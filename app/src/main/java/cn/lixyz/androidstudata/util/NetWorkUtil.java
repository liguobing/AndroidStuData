package cn.lixyz.androidstudata.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

/**
 * Created by LGB on 2016/5/9.
 */
public class NetWorkUtil {
    public static boolean isNetAvailable(Context context) {
        //获得网络管理器
        ConnectivityManager connM =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connM.getActiveNetworkInfo();//得到网络详情

        if (netInfo == null || !netInfo.isAvailable()) {
            Toast.makeText(context, "无网络", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }
}
