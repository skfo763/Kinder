package com.KidsCampus.user.kinder.Search;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Context;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.KidsCampus.user.kinder.NormalBoard.ShowEachBoardActivity;
import com.KidsCampus.user.kinder.NormalBoard.ShowPrivateListActivity;
import com.KidsCampus.user.kinder.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;

public class SearchBoardActivity extends AppCompatActivity {

    InputMethodManager imm;
    private ImageView search;
    private RecyclerView recyclerView;
    private EditText searchtext;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private LinearLayout init, nodata, waiting;
    private ProgressBar progressBar;
    public ArrayList<Map<String, Object>> search_data = new ArrayList<>();
    private boolean checkfirst = true;
    private boolean mLockListView = false;
    String text = "";
    Query next = null;
    String collection_key, board_name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_board);
        collection_key = getIntent().getStringExtra("collection_key");
        board_name = getIntent().getStringExtra("board_name");

        search = findViewById(R.id.searchboard_search);
        recyclerView = findViewById(R.id.searchboard_recyclerview);
        searchtext = findViewById(R.id.searchboard_edittext);
        init = findViewById(R.id.searchboard_init);
        nodata = findViewById(R.id.searchboard_nodata);
        waiting = findViewById(R.id.searchboard_waiting);
        progressBar = findViewById(R.id.searchboard_progrssbar);
        recyclerView.setVisibility(View.GONE);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if(!recyclerView.canScrollVertically(1) && !mLockListView) {
                    if(next != null) {
                        mLockListView = true;
                        progressBar.setVisibility(View.VISIBLE);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getInfo(next, collection_key);
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

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchtext.getWindowToken(), 0);

                if(searchtext.getText().toString().isEmpty() || searchtext.getText().toString().length() < 2) {
                    Toast.makeText(SearchBoardActivity.this, "검색어는 두 글자 이상 입력해주세요", Toast.LENGTH_SHORT).show();
                } else {
                    search_data.clear();
                    checkfirst = true;
                    text = searchtext.getText().toString();
                    init.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    nodata.setVisibility(View.GONE);
                    waiting.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Query query = db.collection("게시판").document(collection_key).collection("board_collection")
                                    .orderBy("time", Query.Direction.DESCENDING).limit(20);
                            getInfo(query, collection_key);
                        }
                    }, 500);
                }
                searchtext.setText("");
            }
        });
    }

    private void getInfo(Query query, final String collection_key) {
        if(query != null) {
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()) {
                        for(DocumentSnapshot snapshot : task.getResult()) {
                            String title = snapshot.getData().get("title").toString();
                            if(title.contains(text) || title.equals(text)) {
                                String key = snapshot.getId();
                                Map<String, Object> temp = snapshot.getData();
                                temp.put("document_key", key);
                                search_data.add(temp);
                            }
                        }
                        if(task.getResult().getDocuments().size() >= 1) {
                            DocumentSnapshot lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);
                            next = db.collection("게시판").document(collection_key).collection("board_collection")
                                    .orderBy("time", Query.Direction.DESCENDING).startAfter(lastVisible).limit(20);
                        } else {
                            next = null;
                        }

                        if(search_data.isEmpty()) {
                            waiting.setVisibility(View.GONE);
                            nodata.setVisibility(View.VISIBLE);
                        } else {
                            RecyclerView.Adapter adapter = new MySearchAdapter(search_data);
                            if(checkfirst) {
                                recyclerView.setLayoutManager(new LinearLayoutManager(SearchBoardActivity.this));
                                recyclerView.setAdapter(adapter);
                                nodata.setVisibility(View.GONE);
                                waiting.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                                checkfirst = false;
                            } else {
                                nodata.setVisibility(View.GONE);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    } else {
                        waiting.setVisibility(View.GONE);
                        nodata.setVisibility(View.VISIBLE);
                        Toast.makeText(SearchBoardActivity.this, "검색 실패", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private class MySearchAdapter extends RecyclerView.Adapter {
        ArrayList<Map<String, Object>> search;

        public MySearchAdapter(ArrayList<Map<String, Object>> search_data) {
            /*Collections.sort(search_data, new Comparator<Map<String, Object>>() {
                @Override
                public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                    long first = 0;
                    long second = 0;
                    first = Long.parseLong(o1.get("time").toString());
                    second = Long.parseLong(o2.get("time").toString());
                    return Long.compare(second, first);
                }
            });*/
            this.search = search_data;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_normal_board, viewGroup, false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
            ((CustomViewHolder)viewHolder).item_title.setText(search.get(i).get("title").toString());
            ((CustomViewHolder)viewHolder).item_context.setText(search.get(i).get("context").toString());

            Long time = Long.parseLong(search.get(i).get("time").toString());
            Date date = new Date(time);
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("MM/dd hh:mm");
            String getDate = sdf.format(date);
            ((CustomViewHolder)viewHolder).item_time.setText(getDate);

            ((CustomViewHolder)viewHolder).item_writer.setText(search.get(i).get("writer_nickname").toString());
            ((CustomViewHolder)viewHolder).item_image.setText(search.get(i).get("image_count").toString());
            ((CustomViewHolder)viewHolder).item_like.setText(search.get(i).get("like_count").toString());
            ((CustomViewHolder)viewHolder).item_comments.setText(search.get(i).get("comment_count").toString());

            if(search.get(i).get("thumbnail") == null) {
                ((CustomViewHolder)viewHolder).thumbnail.setVisibility(View.GONE);
            } else {
                Glide.with(viewHolder.itemView.getContext())
                        .load(search.get(i)
                                .get("thumbnail"))
                        .into(((CustomViewHolder)viewHolder).thumbnail)
                        .waitForLayout();
                ((CustomViewHolder)viewHolder).thumbnail.setVisibility(View.VISIBLE);
            }

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SearchBoardActivity.this, ShowEachBoardActivity.class);
                    ActivityOptions options = ActivityOptions.makeCustomAnimation(SearchBoardActivity.this, R.anim.fromright, R.anim.toleft);
                    intent.putExtra("collection_key", collection_key);
                    intent.putExtra("document_key", search.get(i).get("document_key").toString());
                    intent.putExtra("board_name", board_name);
                    startActivity(intent, options.toBundle());
                }
            });
        }

        @Override
        public int getItemCount() {
            return search.size();
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
