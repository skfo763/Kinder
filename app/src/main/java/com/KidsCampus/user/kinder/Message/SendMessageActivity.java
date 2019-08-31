package com.KidsCampus.user.kinder.Message;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.KidsCampus.user.kinder.Models.NotificationModels;
import com.KidsCampus.user.kinder.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SendMessageActivity extends AppCompatActivity {

    private String dest_id;
    private EditText context;
    private Button button;
    private ImageView goback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);
        dest_id = getIntent().getStringExtra("destination");

        context = findViewById(R.id.sendmessage_edittext);
        button = findViewById(R.id.sendmessage_check);
        goback = findViewById(R.id.sendmessage_goback);

        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = context.getText().toString();
                sendMessage(text);
            }
        });


    }

    private void sendMessage(final String text) {
        Map<String, Object> send_data = new HashMap<>();
        send_data.put("writer_id", FirebaseAuth.getInstance().getCurrentUser().getUid());
        send_data.put("writer", FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        send_data.put("time", System.currentTimeMillis());
        send_data.put("context", text);

        FirebaseFirestore.getInstance().collection("사용자").document(dest_id).collection("saved_message")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("comments")
                .add(send_data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(final DocumentReference documentReference) {
                final String[] token = new String[1];
                FirebaseFirestore.getInstance().collection("사용자").document(dest_id).get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        token[0] = documentSnapshot.get("token").toString();
                        sendFCM(text, token[0]);
                    }
                });

                Map<String, Object> send_data_2 = new HashMap<>();
                send_data_2.put("writer_id", FirebaseAuth.getInstance().getCurrentUser().getUid());
                send_data_2.put("writer", FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                send_data_2.put("time", System.currentTimeMillis());
                send_data_2.put("check","0");
                send_data_2.put("context", text);
                FirebaseFirestore.getInstance().collection("사용자").document(dest_id).collection("saved_message")
                        .document(FirebaseAuth.getInstance().getCurrentUser().getUid()).set(send_data_2)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        context.setText("");
                        onBackPressed();
                    }
                });
            }
        });

    }

    private void sendFCM(String text, String id) {
        Gson gson = new Gson();
        NotificationModels notificationModels = new NotificationModels();
        notificationModels.data.title = "새로운 쪽지가 도착했어요";
        notificationModels.data.text = text;
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
}
