package com.whitehorse.deliverydriver.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.whitehorse.deliverydriver.MainActivity;
import com.whitehorse.deliverydriver.R;

public class ListAdapterAO extends RecyclerView.Adapter<ListAdapterAO.ViewHolder>{
    private ListDataAO[] listdata;
    public Context context;
    View listItem;
    View view;
    ViewHolder viewHolder1;
    // RecyclerView recyclerView;
    public ListAdapterAO(ListDataAO[] listdata) {
        this.listdata = listdata;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        listItem = layoutInflater.inflate(R.layout.list_item_ao, parent, false);
        viewHolder1 = new ViewHolder(listItem);
        return viewHolder1;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ListDataAO myListData = listdata[position];
        holder.textView1.setText(listdata[position].getPickup());
        holder.textView2.setText(listdata[position].getTime());
        holder.relativeLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(),"click on item: "+myListData.getPickup(),Toast.LENGTH_LONG).show();
            }
        });
        holder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*view = v;
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                context = v.getContext();
                Fragment fragment = new MapFragment();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.drawer_layout,fragment).addToBackStack(null).commit();*/
            }
        });
    }

    @Override
    public int getItemCount() {
        return listdata.length;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView1,textView2;
        public RelativeLayout relativeLayout;
        public Button btn;
        public ViewHolder(final View itemView) {
            super(itemView);
            btn = itemView.findViewById(R.id.textView3);
            this.textView1 = itemView.findViewById(R.id.textView1);
            this.textView2 = itemView.findViewById(R.id.textView2);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.relativeLayout);
        }

    }

    public void fragmentJump(){
        Fragment map = new OrderDetailsFragment();
        Bundle  bundle = new Bundle();
        bundle.putString("key","hello");
        map.setArguments(bundle);
        switchContent(R.id.drawer_layout,map);
    }

    private void switchContent(int drawer_layout, Fragment map) {
        if(context == null){
            return;
        }
        if (context instanceof MainActivity){
            MainActivity mainActivity = (MainActivity)context;
            Fragment fragment = map;
            mainActivity.switchContent(drawer_layout,fragment);
        }
    }
}