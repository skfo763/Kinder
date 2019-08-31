package com.KidsCampus.user.kinder.Login;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.KidsCampus.user.kinder.MainActivity;
import com.KidsCampus.user.kinder.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText id, pw;
    private Button login, signin, find, without;
    private LinearLayout waiting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        id = findViewById(R.id.login_id);
        pw = findViewById(R.id.login_pw);
        login = findViewById(R.id.login_login);
        find = findViewById(R.id.login_find);
        signin = findViewById(R.id.login_signin);
        without = findViewById(R.id.login_withoutlogin);
        waiting = findViewById(R.id.login_waiting);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(id.getText().toString().isEmpty() || pw.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "정보를 입력해 주세요", Toast.LENGTH_SHORT).show();
                } else {
                    waiting.setVisibility(View.VISIBLE);
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(id.getText().toString(), pw.getText().toString())
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    waiting.setVisibility(View.GONE);
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            waiting.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "로그인에 실패했습니다", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignInActivity.class);
                startActivity(intent);

            }
        });

        without.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setTitle("비로그인 사용 안내")
                        .setMessage("로그인을 하지 않고 사용할 시, 게시판 및 알림기능을 사용할 수 없습니다.\n그래도 계속하시겠습니까?")
                        .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(), "취소하였습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }).setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                builder.show();
            }
        });

        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, FindActivity.class);
                startActivity(intent);
            }
        });


    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        finish();
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
