package com.whitehorse.deliverydriver.ui.slideshow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.whitehorse.deliverydriver.R;

public class ChangePassword extends Fragment {
    View root;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_change_password, container, false);
        return root;
    }
}
