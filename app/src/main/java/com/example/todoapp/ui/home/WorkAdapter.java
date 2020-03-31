package com.example.todoapp.ui.home;

import android.app.AlertDialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.OnItemClickListener;
import com.example.todoapp.R;
import com.example.todoapp.model.Work;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class WorkAdapter extends RecyclerView.Adapter<WorkAdapter.ViewHolder>{

    private List<Work> list;
    private OnItemClickListener onItemClickListener;

    public void setList(List<Work> list) {
        this.list = list;
    }

    public WorkAdapter(List<Work> list) {
        this.list = list;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_work,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(list.get(position));

    }

    @Override
    public int getItemCount() {

        return list.size();
    }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;

    }
    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textTitle;
        private TextView textDesc;
        private ImageView imageView;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTitle);
            textDesc = itemView.findViewById(R.id.textDesc);
            imageView = itemView.findViewById(R.id.image_view);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   onItemClickListener.itemClick(getAdapterPosition());
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onItemClickListener.itemLongClick(getAdapterPosition());
                    return true;
                }
            });

                    }

        public void bind(Work work){
            textTitle.setText(work.getTitle());
            textDesc.setText(work.getDescription());
        }
    }
}
