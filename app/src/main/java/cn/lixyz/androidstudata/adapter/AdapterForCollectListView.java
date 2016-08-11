package cn.lixyz.androidstudata.adapter;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.lixyz.androidstudata.R;
import cn.lixyz.androidstudata.bean.CollectFileBean;

/**
 * Created by LGB on 2016/5/18.
 */
public class AdapterForCollectListView extends BaseAdapter {

    private Context mContext;
    private List<CollectFileBean> mList;

    public AdapterForCollectListView() {

    }

    public AdapterForCollectListView(Context context, List<CollectFileBean> list) {
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

        ViewHolder vh = null;

        if (convertView == null) {
            vh = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.collect_item, null);
            vh.image = (ImageView) convertView.findViewById(R.id.iv_collect_category);
            vh.text = (TextView) convertView.findViewById(R.id.tv_collect_name);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        if (mList.get(position).getFileCotegory().equals("Android")) {
            vh.image.setImageResource(R.drawable.collect_android);
        } else if (mList.get(position).getFileCotegory().equals("Java")) {
            vh.image.setImageResource(R.drawable.collect_java);
        }
        vh.text.setText(mList.get(position).getFileName());
        return convertView;
    }

    class ViewHolder {
        public ImageView image;
        public TextView text;
    }
}
