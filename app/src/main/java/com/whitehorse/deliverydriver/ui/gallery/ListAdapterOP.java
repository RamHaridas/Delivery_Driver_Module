package com.whitehorse.deliverydriver.ui.gallery;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.whitehorse.deliverydriver.OrderData;
import com.whitehorse.deliverydriver.R;

import java.util.List;


public class ListAdapterOP extends RecyclerView.Adapter<ListAdapterOP.ViewHolder>{

    List<OrderData> listdata;

    // RecyclerView recyclerView;
    public ListAdapterOP(List<OrderData> listdata) {
        this.listdata = listdata;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.list_item_op, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //final ListDataOP myListData = listdata[position];
        holder.textView1.setText(listdata.get(position).getDate());
        holder.textView2.setText(listdata.get(position).getTime());
        //holder.textView3.setText(listdata[position].getCost());
        holder.relativeLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //Toast.makeText(view.getContext(),"click on item: "+myListData.getDate(),Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
    public TextView textView1,textView2,textView3;
    public RelativeLayout relativeLayout;
    public ViewHolder(View itemView) {
        super(itemView);
        this.textView1 = itemView.findViewById(R.id.textView1);
        this.textView2 = itemView.findViewById(R.id.textView2);
        this.textView3 = itemView.findViewById(R.id.textView3);
        relativeLayout = (RelativeLayout) itemView.findViewById(R.id.relativeLayout);
    }
}
}