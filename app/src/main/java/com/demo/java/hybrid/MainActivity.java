package com.demo.java.hybrid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.net.URL;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements AlloGesture.Listener {

    WebView web;

    @Override
    protected void onSaveInstanceState (Bundle outState)
    {
        Allo.i ("onSaveInstanceState " + getClass ());
        super.onSaveInstanceState (outState);
    }

    @Override
    protected void onRestoreInstanceState (Bundle savedInstanceState)
    {
        Allo.i ("onRestoreInstanceState " + getClass ());
        super.onRestoreInstanceState (savedInstanceState);
    }

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        Allo.i ("onCreate " + getClass ());
        super.onCreate (savedInstanceState);

        try
        {
            // 전체화면 구성을 위함
            requestWindowFeature (Window.FEATURE_NO_TITLE);
            requestWindowFeature (Window.FEATURE_ACTION_BAR_OVERLAY);
            getWindow ().requestFeature (Window.FEATURE_NO_TITLE);
            getWindow ().requestFeature (Window.FEATURE_ACTION_BAR_OVERLAY);
            // 20200805 로테이션 disabled
            setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            setContentView(R.layout.activity_main);

            web = (WebView) findViewById (R.id.web);
            // web.setWebContentsDebuggingEnabled (true); // 디버깅 (chrome://inspect)

            web.setWebViewClient (new MainActivity.WebClient ());
            web.setWebChromeClient (new MainActivity.ChromeClient ());
            web.getSettings ().setJavaScriptEnabled (true);
            web.getSettings ().setDefaultTextEncodingName (Allo.CUBE_CHARSET);
            web.getSettings ().setSupportZoom (true);
            web.getSettings ().setUseWideViewPort (true);

            web.getSettings ().setDatabaseEnabled (true);
            web.getSettings ().setDomStorageEnabled (true);
            web.getSettings ().setBuiltInZoomControls (false);
            web.getSettings ().setLoadWithOverviewMode (true);
            web.getSettings ().setCacheMode (WebSettings.LOAD_NO_CACHE);
            web.getSettings ().setPluginState (WebSettings.PluginState.ON);

            web.getSettings ().setAllowFileAccess (true);
            web.getSettings ().setAllowContentAccess (true);
            web.getSettings ().setAllowFileAccessFromFileURLs (true);
            web.getSettings ().setAllowUniversalAccessFromFileURLs (true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) // Android 5.0 Lollipop (API 21)
            {
                web.getSettings ().setMixedContentMode (WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            }
            web.getSettings ().setGeolocationEnabled (true);
            web.setLongClickable (false); // long press disabled (이미지 다운로드 방지용)
            web.requestFocus (View.FOCUS_DOWN);
            web.setOnTouchListener (new AlloGesture (this));
            if (null != savedInstanceState) web.restoreState (savedInstanceState);

            loadSite ();
            rotateFirebase ();
            enableNotificationPermission ();
        } catch (Exception e) { e.printStackTrace (); }

    }

    @Override
    public void onStart ()
    {
        Allo.i ("onStart " + getClass ());
        super.onStart ();
    }

    @Override
    public void onRestart ()
    {
        Allo.i ("onRestart " + getClass ());
        super.onRestart ();
    }

    @Override
    protected void onResume ()
    {
        Allo.i ("onResume " + getClass ());
        super.onResume ();

        try
        {
            rotateNotification ();
        } catch (Exception e) { e.printStackTrace (); }
    }

    @Override
    public void onPause ()
    {
        Allo.i ("onPause " + getClass ());
        super.onPause ();
    }

    @Override
    public void onStop ()
    {
        Allo.i ("onStop " + getClass ());
        super.onStop ();
    }

    @Override
    public void onDestroy ()
    {
        Allo.i ("onDestroy " + getClass ());
        super.onDestroy ();
    }

    @Override
    // 푸시 알림의 데이터를 활용하기 위해 반드시 override 해야함 (특히 카카오톡과 같은 다중 알림)
    protected void onNewIntent (Intent intent)
    {
        super.onNewIntent (intent);
        Allo.i ("onNewIntent " + getClass ());

        try
        {
            setIntent (intent);
        } catch (Exception e) { e.printStackTrace (); }
    }

    private void enableNotificationPermission ()
    {
        Allo.i ("enableNotificationPermission " + getClass ());

        try
        {
            boolean enabled = false;
            Set<String> managedPackages = NotificationManagerCompat.getEnabledListenerPackages (this);
            String packageName = getPackageName ();
            for (String managedPackage : managedPackages)
            {
                if (packageName.equals (managedPackage))
                {
                    enabled = true; break;
                }
            }
            if (!enabled)
            {
                startActivity (new Intent ("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
            }
        } catch (Exception e) { e.printStackTrace (); }
    }

    private void rotateNotification ()
    {
        Allo.i ("rotateNotification " + getClass ());

        try
        {
            Intent params = getIntent ();
            if (null != params)
            {
                if (null != params.getStringExtra (Allo.CUBE_LINK))
                {
                    String link = params.getStringExtra (Allo.CUBE_LINK);
                    Allo.i ("Check link [" + link + "]");
                    try
                    {
                        new URL (link); // 유효하지 않은 경우엔 Exception 및 스킵함
                        startActivity (new Intent (Intent.ACTION_VIEW).setData (Uri.parse (link)));
                    } catch (Exception x) { x.printStackTrace (); }
                    // 푸시 알림 패러미터 재실행 방지를 위해 데이터 삭제요
                    // 예 : (데이터 삭제를 안하면) 띄워진 외부 링크 확인후 앱으로 넘어오면 다시 외부 링크를 띄움 (무한 반복)
                    params.removeExtra (Allo.CUBE_LINK);
                }
            }
        } catch (Exception e) { e.printStackTrace (); }
    }

    private void rotateFirebase ()
    {
        Allo.i ("rotateFirebase " + getClass ());

        try
        {
            FirebaseMessaging.getInstance ().getToken ()
                    .addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete (@NonNull Task<String> task) {
                            if (!task.isSuccessful ()) {
                                Allo.i ("getInstanceId failed " + task.getException ());
                                return;
                            }

                            // Get new Instance ID token
                            final String token = task.getResult ();
                            new Handler (Looper.getMainLooper ()).postDelayed (() -> rotateToken (token), 100);
                        }
                    });
        } catch (Exception e) { e.printStackTrace (); }
    }

    private void rotateToken (String token)
    {
        Allo.i ("rotateToken " + getClass ());

        try
        {
            registDevice (token);
            SharedPreferences sharedPreferences = getPreferences (Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit ();
            editor.putString (Allo.CUBE_TOKEN, token); editor.commit ();
        } catch (Exception e) { e.printStackTrace (); }
    }

    private void registDevice (final String token)
    {
        Allo.i ("registDevice " + getClass ());

        try
        {
            new Thread ()
            {
                public void run ()
                {
                    // 필요시 로컬 및 리모트 서버 연동하여 저장함
                    Allo.i ("Check token [" + token + "]");
                }
            }.start ();
        } catch (Exception e) { e.printStackTrace (); }
    }

    private void loadSite ()
    {
        Allo.i ("loadSite " + getClass ());

        try
        {
            loadLink (Allo.CUBE_SITE);
        } catch (Exception e) { e.printStackTrace (); }
    }

    private void loadLink (String link)
    {
        Allo.i ("loadLink [" + link + "] " + getClass ());

        try
        {
            web.loadUrl (link);
        } catch (Exception e) { e.printStackTrace (); }
    }

    @Override
    public void onGesture (int type)
    {
        Allo.i ("onGesture [" + type + "] " + getClass ());

        try
        {
            switch (type)
            {
                case AlloGesture.SINGLE_SWIPE_LEFT:
                    actionNext();
                    break;
                case AlloGesture.SINGLE_SWIPE_RIGHT:
                    actionPrev();
                    break;
            }
        } catch (Exception e) { e.printStackTrace (); }
    }

    private void actionPrev ()
    {
        Allo.i ("actionPrev [" + web.getUrl () + "] " + getClass ());

        try
        {
            if (web.isFocused () && web.canGoBack ())
            {
                web.goBack ();
            }
            else
            {
                String currentUrl = web.getUrl ();
                if (!(Allo.CUBE_SITE.equals (currentUrl))) loadSite ();
            }
        } catch (Exception e) { e.printStackTrace (); }
    }

    private void actionNext ()
    {
        Allo.i ("actionNext [" + web.getUrl () + "] " + getClass ());

        try
        {
            if (web.isFocused () && web.canGoForward ()) web.goForward ();
        } catch (Exception e) { e.printStackTrace (); }
    }

    private class WebClient extends WebViewClient
    {
        @Override
        public void onPageStarted (WebView view, String url, Bitmap favicon)
        {
            super.onPageStarted (view, url, favicon);
            Allo.i ("onPageStarted [" + url + "] " + getClass ());

            try
            {
                showIndicator ();
            } catch (Exception e) { e.printStackTrace (); }
        }

        @Override
        public void onPageFinished (WebView view, String url)
        {
            super.onPageFinished (view, url);
            Allo.i ("onPageFinished [" + url + "] " + getClass ());

            try
            {
                // 20200813 이전, 다음 등 페이지 이동시 인디케이터가 잠깐 나타나는 등 효과가 미비하다는 언급이 있기에 고정으로 수초 적용함
                // hideIndicator ();
            } catch (Exception e) { e.printStackTrace (); }
        }

        @Override
        public boolean shouldOverrideUrlLoading (WebView view, String url)
        {
            Allo.i ("shouldOverrideUrlLoading [" + url + "] " + getClass ());

            // 웹과 연동하여 네이티브 코드 실행을 위해 지정한 (엑션) 패턴의 경우 오버라이드 처리함
            try
            {
                // check
            } catch (Exception e) { e.printStackTrace (); }

            return super.shouldOverrideUrlLoading (view, url);
        }

        @Override
        @TargetApi (Build.VERSION_CODES.M)
        public void onReceivedError (WebView view, WebResourceRequest request, WebResourceError error)
        {
            super.onReceivedError (view, request, error);
            final Uri uri = request.getUrl ();
            handleError (view, error.getErrorCode (), error.getDescription ().toString (), uri);
        }

        @Override
        @SuppressWarnings ("deprecation")
        public void onReceivedError (WebView view, int errorCode, String description, String failingUrl)
        {
            super.onReceivedError (view, errorCode, description, failingUrl);
            final Uri uri = Uri.parse (failingUrl);
            handleError (view, errorCode, description, uri);
        }

        private void handleError (WebView view, int errorCode, String description, final Uri uri)
        {
            Allo.i ("handleError [" + errorCode + "][" + description + "] " + getClass ());
        }
    }

    private class ChromeClient extends WebChromeClient
    {
        @Override
        public boolean onShowFileChooser (WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams)
        {
            Allo.i ("onShowFileChooser [" + Arrays.toString (fileChooserParams.getAcceptTypes()) + "] @" + this.getClass ());

            try
            {
            } catch (Exception e) { e.printStackTrace (); }

            return true;
        }

    }

    private void showIndicator ()
    {
        Allo.i ("showIndicator " + getClass ());

        try
        {
            ProgressBar progress = (ProgressBar) findViewById (R.id.indicator);
            if (!Objects.isNull (progress)) progress.setVisibility (View.VISIBLE);
            new Handler (Looper.getMainLooper ()).postDelayed (() -> hideIndicator (), 1500);
        } catch (Exception e) { e.printStackTrace (); }
    }

    private void hideIndicator ()
    {
        Allo.i ("hideIndicator " + getClass ());

        try
        {
            ProgressBar progress = (ProgressBar) findViewById (R.id.indicator);
            if (!Objects.isNull (progress)) progress.setVisibility (View.INVISIBLE);
        } catch (Exception e) { e.printStackTrace (); }
    }
}