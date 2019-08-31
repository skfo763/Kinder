package com.KidsCampus.user.kinder.Fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.KidsCampus.user.kinder.R;

public class item_image_fragment extends Fragment {
    public static item_image_fragment newInstance(String uri) {
        Bundle args = new Bundle();
        args.putString("uri", uri);
        item_image_fragment fragment = new item_image_fragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_image_fragment, container, false);
        String uri_show = getArguments().get("uri").toString();
        Uri final_uri = Uri.parse(uri_show);
        ImageView imageView = view.findViewById(R.id.item_image_fragment_imageview);
        Glide.with(this).load(final_uri).into(imageView);
        return view;
    }
}
