package com.KidsCampus.user.kinder.Fragments_Infos;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.KidsCampus.user.kinder.Models.YearofworkModels;
import com.KidsCampus.user.kinder.R;


public class YearofworkFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_yearofwork, container, false);

        assert getArguments() != null;
        YearofworkModels.yearofwork yearofwork = getArguments().getParcelable("data");
        TextView name = view.findViewById(R.id.yearofwork_name_2text);
        TextView yy1_undr_thcnt = view.findViewById(R.id.yearofwork_yy1_undr_thcnttext);
        TextView yy1_abv_yy2_undr_thcnt = view.findViewById(R.id.yearofwork_yy1_abv_yy2_undr_thcnttext);
        TextView yy2_abv_yy4_undr_thcnt = view.findViewById(R.id.yearofwork_yy2_abv_yy4_undr_thcnttext);
        TextView yy4_abv_yy6_undr_thcnt = view.findViewById(R.id.yearofwork_yy4_abv_yy6_undr_thcnttext);
        TextView yy6_abv_thcnt = view.findViewById(R.id.yearofwork_yy6_abv_thcnttext);


        assert yearofwork != null;
        name.setText(yearofwork.kindername);
        yy1_undr_thcnt.setText(yearofwork.yy1_undr_thcnt.concat("명"));
        yy1_abv_yy2_undr_thcnt.setText(yearofwork.yy1_abv_yy2_undr_thcnt.concat("명"));
        yy2_abv_yy4_undr_thcnt.setText(yearofwork.yy2_abv_yy4_undr_thcnt.concat("명"));
        yy4_abv_yy6_undr_thcnt.setText(yearofwork.yy4_abv_yy6_undr_thcnt.concat("명"));
        yy6_abv_thcnt.setText(yearofwork.yy6_abv_thcnt.concat("명"));

        return view;
    }
}
