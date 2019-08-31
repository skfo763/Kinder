package com.KidsCampus.user.kinder.NormalBoard;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Vibrator;
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
import com.KidsCampus.user.kinder.Search.SearchBoardActivity;
import com.KidsCampus.user.kinder.Search.SearchKinderActivity;
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

public class ShowBoardListActivity extends AppCompatActivity {

    private TextView titletext;
    private ImageView goback, write, search, refresh;
    private RecyclerView recyclerView;
    private LinearLayout waiting, waiting2;
    private ProgressBar progressBar;
    private boolean mLockListView = false;
    private boolean checkfirst = true;
    final String[] id = new String[1];
    final ArrayList<Map<String, Object>> board_data = new ArrayList<>();
    Query next;
    String collection_key = "";
    AdView adview;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String title, anonymous, board_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_board_list);

        title = getIntent().getStringExtra("name");
        anonymous = getIntent().getStringExtra("anonymous");
        board_name = getIntent().getStringExtra("board_name");
        titletext = findViewById(R.id.showboardlist_title);
        adview = findViewById(R.id.showboardlist_adview);
        recyclerView = findViewById(R.id.showboardlist_recyclerview);
        refresh = findViewById(R.id.showboardlist_refresh);
        goback = findViewById(R.id.showboardlist_goback);
        write = findViewById(R.id.showboardlist_write);
        search = findViewById(R.id.showboardlist_search);
        waiting = findViewById(R.id.showboardlist_waiting);
        waiting2 = findViewById(R.id.showboardlist_waiting2);
        progressBar = findViewById(R.id.showboardlist_page_progressbar);
        titletext.setText(title);

        waiting2.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        getBoardInfo();

        AdRequest adRequest = new AdRequest.Builder().build();
        adview.loadAd(adRequest);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull final RecyclerView recyclerView, int newState) {
                if(!recyclerView.canScrollVertically(1) && !mLockListView) {
                    if(next != null) {
                        mLockListView = true;
                        progressBar.setVisibility(View.VISIBLE);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getInfo2(next, collection_key);
                                progressBar.setVisibility(View.GONE);
                                mLockListView = false;
                            }
                        }, 500);
                    }
                } else {
                    super.onScrollStateChanged(recyclerView, newState);
                }
            }
        });

        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowBoardListActivity.this, WriteBoardActivity.class);
                intent.putExtra("collection_key", id[0]);
                intent.putExtra("anonymous", anonymous);
                intent.putExtra("name", title);
                intent.putExtra("board_name", board_name);
                startActivity(intent);
                finish();
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowBoardListActivity.this, SearchBoardActivity.class);
                intent.putExtra("collection_key", collection_key);
                intent.putExtra("board_name", board_name);
                startActivity(intent);
            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                waiting.setVisibility(View.GONE);
                waiting2.setVisibility(View.VISIBLE);
                recyclerView.setAdapter(null);
                checkfirst = true;
                recyclerView.setNestedScrollingEnabled(false);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        board_data.clear();
                        refreshBoard();
                    }
                }, 500);
                recyclerView.setNestedScrollingEnabled(true);
            }
        });
    }

    private void refreshBoard() {
        db.collection("게시판").document(collection_key).collection("board_collection")
                .orderBy("time", Query.Direction.DESCENDING).limit(20)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for(DocumentSnapshot snapshot : task.getResult()) {
                        String key = snapshot.getId();
                        Map<String, Object> temp = snapshot.getData();
                        temp.put("document_key", key);
                        board_data.add(temp);
                    }

                    if(task.getResult().getDocuments().size() >= 20) {
                        DocumentSnapshot lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);
                        next = db.collection("게시판").document(ShowBoardListActivity.this.collection_key).collection("board_collection")
                                .orderBy("time", Query.Direction.DESCENDING).startAfter(lastVisible).limit(20);
                    } else {
                        next = null;
                    }

                    if(board_data.isEmpty()) {
                        recyclerView.setVisibility(View.GONE);
                        waiting2.setVisibility(View.GONE);
                        waiting.setVisibility(View.VISIBLE);
                    } else {
                        RecyclerView.Adapter adapter = new MyShowBoardListAdapter(board_data, collection_key);
                        if(checkfirst) {
                            waiting.setVisibility(View.GONE);
                            waiting2.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            recyclerView.setLayoutManager(new LinearLayoutManager(ShowBoardListActivity.this));
                            recyclerView.setAdapter(adapter);
                            checkfirst = false;
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                    }
                    waiting2.setVisibility(View.GONE);
                }
            }
        });
    }

    private void getBoardInfo() {
        db.collection("게시판").whereEqualTo("name", title)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for(DocumentSnapshot snapshot : task.getResult()) {
                        id[0] = snapshot.getId();
                    }
                    collection_key = id[0];
                    Query first = db.collection("게시판").document(collection_key).collection("board_collection")
                            .orderBy("time", Query.Direction.DESCENDING).limit(20);
                    getInfo2(first, collection_key);
                }
            }
        });
    }

    private void getInfo2(final Query query, final String s) {
        if(query != null) {
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()) {
                        for (DocumentSnapshot snapshot : task.getResult()) {
                            if(snapshot.exists()) {
                                String key = snapshot.getId();
                                Map<String, Object> temp = snapshot.getData();
                                temp.put("document_key", key);
                                board_data.add(temp);
                            }
                        }

                        if(task.getResult().getDocuments().size() >= 1) {
                            DocumentSnapshot lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);
                            next = db.collection("게시판").document(ShowBoardListActivity.this.collection_key).collection("board_collection")
                                    .orderBy("time", Query.Direction.DESCENDING).startAfter(lastVisible).limit(20);
                        } else {
                            next = null;
                        }

                        if(board_data.isEmpty()) {
                            recyclerView.setVisibility(View.GONE);
                            waiting2.setVisibility(View.GONE);
                            waiting.setVisibility(View.VISIBLE);
                        } else {
                            RecyclerView.Adapter adapter = new MyShowBoardListAdapter(board_data, s);
                            if(checkfirst) {
                                waiting.setVisibility(View.GONE);
                                waiting2.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                                recyclerView.setLayoutManager(new LinearLayoutManager(ShowBoardListActivity.this));
                                recyclerView.setAdapter(adapter);
                                checkfirst = false;

                            } else {
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
            });
        }
    }

    private class MyShowBoardListAdapter extends RecyclerView.Adapter {
        ArrayList<Map<String, Object>> data;
        String key;

        MyShowBoardListAdapter(ArrayList<Map<String, Object>> board_data, String s) {
            data = board_data;
            key = s;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_normal_board, viewGroup, false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
            final Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

            ((CustomViewHolder)viewHolder).item_title.setText(data.get(i).get("title").toString());
            ((CustomViewHolder)viewHolder).item_context.setText(data.get(i).get("context").toString());

            Long time = Long.parseLong(data.get(i).get("time").toString());
            Date date = new Date(time);
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("MM/dd hh:mm");
            String getDate = sdf.format(date);
            ((CustomViewHolder)viewHolder).item_time.setText(getDate);

            ((CustomViewHolder)viewHolder).item_writer.setText(data.get(i).get("writer_nickname").toString());
            ((CustomViewHolder)viewHolder).item_image.setText(data.get(i).get("image_count").toString());
            ((CustomViewHolder)viewHolder).item_like.setText(data.get(i).get("like_count").toString());
            ((CustomViewHolder)viewHolder).item_comments.setText(data.get(i).get("comment_count").toString());

            if(data.get(i).get("thumbnail") == null) {
                ((CustomViewHolder)viewHolder).thumbnail.setVisibility(View.GONE);
            } else {
                Glide.with(viewHolder.itemView.getContext())
                        .load(data.get(i)
                        .get("thumbnail"))
                        .into(((CustomViewHolder)viewHolder).thumbnail)
                        .waitForLayout();
                ((CustomViewHolder)viewHolder).thumbnail.setVisibility(View.VISIBLE);
            }

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ShowBoardListActivity.this, ShowEachBoardActivity.class);
                    ActivityOptions options = ActivityOptions.makeCustomAnimation(ShowBoardListActivity.this, R.anim.fromright, R.anim.toleft);
                    intent.putExtra("collection_key", key);
                    intent.putExtra("document_key", data.get(i).get("document_key").toString());
                    intent.putExtra("board_name", board_name);
                    startActivity(intent, options.toBundle());
                }
            });

            viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(data.get(i).get("writer").toString().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())) {
                        vibrator.vibrate(50);
                        String item[] = {"삭제"};
                        AlertDialog.Builder maker = new AlertDialog.Builder(ShowBoardListActivity.this);
                        maker.setItems(item, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(which == 0) {
                                    db.collection("사용자").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("user_write")
                                            .whereEqualTo("document_key", data.get(i).get("document_key")).get()
                                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            String key = queryDocumentSnapshots.getDocuments().get(0).getId();
                                            db.collection("사용자").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("user_write")
                                                    .document(key).delete();
                                        }
                                    });

                                    db.collection("게시판").document(key).collection("board_collection")
                                            .document(data.get(i).get("document_key").toString()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            refreshBoard();
                                        }
                                    });
                                }
                            }
                        }).show();
                    }
                    return true;
                }
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    private class CustomViewHolder extends RecyclerView.ViewHolder {

        public TextView item_title, item_context, item_time, item_writer, item_image, item_like, item_comments;
        public ImageView thumbnail;


        public CustomViewHolder(View view) {
            super(view);

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
