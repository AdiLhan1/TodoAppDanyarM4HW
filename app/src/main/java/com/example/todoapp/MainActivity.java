package com.example.todoapp;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.example.todoapp.ui.OnBoard.BoardFragment;
import com.example.todoapp.ui.OnBoard.OnBoardActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.os.Environment;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity {

    private int RequestCode = 200;
    private float size;

    private AppBarConfiguration mAppBarConfiguration;
    private final int RC_WRITE_EXTERNAL = 101;
    EditText editText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean isShown = Prefs.getInstance(this).isShown();
        if (!isShown) {
            startActivity(new Intent(this, OnBoardActivity.class));
            finish();
            return;

                    }
       if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(this, PhoneActivity.class));
            finish();
            return;
        }
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        //editText.setText(size+"");
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MainActivity.this, FormActivity.class), 100);
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToOtherActivity();
            }
        });
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
//        editText.setTextSize(size);

    }

    private void goToOtherActivity() {
        Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
        startActivity(intent);
    }

    @AfterPermissionGranted(RC_WRITE_EXTERNAL)
    private void initFile(String content) {
        String permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        if (EasyPermissions.hasPermissions(this, permission)) {
            File folder = new File(Environment.getExternalStorageDirectory(), "Todo");
            folder.mkdir();
            File file = new File(folder, "note.txt");
            try {
                file.createNewFile();
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(content.getBytes());
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            EasyPermissions.requestPermissions(this, "Разреши!", RC_WRITE_EXTERNAL, permission);

        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Prefs.getInstance(this).Clear();
                break;
            case R.id.action_text_size:
                FirebaseAuth.getInstance().signOut();
                startActivityForResult(new Intent(MainActivity.this, SizeActivity.class), RequestCode);
            case R.id.action_sign_out:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Sign Out")
                        .setMessage("Do you want to sign out?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseAuth.getInstance().signOut();
                                startActivity(new  Intent(getApplicationContext(), PhoneActivity.class));
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                }

                });
                builder.create().show();


        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override()
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("TAG", "onActivityResult Main");
        for (Fragment fragment : getSupportFragmentManager().getFragments()){
            fragment.onActivityResult(requestCode, resultCode,data);
        }
        if (resultCode == RESULT_OK && requestCode == RequestCode) {
            String title = data.getStringExtra("title");
            size = data.getFloatExtra("size", 0);
        }
    }


    public void onclick(MenuItem item) {
        Prefs.getInstance(this).Clear();
        finish();
    }

    @Override
    public void onBackPressed() {
        editText = findViewById(R.id.edit_text);
        initFile(editText.getText().toString());
        super.onBackPressed();
    }
}
