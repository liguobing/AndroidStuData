package cn.lixyz.androidstudata.ui.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import cn.lixyz.androidstudata.R;
import cn.lixyz.androidstudata.adapter.AdapterForSetting;
import cn.lixyz.androidstudata.ui.activity.AboutActivity;
import cn.lixyz.androidstudata.ui.activity.CollectActivity;
import cn.lixyz.androidstudata.ui.activity.FeedbackActivity;
import cn.lixyz.androidstudata.ui.activity.WIFIAutoCacheActivity;

/**
 * Created by LGB on 2016/4/28.
 */
public class SettingFragment extends Fragment {

    private ListView lv_setting;

    private Intent intent;

    private List<String> itemList = new ArrayList<String>();


    private String appDownloadURL, appVersion;

    private int i = 0;


    public static final SettingFragment newInstance()
    {
        SettingFragment fragment = new SettingFragment();
        return fragment ;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AVQuery<AVObject> query = new AVQuery<>("APP_VERSION");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                for (AVObject object : list) {
                    appVersion = object.getString("version");
                    appDownloadURL = object.getString("download_link");
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_about, null);

        lv_setting = (ListView) view.findViewById(R.id.lv_setting);

        itemList.add("WIFI下自动缓存");
        itemList.add("收藏的文章");
        itemList.add("意见反馈");
        itemList.add("更新");
        itemList.add("关于");

        AdapterForSetting adapter = new AdapterForSetting(getActivity(), itemList);

        lv_setting.setAdapter(adapter);

        lv_setting.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        intent = new Intent(getActivity(), WIFIAutoCacheActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        intent = new Intent(getActivity(), CollectActivity.class);
                        startActivity(intent);
                        break;
                    case 2:
                        intent = new Intent(getActivity(), FeedbackActivity.class);
                        startActivity(intent);
                        break;
                    case 3:
//                        getActivity().getDir("update_dir", Context.MODE_WORLD_WRITEABLE);
                        checkUpdate();
                        break;
                    case 4:
                        intent = new Intent(getActivity(), AboutActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });
        return view;
    }

    private void checkUpdate() {
        try {
            PackageManager manager = getActivity().getPackageManager();
            PackageInfo info = manager.getPackageInfo(getActivity().getPackageName(), 0);
            String version = info.versionName;

            if (("" + appVersion).equals(version)) {
                Toast.makeText(getActivity(), "您的版本是最新的的~", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "开始下载", Toast.LENGTH_SHORT).show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        File file = downLoadFile(appDownloadURL);
                        try {
                            String command = "chmod 777 " + file.getAbsolutePath();
                            Runtime runtime = Runtime.getRuntime();
                            Process proc = runtime.exec(command);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        openFile(file);
                    }
                }).start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected File downLoadFile(String httpUrl) {
        // TODO Auto-generated method stub
        final String fileName = "AndroidStuV" + appVersion + ".apk";
        File tmpFile = new File(getActivity().getFilesDir().getParentFile().getAbsolutePath() + "/" + "app_update_dir");
        if (!tmpFile.exists()) {
            tmpFile.mkdir();
        }

        final File file = new File(tmpFile + "/" + fileName);
        try {
            URL url = new URL(httpUrl);
            try {
                HttpURLConnection conn = (HttpURLConnection) url
                        .openConnection();
                InputStream is = conn.getInputStream();
                FileOutputStream fos = new FileOutputStream(file);
                byte[] buf = new byte[64];
                int count = 0;
                while ((count = is.read(buf)) != -1) {
                    fos.write(buf, 0, count);
                    fos.flush();
                }

                is.close();
                fos.close();
                conn.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return file;
    }

    //打开APK程序代码
    private void openFile(File file) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        startActivity(intent);
    }
}
