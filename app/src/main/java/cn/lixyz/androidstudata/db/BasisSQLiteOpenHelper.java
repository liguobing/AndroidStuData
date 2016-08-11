package cn.lixyz.androidstudata.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by LGB on 2016/5/3.
 */
public class BasisSQLiteOpenHelper extends SQLiteOpenHelper {

    public static String CREATE_ANDROID_TABLE = "create table if not exists Android(" +
            "_id integer primary key autoincrement," +
            "FileID text," +    //文件ID
            "FileName text ," + //文件名
            "FileURL text," +   //文件URL
            "FileBasisCategory text," + //文件基本分类
            "FileCategory text," +  //文件分类
            "version text," +   //文本版本号
            "FileAuthor text," +    //作者
            "FileLink text," +  //原文链接
            "isCollect text," +  //是否收藏
            "LocalFileID text," + //本地文件ID(用户载入本地文件和判断是否已经下载)
            "collectTime text," + //收藏时间
            "updateTime text)";   //最后一次更新时间

    public static String CREATE_JAVA_TABLE = "create table if not exists Java(" +
            "_id integer primary key autoincrement," +
            "FileID text," +    //文件ID
            "FileName text ," + //文件名
            "FileURL text," +   //文件URL
            "FileBasisCategory text," + //文件基本分类
            "FileCategory text," +  //文件分类
            "version text," +   //文本版本号
            "FileAuthor text," +    //作者
            "FileLink text," +  //原文链接
            "isCollect text," +  //是否收藏
            "LocalFileID text," + //本地文件ID(用户载入本地文件和判断是否已经下载)
            "collectTime text," + //收藏时间
            "updateTime text)";   //最后一次更新时间


    public BasisSQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_ANDROID_TABLE);
        db.execSQL(CREATE_JAVA_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
