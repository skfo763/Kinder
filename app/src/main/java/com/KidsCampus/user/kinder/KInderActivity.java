package com.KidsCampus.user.kinder;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
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
import android.widget.TextView;
import android.widget.Toast;

import com.KidsCampus.user.kinder.Models.BriefKinderModels;
import com.KidsCampus.user.kinder.Search.SearchKinderActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class KInderActivity extends AppCompatActivity {

    public static Context mContext;
    LinearLayout sido, search;
    TextView sidotext;
    LinearLayout sigungu, waiting;
    TextView sigungutext;
    ImageView button;
    RecyclerView recyclerView;
    FirebaseFirestore db;
    TextView counter;
    private String key = "086934842ed848beb75ef49b7e08ed5c";
    private LinearLayout progressBar;
    private String sidoCode;
    private String sggCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kinder);
        waiting = findViewById(R.id.kinder_waiting);
        mContext = this;

        String presido = getIntent().getStringExtra("sido");
        final String[] sidos = {"인천광역시", "서울특별시", "경기도", "충청남도", "충청북도", "세종특별자치시",
                "대전광역시", "전라남도", "전라북도", "광주광역시", "부산광역시", "울산광역시", "경상남도", "대구광역시",
                "경상북도", "강원도", "제주특별자치도"};

        final String[] codes = {"28", "11", "41", "44", "43", "36", "30", "46", "45", "29", "26", "31",
                "48", "27", "47", "42", "50"};

        db = FirebaseFirestore.getInstance();
        sido = findViewById(R.id.kinder_sido);
        sidotext = findViewById(R.id.kinder_sido_text);
        sigungu = findViewById(R.id.kinder_sigungu);
        sigungutext = findViewById(R.id.kinder_sigungu_text);
        button = findViewById(R.id.kinder_button_search);
        recyclerView = findViewById(R.id.kinder_recyclerview);
        counter = findViewById(R.id.kinder_count);
        progressBar = findViewById(R.id.kinder_progressbar);
        search = findViewById(R.id.kinder_search);
        sidotext.setText(presido);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(KInderActivity.this, SearchKinderActivity.class);
                startActivity(intent);
            }
        });

        sido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                waiting.setVisibility(View.VISIBLE);
                AlertDialog.Builder sidobuilder = new AlertDialog.Builder(KInderActivity.this);
                sidobuilder.setTitle("시/도를 선택해주세요");
                sidobuilder.setItems(sidos, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sidotext.setText(sidos[which]);
                        sigungutext.setText("시/군/구 선택");
                    }
                }).show();
                waiting.setVisibility(View.GONE);
            }
        });

        sigungu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                waiting.setVisibility(View.VISIBLE);
                if(sidotext.getText().toString().equals("시/도 선택")) {
                    Toast.makeText(getApplicationContext(), "시/도를 먼저 선택해주세요", Toast.LENGTH_SHORT).show();
                } else {
                    final ArrayList<String> list = new ArrayList<>();
                    db.collection(sidotext.getText().toString()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot document : task.getResult()) {
                                    String name = document.get("name").toString();
                                    list.add(name);
                                }

                                final CharSequence[] sigungu = list.toArray(new CharSequence[list.size()]);
                                AlertDialog.Builder sidobuilder = new AlertDialog.Builder(KInderActivity.this);
                                sidobuilder.setTitle("시/군/구를 선택해주세요");
                                sidobuilder.setItems(sigungu, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        sigungutext.setText(sigungu[which]);
                                    }
                                }).show();
                                waiting.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sidotext.getText().toString().equals("시/도 선택") || sigungutext.getText().toString().equals("시/군/구 선택")) {
                    Toast.makeText(getApplicationContext(), "정보를 입력해주세요", Toast.LENGTH_SHORT).show();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    int i;

                    for(i=0; i<sidos.length; i++) {
                        if(sidos[i].equals(sidotext.getText().toString())){
                            break;
                        }
                    }
                    final int finalI = i;
                    db.collection(sidotext.getText().toString()).document(sigungutext.getText().toString())
                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()) {
                                sidoCode = codes[finalI];
                                sggCode = task.getResult().get("value").toString();

                                final String uri = "http://e-childschoolinfo.moe.go.kr/api/notice/basicInfo.do" + "?" + "key="
                                        + key + "&" + "sidoCode=" + sidoCode + "&" + "sggCode=" + sggCode;

                                new Thread() {
                                    @Override
                                    public void run() {
                                        String data = getData(uri);
                                        Bundle bun = new Bundle();
                                        bun.putString("data", data);
                                        Message msg = handler.obtainMessage();
                                        msg.setData(bun);
                                        handler.sendMessage(msg);
                                    }
                                }.start();
                            }
                        }
                    });
                }
            }
        });
    }

    private String getData(String uri) {
        URL url;
        BufferedReader bf;
        String line="";
        String result="";

        try {
            url = new URL(uri);
            bf = new BufferedReader(new InputStreamReader(url.openStream()));

            while((line=bf.readLine())!=null) {
                result = line.concat(" ");
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bun = msg.getData();
            String json = bun.getString("data");
            ArrayList<BriefKinderModels.kinder> kindername = parsingData(json);
            if(!kindername.isEmpty()) {
                String count = String.valueOf(kindername.size()).concat("개원");
                counter.setText(count);
                progressBar.setVisibility(View.GONE);
                recyclerView.setLayoutManager(new LinearLayoutManager(KInderActivity.this));
                recyclerView.setAdapter(new MyLayoutAdapter(kindername));

                for(int i=0; i<kindername.size(); i++) {
                    Map<String, Object> temp = new HashMap<>();
                    temp.put("sidoCode", kindername.get(i).sidoCode);
                    temp.put("sggCode", kindername.get(i).sggCode);
                    temp.put("sidoName", kindername.get(i).sidoname);
                    temp.put("sggName", kindername.get(i).sggname);
                    temp.put("kindername", kindername.get(i).kindername);
                    temp.put("establish", kindername.get(i).establish);
                    temp.put("telno", kindername.get(i).telno);
                    temp.put("addr", kindername.get(i).addr);
                    FirebaseFirestore.getInstance().collection(sidotext.getText().toString()).document(sigungutext.getText().toString()).collection("유치원목록")
                            .add(temp);
                }
            }
        }
    };

    protected ArrayList<BriefKinderModels.kinder> parsingData(String json) {
        ArrayList<BriefKinderModels.kinder> titles  = new ArrayList<>();

        try {
            JSONObject data = new JSONObject(json);
            switch (data.getString("status")) {
                case "SUCCESS" :
                    JSONArray jsonArray = data.getJSONArray("kinderInfo");
                    for(int i=0; i<jsonArray.length(); i++) {
                        BriefKinderModels.kinder temp = new BriefKinderModels.kinder();
                        JSONObject object = jsonArray.getJSONObject(i);
                        temp.kindername = object.getString("kindername");
                        temp.addr = object.getString("addr");
                        temp.telno = object.getString("telno");
                        temp.establish = object.getString("establish");
                        titles.add(temp);
                    }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return titles;
    }

    private class MyLayoutAdapter extends RecyclerView.Adapter {
        ArrayList<BriefKinderModels.kinder> temp;

        MyLayoutAdapter(ArrayList<BriefKinderModels.kinder> kindername) {
            this.temp = kindername;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_kinder, parent, false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
            ((CustomViewHolder)holder).kindername.setText(temp.get(position).kindername);
            ((CustomViewHolder)holder).establish.setText(temp.get(position).establish);
            ((CustomViewHolder)holder).addr.setText(temp.get(position).addr);
            ((CustomViewHolder)holder).call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String tell = temp.get(position).telno;
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + tell));
                    startActivity(intent);
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(KInderActivity.this, ShowKinderActivity.class);
                    intent.putExtra("kindername", temp.get(position).kindername);
                    intent.putExtra("sidoCode", sidoCode);
                    intent.putExtra("sggCode", sggCode);
                    intent.putExtra("position", position);
                    intent.putExtra("sidolocation", sidotext.getText());
                    intent.putExtra("sgglocation", sigungutext.getText());
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return temp.size();
        }
    }

    private class CustomViewHolder extends RecyclerView.ViewHolder {
        private TextView kindername;
        private TextView establish;
        private TextView addr;
        private ImageView call;

        CustomViewHolder(View view) {
            super(view);
            kindername = view.findViewById(R.id.kinder_title);
            establish = view.findViewById(R.id.kinder_establish);
            addr = view.findViewById(R.id.kinder_addr);
            call = view.findViewById(R.id.kinder_call);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}