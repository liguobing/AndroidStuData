package cn.lixyz.androidstudata.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Window;
import android.view.WindowManager;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.orhanobut.logger.Logger;

import java.util.List;

import cn.lixyz.androidstudata.R;
import cn.lixyz.androidstudata.db.BasisSQLiteOpenHelper;
import cn.lixyz.androidstudata.util.NetWorkUtil;

/**
 * 欢迎界面
 * 进入APP首先进入该界面，本地数据库和云端做对比，更新
 * 如果没有网络，直接进入主界面
 * Created by LGB on 2016/5/3.
 */
public class WelcomeActivity extends Activity {

    private AVQuery<AVObject> androidQuery;
    private AVQuery<AVObject> javaQuery;

    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase database;

    private boolean javaReady = false;
    private boolean androidReady = false;

    private Cursor androidCursor;
    private Cursor javaCursor;

    private String javaUpdateVersion, androidUpdateVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //全屏显式
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcome);

        //云key
        AVOSCloud.initialize(this, "67SNck7cGavK4yKAqIB3ThaP-gzGzoHsz", "Au0HvDfl8tAFViLQPYmXvIcX");

        //获取权限
        if (Build.VERSION.SDK_INT >= 23) {
            int checkPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return;
            }
        }

        //判断网络
        if (!NetWorkUtil.isNetAvailable(this)) {
            javaReady = true;
            androidReady = true;
            handler.sendEmptyMessage(3);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //获取数据库链接
        openHelper = new BasisSQLiteOpenHelper(this, "AndroidStuDB.db", null, 1);
        database = openHelper.getWritableDatabase();

        //获取本地数据库中最新的updateVersion
        androidCursor = database.rawQuery("select max(updateVersion) as updateVersion from Android", null);
        androidCursor.moveToFirst();
        androidUpdateVersion = androidCursor.getString(androidCursor.getColumnIndex("updateVersion"));
        if (androidUpdateVersion == null) {
            androidUpdateVersion = "0";
        }
        javaCursor = database.rawQuery("select max(updateVersion) as updateVersion from Java", null);
        javaCursor.moveToFirst();
        javaUpdateVersion = javaCursor.getString(javaCursor.getColumnIndex("updateVersion"));
        if (javaUpdateVersion == null) {
            javaUpdateVersion = "0";
        }

        androidCursor.close();
        javaCursor.close();

//        创建一个新线程，用来读取云端中的Android条目
        Thread androidThread = new Thread(new Runnable() {
            @Override
            public void run() {
                androidQuery = new AVQuery<>("AndroidFileBean");
                androidQuery.limit(1000);
                androidQuery.whereGreaterThan("updateVersion", androidUpdateVersion);   //查找条件：云控件中updateVersion大于本地数据库中最大updateVersion的
                androidQuery.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {
                        if (e == null) {
                            if (list.size() > 0) {
                                for (AVObject object : list) {
                                    ContentValues cv = new ContentValues();
                                    String tmp = object.getString("FileID");
                                    cv.put("FileID", tmp);
                                    cv.put("FileName", object.getString("FileName"));
                                    cv.put("FileURL", object.getString("FileURL"));
                                    cv.put("FileBasisCategory", object.getString("FileBasisCategory"));
                                    cv.put("FileCategory", object.getString("FileCategory"));
                                    cv.put("version", object.getString("version"));
                                    cv.put("FileAuthor", object.getString ("FileAuthor"));
                                    cv.put("FileLink", object.getString("FileLink"));
                                    cv.put("updateTime", object.getString("updateTime"));
                                    cv.put("updateVersion", object.getString("updateVersion"));
                                    database.insert("Android", null, cv);
                                }
                            }
                            androidReady = true;    //android数据库插入完成之后，发送消息
                            handler.sendEmptyMessage(1);
                        } else {
                            Logger.d(e.toString(), e);
                        }
                    }
                });
            }
        });

//        创建新线程，用于读取云端中的Java条目
        Thread javaThread = new Thread(new Runnable() {
            @Override
            public void run() {
                javaQuery = new AVQuery<>("JavaFileBean");
                javaQuery.limit(1000);
                javaQuery.whereGreaterThan("updateVersion", javaUpdateVersion);
                javaQuery.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {
                        if (e == null) {
                            if (list.size() > 0) {
                                for (AVObject object : list) {
                                    ContentValues cv = new ContentValues();
                                    cv.put("FileID", object.getString("FileID"));
                                    cv.put("FileName", object.getString("FileName"));
                                    cv.put("FileURL", object.getString("FileURL"));
                                    cv.put("FileBasisCategory", object.getString("FileBasisCategory"));
                                    cv.put("FileCategory", object.getString("FileCategory"));
                                    cv.put("version", object.getString("version"));
                                    cv.put("FileAuthor", object.getString("FileAuthor"));
                                    cv.put("FileLink", object.getString("FileLink"));
                                    cv.put("updateTime", object.getString("updateTime"));
                                    cv.put("updateVersion", object.getString("updateVersion"));
                                    database.insert("Java", null, cv);
                                }
                            }
                            javaReady = true;//java数据库插入完成之后，发送消息
                            handler.sendEmptyMessage(2);
                        } else {
                            com.orhanobut.logger.Logger.e(e.toString(), e);
                        }
                    }
                });
            }
        });


//        启动线程
        androidThread.start();
        javaThread.start();
    }

    /**
     * 分两个线程为Android和Java两个数据库添加数据
     * 查找条件：云空间中updateVersion大于本地数据库中最大updateVersion的
     */
    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //关闭数据库
        if (database != null && database.isOpen()) {
            database.close();
        }
        if (openHelper != null) {
            openHelper.close();
        }
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (javaReady && androidReady) {    //java和android数据库都准备好之后，跳转到主界面
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }
    };
}

