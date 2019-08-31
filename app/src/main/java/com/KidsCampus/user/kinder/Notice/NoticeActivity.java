package com.KidsCampus.user.kinder.Notice;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.KidsCampus.user.kinder.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class NoticeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdView adView;
    private ImageView goback;
    private LinearLayout linearLayout;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<Map<String, Object>> notice_data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);

        recyclerView = findViewById(R.id.notice_recyclerview);
        adView = findViewById(R.id.notice_adview);
        goback = findViewById(R.id.notice_goback);
        linearLayout = findViewById(R.id.notice_waiting);

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        db.collection("공지사항").orderBy("time", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for(DocumentSnapshot documentSnapshot : task.getResult()) {
                        Map<String, Object> temp = documentSnapshot.getData();
                        temp.put("notice_key", documentSnapshot.getId());
                        notice_data.add(temp);
                    }

                    if(notice_data.isEmpty()) {
                        recyclerView.setVisibility(View.GONE);
                        linearLayout.setVisibility(View.VISIBLE);
                    } else {
                        recyclerView.setLayoutManager(new LinearLayoutManager(NoticeActivity.this));
                        recyclerView.setAdapter(new MyNoticeAdapter(notice_data));
                        linearLayout.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
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

    private class MyNoticeAdapter extends RecyclerView.Adapter {
        ArrayList<Map<String, Object>> data;
        public MyNoticeAdapter(ArrayList<Map<String, Object>> notice_data) {
            this.data = notice_data;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_notice, viewGroup, false);
            return new CustomNoticeHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
            Date date = new Date(Long.parseLong(data.get(i).get("time").toString()));
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("MM/dd hh:mm");
            String getDate = sdf.format(date);

            ((CustomNoticeHolder)viewHolder).notice_title.setText(data.get(i).get("title").toString());
            ((CustomNoticeHolder)viewHolder).notice_time.setText(getDate);
            ((CustomNoticeHolder)viewHolder).notice_writer.setText(data.get(i).get("writer").toString());

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(NoticeActivity.this, ShowNoticeActivity.class);
                    ActivityOptions options = ActivityOptions.makeCustomAnimation(NoticeActivity.this, R.anim.fromright, R.anim.toleft);
                    intent.putExtra("notice_key", data.get(i).get("notice_key").toString());
                    startActivity(intent, options.toBundle());
                }
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    private class CustomNoticeHolder extends RecyclerView.ViewHolder {
        public TextView notice_title, notice_time, notice_writer;
        public CustomNoticeHolder(View view) {
            super(view);
            notice_title = view.findViewById(R.id.item_notice_title);
            notice_time = view.findViewById(R.id.item_notice_time);
            notice_writer = view.findViewById(R.id.item_notice_writer);
        }
    }
}
