package com.whitehorse.deliverydriver;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

public class InfoFragment extends Fragment {

    ImageView gmail,website,logo;
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_info, container, false);

        gmail = view.findViewById(R.id.gmail);
        website = view.findViewById(R.id.website);
        logo = view.findViewById(R.id.logo);
        try {
            Glide.with(getContext())
                    .load("https://firebasestorage.googleapis.com/v0/b/token-app-c3c95.appspot.com/o/logo.jpg?alt=media&token=185b8a4d-efd7-4102-9949-667f537da0a3")
                    .into(logo);
        }catch (Exception e){
            Glide.with(getContext())
                    .load(R.drawable.logo)
                    .into(logo);
        }
        gmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{"business@mrwhitehorse.com"});
                //i.putExtra(Intent.EXTRA_SUBJECT, "subject of email");
                //i.putExtra(Intent.EXTRA_TEXT   , "body of email");
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getActivity(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        website.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://mrwhitehorse.com"));
                try {
                    startActivity(browserIntent);
                }catch (Exception e){
                    Toast.makeText(getContext(),"ERROR",Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }
}