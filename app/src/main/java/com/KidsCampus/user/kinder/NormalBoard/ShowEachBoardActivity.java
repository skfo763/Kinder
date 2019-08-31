package com.KidsCampus.user.kinder.NormalBoard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.KidsCampus.user.kinder.Message.SendMessageActivity;
import com.KidsCampus.user.kinder.Models.NotificationModels;
import com.KidsCampus.user.kinder.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ShowEachBoardActivity extends AppCompatActivity {

    public static Context mContext;
    private ImageView profile, image_alone, comment_send, goback, report, send_twit, refresh_button;
    private TextView title, writer, time, context, image_count, like_count, comment_count, scrabbed_count, name;
    private RecyclerView image_recyclerview, comments_recyclerview;
    private EditText comment_edittext;
    private CheckBox checkBox;
    private ScrollView scrollView;
    private Uri thumbnailUri;
    private LinearLayout scrab_layout, like_layout, waiting;
    public FrameLayout frameLayout;
    public int setSubComment = 0;
    public String target_comment_Key = "";
    public String writer_id, board_name;
    AdView adView;
    String collection_key, document_key;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_each_board);
        mContext = this;
        collection_key = getIntent().getStringExtra("collection_key");
        document_key = getIntent().getStringExtra("document_key");
        board_name = getIntent().getStringExtra("board_name");

        name = findViewById(R.id.showeachboard_title);
        profile = findViewById(R.id.showeachboard_profile);
        title = findViewById(R.id.showeachboard_name);
        image_alone = findViewById(R.id.showeachboard_image_alone);
        comment_send = findViewById(R.id.showeachboard_comments_send);
        goback = findViewById(R.id.showeachboard_goback);
        report = findViewById(R.id.showeachboard_warning);
        send_twit = findViewById(R.id.showeachboard_sendmessage);
        writer = findViewById(R.id.showeachboard_writer_nickname);
        time = findViewById(R.id.showeachboard_time);
        context = findViewById(R.id.showeachboard_context);
        image_count = findViewById(R.id.showeachboard_image_count);
        like_count = findViewById(R.id.showeachboard_like_count);
        comment_count = findViewById(R.id.showeachboard_comments_count);
        scrabbed_count = findViewById(R.id.showeachboard_scrabbed_count);
        image_recyclerview = findViewById(R.id.showeachboard_image_many);
        comments_recyclerview = findViewById(R.id.showeachboard_comments);
        comment_edittext = findViewById(R.id.showeachboard_comments_edittext);
        adView = findViewById(R.id.showeachboard_adview);
        checkBox = findViewById(R.id.showeachboard_comments_checkbox);
        scrollView = findViewById(R.id.showeachboard_scrollview);
        frameLayout = findViewById(R.id.showeachboard_framelayout);
        like_layout = findViewById(R.id.showeachboard_like_layout);
        scrab_layout = findViewById(R.id.showeachboard_scrabbed_layout);
        refresh_button = findViewById(R.id.showeachboard_refresh);
        waiting = findViewById(R.id.showeachboard_waiting);

        name.setText(board_name);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        getData();
        refereshData();

        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        refresh_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                waiting.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refereshData();
                        refreshLike();
                        refreshScrab();
                        waiting.setVisibility(View.GONE);
                    }
                }, 500);

            }
        });

        image_alone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Uri> uriArrayList = new ArrayList<>();
                uriArrayList.add(thumbnailUri);
                assert image_alone.getResources() != null;
                showImages(uriArrayList);
            }
        });

        send_twit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowEachBoardActivity.this, SendMessageActivity.class);
                intent.putExtra("destination", writer_id);
                startActivity(intent);
            }
        });

        comment_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(comment_edittext.getText().toString().isEmpty()) {
                    Toast.makeText(ShowEachBoardActivity.this, "댓글을 입력해주세요", Toast.LENGTH_SHORT).show();
                } else {
                    InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    assert inputMethodManager != null;
                    inputMethodManager.hideSoftInputFromWindow(comment_edittext.getWindowToken(), 0);
                    sendComments(comment_edittext.getText().toString());
                    comment_edittext.setText("");
                }
            }
        });

        like_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ShowEachBoardActivity.this);
                builder.setMessage("게시물을 추천하시겠습니까?")
                        .setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db.collection("게시판").document(collection_key).collection("board_collection")
                                        .document(document_key).collection("recommend")
                                        .document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if(documentSnapshot.exists()) {
                                            Toast.makeText(ShowEachBoardActivity.this, "이미 추천하셨습니다.", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Map<String, Object> rec_user = new HashMap<>();
                                            rec_user.put("Uid", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                                            db.collection("게시판").document(collection_key).collection("board_collection").document(document_key)
                                                    .collection("recommend").document(Objects.requireNonNull(FirebaseAuth.getInstance()
                                                    .getCurrentUser()).getUid()).set(rec_user);

                                            db.collection("게시판").document(collection_key).collection("board_collection")
                                                    .document(document_key).get()
                                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                            Map<String, Object> count = documentSnapshot.getData();
                                                            String title = count.get("title").toString();
                                                            String uri = count.get("profile_uri").toString();
                                                            long writetime = Long.parseLong(count.get("time").toString());
                                                            int num = Integer.parseInt(count.get("like_count").toString()) + 1;
                                                            count.put("like_count", num);
                                                            db.collection("게시판").document(collection_key).collection("board_collection")
                                                                    .document(document_key).set(count);

                                                            if(num >= 10) {
                                                                Map<String, Object> hotboard = new HashMap<>();
                                                                hotboard.put("collection_key", collection_key);
                                                                hotboard.put("document_key", document_key);
                                                                hotboard.put("write_time", writetime);
                                                                hotboard.put("writer", writer.getText().toString());
                                                                hotboard.put("context", context.getText().toString());
                                                                hotboard.put("title", title);
                                                                hotboard.put("profile", uri);
                                                                db.collection("실시간인기글").add(hotboard);
                                                            }
                                                            refreshLike();
                                                        }
                                                    });
                                        }
                                    }
                                });
                            }
                        }).setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(ShowEachBoardActivity.this, "취소하였습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
            }
        });

        scrab_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ShowEachBoardActivity.this);
                builder.setMessage("스크랩하시겠습니까?")
                        .setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                long scrab_time = System.currentTimeMillis();
                                Map<String, Object> scrabdata = new HashMap<>();
                                scrabdata.put("title", title.getText().toString());
                                scrabdata.put("collection_key", collection_key);
                                scrabdata.put("document_key", document_key);
                                scrabdata.put("scrab_time", scrab_time);
                                db.collection("사용자").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("scrab")
                                        .add(scrabdata).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                        if(task.isSuccessful()) {
                                            db.collection("게시판").document(collection_key).collection("board_collection").document(document_key).get()
                                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                            Map<String, Object> count = documentSnapshot.getData();
                                                            int num = Integer.parseInt(count.get("scrab_count").toString()) + 1;
                                                            count.put("scrab_count", num);
                                                            db.collection("게시판").document(collection_key).collection("board_collection").document(document_key).set(count);
                                                            refreshScrab();
                                                        }
                                                    });
                                    }
                                    }
                                });
                            }
                        }).setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(ShowEachBoardActivity.this, "취소하였습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
            }
        });

        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ShowEachBoardActivity.this);
                builder.setMessage("해당 게시글을 신고하시겠습니까?")
                        .setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db.collection("게시판").document(collection_key).collection("board_collection").document(document_key).collection("report")
                                        .document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                if(documentSnapshot.exists()) {
                                                    Toast.makeText(ShowEachBoardActivity.this, "이미 추천하셨습니다.", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Map<String, Object> report_data = documentSnapshot.getData();
                                                    int rp_cnt = Integer.parseInt(report_data.get("report_count").toString()) + 1;

                                                    if (rp_cnt <= 30) {
                                                        report_data.put("report_count", rp_cnt);
                                                        db.collection("게시판").document(collection_key).collection("board_collection").document(document_key).set(report_data)
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        Toast.makeText(ShowEachBoardActivity.this, "신고가 완료되었습니다.", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                    } else {
                                                        db.collection("게시판").document(collection_key).collection("board_collection").document(document_key).delete()
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        Toast.makeText(ShowEachBoardActivity.this, "신고가 누적되어 게시물이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                                                        onBackPressed();
                                                                    }
                                                                });
                                                    }
                                                }
                                            }
                                        });
                            }
                        }).setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(ShowEachBoardActivity.this, "취소하였습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }).show();
            }
        });
    }

    private void refreshLike() {
        db.collection("게시판").document(collection_key).collection("board_collection").document(document_key).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()) {
                            like_count.setText(documentSnapshot.get("like_count").toString());
                        } else {
                            like_count.setText("0");
                        }

                    }
                });
    }

    private void refreshScrab() {
        db.collection("게시판").document(collection_key).collection("board_collection").document(document_key).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        scrabbed_count.setText(documentSnapshot.get("scrab_count").toString());
                    }
                });
    }

    private void sendComments(final String text) {
        final Map<String, Object> alert_data = new HashMap<>();
        alert_data.put("title", name.getText().toString());
        alert_data.put("collection_key", collection_key);
        alert_data.put("document_key", document_key);
        alert_data.put("time", System.currentTimeMillis());
        alert_data.put("context", text);

        if(setSubComment == 1) {
            long now = System.currentTimeMillis();
            final Map<String, Object> subcomment = new HashMap<>();
            subcomment.put("comment", text);
            subcomment.put("time", now);
            subcomment.put("profile_uri", FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString());
            if (checkBox.isChecked()) {
                if(writer_id.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    subcomment.put("writer_id", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                    subcomment.put("writer_nickname", "익명(글쓴이)");
                } else {
                    subcomment.put("writer_id", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                    subcomment.put("writer_nickname", "익명");
                }
            } else {
                subcomment.put("writer_id", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                subcomment.put("writer_nickname", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName());
            }
            db.collection("게시판").document(collection_key).collection("board_collection").document(document_key).collection("comments")
                    .document(target_comment_Key).collection("subcomments").add(subcomment).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    refereshData();
                }
            });

            db.collection("게시판").document(collection_key).collection("board_collection").document(document_key).collection("comments")
                    .document(target_comment_Key).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    final String comment_writer_id = documentSnapshot.get("comment_writer_id").toString();
                    db.collection("사용자").document(comment_writer_id).collection("alert").add(alert_data).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if(task.isSuccessful()) {
                                FirebaseFirestore.getInstance().collection("사용자").document(comment_writer_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        String token = documentSnapshot.get("token").toString();
                                        sendFCM(text, token);
                                    }
                                });
                            }
                        }
                    });
                }
            });

            target_comment_Key="";
            setSubComment = 0;

        } else {
            long now = System.currentTimeMillis();
            final Map<String, Object> comment = new HashMap<>();
            comment.put("comment", comment_edittext.getText().toString());
            comment.put("time", now);
            comment.put("profile_uri", FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString());
            comment.put("comment_writer_id", FirebaseAuth.getInstance().getCurrentUser().getUid());
            if (checkBox.isChecked()) {
                if(writer_id.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    comment.put("writer_nickname", "익명(글쓴이)");
                } else {
                    comment.put("writer_nickname", "익명");
                }
            } else {
                comment.put("writer_nickname", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName());
            }

            db.collection("게시판").document(collection_key).collection("board_collection").document(document_key).collection("comments").add(comment)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            refereshData();
                        }
                    });

            db.collection("사용자").document(writer_id).collection("alert").add(alert_data).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    if(task.isSuccessful()) {
                        FirebaseFirestore.getInstance().collection("사용자").document(writer_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                String token = documentSnapshot.get("token").toString();
                                sendFCM(text, token);
                            }
                        });
                    }
                }
            });
        }

        db.collection("게시판").document(collection_key).collection("board_collection").document(document_key).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String, Object> count = documentSnapshot.getData();
                        int num = Integer.parseInt(count.get("comment_count").toString()) + 1;
                        count.put("comment_count", num);
                        db.collection("게시판").document(collection_key).collection("board_collection").document(document_key).set(count);
                    }
                });
    }

    private void sendFCM(String text, String id) {
        Gson gson = new Gson();
        NotificationModels notificationModels = new NotificationModels();
        notificationModels.data.title = "새로운 댓글이 달렸어요";
        notificationModels.data.text = text;
        notificationModels.data.collection_key = collection_key;
        notificationModels.data.document_key = document_key;
        notificationModels.data.board_name = board_name;
        notificationModels.to = id;

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf8"), gson.toJson(notificationModels));
        Request request = new Request.Builder()
                .header("Content-Type", "application/json")
                .addHeader("Authorization", "key=AIzaSyDgO64woitIDfzghI1S3F-gcmk5Nnthek0")
                .url("https://fcm.googleapis.com/fcm/send")
                .post(requestBody)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });
    }

    private void refereshData() {
        db.collection("게시판").document(collection_key).collection("board_collection").document(document_key).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String, Object> dt = documentSnapshot.getData();
                        if(dt != null) {
                            comment_count.setText(dt.get("comment_count").toString());
                        }
                    }
                });

        final ArrayList<Map<String, Object>> comment_datas = new ArrayList<>();
        db.collection("게시판").document(collection_key).collection("board_collection").document(document_key).collection("comments")
                .orderBy("time").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for(DocumentSnapshot snapshot : task.getResult()) {
                        Map<String, Object> temp = snapshot.getData();
                        temp.put("comment_key", snapshot.getId());
                        comment_datas.add(temp);
                    }
                    RecyclerView.Adapter adapter = new MyCommentsAdapter(comment_datas);
                    comments_recyclerview.setLayoutManager(new LinearLayoutManager(ShowEachBoardActivity.this));
                    comments_recyclerview.setAdapter(adapter);
                    waiting.setVisibility(View.GONE);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ShowEachBoardActivity.this);
                    builder.setTitle("알림")
                            .setMessage("게시글이 삭제되었거나 찾을 수 없습니다.")
                            .setPositiveButton("뒤로 가기", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    onBackPressed();
                                }
                            }).show();
                }
            }
        });
    }

    private void getData() {
        db.collection("게시판").document(collection_key).collection("board_collection").document(document_key).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map<String, Object> data = documentSnapshot.getData();
                if(data == null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ShowEachBoardActivity.this);
                    builder.setTitle("알림")
                            .setMessage("게시글이 삭제되었거나 찾을 수 없습니다.")
                            .setPositiveButton("뒤로 가기", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    onBackPressed();
                                }
                            }).show();
                } else {
                    Date date = new Date(Long.parseLong(data.get("time").toString()));
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("MM/dd hh:mm");
                    String getDate = sdf.format(date);
                    writer_id = data.get("writer").toString();

                    title.setText(data.get("title").toString());
                    writer.setText(data.get("writer_nickname").toString());
                    context.setText(data.get("context").toString());
                    time.setText(getDate);
                    image_count.setText(data.get("image_count").toString());
                    like_count.setText(data.get("like_count").toString());
                    comment_count.setText(data.get("comment_count").toString());
                    Glide.with(ShowEachBoardActivity.this).load(Uri.parse(data.get("profile_uri").toString())).into(profile);

                    if (data.get("thumbnail") != null) {
                        if (data.get("image_count").toString().equals("1")) {
                            image_alone.setVisibility(View.VISIBLE);
                            image_recyclerview.setVisibility(View.GONE);
                            thumbnailUri = Uri.parse(data.get("thumbnail").toString());
                            Glide.with(ShowEachBoardActivity.this).load(thumbnailUri).into(image_alone);
                        } else {
                            image_alone.setVisibility(View.GONE);
                            image_recyclerview.setVisibility(View.VISIBLE);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ShowEachBoardActivity.this);
                            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

                            ArrayList<Uri> images = new ArrayList<>();
                            ArrayList<String> before_image = (ArrayList<String>) data.get("pictureUris");
                            for (int i = 0; i < before_image.size(); i++) {
                                images.add(Uri.parse(before_image.get(i)));
                            }

                            image_recyclerview.setLayoutManager(linearLayoutManager);
                            image_recyclerview.setAdapter(new MyImageAdapter(images));
                        }
                    } else {
                        image_recyclerview.setVisibility(View.GONE);
                        image_alone.setVisibility(View.GONE);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ShowEachBoardActivity.this);
                builder.setTitle("알림")
                .setMessage("게시글이 삭제되었거나 찾을 수 없습니다.")
                .setPositiveButton("뒤로 가기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onBackPressed();
                    }
                }).show();
            }
        });
    }

    private void showImages(ArrayList<Uri> uris) {
        ArrayList<String> send_data = new ArrayList<>();
        for(int i=0; i<uris.size(); i++) {
            send_data.add(uris.get(i).toString());
        }
        Intent intent = new Intent(ShowEachBoardActivity.this, ShowImageActivity.class);
        intent.putExtra("uris", send_data);
        startActivity(intent);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        frameLayout.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fromleft, R.anim.toright);
    }

    private class MyImageAdapter extends RecyclerView.Adapter {
        ArrayList<Uri> datas;
        public MyImageAdapter(ArrayList<Uri> images) {
            datas = images;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_image2, viewGroup, false);
            return new CustomViewHolder_image(view);
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
            Glide.with(viewHolder.itemView.getContext()).load(datas.get(i)).into(((CustomViewHolder_image)viewHolder).imageView);
            ((CustomViewHolder_image)viewHolder).imageView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    showImages(datas);
                    return false;
                }
            });

        }

        @Override
        public int getItemCount() {
            return datas.size();
        }
    }

    private class CustomViewHolder_image extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public CustomViewHolder_image(View view) {
            super(view);
            imageView = view.findViewById(R.id.item_image2_image);
        }
    }

    private class MyCommentsAdapter extends RecyclerView.Adapter {
        ArrayList<Map<String, Object>> datac;

        public MyCommentsAdapter(ArrayList<Map<String, Object>> comment_datas) {
            datac = comment_datas;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_comments, viewGroup, false);
            return new CustomViewHolder_comment(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int i) {
            final Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            Date date = new Date(Long.parseLong(datac.get(i).get("time").toString()));
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("MM/dd hh:mm");
            String getDate = sdf.format(date);
            ((CustomViewHolder_comment)viewHolder).comment_writer.setText(datac.get(i).get("writer_nickname").toString());
            ((CustomViewHolder_comment)viewHolder).comment_time.setText(getDate);
            ((CustomViewHolder_comment)viewHolder).comment_context.setText(datac.get(i).get("comment").toString());
            Glide.with(ShowEachBoardActivity.this).load(Uri.parse(datac.get(i).get("profile_uri").toString()))
                    .into(((CustomViewHolder_comment)viewHolder).comment_profile);

            final ArrayList<Map<String, Object>> subcomments = new ArrayList<>();
            db.collection("게시판").document(collection_key).collection("board_collection").document(document_key).collection("comments")
                    .document(datac.get(i).get("comment_key").toString()).collection("subcomments").orderBy("time").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()) {
                        for(DocumentSnapshot snapshot : task.getResult()) {
                            if (snapshot.exists()) {
                                Map<String, Object> subcmt = snapshot.getData();
                                subcmt.put("comment_key_2", datac.get(i).get("comment_key").toString());
                                subcmt.put("subcomment_key", snapshot.getId());
                                subcomments.add(subcmt);
                            }
                        }
                        ((CustomViewHolder_comment)viewHolder).recyclerView_subcomment.setLayoutManager(new LinearLayoutManager(ShowEachBoardActivity.this));
                        ((CustomViewHolder_comment)viewHolder).recyclerView_subcomment.setAdapter(new MySubCommentAdapter(subcomments));
                    }
                }
            });

            ((CustomViewHolder_comment)viewHolder).comment_relativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    vibrator.vibrate(50);
                    String item[] = {"대댓글 작성", "삭제"};
                    String item2[] = {"대댓글 작성"};

                    if(datac.get(i).get("comment_writer_id").toString().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ShowEachBoardActivity.this)
                                .setItems(item, new DialogInterface.OnClickListener() {
                                    @RequiresApi(api = Build.VERSION_CODES.O)
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (which == 0) {
                                            target_comment_Key = datac.get(i).get("comment_key").toString();
                                            viewHolder.itemView.setBackgroundColor(0xFFFF8484);
                                            comment_edittext.requestFocus();
                                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                            assert imm != null;
                                            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                                            setSubComment = 1;
                                        } else if (which == 1) {
                                            db.collection("게시판").document(collection_key).collection("board_collection").document(document_key).collection("comments")
                                                    .document(datac.get(i).get("comment_key").toString()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    db.collection("게시판").document(collection_key).collection("board_collection")
                                                            .document(document_key).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                            Map<String, Object> reviseData = documentSnapshot.getData();
                                                            reviseData.put("comment_count", Integer.parseInt(reviseData.get("comment_count").toString()) - 1);
                                                            db.collection("게시판").document(collection_key).collection("board_collection")
                                                                    .document(document_key).set(reviseData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    refereshData();
                                                                }
                                                            });
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    }
                                });
                        builder.show();
                    } else {
                        AlertDialog.Builder builder2 = new AlertDialog.Builder(ShowEachBoardActivity.this)
                                .setItems(item2, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(which == 0) {
                                            target_comment_Key = datac.get(i).get("comment_key").toString();
                                            viewHolder.itemView.setBackgroundColor(0xFFFF8484);
                                            comment_edittext.requestFocus();
                                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                            assert imm != null;
                                            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                                            setSubComment = 1;
                                        }
                                    }
                                });
                        builder2.show();
                    }
                    return true;
                }
            });
        }

        @Override
        public int getItemCount() {
            return datac.size();
        }
    }

    private class CustomViewHolder_comment extends RecyclerView.ViewHolder {
        public TextView comment_writer, comment_context, comment_time;
        public ImageView comment_profile;
        public RecyclerView recyclerView_subcomment;
        public RelativeLayout comment_relativeLayout;

        public CustomViewHolder_comment(View view) {
            super(view);
            comment_writer = view.findViewById(R.id.commentitem_writer);
            comment_context = view.findViewById(R.id.commentitem_context);
            comment_time = view.findViewById(R.id.commentitem_time);
            comment_profile = view.findViewById(R.id.commentitem_profile);
            recyclerView_subcomment = view.findViewById(R.id.commentitem_recyclerview);
            comment_relativeLayout = view.findViewById(R.id.commentitem_line2);
        }
    }

    private class MySubCommentAdapter extends RecyclerView.Adapter {
        ArrayList<Map<String, Object>> sub_data;
        public MySubCommentAdapter(ArrayList<Map<String, Object>> subcomments) {
            sub_data = subcomments;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_subcomments, viewGroup, false);
            return new CustomViewHolder_subcomment(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
            final Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            Date date = new Date(Long.parseLong(sub_data.get(i).get("time").toString()));
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("MM/dd hh:mm");
            String getDate = sdf.format(date);
            ((CustomViewHolder_subcomment)viewHolder).subcomment_writer.setText(sub_data.get(i).get("writer_nickname").toString());
            ((CustomViewHolder_subcomment)viewHolder).subcomment_time.setText(getDate);
            ((CustomViewHolder_subcomment)viewHolder).subcomment_context.setText(sub_data.get(i).get("comment").toString());
            Glide.with(ShowEachBoardActivity.this).load(Uri.parse(sub_data.get(i).get("profile_uri").toString()))
                    .into(((CustomViewHolder_subcomment)viewHolder).subcomment_profile);

            viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(sub_data.get(i).get("writer_id").toString().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())) {
                        vibrator.vibrate(50);
                        String item[] = {"삭제"};
                        AlertDialog.Builder maker = new AlertDialog.Builder(ShowEachBoardActivity.this);
                        maker.setItems(item, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    db.collection("게시판").document(collection_key).collection("board_collection").document(document_key)
                                            .collection("comments").document(sub_data.get(i).get("comment_key_2").toString()).collection("subcomments")
                                            .document(sub_data.get(i).get("subcomment_key").toString()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            db.collection("게시판").document(collection_key).collection("board_collection")
                                                    .document(document_key).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                    Map<String, Object> reviseData = documentSnapshot.getData();
                                                    reviseData.put("comment_count", Integer.parseInt(reviseData.get("comment_count").toString()) - 1);
                                                    db.collection("게시판").document(collection_key).collection("board_collection")
                                                            .document(document_key).set(reviseData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            refereshData();
                                                        }
                                                    });
                                                }
                                            });
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
            return sub_data.size();
        }
    }

    private class CustomViewHolder_subcomment extends RecyclerView.ViewHolder {

        public TextView subcomment_writer, subcomment_context, subcomment_time;
        public ImageView subcomment_profile;

        public CustomViewHolder_subcomment(View view) {
            super(view);
            subcomment_writer = view.findViewById(R.id.subcommentitem_writer);
            subcomment_context = view.findViewById(R.id.subcommentitem_context);
            subcomment_time = view.findViewById(R.id.subcommentitem_time);
            subcomment_profile = view.findViewById(R.id.subcommentitem_profile);
        }
    }
}
