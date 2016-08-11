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
import cn.lixyz.androidstudata.bean.ShowFileBean;

/**
 * Created by LGB on 2016/5/10.
 */
public class AdapterForListViewMenu extends BaseAdapter {

    private Context mContext;
    private List<String> mList;
    private ShowFileBean mBean;

    public AdapterForListViewMenu() {

    }

    public AdapterForListViewMenu(Context context, List<String> list, ShowFileBean bean) {
        mContext = context;
        mList = list;
        mBean = bean;
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

        View view = LayoutInflater.from(mContext).inflate(R.layout.menu_listview_item, null);
        ImageView img = (ImageView) view.findViewById(R.id.iv_menu_img);
        TextView text = (TextView) view.findViewById(R.id.tv_menu_text);


        switch (position) {
            case 0:

                if(mBean.getIsCollect() == null){
                    img.setImageResource(R.drawable.unlike);
                    text.setText("未收藏");
                }else{
                    img.setImageResource(R.drawable.like);
                    text.setText("已收藏");
                }
                break;
            case 1:
                if(mBean.getLocalFileID() == null){
                    img.setImageResource(R.drawable.undownload);
                    text.setText("未下载");
                }else{
                    img.setImageResource(R.drawable.download);
                    text.setText("已下载");
                }
                break;
            case 2:
                img.setImageResource(R.drawable.author);
                if(mBean.getAuthor() == null){
                    text.setText("作者:未知");
                }else{
                    text.setText("作者:" + mBean.getAuthor());
                }
                break;
            case 3:
                img.setImageResource(R.drawable.link);
                text.setText("点击访问作者链接");
                break;
            case 4:
                img.setImageResource(R.drawable.share);
                text.setText("分享");
                break;
        }
        return view;
    }
}
