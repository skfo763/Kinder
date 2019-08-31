package com.KidsCampus.user.kinder.KinderBoard;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.KidsCampus.user.kinder.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WriteKinderBoardActivity extends AppCompatActivity {

    private String kindername, sidoL, sggL, context, date, officeedu, subofficeedu, est;
    private RadioGroup cirgroup, tecgroup, paygroup, payrategroup, safetygroup, scoregroup;
    private Spinner year, semester;
    private ImageView write;
    private EditText editText;
    private TextView name, office, establish;
    private int star;
    private LinearLayout progress;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_kinder_board);

        kindername = getIntent().getStringExtra("kindername");
        sidoL = getIntent().getStringExtra("sidoL");
        sggL = getIntent().getStringExtra("sggL");
        officeedu = getIntent().getStringExtra("officeedu");
        subofficeedu = getIntent().getStringExtra("subofficeedu");
        est = getIntent().getStringExtra("establish");

        cirgroup = findViewById(R.id.writekinderboard_circumstance_radiogroup);
        tecgroup = findViewById(R.id.writekinderboard_teacher_radiogroup);
        paygroup = findViewById(R.id.writekinderboard_pay_radiogroup);
        payrategroup = findViewById(R.id.writekinderboard_payrate_radiogroup);
        safetygroup = findViewById(R.id.writekinderboard_safety_radiogroup);
        scoregroup = findViewById(R.id.writekinderboard_score_radiogroup);
        year = findViewById(R.id.writeboard_spinner_year);
        semester = findViewById(R.id.writeboard_spinner_semester);
        write = findViewById(R.id.writeboard_write);
        editText = findViewById(R.id.writeboard_context);
        name = findViewById(R.id.writeboard_name);
        office = findViewById(R.id.writeboard_name_2text);
        establish = findViewById(R.id.writeboard_establishtext);
        progress = findViewById(R.id.writeboard_progress);

        name.setText(kindername);
        establish.setText(est);
        office.setText(officeedu.concat(" ").concat(subofficeedu));

        write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.setVisibility(View.VISIBLE);
                context = getMyContext();
                date = getMyDate();
                star = getMyStar();
                writetoServer();
            }
        });

    }

    private int getMyStar() {
        switch (scoregroup.getCheckedRadioButtonId()) {
            case R.id.score_check1 :
                return 1;
            case R.id.score_check2 :
                return 2;
            case R.id.score_check3 :
                return 3;
            case R.id.score_check4 :
                return 4;
            case R.id.score_check5 :
                return 5;
            default:
                break;
        }
        return 0;
    }

    private String getMyDate() {
        if(year.getSelectedItem().toString().isEmpty() || semester.getSelectedItem().toString().isEmpty()) return null;
        return year.getSelectedItem().toString().concat("년 ").concat(semester.getSelectedItem().toString()).concat("학기");
    }

    private String getMyContext() {
        String contextString = editText.getText().toString();
        if(contextString.isEmpty()) contextString = " ";
        return contextString;
    }

    private void writetoServer() {
        Map<String, Object> broad_data = new HashMap<>();
        getbroad_data(broad_data);
        db.collection("유치원평가").document(sidoL).collection(sggL).document(kindername).collection("평점")
                .add(broad_data).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if(task.isSuccessful()) {
                    final Map<String, Object> kinder_data = new HashMap<>();
                    db.collection("유치원평가").document(sidoL).collection(sggL).document(kindername)
                            .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(!documentSnapshot.exists()) getNewData(kinder_data);
                            else {
                                kinder_data.put("clr", documentSnapshot.get("clr"));
                                kinder_data.put("tec", documentSnapshot.get("tec"));
                                kinder_data.put("pay", documentSnapshot.get("pay"));
                                kinder_data.put("payrate", documentSnapshot.get("payrate"));
                                kinder_data.put("safety", documentSnapshot.get("safety"));
                                double nstar = Double.parseDouble(documentSnapshot.get("collection_star").toString());
                                int nsize = Integer.parseInt(documentSnapshot.get("collection_size").toString());
                                addData(kinder_data, nstar, nsize);
                            }
                        }
                    });
                }
            }
        });
    }

    private void move() {
        Intent intent = new Intent(WriteKinderBoardActivity.this, KinderBoardActivity.class);
        intent.putExtra("kindername", kindername);
        intent.putExtra("sidoL", sidoL);
        intent.putExtra("sggL", sggL);
        intent.putExtra("officeedu", officeedu);
        intent.putExtra("subofficeedu", subofficeedu);
        intent.putExtra("establish", est);
        startActivity(intent);
        finish();
    }

    private void getNewData(Map<String, Object> kinder_data) {
        int clr[] = {0, 0, 0};
        int tec[] = {0, 0, 0};
        int pay[] = {0, 0, 0};
        int payrate[] = {0, 0, 0};
        int safety[] = {0, 0, 0};

        clr[getCirNum()] = 1;
        tec[getTecNum()] = 1;
        pay[getPayNum()] = 1;
        payrate[getPayRateNum()] = 1;
        safety[getSafetyNum()] = 1;

        ArrayList<Object> clrlist = getArrayList(clr);
        ArrayList<Object> teclist = getArrayList(tec);
        ArrayList<Object> paylist = getArrayList(pay);
        ArrayList<Object> payratelist = getArrayList(payrate);
        ArrayList<Object> safetylist = getArrayList(safety);

        kinder_data.put("clr", clrlist);
        kinder_data.put("tec", teclist);
        kinder_data.put("pay", paylist);
        kinder_data.put("payrate", payratelist);
        kinder_data.put("safety", safetylist);
        kinder_data.put("collection_star", getMyStar());
        kinder_data.put("collection_size", 1);

        db.collection("유치원평가").document(sidoL).collection(sggL). document(kindername).set(kinder_data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progress.setVisibility(View.GONE);
                move();
            }
        });
    }

    private ArrayList<Object> getArrayList(int[] data) {
        ArrayList<Object> ret = new ArrayList<>();
        for(Object temp : data) {
            ret.add(temp);
        }
        return ret;
    }

    private void addData(Map<String, Object> kinder_data, double star, int size) {
        int clrn, tecn, payn, payraten, safetyn;
        Map<String, Object> newMap = new HashMap<>();

        ArrayList<Object> clr = (ArrayList<Object>) kinder_data.get("clr");
        ArrayList<Object> tec = (ArrayList<Object>) kinder_data.get("tec");
        ArrayList<Object> pay = (ArrayList<Object>) kinder_data.get("pay");
        ArrayList<Object> payrate = (ArrayList<Object>) kinder_data.get("payrate");
        ArrayList<Object> safety = (ArrayList<Object>) kinder_data.get("safety");

        clrn = getCirNum();
        tecn = getTecNum();
        payn = getPayNum();
        payraten = getPayRateNum();
        safetyn = getSafetyNum();

        clr.set(clrn, Integer.parseInt(clr.get(clrn).toString()) + 1);
        tec.set(tecn, Integer.parseInt(clr.get(tecn).toString()) + 1);
        pay.set(payn, Integer.parseInt(clr.get(payn).toString()) + 1);
        payrate.set(payraten, Integer.parseInt(clr.get(payraten).toString()) + 1);
        safety.set(safetyn, Integer.parseInt(clr.get(safetyn).toString()) + 1);

        newMap.put("clr", clr);
        newMap.put("tec", tec);
        newMap.put("pay", pay);
        newMap.put("payrate", payrate);
        newMap.put("safety", safety);
        newMap.put("collection_star", (((size*star)+getMyStar())/(size+1)));
        newMap.put("collection_size", size+1);

        db.collection("유치원평가").document(sidoL).collection(sggL).document(kindername)
                .set(newMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progress.setVisibility(View.GONE);
                move();
            }
        });
    }

    private void getbroad_data(Map<String, Object> board_data) {
        board_data.put("context", context);
        board_data.put("date", date);
        board_data.put("star", star);
        board_data.put("cir", getCir());
        board_data.put("tec", getTec());
        board_data.put("pay", getPay());
        board_data.put("payrate", getPayRate());
        board_data.put("safety", getSafety());
    }

    private int getCirNum() {
        switch (cirgroup.getCheckedRadioButtonId()) {
            case R.id.circumstance_check1 :
                return 0;
            case R.id.circumstance_check2 :
                return 1;
            case R.id.circumstance_check3 :
                return 2;
            default:
                break;
        }
        return -1;
    }

    private int getTecNum() {
        switch (tecgroup.getCheckedRadioButtonId()) {
            case R.id.teacher_check1 :
                return 0;
            case R.id.teacher_check2 :
                return 1;
            case R.id.teacher_check3 :
                return 2;
            default :
                break;
        }
        return -1;
    }

    private int getPayNum() {
        switch (paygroup.getCheckedRadioButtonId()) {
            case R.id.pay_check1 :
                return 0;
            case R.id.pay_check2 :
                return 1;
            case R.id.pay_check3 :
                return 2;
            default :
                break;
        }
        return -1;
    }

    private int getPayRateNum() {
        switch (payrategroup.getCheckedRadioButtonId()) {
            case R.id.payrate_check1 :
                return 0;
            case R.id.payrate_check2 :
                return 1;
            case R.id.payrate_check3 :
                return 2;
            default :
                break;
        }
        return -1;
    }

    private int getSafetyNum() {
        switch (safetygroup.getCheckedRadioButtonId()) {
            case R.id.safety_check1 :
                return 0;
            case R.id.safety_check2 :
                return 1;
            case R.id.safety_check3 :
                return 2;
            default :
                break;
        }
        return -1;
    }

    private String getCir() {
        switch (cirgroup.getCheckedRadioButtonId()) {
            case R.id.circumstance_check1 :
                return "매우 깨끗함";
            case R.id.circumstance_check2 :
                return "적당";
            case R.id.circumstance_check3 :
                return "불청결";
            default:
                break;
        }
        return null;
    }

    private String getTec() {
        switch (tecgroup.getCheckedRadioButtonId()) {
            case R.id.teacher_check1 :
                return "매우 친절";
            case R.id.teacher_check2 :
                return "보통";
            case R.id.teacher_check3 :
                return "불친절";
            default :
                break;
        }
        return null;
    }

    private String getPay() {
        switch (paygroup.getCheckedRadioButtonId()) {
            case R.id.pay_check1 :
                return "높은 편";
            case R.id.pay_check2 :
                return "적당";
            case R.id.pay_check3 :
                return "저렴한 편";
            default :
                break;
        }
        return null;
    }

    private String getPayRate() {
        switch (payrategroup.getCheckedRadioButtonId()) {
            case R.id.payrate_check1 :
                return "가성비 좋음";
            case R.id.payrate_check2 :
                return "적당";
            case R.id.payrate_check3 :
                return "가성비 안좋음";
            default :
                break;
        }
        return null;
    }

    private String getSafety() {
        switch (safetygroup.getCheckedRadioButtonId()) {
            case R.id.safety_check1 :
                return "철저한 관리";
            case R.id.safety_check2 :
                return "보통";
            case R.id.safety_check3 :
                return "관리 미흡";
            default :
                break;
        }
        return null;
    }
}
