package com.x2g.qq.library.wx;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXFileObject;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXMusicObject;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.modelmsg.WXVideoObject;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class EGWXManager {

    private static EGWXManager mInstance;

    private Application application;

    private boolean debug;

    private String wxappid;

    private EGWXManager() {

    }

    public static EGWXManager getInstance() {
        if (mInstance == null) {
            mInstance = new EGWXManager();
        }
        return mInstance;
    }

    public void init(Application application, String wxkey, boolean debug) {
        i("-- init --");
        this.application = application;
        this.wxappid = wxkey;
        this.debug = debug;
        regToWx();
    }

    // IWXAPI 是第三方app和微信通信的openApi接口
    private static IWXAPI api;

    private void regToWx() {
        i("-- regToWx --");
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        api = WXAPIFactory.createWXAPI(application, wxappid, debug);
        // 将应用的appId注册到微信
        api.registerApp(wxappid);
        //建议动态监听微信启动广播进行注册到微信
        application.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // 将该app注册到微信
                i("-- regToWx --");
                api.registerApp(wxappid);
            }
        }, new IntentFilter(ConstantsAPI.ACTION_REFRESH_WXAPP));
    }

    private ShareResultListener listener;

    private final static String TRANSACTION_TEXT = "text";
    private final static String TRANSACTION_IMAGE = "image";
    private final static String TRANSACTION_WEBPAGE = "webpage";
    private final static String TRANSACTION_MUSIC = "music";
    private final static String TRANSACTION_VIDEO = "video";
    private final static String TRANSACTION_FILE = "file";

    public boolean shareText(String shareText, String title, String description,
                             ShareType type, ShareResultListener listener) {
        i("shareText");
        this.listener = listener;
        WXTextObject obj = new WXTextObject(shareText);
        WXMediaMessage msg = buildMediaMesage(obj, title, description);
        BaseReq req = buildSendReq(msg, buildTransaction(TRANSACTION_TEXT), getWxShareType(type));
        return api.sendReq(req);
    }

    public boolean shareImage(Bitmap bitmap, String title, String description,
                              ShareType type, ShareResultListener listener) {
        i("shareImage");
        this.listener = listener;
        WXMediaMessage.IMediaObject obj = new WXImageObject(bitmap);
        WXMediaMessage msg = buildMediaMesage(obj, title, description);
        msg.setThumbImage(bitmap);
        BaseReq req = buildSendReq(msg, buildTransaction(TRANSACTION_IMAGE), getWxShareType(type));
        return api.sendReq(req);
    }

    public boolean shareMusic(String url, String title, String description,
                              ShareType type, ShareResultListener listener) {
        i("shareMusic");
        this.listener = listener;
        WXMusicObject obj = new WXMusicObject();
        obj.musicUrl = url;
        WXMediaMessage msg = buildMediaMesage(obj, title, description);
        BaseReq req = buildSendReq(msg, buildTransaction(TRANSACTION_MUSIC), getWxShareType(type));
        return api.sendReq(req);
    }

    public boolean shareVideo(String url, String title, String description,
                              ShareType type, ShareResultListener listener) {
        i("shareVideo");
        this.listener = listener;
        WXVideoObject obj = new WXVideoObject();
        obj.videoUrl = url;
        WXMediaMessage msg = buildMediaMesage(obj, title, description);
        BaseReq req = buildSendReq(msg, buildTransaction(TRANSACTION_VIDEO), getWxShareType(type));
        return api.sendReq(req);
    }

    public boolean shareWebPage(String weburl, String title, String description,
                                ShareType type, ShareResultListener listener) {
        i("shareWebPage");
        this.listener = listener;
        WXWebpageObject obj = new WXWebpageObject(weburl);
        WXMediaMessage msg = buildMediaMesage(obj, title, description);
        BaseReq req = buildSendReq(msg, buildTransaction(TRANSACTION_WEBPAGE), getWxShareType(type));
        return api.sendReq(req);
    }

    public boolean shareFile(String filepath, String title, String description,
                             ShareType type, ShareResultListener listener) {
        i("shareFile");
        this.listener = listener;
        WXFileObject obj = new WXFileObject(filepath);
        WXMediaMessage msg = buildMediaMesage(obj, title, description);
        BaseReq req = buildSendReq(msg, buildTransaction(TRANSACTION_FILE), getWxShareType(type));
        return api.sendReq(req);
    }

    void handleIntent(Intent intent, IWXAPIEventHandler handler) {
        api.handleIntent(intent, handler);
    }

    boolean performShareResult(boolean result) {
        if (listener != null) {
            i("performShareResult: " + result);
            listener.onShareResult(result);
            listener = null;
            return true;
        }
        return false;
    }

    private int getWxShareType(ShareType type) {
        if (type == ShareType.FRIENDS) {
            return SendMessageToWX.Req.WXSceneSession;
        } else if (type == ShareType.FRIENDSCIRCLE) {
            return SendMessageToWX.Req.WXSceneTimeline;
        } else if (type == ShareType.FAVOURITE) {
            return SendMessageToWX.Req.WXSceneFavorite;
        }

        throw new IllegalArgumentException("非法参数: 不识别的ShareType -> " + type.name());
    }

    private BaseReq buildSendReq(WXMediaMessage msg, String transaction, int scene) {
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.message = msg;
        req.transaction = transaction;
        req.scene = scene;
        return req;
    }

    private WXMediaMessage buildMediaMesage(WXMediaMessage.IMediaObject obj, String title, String description) {
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = obj;
        msg.title = title;
        msg.description = description;
        return msg;
    }

    /**
     * @param type text/image/webpage/music/video
     * @return
     */
    private String buildTransaction(String type) {
        return TextUtils.isEmpty(type) ? String.valueOf(System.currentTimeMillis()) : (type + System.currentTimeMillis());
    }

    /**
     * 分享结果回调
     */
    public interface ShareResultListener {
        void onShareResult(boolean result);
    }

    public enum ShareType {
        FRIENDS, FRIENDSCIRCLE, FAVOURITE
    }

    private void i(String log) {
        if (debug)
            Log.i("EGWX", log);
    }

    boolean isDebug() {
        return debug;
    }
}
