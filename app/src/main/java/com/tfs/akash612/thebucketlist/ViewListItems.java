package com.tfs.akash612.thebucketlist;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Set;

public class ViewListItems extends AppCompatActivity {

    ImageView listItemImage;
    TextView listItemName;
    TextView listItemStatus;
    TextView listItemDate;
    FloatingActionButton fab;
    ArrayList<DataListObject> receivedObject;
    int listPosition;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_list_items);
        receivedObject = (ArrayList<DataListObject>) getIntent().getSerializableExtra("listObject");
        listPosition = (int) getIntent().getSerializableExtra("index");
        final String url = (String) getIntent().getSerializableExtra("url");
        final String backgroundColor = (String) getIntent().getSerializableExtra("backgroundColor");

        listItemImage = findViewById(R.id.listItemImage);
        listItemName = findViewById(R.id.listItemName);
        listItemStatus = findViewById(R.id.listItemStatus);
        listItemDate = findViewById(R.id.listItemDate);
        fab = findViewById(R.id.fab);

        listItemName.setText(receivedObject.get(listPosition).getBucketListItem());
        listItemStatus.setText(receivedObject.get(listPosition).getBucketListStatus());
        listItemDate.setText(receivedObject.get(listPosition).getBucketListDate());
        if (url.contains(receivedObject.get(listPosition).getBucketListItem())) {
            StorageReference storage;
            storage = FirebaseStorage.getInstance().getReference();
            final StorageReference ref = storage.child(url);
            Glide.with(this).using(new FirebaseImageLoader()).load(ref).centerCrop().into(listItemImage);
        } else {
            //listItemImage.setVisibility(View.INVISIBLE);
            Bitmap bitmap = Bitmap.createBitmap(1500, 1500, Bitmap.Config.RGB_565);
            bitmap.eraseColor(Color.parseColor(backgroundColor));
            listItemImage.setImageBitmap(bitmap);
            listItemImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            //listItemImage.setColorFilter(Color.parseColor(backgroundColor));
            //listItemImage.setBackgroundColor(Color.parseColor(backgroundColor));
        }
        Log.d("XXX : Debugging URL :", backgroundColor);
        Log.d("XXX : Debugging URL :", url);

        //All the code to Edit the stuff
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewListItems.this, AddEditListItems.class);
                intent.putExtra("listObject", receivedObject);
                intent.putExtra("index", listPosition);
                intent.putExtra("url", url);
                intent.putExtra("backgroundColor", backgroundColor);
                startActivity(intent);
            }
        });

    }


    public String userToken() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        Log.d("User getting ID", user.getUid());
        return user.getUid();
    }

}
