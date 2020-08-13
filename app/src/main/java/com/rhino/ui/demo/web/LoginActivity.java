package com.rhino.ui.demo.web;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.rhino.ui.demo.R;
import com.rhino.ui.utils.ui.AnimUtils;
import com.rhino.ui.utils.ui.ScreenUtils;

/**
 * @author rhino
 * @since Create on 2020/1/15.
 **/
public class LoginActivity extends Activity {

    private static final int REQUEST_CODE_PERMISSION = 1;
    private static final String[] PERMISSIONS_NET = {
            Manifest.permission.INTERNET,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//        ViewGroup viewGroup = findViewById(R.id.ll_container);
//        AnimUtils.rotate(viewGroup, 0, 90, 50, null);
//        ViewGroup.LayoutParams params = viewGroup.getLayoutParams();
//        params.height = ScreenUtils.getScreenWidth(this);
//        params.width = ScreenUtils.getScreenHeight(this);
//        viewGroup.setLayoutParams(params);

        findViewById(R.id.bt_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, WebViewActivity.class));
            }
        });

//        if (!hasPermissionGranted(this, PERMISSIONS_NET)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_NET, REQUEST_CODE_PERMISSION);
//        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (!hasPermissionGranted(this, permissions)) {
                Toast.makeText(this, "需要手动设置权限！", Toast.LENGTH_SHORT).show();
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
}
