package com.whitehorse.deliverydriver.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.whitehorse.deliverydriver.R;

public class AvailableOrdersFragment extends Fragment {
    View view;
    Button button;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_available_orders, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_ao);
        ListDataAO[] myListData1 = new ListDataAO[] {
            new ListDataAO("Test1","Test2","Test3"),
            new ListDataAO("Test1","Test2","Test3"),
            new ListDataAO("Test1","Test2","Test3"),
            new ListDataAO("Test1","Test2","Test3")
        };
        ListAdapterAO adapter = new ListAdapterAO(myListData1);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        /*recyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment map = new MapFragment();
                FragmentManager manager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.drawer_layout,map)
                        .addToBackStack(null)
                        .commit();
            }
        });*/
        return view;
    }

}
