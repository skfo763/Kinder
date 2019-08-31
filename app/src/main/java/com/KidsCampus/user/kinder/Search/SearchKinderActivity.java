package com.KidsCampus.user.kinder.Search;

import android.annotation.SuppressLint;
import android.content.Context;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.KidsCampus.user.kinder.KInderActivity;
import com.KidsCampus.user.kinder.Models.BriefKinderModels;
import com.KidsCampus.user.kinder.R;
import com.KidsCampus.user.kinder.ShowKinderActivity;
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
import java.util.Map;

public class SearchKinderActivity extends AppCompatActivity {

    InputMethodManager imm;
    public ImageView search;
    public RecyclerView recyclerView;
    public EditText searchtext;
    public FirebaseFirestore db = FirebaseFirestore.getInstance();
    public LinearLayout init, nodata, waiting;
    public ArrayList<Map<String, Object>> search_data = new ArrayList<>();
    boolean checkfirst = true;
    String text = "";
    final public String key = "086934842ed848beb75ef49b7e08ed5c";
    final public String[] sidos = {"인천광역시", "제주특별자치도", "강원도", "충청남도", "충청북도", "세종특별자치시",
            "대전광역시", "전라남도", "전라북도", "광주광역시", "부산광역시", "울산광역시", "경상남도", "대구광역시",
            "경상북도", "서울특별시", "경기도"};
    final public String[] codes = {"28", "50", "42", "44", "43", "36", "30", "46", "45", "29", "26", "31",
            "48", "27", "47", "11", "41"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_kinder);
        search = findViewById(R.id.searchkinder_search);
        recyclerView = findViewById(R.id.searchkinder_recyclerview);
        searchtext = findViewById(R.id.searchkinder_edittext);
        init = findViewById(R.id.searchkinder_init);
        nodata = findViewById(R.id.searchkinder_nodata);
        waiting = findViewById(R.id.searchkinder_waiting);
        recyclerView.setVisibility(View.GONE);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setLayoutManager(null);
                recyclerView.setAdapter(null);
                imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchtext.getWindowToken(), 0);
                search_data.clear();
                if(searchtext.getText().toString().isEmpty() || searchtext.getText().toString().length() < 2) {
                    Toast.makeText(SearchKinderActivity.this, "검색어는 두 글자 이상 입력해주세요", Toast.LENGTH_SHORT).show();
                } else {
                    search_data.clear();
                    text = searchtext.getText().toString();
                    init.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    nodata.setVisibility(View.GONE);
                    waiting.setVisibility(View.VISIBLE);
                    searchtext.setFocusable(false);

                    getInfo();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(checkfirst && search_data.isEmpty()) {
                                Toast.makeText(SearchKinderActivity.this, "알수 없는 오류로 검색이 중지되었습니다. 한번 더 시도해주세요.", Toast.LENGTH_SHORT).show();
                                checkfirst = false;
                            }
                            System.out.println(search_data.size());
                            recyclerView.setLayoutManager(new LinearLayoutManager(SearchKinderActivity.this));
                            recyclerView.setAdapter(new MyAdapter(search_data));
                            recyclerView.setVisibility(View.VISIBLE);
                            searchtext.setFocusableInTouchMode(true);
                            waiting.setVisibility(View.GONE);
                        }
                    }, 5000);
                }
            }
        });
    }

    private void getInfo() {
        for(int i=0; i<sidos.length; i++) {
            final String t = sidos[i];
            db.collection(t).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()) {
                        for(final DocumentSnapshot snapshot : task.getResult()) {
                            db.collection(t).document(snapshot.getId()).collection("유치원목록")
                                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful()) {
                                        for(DocumentSnapshot documentSnapshot : task.getResult()) {
                                            String name = documentSnapshot.get("kindername").toString();
                                            if(name.equals(searchtext.getText().toString()) || name.contains(searchtext.getText().toString())) {
                                                search_data.add(documentSnapshot.getData());
                                            }
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
            });
        }
    }

    public class MyAdapter extends RecyclerView.Adapter {
        ArrayList<Map<String, Object>> data;
        public MyAdapter(ArrayList<Map<String, Object>> search_data) {
            this.data = search_data;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_kinder, viewGroup, false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
            ((CustomViewHolder)viewHolder).kindername.setText(data.get(i).get("kindername").toString());
            ((CustomViewHolder)viewHolder).establish.setText(data.get(i).get("establish").toString());
            ((CustomViewHolder)viewHolder).addr.setText(data.get(i).get("addr").toString());
            ((CustomViewHolder)viewHolder).call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String tell = data.get(i).get("telno").toString();
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + tell));
                    startActivity(intent);
                }
            });

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SearchKinderActivity.this, ShowKinderActivity.class);
                    intent.putExtra("kindername", data.get(i).get("kindername").toString());
                    intent.putExtra("sidoCode", data.get(i).get("sidoCode").toString());
                    intent.putExtra("sggCode", data.get(i).get("sggCode").toString());
                    intent.putExtra("position", i);
                    intent.putExtra("sidolocation", data.get(i).get("sidoName").toString());
                    intent.putExtra("sgglocation", data.get(i).get("sggName").toString());
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        public TextView kindername;
        public TextView establish;
        public TextView addr;
        public ImageView call;
        
        public CustomViewHolder(View view) {
            super(view);
            kindername = view.findViewById(R.id.kinder_title);
            establish = view.findViewById(R.id.kinder_establish);
            addr = view.findViewById(R.id.kinder_addr);
            call = view.findViewById(R.id.kinder_call);
        }
    }
}
