package com.whitehorse.deliverydriver.ui.home;

import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.whitehorse.deliverydriver.OrderData;
import com.whitehorse.deliverydriver.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class OrderDetailsFragment extends Fragment{
    double lat_a,long_a;
    Location currLoc;
    FusedLocationProviderClient fusedLocationProviderClient;
    View view;
    CollectionReference collectionReference;
    Button accept,reject;
    ImageView imageView1,imageView2,imageView3,imageView4;
    EditText descrition,weight,dimen,rec_name,rec_no,rec_address;
    TextView date,time,orderstatus;
    FirebaseFirestore firebaseFirestore;
    DocumentReference documentReference;
    Bundle args;
    SharedPreferences sharedPreferences;
    String phone_no;
    ImageView img1,img2,img3,img4;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_order_details, container, false);
        sharedPreferences = this.getActivity().getSharedPreferences("phone", MODE_PRIVATE);
        phone_no = sharedPreferences.getString("phone","");
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(view.getContext());
        firebaseFirestore = FirebaseFirestore.getInstance();
        documentReference = firebaseFirestore.collection("DRIVERS").document(phone_no);
        args = getArguments();

        descrition = view.findViewById(R.id.description_et);
        weight = view.findViewById(R.id.weight_et);
        dimen = view.findViewById(R.id.dimen_et);
        rec_name = view.findViewById(R.id.rname_et);
        rec_no = view.findViewById(R.id.rmobile_et);
        rec_address = view.findViewById(R.id.raddress);
        date = view.findViewById(R.id.date_tv);
        time = view.findViewById(R.id.time_tv);
        accept = view.findViewById(R.id.accept);
        reject = view.findViewById(R.id.reject);
        img1 = view.findViewById(R.id.img1);


        descrition.setEnabled(false);
        weight.setEnabled(false);
        dimen.setEnabled(false);
        rec_address.setEnabled(false);
        rec_no.setEnabled(false);
        rec_name.setEnabled(false);
        getOrderDetails();
        reject.setOnClickListener(v -> getActivity().getSupportFragmentManager().popBackStack());
        accept.setOnClickListener(this::accept);
        if(!accept.isEnabled()){
            //change background so that user can understand
            accept.setBackgroundResource(R.drawable.bg_grey_button);
        }
        accept.setVisibility(View.INVISIBLE);
        check_orders();
        orderstatus=view.findViewById(R.id.orderstatus);
        return view;
    }

    private void getOrderDetails(){

        String name = " ";
        name = args.getString("id");
        if(name == null){
            return;
        }
        try {
            documentReference.collection("ORDERS").document(name).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        OrderData orderData = documentSnapshot.toObject(OrderData.class);
                        if(orderData != null && orderData.getDesc() != null && orderData.getWeight() != null
                            && orderData.getDimen() != null && orderData.getRec_address() != null &&
                            orderData.getMobile_number() != null && orderData.getDate() != null
                            && orderData.getTime() != null) {
                            accept.setVisibility(View.VISIBLE);
                            descrition.setText(orderData.getDesc());
                            weight.setText(orderData.getWeight());
                            dimen.setText(orderData.getDimen());
                            rec_address.setText(orderData.getRec_address());
                            rec_no.setText(orderData.getMobile_number());
                            rec_name.setText(orderData.getRec_name());
                            date.setText(orderData.getDate());
                            time.setText(orderData.getTime());
                            try {
                                Glide.with(getContext())
                                        .load(orderData.getUrl1())
                                        .centerCrop()
                                        .into(imageView1);
                            }catch (Exception ex){}
                            String txt = orderData.getUser()+orderData.getTime();
                        }
                    });
        }catch (Exception w){
            Toast.makeText(getContext(),"Sorry the order was accepted by other driver",Toast.LENGTH_SHORT).show();
            accept.setEnabled(false);
            accept.setVisibility(View.INVISIBLE);
            reject.setEnabled(false);
        }
    }
    public void accept(View view){
        String number = args.getString("number","");
        String name = args.getString("id");
        NavController navController = NavHostFragment.findNavController(this);
        Bundle arg = new Bundle();
        arg.putString("number",number);
        arg.putString("id",name);
        try {
            DocumentReference dref = firebaseFirestore.collection("USERS").document(number);
            Map<String,Object> map = new HashMap<>();
            map.put("assigned",true);
            dref.collection("ORDER").document(name).set(map, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> navController.navigate(R.id.action_details_to_map,arg));
        }catch (Exception e){
            Toast.makeText(getContext(),"Error: "+e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }


    public void check_orders(){

        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("phone",MODE_PRIVATE);
        String phone_no = sharedPreferences.getString("phone","");
        collectionReference = firebaseFirestore.collection("DRIVERS")
                .document(phone_no).collection("ORDERS");
        collectionReference.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if(e != null){
                return;
            }
            for(DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()){
                switch (dc.getType()){
                    case ADDED:

                    case MODIFIED:

                    case REMOVED:
                        accept.setVisibility(View.GONE);
                        orderstatus.setVisibility(View.VISIBLE);
                        reject.setText("Close");
                }
            }
        });
    }
}
