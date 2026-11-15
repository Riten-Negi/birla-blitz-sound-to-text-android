package com.birlablitz.soundtotext;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;

public class MainActivity extends Activity {
    private static final String TAG = "BirlaBlitz";
    private static final int PERMISSION_REQ_CODE = 1001;

    private WebView webView;
    private LocalAssetServer localServer;
    private int localPort = 0;
    private PermissionRequest pendingPermissionRequest = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Start internal loopback asset server
        try {
            localServer = new LocalAssetServer(getAssets());
            localServer.start();
            localPort = localServer.getPort();
            Log.i(TAG, "Local loopback asset server started on port " + localPort);
        } catch (Exception e) {
            Log.e(TAG, "Failed to start local loopback server: " + e.getMessage());
        }

        webView = new WebView(this);
        setContentView(webView);

        configureWebView();
        checkAudioPermission();
        loadApp();
    }

    private void configureWebView() {
        WebSettings ws = webView.getSettings();
        ws.setJavaScriptEnabled(true);
        ws.setDomStorageEnabled(true);
        ws.setAllowFileAccess(true);
        ws.setAllowContentAccess(true);
        ws.setMediaPlaybackRequiresUserGesture(false);

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onPermissionRequest(final PermissionRequest request) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                                pendingPermissionRequest = request;
                                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSION_REQ_CODE);
                                return;
                            }
                        }
                        request.grant(request.getResources());
                    }
                });
            }
        });

        webView.setWebViewClient(new WebViewClient());
    }

    private void loadApp() {
        if (localPort > 0) {
            webView.loadUrl("http://127.0.0.1:" + localPort + "/phone.html");
        } else {
            webView.loadUrl("file:///android_asset/phone.html");
        }
    }

    private void checkAudioPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSION_REQ_CODE);
            }
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

    private static class LocalAssetServer extends Thread {
        private final AssetManager assetManager;
        private ServerSocket serverSocket;
        private volatile boolean running = true;
        private int port = 0;

        public LocalAssetServer(AssetManager assetManager) {
            this.assetManager = assetManager;
        }

        public int getPort() { return port; }

        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(0, 50, InetAddress.getByName("127.0.0.1"));
                port = serverSocket.getLocalPort();
                while (running) {
                    try {
                        Socket socket = serverSocket.accept();
                        handleClient(socket);
                    } catch (Exception ignored) {}
                }
            } catch (Exception e) {
                Log.e(TAG, "Server error: " + e.getMessage());
            }
        }

        private void handleClient(final Socket socket) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        InputStream in = socket.getInputStream();
                        OutputStream out = socket.getOutputStream();
                        byte[] buf = new byte[2048];
                        int read = in.read(buf);
                        if (read <= 0) { socket.close(); return; }
                        String req = new String(buf, 0, read);
                        String[] lines = req.split("\r?\n");
                        if (lines.length == 0) { socket.close(); return; }
                        String[] parts = lines[0].split(" ");
                        if (parts.length < 2) { socket.close(); return; }
                        String path = parts[1].replaceFirst("^/", "");
                        if (path.isEmpty() || path.equals("/")) path = "phone.html";
                        path = URLDecoder.decode(path.split("\?")[0], "UTF-8");

                        InputStream fileIn = assetManager.open(path);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        byte[] fileBuf = new byte[8192];
                        int n;
                        while ((n = fileIn.read(fileBuf)) != -1) baos.write(fileBuf, 0, n);
                        fileIn.close();
                        byte[] data = baos.toByteArray();

                        String mime = "application/octet-stream";
                        if (path.endsWith(".html")) mime = "text/html; charset=UTF-8";
                        else if (path.endsWith(".js")) mime = "application/javascript";
                        else if (path.endsWith(".png")) mime = "image/png";

                        String header = "HTTP/1.1 200 OK\r\nContent-Type: " + mime + "\r\nContent-Length: " + data.length + "\r\nConnection: close\r\n\r\n";
                        out.write(header.getBytes("UTF-8"));
                        out.write(data);
                        out.flush();
                        socket.close();
                    } catch (Exception e) {
                        try { socket.close(); } catch (Exception ignored) {}
                    }
                }
            }).start();
        }
    }
}
