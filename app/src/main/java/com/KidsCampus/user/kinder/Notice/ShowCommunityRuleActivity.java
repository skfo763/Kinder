package com.KidsCampus.user.kinder.Notice;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.KidsCampus.user.kinder.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class ShowCommunityRuleActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TextView overview, context, image, admin, law, msg, other;
    private ImageView goback;
    private LinearLayout waiting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_community_rule);

        goback = findViewById(R.id.showcmrule_goback);
        overview = findViewById(R.id.showcmrule_overview);
        context = findViewById(R.id.showcmrule_context);
        image = findViewById(R.id.showcmrule_image);
        admin = findViewById(R.id.showcmrule_admin);
        law = findViewById(R.id.showcmrule_law);
        msg = findViewById(R.id.showcmrule_msg);
        other = findViewById(R.id.showcmrule_other);
        waiting = findViewById(R.id.showcmrule_waiting);

        db.collection("이용규칙").document("community_rule").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map<String, Object> cmrule = documentSnapshot.getData();
                if(cmrule != null) {
                    overview.setText(cmrule.get("overview").toString());
                    context.setText(cmrule.get("rule_context").toString());
                    image.setText(cmrule.get("rule_image").toString());
                    admin.setText(cmrule.get("rule_admin").toString());
                    law.setText(cmrule.get("rule_law").toString());
                    msg.setText(cmrule.get("rule_msg").toString());
                    other.setText(cmrule.get("rule_other").toString());
                    waiting.setVisibility(View.GONE);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ShowCommunityRuleActivity.this, "로딩 오류", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onBackPressed();
                    }
                }, 1000);
            }
        });

        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
