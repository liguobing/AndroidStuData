package cn.lixyz.androidstudata.ui.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import cn.lixyz.androidstudata.R;
import cn.lixyz.androidstudata.adapter.AdapterForFragmentListView;
import cn.lixyz.androidstudata.basis.BasisFragment;
import cn.lixyz.androidstudata.db.BasisSQLiteOpenHelper;
import cn.lixyz.androidstudata.ui.activity.CategoryActivity;

/**
 * Created by LGB on 2016/4/28.
 */
public class AndroidFragment extends BasisFragment implements AdapterView.OnItemClickListener {
//    private Context mContext;

    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase database;
    private Cursor androidCursor;

    private List<String> basisList = new ArrayList<String>();
    private List<String> advancedList = new ArrayList<String>();
    private List<String> featuresList = new ArrayList<String>();

    private ListView basisListView;
    private ListView advancedListView;
    private ListView featuresListView;

    private AdapterForFragmentListView basisAdapter;
    private AdapterForFragmentListView advancedAdapter;
    private AdapterForFragmentListView featuresAdapter;

    /**
     * 构造方法
     */
//    public AndroidFragment() {
//    }

//    public AndroidFragment(Context context) {
//        this.mContext = context;
//        openHelper = new BasisSQLiteOpenHelper(getActivity(), "AndroidStuDB.db", null, 1);
//        database = openHelper.getWritableDatabase();
//    }
    public static final AndroidFragment newInstance() {
        AndroidFragment fragment = new AndroidFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        openHelper = new BasisSQLiteOpenHelper(getActivity(), "AndroidStuDB.db", null, 1);
        database = openHelper.getWritableDatabase();
        androidCursor = database.query(true, "Android", null, null, null, null, null, "updateTime", null);
        if (androidCursor.getCount() > 0) {
            androidCursor.moveToFirst();
            do {
                if ("基础".equals(androidCursor.getString(androidCursor.getColumnIndex("FileBasisCategory")))) {
                    if (!basisList.contains(androidCursor.getString(androidCursor.getColumnIndex("FileCategory")))) {
                        basisList.add(androidCursor.getString(androidCursor.getColumnIndex("FileCategory")));
                    }
                } else if ("进阶".equals(androidCursor.getString(androidCursor.getColumnIndex("FileBasisCategory")))) {
                    if (!advancedList.contains(androidCursor.getString(androidCursor.getColumnIndex("FileCategory")))) {
                        advancedList.add(androidCursor.getString(androidCursor.getColumnIndex("FileCategory")));
                    }
                } else if ("专题".equals(androidCursor.getString(androidCursor.getColumnIndex("FileBasisCategory")))) {
                    if (!featuresList.contains(androidCursor.getString(androidCursor.getColumnIndex("FileCategory")))) {
                        featuresList.add(androidCursor.getString(androidCursor.getColumnIndex("FileCategory")));
                    }
                }
            } while (androidCursor.moveToNext());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_android, null);

        basisListView = (ListView) view.findViewById(R.id.lv_android_basis);
        advancedListView = (ListView) view.findViewById(R.id.lv_android_advanced);
        featuresListView = (ListView) view.findViewById(R.id.lv_android_features);

        basisAdapter = new AdapterForFragmentListView(getActivity(), basisList);
        advancedAdapter = new AdapterForFragmentListView(getActivity(), advancedList);
        featuresAdapter = new AdapterForFragmentListView(getActivity(), featuresList);

        setListViewHeight(basisListView, basisAdapter, basisList.size());
        setListViewHeight(advancedListView, advancedAdapter, advancedList.size());
        setListViewHeight(featuresListView, featuresAdapter, featuresList.size());

        basisListView.setAdapter(basisAdapter);
        advancedListView.setAdapter(advancedAdapter);
        featuresListView.setAdapter(featuresAdapter);

        basisListView.setOnItemClickListener(this);
        advancedListView.setOnItemClickListener(this);
        featuresListView.setOnItemClickListener(this);

        return view;
    }


    /**
     * 为放置滑动冲突，将ListView重新测量，使其显示全部不在滚动。
     *
     * @param listView
     * @param adapter
     * @param count
     */
    private void setListViewHeight(ListView listView, BaseAdapter adapter, int count) {
        int totalHeight = 0;
        for (int i = 0; i < count; i++) {
            View listItem = adapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * count);
        listView.setLayoutParams(params);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), CategoryActivity.class);
        intent.putExtra("Language", "Android");
        if (parent.getId() == basisListView.getId()) {
            intent.putExtra("Category", basisList.get(position));
            intent.putExtra("BasisCategory", "基础");
        } else if (parent.getId() == advancedListView.getId()) {
            intent.putExtra("Category", advancedList.get(position));
            intent.putExtra("BasisCategory", "进阶");
        } else if (parent.getId() == featuresListView.getId()) {
            intent.putExtra("Category", featuresList.get(position));
            intent.putExtra("BasisCategory", "专题");
        }

        startActivity(intent);
    }
}
