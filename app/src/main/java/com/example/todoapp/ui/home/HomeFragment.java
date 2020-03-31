package com.example.todoapp.ui.home;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.menu.MenuView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.App;
import com.example.todoapp.FormActivity;
import com.example.todoapp.OnItemClickListener;
import com.example.todoapp.R;
import com.example.todoapp.model.Work;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private WorkAdapter adapter;
    private ArrayList<Work> list;
    Button btnSubmit;


    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        btnSubmit=view.findViewById(R.id.btnSubmit);
        return view;


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        list = new ArrayList<>();
        adapter = new WorkAdapter(list);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void itemClick(int position) {
                Work work = list.get(position);
                Intent intent = new Intent(getContext(), FormActivity.class);
                intent.putExtra("work", work);
                startActivity(intent);


            }

            @Override
            public void itemLongClick(final int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle("Do you wat to delete?")
                        .setMessage("Delete")
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        App.getDatabase().workDao().delete(list.get(position));
                    }
                }).show();


            }


        });
        recyclerView.setAdapter(adapter);
        App.getDatabase().workDao().getAll().observe(this, new Observer<List<Work>>() {
            @Override
            public void onChanged(List<Work> works) {
                list.clear();
                list.addAll(works);
                adapter.notifyDataSetChanged();


            }
        });

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void itemClick(int position) {
                Work work = list.get(position);
                Intent intent = new Intent(getContext(), FormActivity.class);
                intent.putExtra("work", work);
                startActivity(intent);
            }


            @Override
            public void itemLongClick(final int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle("Do you want to delete?")
                        .setMessage("Delete Message")
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        App.getDatabase().workDao().delete(list.get(position));
                    }
                }).show();

            }

        });

        return;


    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Activity.RESULT_OK && requestCode == 100) {
            Work work = (Work) data.getSerializableExtra("work");
            list.add(work);
            adapter.notifyDataSetChanged();
        }
    }
}