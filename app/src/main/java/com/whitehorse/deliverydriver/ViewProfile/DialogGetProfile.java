package com.whitehorse.deliverydriver.ViewProfile;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;


import com.whitehorse.deliverydriver.R;

public class DialogGetProfile extends DialogFragment {
    public interface onPhotoSelectedListener{
        void getImagePath(Uri imagePath);
        void getImageBitmap(Bitmap bitmap);

        void handleDialogClose();
    }
    public interface MyDialogCloseListener
    {
        public void handleDialogClose();//or whatever args you want
    }
    onPhotoSelectedListener psl;
    View root;
    private static final int PICK_REQUEST_CODE=123,TAKE_REQUEST_CODE=321;
    private static final String TAG="Dialog_select_profile";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root=inflater.inflate(R.layout.dialog_get_profile,container,false);
        TextView selectPhoto=root.findViewById(R.id.choose_gallery);
        TextView takePhoto=root.findViewById(R.id.open_camera);
        Bundle marrs=getArguments();
        selectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,PICK_REQUEST_CODE);
            }
        });
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,TAKE_REQUEST_CODE);
            }
        });
        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_REQUEST_CODE && resultCode== Activity.RESULT_OK){
            Uri selectedImageUri=data.getData();
            psl.getImagePath(selectedImageUri);
            getDialog().dismiss();
        }else if(requestCode==TAKE_REQUEST_CODE && resultCode==Activity.RESULT_OK){
            Bitmap bitmap;
            bitmap=(Bitmap)data.getExtras().get("data");
            psl.getImageBitmap(bitmap);
            getDialog().dismiss();
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            psl=(onPhotoSelectedListener)getTargetFragment();
        }catch (ClassCastException e){
            Log.e(TAG,"onAttach--------"+e.getMessage());
        }
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        Fragment f1=getTargetFragment();
        if(f1 instanceof MyDialogCloseListener)
            ((MyDialogCloseListener)f1).handleDialogClose();
    }
}
