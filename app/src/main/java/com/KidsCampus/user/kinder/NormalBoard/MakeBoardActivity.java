package com.KidsCampus.user.kinder.NormalBoard;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.KidsCampus.user.kinder.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MakeBoardActivity extends AppCompatActivity {

    private EditText name, description;
    private Switch aSwitch;
    private ImageView goback;
    private Button check;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_board);

        name = findViewById(R.id.makeboard_name);
        description = findViewById(R.id.makeboard_description);
        aSwitch = findViewById(R.id.makeboard_switch);
        goback = findViewById(R.id.makeboard_goback);
        check = findViewById(R.id.makeboard_check);

        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> map = new HashMap<>();
                if(name.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "제목을 입력해 주세요", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    map.put("name", name.getText().toString());
                }

                if(description.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "설명을 입력해 주세요", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    map.put("description", description.getText().toString());
                }
                map.put("writer", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                map.put("anonymous", aSwitch.isChecked());
                map.put("center", false);

                db.collection("게시판").add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Intent intent = new Intent(MakeBoardActivity.this, ShowBoardListActivity.class);
                        intent.putExtra("name", name.getText().toString());
                        if(aSwitch.isChecked()) {
                            intent.putExtra("anonymous", "true");
                        } else {
                            intent.putExtra("anonymous", "false");
                        }
                        startActivity(intent);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "게시판 생성에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
