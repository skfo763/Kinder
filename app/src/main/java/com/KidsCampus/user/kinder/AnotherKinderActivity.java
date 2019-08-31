package com.KidsCampus.user.kinder;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.KidsCampus.user.kinder.Fragments_Infos.BuildingFragment;
import com.KidsCampus.user.kinder.Fragments_Infos.ClassFragment;
import com.KidsCampus.user.kinder.Fragments_Infos.HygeneFragment;
import com.KidsCampus.user.kinder.Fragments_Infos.InsuranceFragment;
import com.KidsCampus.user.kinder.Fragments_Infos.LectureFragment;
import com.KidsCampus.user.kinder.Fragments_Infos.MealFragment;
import com.KidsCampus.user.kinder.Fragments_Infos.SafetyFragment;
import com.KidsCampus.user.kinder.Fragments_Infos.SchoolbusFragment;
import com.KidsCampus.user.kinder.Fragments_Infos.SocietyFragment;
import com.KidsCampus.user.kinder.Fragments_Infos.TeacherFragment;
import com.KidsCampus.user.kinder.Fragments_Infos.YearofworkFragment;
import com.KidsCampus.user.kinder.Models.BuildingModels;
import com.KidsCampus.user.kinder.Models.ClassModels;
import com.KidsCampus.user.kinder.Models.HygeneModels;
import com.KidsCampus.user.kinder.Models.InsuranceModels;
import com.KidsCampus.user.kinder.Models.LectureModels;
import com.KidsCampus.user.kinder.Models.MealModels;
import com.KidsCampus.user.kinder.Models.SafetyModels;
import com.KidsCampus.user.kinder.Models.SchoolbusModels;
import com.KidsCampus.user.kinder.Models.SocietyModels;
import com.KidsCampus.user.kinder.Models.TeacherModels;
import com.KidsCampus.user.kinder.Models.YearofworkModels;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class AnotherKinderActivity extends AppCompatActivity {

    private String kindername, select, sidoCode, sggCode;
    private String key = "086934842ed848beb75ef49b7e08ed5c";
    private TextView title;
    private TextView moreinfo;
    private LinearLayout waiting;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_another_kinder);
        waiting = findViewById(R.id.another_kinder_waiting);
        waiting.setVisibility(View.VISIBLE);

        kindername = getIntent().getStringExtra("kindername");
        select = getIntent().getStringExtra("select");
        sidoCode = getIntent().getStringExtra("sidoCode");
        sggCode = getIntent().getStringExtra("sggCode");
        title = findViewById(R.id.another_kinder_name);
        moreinfo = findViewById(R.id.another_kinder_moreinfo);
        TextView seemore = findViewById(R.id.another_kinder_seemore);
        ImageView seemore2 = findViewById(R.id.another_kinder_seemore2);

        String uriString = getUriString(select, sidoCode, sggCode);
        if(uriString != null) getJson(uriString);
        waiting.setVisibility(View.GONE);

        seemore2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                movetoWindow();
            }
        });

        seemore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                movetoWindow();

            }
        });

    }

    private void movetoWindow() {
        onBackPressed();
    }

    private String getUriString(String select, String sidoCode, String sggCode) {
        String uri = null;
        switch(select) {
            case "일반현황":
                break;
            case "건물현황":
                uri = "http://e-childschoolinfo.moe.go.kr/api/notice/building.do";
                break;
            case "교실면적현황":
                uri = "http://e-childschoolinfo.moe.go.kr/api/notice/classArea.do";
                break;
            case "직위/자격별교직원현황":
                uri = "http://e-childschoolinfo.moe.go.kr/api/notice/teachersInfo.do";
                break;
            case "수업일수현황":
                uri = "http://e-childschoolinfo.moe.go.kr/api/notice/lessonDay.do";
                break;
            case "급식운영현황":
                uri = "http://e-childschoolinfo.moe.go.kr/api/notice/schoolMeal.do";
                break;
            case "통학차랑현황":
                uri = "http://e-childschoolinfo.moe.go.kr/api/notice/schoolBus.do";
                break;
            case "근속연수현황":
                uri = "http://e-childschoolinfo.moe.go.kr/api/notice/yearOfWork.do";
                break;
            case "환경위생관리현황":
                uri = "http://e-childschoolinfo.moe.go.kr/api/notice/environmentHygiene.do";
                break;
            case "안전점검/교육실시현황":
                uri = "http://e-childschoolinfo.moe.go.kr/api/notice/safetyEdu.do";
                break;
            case "공제회 가입 현황":
                uri = "http://e-childschoolinfo.moe.go.kr/api/notice/deductionSociety.do";
                break;
            case "보험별 가입 현황" :
                uri = "http://e-childschoolinfo.moe.go.kr/api/notice/insurance.do";
                break;
            default:
                uri = null;
                break;
        }

        if(uri != null) {
            return uri + "?key=" + key  + "&" + "sidoCode=" + sidoCode + "&" + "sggCode=" + sggCode;
        }
        return null;
    }

    private void getJson(final String uriString) {
        new Thread() {
            @Override
            public void run() {
                String data = getData(uriString);
                Bundle bundle = new Bundle();
                bundle.putString("data", data);
                Message msg = handler.obtainMessage();
                msg.setData(bundle);
                handler.sendMessage(msg);
                super.run();
            }
        }.start();
    }

    private String getData(String uriString) {
        URL url;
        BufferedReader bf;
        String line="";
        String result="";

        try {
            url = new URL(uriString);
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
            try {
                setdata(select, json);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private JSONObject parsedata(String json) {
        try {
            JSONObject data = new JSONObject(json);
            switch (data.getString("status")) {
                case "SUCCESS":
                    JSONArray jsonArray = data.getJSONArray("kinderInfo");
                    for(int i=0; i<jsonArray.length(); i++) {
                        JSONObject objects = jsonArray.getJSONObject(i);
                        String check = objects.getString("kindername");
                        if(check.equals(kindername)) return objects;
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

    private void setdata(String select, String json) throws JSONException {
        switch(select) {
            case "일반현황":
                return;
            case "건물현황":
                JSONObject object = parsedata(json);
                BuildingModels.building building = new BuildingModels.building();
                assert object != null;
                building.officeedu = object.getString("officeedu");
                building.subofficeedu = object.getString("subofficeedu");
                building.kindername = object.getString("kindername");
                building.bldgprusarea = object.getString("bldgprusarea");
                building.archyy = object.getString("archyy");
                building.floorcnt = object.getString("floorcnt");
                building.grottar = object.getString("grottar");

                Fragment fragment = new BuildingFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable("data", building);
                fragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.another_kinder_framelayout, fragment).commit();
                break;
            case "교실면적현황":
                JSONObject object2 = parsedata(json);
                ClassModels.classmod classmod = new ClassModels.classmod();
                assert object2 != null;
                classmod.officeedu = object2.getString("officeedu");
                classmod.subofficeedu = object2.getString("subofficeedu");
                classmod.kindername = object2.getString("kindername");
                classmod.clsrarea = object2.getString("clsrarea");
                classmod.crcnt = object2.getString("crcnt");
                classmod.hlsparea = object2.getString("hlsparea");
                classmod.ktchmssparea = object2.getString("ktchmssparea");
                classmod.otsparea = object2.getString("otsparea");
                classmod.phgrindrarea = object2.getString("phgrindrarea");

                Fragment fragment2 = new ClassFragment();
                Bundle bundle2 = new Bundle();
                bundle2.putParcelable("data", classmod);
                fragment2.setArguments(bundle2);
                getSupportFragmentManager().beginTransaction().replace(R.id.another_kinder_framelayout, fragment2).commit();
                break;

            case "직위/자격별교직원현황":
                JSONObject object3 = parsedata(json);
                TeacherModels.teachers teachers = new TeacherModels.teachers();
                teachers.officeedu = object3.getString("officeedu");
                teachers.subofficeedu = object3.getString("subofficeedu");
                teachers.kindername = object3.getString("kindername");
                teachers.drcnt = object3.getString("drcnt");
                teachers.adcnt = object3.getString("adcnt");
                teachers.hdst_thcnt = object3.getString("hdst_thcnt");
                teachers.asps_thcnt = object3.getString("asps_thcnt");
                teachers.gnrl_thcnt = object3.getString("gnrl_thcnt");
                teachers.spcn_thcnt = object3.getString("spcn_thcnt");
                teachers.ntcnt = object3.getString("ntcnt");
                teachers.ntrt_thcnt = object3.getString("ntrt_thcnt");
                teachers.shcnt_thcnt = object3.getString("shcnt_thcnt");
                teachers.incnt = object3.getString("incnt");
                teachers.owcnt = object3.getString("owcnt");
                teachers.hdst_tchr_qacnt = object3.getString("hdst_tchr_qacnt");
                teachers.rgth_gd1_qacnt = object3.getString("rgth_gd1_qacnt");
                teachers.rgth_gd2_qacnt = object3.getString("rgth_gd2_qacnt");
                teachers.asth_qacnt = object3.getString("asth_qacnt");

                Fragment fragment3 = new TeacherFragment();
                Bundle bundle3 = new Bundle();
                bundle3.putParcelable("data", teachers);
                fragment3.setArguments(bundle3);
                getSupportFragmentManager().beginTransaction().replace(R.id.another_kinder_framelayout, fragment3).commit();
                break;

            case "수업일수현황":
                JSONObject object4 = parsedata(json);
                LectureModels.lectures lectures = new LectureModels.lectures();
                lectures.officeedu = object4.getString("officeedu");
                lectures.subofficeedu = object4.getString("subofficeedu");
                lectures.kindername = object4.getString("kindername");
                lectures.afsc_pros_lsn_dcnt = object4.getString("afsc_pros_lsn_dcnt");
                lectures.ag3_lsn_dcnt = object4.getString("ag3_lsn_dcnt");
                lectures.ag4_lsn_dcnt = object4.getString("ag4_lsn_dcnt");
                lectures.ag5_lsn_dcnt = object4.getString("ag5_lsn_dcnt");
                lectures.fdtn_kndr_yn = object4.getString("fdtn_kndr_yn");
                lectures.ldnum_blw_yn = object4.getString("ldnum_blw_yn");
                lectures.mix_age_lsn_dcnt = object4.getString("mix_age_lsn_dcnt");
                lectures.spcl_lsn_dcnt = object4.getString("spcl_lsn_dcnt");

                Fragment fragment4 = new LectureFragment();
                Bundle bundle4 = new Bundle();
                bundle4.putParcelable("data", lectures);
                fragment4.setArguments(bundle4);
                getSupportFragmentManager().beginTransaction().replace(R.id.another_kinder_framelayout, fragment4).commit();
                break;

            case "급식운영현황":
                JSONObject object5 = parsedata(json);
                MealModels.meals meals = new MealModels.meals();
                meals.officeedu = object5.getString("officeedu");
                meals.subofficeedu = object5.getString("subofficeedu");
                meals.kindername = object5.getString("kindername");
                meals.al_kpcnt = object5.getString("al_kpcnt");
                meals.ckcnt = object5.getString("ckcnt");
                meals.cmcnt = object5.getString("cmcnt");
                meals.cons_ents_nm = object5.getString("cons_ents_nm");
                meals.mas_mspl_dclr_yn = object5.getString("mas_mspl_dclr_yn");
                meals.cprt_agmt_ntrt_thcnt = object5.getString("cprt_agmt_ntrt_thcnt");
                meals.mlsr_kpcnt = object5.getString("mlsr_kpcnt");
                meals.mlsr_oprn_way_tp_cd = object5.getString("mlsr_oprn_way_tp_cd");
                meals.ntrt_tchr_agmt_yn = object5.getString("ntrt_tchr_agmt_yn");
                meals.snge_agmt_ntrt_thcnt = object5.getString("snge_agmt_ntrt_thcnt");

                Fragment fragment5 = new MealFragment();
                Bundle bundle5 = new Bundle();
                bundle5.putParcelable("data", meals);
                fragment5.setArguments(bundle5);
                getSupportFragmentManager().beginTransaction().replace(R.id.another_kinder_framelayout, fragment5).commit();
                break;

            case "통학차랑현황":
                JSONObject object6 = parsedata(json);
                SchoolbusModels.schoolbus schoolbus = new SchoolbusModels.schoolbus();
                schoolbus.officeedu = object6.getString("officeedu");
                schoolbus.subofficeedu = object6.getString("subofficeedu");
                schoolbus.kindername = object6.getString("kindername");
                schoolbus.vhcl_oprn_yn = object6.getString("vhcl_oprn_yn");
                schoolbus.opra_vhcnt = object6.getString("opra_vhcnt");
                schoolbus.dclr_vhcnt = object6.getString("dclr_vhcnt");
                schoolbus.psg9_dclr_vhcnt = object6.getString("psg9_dclr_vhcnt");
                schoolbus.psg12_dclr_vhcnt = object6.getString("psg12_dclr_vhcnt");
                schoolbus.psg15_dclr_vhcnt = object6.getString("psg15_dclr_vhcnt");

                Fragment fragment6 = new SchoolbusFragment();
                Bundle bundle6 = new Bundle();
                bundle6.putParcelable("data", schoolbus);
                fragment6.setArguments(bundle6);
                getSupportFragmentManager().beginTransaction().replace(R.id.another_kinder_framelayout, fragment6).commit();
                break;

            case "근속연수현황":
                JSONObject object7 = parsedata(json);
                YearofworkModels.yearofwork yearbook = new YearofworkModels.yearofwork();
                yearbook.officeedu = object7.getString("officeedu");
                yearbook.subofficeedu = object7.getString("subofficeedu");
                yearbook.kindername = object7.getString("kindername");
                yearbook.yy1_undr_thcnt = object7.getString("yy1_undr_thcnt");
                yearbook.yy1_abv_yy2_undr_thcnt = object7.getString("yy1_abv_yy2_undr_thcnt");
                yearbook.yy2_abv_yy4_undr_thcnt = object7.getString("yy2_abv_yy4_undr_thcnt");
                yearbook.yy4_abv_yy6_undr_thcnt = object7.getString("yy4_abv_yy6_undr_thcnt");
                yearbook.yy6_abv_thcnt = object7.getString("yy6_abv_thcnt");

                Fragment fragment7 = new YearofworkFragment();
                Bundle bundle7 = new Bundle();
                bundle7.putParcelable("data", yearbook);
                fragment7.setArguments(bundle7);
                getSupportFragmentManager().beginTransaction().replace(R.id.another_kinder_framelayout, fragment7).commit();
                break;

            case "환경위생관리현황":
                JSONObject object8 = parsedata(json);
                HygeneModels.hygene hygene = new HygeneModels.hygene();
                hygene.officeedu = object8.getString("officeedu");
                hygene.subofficeedu = object8.getString("subofficeedu");
                hygene.kindername = object8.getString("kindername");
                hygene.arql_chk_dt = object8.getString("arql_chk_dt");
                hygene.arql_chk_rslt_tp_cd = object8.getString("arql_chk_rslt_tp_cd");
                hygene.fxtm_dsnf_chk_rslt_tp_cd = object8.getString("fxtm_dsnf_chk_rslt_tp_cd");
                hygene.fxtm_dsnf_trgt_yn = object8.getString("fxtm_dsnf_trgt_yn");
                hygene.fxtm_dsnf_chk_dt = object8.getString("fxtm_dsnf_chk_dt");
                hygene.tp_01 = object8.getString("tp_01");
                hygene.tp_02 = object8.getString("tp_02");
                hygene.tp_03 = object8.getString("tp_03");
                hygene.tp_04 = object8.getString("tp_04");
                hygene.unwt_qlwt_insc_yn = object8.getString("unwt_qlwt_insc_yn");
                hygene.qlwt_insc_dt = object8.getString("qlwt_insc_dt");
                hygene.qlwt_insc_stby_yn = object8.getString("qlwt_insc_stby_yn");
                hygene.mdst_chk_dt = object8.getString("mdst_chk_dt");
                hygene.mdst_chk_rslt_cd = object8.getString("mdst_chk_rslt_cd");
                hygene.ilmn_chk_dt = object8.getString("ilmn_chk_dt");
                hygene.ilmn_chk_rslt_cd = object8.getString("ilmn_chk_rslt_cd");

                Fragment fragment8 = new HygeneFragment();
                Bundle bundle8 = new Bundle();
                bundle8.putParcelable("data", hygene);
                fragment8.setArguments(bundle8);
                getSupportFragmentManager().beginTransaction().replace(R.id.another_kinder_framelayout, fragment8).commit();
                break;

            case "안전점검/교육실시현황":
                JSONObject object9 = parsedata(json);
                SafetyModels.safety safety = new SafetyModels.safety();
                safety.officeedu = object9.getString("officeedu");
                safety.subofficeedu = object9.getString("subofficeedu");
                safety.kindername = object9.getString("kindername");
                safety.fire_avd_yn = object9.getString("fire_avd_yn");
                safety.fire_avd_dt = object9.getString("fire_avd_dt");
                safety.gas_ck_yn = object9.getString("gas_ck_yn");
                safety.gas_ck_dt = object9.getString("gas_ck_dt");
                safety.fire_safe_yn = object9.getString("fire_safe_yn");
                safety.fire_safe_dt = object9.getString("fire_safe_dt");
                safety.elect_ck_yn = object9.getString("elect_ck_yn");
                safety.elect_ck_dt = object9.getString("elect_ck_dt");
                safety.plyg_ck_yn = object9.getString("plyg_ck_yn");
                safety.plyg_ck_dt = object9.getString("plyg_ck_dt");
                safety.plyg_ck_rs_cd = object9.getString("plyg_ck_rs_cd");
                safety.cctv_ist_yn = object9.getString("cctv_ist_yn");
                safety.cctv_ist_total = object9.getString("cctv_ist_total");
                safety.cctv_ist_in = object9.getString("cctv_ist_in");
                safety.cctv_ist_out = object9.getString("cctv_ist_out");

                Fragment fragment9 = new SafetyFragment();
                Bundle bundle9 = new Bundle();
                bundle9.putParcelable("data", safety);
                fragment9.setArguments(bundle9);
                getSupportFragmentManager().beginTransaction().replace(R.id.another_kinder_framelayout, fragment9).commit();
                break;

            case "공제회 가입 현황":
                JSONObject object10 = parsedata(json);
                SocietyModels.societies societies = new SocietyModels.societies();
                societies.officeedu = object10.getString("officeedu");
                societies.subofficeedu = object10.getString("subofficeedu");
                societies.kindername = object10.getString("kindername");
                societies.school_ds_yn = object10.getString("school_ds_yn");
                societies.school_ds_en = object10.getString("school_ds_en");
                societies.educate_ds_yn = object10.getString("educate_ds_yn");
                societies.ducate_ds_en = object10.getString("educate_ds_en");

                Fragment fragment10 = new SocietyFragment();
                Bundle bundle10 = new Bundle();
                bundle10.putParcelable("data", societies);
                fragment10.setArguments(bundle10);
                getSupportFragmentManager().beginTransaction().replace(R.id.another_kinder_framelayout, fragment10).commit();
                break;

            case "보험별 가입 현황":
                JSONObject object11 = parsedata(json);
                InsuranceModels.insurance insurance = new InsuranceModels.insurance();
                insurance.officeedu = object11.getString("officeedu");
                insurance.subofficeedu = object11.getString("subofficeedu");
                insurance.kindername = object11.getString("kindername");
                insurance.insurance_nm = object11.getString("insurance_nm");
                insurance.insurance_en = object11.getString("insurance_en");
                insurance.insurance_yn = object11.getString("insurance_yn");
                insurance.company1 = object11.getString("company1");
                insurance.company2 = object11.getString("company2");
                insurance.company3 = object11.getString("company3");

                Fragment fragment11 = new InsuranceFragment();
                Bundle bundle11 = new Bundle();
                bundle11.putParcelable("data", insurance);
                fragment11.setArguments(bundle11);
                getSupportFragmentManager().beginTransaction().replace(R.id.another_kinder_framelayout, fragment11).commit();
                break;

            default:
                break;
        }
        String mf = "상세정보 - " + select;
        title.setText(kindername);
        moreinfo.setText(mf);
    }
}

