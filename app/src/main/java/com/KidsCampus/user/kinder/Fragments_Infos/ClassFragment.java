package com.KidsCampus.user.kinder.Fragments_Infos;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.KidsCampus.user.kinder.Models.ClassModels;
import com.KidsCampus.user.kinder.R;


public class ClassFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_class, container, false);

        assert getArguments() != null;
        ClassModels.classmod classmod = getArguments().getParcelable("data");
        TextView name = view.findViewById(R.id.class_name_2text);
        TextView crcnt = view.findViewById(R.id.class_crcnttext);
        TextView clsarea = view.findViewById(R.id.class_clsrareatext);
        TextView phgr = view.findViewById(R.id.class_phgrindrareatext);
        TextView hlsparea = view.findViewById(R.id.class_hlspareatext);
        TextView ktchmssparea = view.findViewById(R.id.class_ktchmsspareatext);
        TextView otarea = view.findViewById(R.id.class_otspareatext);

        assert classmod != null;
        name.setText(classmod.kindername);
        crcnt.setText(classmod.crcnt);
        clsarea.setText(classmod.clsrarea);
        phgr.setText(classmod.phgrindrarea);
        hlsparea.setText(classmod.hlsparea);
        ktchmssparea.setText(classmod.ktchmssparea);
        otarea.setText(classmod.otsparea);

        return view;
    }
}
