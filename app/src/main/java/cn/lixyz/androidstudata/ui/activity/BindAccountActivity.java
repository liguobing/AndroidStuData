package cn.lixyz.androidstudata.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import cn.lixyz.androidstudata.R;
import cn.lixyz.androidstudata.basis.BasisActivity;
import cn.lixyz.androidstudata.sinaweibo.AccessTokenKeeper;
import cn.lixyz.androidstudata.sinaweibo.Constants;
import cn.lixyz.androidstudata.sinaweibo.User;
import cn.lixyz.androidstudata.sinaweibo.UsersAPI;


/**
 * 绑定账户界面
 * Created by LGB on 2016/5/13.
 */
public class BindAccountActivity extends BasisActivity implements View.OnClickListener {

    private ImageButton ib_weibo, ib_qq, ib_wechat;
    private TextView tv_weibo_name, tv_exit_weibo, tv_wechat_name, tv_exit_wechat, tv_qq_name, tv_exit_qq;

    //新浪微博
    private AuthInfo mAuthInfo;
    private Oauth2AccessToken mAccessToken;
    private SsoHandler mSsoHandler;
    private UsersAPI mUsersAPI;
    private static final String WEIBO_PREFERENCES_NAME = "com_weibo_sdk_android";
    private static final String KEY_UID = "uid";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_EXPIRES_IN = "expires_in";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";

    //QQ
    private Tencent mTencent;
    private static final String APP_ID = "1105329091";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_account);

        initView();

        mTencent = Tencent.createInstance(APP_ID, this.getApplicationContext());

        //新浪微博
        mAuthInfo = new AuthInfo(this, Constants.APP_KEY,
                Constants.REDIRECT_URL, Constants.SCOPE);
        Oauth2AccessToken token = AccessTokenKeeper.readAccessToken(this);
        if (token.isSessionValid()) {
            mUsersAPI = new UsersAPI(BindAccountActivity.this, Constants.APP_KEY, token);
            long uid = Long.parseLong(token.getUid());
            mUsersAPI.show(uid, mListener);
        } else {
            tv_weibo_name.setText("未登录");
        }
    }

    private void initView() {
        ib_weibo = (ImageButton) findViewById(R.id.ib_weibo);
        ib_qq = (ImageButton) findViewById(R.id.ib_qq);
        ib_wechat = (ImageButton) findViewById(R.id.ib_wechat);
        tv_exit_weibo = (TextView) findViewById(R.id.tv_exit_weibo);
        tv_exit_wechat = (TextView) findViewById(R.id.tv_exit_wechat);
        tv_exit_qq = (TextView) findViewById(R.id.tv_exit_qq);
        tv_weibo_name = (TextView) findViewById(R.id.tv_weibo_name);
        tv_wechat_name = (TextView) findViewById(R.id.tv_wechat_name);
        tv_qq_name = (TextView) findViewById(R.id.tv_qq_name);

        ib_weibo.setOnClickListener(this);
        ib_qq.setOnClickListener(this);
        ib_wechat.setOnClickListener(this);
        tv_exit_weibo.setOnClickListener(this);
        tv_exit_wechat.setOnClickListener(this);
        tv_exit_qq.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_weibo:
                bindWeiBoAccount();
                break;
            case R.id.ib_qq:
                break;
            case R.id.ib_wechat:
                Toast.makeText(this, "WECHAT", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tv_exit_weibo:
                ib_weibo.setImageResource(R.drawable.weibo_off);
                tv_weibo_name.setText("未登录");
                tv_exit_weibo.setVisibility(View.GONE);
                SharedPreferences pref = this.getSharedPreferences(WEIBO_PREFERENCES_NAME, Context.MODE_APPEND);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString(KEY_UID, "");
                editor.putString(KEY_ACCESS_TOKEN, "");
                editor.putString(KEY_REFRESH_TOKEN, "");
                editor.putLong(KEY_EXPIRES_IN, 0);
                editor.commit();
                break;
            case R.id.tv_exit_wechat:
                Toast.makeText(this, "退出微信", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tv_exit_qq:
                Toast.makeText(this, "退出QQ", Toast.LENGTH_SHORT).show();
                break;
        }
    }


    /**
     * 绑定新浪微博
     */
    private void bindWeiBoAccount() {
        mSsoHandler = new SsoHandler(BindAccountActivity.this, mAuthInfo);
        mSsoHandler.authorize(new AuthListener());
    }

    /**
     * 微博认证授权回调类。
     * 1. SSO 授权时，需要在 {@link #onActivityResult} 中调用 {@link SsoHandler#authorizeCallBack} 后，
     * 该回调才会被执行。
     * 2. 非 SSO 授权时，当授权结束后，该回调就会被执行。
     * 当授权成功后，请保存该 access_token、expires_in、uid 等信息到 SharedPreferences 中。
     */
    class AuthListener implements WeiboAuthListener {
        @Override
        public void onComplete(Bundle values) {

            mAccessToken = Oauth2AccessToken.parseAccessToken(values); // 从 Bundle 中解析 Token
            if (mAccessToken.isSessionValid()) {
                AccessTokenKeeper.writeAccessToken(BindAccountActivity.this, mAccessToken); //保存Token
                mUsersAPI = new UsersAPI(BindAccountActivity.this, Constants.APP_KEY, mAccessToken);
                long uid = Long.parseLong(mAccessToken.getUid());
                mUsersAPI.show(uid, mListener);
            } else {
                // 当您注册的应用程序签名不正确时，就会收到错误Code，请确保签名正确
                String code = values.getString("code", "");
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {

        }

        @Override
        public void onCancel() {

        }
    }

    /**
     * 微博 OpenAPI 回调接口。
     */
    private RequestListener mListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                // 调用 User#parse 将JSON串解析成User对象
                User user = User.parse(response);
                setWeiboName(user);
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Log.d("TTTT","~~~~~~~" + e.toString());
            Toast.makeText(BindAccountActivity.this,e.toString(),Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * 显示并设置微博名
     *
     * @param user
     */
    private void setWeiboName(User user) {
        tv_weibo_name.setText(user.screen_name);
        tv_exit_weibo.setVisibility(View.VISIBLE);
        ib_weibo.setImageResource(R.drawable.weibo);
    }

    /**
     * 当 SSO 授权 Activity 退出时，该函数被调用。
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

}
