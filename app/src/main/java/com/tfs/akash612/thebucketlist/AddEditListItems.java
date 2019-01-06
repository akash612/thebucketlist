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

public class AddEditListItems extends AppCompatActivity {

    ImageView listItemImage;
    TextView listItemName;
    TextView listItemStatus;
    TextView listItemDate;
    int REQUEST_GALLERY=2;
    int REQUEST_CAMERA=3;
    private Uri filePath;
    ArrayList<DataListObject> receivedObject;
    int listPosition;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference("Users");
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_list_items);
        storageReference = FirebaseStorage.getInstance().getReference();
        receivedObject = (ArrayList<DataListObject>) getIntent().getSerializableExtra("listObject");
        listPosition = (int) getIntent().getSerializableExtra("index");
        String url = (String) getIntent().getSerializableExtra("url");
        String backgroundColor = (String) getIntent().getSerializableExtra("backgroundColor");

        listItemImage = findViewById(R.id.listItemImage);
        listItemName = findViewById(R.id.listItemName);
        listItemStatus = findViewById(R.id.listItemStatus);
        listItemDate = findViewById(R.id.listItemDate);

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
        //else {
            //listItemImage.setVisibility(View.INVISIBLE);
            //listItemImage.setColorFilter(Color.parseColor(backgroundColor));
            //listItemImage.setBackgroundColor(Color.parseColor(backgroundColor));
        //}
        //Log.d("XXX : Debugging URL :", backgroundColor);
        Log.d("XXX : Debugging URL :", url);

        //All the code to Edit the stuff
        listItemName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editBucketListItem(listPosition);

            }
        });

        listItemDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectDate(listPosition);

            }
        });

        listItemStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               updateStatus(listPosition);
            }
        });

        listItemImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= 23) {

                    if (checkPermission()){

                        Log.e("permission", "Permission is Already granted");

                    }else {
                        requestPermissionForGallery();
                    }
                }

                selectImage();
            }
        });
    }

    private boolean checkPermission() {

        int result1 = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        int result2 = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
        if ((result1 == PackageManager.PERMISSION_GRANTED) && (result2 == PackageManager.PERMISSION_GRANTED)) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermissionForGallery() {

        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA}, REQUEST_GALLERY);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 2:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this,
                            "Permission accepted", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this,
                            "Permission denied", Toast.LENGTH_LONG).show();

                }
                break;
        }
    }



    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                //boolean result=Utility.checkPermission(ImageDateUpload.this);
                if (items[item].equals("Take Photo")) {
                    cameraIntent();
                } else if (items[item].equals("Choose from Library")) {
                    galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }



    private void cameraIntent()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),REQUEST_GALLERY);
    }

    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        filePath = data.getData();
        Log.d("XXX: Debug File path", String.valueOf(filePath));
        //Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath);
        uploadFile();
    }

    private void uploadFile() {
        //checking if file is available
        if (filePath != null) {
            //displaying progress dialog while image is uploading
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading");
            progressDialog.show();

            //getting the storage reference
            receivedObject.get(listPosition).setBucketListUrl("gs://thebucketlist-bf5bf.appspot.com/Users/"+userToken()+"/"+receivedObject.get(listPosition).getBucketListItem());
            receivedObject.get(listPosition).getBucketListUrl();
            updateDatabase();
            StorageReference sRef = storageReference.child("Users/"+userToken()+"/"+receivedObject.get(listPosition).getBucketListItem());
            Log.d("XXX: debugging storage", "Reached here");
            //adding the file to reference
            sRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //dismissing the progress dialog
                            progressDialog.dismiss();

                            //displaying success toast
                            Toast.makeText(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();

                            //creating the upload object to store uploaded image details
                            //Upload upload = new Upload(editTextName.getText().toString().trim(), taskSnapshot.getDownloadUrl().toString());

                            //adding an upload to firebase database
                            //String uploadId = mDatabase.push().getKey();
                            //mDatabase.child(uploadId).setValue(upload);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //displaying the upload progress
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });
        } else {
            //display an error if no file is selected
        }
    }

    private void updateDatabase() {

        DatabaseReference updateList = database.getReference("Users/" + userToken());
        Query updateQuery = updateList.orderByChild("name").equalTo(receivedObject.get(listPosition).getBucketListName());
        //Addlistener For this Query
        updateQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot x : dataSnapshot.getChildren()) {
                    x.child("dataList").getRef().setValue(receivedObject);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }



    public void deleteItem(final int i) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Users/"+ userToken());
        String blItem = receivedObject.get(listPosition).getBucketListName();
        receivedObject.remove(i);
        Query updateQuery = ref.orderByChild("name").equalTo(blItem);
        //Addlistener For this Query
        updateQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot updateList: dataSnapshot.getChildren()) {
                    updateList.child("dataList").getRef().setValue(receivedObject);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void updateStatus(final int i){
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference ref = database.getReference("Users/"+ userToken());
        final String blItem = receivedObject.get(i).getBucketListName();
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int j) {
                receivedObject.get(i).setBucketListStatus("Status : Done");
                Query updateQuery = ref.orderByChild("name").equalTo(blItem);
                //Addlistener For this Query
                updateQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot updateList: dataSnapshot.getChildren()) {
                            updateList.child("dataList").getRef().setValue(receivedObject);
                        }
                        finish();
                        startActivity(getIntent());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        });
        alertDialogBuilder.setNegativeButton("Not Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int j) {
                receivedObject.get(i).setBucketListStatus("Status : Not Done");
                Query updateQuery = ref.orderByChild("name").equalTo(blItem);
                //Addlistener For this Query
                updateQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot updateList: dataSnapshot.getChildren()) {
                            updateList.child("dataList").getRef().setValue(receivedObject);
                        }
                        finish();
                        startActivity(getIntent());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        });


        alertDialogBuilder.show();
    }

    public String userToken() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        Log.d("User getting ID", user.getUid());
        return user.getUid();
    }

    public void selectDate(final int i) {
        int mYear, mMonth, mDay, mHour, mMinute;
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        String date = "Done Date : " + (monthOfYear + 1) + "-" + dayOfMonth + "-" + year;
                        receivedObject.get(i).setBucketListDate(date);
                        final FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference ref = database.getReference("Users/" + userToken());
                        String blItem = receivedObject.get(i).getBucketListName();
                        Query updateQuery = ref.orderByChild("name").equalTo(blItem);
                        //Addlistener For this Query
                        updateQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot updateList : dataSnapshot.getChildren()) {
                                    updateList.child("dataList").getRef().setValue(receivedObject);
                                }
                                finish();
                                startActivity(getIntent());
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();

    }

    public void editBucketListItem(final int i) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle("Edit Bucket List Item");
        //final BucketListObject receivedName = (BucketListObject) getIntent().getSerializableExtra("listName");
        //Setting up the input
        final EditText input = new EditText(this);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference ref = database.getReference("Users/"+ userToken());
        final String blItem = receivedObject.get(i).getBucketListName();


        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(receivedObject.get(i).getBucketListItem());
        input.setSelection(input.getText().length());
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int j) {
                //receivedObject.get(i).setBucketListName(input.getText());
                final String m_text = input.getText().toString();
                receivedObject.get(i).setBucketListItem(m_text);
                Query updateQuery = ref.orderByChild("name").equalTo(blItem);
                //Addlistener For this Query
                updateQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot updateList: dataSnapshot.getChildren()) {
                            updateList.child("dataList").getRef().setValue(receivedObject);
                        }
                        finish();
                        startActivity(getIntent());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int j) {
            }
        });
        builder.show();
    }
}
