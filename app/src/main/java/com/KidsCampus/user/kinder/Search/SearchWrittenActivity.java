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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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

public class SearchWrittenActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_written);
        search = findViewById(R.id.searchwritten_search);
        recyclerView = findViewById(R.id.searchwritten_recyclerview);
        searchtext = findViewById(R.id.searchwritten_edittext);
        init = findViewById(R.id.searchwritten_init);
        nodata = findViewById(R.id.searchwritten_nodata);
        waiting = findViewById(R.id.searchwritten_waiting);
        progressBar = findViewById(R.id.searchwritten_progrssbar);
        recyclerView.setVisibility(View.GONE);

        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if(!recyclerView.canScrollVertically(1) && !mLockListView) {
                    if(next != null) {
                        mLockListView = true;
                        progressBar.setVisibility(View.VISIBLE);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getInfo(next);
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
                    Toast.makeText(SearchWrittenActivity.this, "검색어는 두 글자 이상 입력해주세요", Toast.LENGTH_SHORT).show();
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
                            Query query = db.collection("사용자").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .collection("user_write").orderBy("write_time", Query.Direction.DESCENDING).limit(20);
                            getInfo(query);
                        }
                    }, 500);
                    searchtext.setText("");
                }
            }
        });
    }

    private void getInfo(Query query) {
        if(query != null) {
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()) {
                        for(DocumentSnapshot snapshot : task.getResult()) {
                            String title = snapshot.getData().get("title").toString();
                            if(title.contains(text) || title.equals(text)) {
                                Map<String, Object> sd = snapshot.getData();
                                search_data.add(sd);
                            }

                            if(task.getResult().getDocuments().size() >= 1) {
                                DocumentSnapshot lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);
                                next = db.collection("사용자").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .collection("user_write").orderBy("write_time", Query.Direction.DESCENDING).startAfter(lastVisible).limit(20);
                            } else {
                                next = null;
                            }

                            if(search_data.isEmpty()) {
                                waiting.setVisibility(View.GONE);
                                nodata.setVisibility(View.VISIBLE);
                            } else {
                                RecyclerView.Adapter adapter = new MySearchAdapter(search_data);
                                if(checkfirst) {
                                    recyclerView.setLayoutManager(new LinearLayoutManager(SearchWrittenActivity.this));
                                    recyclerView.setAdapter(adapter);
                                    waiting.setVisibility(View.GONE);
                                    recyclerView.setVisibility(View.VISIBLE);
                                    nodata.setVisibility(View.GONE);
                                    checkfirst = false;
                                } else {
                                    nodata.setVisibility(View.GONE);
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        }
                    } else {
                        waiting.setVisibility(View.GONE);
                        nodata.setVisibility(View.VISIBLE);
                        Toast.makeText(SearchWrittenActivity.this, "검색 실패", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private class MySearchAdapter extends RecyclerView.Adapter {
        ArrayList<Map<String, Object>> data;
        public MySearchAdapter(ArrayList<Map<String, Object>> search_data) {
            this.data = search_data;
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

            db.collection("게시판").document(data.get(i).get("collection_key").toString()).collection("board_collection")
                    .document(data.get(i).get("document_key").toString()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Map<String, Object> adapter_data = documentSnapshot.getData();
                    ((CustomViewHolder)viewHolder).item_title.setText(adapter_data.get("title").toString());
                    ((CustomViewHolder)viewHolder).item_context.setText(adapter_data.get("context").toString());

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

            db.collection("게시판").document(data.get(i).get("collection_key").toString()).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            board_name[0] = documentSnapshot.get("name").toString();
                        }
                    });

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SearchWrittenActivity.this, ShowEachBoardActivity.class);
                    ActivityOptions options = ActivityOptions.makeCustomAnimation(SearchWrittenActivity.this, R.anim.fromright, R.anim.toleft);
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
