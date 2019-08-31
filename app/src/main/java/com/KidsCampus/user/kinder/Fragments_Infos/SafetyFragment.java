package com.KidsCampus.user.kinder.Fragments_Infos;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.KidsCampus.user.kinder.Models.SafetyModels;
import com.KidsCampus.user.kinder.R;


public class SafetyFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_safety, container, false);

        assert getArguments() != null;
        SafetyModels.safety safety = getArguments().getParcelable("data");
        if(safety.cctv_ist_total.equals("null")) safety.cctv_ist_total = "정보 없음";
        if(safety.cctv_ist_in.equals("null")) safety.cctv_ist_in = "정보 없음";
        if(safety.cctv_ist_out.equals("null")) safety.cctv_ist_out = "정보 없음";

        TextView name = view.findViewById(R.id.safety_name_2text);
        TextView fire_avd_yn = view.findViewById(R.id.safety_fire_avd_yntext);
        TextView fire_avd_dt = view.findViewById(R.id.safety_fire_avd_dttext);
        TextView gas_ck_yn = view.findViewById(R.id.safety_gas_ck_yntext);
        TextView gas_ck_dt = view.findViewById(R.id.safety_gas_ck_dttext);
        TextView fire_safe_yn = view.findViewById(R.id.safety_fire_safe_yntext);
        TextView fire_safe_dt = view.findViewById(R.id.safety_fire_safe_dttext);
        TextView elect_ck_yn = view.findViewById(R.id.safety_elect_ck_yntext);
        TextView elect_ck_dt = view.findViewById(R.id.safety_elect_ck_dttext);
        TextView plyg_ck_yn = view.findViewById(R.id.safety_plyg_ck_yntext);
        TextView plyg_ck_dt = view.findViewById(R.id.safety_plyg_ck_dttext);
        TextView plyg_ck_rs_cd = view.findViewById(R.id.safety_plyg_ck_rs_cdtext);
        TextView cctv_ist_yn = view.findViewById(R.id.safety_cctv_ist_yntext);
        TextView cctv_ist_total = view.findViewById(R.id.safety_cctv_ist_totaltext);
        TextView cctv_ist_in = view.findViewById(R.id.safety_cctv_ist_intext);
        TextView cctv_ist_out = view.findViewById(R.id.safety_cctv_ist_outtext);


        assert safety != null;
        name.setText(safety.kindername);
        fire_avd_yn.setText(safety.fire_avd_yn);
        fire_avd_dt.setText(safety.fire_avd_dt);
        gas_ck_yn.setText(safety.gas_ck_yn);
        gas_ck_dt.setText(safety.gas_ck_dt);
        fire_safe_yn.setText(safety.fire_safe_yn);
        fire_safe_dt.setText(safety.fire_safe_dt);
        elect_ck_yn.setText(safety.elect_ck_yn);
        elect_ck_dt.setText(safety.elect_ck_dt);
        plyg_ck_yn.setText(safety.plyg_ck_yn);
        plyg_ck_dt.setText(safety.plyg_ck_dt);
        plyg_ck_rs_cd.setText(safety.plyg_ck_rs_cd);
        cctv_ist_yn.setText(safety.cctv_ist_yn);
        cctv_ist_total.setText(safety.cctv_ist_total);
        cctv_ist_in.setText(safety.cctv_ist_in);
        cctv_ist_out.setText(safety.cctv_ist_out);

        return view;
    }
}
