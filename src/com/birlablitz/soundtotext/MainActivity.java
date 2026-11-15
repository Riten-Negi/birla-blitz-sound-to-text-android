package com.birlablitz.soundtotext;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.Locale;

public class MainActivity extends Activity implements TextToSpeech.OnInitListener {
    private static final String TAG = "BirlaBlitz";
    private static final int PERMISSION_REQ_CODE = 1001;

    private WebView webView;
    private LocalAssetServer localServer;
    private int localPort = 0;
    private TextToSpeech tts;
    private boolean ttsReady = false;
    private String pendingSpeechText = null;
    private PermissionRequest pendingPermissionRequest = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Native Speech Synthesizer for blind/visually-impaired accessibility
        try {
            tts = new TextToSpeech(this, this);
        } catch (Exception e) {
            Log.e(TAG, "Could not initialize TTS: " + e.getMessage());
        }

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
        ws.setDatabaseEnabled(true);
        ws.setAllowFileAccess(true);
        ws.setAllowContentAccess(true);
        ws.setAllowFileAccessFromFileURLs(true);
        ws.setAllowUniversalAccessFromFileURLs(true);
        ws.setMediaPlaybackRequiresUserGesture(false);

        // Native TextToSpeech accessibility bridge for blind/visually-impaired spoken narrative
        webView.addJavascriptInterface(new WebAppInterface(), "AndroidTTS");

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onPermissionRequest(final PermissionRequest request) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(TAG, "WebView requested permissions: " + java.util.Arrays.toString(request.getResources()));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                                pendingPermissionRequest = request;
                                requestPermissions(new String[]{
                                        Manifest.permission.RECORD_AUDIO,
                                        Manifest.permission.MODIFY_AUDIO_SETTINGS
                                }, PERMISSION_REQ_CODE);
                                return;
                            }
                        }
                        request.grant(request.getResources());
                    }
                });
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("http://127.0.0.1") || url.startsWith("file:///android_asset")) {
                    return false;
                }
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                } catch (Exception e) {
                    Log.w(TAG, "Could not launch external URL: " + url);
                }
                return true;
            }
        });
    }

    private void loadApp() {
        if (localPort > 0) {
            String url = "http://127.0.0.1:" + localPort + "/phone.html";
            Log.i(TAG, "Loading local secure loopback: " + url);
            webView.loadUrl(url);
        } else {
            Log.w(TAG, "Falling back to file:///android_asset/phone.html");
            webView.loadUrl("file:///android_asset/phone.html");
        }
    }

    private void checkAudioPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.MODIFY_AUDIO_SETTINGS
                }, PERMISSION_REQ_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQ_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "Microphone permission granted by user.");
                if (pendingPermissionRequest != null) {
                    try {
                        pendingPermissionRequest.grant(pendingPermissionRequest.getResources());
                    } catch (Exception e) {
                        Log.e(TAG, "Error granting pending permission: " + e.getMessage());
                    }
                    pendingPermissionRequest = null;
                }
                if (webView != null) {
                    webView.post(new Runnable() {
                        @Override
                        public void run() {
                            webView.evaluateJavascript("if (typeof onNativeMicPermissionGranted === 'function') onNativeMicPermissionGranted();", null);
                        }
                    });
                }
            } else {
                if (pendingPermissionRequest != null) {
                    try {
                        pendingPermissionRequest.deny();
                    } catch (Exception ignored) {}
                    pendingPermissionRequest = null;
                }
                Toast.makeText(this, "Microphone permission is required to listen for acoustic signals.", Toast.LENGTH_LONG).show();
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

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            if (tts != null) {
                tts.setLanguage(Locale.US);
                tts.setSpeechRate(0.95f);
                tts.setPitch(1.0f);
            }
            ttsReady = true;
            Log.i(TAG, "Android TextToSpeech engine initialized successfully.");
            if (pendingSpeechText != null && !pendingSpeechText.isEmpty()) {
                final String textToSpeak = pendingSpeechText;
                pendingSpeechText = null;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                tts.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, "BIRLA_TTS");
                            } else {
                                tts.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null);
                            }
                        } catch (Exception ignored) {}
                    }
                });
            }
        } else {
            Log.e(TAG, "TTS initialization failed with code: " + status);
        }
    }

    public class WebAppInterface {
        @android.webkit.JavascriptInterface
        public void speak(final String text) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (text == null || text.isEmpty()) return;
                    if (tts != null && ttsReady) {
                        try {
                            tts.stop();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "BIRLA_TTS");
                            } else {
                                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                            }
                            Log.i(TAG, "Speaking narrative aloud for accessibility: " + text.substring(0, Math.min(40, text.length())));
                        } catch (Exception e) {
                            Log.e(TAG, "TTS speak error: " + e.getMessage());
                        }
                    } else {
                        pendingSpeechText = text;
                        Log.i(TAG, "TTS engine initializing, queued speech.");
                    }
                }
            });
        }

        @android.webkit.JavascriptInterface
        public void stop() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (tts != null) {
                        tts.stop();
                    }
                }
            });
        }

        @android.webkit.JavascriptInterface
        public boolean isAvailable() {
            return ttsReady;
        }

        @android.webkit.JavascriptInterface
        public boolean hasMicPermission() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
            }
            return true;
        }

        @android.webkit.JavascriptInterface
        public void requestMicPermission() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    checkAudioPermission();
                }
            });
        }

        @android.webkit.JavascriptInterface
        public void showToast(final String message) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            try {
                tts.stop();
                tts.shutdown();
            } catch (Exception ignored) {}
        }
        if (localServer != null) {
            localServer.stopServer();
        }
        super.onDestroy();
    }

    /**
     * Minimal zero-dependency loopback HTTP server.
     * Serves APK assets on 127.0.0.1 so WebView treats it as a 100% Secure Context,
     * allowing getUserMedia and WebAudio without requiring HTTPS certificates or Wi-Fi.
     */
    private static class LocalAssetServer extends Thread {
        private final AssetManager assetManager;
        private ServerSocket serverSocket;
        private volatile boolean running = true;
        private int port;

        public LocalAssetServer(AssetManager assetManager) throws Exception {
            this.assetManager = assetManager;
            // Bind to loopback address with ephemeral free port
            this.serverSocket = new ServerSocket(0, 50, InetAddress.getByName("127.0.0.1"));
            this.port = serverSocket.getLocalPort();
        }

        public int getPort() {
            return port;
        }

        public void stopServer() {
            running = false;
            try {
                if (serverSocket != null) {
                    serverSocket.close();
                }
            } catch (Exception ignored) {}
        }

        @Override
        public void run() {
            while (running) {
                try {
                    Socket client = serverSocket.accept();
                    handleClient(client);
                } catch (Exception e) {
                    if (!running) break;
                }
            }
        }

        private void handleClient(final Socket socket) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        InputStream in = socket.getInputStream();
                        OutputStream out = socket.getOutputStream();

                        // Read request line
                        ByteArrayOutputStream lineBuf = new ByteArrayOutputStream();
                        int prev = -1, curr;
                        while ((curr = in.read()) != -1) {
                            if (prev == '\r' && curr == '\n') break;
                            lineBuf.write(curr);
                            prev = curr;
                        }

                        String requestLine = new String(lineBuf.toByteArray()).trim();
                        if (requestLine.isEmpty()) {
                            socket.close();
                            return;
                        }

                        // Drain any remaining HTTP request headers
                        int consecutiveNewlines = 0;
                        while (in.available() > 0) {
                            int b = in.read();
                            if (b == '\n') {
                                consecutiveNewlines++;
                                if (consecutiveNewlines >= 2) break;
                            } else if (b != '\r') {
                                consecutiveNewlines = 0;
                            }
                        }

                        String[] parts = requestLine.split(" ");
                        if (parts.length < 2) {
                            socket.close();
                            return;
                        }

                        String rawPath = parts[1];
                        int qIdx = rawPath.indexOf('?');
                        if (qIdx != -1) {
                            rawPath = rawPath.substring(0, qIdx);
                        }

                        String path = URLDecoder.decode(rawPath, "UTF-8");
                        if (path.startsWith("/")) {
                            path = path.substring(1);
                        }
                        if (path.isEmpty() || path.equals("/")) {
                            path = "phone.html";
                        }

                        try {
                            InputStream assetIn = null;
                            try {
                                assetIn = assetManager.open(path);
                            } catch (Exception e1) {
                                if (path.startsWith("assets/")) {
                                    try {
                                        assetIn = assetManager.open(path.substring(7));
                                    } catch (Exception ignored) {}
                                }
                            }

                            if (assetIn == null) {
                                throw new Exception("Asset not found: " + path);
                            }

                            ByteArrayOutputStream assetBuf = new ByteArrayOutputStream();
                            byte[] chunk = new byte[8192];
                            int readBytes;
                            while ((readBytes = assetIn.read(chunk)) != -1) {
                                assetBuf.write(chunk, 0, readBytes);
                            }
                            assetIn.close();

                            byte[] body = assetBuf.toByteArray();
                            String mime = getMimeType(path);

                            String header = "HTTP/1.1 200 OK\r\n" +
                                    "Content-Type: " + mime + "\r\n" +
                                    "Content-Length: " + body.length + "\r\n" +
                                    "Access-Control-Allow-Origin: *\r\n" +
                                    "Cache-Control: no-cache\r\n" +
                                    "Connection: close\r\n\r\n";

                            out.write(header.getBytes("UTF-8"));
                            out.write(body);
                            out.flush();
                        } catch (Exception notFound) {
                            String resp404 = "HTTP/1.1 404 Not Found\r\n" +
                                    "Content-Type: text/plain\r\n" +
                                    "Content-Length: 9\r\n" +
                                    "Connection: close\r\n\r\nNot Found";
                            out.write(resp404.getBytes("UTF-8"));
                            out.flush();
                        }
                    } catch (Exception ignored) {
                    } finally {
                        try {
                            socket.close();
                        } catch (Exception ignored) {}
                    }
                }
            }).start();
        }

        private String getMimeType(String path) {
            String lower = path.toLowerCase();
            if (lower.endsWith(".html") || lower.endsWith(".htm")) return "text/html; charset=utf-8";
            if (lower.endsWith(".js")) return "application/javascript; charset=utf-8";
            if (lower.endsWith(".css")) return "text/css; charset=utf-8";
            if (lower.endsWith(".png")) return "image/png";
            if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
            if (lower.endsWith(".svg")) return "image/svg+xml";
            if (lower.endsWith(".json")) return "application/json";
            if (lower.endsWith(".wasm")) return "application/wasm";
            return "application/octet-stream";
        }
    }
}
