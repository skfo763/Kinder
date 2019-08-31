package com.KidsCampus.user.kinder;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.KidsCampus.user.kinder.Login.LoginActivity;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import java.util.Objects;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        MobileAds.initialize(this, "ca-app-pub-2826122867888366~8262082336");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(isConnected() == true) {
                    if(FirebaseAuth.getInstance().getCurrentUser() != null) {
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
                    builder.setTitle("인터넷 연결 알림")
                            .setMessage("인터넷이 연결되지 않아 서비스를 이용하실 수 없습니다. 설정 창으로 이동하시겠습니까?")
                            .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                                    startActivityForResult(intent, 0);
                                }
                            }).setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(SplashActivity.this, "인터넷에 연결되지 않아 앱을 종료합니다.", Toast.LENGTH_SHORT).show();
                                }
                            }).show();
                }
            }
        }, 1500);
    }

    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNet = Objects.requireNonNull(cm).getActiveNetworkInfo();
        boolean retValue = activeNet != null && activeNet.isConnected();
        return retValue;
    }
}
