package cn.lixyz.androidstudata.ui.activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.GetDataCallback;
import com.avos.avoscloud.ProgressCallback;
import com.avos.avoscloud.okhttp.internal.framed.FrameReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.lixyz.androidstudata.R;
import cn.lixyz.androidstudata.adapter.AdapterForListViewMenu;
import cn.lixyz.androidstudata.basis.BasisActivity;
import cn.lixyz.androidstudata.bean.ShowFileBean;
import cn.lixyz.androidstudata.db.BasisSQLiteOpenHelper;

/**
 * Created by LGB on 2016/5/4.
 */
public class ShowContentActivity extends BasisActivity implements AdapterView.OnItemClickListener {

    private WebView wv_show;
    private ListView lv_menu;
    private TextView text;
    private List<String> listMenu = new ArrayList<String>();

    private String fileURL;
    private String language;

    private Intent intent;

    private BasisSQLiteOpenHelper openHelper;
    private SQLiteDatabase database;
    private Cursor cursor;

    private String fileID;

    private ShowFileBean bean;
    private AdapterForListViewMenu adapter;

    private WifiManager wifiManager;

    public static final int TOP_MESSAGE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        text = (TextView) findViewById(R.id.text);

        intent = getIntent();
        fileURL = intent.getStringExtra("FileURL");
        language = intent.getStringExtra("language");
        fileID = intent.getStringExtra("FileID");

        openHelper = new BasisSQLiteOpenHelper(this, "AndroidStuDB.db", null, 1);
        database = openHelper.getWritableDatabase();
        cursor = database.query(true, language, new String[]{"FileName", "FileURL", "FileAuthor", "FileLink", "LocalFileID", "isCollect"}, "FileURL=?", new String[]{fileURL}, null, null, null, null);

        cursor.moveToFirst();

        bean = new ShowFileBean();
        bean.setAuthor(cursor.getString(cursor.getColumnIndex("FileAuthor")));
        bean.setFileLink(cursor.getString(cursor.getColumnIndex("FileLink")));
        bean.setLocalFileID(cursor.getString(cursor.getColumnIndex("LocalFileID")));
        bean.setIsCollect(cursor.getString(cursor.getColumnIndex("isCollect")));

        wv_show = (WebView) findViewById(R.id.wv_show);
        lv_menu = (ListView) findViewById(R.id.lv_menu);

        listMenu.add("收藏");
        listMenu.add("下载");
        listMenu.add("作者");
        listMenu.add("访问源链接");
        listMenu.add("分享");

        adapter = new AdapterForListViewMenu(this, listMenu, bean);

        lv_menu.setAdapter(adapter);

        lv_menu.setOnItemClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        /**
         * 如果LocalFileID为空，则从网络加载，否则从本地加载
         */
        if (bean.getLocalFileID() == null) {
            Toast.makeText(ShowContentActivity.this, "正在从网络为您加载", Toast.LENGTH_SHORT).show();
            wv_show.loadUrl(fileURL);
        } else {
            Toast.makeText(ShowContentActivity.this, "正在从本地为您加载", Toast.LENGTH_SHORT).show();
            wv_show.loadUrl("file:///data/user/0/cn.lixyz.androidstudata/files/" + fileID + ".html");
        }

        /**
         * 显示顶部提示信息
         */
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                    handler.sendEmptyMessage(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        /**
         * 根据设置，判断是否在WIFI下自动缓存文件
         */
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED && bean.getLocalFileID() == null && getSharedPreferences("config", MODE_PRIVATE).getBoolean("wifi_cache_status", false)) {
            Toast.makeText(this, "您设置了WIFI下自动缓存，已经为您下载文件到本地，下次阅读本文将为您从本地加载", Toast.LENGTH_SHORT).show();
            downloadFile();
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 菜单点击事件
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                ContentValues collectContentValues = new ContentValues();
                if (bean.getIsCollect() == null) {
                    collectContentValues.put("isCollect", "1");
                    database.update(language, collectContentValues, "fileID=?", new String[]{fileID});
                    collectContentValues.clear();
                    bean.setIsCollect("collected");
                } else {
                    collectContentValues.putNull("isCollect");
                    database.update(language, collectContentValues, "fileID=?", new String[]{fileID});
                    collectContentValues.clear();
                    bean.setIsCollect(null);
                }
                adapter.notifyDataSetChanged();
                break;
            case 1:
                if (bean.getLocalFileID() == null) {
                    downloadFile();
                } else {
                    ContentValues downloadContentValues = new ContentValues();
                    downloadContentValues.putNull("LocalFileID");
                    database.update(language, downloadContentValues, "fileID=?", new String[]{fileID});
                    downloadContentValues.clear();
                    File file = new File(getFilesDir() + "/" + fileID + ".html");
                    file.delete();
                    bean.setLocalFileID(null);
                    adapter.notifyDataSetChanged();
                }

                break;
            case 2:
                break;
            case 3:
                if ("未知".equals(bean.getFileLink())) {
                    Toast.makeText(this, "没有找到作者地址，如果您是作者，请联系我，29418290@qq.com", Toast.LENGTH_SHORT).show();
                } else {
                    wv_show.loadUrl(bean.getFileLink());
                }
                break;
            case 4:
                Intent intent = new Intent(this, ShareActivity.class);
                intent.putExtra("FileName", cursor.getString(cursor.getColumnIndex("FileName")));
                intent.putExtra("FileURL", fileURL);
                startActivity(intent);
                break;
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == TOP_MESSAGE) {
                text.setVisibility(View.GONE);
            }
        }
    };


    private void downloadFile() {
        AVFile avFile = new AVFile(fileID, fileURL, new HashMap<String, Object>());
        avFile.getDataInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] bytes, AVException e) {
                try {
                    FileOutputStream fos = openFileOutput(fileID + ".html", MODE_PRIVATE);
                    fos.write(bytes);
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
        }, new ProgressCallback() {
            @Override
            public void done(Integer integer) {
                if (integer == 100) {
                    ContentValues downloadContentValues = new ContentValues();
                    downloadContentValues.put("LocalFileID", fileID);
                    database.update(language, downloadContentValues, "fileID=?", new String[]{fileID});
                    downloadContentValues.clear();
                    bean.setLocalFileID("1");
                    adapter.notifyDataSetChanged();
                    Toast.makeText(ShowContentActivity.this, "下载完成", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
