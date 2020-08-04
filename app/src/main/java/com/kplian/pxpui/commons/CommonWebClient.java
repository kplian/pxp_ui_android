package com.kplian.pxpui.commons;

import android.graphics.Bitmap;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;


public class CommonWebClient extends WebViewClient {
    private static final String TAG = CommonWebClient.class.getSimpleName();
    public static final String SUCCESS_TAG = "success";

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Log.d(TAG, "shouldOverrideUrlLoading: " + url);
        if (url.contains(SUCCESS_TAG)) {
            Log.d(TAG, "shouldOverrideUrlLoading: success");
        }
        return super.shouldOverrideUrlLoading(view, url);
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        Log.d(TAG, "onPageStarted: " + url);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        Log.d(TAG, "onPageFinished: " + url);

        String cookies = CookieManager.getInstance().getCookie(url);
        Log.d(TAG, "All the cookies in a string:" + cookies);
    }

    private void storeCookies(String url, String cookieStr) throws URISyntaxException {
        if (cookieStr != null && !cookieStr.isEmpty()) {
            URI uri = new URI(url);
            List<HttpCookie> cookies = HttpCookie.parse(cookieStr);
            for (HttpCookie cookie : cookies) {
                Log.d("---cookie", cookie.getName());
//                cookieStore.add(uri, cookie); // java.net.CookieStore from a java.net.CookieManager
            }
        }
    }
}