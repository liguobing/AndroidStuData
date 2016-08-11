package cn.lixyz.androidstudata.ui.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.lixyz.androidstudata.R;
import cn.lixyz.androidstudata.adapter.AdapterForFragmentListView;
import cn.lixyz.androidstudata.basis.BasisActivity;
import cn.lixyz.androidstudata.db.BasisSQLiteOpenHelper;

/**
 * Created by LGB on 2016/5/4.
 */
public class CategoryActivity extends BasisActivity implements AdapterView.OnItemClickListener {

    private ListView FileNameListView;
    private TextView tv_content_name;

    private String category, basisCategory, language;

    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase database;

    private List<String> fileNameList = new ArrayList<String>();

    private AdapterForFragmentListView adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        FileNameListView = (ListView) findViewById(R.id.lv_file_name);
        tv_content_name = (TextView) findViewById(R.id.tv_content_name);

        Intent intent = getIntent();
        category = intent.getStringExtra("Category");
        basisCategory = intent.getStringExtra("BasisCategory");
        language = intent.getStringExtra("Language");

        tv_content_name.setText(category);

        openHelper = new BasisSQLiteOpenHelper(this, "AndroidStuDB.db", null, 1);
        database = openHelper.getWritableDatabase();

        Cursor cursor = database.query(true, language, null, "FileCategory=? and FileBasisCategory=?", new String[]{category, basisCategory}, null, null, "updateTime", null);
        cursor.moveToFirst();
        do {
            fileNameList.add(cursor.getString(cursor.getColumnIndex("FileName")));
        } while (cursor.moveToNext());
        cursor.close();
    }

    @Override
    protected void onStart() {
        super.onStart();

        adapter = new AdapterForFragmentListView(this, fileNameList);

        FileNameListView.setAdapter(adapter);

        FileNameListView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cursor cursor = database.query(true, language, new String[]{"FileURL", "FileID","LocalFileID"}, "FileCategory=? and FileBasisCategory=? and FileName=?", new String[]{category, basisCategory, fileNameList.get(position)}, null, null, null, null);
        cursor.moveToFirst();
        Intent intent = new Intent(CategoryActivity.this, ShowContentActivity.class);
        intent.putExtra("FileURL", cursor.getString(cursor.getColumnIndex("FileURL")));
        intent.putExtra("language", language);
        intent.putExtra("FileID", cursor.getString(cursor.getColumnIndex("FileID")));
        cursor.close();
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        openHelper.close();
        database.close();
    }
}
