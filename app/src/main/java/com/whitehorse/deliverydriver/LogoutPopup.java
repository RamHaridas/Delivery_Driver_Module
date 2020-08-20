package com.whitehorse.deliverydriver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;


public class LogoutPopup extends DialogFragment {

    TextView question;
    Button delete;
    View view;
    SharedPreferences share;
    SharedPreferences.Editor edit;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.deletepopup, container, false);

        share = this.getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
        edit = share.edit();
        question = view.findViewById(R.id.question);
        delete = view.findViewById(R.id.delete);
        String text = "Do you want to logout?";
        question.setText(text);
        delete.setText("Yes");
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                edit.putBoolean("login",false);
                edit.apply();
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });
        return view;
    }
}
