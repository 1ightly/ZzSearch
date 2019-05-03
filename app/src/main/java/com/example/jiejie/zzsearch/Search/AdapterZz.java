package com.example.jiejie.zzsearch.Search;

import android.content.ClipData;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jiejie.zzsearch.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AdapterZz extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private LayoutInflater inflater;
    List<dataZz> data=Collections.emptyList();
    //create constructor to initialize context and data from MainActivity
    public AdapterZz(Context context,List<dataZz> data){
        this.mContext=context;
        inflater=LayoutInflater.from(context);
        this.data=data;
    }
    //Inflate the layout when ViewHolder created
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
        View view=inflater.inflate(R.layout.container_zz,parent,false);
        return new ItemHolder(view);
    }
    //Bind data
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder,final int position){
        //Get current position of item in RecyclerView to bind data and assign values from list
        ItemHolder myHolder=(ItemHolder)viewHolder;
        dataZz current=data.get(position);
        myHolder.textTitle.setText(current.title);
        myHolder.textUrl.setText(current.clickUrl);
        myHolder.textUrl.setTextColor(ContextCompat.getColor(mContext, R.color.blue));
    }
    @Override
    //get sum of List
    public int getItemCount(){
        return data.size();
    }
    //define the view holders of items
    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView textTitle;
        private TextView textUrl;
        public ItemHolder(View itemView){
            super(itemView);
            textTitle=itemView.findViewById(R.id.textTitle);
            textUrl=itemView.findViewById(R.id.textUrl);
            itemView.setOnClickListener(this);
        }
        //click event for all items
        @Override
        public void onClick(View v){
            Toast.makeText(mContext, "You clicked an item", Toast.LENGTH_SHORT).show();
        }
    }
}
