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

public class ShowInfoRuleActivity extends AppCompatActivity {

    private TextView overview, context, usage, offer, manage, right, service, other;
    private ImageView goback;
    private LinearLayout waiting;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_info_rule);

        overview = findViewById(R.id.showinrule_overview);
        context = findViewById(R.id.showinrule_context);
        usage = findViewById(R.id.showinrule_usage);
        offer = findViewById(R.id.showinrule_offer);
        manage = findViewById(R.id.showinrule_manage);
        right = findViewById(R.id.showinrule_right);
        service = findViewById(R.id.showinrule_service);
        other = findViewById(R.id.showinrule_other);
        goback = findViewById(R.id.showinrule_goback);
        waiting = findViewById(R.id.showinrule_waiting);

        db.collection("이용규칙").document("info_rule").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map<String, Object> inrule = documentSnapshot.getData();
                overview.setText(inrule.get("overview").toString());
                context.setText(inrule.get("rule_context").toString());
                usage.setText(inrule.get("rule_usage").toString());
                offer.setText(inrule.get("rule_offer").toString());
                manage.setText(inrule.get("rule_manage").toString());
                right.setText(inrule.get("rule_right").toString());
                service.setText(inrule.get("rule_service").toString());
                other.setText(inrule.get("rule_other").toString());
                waiting.setVisibility(View.GONE);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ShowInfoRuleActivity.this, "로딩 오류", Toast.LENGTH_SHORT).show();
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
