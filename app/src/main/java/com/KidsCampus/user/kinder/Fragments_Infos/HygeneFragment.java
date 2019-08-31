package com.KidsCampus.user.kinder.Fragments_Infos;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.KidsCampus.user.kinder.Models.HygeneModels;
import com.KidsCampus.user.kinder.R;


public class HygeneFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hygene, container, false);

        assert getArguments() != null;
        HygeneModels.hygene hygene = getArguments().getParcelable("data");
        TextView name = view.findViewById(R.id.hygene_name_2text);
        TextView arql_chk_dt = view.findViewById(R.id.hygene_arql_chk_dttext);
        TextView arql_chk_rslt_tp_cd = view.findViewById(R.id.hygene_arql_chk_rslt_tp_cdtext);
        TextView fxtm_dsnf_trgt_yn = view.findViewById(R.id.hygene_fxtm_dsnf_trgt_yntext);
        TextView fxtm_dsnf_chk_dt = view.findViewById(R.id.hygene_fxtm_dsnf_chk_dttext);
        TextView fxtm_dsnf_chk_rslt_tp_cd = view.findViewById(R.id.hygene_fxtm_dsnf_chk_rslt_tp_cdtext);
        TextView tp_01 = view.findViewById(R.id.hygene_tp_01text);
        TextView tp_02 = view.findViewById(R.id.hygene_tp_02text);
        TextView tp_03 = view.findViewById(R.id.hygene_tp_03text);
        TextView tp_04 = view.findViewById(R.id.hygene_tp_04text);
        TextView unwt_qlwt_insc_yn = view.findViewById(R.id.hygene_unwt_qlwt_insc_yntext);
        TextView qlwt_insc_dt = view.findViewById(R.id.hygene_qlwt_insc_dttext);
        TextView qlwt_insc_stby_yn = view.findViewById(R.id.hygene_qlwt_insc_stby_yntext);
        TextView mdst_chk_dt = view.findViewById(R.id.hygene_mdst_chk_dttext);
        TextView mdst_chk_rslt_cd = view.findViewById(R.id.hygene_mdst_chk_rslt_cdtext);
        TextView ilmn_chk_dt = view.findViewById(R.id.hygene_ilmn_chk_dttext);
        TextView ilmn_chk_rslt_cd  = view.findViewById(R.id.hygene_ilmn_chk_rslt_cdtext);


        assert hygene != null;
        name.setText(hygene.kindername);
        arql_chk_dt.setText(hygene.arql_chk_dt);
        arql_chk_rslt_tp_cd.setText(hygene.arql_chk_rslt_tp_cd);
        fxtm_dsnf_trgt_yn.setText(hygene.fxtm_dsnf_trgt_yn);
        fxtm_dsnf_chk_dt.setText(hygene.fxtm_dsnf_chk_dt);
        fxtm_dsnf_chk_rslt_tp_cd.setText(hygene.fxtm_dsnf_chk_rslt_tp_cd);
        tp_01.setText(hygene.tp_01);
        tp_02.setText(hygene.tp_02);
        tp_03.setText(hygene.tp_03);
        tp_04.setText(hygene.tp_04);
        unwt_qlwt_insc_yn.setText(hygene.unwt_qlwt_insc_yn);
        qlwt_insc_dt.setText(hygene.qlwt_insc_dt);
        qlwt_insc_stby_yn.setText(hygene.qlwt_insc_stby_yn);
        mdst_chk_dt.setText(hygene.mdst_chk_dt);
        mdst_chk_rslt_cd.setText(hygene.mdst_chk_rslt_cd);
        ilmn_chk_dt.setText(hygene.ilmn_chk_dt);
        ilmn_chk_rslt_cd.setText(hygene.ilmn_chk_rslt_cd);

        return view;
    }
}
