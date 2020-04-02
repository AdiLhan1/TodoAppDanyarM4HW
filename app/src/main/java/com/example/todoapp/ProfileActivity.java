package com.example.todoapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    private ImageView mImage;
    private Uri mImageUri;
    EditText editText;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference;

    SharedPreferences preferences;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    DocumentReference documentReference = firebaseFirestore.document("users/My profile");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mImage = findViewById(R.id.imageView);
        editText = findViewById(R.id.edit_profile);
        saveInFirestore();
        storageReference = storage.getReference();

        preferences = getSharedPreferences("pref", MODE_PRIVATE);
        String image = preferences.getString("pref", "");
        Glide.with(this).load(image).into(mImage);

    }

    private void saveInFirestore() {
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String nameDoc = documentSnapshot.getString("text");

                    editText.setText(nameDoc);
                } else {
                    Toast.makeText(ProfileActivity.this, "not exists", Toast.LENGTH_SHORT).show();
                }
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
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 2 && data != null) {
            mImageUri = data.getData();
            preferences.edit().putString("pref", String.valueOf(mImageUri)).apply();
            Glide.with(this).load(mImageUri).into(mImage);

            StorageReference sRef = storageReference.child("Photos").child(mImageUri.getLastPathSegment());
            sRef.putFile(mImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(ProfileActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ProfileActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void onSave(View view) {
        String text = editText.getText().toString();
        Map<String, Object> note = new HashMap<>();
        note.put("text", text);
        documentReference.set(note)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ProfileActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ProfileActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}

