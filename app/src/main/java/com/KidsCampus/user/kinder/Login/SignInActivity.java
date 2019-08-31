package com.KidsCampus.user.kinder.Login;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.SigningInfo;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.KidsCampus.user.kinder.MainActivity;
import com.KidsCampus.user.kinder.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SignInActivity extends AppCompatActivity {

    Calendar cal = Calendar.getInstance();
    EditText id, pw, pwcheck, realname, nickname, birth;
    Button check, choosebirth;
    ImageView goback;
    LinearLayout waiting;
    final Uri uri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/kinder-114b0.appspot.com/o/%EC%82%AC%EC%9A%A9%EC%9E%90%2F%ED%94%84%EB%A1%9C%ED%95%84%2F%EA%B8%B0%EB%B3%B8%20%ED%94%84%EB%A1%9C%ED%95%84%20%EC%9D%B4%EB%AF%B8%EC%A7%80%2Fimage?alt=media&token=9e22b88a-08dc-4e5d-ac06-a854c362b67a");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        id = findViewById(R.id.signin_id);
        pw = findViewById(R.id.signin_pw);
        pwcheck = findViewById(R.id.signin_pw_check);
        realname = findViewById(R.id.signin_realname);
        nickname = findViewById(R.id.signin_nickname);
        check = findViewById(R.id.signin_signin);
        goback = findViewById(R.id.signin_goback);
        waiting = findViewById(R.id.signin_waiting);
        birth = findViewById(R.id.signin_birthdate);
        choosebirth = findViewById(R.id.signin_choosebirth);

        birth.setFocusable(false);
        choosebirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(SignInActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        String msg = String.format("%d년 %d월 %d일", i, i1+1, i2);
                        birth.setText(msg);
                    }
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));
                datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
                datePickerDialog.show();
            }
        });

        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(id.getText().toString().isEmpty() || pw.getText().toString().isEmpty() || pwcheck.getText().toString().isEmpty() ||
                        realname.getText().toString().isEmpty() || nickname.getText().toString().isEmpty()) {
                    waiting.setVisibility(View.GONE);
                    Toast.makeText(SignInActivity.this, "해당 정보를 모두 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    if(!pw.getText().toString().equals(pwcheck.getText().toString())) {
                        waiting.setVisibility(View.GONE);
                        Toast.makeText(SignInActivity.this, "비밀번호를 다시 확인해주세요", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (pw.getText().length() < 6) {
                        waiting.setVisibility(View.GONE);
                        Toast.makeText(SignInActivity.this, "비밀번호는 6자 이상으로 입력해주세요", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                waiting.setVisibility(View.VISIBLE);
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(id.getText().toString(), pw.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        final FirebaseUser user = authResult.getUser();
                        FirebaseAuth.getInstance().signInWithEmailAndPassword(id.getText().toString(), pw.getText().toString())
                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                setInfo(user);
                                waiting.setVisibility(View.GONE);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                waiting.setVisibility(View.GONE);
                                user.delete();
                                Toast.makeText(SignInActivity.this, "회원가입에 실패했습니다.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        waiting.setVisibility(View.GONE);
                        Toast.makeText(SignInActivity.this, "회원가입에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setInfo(final FirebaseUser user) {
        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignInActivity.this);
                    builder.setTitle("이메일 인증")
                            .setMessage("이메일 주소에 전송된 인증 메일의 링크를 확인해주세요. 인증 후 앱을 종료 후 다시 실행해주세요.")
                            .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    Toast.makeText(SignInActivity.this, "인증이 완료되지 않았습니다.", Toast.LENGTH_SHORT).show();
                                }
                            }).show();

                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(nickname.getText().toString())
                            .setPhotoUri(uri)
                            .build();

                    assert user != null;
                    user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                Map <String, Object> temp = new HashMap<>();
                                temp.put("realname", realname.getText().toString());
                                temp.put("birthdate", birth.getText().toString());
                                temp.put("id", id.getText().toString());
                                FirebaseFirestore.getInstance().collection("사용자").document(user.getUid()).set(temp).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        FirebaseAuth.getInstance().signOut();

                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                waiting.setVisibility(View.GONE);
                                                Intent intent = new Intent(SignInActivity.this, LoginActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }, 5000);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        user.delete();
                                        Toast.makeText(SignInActivity.this, "회원가입에 실패했습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                user.delete();
                                Toast.makeText(SignInActivity.this, "회원가입에 실패했습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            waiting.setVisibility(View.GONE);
                            Toast.makeText(SignInActivity.this, "회원가입에 실패했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    Toast.makeText(SignInActivity.this, "이메일 전송 실패", Toast.LENGTH_SHORT).show();
                    FirebaseAuth.getInstance().signOut();
                }
            }
        });
    }
}
