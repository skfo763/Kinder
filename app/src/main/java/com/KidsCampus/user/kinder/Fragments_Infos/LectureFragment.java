package com.KidsCampus.user.kinder.Fragments_Infos;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.KidsCampus.user.kinder.Models.LectureModels;
import com.KidsCampus.user.kinder.R;


public class LectureFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lecture, container, false);

        assert getArguments() != null;
        LectureModels.lectures lecture = getArguments().getParcelable("data");

        if(lecture.mix_age_lsn_dcnt.equals("null")) lecture.mix_age_lsn_dcnt = "0";
        if(lecture.spcl_lsn_dcnt.equals("null")) lecture.spcl_lsn_dcnt = "0";
        if(lecture.ldnum_blw_yn.equals("null")) lecture.ldnum_blw_yn = "해당사항 없음";
        if(lecture.fdtn_kndr_yn.equals("null")) lecture.fdtn_kndr_yn = "해당사항 없음";


        TextView name = view.findViewById(R.id.lecture_name_2text);
        TextView ag3_lsn_dcnt = view.findViewById(R.id.lecture_ag3_lsn_dcnttext);
        TextView ag4_lsn_dcnt = view.findViewById(R.id.lecture_ag4_lsn_dcnttext);
        TextView ag5_lsn_dcnt = view.findViewById(R.id.lecture_ag5_lsn_dcnttext);
        TextView mix_age_lsn_dcnt = view.findViewById(R.id.lecture_mix_age_lsn_dcnttext);
        TextView spcl_lsn_dcnt = view.findViewById(R.id.lecture_spcl_lsn_dcnttext);
        TextView ldnum_blw_yn = view.findViewById(R.id.lecture_ldnum_blw_yntext);
        TextView afsc_pros_lsn_dcnt = view.findViewById(R.id.lecture_afsc_pros_lsn_dcnttext);
        TextView fdtn_kndr_yn = view.findViewById(R.id.lecture_fdtn_kndr_yntext);

        assert lecture != null;
        name.setText(lecture.kindername);
        ag3_lsn_dcnt.setText(lecture.ag3_lsn_dcnt.concat("일"));
        ag4_lsn_dcnt.setText(lecture.ag4_lsn_dcnt.concat("일"));
        ag5_lsn_dcnt.setText(lecture.ag5_lsn_dcnt.concat("일"));
        mix_age_lsn_dcnt.setText(lecture.mix_age_lsn_dcnt.concat("일"));
        spcl_lsn_dcnt.setText(lecture.spcl_lsn_dcnt.concat("일"));
        ldnum_blw_yn.setText(lecture.ldnum_blw_yn);
        afsc_pros_lsn_dcnt.setText(lecture.afsc_pros_lsn_dcnt.concat("일"));
        fdtn_kndr_yn.setText(lecture.fdtn_kndr_yn);

        return view;
    }
}
