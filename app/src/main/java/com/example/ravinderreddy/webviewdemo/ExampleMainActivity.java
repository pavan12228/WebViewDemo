package com.example.ravinderreddy.webviewdemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

public class ExampleMainActivity extends Activity {

    protected WebView mainWebView;
    private ProgressBar mProgress;
    private Context mContext;
    private WebView mWebview;
    private WebView mWebviewPop;
    private RelativeLayout mContainer;

    private String url = "https://devm.pyar.com/";
    private String target_url_prefix = "facebook.com";


    public void onBackPressed(){

        if (mainWebView.isFocused() && mainWebView.canGoBack()) {
            mainWebView.goBack();
        }
        else {
            super.onBackPressed();
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get main webview
        mainWebView = (WebView) findViewById(R.id.webview);

        //Progress Bar
        mProgress = (ProgressBar) findViewById(R.id.progressBar);

        //Cookie manager for the webview
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);

        //Get outer container
        mContainer = (RelativeLayout) findViewById(R.id.webview_frame);

        //Settings
        WebSettings webSettings = mainWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setSupportMultipleWindows(true);

        mainWebView.setWebViewClient(new UriWebViewClient());
        mainWebView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);

        mainWebView.setWebChromeClient(new UriChromeClient());
        mainWebView.loadUrl("https://stackoverflow.com");

        mContext=this.getApplicationContext();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }
    private class UriWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            String host = Uri.parse(url).getHost();
            Log.d("shouldOverride", url);
            if (host.equals(target_url_prefix))
            {
                // This is my web site, so do not override; let my WebView load
                // the page
                if(mWebviewPop!=null)
                {
                    mWebviewPop.setVisibility(View.GONE);
                    mContainer.removeView(mWebviewPop);
                    mWebviewPop=null;
                }
                return false;
            }

            if(host.equals("stackoverflow.com"))
            {
                if(url.equals("https://stackoverflow.com/users/logout")){
                    clearCookies(getApplicationContext());
//                    clearFB();
                }
                return false;
            }
            if(host.contains("www.facebook.com") | host.contains("m.facebook.com") | host.contains("outh")|
                    host.contains("facebook")  | host.contains("www.facebook.com//v2") | host.contains("stackauth.com"))
            {
                return false;
            }

            // Otherwise, the link is not for a page on my site, so launch
            // another Activity that handles URLs
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler,
                                       SslError error) {
            Log.d("onReceivedSslError", "onReceivedSslError");
            //super.onReceivedSslError(view, handler, error);
        }
    }

    private class UriChromeClient extends WebChromeClient {

        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog,
                                      boolean isUserGesture, Message resultMsg) {
            Log.d("ExampleMain","onCreateWindow");
            WebView mWebviewPop = new WebView(mContext);
            mWebviewPop.setVerticalScrollBarEnabled(false);
            mWebviewPop.setHorizontalScrollBarEnabled(false);
            mWebviewPop.setWebViewClient(new UriWebViewClient());
            mWebviewPop.getSettings().setJavaScriptEnabled(true);
            mWebviewPop.getSettings().setSavePassword(false);
            mWebviewPop.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            mContainer.addView(mWebviewPop);
            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
            transport.setWebView(mWebviewPop);
            resultMsg.sendToTarget();


            return true;
        }

        @Override
        public void onCloseWindow(WebView window) {
            Log.d("onCloseWindow", "called");
        }

    }

    public String clearFBcoockies() {
        String facebookCoockies = CookieManager.getInstance().getCookie("https://facebook.com");
        Log.d("ExampleMain", "Cookies for facebook.com:" + facebookCoockies);
        return facebookCoockies;
    }
    public void clearFB() {
        CookieManager cookieManager=CookieManager.getInstance();
        cookieManager.setCookie(clearFBcoockies(),"");
    }

    @SuppressWarnings("deprecation")
    public  void clearCookies(Context context)
    {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            Log.d("ExampleMain", "Using clearCookies code for API >=" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else
        {
            Log.d("ExampleMain", "Using clearCookies code for API <" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieSyncManager cookieSyncMngr=CookieSyncManager.createInstance(context);
            cookieSyncMngr.startSync();
            CookieManager cookieManager=CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }
    }
}