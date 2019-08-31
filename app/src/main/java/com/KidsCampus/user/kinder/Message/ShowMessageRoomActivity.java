package com.KidsCampus.user.kinder.Message;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.KidsCampus.user.kinder.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ShowMessageRoomActivity extends AppCompatActivity {

    private TextView title;
    private RecyclerView recyclerView;
    private ImageView delete, goback, send;
    private AdView adView;
    String target_uid;
    ArrayList<Map<String, Object>> twit_data = new ArrayList<>();
    Map<String, Object> objectMap = new HashMap<>();
    RecyclerView.Adapter adapter = new MyAdapter(twit_data);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_message_room);
        target_uid = getIntent().getStringExtra("key");
        adView = findViewById(R.id.showmessageroom_adview);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        title = findViewById(R.id.showmessageroom_title);
        delete = findViewById(R.id.showmessageroom_delete);
        send = findViewById(R.id.showmessageroom_send);
        goback = findViewById(R.id.showmessageroom_goback);
        recyclerView = findViewById(R.id.showmessageroom_recyclerview);

        FirebaseFirestore.getInstance().collection("사용자").document(target_uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String display_name = documentSnapshot.get("display_name").toString();
                title.setText(display_name);
            }
        });

        FirebaseFirestore.getInstance().collection("사용자").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("saved_message").document(target_uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                objectMap = documentSnapshot.getData();
                objectMap.put("check", "1");
                FirebaseFirestore.getInstance().collection("사용자").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .collection("saved_message").document(target_uid).set(objectMap);
            }
        });

        FirebaseFirestore.getInstance().collection("사용자").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("saved_message").document(target_uid).collection("comments").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for(DocumentSnapshot snapshot : task.getResult()) {
                        Map<String, Object> temp = snapshot.getData();
                        temp.put("kind", "received");
                        temp.put("msg_key", snapshot.getId());
                        twit_data.add(temp);
                    }

                    FirebaseFirestore.getInstance().collection("사용자").document(target_uid).collection("saved_message")
                            .document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("comments").get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful()) {
                                        for (DocumentSnapshot snapshot : task.getResult()) {
                                            Map<String, Object> temp2 = snapshot.getData();
                                            temp2.put("kind", "send");
                                            temp2.put("msg_key", snapshot.getId());
                                            twit_data.add(temp2);
                                        }
                                        recyclerView.setLayoutManager(new LinearLayoutManager(ShowMessageRoomActivity.this));
                                        recyclerView.setAdapter(adapter);
                                    }
                                }
                            });

                }
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowMessageRoomActivity.this, SendMessageActivity.class);
                intent.putExtra("destination", target_uid);
                startActivity(intent);
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ShowMessageRoomActivity.this);
                builder.setTitle("전체 삭제")
                        .setMessage("해당 회원으로부터 받은 모든 쪽지를 삭제합니다.")
                        .setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseFirestore.getInstance().collection("사용자").document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                                        .collection("saved_message").document(target_uid).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()) {
                                            Toast.makeText(ShowMessageRoomActivity.this, "쪽지를 모두 삭제하였습니다.", Toast.LENGTH_SHORT).show();
                                            onBackPressed();
                                        }
                                    }
                                });
                            }
                        }).setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(ShowMessageRoomActivity.this, "취소하였습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }).show();
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

    private class MyAdapter extends RecyclerView.Adapter {

        ArrayList<Map<String, Object>> data;

        public MyAdapter(ArrayList<Map<String, Object>> twit_data) {

            Collections.sort(twit_data, new Comparator<Map<String, Object>>() {
                @Override
                public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                    long first = 0;
                    long second = 0;
                    first = Long.parseLong(o1.get("time").toString());
                    second = Long.parseLong(o2.get("time").toString());
                    return Long.compare(second, first);
                }
            });
            this.data = twit_data;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_message, viewGroup, false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
            Date date = new Date(Long.parseLong(data.get(i).get("time").toString()));
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("MM/dd hh:mm");
            String getDate = sdf.format(date);

            ((CustomViewHolder)viewHolder).item_context.setText(data.get(i).get("context").toString());
            ((CustomViewHolder)viewHolder).item_time.setText(getDate);

            if(data.get(i).get("kind").toString().equals("received")) {
                ((CustomViewHolder)viewHolder).item_kind.setText("받은 쪽지");
                ((CustomViewHolder)viewHolder).item_kind.setTextColor(0xFF44B8FF);
            } else if(data.get(i).get("kind").toString().equals("send")) {
                ((CustomViewHolder)viewHolder).item_kind.setText("보낸 쪽지");
                ((CustomViewHolder)viewHolder).item_kind.setTextColor(0xFFFF5F62);
            }

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ShowMessageRoomActivity.this);
                    builder.setTitle("경고")
                            .setMessage("수신자의 쪽지함에서만 데이터가 삭제됩니다. 발신자의 쪽지함에는 데이터가 남아있을 수 있습니다. 정말로 삭제하시겠습니까?")
                            .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    FirebaseFirestore.getInstance().collection("사용자").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .collection("saved_message").document(target_uid).collection("comments")
                                            .document(data.get(i).get("msg_key").toString()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            adapter.notifyDataSetChanged();
                                        }
                                    });
                                }
                            }).setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(ShowMessageRoomActivity.this, "취소하였습니다.", Toast.LENGTH_SHORT).show();
                                }
                            }).show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    private class CustomViewHolder extends RecyclerView.ViewHolder {

        public TextView item_kind, item_context, item_time;
        public CustomViewHolder(View view) {
            super(view);
            item_kind = view.findViewById(R.id.item_message_kind);
            item_context = view.findViewById(R.id.item_message_context);
            item_time = view.findViewById(R.id.item_message_time);
        }
    }
}
