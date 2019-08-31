package com.KidsCampus.user.kinder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.KidsCampus.user.kinder.KinderBoard.KinderBoardActivity;
import com.KidsCampus.user.kinder.Location.LocationActivity;
import com.KidsCampus.user.kinder.Models.KinderModels;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.NaverMapOptions;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Align;
import com.naver.maps.map.overlay.Marker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class ShowKinderActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static Context mContext;
    private String sidoCode, sggCode, kindername, mapaddress, mapname, sidoL, sggL;
    private String key = "086934842ed848beb75ef49b7e08ed5c";
    public String downloadUrl, officeedu1, subofficeedu1;
    private ImageView star1, star2, star3, star4, star5;
    private TextView titlename, name, establish, date, opendate, gov, tel, homepage, time, address, point;
    private MapFragment mapFragment;
    public LinearLayout seemore, waiting;
    private RelativeLayout goBoard;
    private double lat, lng;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_kinder);
        waiting = findViewById(R.id.show_kinder_waiting);
        waiting.setVisibility(View.VISIBLE);
        mContext = this;

        sidoCode = getIntent().getStringExtra("sidoCode");
        sggCode = getIntent().getStringExtra("sggCode");
        kindername = getIntent().getStringExtra("kindername");
        sidoL = getIntent().getStringExtra("sidolocation");
        sggL = getIntent().getStringExtra("sgglocation");

        seemore = findViewById(R.id.show_kinder_seemore);
        goBoard = findViewById(R.id.show_kinder_goboard);
        name = findViewById(R.id.show_kinder_name_2text);
        establish = findViewById(R.id.show_kinder_establishtext);
        date = findViewById(R.id.show_kinder_datetext);
        opendate = findViewById(R.id.show_kinder_opendatetext);
        gov = findViewById(R.id.show_kinder_govtext);
        tel = findViewById(R.id.show_kinder_teltext);
        homepage = findViewById(R.id.show_kinder_homepagetext);
        time = findViewById(R.id.show_kinder_timetext);
        address = findViewById(R.id.show_kinder_addresstext);
        titlename = findViewById(R.id.show_kinder_name);
        mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.naver_map_fragment);

        star1 = findViewById(R.id.show_kinder_star1);
        star2 = findViewById(R.id.show_kinder_star2);
        star3 = findViewById(R.id.show_kinder_star3);
        star4 = findViewById(R.id.show_kinder_star4);
        star5 = findViewById(R.id.show_kinder_star5);
        point = findViewById(R.id.show_kinder_number);

        downloadUrl = "http://e-childschoolinfo.moe.go.kr/api/notice/basicInfo.do?" + "key=" + key  + "&" + "sidoCode=" + sidoCode + "&" + "sggCode=" + sggCode;
        getJson(downloadUrl);
        getStars();

        seemore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToWindow();
            }
        });

        goBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                waiting.setVisibility(View.VISIBLE);
                Intent intent = new Intent(ShowKinderActivity.this, KinderBoardActivity.class);
                intent.putExtra("kindername", kindername);
                intent.putExtra("officeedu", officeedu1);
                intent.putExtra("subofficeedu", subofficeedu1);
                intent.putExtra("establish", establish.getText().toString());
                intent.putExtra("sidoL", sidoL);
                intent.putExtra("sggL", sggL);
                startActivity(intent);
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(waiting.getVisibility() == View.VISIBLE) {
                    Toast.makeText(ShowKinderActivity.this, "네트워크 문제로 인해 로딩이 느려지고 있습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        }, 5000);
    }

    private void getStars() {
        db.collection("유치원평가").document(sidoL).collection(sggL).document(kindername).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()) {
                    double cstar = documentSnapshot.getDouble("collection_star");
                    setStar(cstar);
                }
            }
        });
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

    private void moveToWindow() {
        final String[] items = {"일반현황", "건물현황", "교실면적현황", "직위/자격별교직원현황", "수업일수현황", "급식운영현황", "통학차랑현황",
                "근속연수현황", "환경위생관리현황", "안전점검/교육실시현황", "공제회 가입 현황", "보험별 가입 현황"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("열람하실 정보를 선택해주세요.")
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == 0) {
                            Toast.makeText(getApplicationContext(), "다른 항목을 선택해주세요.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Intent intent = new Intent(ShowKinderActivity.this, AnotherKinderActivity.class);
                        intent.putExtra("kindername", kindername);
                        intent.putExtra("select", items[which]);
                        intent.putExtra("sidoCode", sidoCode);
                        intent.putExtra("sggCode", sggCode);
                        startActivity(intent);
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void getJson(final String downloadUrl) {
        new Thread() {
            @Override
            public void run() {
                String data = getData(downloadUrl);
                Bundle bundle = new Bundle();
                bundle.putString("data", data);
                Message msg = handler.obtainMessage();
                msg.setData(bundle);
                handler.sendMessage(msg);
                super.run();
            }
        }.start();
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
            KinderModels.showkinder showkinders = parsingData(json);

            if(showkinders != null) {
                mapaddress = showkinders.addr;
                mapname = showkinders.kindername;
                setTexts(showkinders);
                setMaps();
            }

        }
    };

    private StringBuffer makedate(String date) {
        StringBuffer sb = new StringBuffer(date);
        sb.insert(4, "-");
        sb.insert(7, "-");
        return sb;
    }

    private void setTexts(KinderModels.showkinder showkinders) {
        if(showkinders.hpaddr.equals("null")) showkinders.hpaddr = "등록된 홈페이지가 없습니다";
        if(showkinders.telno.equals("null")) showkinders.telno = "등록된 전화번호가 없습니다";

        titlename.setText(showkinders.kindername);
        name.setText(showkinders.kindername);
        establish.setText(showkinders.establish);
        date.setText(makedate(showkinders.edate));
        opendate.setText(makedate(showkinders.odate));
        gov.setText(showkinders.officeedu.concat(" / ").concat(showkinders.subofficeedu));
        tel.setText(showkinders.telno);
        homepage.setText(showkinders.hpaddr);
        time.setText(showkinders.opertime);
        address.setText(showkinders.addr);
    }

    private KinderModels.showkinder parsingData(String json) {
        try {
            JSONObject data = new JSONObject(json);
            switch (data.getString("status")) {
                case "SUCCESS":
                    JSONArray jsonArray = data.getJSONArray("kinderInfo");

                    for(int i=0; i<jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        String check = object.getString("kindername");
                        if(check.equals(kindername)) {
                            KinderModels.showkinder temp = new KinderModels.showkinder();
                            temp.officeedu = object.getString("officeedu");
                            temp.subofficeedu = object.getString("subofficeedu");
                            temp.kindername = object.getString("kindername");
                            temp.establish = object.getString("establish");
                            temp.edate = object.getString("edate");
                            temp.odate = object.getString("edate");
                            temp.addr = object.getString("addr");
                            temp.telno = object.getString("telno");
                            temp.hpaddr = object.getString("hpaddr");
                            temp.opertime = object.getString("opertime");
                            temp.clcnt3 = object.getString("clcnt3");
                            temp.clcnt4 = object.getString("clcnt4");
                            temp.clcnt5 = object.getString("clcnt5");
                            temp.mixclcnt = object.getString("mixclcnt");
                            temp.shclcnt = object.getString("shclcnt");
                            temp.ppcnt3 = object.getString("ppcnt3");
                            temp.ppcnt4= object.getString("ppcnt4");
                            temp.ppcnt5 = object.getString("ppcnt5");
                            temp.mixppcnt = object.getString("mixppcnt");
                            temp.shppcnt = object.getString("shppcnt");

                            officeedu1 = object.getString("officeedu");
                            subofficeedu1 = object.getString("subofficeedu");
                            return temp;
                        }
                    }
                    break;

                default:
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void setMaps() {
        if(mapFragment != null) {
            List<Address> list = getGeopoint();
            assert list != null;
            if(list.get(0) != null) {
                lat = list.get(0).getLatitude();
                lng = list.get(0).getLongitude();
        }

            NaverMapOptions options = new NaverMapOptions()
                    .camera(new CameraPosition(new LatLng(lat, lng), 16))
                    .mapType(NaverMap.MapType.Basic);
            if(mapFragment == null) {
                mapFragment = MapFragment.newInstance(options);
                getSupportFragmentManager().beginTransaction().add(R.id.naver_map_fragment, mapFragment).commit();
            }
        }
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        waiting.setVisibility(View.GONE);
        naverMap.setCameraPosition(new CameraPosition(new LatLng(lat, lng), 16));

        Marker marker = new Marker();
        marker.setPosition(new LatLng(lat, lng));
        marker.setWidth(Marker.SIZE_AUTO);
        marker.setHeight(Marker.SIZE_AUTO);
        marker.setCaptionText(mapname);
        marker.setCaptionAlign(Align.Top);
        marker.setCaptionOffset(18);
        marker.setMap(naverMap);

    }

    private List<Address> getGeopoint() {
        Geocoder geocoder = new Geocoder(this);
        List<Address> list = null;
        try {
            list = geocoder.getFromLocationName(mapaddress, 10);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("test","입출력 오류 - 서버에서 주소변환시 에러발생");
        }

        if(list != null) {
            return list;
        }
        return null;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
