package com.KidsCampus.user.kinder.Fragments_Infos;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.KidsCampus.user.kinder.Models.BuildingModels;
import com.KidsCampus.user.kinder.R;


public class BuildingFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_building, container, false);

        assert getArguments() != null;
        BuildingModels.building building = getArguments().getParcelable("data");
        TextView name = view.findViewById(R.id.building_name_2text);
        TextView archyy = view.findViewById(R.id.building_archyytext);
        TextView bld = view.findViewById(R.id.building_bldgprusareatext);
        TextView floor =  view.findViewById(R.id.building_floorcnttext);
        TextView grottar = view.findViewById(R.id.building_grottartext);

        assert building != null;
        name.setText(building.kindername);
        archyy.setText(building.archyy);
        bld.setText(building.bldgprusarea);
        floor.setText(building.floorcnt);
        grottar.setText(building.grottar);

        return view;
    }
}
