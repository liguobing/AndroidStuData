package cn.lixyz.androidstudata.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.SaveCallback;

import cn.lixyz.androidstudata.R;

/**
 * 意见反馈界面
 * Created by LGB on 2016/5/13.
 */
public class FeedbackActivity extends Activity {

    private EditText et_feedback_contact, et_feedback_content;
    private Button bt_submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        et_feedback_contact = (EditText) findViewById(R.id.et_feedback_contact);
        et_feedback_content = (EditText) findViewById(R.id.et_feedback_content);
        bt_submit = (Button) findViewById(R.id.bt_submit);

        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AVObject object = new AVObject("FeedBack");// 构建对象
                object.put("contact", et_feedback_contact.getText().toString());// 设置名称
                object.put("content", et_feedback_content.getText().toString());// 设置优先级

                object.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        if (e == null) {
                            Toast.makeText(FeedbackActivity.this, "提交成功，感谢您的建议~", Toast.LENGTH_SHORT).show();
                            et_feedback_contact.setText("");
                            et_feedback_content.setText("");
                        } else {
                            Toast.makeText(FeedbackActivity.this, "提交失败，请重试~", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
