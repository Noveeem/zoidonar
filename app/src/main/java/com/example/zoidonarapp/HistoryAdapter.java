package com.example.zoidonarapp;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder>{

    Context context;
    ArrayList<modelHistory> list;

    public HistoryAdapter(Context context, ArrayList<modelHistory> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.donorhistorylist,parent, false);
        return new HistoryAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        modelHistory modelHistory  = list.get(position);
        holder.resultDate.setText(modelHistory.getDatee());;
        holder.resultVol.setText(modelHistory.getVolume() + " mL");;
        holder.resultBBO.setText(modelHistory.getRemarks());;
        holder.resultRems.setText(modelHistory.getStatus());;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView resultDate, resultVol, resultBBO, resultRems;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            resultDate = itemView.findViewById(R.id.resultDate);
            resultVol = itemView.findViewById(R.id.resultVol);
            resultBBO = itemView.findViewById(R.id.resultBBO);
            resultRems = itemView.findViewById(R.id.resultRems);
        }
    }
}
