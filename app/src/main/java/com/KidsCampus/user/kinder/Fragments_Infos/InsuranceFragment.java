package com.KidsCampus.user.kinder.Fragments_Infos;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.KidsCampus.user.kinder.Models.InsuranceModels;
import com.KidsCampus.user.kinder.R;


public class InsuranceFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_insurance, container, false);

        assert getArguments() != null;
        InsuranceModels.insurance insurance = getArguments().getParcelable("data");
        assert insurance != null;

        if(insurance.insurance_nm.equals("null")) insurance.insurance_nm = "해당사항 없음";
        if(insurance.company1.equals("null")) insurance.company1 = "해당사항 없음";
        if(insurance.company2.equals("null")) insurance.company2 = "해당사항 없음";
        if(insurance.company3.equals("null")) insurance.company3 = "해당사항 없음";

        TextView name = view.findViewById(R.id.insurance_name_2text);
        TextView insurance_nm = view.findViewById(R.id.insurance_insurance_nmtext);
        TextView insurance_en = view.findViewById(R.id.insurance_insurance_entext);
        TextView insurance_yn = view.findViewById(R.id.insurance_insurance_yntext);
        TextView company1 = view.findViewById(R.id.insurance_company1text);
        TextView company2 = view.findViewById(R.id.insurance_company2text);
        TextView company3 = view.findViewById(R.id.insurance_company3text);

        name.setText(insurance.kindername);
        insurance_nm.setText(insurance.insurance_nm);
        insurance_en.setText(insurance.insurance_en);
        insurance_yn.setText(insurance.insurance_yn);
        company1.setText(insurance.company1);
        company2.setText(insurance.company2);
        company3.setText(insurance.company3);

        return view;
    }
}
