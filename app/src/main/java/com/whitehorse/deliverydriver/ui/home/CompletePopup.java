package com.whitehorse.deliverydriver.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.whitehorse.deliverydriver.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


public class CompletePopup extends DialogFragment {

    TextView question;
    Button delete;
    View view;
    Fragment fragment;

    public CompletePopup(Fragment fragment){
        this.fragment = fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.deletepopup, container, false);



        question = view.findViewById(R.id.question);
        delete = view.findViewById(R.id.delete);
        String text = "Are you sure?";
        question.setText(text);
        delete.setText("Yes");
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        Bundle args = getArguments();
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = args.getString("id");
                String number = args.getString("number");
                try{
                    DocumentReference documentReference = firebaseFirestore.collection("USERS")
                            .document(number).collection("ORDER").document(name);
                    documentReference.update("completed",true);
                    NavController navController = NavHostFragment.findNavController(fragment);
                    try{
                        navController.navigate(R.id.action_map_to_home);
                    }catch (Exception e){}
                }catch (Exception e){
                    Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }
}
