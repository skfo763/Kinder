package com.KidsCampus.user.kinder.Login;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.KidsCampus.user.kinder.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class FindActivity extends AppCompatActivity {

    Calendar cal = Calendar.getInstance();
    EditText birth, realname, email;
    Button choosebirth, idfind, pwreset;
    ImageView goback;
    LinearLayout waiting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);

        birth = findViewById(R.id.find_id_edittext_birth);
        realname = findViewById(R.id.find_id_edittext);
        email = findViewById(R.id.find_pw_edittext_id);
        choosebirth = findViewById(R.id.find_id_button_birth);
        idfind = findViewById(R.id.find_id_button);
        pwreset = findViewById(R.id.find_pw_button_id);
        goback = findViewById(R.id.find_goback);
        waiting = findViewById(R.id.find_waiting);

        birth.setFocusable(false);
        choosebirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(FindActivity.this, new DatePickerDialog.OnDateSetListener() {
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

        idfind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(birth.getText().toString().isEmpty() || realname.getText().toString().isEmpty()) {
                    Toast.makeText(FindActivity.this, "정보를 모두 입력해주세요", Toast.LENGTH_SHORT).show();
                } else {
                    waiting.setVisibility(View.VISIBLE);
                    FirebaseFirestore.getInstance().collection("사용자").whereEqualTo("realname", realname.getText().toString())
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()) {
                                ArrayList<String> id_data = new ArrayList<>();
                                for(DocumentSnapshot documentSnapshot : task.getResult()) {
                                    String t_birth = documentSnapshot.getData().get("birthdate").toString();
                                    if(t_birth.equals(birth.getText().toString())) {
                                        id_data.add(documentSnapshot.getData().get("id").toString());
                                    }
                                }

                                final String[] array = id_data.toArray(new String[id_data.size()]);
                                final AlertDialog.Builder builder = new AlertDialog.Builder(FindActivity.this);
                                builder.setTitle("아이디 검색결과")
                                        .setItems(array, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                waiting.setVisibility(View.GONE);
                                                email.setText(array[which]);
                                            }
                                        }).show();
                            } else {
                                waiting.setVisibility(View.GONE);
                                Toast.makeText(FindActivity.this, "정보를 찾을 수 없습니다", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        pwreset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String eAddress = email.getText().toString();
                FirebaseFirestore.getInstance().collection("사용자").whereEqualTo("id", eAddress).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots.getDocuments().size() > 0) {
                            getEmail(eAddress);
                        } else {
                            Toast.makeText(FindActivity.this, "존재하지 않는 이메일입니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void getEmail(String eAddress) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(eAddress).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(FindActivity.this);
                builder.setTitle("비밀번호 재설정 안내")
                        .setMessage("비밀번호 재설정 메일을 귀하의 이메일 주소로 전송하였습니다. 확인 및 비밀번호 변경 후 재로그인하시기 바랍니다.")
                        .setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                onBackPressed();
                            }
                        })
                        .show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onBackPressed();
                    }
                }, 3000);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(FindActivity.this, "존재하지 않는 이메일입니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
