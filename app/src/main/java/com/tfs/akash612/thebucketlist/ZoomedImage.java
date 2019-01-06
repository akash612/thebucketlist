package com.tfs.akash612.thebucketlist;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ZoomedImage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoomed_image);


        String url = (String) getIntent().getSerializableExtra("imageRef");
        StorageReference storage;
        storage = FirebaseStorage.getInstance().getReference();
        final StorageReference ref = storage.child(url);
        ImageView myImage = findViewById(R.id.myImage);
        Glide.with(this).using(new FirebaseImageLoader()).load(ref).into(myImage);
    }
}
