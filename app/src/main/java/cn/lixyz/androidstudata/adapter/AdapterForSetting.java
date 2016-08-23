package cn.lixyz.androidstudata.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.lixyz.androidstudata.R;

/**
 * Created by LGB on 2016/5/12.
 */
public class AdapterForSetting extends BaseAdapter {

    private Context mContext;
    private List<String> mList;

    public AdapterForSetting() {

    }

    public AdapterForSetting(Context context, List<String> list) {
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

        View view = LayoutInflater.from(mContext).inflate(R.layout.setting_listview_item, null);
        TextView text = (TextView) view.findViewById(R.id.tv_setting_text);
        ImageView img = (ImageView) view.findViewById(R.id.iv_setting_img);

        text.setText(mList.get(position));
        switch (position){
            case 0:
                img.setImageResource(R.drawable.cache);
                break;
            case 1:
                img.setImageResource(R.drawable.collect);
                break;
            case 2:
                img.setImageResource(R.drawable.feedback);
                break;
            case 3:
                img.setImageResource(R.drawable.update);
                break;
            case 4:
                img.setImageResource(R.drawable.about);
                break;
        }
        return view;
    }
}
