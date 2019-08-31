package com.KidsCampus.user.kinder;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.KidsCampus.user.kinder.Fragment.AlertFragment;
import com.KidsCampus.user.kinder.Fragment.BoardFragment;
import com.KidsCampus.user.kinder.Fragment.KinderFragment;
import com.KidsCampus.user.kinder.Fragment.MessageFragment;
import com.KidsCampus.user.kinder.Fragment.MainFragment;
import com.KidsCampus.user.kinder.NormalBoard.MakeBoardActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    public static Context mContext;
    public ImageView toolbar_button, toolbar_button2;
    public TextView titletext;
    public BottomNavigationView navigationView;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        titletext = findViewById(R.id.main_titletext);
        toolbar_button = findViewById(R.id.mainactivity_person);
        toolbar_button2 = findViewById(R.id.main_write);
        mContext = this;

        if(user != null) {
            if (!user.isEmailVerified()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("이메일 인증")
                        .setMessage("이메일 주소에 전송된 인증 메일의 링크를 확인해주세요. 인증 후 앱을 종료 후 다시 실행해주세요.")
                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                Toast.makeText(MainActivity.this, "인증이 완료되지 않아 로그아웃합니다.", Toast.LENGTH_SHORT).show();
                            }
                        }).show();
                FirebaseAuth.getInstance().signOut();
            } else {
                passPushToken();
            }
        }

        setHome();
        navigationView = findViewById(R.id.bottom_navigation_view);
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_home :
                        setHome();
                        return true;
                    case R.id.action_kinder :
                        setKinder();
                        return true;
                    case R.id.action_board :
                        setBoard();
                        return true;
                    case R.id.action_alert :
                        setAlert();
                        return true;
                    case R.id.action_message :
                        setMessage();
                        return true;
                    default:
                        break;
                }
                return false;
            }
        });

        toolbar_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PrivateInfoActivity.class);
                ActivityOptions options = ActivityOptions.makeCustomAnimation(MainActivity.this, R.anim.fromright, R.anim.toleft);
                startActivity(intent, options.toBundle());
            }
        });

        toolbar_button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MakeBoardActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setAlert() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                getSupportFragmentManager().beginTransaction().replace(R.id.main_framelayout, new AlertFragment()).commit();

            }
        }).start();
    }

    private void setMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                getSupportFragmentManager().beginTransaction().replace(R.id.main_framelayout, new MessageFragment()).commit();
            }
        }).start();
    }

    private void setBoard() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                getSupportFragmentManager().beginTransaction().replace(R.id.main_framelayout, new BoardFragment()).commit();
            }
        }).start();
    }

    public void setHome() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                getSupportFragmentManager().beginTransaction().replace(R.id.main_framelayout, new MainFragment()).commit();
            }
        }).start();
    }

    public void setKinder() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_framelayout, new KinderFragment()).commit();
                    }
                }).start();
            }
        }).start();
    }

    private void passPushToken() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String token = FirebaseInstanceId.getInstance().getToken();
        Map<String, Object> token_data = new HashMap<>();
        token_data.put("token", token);
        token_data.put("display_name", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName());
        FirebaseFirestore.getInstance().collection("사용자").document(uid).update(token_data);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        finish();
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}