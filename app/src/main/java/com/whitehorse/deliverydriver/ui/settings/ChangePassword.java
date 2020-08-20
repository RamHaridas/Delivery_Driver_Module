package com.whitehorse.deliverydriver.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;


import com.whitehorse.deliverydriver.MainActivity;
import com.whitehorse.deliverydriver.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ChangePassword extends Fragment {
    View root;
    TextInputEditText old,new_p,confirm;
    Button button;
    DocumentReference documentReference;
    FirebaseFirestore firebaseFirestore;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_change_password, container, false);
        old = root.findViewById(R.id.old_pass);
        new_p = root.findViewById(R.id.new_pass);
        confirm = root.findViewById(R.id.conf_pass);
        button = root.findViewById(R.id.change_pass);
        firebaseFirestore = FirebaseFirestore.getInstance();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword(v);
            }
        });
        return root;
    }

    public void changePassword(View v){
        try{
            documentReference = firebaseFirestore.collection("DRIVERS").document(MainActivity.static_userData.getPhone());
            String old_p = MainActivity.static_userData.getPassword();
            if(!old.getText().toString().equals(old_p)){
                old.setError("Wrong Password");
                return;
            }else if(new_p.getText().toString().isEmpty()){
                new_p.setError("Cannot be empty");
                return;
            }else if(!confirm.getText().toString().equals(new_p.getText().toString())){
                confirm.setError("Does not match");
                return;
            }

            documentReference.update("password",confirm.getText().toString().trim()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    old.getText().clear();
                    new_p.getText().clear();
                    confirm.getText().clear();
                    Toast.makeText(getContext(),"Password changed successfully",Toast.LENGTH_SHORT).show();
                }
            });
        }catch (Exception e){

        }
    }
}
