package cn.lixyz.androidstudata.ui.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import cn.lixyz.androidstudata.R;
import cn.lixyz.androidstudata.adapter.AdapterForCollectListView;
import cn.lixyz.androidstudata.basis.BasisActivity;
import cn.lixyz.androidstudata.bean.CollectFileBean;
import cn.lixyz.androidstudata.db.BasisSQLiteOpenHelper;

/**
 * 收藏界面
 * Created by LGB on 2016/5/13.
 */
public class CollectActivity extends BasisActivity {

    private List<CollectFileBean> list = new ArrayList<CollectFileBean>();

    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect);

        openHelper = new BasisSQLiteOpenHelper(this, "AndroidStuDB.db", null, 1);
        database = openHelper.getWritableDatabase();

    }

    @Override
    protected void onStart() {
        super.onStart();
        list.clear();
        Cursor androidCursor = database.rawQuery("select * from Android where isCollect " +
                " not null", null);
        Cursor javaCursor = database.rawQuery("select * from Java where isCollect " +
                " not null", null);
        if (androidCursor.moveToFirst()) {
            do {
                CollectFileBean bean = new CollectFileBean();
                bean.setFileURL(androidCursor.getString(androidCursor.getColumnIndex("FileURL")));
                bean.setFileName(androidCursor.getString(androidCursor.getColumnIndex("FileName")));
                bean.setFileID(androidCursor.getString(androidCursor.getColumnIndex("FileID")));
                bean.setFileCotegory("Android");
                list.add(bean);
            } while (androidCursor.moveToNext());
        }

        if (javaCursor.moveToFirst()) {
            javaCursor.moveToFirst();
            do {
                CollectFileBean bean = new CollectFileBean();
                bean.setFileURL(javaCursor.getString(javaCursor.getColumnIndex("FileURL")));
                bean.setFileName(javaCursor.getString(javaCursor.getColumnIndex("FileName")));
                bean.setFileID(javaCursor.getString(javaCursor.getColumnIndex("FileID")));
                bean.setFileCotegory("Java");
                list.add(bean);
            } while (javaCursor.moveToNext());
        }
        ListView listView = (ListView) findViewById(R.id.lv_collect);
        AdapterForCollectListView adapter = new AdapterForCollectListView(this, list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(CollectActivity.this, ShowContentActivity.class);
                intent.putExtra("FileURL", list.get(position).getFileURL());
                intent.putExtra("language", list.get(position).getFileCotegory());
                intent.putExtra("FileID", list.get(position).getFileID());
                startActivity(intent);
            }
        });
    }
}
