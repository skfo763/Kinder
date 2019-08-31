package com.KidsCampus.user.kinder.Fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.KidsCampus.user.kinder.Login.LoginActivity;
import com.KidsCampus.user.kinder.MainActivity;
import com.KidsCampus.user.kinder.NormalBoard.ShowEachBoardActivity;
import com.KidsCampus.user.kinder.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

public class AlertFragment extends Fragment {

    private RecyclerView recyclerView;
    private LinearLayout waiting, noalerm, nologin;
    private ArrayList<Map<String, Object>> alert_data = new ArrayList<>();
    private Button gologin;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alert, container, false);
        recyclerView = view.findViewById(R.id.alertfragment_recyclerview);
        waiting = view.findViewById(R.id.alertfragment_waiting);
        noalerm = view.findViewById(R.id.alertfragment_no_alerm);
        nologin = view.findViewById(R.id.alertfragment_no_login);
        gologin = view.findViewById(R.id.alertfragment_gologin);
        ((MainActivity)MainActivity.mContext).titletext.setText("알림");

        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            waiting.setVisibility(View.GONE);
            nologin.setVisibility(View.VISIBLE);
            gologin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    startActivity(intent);
                }
            });

        } else {
            nologin.setVisibility(View.GONE);
            FirebaseFirestore.getInstance().collection("사용자").document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                    .collection("alert").orderBy("time", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot snapshot : task.getResult()) {
                            if (snapshot.exists()) {
                                Map<String, Object> temp = snapshot.getData();
                                temp.put("alert_key", snapshot.getId());
                                alert_data.add(temp);
                            }
                        }
                        if (alert_data.size() != 0) {
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                            recyclerView.setAdapter(new MyAlertAdapter(alert_data));
                            recyclerView.setVisibility(View.VISIBLE);
                            waiting.setVisibility(View.GONE);
                        } else {
                            waiting.setVisibility(View.GONE);
                            noalerm.setVisibility(View.VISIBLE);
                        }

                    }
                }
            });
        }
        return view;
    }

    private class MyAlertAdapter extends RecyclerView.Adapter {

        ArrayList<Map<String, Object>> adapter_data;

        public MyAlertAdapter(ArrayList<Map<String, Object>> alert_data) {
            this.adapter_data = alert_data;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_alert, viewGroup, false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
            Date date = new Date(Long.parseLong(adapter_data.get(i).get("time").toString()));
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("MM/dd hh:mm");
            String getDate = sdf.format(date);

            ((CustomViewHolder)viewHolder).item_title.setText(adapter_data.get(i).get("title").toString());
            ((CustomViewHolder)viewHolder).item_context.setText(adapter_data.get(i).get("context").toString());
            ((CustomViewHolder)viewHolder).item_time.setText(getDate);

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseFirestore.getInstance().collection("사용자").document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                            .collection("alert").document(adapter_data.get(i).get("alert_key").toString()).delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Intent intent = new Intent(getContext(), ShowEachBoardActivity.class);
                            intent.putExtra("collection_key", adapter_data.get(i).get("collection_key").toString());
                            intent.putExtra("document_key", adapter_data.get(i).get("document_key").toString());
                            intent.putExtra("board_name", adapter_data.get(i).get("title").toString());
                            startActivity(intent);
                        }
                    });
                }
            });
        }

        @Override
        public int getItemCount() {
            return adapter_data.size();
        }
    }

    private class CustomViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public TextView item_title, item_context, item_time;

        public CustomViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.alertitem_imageView);
            item_title = view.findViewById(R.id.alertitem_title);
            item_context = view.findViewById(R.id.alertitem_context);
            item_time = view.findViewById(R.id.alertitem_time);
        }
    }
}
