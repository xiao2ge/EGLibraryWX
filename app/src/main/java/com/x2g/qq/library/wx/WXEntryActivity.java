package com.x2g.qq.library.wx;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EGWXManager.getInstance().handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        EGWXManager.getInstance().handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq baseReq) {
        i("baseReq=" + baseReq + ",thread=" + Thread.currentThread().getName());
    }

    @Override
    public void onResp(BaseResp resp) {
        i("baseResp=" + resp + ",thread=" + Thread.currentThread().getName());
        if (isFinishing()) return;

        if (EGWXManager.getInstance().performShareResult(resp.errCode == BaseResp.ErrCode.ERR_OK)) {
            finish();
            return;
        }

        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                t("已发送");
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                t("发送被拒绝");
                break;
            case BaseResp.ErrCode.ERR_SENT_FAILED:
                t("发送失败");
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                t("取消发送");
                break;
            default:
                t("发送返回");
        }
        finish();
    }

    private void t(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void i(String log) {
        if (EGWXManager.getInstance().isDebug())
            Log.i("EGWX", log);
    }
}
