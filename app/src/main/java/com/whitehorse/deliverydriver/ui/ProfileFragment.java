package com.whitehorse.deliverydriver.ui;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.whitehorse.deliverydriver.Dialog_Get_Image;
import com.whitehorse.deliverydriver.R;

public class ProfileFragment extends Fragment implements Dialog_Get_Image.MyDialogCloseListener,Dialog_Get_Image.onPhotoSelectedListener {
    View root;
    ImageView profile_iv;
    Uri imageuri;
    Bitmap imagebitmap;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_profile, container, false);
        profile_iv = root.findViewById(R.id.profile_iv);
        profile_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putInt("curr", 4);
                Dialog_Get_Image dgi = new Dialog_Get_Image();
                dgi.setArguments(args);
                dgi.show(getFragmentManager(), "DialogGetImage");
            }
        });
        return root;
    }

    @Override
    public void getImagePath(Uri imagePath) {
        imageuri = imagePath;
        imagebitmap = null;
    }

    @Override
    public void getImageBitmap(Bitmap bitmap) {
        imagebitmap = bitmap;
        imageuri = null;
    }

    @Override
    public void handleDialogClose(int num) {
        if (num == 4) {
            if (imagebitmap != null) {
                profile_iv.setImageDrawable(null);
                profile_iv.setImageBitmap(imagebitmap);
            } else if (imageuri != null) {
                profile_iv.setImageDrawable(null);
                profile_iv.setImageURI(imageuri);
            } else {
                Toast.makeText(getContext(), "No image was selected", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
