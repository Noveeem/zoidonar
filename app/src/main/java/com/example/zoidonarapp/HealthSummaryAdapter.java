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

public class HealthSummaryAdapter extends RecyclerView.Adapter<HealthSummaryAdapter.MyViewHolder> {
    Context context;
    ArrayList<modelQuestion> list;

    public HealthSummaryAdapter(Context context, ArrayList<modelQuestion> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.healthsummarylist,parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        modelQuestion modelQuestion  = list.get(position);
        holder.healthQuestion.setText(modelQuestion.getQuestion());;
        holder.healthAnswer.setText("Response: " + modelQuestion.getAnswer());
        switch (modelQuestion.getAnswer())
        {
            case "NO":
                holder.healthAnswer.setTextColor(Color.BLACK);
                break;
            case "YES":
                holder.healthAnswer.setTextColor(Color.RED);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView healthQuestion, healthAnswer;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            healthQuestion = itemView.findViewById(R.id.txtDateEvent);
            healthAnswer = itemView.findViewById(R.id.txtAnswer);
        }
    }
}
