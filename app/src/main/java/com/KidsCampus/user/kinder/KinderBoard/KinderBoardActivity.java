package com.KidsCampus.user.kinder.KinderBoard;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.KidsCampus.user.kinder.Models.KinderBoardModels;
import com.KidsCampus.user.kinder.R;
import com.KidsCampus.user.kinder.ShowKinderActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class KinderBoardActivity extends AppCompatActivity {

    private static final String TAG = "tag";
    String kindername, officeedu, subofficeedu, sggL, sidoL, est;
    TextView title, establish, officeedu1, point, circumstance, teacher, paytext, payratetext, safetytext;
    ImageView star1, star2, star3, star4, star5;
    Button makereview, makereview2;
    LinearLayout layout1;
    RelativeLayout layout2;
    RecyclerView recyclerView;
    FirebaseFirestore db;
    public AdView adview;
    private ArrayList<KinderBoardModels.kinderboards> kinderboards = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kinder_board);

        kindername = getIntent().getStringExtra("kindername");
        officeedu = getIntent().getStringExtra("officeedu");
        subofficeedu = getIntent().getStringExtra("subofficeedu");
        est = getIntent().getStringExtra("establish");
        sidoL = getIntent().getStringExtra("sidoL");
        sggL = getIntent().getStringExtra("sggL");
        db = FirebaseFirestore.getInstance();
        adview = findViewById(R.id.kinderboard_adview);
        AdRequest adRequest = new AdRequest.Builder().build();
        adview.loadAd(adRequest);

        title = findViewById(R.id.kinderboard_name);
        officeedu1 = findViewById(R.id.kinderboard_name_2text);
        establish = findViewById(R.id.kinderboard_establishtext);
        makereview = findViewById(R.id.kinderboard_makereview);
        makereview2 = findViewById(R.id.kinderboard_makereview_2);
        recyclerView = findViewById(R.id.kinderboard_recyclerview);
        layout1 = findViewById(R.id.kinderboard_linearlayout);
        layout2 = findViewById(R.id.kinderboard_relativelayout);

        star1 = findViewById(R.id.kinderboard_star1);
        star2 = findViewById(R.id.kinderboard_star2);
        star3 = findViewById(R.id.kinderboard_star3);
        star4 = findViewById(R.id.kinderboard_star4);
        star5 = findViewById(R.id.kinderboard_star5);
        point = findViewById(R.id.kinderboard_number);

        circumstance = findViewById(R.id.kinderboard_circumstancetext);
        teacher = findViewById(R.id.kinderboard_teachertext);
        paytext = findViewById(R.id.kinderboard_paytext);
        payratetext = findViewById(R.id.kinderboard_payratetext);
        safetytext = findViewById(R.id.kinderboard_safetytext);

        getData();
        setData();

        makereview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(KinderBoardActivity.this, WriteKinderBoardActivity.class);
                intent.putExtra("kindername", kindername);
                intent.putExtra("sidoL", sidoL);
                intent.putExtra("sggL", sggL);
                intent.putExtra("officeedu", officeedu);
                intent.putExtra("subofficeedu", subofficeedu);
                intent.putExtra("establish", est);
                startActivity(intent);
            }
        });

        makereview2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(KinderBoardActivity.this, WriteKinderBoardActivity.class);
                intent.putExtra("kindername", kindername);
                intent.putExtra("sidoL", sidoL);
                intent.putExtra("sggL", sggL);
                intent.putExtra("officeedu", officeedu);
                intent.putExtra("subofficeedu", subofficeedu);
                intent.putExtra("establish", est);
                startActivity(intent);
            }
        });
    }

    private void setData() {
        title.setText(kindername);
        officeedu1.setText(officeedu.concat(" ").concat(subofficeedu));
        establish.setText(est);
    }

    private void getData() {
        db.collection("유치원평가").document(sidoL).collection(sggL).document(kindername).collection("평점").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot document : task.getResult()) {
                            KinderBoardModels.kinderboards temp = new KinderBoardModels.kinderboards();
                            temp.title = document.getString("title");
                            temp.context = document.getString("context");
                            temp.date = document.getString("date");
                            temp.star = document.getDouble("star");
                            temp.cir = document.getString("cir");
                            temp.tec = document.getString("tec");
                            temp.pay = document.getString("pay");
                            temp.payrate = document.getString("payrate");
                            temp.safety = document.getString("safety");
                            kinderboards.add(temp);
                        }
                        getbroad_data(kinderboards);
                        recyclerView.setLayoutManager(new LinearLayoutManager(KinderBoardActivity.this));
                        recyclerView.setAdapter(new MyRecyclerViewAdapter(kinderboards));
                    }
                });
    }

    private void getbroad_data(ArrayList<KinderBoardModels.kinderboards> kinderboards) {
        db.collection("유치원평가").document(sidoL).collection(sggL).document(kindername)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()) {
                    Map<String, Object> temp = new HashMap<>();
                    temp.put("clr", documentSnapshot.get("clr"));
                    temp.put("tec", documentSnapshot.get("tec"));
                    temp.put("pay", documentSnapshot.get("pay"));
                    temp.put("payrate", documentSnapshot.get("payrate"));
                    temp.put("safety", documentSnapshot.get("safety"));
                    temp.put("collection_star", documentSnapshot.get("collection_star"));
                    setbroad_data(temp);
                }
            }
        });
    }

    private void setbroad_data(Map<String, Object> temp) {
        ArrayList<Object> clr = (ArrayList<Object>) temp.get("clr");
        ArrayList<Object> tec = (ArrayList<Object>) temp.get("tec");
        ArrayList<Object> pay = (ArrayList<Object>) temp.get("pay");
        ArrayList<Object> payrate = (ArrayList<Object>) temp.get("payrate");
        ArrayList<Object> safety = (ArrayList<Object>) temp.get("safety");
        double cstar = Double.parseDouble(temp.get("collection_star").toString());

        circumstance.setText(getCir(getLargest(clr)));
        teacher.setText(getTec(getLargest(tec)));
        paytext.setText(getPay(getLargest(pay)));
        payratetext.setText(getPayRate(getLargest(payrate)));
        safetytext.setText(getSafety(getLargest(safety)));
        setStar(cstar);
    }

    private void setStar(double cstar) {
        @SuppressLint("DefaultLocale") String points = String.format("%.2f", cstar);
        point.setText(points);

        if(cstar >= 0 && cstar < 1) {
            star2.setImageResource(R.drawable.ic_star_border_black_24dp);
            star3.setImageResource(R.drawable.ic_star_border_black_24dp);
            star4.setImageResource(R.drawable.ic_star_border_black_24dp);
            star5.setImageResource(R.drawable.ic_star_border_black_24dp);

            if(cstar - 0.5 >= 0) {
                star1.setImageResource(R.drawable.ic_star_half_black_24dp);
            } else {
                star1.setImageResource(R.drawable.ic_star_border_black_24dp);
            }
        } else if(cstar >= 1 && cstar < 2) {
            star1.setImageResource(R.drawable.ic_star_black_24dp);
            star3.setImageResource(R.drawable.ic_star_border_black_24dp);
            star4.setImageResource(R.drawable.ic_star_border_black_24dp);
            star5.setImageResource(R.drawable.ic_star_border_black_24dp);

            if (cstar - 1.5 >= 0) {
                star2.setImageResource(R.drawable.ic_star_half_black_24dp);
            } else {
                star2.setImageResource(R.drawable.ic_star_border_black_24dp);
            }
        } else if(cstar >= 2 && cstar < 3) {
            star1.setImageResource(R.drawable.ic_star_black_24dp);
            star2.setImageResource(R.drawable.ic_star_black_24dp);
            star4.setImageResource(R.drawable.ic_star_border_black_24dp);
            star5.setImageResource(R.drawable.ic_star_border_black_24dp);

            if (cstar - 2.5 >= 0) {
                star3.setImageResource(R.drawable.ic_star_half_black_24dp);
            } else {
                star3.setImageResource(R.drawable.ic_star_border_black_24dp);
            }
        } else if(cstar >= 3 && cstar < 4) {
            star1.setImageResource(R.drawable.ic_star_black_24dp);
            star2.setImageResource(R.drawable.ic_star_black_24dp);
            star3.setImageResource(R.drawable.ic_star_black_24dp);
            star5.setImageResource(R.drawable.ic_star_border_black_24dp);

            if (cstar - 3.5 >= 0) {
                star4.setImageResource(R.drawable.ic_star_half_black_24dp);
            } else {
                star4.setImageResource(R.drawable.ic_star_border_black_24dp);
            }
        } else if(cstar >= 4 && cstar < 5) {
            star1.setImageResource(R.drawable.ic_star_black_24dp);
            star2.setImageResource(R.drawable.ic_star_black_24dp);
            star3.setImageResource(R.drawable.ic_star_black_24dp);
            star4.setImageResource(R.drawable.ic_star_black_24dp);
            if (cstar - 4.5 >= 0) {
                star5.setImageResource(R.drawable.ic_star_half_black_24dp);
            } else {
                star5.setImageResource(R.drawable.ic_star_border_black_24dp);
            }
        }
    }

    private int getLargest(ArrayList<Object> data) {
        Object[] data2 = data.toArray();
        int largest = 0;
        for(int i=1; i<data2.length; i++) {
            if(Integer.parseInt(data2[i].toString()) > Integer.parseInt(data2[largest].toString())) largest = i;
        }
        return largest;
    }

    private String getCir(int number) {
        switch (number) {
            case 0 :
                return "매우 깨끗함";
            case 1 :
                return "적당";
            case 2 :
                return "불청결";
            default:
                break;
        }
        return null;
    }

    private String getTec(int number) {
        switch (number) {
            case 0 :
                return "매우 친절";
            case 1 :
                return "보통";
            case 2 :
                return "불친절";
            default :
                break;
        }
        return null;
    }

    private String getPay(int number) {
        switch (number) {
            case 0 :
                return "높은 편";
            case 1 :
                return "적당";
            case 2 :
                return "저렴한 편";
            default :
                break;
        }
        return null;
    }

    private String getPayRate(int number) {
        switch (number) {
            case 0 :
                return "가성비 좋음";
            case 1 :
                return "적당";
            case 2 :
                return "가성비 안좋음";
            default :
                break;
        }
        return null;
    }

    private String getSafety(int number) {
        switch (number) {
            case 0 :
                return "철저한 관리";
            case 1 :
                return "보통";
            case 2 :
                return "관리 미흡";
            default :
                break;
        }
        return null;
    }

    private class MyRecyclerViewAdapter extends RecyclerView.Adapter {

        ArrayList<KinderBoardModels.kinderboards> kinderboards;

        public MyRecyclerViewAdapter(ArrayList<KinderBoardModels.kinderboards> kboards) {
            kinderboards = kboards;
            if(kinderboards.size() == 0) {
                layout2.setVisibility(View.GONE);
                layout1.setVisibility(View.VISIBLE);
            } else {
                layout2.setVisibility(View.VISIBLE);
                layout1.setVisibility(View.GONE);
            }
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_kinderboard, parent, false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
            ((CustomViewHolder)viewHolder).date.setText(kinderboards.get(i).date);
            ((CustomViewHolder)viewHolder).context.setText(kinderboards.get(i).context);

            double stars = kinderboards.get(i).star;
            if(stars >= 0 && stars < 1) {
                ((CustomViewHolder)viewHolder).rstar2.setImageResource(R.drawable.ic_star_border_black_24dp);
                ((CustomViewHolder)viewHolder).rstar3.setImageResource(R.drawable.ic_star_border_black_24dp);
                ((CustomViewHolder)viewHolder).rstar4.setImageResource(R.drawable.ic_star_border_black_24dp);
                ((CustomViewHolder)viewHolder).rstar5.setImageResource(R.drawable.ic_star_border_black_24dp);

                if(stars == 0) {
                    ((CustomViewHolder)viewHolder).rstar1.setImageResource(R.drawable.ic_star_border_black_24dp);
                } else {
                    ((CustomViewHolder)viewHolder).rstar1.setImageResource(R.drawable.ic_star_half_black_24dp);
                }
            } else if(stars >= 1 && stars < 2) {
                ((CustomViewHolder) viewHolder).rstar1.setImageResource(R.drawable.ic_star_black_24dp);
                ((CustomViewHolder) viewHolder).rstar3.setImageResource(R.drawable.ic_star_border_black_24dp);
                ((CustomViewHolder) viewHolder).rstar4.setImageResource(R.drawable.ic_star_border_black_24dp);
                ((CustomViewHolder) viewHolder).rstar5.setImageResource(R.drawable.ic_star_border_black_24dp);

                if (stars - 1 == 0) {
                    ((CustomViewHolder) viewHolder).rstar2.setImageResource(R.drawable.ic_star_border_black_24dp);
                } else {
                    ((CustomViewHolder) viewHolder).rstar2.setImageResource(R.drawable.ic_star_half_black_24dp);
                }
            } else if(stars >= 2 && stars < 3) {
                ((CustomViewHolder) viewHolder).rstar1.setImageResource(R.drawable.ic_star_black_24dp);
                ((CustomViewHolder) viewHolder).rstar2.setImageResource(R.drawable.ic_star_black_24dp);
                ((CustomViewHolder) viewHolder).rstar4.setImageResource(R.drawable.ic_star_border_black_24dp);
                ((CustomViewHolder) viewHolder).rstar5.setImageResource(R.drawable.ic_star_border_black_24dp);

                if (stars - 2 == 0) {
                    ((CustomViewHolder) viewHolder).rstar3.setImageResource(R.drawable.ic_star_border_black_24dp);
                } else {
                    ((CustomViewHolder) viewHolder).rstar3.setImageResource(R.drawable.ic_star_half_black_24dp);
                }
            } else if(stars >= 3 && stars < 4) {
                ((CustomViewHolder) viewHolder).rstar1.setImageResource(R.drawable.ic_star_black_24dp);
                ((CustomViewHolder) viewHolder).rstar2.setImageResource(R.drawable.ic_star_black_24dp);
                ((CustomViewHolder) viewHolder).rstar3.setImageResource(R.drawable.ic_star_black_24dp);
                ((CustomViewHolder) viewHolder).rstar5.setImageResource(R.drawable.ic_star_border_black_24dp);

                if (stars - 3 == 0) {
                    ((CustomViewHolder) viewHolder).rstar4.setImageResource(R.drawable.ic_star_border_black_24dp);
                } else {
                    ((CustomViewHolder) viewHolder).rstar4.setImageResource(R.drawable.ic_star_half_black_24dp);
                }
            } else if(stars >= 4 && stars < 5) {
                ((CustomViewHolder) viewHolder).rstar1.setImageResource(R.drawable.ic_star_black_24dp);
                ((CustomViewHolder) viewHolder).rstar2.setImageResource(R.drawable.ic_star_black_24dp);
                ((CustomViewHolder) viewHolder).rstar3.setImageResource(R.drawable.ic_star_black_24dp);
                ((CustomViewHolder) viewHolder).rstar4.setImageResource(R.drawable.ic_star_black_24dp);

                if (stars - 4 == 0) {
                    ((CustomViewHolder) viewHolder).rstar5.setImageResource(R.drawable.ic_star_border_black_24dp);
                } else {
                    ((CustomViewHolder) viewHolder).rstar5.setImageResource(R.drawable.ic_star_half_black_24dp);
                }
            } else if (stars == 5) {
                ((CustomViewHolder) viewHolder).rstar1.setImageResource(R.drawable.ic_star_black_24dp);
                ((CustomViewHolder) viewHolder).rstar2.setImageResource(R.drawable.ic_star_black_24dp);
                ((CustomViewHolder) viewHolder).rstar3.setImageResource(R.drawable.ic_star_black_24dp);
                ((CustomViewHolder) viewHolder).rstar4.setImageResource(R.drawable.ic_star_black_24dp);
                ((CustomViewHolder) viewHolder).rstar5.setImageResource(R.drawable.ic_star_black_24dp);
            }
        }

        @Override
        public int getItemCount() {
            return kinderboards.size();
        }
    }

    private class CustomViewHolder extends RecyclerView.ViewHolder {

        private ImageView rstar1, rstar2, rstar3, rstar4, rstar5;
        private TextView date, context;

        public CustomViewHolder(View view) {
            super(view);
            rstar1 = view.findViewById(R.id.kinderboard_item_star1);
            rstar2 = view.findViewById(R.id.kinderboard_item_star2);
            rstar3 = view.findViewById(R.id.kinderboard_item_star3);
            rstar4 = view.findViewById(R.id.kinderboard_item_star4);
            rstar5 = view.findViewById(R.id.kinderboard_item_star5);
            date = view.findViewById(R.id.kinderboard_item_date);
            context = view.findViewById(R.id.contestx);


        }
    }

    @Override
    public void onBackPressed() {
        ((ShowKinderActivity)ShowKinderActivity.mContext).waiting.setVisibility(View.GONE);
        super.onBackPressed();
    }
}
