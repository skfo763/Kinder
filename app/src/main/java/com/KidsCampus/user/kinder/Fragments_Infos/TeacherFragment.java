package com.KidsCampus.user.kinder.Fragments_Infos;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.KidsCampus.user.kinder.Models.TeacherModels;
import com.KidsCampus.user.kinder.R;


public class TeacherFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teacher, container, false);

        assert getArguments() != null;
        TeacherModels.teachers teacher = getArguments().getParcelable("data");
        if(teacher.incnt.equals("null")) teacher.incnt = "0";
        TextView name = view.findViewById(R.id.teacher_name_2text);
        TextView drcnt = view.findViewById(R.id.teacher_drcnttext);
        TextView adcnt = view.findViewById(R.id.teacher_adcnttext);
        TextView hdst_thcnt = view.findViewById(R.id.teacher_hdst_thcnttext);
        TextView asps_thcnt = view.findViewById(R.id.teacher_asps_thcnttext);
        TextView gnrl_thcnt = view.findViewById(R.id.teacher_gnrl_thcnttext);
        TextView spcn_thcnt = view.findViewById(R.id.teacher_spcn_thcnttext);
        TextView ntcnt = view.findViewById(R.id.teacher_ntcnttext);
        TextView ntrt_thcnt = view.findViewById(R.id.teacher_ntrt_thcnttext);
        TextView shcnt_thcnt = view.findViewById(R.id.teacher_shcnt_thcnttext);
        TextView incnt = view.findViewById(R.id.teacher_incnttext);
        TextView owcnt = view.findViewById(R.id.teacher_owcnttext);
        TextView hdst_tchr_qacnt = view.findViewById(R.id.teacher_hdst_tchr_qacnttext);
        TextView rgth_gd1_qacnt = view.findViewById(R.id.teacher_rgth_gd1_qacnttext);
        TextView rgth_gd2_qacnt = view.findViewById(R.id.teacher_rgth_gd2_qacnttext);
        TextView asth_qacnt = view.findViewById(R.id.teacher_asth_qacnttext);


        assert teacher != null;
        name.setText(teacher.kindername.concat("명"));
        drcnt.setText(teacher.drcnt.concat("명"));
        adcnt.setText(teacher.adcnt.concat("명"));
        hdst_thcnt.setText(teacher.hdst_thcnt.concat("명"));
        asps_thcnt.setText(teacher.asps_thcnt.concat("명"));
        gnrl_thcnt.setText(teacher.gnrl_thcnt.concat("명"));
        spcn_thcnt.setText(teacher.spcn_thcnt.concat("명"));
        ntcnt.setText(teacher.ntcnt.concat("명"));
        ntrt_thcnt.setText(teacher.ntrt_thcnt.concat("명"));
        shcnt_thcnt.setText(teacher.shcnt_thcnt.concat("명"));
        incnt.setText(teacher.incnt.concat("명"));
        owcnt.setText(teacher.owcnt.concat("명"));
        hdst_tchr_qacnt.setText(teacher.hdst_tchr_qacnt.concat("명"));
        rgth_gd1_qacnt.setText(teacher.rgth_gd1_qacnt.concat("명"));
        rgth_gd2_qacnt.setText(teacher.rgth_gd2_qacnt.concat("명"));
        asth_qacnt.setText(teacher.asth_qacnt.concat("명"));

        return view;
    }
}
