package com.KidsCampus.user.kinder.Location;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.textclassifier.TextLinks;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.KidsCampus.user.kinder.Models.BriefKinderModels;
import com.KidsCampus.user.kinder.R;
import com.KidsCampus.user.kinder.ShowKinderActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.NaverMapSdk;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Align;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.overlay.OverlayImage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    private MapFragment mapFragment;
    private LinearLayout waiting;
    private LinearLayout showkinder;
    private TextView title, establish, addrtext;
    private ImageView call, chevron;
    private NaverMap nMap;
    Map<String, Double> mLocation;
    String sido;
    String sgg;

    private final int PERMISSIONS_ACCESS_FINE_LOCATION = 1000;
    private final int PERMISSIONS_ACCESS_COARSE_LOCATION = 1001;
    private boolean isAccessFineLocation = false;
    private boolean isAccessCoarseLocation = false;
    private boolean isPermission = false;
    private GPSInfo gpsInfo;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String key = "086934842ed848beb75ef49b7e08ed5c";
    final Map<String, String> sido_map = new HashMap<>();
    ArrayList<BriefKinderModels.kinder> kAddress = new ArrayList<>();
    final String[] sidos = {"인천광역시", "서울특별시", "경기도", "충청남도", "충청북도", "세종특별자치시",
            "대전광역시", "전라남도", "전라북도", "광주광역시", "부산광역시", "울산광역시", "경상남도", "대구광역시",
            "경상북도", "강원도", "제주특별자치도"};

    final String[] codes = {"28", "11", "41", "44", "43", "36", "30", "46", "45", "29", "26", "31",
            "48", "27", "47", "42", "50"};

    public LocationActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        waiting = findViewById(R.id.location_waiting);
        showkinder = findViewById(R.id.location_showkinder);
        title = findViewById(R.id.location_title);
        establish = findViewById(R.id.location_establish);
        addrtext = findViewById(R.id.location_addr);
        call = findViewById(R.id.location_call);
        chevron = findViewById(R.id.locationitem_chevron);

        for(int i=0; i<sidos.length; i++) {
            sido_map.put(sidos[i], codes[i]);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LocationActivity.this, "네트워크 문제로 인해 로딩이 느려질 수 있습니다.", Toast.LENGTH_SHORT).show();
            }
        }, 3000);

        callPermission();

        mLocation = getGps();
        if(mLocation != null) {
            if (mLocation.size() != 0) {
                waiting.setVisibility(View.VISIBLE);
                Double lat = mLocation.get("latitude");
                Double lng = mLocation.get("longitude");
                getAddress(lat, lng);
            }
        } else {
            Toast.makeText(LocationActivity.this, "접근 권한이 없습니다.", Toast.LENGTH_SHORT).show();
            onBackPressed();
        }
    }


    @Override
    public void onMapReady(@NonNull final NaverMap naverMap) {
        nMap = naverMap;
        Double lat = mLocation.get("latitude");
        Double lng = mLocation.get("longitude");

        naverMap.setCameraPosition(new CameraPosition(new LatLng(lat, lng), 13));
        Marker marker = new Marker();
        marker.setPosition(new LatLng(lat, lng));
        marker.setWidth(Marker.SIZE_AUTO);
        marker.setHeight(Marker.SIZE_AUTO);
        marker.setCaptionText("현재 위치");
        marker.setCaptionAlign(Align.Top);
        marker.setCaptionOffset(18);
        marker.setMap(naverMap);

        if(sido.equals("세종특별자치시")) sgg = "세종특별자치시";
        getNearAddress(sido, sgg);

        naverMap.setOnMapDoubleTapListener(new NaverMap.OnMapDoubleTapListener() {
            @Override
            public boolean onMapDoubleTap(@NonNull PointF pointF, @NonNull LatLng latLng) {
                showkinder.setVisibility(View.GONE);
                return true;
            }
        });
    }

    public static Location findGeoPoint(Context mcontext, String address) {
        Location loc = new Location("");
        Geocoder coder = new Geocoder(mcontext);
        List<Address> addr = null;// 한좌표에 대해 두개이상의 이름이 존재할수있기에 주소배열을 리턴받기 위해 설정

        try {
            addr = coder.getFromLocationName(address, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }// 몇개 까지의 주소를 원하는지 지정 1~5개 정도가 적당
        if (addr != null) {
            for (int i = 0; i < addr.size(); i++) {
                Address lating = addr.get(i);
                double lat = lating.getLatitude(); // 위도가져오기
                double lon = lating.getLongitude(); // 경도가져오기
                loc.setLatitude(lat);
                loc.setLongitude(lon);
            }
        }
        return loc;
    }

    private void getAddress(final double lat, final double lng) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();

                HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse("https://naveropenapi.apigw.ntruss.com/map-reversegeocode/v2/gc")).newBuilder();
                urlBuilder.addEncodedQueryParameter("coords", lng+","+lat)
                        .addEncodedQueryParameter("output", "json");
                String requestUrl = urlBuilder.build().toString();

                Request request = new Request.Builder()
                        .url(requestUrl)
                        .header("X-NCP-APIGW-API-KEY-ID", "Password(blocked)")
                        .addHeader("X-NCP-APIGW-API-KEY", "Password(blocked)")
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Toast.makeText(LocationActivity.this, "현재 위치를 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try {
                            JSONObject object = new JSONObject(Objects.requireNonNull(response.body()).string());
                            sido = object.getJSONArray("results").getJSONObject(1).getJSONObject("region")
                                    .getJSONObject("area1").get("name").toString();
                            sgg = object.getJSONArray("results").getJSONObject(1).getJSONObject("region")
                                    .getJSONObject("area2").get("name").toString();

                            Message msg = setMap.obtainMessage();
                            setMap.sendMessage(msg);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();
    }

    private Map<String, Double> getGps() {
        Map<String, Double> loc = new HashMap<>();

        gpsInfo = new GPSInfo(LocationActivity.this);
        if(!isPermission) {
            callPermission();
        }

        if(gpsInfo.isGetLocation()) {
            loc.put("latitude", gpsInfo.getLatitude());
            loc.put("longitude", gpsInfo.getLongitude());
        } else {
            gpsInfo.showSettingsAlert();
        }
        return loc;
    }

    private void getNearAddress(final String sido, final String sgg) {
        db.collection(sido).document(sgg).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String sgg = documentSnapshot.getId();
                getInfoFromAPI(sido_map.get(sido), documentSnapshot.get("value").toString(), sgg, documentSnapshot.get("name").toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LocationActivity.this, "유치원을 불러오는데 실패했습니다", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getInfoFromAPI(final String sidoCode, final String sggCode, final String sidoname, final String sggname) {
        final String uri = "http://e-childschoolinfo.moe.go.kr/api/notice/basicInfo.do" + "?key="
                + key + "&sidoCode=" + sidoCode + "&sggCode=" + sggCode;

        new Thread() {
            @Override
            public void run() {
                String data = getData(uri);
                Bundle bun = new Bundle();
                bun.putString("data", data);
                bun.putString("sidoCode", sidoCode);
                bun.putString("sggCode", sggCode);
                bun.putString("sidoname", sidoname);
                bun.putString("sggname", sggname);

                Message msg = handler.obtainMessage();
                msg.setData(bun);
                handler.sendMessage(msg);
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

        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    @SuppressLint("HandlerLeak")
    Handler setMap = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.location_mapfragment);

            if(mapFragment == null) {
                mapFragment = MapFragment.newInstance();
                getSupportFragmentManager().beginTransaction().add(R.id.location_mapfragment, mapFragment).commit();
            }
            mapFragment.getMapAsync(LocationActivity.this);
        }
    };

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bun = msg.getData();
            String json = bun.getString("data");
            String sidoCode = bun.getString("sidoCode");
            String sggCode = bun.getString("sggCode");
            String sidoname = bun.getString("sidoname");
            String sggname = bun.getString("sggname");
            final ArrayList<BriefKinderModels.kinder> kindername = parsingData(json, sidoCode, sggCode, sidoname, sggname);

            if(kindername != null) {
                if (kindername.size() == 0) {
                    Toast.makeText(LocationActivity.this, "근처의 유치원이 없습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    for (int i=0; i<kindername.size(); i++) {
                        Marker marker = new Marker();
                        Location temp = findGeoPoint(LocationActivity.this, kindername.get(i).addr);

                        marker.setPosition(new LatLng(temp.getLatitude(), temp.getLongitude()));
                        marker.setWidth(50);
                        marker.setHeight(70);
                        marker.setCaptionText(kindername.get(i).kindername);
                        marker.setCaptionAlign(Align.Top);
                        marker.setCaptionOffset(5);
                        marker.setMap(nMap);

                        final int finalI = i;
                        marker.setOnClickListener(new Overlay.OnClickListener() {
                            @Override
                            public boolean onClick(@NonNull Overlay overlay) {
                                title.setText(kindername.get(finalI).kindername);
                                establish.setText(kindername.get(finalI).establish);
                                addrtext.setText(kindername.get(finalI).addr);

                                call.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String tell = kindername.get(finalI).telno;
                                        Intent intent = new Intent();
                                        intent.setAction(Intent.ACTION_DIAL);
                                        intent.setData(Uri.parse("tel:" + tell));
                                        startActivity(intent);
                                    }
                                });
                                chevron.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(LocationActivity.this, ShowKinderActivity.class);
                                        intent.putExtra("sidoCode", kindername.get(finalI).sidoCode);
                                        intent.putExtra("sggCode", kindername.get(finalI).sggCode);
                                        intent.putExtra("kindername", kindername.get(finalI).kindername);
                                        intent.putExtra("sidolocation", kindername.get(finalI).sidoname);
                                        intent.putExtra("sgglocation", kindername.get(finalI).sggname);
                                        startActivity(intent);
                                    }
                                });
                                showkinder.setVisibility(View.VISIBLE);
                                return false;
                            }
                        });
                    }
                }
                waiting.setVisibility(View.GONE);
            }
        }
    };

    private ArrayList<BriefKinderModels.kinder> parsingData(String json, String sidoCode, String sggCode, String sidoname, String sggname) {
        ArrayList<BriefKinderModels.kinder> titles  = new ArrayList<>();

        try {
            JSONObject data = new JSONObject(json);
            switch (data.getString("status")) {
                case "SUCCESS" :
                    JSONArray jsonArray = data.getJSONArray("kinderInfo");
                    for(int i=0; i<jsonArray.length(); i++) {
                        BriefKinderModels.kinder temp = new BriefKinderModels.kinder();
                        JSONObject object = jsonArray.getJSONObject(i);
                        temp.sidoCode = sidoCode;
                        temp.sggCode = sggCode;
                        temp.sidoname = sidoname;
                        temp.sggname = sggname;
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_ACCESS_FINE_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            isAccessFineLocation = true;

        } else if (requestCode == PERMISSIONS_ACCESS_COARSE_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            isAccessCoarseLocation = true;
        }

        if (isAccessFineLocation && isAccessCoarseLocation) {
            isPermission = true;
        }
    }

    private void callPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_ACCESS_FINE_LOCATION);

            } else if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_ACCESS_COARSE_LOCATION);
            } else {
                isPermission = true;
            }
        } else {
            isPermission = true;
        }
    }

    private class BackGroundTask extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] objects) {
            return null;
        }
    }
}
