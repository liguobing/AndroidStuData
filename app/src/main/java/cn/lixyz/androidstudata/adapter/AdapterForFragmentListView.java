package cn.lixyz.androidstudata.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cn.lixyz.androidstudata.R;

/**
 * Created by LGB on 2016/4/28.
 */
public class AdapterForFragmentListView extends BaseAdapter {

    private Context mContext;
    private List<String> mList;

    /**
     * 构造方法
     */
    public AdapterForFragmentListView() {
    }

    public AdapterForFragmentListView(Context context, List<String> list) {
        mContext = context;
        mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.fragment_listview_item, null);
            viewHolder.tv = (TextView) convertView.findViewById(R.id.tv_item_name);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tv.setText(mList.get(position));

        return convertView;
    }

    class ViewHolder {
        public TextView tv;
    }
}
