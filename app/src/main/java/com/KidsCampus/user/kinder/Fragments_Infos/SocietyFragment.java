package com.KidsCampus.user.kinder.Fragments_Infos;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.KidsCampus.user.kinder.Models.SocietyModels;
import com.KidsCampus.user.kinder.R;


public class SocietyFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_society, container, false);

        assert getArguments() != null;
        SocietyModels.societies society = getArguments().getParcelable("data");
        TextView name = view.findViewById(R.id.society_name_2text);
        TextView school_ds_yn = view.findViewById(R.id.society_school_ds_yntext);
        TextView school_ds_en = view.findViewById(R.id.society_school_ds_entext);
        TextView educate_ds_yn = view.findViewById(R.id.society_educate_ds_yntext);
        TextView ducate_ds_en = view.findViewById(R.id.society_educate_ds_entext);


        assert society != null;
        name.setText(society.kindername);
        school_ds_yn.setText(society.school_ds_yn);
        school_ds_en.setText(society.school_ds_en);
        educate_ds_yn.setText(society.educate_ds_yn);
        ducate_ds_en.setText(society.ducate_ds_en);

        return view;
    }
}
