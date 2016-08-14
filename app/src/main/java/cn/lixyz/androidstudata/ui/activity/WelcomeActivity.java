package cn.lixyz.androidstudata.ui.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.PushService;
import com.mmga.metroloading.MetroLoadingView;

import java.util.List;

import cn.lixyz.androidstudata.R;
import cn.lixyz.androidstudata.basis.BasisActivity;
import cn.lixyz.androidstudata.db.BasisSQLiteOpenHelper;
import cn.lixyz.androidstudata.util.NetWorkUtil;

/**
 * Created by LGB on 2016/5/3.
 */
public class WelcomeActivity extends BasisActivity {

    private MetroLoadingView mLoading;

    private AVQuery<AVObject> androidQuery;
    private AVQuery<AVObject> javaQuery;

    private String javaLastTime, androidLastTime;    //sqlite中最后添加文件的时间

    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase database;

    private boolean javaReady = false;
    private boolean androidReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //全屏显式
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcome);

        //判断网络
        if (!NetWorkUtil.isNetAvailable(this)) {
            javaReady = true;
            androidReady = true;
            handler.sendEmptyMessage(3);
        }

        //保存 Installation，用于推送消息
        AVInstallation.getCurrentInstallation().saveInBackground();
        //点击推送消息，跳转到主界面
        PushService.setDefaultPushCallback(this, MainActivity.class);

        //开始动画
        mLoading = (MetroLoadingView) findViewById(R.id.loading);
        mLoading.start();

        //获取数据库链接
        openHelper = new BasisSQLiteOpenHelper(this, "AndroidStuDB.db", null, 1);
        database = openHelper.getWritableDatabase();

        //按照updateTime排序，然后获取第一条数据，为最后更新的android文件时间，如果数据库为空，则默认为0000-00-00 00:00:00
        Cursor androidCursor = database.rawQuery("select updateTime from Android order by updateTime desc", null);
        androidCursor.moveToFirst();
        if (androidCursor.getCount() > 0) {
            androidLastTime = androidCursor.getString(androidCursor.getColumnIndex("updateTime"));
        } else {
            androidLastTime = "0000-00-00 00:00:00";
        }
        //按照updateTime排序，然后获取第一条数据，为最后更新的java文件时间，如果数据库为空，则默认为0000-00-00 00:00:00
        Cursor javaCursor = database.rawQuery("select updateTime from Java order by updateTime desc", null);
        javaCursor.moveToFirst();
        if (javaCursor.getCount() > 0) {
            javaLastTime = javaCursor.getString(javaCursor.getColumnIndex("updateTime"));
        } else {
            javaLastTime = "0000-00-00 00:00:00";
        }

        //关闭数据库
        androidCursor.close();
        javaCursor.close();
    }

    /**
     * 分两个线程为Android和Java两个数据库添加数据
     * 查找条件：云控件中updateTime大于本地数据库中最大updateTime的
     */
    @Override
    protected void onStart() {
        super.onStart();

        Thread androidThread = new Thread(new Runnable() {
            @Override
            public void run() {
                androidQuery = new AVQuery<>("AndroidFileBean");
                androidQuery.limit(1000);
                androidQuery.whereGreaterThan("updateTime", androidLastTime);   //查找条件：云控件中updateTime大于本地数据库中最大updateTime的
                androidQuery.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {
                        if (e == null) {
                            for (AVObject object : list) {
                                ContentValues cv = new ContentValues();
                                cv.put("FileID", object.getString("FileID").replaceAll("(\r\n|\r|\n|\n\r)", ""));
                                cv.put("FileName", object.getString("FileName").replaceAll("(\r\n|\r|\n|\n\r)", ""));
                                cv.put("FileURL", object.getString("FileURL").replaceAll("(\r\n|\r|\n|\n\r)", ""));
                                cv.put("FileBasisCategory", object.getString("FileBasisCategory").replaceAll("(\r\n|\r|\n|\n\r)", ""));
                                cv.put("FileCategory", object.getString("FileCategory").replaceAll("(\r\n|\r|\n|\n\r)", ""));
                                cv.put("version", object.getString("version").replaceAll("(\r\n|\r|\n|\n\r)", ""));
                                cv.put("FileAuthor", object.getString("FileAuthor").replaceAll("(\r\n|\r|\n|\n\r)", ""));
                                cv.put("FileLink", object.getString("FileLink").replaceAll("(\r\n|\r|\n|\n\r)", ""));
                                cv.put("updateTime", object.getString("updateTime").replaceAll("(\r\n|\r|\n|\n\r)", ""));
                                database.insert("Android", null, cv);
                            }
                            androidReady = true;    //android数据库插入完成之后，发送消息
                            handler.sendEmptyMessage(1);
                        } else {
                            Log.d("TTTT", e.toString());
                        }
                    }
                });


            }
        });
        androidThread.setPriority(5);
        androidThread.start();

        Thread javaThread = new Thread(new Runnable() {
            @Override
            public void run() {
                javaQuery = new AVQuery<>("JavaFileBean");
                javaQuery.whereGreaterThan("updateTime", javaLastTime);
                javaQuery.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {
                        if (e == null) {
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
                                database.insert("Java", null, cv);
                            }
                            javaReady = true;//java数据库插入完成之后，发送消息
                            handler.sendEmptyMessage(2);
                        } else {
                            Log.d("TTTT", e.toString());
                        }
                    }
                });
            }
        });
        javaThread.setPriority(4);
        javaThread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLoading.stop();    //停止动画
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (javaReady && androidReady) {    //java和android数据库都准备好之后，关闭数据库并跳转到主界面
                database.close();
                openHelper.close();
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }
    };
}
