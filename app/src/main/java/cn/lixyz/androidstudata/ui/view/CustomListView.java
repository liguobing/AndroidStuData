package cn.lixyz.androidstudata.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.AdapterView;
import android.widget.ListView;

import cn.lixyz.androidstudata.R;

/**
 * Created by LGB on 2016/5/10.
 */
public class CustomListView extends ListView {

    public CustomListView(Context context) {
        super(context);
    }

    public CustomListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 重写此方式实现不同行的样式不一样
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            //
            case MotionEvent.ACTION_DOWN:
                int x = (int) ev.getX();
                int y = (int) ev.getY();
                //返回记录数据行数
                int itemnum = pointToPosition(x, y);

                if (itemnum == AdapterView.INVALID_POSITION)
                    break;
                else {
//                    if (itemnum == 0) {
//                        if (itemnum == (getAdapter().getCount() - 1)) {
//                            setSelector(R.drawable.app_list_corner_round); //仅仅一行记录的样式
//                        } else {
//                            setSelector(R.drawable.app_list_corner_round_top); //多行且第一行的样式
//                        }
//                    } else if (itemnum == (getAdapter().getCount() - 1))  //最后一行的样式
//                        setSelector(R.drawable.app_list_corner_round_bottom);
//                    else {
//                        setSelector(R.drawable.app_list_corner_shape);
//                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }

        return super.onInterceptTouchEvent(ev);
    }
}
