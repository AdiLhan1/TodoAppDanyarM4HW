package com.example.todoapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ProfileActivity extends AppCompatActivity {

    ImageView imageView;
    EditText editText;
    private ImageView mImage;
    private Uri mImageUri;
    SharedPreferences sharedPreferences;
    public static String myProfile = "MyProfile";
    FirebaseStorage firebaseStorage;
    Button sendToFirebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mImage = findViewById(R.id.imageView);
        editText = findViewById(R.id.edit_profile);
        sendToFirebase = findViewById(R.id.SendToFirebase);
        sendToFirebase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        uploadImage();
            }
        });

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String image = preferences.getString("image", "");
        mImage.setImageURI(Uri.parse(image));


    }
    private void uploadImage() {

        StorageReference reference = FirebaseStorage.getInstance()
                .getReference().child("avatars/image1.jpg");
        UploadTask task = reference.putFile(mImageUri);
        task.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ProfileActivity.this, "ok", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProfileActivity.this, "false", Toast.LENGTH_SHORT).show();                }
            }
        });
    }

    public void onClick(View view) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 2);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && requestCode == 2 && data != null) {
            mImageUri = data.getData();
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("image", String.valueOf(mImageUri));
            editor.apply();
            mImage.setImageURI(mImageUri);
            mImage.invalidate();

        }
        super.onActivityResult(requestCode, resultCode, data);
    }



}

