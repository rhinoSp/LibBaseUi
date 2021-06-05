package com.rhino.ui.demo.web;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.rhino.ui.demo.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

/**
 * @author LuoLin
 * @since Create on 2019/08/22.
 **/
public class WebViewActivity extends Activity {

    private static final int REQUEST_CODE_PERMISSION = 1;
    private final static int REQUEST_CODE_TAKE_PICTURE = 2;
    private String URL;
    //    private final static String URL_LOCAL = "file:///android_asset/index.html";
    private final static String URL_LOCAL = "https://www.baidu.com";

    private static final String[] PERMISSIONS_NET = {
            Manifest.permission.INTERNET,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private WebView webView;
    private ValueCallback<Uri[]> uploadMessageAboveL;
    private String imageFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        URL = getIntent().getStringExtra("url");
        setContentView(R.layout.activity_h5_authentication);
        setTitle(URL);
        webView = findViewById(R.id.WebView_test);
//        webView.post(new Runnable() {
//            @Override
//            public void run() {
//                AnimUtils.rotate(webView, 0, 90, 50, null);
//                ViewGroup.LayoutParams params = webView.getLayoutParams();
//                params.height = ScreenUtils.getScreenWidth(getApplicationContext());
//                params.width = ScreenUtils.getScreenHeight(getApplicationContext());
//                webView.setLayoutParams(params);
//            }
//        });

        if (!hasPermissionGranted(this, PERMISSIONS_NET)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_NET, REQUEST_CODE_PERMISSION);
        } else {
            loadUrl();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (!hasPermissionGranted(this, permissions)) {
                Toast.makeText(this, "需要手动设置权限！", Toast.LENGTH_SHORT).show();
            } else {
                loadUrl();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_TAKE_PICTURE) {
            Uri[] results = null;
            if (resultCode == RESULT_OK) {
                results = new Uri[]{Uri.fromFile(new File(imageFilePath))};
            }
            if (uploadMessageAboveL != null) {
                uploadMessageAboveL.onReceiveValue(results);
                uploadMessageAboveL = null;
            }
        }
    }

    /**
     * 检查权限组中的每个权限是否授权
     */
    private static boolean hasPermissionGranted(Activity activity, String[] permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 创建照片文件路径
     */
    private static String createImageFilePath() {
        File dir = new File(Environment.getExternalStorageDirectory(), "Album");
        if (!dir.exists() && !dir.mkdir()) {
            return "";
        }
        Random random = new Random();
        int randomNumber = 100000 + random.nextInt(899999);
        String fileName = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault()).format(new Date()) + randomNumber + ".jpg";
        return dir + "/" + fileName;
    }

    /**
     * 调系统相机拍照
     */
    private void takePhoto() {
        imageFilePath = createImageFilePath();
        if (TextUtils.isEmpty(imageFilePath)) {
            return;
        }
        File imageFile = new File(imageFilePath);
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, imageFile.getAbsolutePath());
        contentValues.put("_data", imageFile.getAbsolutePath());
        contentValues.put(MediaStore.Images.Media.PICASA_ID, imageFile.getAbsolutePath());
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, imageFile.getAbsolutePath());
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, imageFile.getAbsolutePath());
        contentValues.put(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, imageFile.getAbsolutePath());
        Uri photoUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);

    }

    /**
     * 加载WebView
     */
    private void loadUrl() {
        webView.setWebChromeClient(new WebChromeClient() {
            // For Android >= 5.0
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                uploadMessageAboveL = filePathCallback;
                takePhoto();
                return true;
            }
        });
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                try {
                    if (url.startsWith("http:") || url.startsWith("https:")) {
                        view.loadUrl(url);
                    } else {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                    }
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
//                super.onReceivedSslError(view, handler, error);
                handler.proceed();
            }
        });
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            webSettings.setDatabasePath("/data/data/" + webView.getContext().getPackageName() + "/databases/");
        }
//        webView.loadUrl(URL);
        webView.loadUrl(URL_LOCAL);

    }

}
