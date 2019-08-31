package com.KidsCampus.user.kinder.Fragments_Infos;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.KidsCampus.user.kinder.Models.MealModels;
import com.KidsCampus.user.kinder.R;


public class MealFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meal, container, false);

        assert getArguments() != null;
        MealModels.meals meal = getArguments().getParcelable("data");
        assert meal != null;

        if(meal.cons_ents_nm.equals("null")) meal.cons_ents_nm = "해당사항 없음";
        if(meal.al_kpcnt.equals("null")) meal.al_kpcnt = "0";
        if(meal.mlsr_kpcnt.equals("null")) meal.mlsr_kpcnt = "0";
        if(meal.snge_agmt_ntrt_thcnt.equals("null")) meal.snge_agmt_ntrt_thcnt = "0";
        if(meal.cprt_agmt_ntrt_thcnt.equals("null")) meal.cprt_agmt_ntrt_thcnt = "0";
        if(meal.ckcnt.equals("null")) meal.ckcnt = "0";
        if(meal.cmcnt.equals("null")) meal.cmcnt = "0";

        TextView name = view.findViewById(R.id.meal_name_2text);
        TextView mlsr_oprn_way_tp_cd = view.findViewById(R.id.meal_mlsr_oprn_way_tp_cdtext);
        TextView cons_ents_nm = view.findViewById(R.id.meal_cons_ents_nmtext);
        TextView al_kpcnt = view.findViewById(R.id.meal_al_kpcnttext);
        TextView mlsr_kpcnt = view.findViewById(R.id.meal_mlsr_kpcnttext);
        TextView ntrt_tchr_agmt_yn = view.findViewById(R.id.meal_ntrt_tchr_agmt_yntext);
        TextView snge_agmt_ntrt_thcnt = view.findViewById(R.id.meal_snge_agmt_ntrt_thcnttext);
        TextView cprt_agmt_ntrt_thcnt = view.findViewById(R.id.meal_cprt_agmt_ntrt_thcnttext);
        TextView ckcnt = view.findViewById(R.id.meal_ckcnttext);
        TextView cmcnt = view.findViewById(R.id.meal_cmcnttext);
        TextView mas_mspl_dclr_yn = view.findViewById(R.id.meal_mas_mspl_dclr_yntext);

        name.setText(meal.kindername);
        mlsr_oprn_way_tp_cd.setText(meal.mlsr_oprn_way_tp_cd);
        cons_ents_nm.setText(meal.cons_ents_nm);

        al_kpcnt.setText(meal.al_kpcnt);
        if(meal.al_kpcnt.equals("null")) al_kpcnt.setText("정보 없음");
        al_kpcnt.setText(meal.al_kpcnt.concat("명"));

        mlsr_kpcnt.setText(meal.mlsr_kpcnt.concat("명"));
        ntrt_tchr_agmt_yn.setText(meal.ntrt_tchr_agmt_yn);
        snge_agmt_ntrt_thcnt.setText(meal.snge_agmt_ntrt_thcnt.concat("명"));
        cprt_agmt_ntrt_thcnt.setText(meal.cprt_agmt_ntrt_thcnt.concat("명"));
        ckcnt.setText(meal.ckcnt.concat("명"));
        cmcnt.setText(meal.cmcnt.concat("명"));
        mas_mspl_dclr_yn.setText(meal.mas_mspl_dclr_yn);
        return view;
    }
}
