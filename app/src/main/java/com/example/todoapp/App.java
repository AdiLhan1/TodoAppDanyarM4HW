package com.example.todoapp;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;
import androidx.room.Room;

import com.example.todoapp.room.AppDatabase;

public class App extends MultiDexApplication {

    private static AppDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();
        database = Room.databaseBuilder(this,AppDatabase.class, "database")
                .allowMainThreadQueries()
                .build();

    }


    @Override
    protected void attachBaseContext(Context base) {
       super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static AppDatabase getDatabase(){
        return database;
    }
}
