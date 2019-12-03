# EGLibraryWX
微信分享  [![](https://jitpack.io/v/xiao2ge/EGLibraryWX.svg)](https://jitpack.io/#xiao2ge/EGLibraryWX)

## 集成
#### 1.项目的 build.gradle 中添加jitpack
```
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}
```
#### 2.app的build.gradle中添加依赖
```
dependencies {
  implementation 'com.github.xiao2ge:EGLibraryWX:1.0.1'
}
```

## 使用

#### 1.新建一个 Activity 继承 WXEntryActivity
#### 2.初始化
```
EGWXManager.getInstance().init(Application application, String wxkey, boolean debug);
```
#### 3.使用分享
```
// 纯文本
EGWXManager.getInstance().shareText(String shareText, EGWXShareType type, ShareResultListener listener);
// 图片
EGWXManager.getInstance().shareImage(Bitmap bitmap, EGWXShareType type, ShareResultListener listener);
// 音频
EGWXManager.getInstance().shareMusic(String url, String title, String description, Bitmap icon, EGWXShareType type, ShareResultListener listener);
// 视频
EGWXManager.getInstance().shareVideo(String url, String title, String description, Bitmap icon, EGWXShareType type, ShareResultListener listener);
// 网页
EGWXManager.getInstance().shareWebPage(String weburl, String title, String description, Bitmap icon, EGWXShareType type, ShareResultListener listener);
// 链接
EGWXManager.getInstance().shareFile(String filepath, String title, String description, Bitmap icon, EGWXShareType type, ShareResultListener listener);
```

#### EGWXShareType 分享对象
```
FRIENDS : 好友
FRIENDSCIRCLE : 朋友圈
FAVOURITE : 收藏
```

#### ShareResultListener 分享结果
```
// 分享成功 true
// 失败取消等其他状态 false
void onShareResult(boolean result)；
``` 
如果使用的时候传参 listener !=null 执行 onShareResult 方法
如果传 null 执行默认的方法
```
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
```
