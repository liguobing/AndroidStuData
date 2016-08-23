package cn.lixyz.androidstudata.ui.fragment;

import android.app.Fragment;
import android.content.ContentValues;
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
import android.widget.ScrollView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import cn.lixyz.androidstudata.R;
import cn.lixyz.androidstudata.adapter.AdapterForFragmentListView;
import cn.lixyz.androidstudata.db.BasisSQLiteOpenHelper;
import cn.lixyz.androidstudata.ui.activity.CategoryActivity;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * Android文章展示
 * Created by LGB on 2016/4/28.
 */
public class AndroidFragment extends Fragment implements AdapterView.OnItemClickListener {

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

    private PtrClassicFrameLayout mPtrFrame;
    private ScrollView mScrollView;

    private static AndroidFragment fragment;

    public static AndroidFragment newInstance() {
        if(fragment == null){
            fragment = new AndroidFragment();
        }
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        openHelper = new BasisSQLiteOpenHelper(getActivity(), "AndroidStuDB.db", null, 1);
        database = openHelper.getWritableDatabase();

        androidCursor = database.query(true,"Android",new String[]{"FileBasisCategory","FileCategory"},null,null,null,null,"updateTime",null);

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
        androidCursor.close();

        Logger.d("onCreate方法执行了");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {



        final View view = inflater.inflate(R.layout.fragment_android, container, false);
        //下拉刷新
        mScrollView = (ScrollView) view.findViewById(R.id.rotate_header_scroll_view);
        mPtrFrame = (PtrClassicFrameLayout) view.findViewById(R.id.rotate_header_web_view_frame);
        mPtrFrame.setLastUpdateTimeRelateObject(this);
        mPtrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, mScrollView, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                mPtrFrame.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //查找本地数据库中最大的updateVersion
                        Cursor cursor = database.rawQuery("select max(updateVersion) as updateVersion from Android", null);
                        cursor.moveToFirst();
                        String maxUpdateVersion = cursor.getString(cursor.getColumnIndex("updateVersion"));

                        //查找云端中updateVersion大于本地maxUpdateVersion的信息
                        AVQuery<AVObject> query = new AVQuery<>("AndroidFileBean");
                        query.whereGreaterThan("updateVersion", maxUpdateVersion);
                        query.findInBackground(new FindCallback<AVObject>() {
                            @Override
                            public void done(List<AVObject> list, AVException e) {
                                if (e == null) {
                                    if (list.size() > 0) {
                                        for (AVObject object : list) {
                                            //将云端的新内容更新到本地数据库
                                            ContentValues cv = new ContentValues();
                                            cv.put("FileID", object.getString("FileID"));
                                            cv.put("FileName", object.getString("FileName"));
                                            cv.put("FileURL", object.getString("FileURL"));
                                            String fileBasisCategory = object.getString("FileBasisCategory");
                                            cv.put("FileBasisCategory", fileBasisCategory);
                                            String fileCategory = object.getString("FileCategory");
                                            cv.put("FileCategory", fileCategory);
                                            cv.put("version", object.getString("version"));
                                            cv.put("FileAuthor", object.getString ("FileAuthor"));
                                            cv.put("FileLink", object.getString("FileLink"));
                                            cv.put("updateTime", object.getString("updateTime"));
                                            cv.put("updateVersion", object.getString("updateVersion"));
                                            database.insert("Android", null, cv);

                                            //将云端的新内容更新到ListView中
                                            if ("基础".equals(fileBasisCategory)) {
                                                if (!basisList.contains(fileCategory)) {
                                                    basisList.add(fileCategory);
                                                }
                                            } else if ("进阶".equals(fileBasisCategory)) {
                                                if (!advancedList.contains(fileCategory)) {
                                                    advancedList.add(fileCategory);
                                                }
                                            } else if ("专题".equals(fileBasisCategory)) {
                                                if (!featuresList.contains(fileCategory)) {
                                                    featuresList.add(fileCategory);
                                                }
                                            }
                                        }
                                        //重新设置ListView的高度，防止滚动
                                        setListViewHeight(basisListView, basisAdapter, basisList.size());
                                        setListViewHeight(advancedListView, advancedAdapter, advancedList.size());
                                        setListViewHeight(featuresListView, featuresAdapter, featuresList.size());
                                    } else {
                                        Logger.d("云端没有更新");
                                    }
                                } else {
                                    Logger.e(e.toString(),e);
                                }
                            }
                        });
                        mPtrFrame.refreshComplete();
                        basisAdapter.notifyDataSetChanged();
                        advancedAdapter.notifyDataSetChanged();
                        featuresAdapter.notifyDataSetChanged();
                    }
                }, 100);
            }
        });

        //设置下拉刷新的一些属性
        mPtrFrame.setResistance(1.7f);
        mPtrFrame.setRatioOfHeaderHeightToRefresh(1.2f);
        mPtrFrame.setDurationToClose(200);
        mPtrFrame.setDurationToCloseHeader(1000);
        // default is false
        mPtrFrame.setPullToRefresh(false);
        // default is true
        mPtrFrame.setKeepHeaderWhenRefresh(true);
        mPtrFrame.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPtrFrame.autoRefresh();
            }
        }, 100);


        //绑定组件
        basisListView = (ListView) view.findViewById(R.id.lv_android_basis);
        advancedListView = (ListView) view.findViewById(R.id.lv_android_advanced);
        featuresListView = (ListView) view.findViewById(R.id.lv_android_features);

        //实例化adapter
        basisAdapter = new AdapterForFragmentListView(getActivity(), basisList);
        advancedAdapter = new AdapterForFragmentListView(getActivity(), advancedList);
        featuresAdapter = new AdapterForFragmentListView(getActivity(), featuresList);

        //设置ListView的固定高度，防止滚动
        setListViewHeight(basisListView, basisAdapter, basisList.size());
        setListViewHeight(advancedListView, advancedAdapter, advancedList.size());
        setListViewHeight(featuresListView, featuresAdapter, featuresList.size());

        //ListVew设置Adapter
        basisListView.setAdapter(basisAdapter);
        advancedListView.setAdapter(advancedAdapter);
        featuresListView.setAdapter(featuresAdapter);

        //设置Item点击事件
        basisListView.setOnItemClickListener(this);
        advancedListView.setOnItemClickListener(this);
        featuresListView.setOnItemClickListener(this);

        Logger.d("onCreateView方法执行了，basisList.size=" + basisList.size());
        return view;
    }


    protected void setupViews(final PtrClassicFrameLayout ptrFrame) {

    }


    /**
     * 为防止滑动冲突，将ListView重新测量，使其显示全部不再滚动。
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
