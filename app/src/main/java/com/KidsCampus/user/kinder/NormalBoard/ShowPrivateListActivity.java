package com.KidsCampus.user.kinder.NormalBoard;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Handler;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.KidsCampus.user.kinder.MainActivity;
import com.KidsCampus.user.kinder.R;
import com.KidsCampus.user.kinder.Search.SearchKinderActivity;
import com.KidsCampus.user.kinder.Search.SearchPopularActivity;
import com.KidsCampus.user.kinder.Search.SearchScrabbedActivity;
import com.KidsCampus.user.kinder.Search.SearchWrittenActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
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


public class ShowPrivateListActivity extends AppCompatActivity {

    private TextView title;
    private ImageView goback, search;
    private RecyclerView recyclerView;
    private AdView adview;
    private ProgressBar progressBar;
    private LinearLayout waiting, noboard;
    private String menu;
    private boolean mLockListView = false;
    private boolean checkfirst = true;
    ArrayList<Map<String, Object>> key_data = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Query first_write = FirebaseFirestore.getInstance().collection("사용자").document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
            .collection("user_write").orderBy("write_time", Query.Direction.DESCENDING).limit(20);
    Query first_scrab = FirebaseFirestore.getInstance().collection("사용자").document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
            .collection("scrab").orderBy("scrab_time", Query.Direction.DESCENDING).limit(20);
    Query first_pop = FirebaseFirestore.getInstance().collection("실시간인기글").orderBy("write_time", Query.Direction.DESCENDING).limit(20);
    Query next_write, next_scrab, next_popular;
    RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_private_list);
        menu = getIntent().getStringExtra("menu");
        goback = findViewById(R.id.showprivate_goback);
        search = findViewById(R.id.showprivate_search);
        recyclerView = findViewById(R.id.showprivate_recyclerview);
        progressBar = findViewById(R.id.showprivate_progressbar);
        adview = findViewById(R.id.showprivate_adview);
        title = findViewById(R.id.showprivate_title);
        noboard = findViewById(R.id.showprivate_noboard);
        waiting = findViewById(R.id.showprivate_waiting);

        AdRequest adRequest = new AdRequest.Builder().build();
        adview.loadAd(adRequest);

        recyclerView.setVisibility(View.GONE);
        waiting.setVisibility(View.VISIBLE);
        setData(menu);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(menu) {
                    case "written" :
                        Intent intent = new Intent(ShowPrivateListActivity.this, SearchWrittenActivity.class);
                        startActivity(intent);
                        return;
                    case "scrabbed" :
                        Intent intent2 = new Intent(ShowPrivateListActivity.this, SearchScrabbedActivity.class);
                        startActivity(intent2);
                        return;
                    case "popular" :
                        Intent intent3 = new Intent(ShowPrivateListActivity.this, SearchPopularActivity.class);
                        startActivity(intent3);
                        return;
                    default:
                        break;
                }
            }
        });

        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull final RecyclerView recyclerView, int newState) {
                if(!recyclerView.canScrollVertically(1) && !mLockListView) {
                    if(menu.equals("written")) {
                        if(next_write != null) {
                            mLockListView = true;
                            progressBar.setVisibility(View.VISIBLE);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    setWritten(next_write);
                                    progressBar.setVisibility(View.GONE);
                                    mLockListView = false;
                                }
                            }, 500);
                        }
                    } else if(menu.equals("scrabbed")) {
                        if(next_scrab != null) {
                            mLockListView = true;
                            progressBar.setVisibility(View.VISIBLE);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    setScrab(next_scrab);
                                    progressBar.setVisibility(View.GONE);
                                    mLockListView = false;
                                }
                            }, 500);
                        }
                    } else if(menu.equals("popular")) {
                        if(next_popular != null) {
                            mLockListView = true;
                            progressBar.setVisibility(View.VISIBLE);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    setHotBoard(next_popular);
                                    progressBar.setVisibility(View.GONE);
                                    mLockListView = false;
                                }
                            }, 500);
                        }
                    }
                }
            }
        });
    }

    private void setData(String menu) {
        assert menu != null;
        switch(menu) {
            case "written" :
                title.setText("내가 쓴 글");
                setWritten(first_write);
                return;
            case "scrabbed" :
                title.setText("스크랩");
                setScrab(first_scrab);
                return;
            case "popular" :
                title.setText("실시간 인기 글");
                setHotBoard(first_pop);
                return;
            default:
                break;
        }
    }

    private void setHotBoard(Query first) {
        if (first != null) {
            first.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot snapshot : task.getResult()) {
                            if (snapshot.exists()) {
                                key_data.add(snapshot.getData());
                            }
                        }

                        if (task.getResult().getDocuments().size() >= 1) {
                            DocumentSnapshot lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);
                            next_popular = db.collection("실시간인기글").orderBy("write_time", Query.Direction.DESCENDING).startAfter(lastVisible).limit(20);
                        } else {
                            next_popular = null;
                        }

                        if (key_data.isEmpty()) {
                            waiting.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.GONE);
                            noboard.setVisibility(View.VISIBLE);
                        } else {
                            if (checkfirst) {
                                noboard.setVisibility(View.GONE);
                                waiting.setVisibility(View.GONE);
                                adapter = new MyPrivateAdapter(key_data);
                                recyclerView.setLayoutManager(new LinearLayoutManager(ShowPrivateListActivity.this));
                                recyclerView.setAdapter(adapter);
                                recyclerView.setVisibility(View.VISIBLE);
                                checkfirst = false;
                            } else {
                                waiting.setVisibility(View.GONE);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
            });
        }
    }

    private void setWritten(Query first) {
        if(first != null) {
            first.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot snapshot : task.getResult()) {
                            if (snapshot.exists()) {
                                key_data.add(snapshot.getData());
                            }
                        }

                        if (task.getResult().getDocuments().size() >= 1) {
                            DocumentSnapshot lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);
                            next_write = db.collection("게시판").document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).collection("user_write")
                                    .orderBy("write_time", Query.Direction.DESCENDING).startAfter(lastVisible);
                        } else {
                            next_write = null;
                        }

                        if (key_data.isEmpty()) {
                            waiting.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.GONE);
                            noboard.setVisibility(View.VISIBLE);
                        } else {
                            noboard.setVisibility(View.GONE);
                            if (checkfirst) {
                                waiting.setVisibility(View.GONE);
                                adapter = new MyPrivateAdapter(key_data);
                                recyclerView.setLayoutManager(new LinearLayoutManager(ShowPrivateListActivity.this));
                                recyclerView.setAdapter(adapter);
                                recyclerView.setVisibility(View.VISIBLE);
                                checkfirst = false;
                            } else {
                                waiting.setVisibility(View.GONE);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
            });
        }
    }

    private void setScrab(Query first) {
        if(first != null) {
            first.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot snapshot : task.getResult()) {
                            key_data.add(snapshot.getData());
                        }

                        if (task.getResult().getDocuments().size() >= 1) {
                            DocumentSnapshot lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);
                            next_scrab = db.collection("사용자").document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).collection("scrab")
                                    .orderBy("scrab_time", Query.Direction.DESCENDING).startAfter(lastVisible);
                        } else {
                            next_scrab = null;
                        }

                        if (key_data.isEmpty()) {
                            waiting.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.GONE);
                            noboard.setVisibility(View.VISIBLE);
                        } else {
                            noboard.setVisibility(View.GONE);
                            if (checkfirst) {
                                adapter = new MyPrivateAdapter(key_data);
                                waiting.setVisibility(View.GONE);
                                recyclerView.setLayoutManager(new LinearLayoutManager(ShowPrivateListActivity.this));
                                recyclerView.setAdapter(adapter);
                                recyclerView.setVisibility(View.VISIBLE);
                                checkfirst = false;
                            } else {
                                recyclerView.setVisibility(View.VISIBLE);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
            });
        }
    }

    private class MyPrivateAdapter extends RecyclerView.Adapter {

        ArrayList<Map<String, Object>> adapter_key;
        
        MyPrivateAdapter(ArrayList<Map<String, Object>> key_data) {
            this.adapter_key = key_data;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_normal_board, viewGroup, false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int i) {
            ((CustomViewHolder)viewHolder).waiting.setVisibility(View.VISIBLE);
            final String[] board_name = new String[1];

            db.collection("게시판").document(adapter_key.get(i).get("collection_key").toString()).collection("board_collection")
                    .document(adapter_key.get(i).get("document_key").toString()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Map<String, Object> adapter_data = documentSnapshot.getData();
                    ((ShowPrivateListActivity.CustomViewHolder)viewHolder).item_title.setText(adapter_data.get("title").toString());
                    ((ShowPrivateListActivity.CustomViewHolder)viewHolder).item_context.setText(adapter_data.get("context").toString());

                    Long time = Long.parseLong(adapter_data.get("time").toString());
                    Date date = new Date(time);
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("MM/dd hh:mm");
                    String getDate = sdf.format(date);
                    ((CustomViewHolder)viewHolder).item_time.setText(getDate);

                    ((CustomViewHolder)viewHolder).item_writer.setText(adapter_data.get("writer_nickname").toString());
                    ((CustomViewHolder)viewHolder).item_image.setText(adapter_data.get("image_count").toString());
                    ((CustomViewHolder)viewHolder).item_like.setText(adapter_data.get("like_count").toString());
                    ((CustomViewHolder)viewHolder).item_comments.setText(adapter_data.get("comment_count").toString());

                    if(adapter_data.get("thumbnail") == null) {
                        ((CustomViewHolder)viewHolder).thumbnail.setVisibility(View.GONE);
                    } else {
                        Glide.with(viewHolder.itemView.getContext()).load(adapter_data.get("thumbnail")).into(((CustomViewHolder)viewHolder).thumbnail);
                        ((CustomViewHolder)viewHolder).thumbnail.setVisibility(View.VISIBLE);
                    }
                    ((CustomViewHolder)viewHolder).waiting.setVisibility(View.GONE);
                }
            });

            db.collection("게시판").document(adapter_key.get(i).get("collection_key").toString()).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    board_name[0] = documentSnapshot.get("name").toString();
                }
            });

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ShowPrivateListActivity.this, ShowEachBoardActivity.class);
                    ActivityOptions options = ActivityOptions.makeCustomAnimation(ShowPrivateListActivity.this, R.anim.fromright, R.anim.toleft);
                    intent.putExtra("collection_key", adapter_key.get(i).get("collection_key").toString());
                    intent.putExtra("document_key", adapter_key.get(i).get("document_key").toString());
                    intent.putExtra("board_name", board_name[0]);
                    startActivity(intent, options.toBundle());
                }
            });
        }

        @Override
        public int getItemCount() {
            return key_data.size();
        }
    }

    private class CustomViewHolder extends RecyclerView.ViewHolder {

        public TextView item_title, item_context, item_time, item_writer, item_image, item_like, item_comments;
        public ImageView thumbnail;
        public LinearLayout waiting;
        
        public CustomViewHolder(View view) {
            super(view);

            waiting = view.findViewById(R.id.normalboard_waiting);
            item_title = view.findViewById(R.id.context_title);
            item_context = view.findViewById(R.id.context_context);
            item_time = view.findViewById(R.id.context_time);
            item_writer = view.findViewById(R.id.context_writer);
            item_image = view.findViewById(R.id.context_image);
            item_like = view.findViewById(R.id.context_like);
            item_comments = view.findViewById(R.id.context_comments);
            thumbnail = view.findViewById(R.id.thumbnail_image);
        }
    }
}
