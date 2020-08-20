package com.whitehorse.deliverydriver.ViewProfile;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


import com.whitehorse.deliverydriver.DriverData;
import com.whitehorse.deliverydriver.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class ViewProfile extends Fragment implements DialogGetProfile.MyDialogCloseListener,DialogGetProfile.onPhotoSelectedListener {
    View view;
    ImageView profile_iv;
    DialogGetProfile dgi;
    Bitmap imagebitmap;
    Uri imageuri;
    TextInputEditText name,email,mobile;
    DocumentReference documentReference;
    FirebaseFirestore firebaseFirestore;
    SharedPreferences sharedPreferences;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, final Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.view_profile, container, false);
        sharedPreferences = this.getActivity().getSharedPreferences("phone", Context.MODE_PRIVATE);
        firebaseFirestore = FirebaseFirestore.getInstance();
        documentReference = firebaseFirestore.collection("DRIVERS").document(sharedPreferences.getString("phone",""));
        email = view.findViewById(R.id.email_address);
        name = view.findViewById(R.id.full_name);
        mobile = view.findViewById(R.id.mobile_number);
        email.setEnabled(false);
        name.setEnabled(false);
        mobile.setEnabled(false);
        profile_iv=view.findViewById(R.id.profile_image);
        profile_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*dgi=new DialogGetProfile();
                dgi.setTargetFragment(ViewProfile.this,1);
                dgi.show(getParentFragmentManager(),getString(R.string.dialog_Get_Profile));  */          }
        });
        getProfileData();
        return view;
    }
    @Override
    public void handleDialogClose() {
        if(imagebitmap!=null)
        {
            profile_iv.setImageDrawable(null);
            profile_iv.setImageBitmap(imagebitmap);
            Log.e("Image Adder", "----" +1);
        }
        else if(imageuri!=null)
        {
            profile_iv.setImageDrawable(null);
            profile_iv.setImageURI(imageuri);
            Log.e("Image Adder", "----" + 2);
        }
        else{
            Toast.makeText(getContext(),"No Image Selected",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void getImagePath(Uri imagePath) {
        imageuri=imagePath;
        imagebitmap=null;
    }
    @Override
    public void getImageBitmap(Bitmap bitmap) {
        imagebitmap=bitmap;
        imageuri=null;
    }
    public void getProfileData(){
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                DriverData userData = documentSnapshot.toObject(DriverData.class);
                if(userData == null){
                    return;
                }
                if (userData.getName() != null){
                    name.setText(userData.getName());
                }
                if(userData.getEmail() != null){
                    email.setText(userData.getEmail());
                }
                if(userData.getPhone() != null){
                    String num = userData.getPhone().substring(3,13);
                    mobile.setText(num);
                }
                if(userData.getProfile_url() != null){
                    Picasso.with(getContext())
                            .load(userData.getProfile_url())
                            .placeholder(R.drawable.profile)
                            .fit()
                            .centerCrop()
                            .into(profile_iv);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
}
