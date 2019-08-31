package com.KidsCampus.user.kinder.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.KidsCampus.user.kinder.KInderActivity;
import com.KidsCampus.user.kinder.Location.LocationActivity;
import com.KidsCampus.user.kinder.MainActivity;
import com.KidsCampus.user.kinder.R;


public class KinderFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_kinder, container, false);
        RelativeLayout goSearch = view.findViewById(R.id.kinder_frag_search);
        RelativeLayout goLocation = view.findViewById(R.id.kinder_frag_location);
        ((MainActivity)MainActivity.mContext).titletext.setText("찾아보기");

        goSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(inflater.getContext(), KInderActivity.class);
                intent.putExtra("sido", "서울특별시");
                startActivity(intent);
            }
        });

        goLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), LocationActivity.class);
                startActivity(intent);
            }
        });

        return view;

    }
}
