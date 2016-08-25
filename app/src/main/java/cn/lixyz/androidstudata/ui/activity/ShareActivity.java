package cn.lixyz.androidstudata.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;


import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONObject;

import java.util.ArrayList;

import cn.lixyz.androidstudata.R;
import cn.lixyz.androidstudata.sinaweibo.AccessTokenKeeper;
import cn.lixyz.androidstudata.sinaweibo.Constants;
import cn.lixyz.androidstudata.sinaweibo.UsersAPI;
import cn.lixyz.androidstudata.sinaweibo.WeiBo;
import cn.lixyz.androidstudata.tencent.Util;

/**
 * Created by LGB on 2016/5/16.
 */
public class ShareActivity extends Activity implements View.OnClickListener {

    private ImageView iv_share_weibo, iv_share_pengyouquan, iv_share_weixin, iv_share_qqzone, iv_share_qq;

    //微博
    private IWeiboShareAPI mWeiboShareAPI;
    private UsersAPI mUsersAPI;
    private Oauth2AccessToken token;

    //QQ  QQZONE
    public static Tencent mTencent;
    public static String mAppid = "1115329091";
    private static boolean isServerSideLogin = false;
    public static final int SHARE_TO_QQ_TYPE_DEFAULT = 1;

    //微信
    private static final String APP_ID = "wx07fbff1c7c8f3cd0";
    private IWXAPI api;

    private String fileName, fileURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        setTitle("分享到...");

        fileName = getIntent().getStringExtra("FileName");
        fileURL = getIntent().getStringExtra("FileURL");

        //微博
        token = AccessTokenKeeper.readAccessToken(this);
        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, Constants.APP_KEY);
        mWeiboShareAPI.registerApp(); // 将应用注册到微博客户端

        //QQ QQZONE
        mTencent = Tencent.createInstance(mAppid, this);

        //微信
        regToWx();

        initView();

    }

    private void initView() {
        iv_share_weibo = (ImageView) findViewById(R.id.iv_share_weibo);
        iv_share_pengyouquan = (ImageView) findViewById(R.id.iv_share_pengyouquan);
        iv_share_weixin = (ImageView) findViewById(R.id.iv_share_weixin);
        iv_share_qqzone = (ImageView) findViewById(R.id.iv_share_qqzone);
        iv_share_qq = (ImageView) findViewById(R.id.iv_share_qq);
        iv_share_weibo.setOnClickListener(this);
        iv_share_pengyouquan.setOnClickListener(this);
        iv_share_weixin.setOnClickListener(this);
        iv_share_qqzone.setOnClickListener(this);
        iv_share_qq.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_share_weibo:
                shareWebPageToWeiBo();
                break;
            case R.id.iv_share_pengyouquan:
                shareWebPageToWXTimeline();
                break;
            case R.id.iv_share_weixin:
                shareWebPageToWXSession();
                break;
            case R.id.iv_share_qqzone:
                shareWebPageToQQZone();
                break;
            case R.id.iv_share_qq:
                shareWebPageToQQ();
                break;
        }
    }

    /**
     * 分享到QQ空间
     */
    private void shareWebPageToQQZone() {
        Bundle qqZoneParams = new Bundle();
        ArrayList<String> imageUrls = new ArrayList<String>(); //========================
        imageUrls.add("http://ww3.sinaimg.cn/mw1024/98e0fbbdgw1f3xww21z2jj201t01tq2r.jpg");
        qqZoneParams.putString(QzoneShare.SHARE_TO_QQ_TITLE, fileName);//必填
        qqZoneParams.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, "一个分享Android笔记的APP");//选填
        qqZoneParams.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, fileURL);//必填
        qqZoneParams.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);
        mTencent.shareToQzone(ShareActivity.this, qqZoneParams, new BaseUiListener());
    }

    /**
     * 分享到QQ
     */
    private void shareWebPageToQQ() {
        Bundle qqParams = new Bundle();
        qqParams.putString(QQShare.SHARE_TO_QQ_TITLE, fileName);
        qqParams.putString(QQShare.SHARE_TO_QQ_SUMMARY, "一个分享Android笔记的APP");
        qqParams.putString(QQShare.SHARE_TO_QQ_TARGET_URL, fileURL);
        qqParams.putString(QQShare.SHARE_TO_QQ_IMAGE_URL,
                "http://ww3.sinaimg.cn/mw1024/98e0fbbdgw1f3xww21z2jj201t01tq2r.jpg");
        qqParams.putString(QQShare.SHARE_TO_QQ_APP_NAME, "Android学习手册");
        mTencent.shareToQQ(ShareActivity.this, qqParams, new BaseUiListener());
    }

    /**
     * 分享到微博
     */
    private void shareWebPageToWeiBo() {
        token = AccessTokenKeeper.readAccessToken(this);
        if (!token.isSessionValid()) {
            Intent intent = new Intent(this, BindAccountActivity.class);
            startActivity(intent);
        } else {
            WeiBo weibo = new WeiBo(this, fileName, fileURL);
            weibo.send(true, false, true, false, false, false);
            finish();
        }
    }

    /**
     * 分享到朋友圈
     */
    private void shareWebPageToWXTimeline() {
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = fileURL;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = fileName;
        msg.description = "一个记录Android学习笔记的APP";
        Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.mipmap.logo);
        msg.thumbData = cn.lixyz.androidstudata.weixin.Util.bmpToByteArray(thumb, true);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneTimeline;
        api.sendReq(req);
    }

    /**
     * 分享到微信聊天
     */
    private void shareWebPageToWXSession() {
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = fileURL;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = fileName;
        msg.description = "一个记录Android学习笔记的APP";
        Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.mipmap.logo);
        msg.thumbData = cn.lixyz.androidstudata.weixin.Util.bmpToByteArray(thumb, true);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneSession;
        api.sendReq(req);
    }

    /**
     * 注册APP到微信
     */
    private void regToWx() {
        api = WXAPIFactory.createWXAPI(this, APP_ID, true);
        api.registerApp(APP_ID);
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    private class BaseUiListener implements IUiListener {

        @Override
        public void onComplete(Object response) {
            if (null == response) {
                Util.showResultDialog(ShareActivity.this, "返回为空", "登录失败");
                return;
            }
            JSONObject jsonResponse = (JSONObject) response;
            if (null != jsonResponse && jsonResponse.length() == 0) {
                Util.showResultDialog(ShareActivity.this, "返回为空", "登录失败");
                return;
            }
            Util.showResultDialog(ShareActivity.this, response.toString(), "登录成功");
            doComplete((JSONObject) response);
        }

        protected void doComplete(JSONObject values) {
        }

        @Override
        public void onError(UiError e) {
            Util.toastMessage(ShareActivity.this, "onError: " + e.errorDetail);
            Util.dismissDialog();
        }

        @Override
        public void onCancel() {
            Util.toastMessage(ShareActivity.this, "onCancel: ");
            Util.dismissDialog();
            if (isServerSideLogin) {
                isServerSideLogin = false;
            }
        }
    }
}
