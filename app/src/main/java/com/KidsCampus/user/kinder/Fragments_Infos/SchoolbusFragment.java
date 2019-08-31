package com.KidsCampus.user.kinder.Fragments_Infos;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.KidsCampus.user.kinder.Models.SchoolbusModels;
import com.KidsCampus.user.kinder.R;


public class SchoolbusFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schoolbus, container, false);

        assert getArguments() != null;
        SchoolbusModels.schoolbus schoolbus = getArguments().getParcelable("data");
        TextView name = view.findViewById(R.id.schoolbus_name_2text);
        TextView vhcl_oprn_yn = view.findViewById(R.id.schoolbus_vhcl_oprn_yntext);
        TextView opra_vhcnt = view.findViewById(R.id.schoolbus_opra_vhcnttext);
        TextView dclr_vhcnt = view.findViewById(R.id.schoolbus_dclr_vhcnttext);
        TextView psg9_dclr_vhcnt = view.findViewById(R.id.schoolbus_psg9_dclr_vhcnttext);
        TextView psg12_dclr_vhcnt = view.findViewById(R.id.schoolbus_psg12_dclr_vhcnttext);
        TextView psg15_dclr_vhcnt = view.findViewById(R.id.schoolbus_psg15_dclr_vhcnttext);

        assert schoolbus != null;
        name.setText(schoolbus.kindername);
        vhcl_oprn_yn.setText(schoolbus.vhcl_oprn_yn);
        opra_vhcnt.setText(schoolbus.opra_vhcnt.concat("대"));
        dclr_vhcnt.setText(schoolbus.dclr_vhcnt.concat("대"));
        psg9_dclr_vhcnt.setText(schoolbus.psg9_dclr_vhcnt.concat("대"));
        psg12_dclr_vhcnt.setText(schoolbus.psg12_dclr_vhcnt.concat("대"));
        psg15_dclr_vhcnt.setText(schoolbus.psg15_dclr_vhcnt.concat("대"));

        return view;
    }
}
