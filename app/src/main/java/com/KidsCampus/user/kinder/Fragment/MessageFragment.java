package com.KidsCampus.user.kinder.Fragment;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.KidsCampus.user.kinder.Login.LoginActivity;
import com.KidsCampus.user.kinder.MainActivity;
import com.KidsCampus.user.kinder.Message.ShowMessageRoomActivity;
import com.KidsCampus.user.kinder.NormalBoard.ShowPrivateListActivity;
import com.KidsCampus.user.kinder.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class MessageFragment extends Fragment {

    private RecyclerView recyclerView;
    private LinearLayout nomessage, waiting, noLogin;
    private Button goLogin;
    ArrayList<Map<String, Object>> messageData = new ArrayList<>();
    ArrayList<String> key_data = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        ((MainActivity)MainActivity.mContext).titletext.setText("쪽지함");
        recyclerView = view.findViewById(R.id.messagefragment_recyclerview);
        nomessage = view.findViewById(R.id.messagefragment_no_message);
        waiting = view.findViewById(R.id.messagefragment_waiting);
        noLogin = view.findViewById(R.id.messagefragment_no_login);
        goLogin = view.findViewById(R.id.messagefragment_gologin);

        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseFirestore.getInstance().collection("사용자").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .collection("saved_message").orderBy("time").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()) {
                        for (DocumentSnapshot snapshot : task.getResult()) {
                            messageData.add(snapshot.getData());
                            key_data.add(snapshot.getId());
                        }
                        if(messageData.isEmpty()) {
                            waiting.setVisibility(View.GONE);
                            nomessage.setVisibility(View.VISIBLE);
                        } else {
                            RecyclerView.Adapter adapter = new MyAdapter(messageData, key_data);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                            recyclerView.setAdapter(adapter);
                            waiting.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                    }
                }
            });
        } else {
            waiting.setVisibility(View.GONE);
            noLogin.setVisibility(View.VISIBLE);
        }

        goLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    private class MyAdapter extends RecyclerView.Adapter {

        ArrayList<Map<String, Object>> mdata;
        ArrayList<String> kdata;

        public MyAdapter(ArrayList<Map<String, Object>> messageData, ArrayList<String> key_data) {
            this.mdata = messageData;
            this.kdata = key_data;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_message_room, viewGroup, false);
            return new CustomItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
            Date date = new Date(Long.parseLong(mdata.get(i).get("time").toString()));
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("MM/dd hh:mm");
            String getDate = sdf.format(date);

            ((CustomItemViewHolder)viewHolder).writer.setText(mdata.get(i).get("writer").toString());
            ((CustomItemViewHolder)viewHolder).context.setText(mdata.get(i).get("context").toString());
            ((CustomItemViewHolder)viewHolder).time.setText(getDate);

            if(messageData.get(i).get("check").toString().equals("0")) {
                ((CustomItemViewHolder)viewHolder).check.setText("(읽지 않음)");
            } else {
                ((CustomItemViewHolder)viewHolder).check.setText("");
            }

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), ShowMessageRoomActivity.class);
                    ActivityOptions options = ActivityOptions.makeCustomAnimation(getContext(), R.anim.fromright, R.anim.toleft);
                    intent.putExtra("key", kdata.get(i));
                    startActivity(intent, options.toBundle());
                }
            });
        }

        @Override
        public int getItemCount() {
            return messageData.size();
        }
    }

    private class CustomItemViewHolder extends RecyclerView.ViewHolder {
        public TextView writer, context, time, check;

        public CustomItemViewHolder(View view) {
            super(view);
            writer = view.findViewById(R.id.item_message_room_writer);
            context = view.findViewById(R.id.item_message_room_context);
            time = view.findViewById(R.id.item_message_room_time);
            check = view.findViewById(R.id.item_message_room_check);
        }
    }
}
