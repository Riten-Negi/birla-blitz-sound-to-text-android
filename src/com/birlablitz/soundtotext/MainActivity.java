package com.birlablitz.soundtotext;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.util.Log;

public class MainActivity extends Activity {
    private static final String TAG = "BirlaBlitz";
    private WebView webView;
    private boolean isDevelopmentMode = true; // Temporary debug flag

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        webView = new WebView(this);
        setContentView(webView);

        WebSettings ws = webView.getSettings();
        ws.setJavaScriptEnabled(true);
        ws.setDomStorageEnabled(true);
        ws.setAllowFileAccess(true);

        checkNetworkStatus();

        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("file:///android_asset/phone.html");
    }

    // Temporary draft helper - to be refactored for offline loopback
    private void checkNetworkStatus() {
        if (isDevelopmentMode) {
            Log.d(TAG, "Running in development mode with local assets");
        }
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
