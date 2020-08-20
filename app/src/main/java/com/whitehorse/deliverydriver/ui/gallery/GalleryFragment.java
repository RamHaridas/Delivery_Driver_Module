package com.whitehorse.deliverydriver.ui.gallery;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.whitehorse.deliverydriver.OrderData;
import com.whitehorse.deliverydriver.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class GalleryFragment extends Fragment {
    private View root;
    CollectionReference collectionReference;
    FirebaseFirestore firebaseFirestore;
    SharedPreferences sharedPreferences;
    List<OrderData> orderDataList;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_gallery, container, false);

        orderDataList = new ArrayList<OrderData>();
        sharedPreferences = getActivity().getSharedPreferences("phone", Context.MODE_PRIVATE);
        String phone = sharedPreferences.getString("phone","");
        firebaseFirestore = FirebaseFirestore.getInstance();
        try {
            collectionReference = firebaseFirestore.collection("DRIVERS").document(phone).collection("ORDERS");
            getData();
        }catch (Exception e){
            Toast.makeText(getContext(),"ERROR",Toast.LENGTH_SHORT).show();
        }
        /*RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.recyclerView_op);
        ListAdapterOP adapter = new ListAdapterOP(myListData1);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);*/
        return root;
    }

    public void getData(){
        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                    OrderData orderData = documentSnapshot.toObject(OrderData.class);
                    if(orderData != null){
                        if(orderData.isCompleted()){
                            orderDataList.add(orderData);
                        }
                    }
                }
                RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.recyclerView_po);
                ListAdapterOP adapter = new ListAdapterOP(orderDataList);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setAdapter(adapter);
                if(orderDataList.isEmpty()){
                    TextView textView = root.findViewById(R.id.text786);
                    textView.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
