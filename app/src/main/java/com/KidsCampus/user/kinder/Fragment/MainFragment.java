package com.KidsCampus.user.kinder.Fragment;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.AnyRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.KidsCampus.user.kinder.KInderActivity;
import com.KidsCampus.user.kinder.Location.LocationActivity;
import com.KidsCampus.user.kinder.MainActivity;
import com.KidsCampus.user.kinder.NormalBoard.ShowEachBoardActivity;
import com.KidsCampus.user.kinder.NormalBoard.ShowPrivateListActivity;
import com.KidsCampus.user.kinder.R;
import com.KidsCampus.user.kinder.Search.SearchKinderActivity;
import com.KidsCampus.user.kinder.Search.SearchPopularActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.zip.Inflater;

public class MainFragment extends Fragment {

    private TextView goSearch, goLoc, goSido, today, today_say;
    private LinearLayout goDownload;
    private RecyclerView recyclerView_popular, recyclerView_alerm, recyclerView_message;
    private AdView adView;
    private ImageView eventImage;
    private Button pop_seemore, alerm_seemore, message_seemore, event_seemore;
    private LinearLayout event, pop_nologin, alerm_nologin, message_nologin, pop_waiting, alerm_waiting, message_waiting;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<Map<String, Object>> pop_data = new ArrayList<>();
    private ArrayList<Map<String, Object>> alerm_data = new ArrayList<>();
    private ArrayList<Map<String, Object>> message_data = new ArrayList<>();

    String duri = "http://imnews.imbc.com/issue/report/index.html";
    String luri = "https://firebasestorage.googleapis.com/v0/b/kinder-114b0.appspot.com/o/%EC%B4%88%EA%B8%B0%20%EC%84%B8%ED%8C%85%2F%EC%B4%88%EA%B8%B0%20%EC%84%B8%ED%8C%85%20%EC%9D%B4%EB%AF%B8%EC%A7%80%2Fmbc_logo?alt=media&token=446e32d1-a28b-4ae8-85ce-7112aab46181";
    private String[] loc = {"인천광역시", "서울특별시", "경기도", "충청남도", "충청북도", "세종특별자치시",
            "대전광역시", "전라남도", "전라북도", "광주광역시", "부산광역시", "울산광역시", "경상남도", "대구광역시",
            "경상북도", "강원도", "제주특별자치도"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ((MainActivity)MainActivity.mContext).titletext.setText("홈");

        event = view.findViewById(R.id.home_event);
        event.setVisibility(View.GONE);

        ImageView imageView2 = view.findViewById(R.id.home_logo);
        Glide.with(Objects.requireNonNull(getContext())).load(luri).into(imageView2);

        goSearch = view.findViewById(R.id.home_gosearch);
        goLoc = view.findViewById(R.id.home_goLoc);
        goSido = view.findViewById(R.id.home_goSido);
        goDownload = view.findViewById(R.id.home_goDownload);
        today = view.findViewById(R.id.home_today);
        today_say = view.findViewById(R.id.home_today_say);
        recyclerView_popular = view.findViewById(R.id.home_popular_recyclerview);
        recyclerView_alerm = view.findViewById(R.id.home_alerm_recyclerview);
        recyclerView_message = view.findViewById(R.id.home_message_recyclerview);
        adView = view.findViewById(R.id.home_adview);
        eventImage = view.findViewById(R.id.home_event_imageview);
        pop_seemore = view.findViewById(R.id.home_popular_seemore);
        alerm_seemore = view.findViewById(R.id.home_alerm_seemore);
        message_seemore = view.findViewById(R.id.home_message_seemore);
        event_seemore = view.findViewById(R.id.home_event_seemore);
        pop_nologin = view.findViewById(R.id.home_popular_nologin);
        pop_waiting = view.findViewById(R.id.home_popular_waiting);
        alerm_nologin = view.findViewById(R.id.home_alerm_nologin);
        alerm_waiting = view.findViewById(R.id.home_alerm_waiting);
        message_nologin = view.findViewById(R.id.home_message_nologin);
        message_waiting = view.findViewById(R.id.home_message_waiting);

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        getToday();
        getEvent();
        getPop();
        getalerm();
        getmessage();

        goSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SearchKinderActivity.class);
                startActivity(intent);
            }
        });

        goLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), LocationActivity.class);
                startActivity(intent);
            }
        });

        goSido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), KInderActivity.class);
                intent.putExtra("sido", "서울특별시");
                startActivity(intent);
            }
        });

        goDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(duri));
                startActivity(intent);
            }
        });

        pop_seemore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ShowPrivateListActivity.class);
                intent.putExtra("menu", "popular");
                startActivity(intent);
            }
        });

        alerm_seemore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction()
                                .replace(R.id.main_framelayout, new AlertFragment()).commit();
                    }
                }).start();
            }
        });

        message_seemore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction()
                                .replace(R.id.main_framelayout, new MessageFragment()).commit();
                    }
                }).start();
            }}
        );

        event_seemore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "이벤트 오류", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void getToday() {
        Date date = new Date(System.currentTimeMillis());
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일");
        String getDate = sdf.format(date);
        today.setText(getDate);

        db.collection("오늘의명언").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    final String say = task.getResult().getDocuments().get(0).get("context").toString();
                    today_say.setText(say);
                    today_say.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle("오늘의 명언")
                                    .setMessage(say)
                                    .setPositiveButton("닫기", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    }).show();
                        }
                    });
                } else {
                    today_say.setText("로딩 실패..");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                today_say.setText("로딩 실패..");
            }
        });
    }




    private void getEvent() {


    }

    private void getPop() {
        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            pop_nologin.setVisibility(View.GONE);
            db.collection("실시간인기글").orderBy("write_time").limit(3).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()) {
                        for(DocumentSnapshot snapshot : task.getResult()) {
                            Map<String, Object> temp = snapshot.getData();
                            temp.put("temp_key", snapshot.getId());
                            pop_data.add(temp);
                        }

                        if(pop_data.isEmpty()) {
                            recyclerView_popular.setVisibility(View.GONE);
                            pop_seemore.setVisibility(View.GONE);
                            pop_waiting.setVisibility(View.VISIBLE);
                        } else {
                            recyclerView_popular.setLayoutManager(new LinearLayoutManager(getContext()));
                            recyclerView_popular.setAdapter(new MyPopularAdapter(pop_data));
                            pop_waiting.setVisibility(View.GONE);
                            recyclerView_popular.setVisibility(View.VISIBLE);
                            pop_seemore.setVisibility(View.VISIBLE);
                        }
                    }
                }
            });
        } else {
            pop_waiting.setVisibility(View.GONE);
            pop_nologin.setVisibility(View.VISIBLE);
        }
    }

    private void getalerm() {
        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            alerm_nologin.setVisibility(View.GONE);
            db.collection("사용자").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("alert")
                    .orderBy("time", Query.Direction.DESCENDING).limit(5).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()) {
                        for(DocumentSnapshot snapshot : task.getResult()) {
                            Map<String, Object> temp = snapshot.getData();
                            temp.put("temp_key", snapshot.getId());
                            alerm_data.add(temp);
                        }

                        if(alerm_data.isEmpty()) {
                            recyclerView_alerm.setVisibility(View.GONE);
                            alerm_seemore.setVisibility(View.GONE);
                            alerm_waiting.setVisibility(View.VISIBLE);
                        } else {
                            recyclerView_alerm.setLayoutManager(new LinearLayoutManager(getContext()));
                            recyclerView_alerm.setAdapter(new MyAlermAdapter(alerm_data));
                            alerm_waiting.setVisibility(View.GONE);
                            recyclerView_alerm.setVisibility(View.VISIBLE);
                            alerm_seemore.setVisibility(View.VISIBLE);
                        }
                    }
                }
            });
        } else {
            alerm_waiting.setVisibility(View.GONE);
            alerm_nologin.setVisibility(View.VISIBLE);
        }
    }

    private void getmessage() {
        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            message_nologin.setVisibility(View.GONE);
            db.collection("사용자").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("saved_message")
                    .orderBy("time", Query.Direction.DESCENDING).limit(5).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()) {
                        for(DocumentSnapshot snapshot : task.getResult()) {
                            Map<String, Object> temp = snapshot.getData();
                            temp.put("temp_key", snapshot.getId());
                            message_data.add(temp);
                        }

                        if(message_data.isEmpty()) {
                            recyclerView_message.setVisibility(View.GONE);
                            message_seemore.setVisibility(View.GONE);
                            message_waiting.setVisibility(View.VISIBLE);
                        } else {
                            recyclerView_message.setLayoutManager(new LinearLayoutManager(getContext()));
                            recyclerView_message.setAdapter(new MyMessageAdapter(message_data));
                            message_waiting.setVisibility(View.GONE);
                            recyclerView_message.setVisibility(View.VISIBLE);
                            message_seemore.setVisibility(View.VISIBLE);
                        }
                    }
                }
            });
        } else {
            message_waiting.setVisibility(View.GONE);
            message_nologin.setVisibility(View.VISIBLE);
        }
    }

    private class MyAlermAdapter extends RecyclerView.Adapter {
        ArrayList<Map<String, Object>> data;

        public MyAlermAdapter(ArrayList<Map<String, Object>> alerm_data) {
            this.data = alerm_data;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_home_alert, viewGroup, false);
            return new CustomAltHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            Date date = new Date(Long.parseLong(data.get(i).get("time").toString()));
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("MM/dd hh:mm");
            String getDate = sdf.format(date);

            ((CustomAltHolder)viewHolder).alt_item_title.setText(data.get(i).get("title").toString());
            ((CustomAltHolder)viewHolder).alt_item_context.setText(data.get(i).get("context").toString());
            ((CustomAltHolder)viewHolder).alt_item_time.setText(getDate);
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    private class MyMessageAdapter extends RecyclerView.Adapter {
        ArrayList<Map<String, Object>> data;

        public MyMessageAdapter(ArrayList<Map<String, Object>> message_data) {
            this.data = message_data;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_home_message, viewGroup,false);
            return new CustomMsgHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            ((CustomMsgHolder)viewHolder).msg_item_writer.setText(data.get(i).get("writer").toString());
            ((CustomMsgHolder)viewHolder).msg_item_context.setText(data.get(i).get("context").toString());
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    private class MyPopularAdapter extends RecyclerView.Adapter {
        ArrayList<Map<String, Object>> data;

        public MyPopularAdapter(ArrayList<Map<String, Object>> pop_data) {
            this.data = pop_data;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_home_comment, viewGroup, false);
            return new CustomPopHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
            Date date = new Date(Long.parseLong(data.get(i).get("write_time").toString()));
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("MM/dd hh:mm");
            String getDate = sdf.format(date);
            final String[] board_name = new String[1];

            db.collection("게시판").document(data.get(i).get("collection_key").toString()).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            board_name[0] = documentSnapshot.get("name").toString();
                        }
                    });

            ((CustomPopHolder)viewHolder).pop_item_writer.setText(data.get(i).get("writer").toString());
            ((CustomPopHolder)viewHolder).pop_item_context.setText(data.get(i).get("context").toString());
            ((CustomPopHolder)viewHolder).pop_item_time.setText(getDate);

            Glide.with(Objects.requireNonNull(getContext())).load(Uri.parse(data.get(i).get("profile").toString()))
                    .into(((CustomPopHolder) viewHolder).pop_item_profile);

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), ShowEachBoardActivity.class);
                    ActivityOptions options = ActivityOptions.makeCustomAnimation(getContext(), R.anim.fromright, R.anim.toleft);
                    intent.putExtra("collection_key", data.get(i).get("collection_key").toString());
                    intent.putExtra("document_key", data.get(i).get("document_key").toString());
                    intent.putExtra("board_name", board_name[0]);
                    startActivity(intent, options.toBundle());
                }
            });
        }
        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    private class CustomPopHolder extends RecyclerView.ViewHolder {
        public TextView pop_item_writer, pop_item_context, pop_item_time;
        public ImageView pop_item_profile;

        public CustomPopHolder(View view) {
            super(view);
            pop_item_writer = view.findViewById(R.id.home_pop_writer);
            pop_item_time = view.findViewById(R.id.home_pop_time);
            pop_item_context = view.findViewById(R.id.home_pop_context);
            pop_item_profile = view.findViewById(R.id.home_pop_profile);
        }
    }

    private class CustomMsgHolder extends RecyclerView.ViewHolder {
        public TextView msg_item_writer, msg_item_context;

        public CustomMsgHolder(View view) {
            super(view);
            msg_item_writer = view.findViewById(R.id.home_msg_writer);
            msg_item_context = view.findViewById(R.id.home_msg_context);
        }
    }

    private class CustomAltHolder extends RecyclerView.ViewHolder {
        public TextView alt_item_title, alt_item_context, alt_item_time;
        public CustomAltHolder(View view) {
            super(view);
            alt_item_title = view.findViewById(R.id.home_alt_title);
            alt_item_context = view.findViewById(R.id.home_alt_context);
            alt_item_time = view.findViewById(R.id.home_alt_time);
        }
    }
}
