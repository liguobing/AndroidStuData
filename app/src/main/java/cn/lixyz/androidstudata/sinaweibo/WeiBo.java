package cn.lixyz.androidstudata.sinaweibo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMessage;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.utils.Utility;

import cn.lixyz.androidstudata.R;

/**
 * Created by LGB on 2016/5/16.
 */
public class WeiBo {
    public static final int SHARE_CLIENT = 1;
    public static final int SHARE_ALL_IN_ONE = 2;
    private int mShareType = SHARE_CLIENT;
    private IWeiboShareAPI mWeiboShareAPI = null;

    private Context mContext;
    private String mFileName;
    private String mFileURL;

    public WeiBo(Context context, String fileName, String fileURL) {
        mContext = context;
        mFileName = fileName;
        mFileURL = fileURL;
        // 创建微博分享接口实例
        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(mContext, Constants.APP_KEY);
        mWeiboShareAPI.registerApp();
    }

    public void send(boolean hasText, boolean hasImage, boolean hasWebpage, boolean hasMusic, boolean hasVideo,
                     boolean hasVoice) {
        sendMessage(hasText, hasImage, hasWebpage, hasMusic, hasVideo, hasVoice);
    }


    /**
     * 第三方应用发送请求消息到微博，唤起微博分享界面。
     */
    private void sendMessage(boolean hasText, boolean hasImage, boolean hasWebpage, boolean hasMusic, boolean hasVideo,
                             boolean hasVoice) {

        if (mShareType == SHARE_CLIENT) {
            if (mWeiboShareAPI.isWeiboAppSupportAPI()) {
                int supportApi = mWeiboShareAPI.getWeiboAppSupportAPI();
                if (supportApi >= 10351 /* ApiUtils.BUILD_INT_VER_2_2 */) {
                    sendMultiMessage(hasText, hasImage, hasWebpage, hasMusic, hasVideo, hasVoice);
                } else {
                    sendSingleMessage(hasText, hasImage, hasWebpage, hasMusic,
                            hasVideo/* , hasVoice */);
                }
            } else {
                Toast.makeText(mContext, "微博客户端不支持 SDK 分享或微博客户端未安装或微博客户端是非官方版本。", Toast.LENGTH_SHORT).show();
            }
        } else if (mShareType == SHARE_ALL_IN_ONE) {
            sendMultiMessage(hasText, hasImage, hasWebpage, hasMusic, hasVideo, hasVoice);
        }
    }

    private void sendMultiMessage(boolean hasText, boolean hasImage, boolean hasWebpage, boolean hasMusic,
                                  boolean hasVideo, boolean hasVoice) {

        // 1. 初始化微博的分享消息
        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
        if (hasText) {
            weiboMessage.textObject = getTextObj();
        }
        if (hasWebpage) {
            weiboMessage.mediaObject = getWebpageObj();
        }

        // 2. 初始化从第三方到微博的消息请求
        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        // 用transaction唯一标识一个请求
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = weiboMessage;

        // 3. 发送请求消息到微博，唤起微博分享界面
        if (mShareType == SHARE_CLIENT) {
            mWeiboShareAPI.sendRequest((Activity) mContext, request);
        }
    }

    private void sendSingleMessage(boolean hasText, boolean hasImage, boolean hasWebpage, boolean hasMusic,
                                   boolean hasVideo/* , boolean hasVoice */) {

        // 1. 初始化微博的分享消息
        // 用户可以分享文本、图片、网页、音乐、视频中的一种
        WeiboMessage weiboMessage = new WeiboMessage();
        if (hasText) {
            weiboMessage.mediaObject = getTextObj();
        }
        // 用户可以分享其它媒体资源（网页、音乐、视频、声音中的一种）
        if (hasWebpage) {
            weiboMessage.mediaObject = getWebpageObj();
        }

		/*
         * if (hasVoice) { weiboMessage.mediaObject = getVoiceObj(); }
		 */

        // 2. 初始化从第三方到微博的消息请求
        SendMessageToWeiboRequest request = new SendMessageToWeiboRequest();
        // 用transaction唯一标识一个请求
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.message = weiboMessage;

        // 3. 发送请求消息到微博，唤起微博分享界面
        mWeiboShareAPI.sendRequest((Activity) mContext, request);
    }

    /**
     * 创建文本消息对象。
     *
     * @return 文本消息对象。
     */
    private TextObject getTextObj() {
        TextObject textObject = new TextObject();
        textObject.text = "我正在读 " + mFileName;
        return textObject;
    }

    /**
     * 创建多媒体（网页）消息对象。
     *
     * @return 多媒体（网页）消息对象。
     */

    private WBShareItemView mShareWebPageView;

    private WebpageObject getWebpageObj() {
        WebpageObject mediaObject = new WebpageObject();
        mediaObject.identify = Utility.generateGUID();
        mediaObject.title = mFileName;
        mediaObject.description = "";

        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.logo);
        // 设置 Bitmap 类型的图片到视频对象里 设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
        mediaObject.setThumbImage(bitmap);
        mediaObject.actionUrl = mFileURL;
        mediaObject.defaultText = "我正在读 " + mFileName;
        return mediaObject;
    }
}
