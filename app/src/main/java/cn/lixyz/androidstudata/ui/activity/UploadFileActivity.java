package cn.lixyz.androidstudata.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.ProgressCallback;
import com.avos.avoscloud.SaveCallback;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.lixyz.androidstudata.R;
import cn.lixyz.androidstudata.basis.BasisActivity;

/**
 * Created by LGB on 2016/5/2.
 */
public class UploadFileActivity extends BasisActivity {

    private ImageView iv_img;
    private EditText et_file_name, et_version, et_author, et_link, alert_editText;
    private Button bt_select_file, bt_upload_file;
    private ProgressBar pb_upload_progress;
    private Spinner sp_basis_category, sp_category;
    private RadioGroup rg_category;
    private RadioButton rb_android, rb_java;

    private String javaAndroid;    //上传的文件是java还是android

    private String localFilePath, localFileName;  //需要上传的本地文件路径和文件名
    private List<String> basisCategoryList;//基础分类Spinner数据源
    private List<String> categoryList;//分类Spinner数据源

    private AVQuery query;

    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_file);

        initView(); //初始化组件

        basisCategoryList = new ArrayList<String>();    //实例化基础分类Spinner数据源
        categoryList = new ArrayList<String>();     //实例化分类Spinner数据源

        //选择上传的文件是Java分类还是Android分类
        rg_category.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == rb_android.getId()) {
                    javaAndroid = "AndroidFileBean";
                } else {
                    javaAndroid = "JavaFileBean";
                }

//                query = new AVQuery(javaAndroid);
//                query.findInBackground(new FindCallback<AVObject>() {
//                    @Override
//                    public void done(List<AVObject> list, AVException e) {
//                        basisCategoryList.clear();
//                        for (AVObject object : list) {
//                            if (!basisCategoryList.contains(object.getString("FileBasisCategory"))) {
//                                basisCategoryList.add(object.getString("FileBasisCategory"));
//                            }
//                        }
//                        basisCategoryList.add("基础");
//                        basisCategoryList.add("进阶");
//                        basisCategoryList.add("专题");
//                        adapter = new ArrayAdapter<String>(UploadFileActivity.this, android.R.layout.simple_spinner_item, basisCategoryList);
//                        sp_basis_category.setAdapter(adapter);
//                    }
//                });
                basisCategoryList.add("基础");
                basisCategoryList.add("进阶");
                basisCategoryList.add("专题");
                adapter = new ArrayAdapter<String>(UploadFileActivity.this, android.R.layout.simple_spinner_item, basisCategoryList);
                sp_basis_category.setAdapter(adapter);
            }
        });


        /**
         * 基础分类Spinner的Item选中事件，如果选中的是“添加新分类”，则填出对话框，提示用户新增一个分类，然后将用户新增的分类添加到数据源中并设置adapter
         * 如果用户选择的不是最后一样，那么根据用户用户选择的基础分类查找到详细分类，并为详细分类的Spinner设置adapter
         */
        sp_basis_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                if ("添加新基础分类".equals(basisCategoryList.get(position))) {
//                    final EditText et = new EditText(UploadFileActivity.this);
//                    Dialog alertDialog = new AlertDialog.Builder(UploadFileActivity.this).setView(et).setTitle("添加新的基础分类")
//                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    basisCategoryList.add(0, et.getText().toString());
//                                    adapter = new ArrayAdapter<>(UploadFileActivity.this, android.R.layout.simple_list_item_1, basisCategoryList);
//                                    sp_basis_category.setSelection(basisCategoryList.size() - 1, true);
//                                    sp_basis_category.setAdapter(adapter);
//                                }
//                            }).create();
//                    alertDialog.show();
//                } else {
                AVQuery query = new AVQuery(javaAndroid);
                query.whereEqualTo("FileBasisCategory", basisCategoryList.get(position));
                query.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {
                        categoryList.clear();
                        for (AVObject object : list) {
                            if (!categoryList.contains(object.getString("FileCategory"))) {
                                categoryList.add(object.getString("FileCategory"));
                            }
                        }
                        categoryList.add("添加新详细分类");
                        adapter = new ArrayAdapter<String>(UploadFileActivity.this, android.R.layout.simple_spinner_item, categoryList);
                        sp_category.setAdapter(adapter);
                    }
                });
//                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        /**
         * 为详细分类Spinner设置Item选中事件，如果数据源是“添加新详细分类”，那么弹出对话框让用户创建新的详细分类
         */
        sp_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if ("添加新详细分类".equals(categoryList.get(position))) {
                    final EditText et = new EditText(UploadFileActivity.this);
                    Dialog alertDialog = new AlertDialog.Builder(UploadFileActivity.this).setView(et).setTitle("添加新的详细分类")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    categoryList.add(0, et.getText().toString());
                                    adapter = new ArrayAdapter<>(UploadFileActivity.this, android.R.layout.simple_list_item_1, categoryList);
                                    sp_category.setSelection(categoryList.size() - 1, true);
                                    sp_category.setAdapter(adapter);
                                }
                            }).create();
                    alertDialog.show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        //点击按钮，选择文件
        bt_select_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("text/html");
                startActivityForResult(intent, 1);
            }
        });

        //点击按钮，开始上传
        bt_upload_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //构建AVFile
                    final AVFile avFile = AVFile.withAbsoluteLocalPath(localFileName, localFilePath);
                    //上传
                    avFile.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e == null) {    //上传成功
                                AVObject avObject = new AVObject(javaAndroid);   //构建AVObject对象
                                avObject.put("FileID", avFile.getObjectId());   //存入文件ID
                                avObject.put("FileName", et_file_name.getText().toString());//存入文件标题
                                avObject.put("FileURL", avFile.getUrl());//存入文件URL
                                avObject.put("FileBasisCategory", basisCategoryList.get(sp_basis_category.getSelectedItemPosition()));
                                avObject.put("FileCategory", categoryList.get(sp_category.getSelectedItemPosition()));
                                if (et_version.getText().toString().length() == 0) {  //如果不填版本号，则默认为1
                                    avObject.put("version", "1");
                                } else {
                                    avObject.put("version", et_version.getText().toString());
                                }
                                if (et_author.getText().toString().length() == 0) {//如果不填作者，默认未知
                                    avObject.put("FileAuthor", "未知");
                                } else {
                                    avObject.put("FileAuthor", et_author.getText().toString());
                                }
                                if (et_link.getText().toString().length() == 0) {//如果不填原文链接，默认未知
                                    avObject.put("FileLink", "未知");
                                } else {
                                    avObject.put("FileLink", et_link.getText().toString());
                                }
                                avObject.put("updateTime", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                                avObject.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(AVException e) {
                                        if (e == null) {
                                            Toast.makeText(UploadFileActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
                                            //上传成功之后，将图标改回并清空输入框
                                            iv_img.setImageResource(R.drawable.upload);
                                            et_link.setText("");
                                            et_author.setText("");
                                            et_file_name.setText("");
                                            et_version.setText("");
                                        } else {
                                            Toast.makeText(UploadFileActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(UploadFileActivity.this, "上传失败", Toast.LENGTH_SHORT).show(); //上传失败
                            }
                        }
                    }, new ProgressCallback() {
                        @Override
                        public void done(Integer integer) {
                            pb_upload_progress.setVisibility(View.VISIBLE);
                            pb_upload_progress.setProgress(integer);
                            if (integer == 100) {   //上传完成，隐藏进度条
                                pb_upload_progress.setVisibility(View.GONE);
                            }
                        }
                    });
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 初始化组件
     */
    private void initView() {
        iv_img = (ImageView) findViewById(R.id.iv_img);
        et_file_name = (EditText) findViewById(R.id.et_file_name);
        sp_basis_category = (Spinner) findViewById(R.id.sp_basis_category);
        sp_category = (Spinner) findViewById(R.id.sp_category);
        et_version = (EditText) findViewById(R.id.et_version);
        et_author = (EditText) findViewById(R.id.et_author);
        et_link = (EditText) findViewById(R.id.et_link);
        bt_select_file = (Button) findViewById(R.id.bt_select_file);
        bt_upload_file = (Button) findViewById(R.id.bt_upload_file);
        pb_upload_progress = (ProgressBar) findViewById(R.id.pb_upload_progress);
        rg_category = (RadioGroup) findViewById(R.id.rg_category);
        rb_android = (RadioButton) findViewById(R.id.rb_android);
        rb_java = (RadioButton) findViewById(R.id.rb_java);

        alert_editText = (EditText) findViewById(R.id.alert_editText);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            iv_img.setImageResource(R.drawable.file);   //改变选择图像，表示已经选择了文件
            localFilePath = data.getData().toString().replace("file:///", "/");
            //将字符串分割，数组最后一个为文件名
            String[] tmp = localFilePath.split("/");
            localFileName = tmp[tmp.length - 1];
        }
    }
}
