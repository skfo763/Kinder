package com.KidsCampus.user.kinder.Notice;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.KidsCampus.user.kinder.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ShowNoticeActivity extends AppCompatActivity {

    String notice_key;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TextView title, time, context, writer;
    private ImageView goback;
    private LinearLayout waiting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_notice);
        notice_key = getIntent().getStringExtra("notice_key");

        waiting = findViewById(R.id.show_notice_waiting);
        title = findViewById(R.id.show_notice_title);
        time = findViewById(R.id.show_notice_time);
        context = findViewById(R.id.show_notice_context);
        writer = findViewById(R.id.show_notice_writer);
        goback = findViewById(R.id.show_notice_goback);

        db.collection("공지사항").document(notice_key).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map<String, Object> data = documentSnapshot.getData();
                if(data != null) {
                    Date date = new Date(Long.parseLong(data.get("time").toString()));
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("MM/dd hh:mm");
                    String getDate = sdf.format(date);

                    title.setText(data.get("title").toString());
                    time.setText(getDate);
                    context.setText(data.get("context").toString());
                    writer.setText(data.get("writer").toString());
                    waiting.setVisibility(View.GONE);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ShowNoticeActivity.this);
                    builder.setMessage("공지사항을 불러올 수 없습니다.")
                            .setPositiveButton("뒤로가기", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    onBackPressed();
                                }
                            }).show();
                }
            }
        });

        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fromleft, R.anim.toright);
    }
}
