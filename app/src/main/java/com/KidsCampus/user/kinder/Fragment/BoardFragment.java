package com.KidsCampus.user.kinder.Fragment;

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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.KidsCampus.user.kinder.Login.LoginActivity;
import com.KidsCampus.user.kinder.MainActivity;
import com.KidsCampus.user.kinder.NormalBoard.ShowBoardListActivity;
import com.KidsCampus.user.kinder.NormalBoard.ShowPrivateListActivity;
import com.KidsCampus.user.kinder.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class BoardFragment extends Fragment {

    private TextView written, scrabbed, popular;
    private RecyclerView recyclerView, main_recyclerview;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private LinearLayout waiting1, waiting2;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_board, container, false);
        waiting1 = view.findViewById(R.id.boardfragment_waiting_1);
        waiting2 = view.findViewById(R.id.boardfragment_waiting_2);
        written = view.findViewById(R.id.boardfragment_writtenfromme);
        scrabbed = view.findViewById(R.id.boardfragment_scrabbed);
        popular = view.findViewById(R.id.boardfragment_popular);
        recyclerView = view.findViewById(R.id.boardfragment_recyclerview);
        main_recyclerview = view.findViewById(R.id.boardfragment_main_recyclerview);
        getBoardinfo();

        ((MainActivity)MainActivity.mContext).toolbar_button.setVisibility(View.GONE);
        ((MainActivity)MainActivity.mContext).toolbar_button2.setVisibility(View.VISIBLE);
        ((MainActivity)MainActivity.mContext).titletext.setText("게시판");

        written.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkLogin() == 0) {
                    show_dialog();
                } else {
                    Intent intent = new Intent(getContext(), ShowPrivateListActivity.class);
                    intent.putExtra("menu", "written");
                    startActivity(intent);
                }
            }
        });

        scrabbed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkLogin() == 0) {
                    show_dialog();
                } else {
                    Intent intent = new Intent(getContext(), ShowPrivateListActivity.class);
                    intent.putExtra("menu", "scrabbed");
                    startActivity(intent);
                }
            }
        });

        popular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkLogin() == 0) {
                    show_dialog();
                } else {
                    Intent intent = new Intent(getContext(), ShowPrivateListActivity.class);
                    intent.putExtra("menu", "popular");
                    startActivity(intent);
                }
            }
        });

        return view;
    }

    private void show_dialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle("로그인 알림")
                .setMessage("로그인 후 사용가능한 기능입니다.\n로그인 하시겠습니까?")
                .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getContext(), "취소하였습니다", Toast.LENGTH_SHORT).show();
                    }
                }).setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
        dialog.show();
    }

    private int checkLogin() {
        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            return 0;
        } else {
            return 1;
        }
    }

    private void getBoardinfo() {
        waiting1.setVisibility(View.VISIBLE);
        waiting2.setVisibility(View.VISIBLE);
        final ArrayList<Map<String, Object>> centerdata = new ArrayList<>();
        db.collection("게시판").whereEqualTo("center", true).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for(DocumentSnapshot snapshot : task.getResult()) {
                        Map<String, Object> centemp = snapshot.getData();
                        centerdata.add(centemp);
                    }
                    main_recyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
                    main_recyclerview.setAdapter(new MyCenterAdapter(centerdata));
                    waiting1.setVisibility(View.GONE);
                }
            }
        });

        final ArrayList<Map<String, Object>> data = new ArrayList<>();
        db.collection("게시판").whereEqualTo("center", false).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for(DocumentSnapshot snapshot : task.getResult()) {
                        Map<String, Object> temp = snapshot.getData();
                        data.add(temp);
                    }
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    recyclerView.setAdapter(new MyAdapter(data));
                    waiting2.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onDetach() {
        ((MainActivity)MainActivity.mContext).titletext.setText("전국 유치원 정보");
        ((MainActivity)MainActivity.mContext).toolbar_button.setVisibility(View.VISIBLE);
        ((MainActivity)MainActivity.mContext).toolbar_button2.setVisibility(View.GONE);
        super.onDetach();
    }

    private class MyAdapter extends RecyclerView.Adapter {
        ArrayList<Map<String, Object>> datas;
        MyAdapter(ArrayList<Map<String, Object>> temp_data) {
            datas = temp_data;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_each_board, viewGroup, false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int i) {
            ((CustomViewHolder)viewHolder).name.setText(datas.get(i).get("name").toString());
            ((CustomViewHolder)viewHolder).description.setText(datas.get(i).get("description").toString());

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(checkLogin() == 0) {
                        show_dialog();
                    } else {
                        Intent intent = new Intent(getContext(), ShowBoardListActivity.class);
                        intent.putExtra("name", ((CustomViewHolder) viewHolder).name.getText().toString());
                        intent.putExtra("anonymous", datas.get(i).get("anonymous").toString());
                        startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return datas.size();
        }
    }

    private class CustomViewHolder extends RecyclerView.ViewHolder {

        TextView name, description;

        CustomViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.item_eachboard_name);
            description = view.findViewById(R.id.item_eachboard_description);
        }
    }

    private class MyCenterAdapter extends RecyclerView.Adapter {
        ArrayList<Map<String, Object>> tep;

        MyCenterAdapter(ArrayList<Map<String, Object>> centerdata) {
            tep = centerdata;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_each_board, viewGroup, false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int i) {
            ((CustomViewHolder)holder).name.setText(tep.get(i).get("name").toString());
            ((CustomViewHolder)holder).description.setText(tep.get(i).get("description").toString());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(checkLogin() == 0) {
                        show_dialog();
                    } else {
                        Intent intent = new Intent(getContext(), ShowBoardListActivity.class);
                        intent.putExtra("name", ((CustomViewHolder) holder).name.getText().toString());
                        intent.putExtra("anonymous", tep.get(i).get("anonymous").toString());
                        intent.putExtra("board_name", tep.get(i).get("name").toString());
                        startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return tep.size();
        }
    }
}
