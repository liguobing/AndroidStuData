package cn.lixyz.androidstudata.basis;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;

import com.avos.avoscloud.AVOSCloud;

/**
 * Created by LGB on 2016/5/3.
 */
public class BasisFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AVOSCloud.initialize(getActivity(), "67SNck7cGavK4yKAqIB3ThaP-gzGzoHsz", "Au0HvDfl8tAFViLQPYmXvIcX");
    }
}
