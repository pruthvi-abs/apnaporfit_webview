package com.example.apnaprofitportal;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private SwipeRefreshLayout swipeContainer;
    private WebView webView;
    private ValueCallback<Uri> mUploadMessage;
    public ValueCallback<Uri[]> uploadMessage;
    public static final int REQUEST_SELECT_FILE = 100;
    private final static int FILECHOOSER_RESULTCODE = 1;
    public String url ="http://absolutewebdevelopment.in/apnaProfit/login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            this.getSupportActionBar().hide();
        } catch (NullPointerException e) {
            Toast.makeText(this, "Please Wait...", Toast.LENGTH_SHORT).show();
        }

        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.black, this.getTheme()));
        } else {
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        }

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Intent i = new Intent(MainActivity.this, MainActivity.class);
                startActivity(i);
            }
        });

        if (!InternetCheck.checkInternetConnection(this)) {
            setContentView(R.layout.internetdialog);
            findViewById(R.id.retry).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent1 = new Intent();
                    intent1.setClassName(getPackageName(), MainActivity.class.getName());
                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent1);
                }
            });
        } else {
            webView = findViewById(R.id.webview);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            webView.getSettings().setDomStorageEnabled(true);
            webView.getSettings().setAllowContentAccess(true);
            webView.getSettings().setAllowFileAccessFromFileURLs(true);
            webView.getSettings().setAllowUniversalAccessFromFileURLs(true);

            webView.getSettings().setAllowFileAccess( true );
            webView.getSettings().setAppCacheEnabled( true );
            webView.getSettings().setCacheMode( WebSettings.LOAD_DEFAULT );
            checkDownloadPermission();
            webView.loadUrl(url);
            webView.setWebViewClient(new xWebViewClient());
            webView.setWebChromeClient(new WebChromeClient()
            {
                // For 3.0+ Devices (Start)
                // onActivityResult attached before constructor
                protected void openFileChooser(ValueCallback uploadMsg, String acceptType)
                {
                    mUploadMessage = uploadMsg;
                    Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                    i.addCategory(Intent.CATEGORY_OPENABLE);
                    i.setType("image/*");
                    startActivityForResult(Intent.createChooser(i, "File Browser"), FILECHOOSER_RESULTCODE);
                }


                // For Lollipop 5.0+ Devices
                public boolean onShowFileChooser(WebView mWebView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams)
                {
                    if (uploadMessage != null) {
                        uploadMessage.onReceiveValue(null);
                        uploadMessage = null;
                    }

                    uploadMessage = filePathCallback;

                    Intent intent = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        intent = fileChooserParams.createIntent();
                    }
                    try
                    {
                        startActivityForResult(intent, REQUEST_SELECT_FILE);
                    } catch (ActivityNotFoundException e)
                    {
                        uploadMessage = null;
                        return false;
                    }
                    return true;
                }

                //For Android 4.1 only
                protected void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture)
                {
                    mUploadMessage = uploadMsg;
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent, "File Browser"), FILECHOOSER_RESULTCODE);
                }

                protected void openFileChooser(ValueCallback<Uri> uploadMsg)
                {
                    mUploadMessage = uploadMsg;
                    Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                    i.addCategory(Intent.CATEGORY_OPENABLE);
                    i.setType("image/*");
                    startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
                }
            });

//            webView.setDownloadListener(new DownloadListener() {
//                @Override
//                public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
//                    url = url + '/' + URLUtil.guessFileName(url, contentDisposition, mimetype);
//                    Log.d("PDF",url);
//                    Log.d("PDF",userAgent);
//                    Log.d("PDF",contentDisposition);
//                    Log.d("PDF",mimetype);
//                    Log.d("PDF", String.valueOf(contentLength));
//                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
//                    request.allowScanningByMediaScanner();
//                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); //Notify client once download is completed!
//                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url, contentDisposition, mimetype));
//                    DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
//                    dm.enqueue(request);
//                    Toast.makeText(getApplicationContext(), "Downloading File", Toast.LENGTH_LONG).show();
//                }
//            });

            webView.setDownloadListener(new DownloadListener() {
                @Override
                public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {

                    String file_name = contentDisposition.substring(18,52);
//                    url = url + '/' + file_name;
                    Log.d("PDF",url);
                    Log.d("PDF",userAgent);
                    Log.d("PDF", String.valueOf(contentDisposition.length()));
                    Log.d("PDF", file_name );
                    Log.d("PDF",mimetype);
                    Log.d("PDF", String.valueOf(contentLength));

                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                    request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimetype));
                    request.setDescription("Downloading file...");
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, file_name);
                    DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                    dm.enqueue(request);
                    Toast.makeText(getApplicationContext(), "Downloading...", Toast.LENGTH_SHORT).show();
                    registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                }
                BroadcastReceiver onComplete = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        Toast.makeText(getApplicationContext(), "Downloading Complete", Toast.LENGTH_SHORT).show();
                    }
                };
            });

//            webView.setDownloadListener(new DownloadListener() {
//                public void onDownloadStart(String url, String userAgent,
//                                            String contentDisposition, String mimetype,
//                                            long contentLength) {
//                    Intent i = new Intent(Intent.ACTION_VIEW);
//                    i.setData(Uri.parse(url));
//                    startActivity(i);
//                }});

        }
    }

    private void checkDownloadPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(MainActivity.this, "Write External Storage permission allows us to save files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode == REQUEST_SELECT_FILE) {
                if (uploadMessage == null)
                    return;
                uploadMessage.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent));
                uploadMessage = null;
            }
        } else if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage)
                return;
            // Use MainActivity.RESULT_OK if you're implementing WebView inside Fragment
            // Use RESULT_OK only if you're implementing WebView inside an Activity
            Uri result = intent == null || resultCode != MainActivity.RESULT_OK ? null : intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        }
    }

    private class xWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    public void onBackPressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (webView.canGoBack()) {
                webView.goBack();
            } else {
                new AlertDialog.Builder(this)
                    .setTitle("Confirm Exit?")
                    .setMessage("Are you sure you want to exit?")
                    .setNegativeButton(Html.fromHtml("<font color='#bf9233'>Cancel</font>"), null)
                    .setPositiveButton(Html.fromHtml("<font color='#bf9233'>Ok</font>"), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            Intent intent = new Intent(Intent.ACTION_MAIN);
                            intent.addCategory(Intent.CATEGORY_HOME);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }).create().show();
            }
        }
    }
}
