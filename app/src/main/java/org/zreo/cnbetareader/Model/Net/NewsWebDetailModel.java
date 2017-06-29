package org.zreo.cnbetareader.Model.Net;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.zreo.cnbetareader.AppConfig;
import org.zreo.cnbetareader.Entitys.NewsEntity;
import org.zreo.cnbetareader.R;
import org.zreo.cnbetareader.Utils.NetTools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;

/**
 * Created by zqh on 2015/8/1  11:29.
 * Email:zqhkey@163.com
 * 新闻WebView数据实现模块
 */
public class NewsWebDetailModel extends WebDetailModel<String, BaseWebHttpModel> {

    private ImageButton button;
    private ProgressDialog dialog;
    private WebView mWebView;
    private NewsEntity mEntity;
    private boolean hascontent;
    private VideoWebChromeClient client = new VideoWebChromeClient();
    private Handler myHandler;
    SharedPreferences pref;

//    private VideoWebChromeClient client = new VideoWebChromeClient();
private String webTemplate = "<!DOCTYPE html><html><head><title></title><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no\"/>" +
        "<link  rel=\"stylesheet\" href=\"file:///android_asset/style.css\" type=\"text/css\"/><style>.title{color: #%s;}%s</style></head>" +
        "<body><div><div class=\"title\">%s</div><div class=\"from\">%s<span style=\"float: right\">%s</span></div><div id=\"introduce\">%s<div class=\"clear\"></div></div><div class=\"content\">%s</div><div class=\"clear foot\">-- The End --</div></div>" +
        "<script>var config = {\"enableImage\":%s,\"enableFlashToHtml5\":%s};" +
        "</script><script src=\"file:///android_asset/loder.js\"></script></body></html>";
    private String night = "body{color:#9bafcb}#introduce{background-color:#262f3d;color:#616d80}.content blockquote{background-color:#262f3d;color:#616d80}";
    private String light = "#introduce{background-color:#F1F1F1;color: #444;}";
    public NewsWebDetailModel(BaseWebHttpModel model) {
        super(model);

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {

    }

    /**
     * 初始化View中的各项参数
     *
     * @param view
     */
    @Override
    public void assumeView(View view) {

    }

    @SuppressLint({"AddJavascriptInterface", "SetJavaScriptEnabled"})
    public void InitView(WebView view, NewsEntity entity,Context context,ImageButton imageButton) {
        pref = getActivity().getSharedPreferences("org.zreo.cnbetareader_preferences", Context.MODE_PRIVATE);
        dialog=new ProgressDialog(context);
        dialog.setMessage("页面加载中,请稍候...");
        button=imageButton;
        mWebView = view;
        mEntity = entity;
        this.myHandler = new Handler();
        WebSettings settings = mWebView.getSettings();
        settings.setSupportZoom(false);
        settings.setAllowFileAccess(true);//启用或禁止WebView访问文件数据
        settings.setPluginState(WebSettings.PluginState.ON_DEMAND);//播放或者Flash相关
        settings.setJavaScriptEnabled(true);//设置支持JavaScript脚本
        settings.setDomStorageEnabled(true);//设置是否启用了DOM storage API。
        if(pref.getBoolean("informationDetail", true)){//读取设置里的软件是否自动加载图片
            settings.setLoadsImagesAutomatically(true); //支持自动加载图片
        }else{
            settings.setLoadsImagesAutomatically(false);//不自动加载图片
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);//webview 远程调试
        }
        /*判断网络类型，优化流量*/
        if (NetTools.isWifiConnected()) {
            settings.setCacheMode(WebSettings.LOAD_DEFAULT);//根据cache-control决定是否从网络上取数据。
        } else {
            settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);//只要本地有，无论是否过期，或者no-cache，都使用缓存中的数据。
        }
        settings.setTextZoom(100);//字体大小
//        mWebView.addJavascriptInterface(new JavascriptInterface(mActivity), "Interface");//回调页面中的方法
        mWebView.setWebChromeClient(client);
        mWebView.setWebViewClient(new MyWebViewClient());
    }

    @Override
    public void LoadData(boolean startup) {
        DataModel.loadWebData(mEntity.getSid());

    }

    @Override
    public void LoadStart() {

    }

    @Override
    public void LoadSuccess(String object) {
//       int titleColor = array.getColor(2,mActivity.getResources().getColor(R.color.toolbarColor));
        if (AppConfig.STANDRA_PATTERN.matcher(object).find()) {
            new AsyncTask<String, String, Boolean>() {
                @Override
                protected Boolean doInBackground(String... strings) {
                    hascontent = BaseWebHttpModel.handleResponceString(mEntity, strings[0], false);
                    return hascontent;
                }

                @Override
                protected void onPostExecute(Boolean hascontent) {
                    if (hascontent) {
                        LoadWebData(mEntity);
                    } else {
                        LoadFialure();
                    }
                }
            }.execute(object);
        } else {
            LoadFialure();
        }

    }

    private void LoadWebData(NewsEntity mNewsItem) {
        String add=light;
        TypedArray array = mActivity.obtainStyledAttributes(new int[]{R.attr.colorPrimary,
                R.attr.colorPrimaryDark, R.attr.titleColor,android.R.attr.windowBackground,
                R.attr.colorAccent
        });
        int titleColor = array.getColor(2, mActivity.getResources().getColor(R.color.blue));
        String colorString = Integer.toHexString(titleColor);
        String data = String.format(Locale.CHINA, webTemplate, colorString.substring(2, colorString.length()),
                add,mNewsItem.getTitle(),mNewsItem.getFrom(),mNewsItem.getInputtime(),mNewsItem.getHometext(),mNewsItem.getContent(), true, true);
        mWebView.loadDataWithBaseURL(AppConfig.BASE_URL, data, "text/html", "utf-8", null);

    }
    @Override
    public void LoadFialure() {

    }

    @Override
    public void LoadFinish() {

    }
    class MyWebViewClient extends WebViewClient {
        private static final String TAG = "WebView ImageLoader";
        private boolean finish = false;

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            finish = false;
            super.onPageStarted(view, url, favicon);
            dialog.show();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse(url));
            mActivity.startActivity(intent);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            System.out.println("MyWebViewClient.onPageFinished");
            super.onPageFinished(view, url);
            finish = true;
            dialog.dismiss();
            button.setVisibility(View.VISIBLE);
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                System.out.println("MyWebViewClient.shouldInterceptRequest(view,url) url = [" + url + "]");
            String prefix = MimeTypeMap.getFileExtensionFromUrl(url);
            if (!TextUtils.isEmpty(prefix)) {
                String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(prefix);
                if (mimeType != null && mimeType.startsWith("image")) {
                    if (finish ) {
                        File image = ImageLoader.getInstance().getDiskCache().get(url);
                        if (image != null) {
                            System.out.println("load Image From disk cache");
                            try {
                                return new WebResourceResponse(mimeType, "UTF-8", new FileInputStream(image));
                            } catch (FileNotFoundException ignored) {
                            }
                        } else {
                            System.out.println("load Image From net");
                        }
                    } else {
                        System.out.println("Load Image Hoder");
                        try {
                            return new WebResourceResponse("image/svg+xml", "UTF-8", mActivity.getAssets().open("image.svg"));
                        } catch (IOException ignored) {
                        }
                    }
                } else {
                    System.out.println("load other resourse");
                }
            }
            return super.shouldInterceptRequest(view, url);
        }
    }
    class VideoWebChromeClient extends WebChromeClient {
        private View myView = null;
        CustomViewCallback myCallback = null;
        private int orientation;
        private int requiredOrientation;

        @Override
        public void onShowCustomView(View view, CustomViewCallback customViewCallback) {
            if (myCallback != null) {
                myCallback.onCustomViewHidden();
                myCallback = null;
                return;
            }
            requiredOrientation = mActivity.getRequestedOrientation();
            orientation = mActivity.getResources().getConfiguration().orientation;
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//            mActivity.getSupportActionBar().hide();
            view.setBackgroundColor(Color.BLACK);
//            onShowHtmlVideoView(view);
            myView = view;
            myCallback = customViewCallback;
        }

        @Override
        public void onHideCustomView() {

            if (myView != null) {

                if (myCallback != null) {
                    myCallback.onCustomViewHidden();
                    myCallback = null;
                }
                mActivity.setRequestedOrientation(orientation);
                mActivity.setRequestedOrientation(requiredOrientation);
//                mActivity.getSupportActionBar().show();
//                onHideHtmlVideoView(myView);
                myView = null;
            }
        }
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && client.myCallback != null) {
            client.onHideCustomView();
            return true;
        }
        return false;
    }
//    private class JavascriptInterface {
//        private Activity mActivity;
//
//        public JavascriptInterface(Activity activity) {
//            this.mActivity = activity;
//        }
//
//        @android.webkit.JavascriptInterface
//        public void showImage(String pos, final String[] imageSrcs) {
//            final int posi;
//            try {
//                posi = Integer.parseInt(pos);
//                myHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        Intent intent = new Intent(mContext, ImageViewActivity.class);
//                        intent.putExtra(ImageViewActivity.IMAGE_URLS, imageSrcs);
//                        intent.putExtra(ImageViewActivity.CURRENT_POS, posi);
//                        mContext.startActivity(intent);
//                    }
//                });
//            } catch (Exception e) {
//                Log.d(getClass().getName(), "Illegal argument");
//            }
//        }
//
//        @android.webkit.JavascriptInterface
//        public void loadSohuVideo(final String hoder_id, final String requestUrl) {
//            myHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    NetKit.getInstance().getClient().get(requestUrl, new JsonHttpResponseHandler() {
//                        @Override
//                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                            try {
//                                mWebView.loadUrl("javascript:Loader.VideoCallBack(\"" + hoder_id + "\",\"" + response.getJSONObject("data").getString("url_high_mp4") + "\",\"" + response.getJSONObject("data").getString("hor_big_pic") + "\")");
//                            } catch (Exception e) {
//                                Toolkit.showCrouton(mActivity, "搜狐视频加载失败", Style.ALERT);
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                            Toolkit.showCrouton(mActivity, "搜狐视频加载失败", Style.ALERT);
//                        }
//                    });
//                }
//            });
//        }
//
//        @android.webkit.JavascriptInterface
//        public void showMessage(final String message, final String type) {
//            myHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    Toolkit.showCrouton(mActivity, message, CroutonStyle.getStyle(type));
//                }
//            });
//        }
//    }
}
