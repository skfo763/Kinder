package com.KidsCampus.user.kinder.NormalBoard;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.esafirm.imagepicker.features.ImagePicker;
import com.KidsCampus.user.kinder.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class WriteBoardActivity extends AppCompatActivity {

    private EditText title, context;
    private TextView name, addphoto;
    private ImageView insert_image, goback;
    private Button check;
    private LinearLayout checkbox_linarlayout, waiting;
    private CheckBox checkbox;
    private RecyclerView recyclerView;
    String anonymous, collection_key, nametext, board_name;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    ArrayList<Uri> imageList = new ArrayList<>();
    ArrayList<String> downloadUri = new ArrayList<>();
    int j;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_board);
        anonymous = getIntent().getStringExtra("anonymous");
        collection_key = getIntent().getStringExtra("collection_key");
        nametext = getIntent().getStringExtra("name");
        board_name = getIntent().getStringExtra("board_name");

        title = findViewById(R.id.writecontext_title);
        context = findViewById(R.id.writecontext_context);
        name = findViewById(R.id.writecontext_name);
        insert_image = findViewById(R.id.writecontext_insert_image);
        goback = findViewById(R.id.writecontext_goback);
        check = findViewById(R.id.writecontext_check);
        checkbox_linarlayout = findViewById(R.id.writecontext_checkbox_linearlayout);
        checkbox = findViewById(R.id.writecontext_checkbox);
        addphoto = findViewById(R.id.writecontext_addphoto);
        recyclerView = findViewById(R.id.writecontext_recyclerview);
        waiting = findViewById(R.id.writeboard_waiting);
        if(anonymous.equals("false")) checkbox_linarlayout.setVisibility(View.GONE);

        int writePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int networkPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE);
        int readPerminssion  = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        getPermission(writePermission, networkPermission, readPerminssion);

        addphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_photo();
            }
        });

        insert_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_photo();
            }
        });

        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(title.getText().toString().isEmpty() || context.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "게시글을 작성해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    if(title.getText().toString().length() < 2) {
                        Toast.makeText(getApplicationContext(), "제목은 두 글자 이상 작성해주세요.", Toast.LENGTH_SHORT).show();
                    } else {
                        add_data();
                    }
                }
            }
        });

        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void add_data() {
        waiting.setVisibility(View.VISIBLE);
        long now = System.currentTimeMillis();

        Map<String, Object> data = new HashMap<>();
        data.put("title", title.getText().toString());
        data.put("context", context.getText().toString());
        data.put("time", now);
        data.put("profile_uri", FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString());
        data.put("image_count", 0);
        data.put("like_count", 0);
        data.put("comment_count", 0);
        data.put("scrab_count", 0);
        data.put("report_count", 0);

        if(checkbox.isChecked()) {
            data.put("writer", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
            data.put("writer_nickname", "익명");
        } else {
            data.put("writer", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
            data.put("writer_nickname", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName());
        }

        db.collection("게시판").document(collection_key).collection("board_collection").add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(final DocumentReference documentReference) {
                final String document_key = documentReference.getId();
                if(imageList.isEmpty()) {
                    Map<String, Object> userdata = new HashMap<>();
                    userdata.put("collection_key", collection_key);
                    userdata.put("document_key", document_key);
                    userdata.put("write_time", System.currentTimeMillis());
                    db.collection("사용자").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("user_write")
                            .add(userdata).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            waiting.setVisibility(View.GONE);
                            Intent intent = new Intent(WriteBoardActivity.this, ShowBoardListActivity.class);
                            intent.putExtra("anonymous", anonymous);
                            intent.putExtra("name", nametext);
                            startActivity(intent);
                            finish();
                        }
                    });
                } else {
                    Map<String, Object> userdata = new HashMap<>();
                    userdata.put("title", title.getText().toString());
                    userdata.put("collection_key", collection_key);
                    userdata.put("document_key", documentReference.getId());
                    userdata.put("write_time", System.currentTimeMillis());
                    db.collection("사용자").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("user_write")
                            .add(userdata).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            savePhoto(document_key);
                        }
                    });
                }
            }
        });
    }

    private void savePhoto(final String key) {
        StorageReference storageReference = storage.getReferenceFromUrl("gs://kinder-114b0.appspot.com");
        for(j=0; j<imageList.size(); j++) {
            StorageReference riverref = storageReference.child("게시판").child(collection_key).child(key.concat("/".concat(imageList.get(j).getLastPathSegment())));
            UploadTask uploadTask = riverref.putFile(imageList.get(j));
            final int finalJ = j;
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri tempUriforDown = taskSnapshot.getDownloadUrl();
                    downloadUri.add(tempUriforDown.toString());
                    if (finalJ == (imageList.size() -1)) {
                        addInfo(key);
                    }
                }
            });
        }
    }

    private void addInfo(String key) {
        long now = System.currentTimeMillis();

        Map<String, Object> insertdata = new HashMap<>();
        insertdata.put("title", title.getText().toString());
        insertdata.put("context", context.getText().toString());
        insertdata.put("thumbnail", downloadUri.get(0));
        insertdata.put("pictureUris", downloadUri);
        insertdata.put("profile_uri", FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString());
        insertdata.put("time", now);
        insertdata.put("image_count", downloadUri.size());
        insertdata.put("like_count", 0);
        insertdata.put("comment_count", 0);
        insertdata.put("scrab_count", 0);
        insertdata.put("report_count", 0);

        if(checkbox.isChecked()) {
            insertdata.put("writer", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
            insertdata.put("writer_nickname", "익명");
        } else {
            insertdata.put("writer", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
            insertdata.put("writer_nickname", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName());
        }

        db.collection("게시판").document(collection_key).collection("board_collection").document(key).set(insertdata)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        waiting.setVisibility(View.GONE);
                        Intent intent = new Intent(WriteBoardActivity.this, ShowBoardListActivity.class);
                        intent.putExtra("anonymous", anonymous);
                        intent.putExtra("name", nametext);
                        intent.putExtra("board_name", board_name);
                        startActivity(intent);
                        finish();
                    }
                });
    }

    private void add_photo() {
        ImagePicker.create(this)
                .folderMode(true) // folder mode (false by default)
                .toolbarFolderTitle("Folder") // folder selection title
                .toolbarImageTitle("Tap to select") // image selection title
                .toolbarArrowColor(Color.BLACK) // Toolbar 'up' arrow color
                .includeVideo(false) // Show video on image picker
                .multi()
                .limit(10)
                .showCamera(false) // show camera or not (true by default)
                .imageDirectory("Camera") // directory name for captured image  ("Camera" folder by default)
                .enableLog(false)
                .start();
    }

    private void getPermission(int writePermission, int networkPermission, int readPerminssion) {
        if(writePermission == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }

        if(networkPermission == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, 0);
        }

        if(readPerminssion == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

            List<com.esafirm.imagepicker.model.Image> getedimageList = ImagePicker.getImages(data);
            for(int i=0; i<getedimageList.size(); i++) {
                Uri uri = Uri.parse("file://" + getedimageList.get(i).getPath());
                imageList.add(uri);
            }
            recyclerView.setVisibility(View.VISIBLE);
            addphoto.setVisibility(View.GONE);
            assert imageList != null;
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setAdapter(new MyAdapter(imageList));
        }
    }

    private class MyAdapter extends RecyclerView.Adapter {
        ArrayList<Uri> data;

        public MyAdapter(ArrayList<Uri> imageList) {
            data = imageList;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_image, viewGroup, false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            Glide.with(viewHolder.itemView.getContext()).load(data.get(i)).into(((CustomViewHolder)viewHolder).image);
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    private class CustomViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public CustomViewHolder(View view) {
            super(view);
            image = view.findViewById(R.id.imageitem_image);
        }
    }
}
