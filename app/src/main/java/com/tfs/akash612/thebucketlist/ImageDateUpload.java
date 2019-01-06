package com.tfs.akash612.thebucketlist;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class ImageDateUpload extends AppCompatActivity {

    int REQUEST_GALLERY=2;
    int REQUEST_CAMERA=3;
    private Uri filePath;
    private Button button;
    ImageView iv;
    int index;
    ArrayList<DataListObject> dataSet = new ArrayList<>();
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference("Users");

    private StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_date_upload);
        button = (Button) findViewById(R.id.btnSelectPhoto);
        iv = (ImageView) findViewById(R.id.ivImage);
        storageReference = FirebaseStorage.getInstance().getReference();

        dataSet = (ArrayList<DataListObject>) getIntent().getSerializableExtra("listName");
        index = (int) getIntent().getSerializableExtra("index");
        button.setOnClickListener(new View.OnClickListener() {
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

        int result1 = ContextCompat.checkSelfPermission(ImageDateUpload.this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        int result2 = ContextCompat.checkSelfPermission(ImageDateUpload.this, android.Manifest.permission.CAMERA);
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

                    Toast.makeText(ImageDateUpload.this,
                            "Permission accepted", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ImageDateUpload.this,
                            "Permission denied", Toast.LENGTH_LONG).show();

                }
                break;
        }
    }



    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(ImageDateUpload.this);
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
        /*Bitmap bitmap=(Bitmap)data.getExtras().get("data");
        Log.d("Image Debugging", String.valueOf(bitmap));
        //dataSet.setBucketListImage(bitmap);
        updateItem(bitmap);
        iv.setImageBitmap(bitmap);*/
        filePath = data.getData();
        Log.d("XXX: Debug File path", String.valueOf(filePath));
        //Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath);
        uploadFile();
    }

    /*public void updateItem(Bitmap image){
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Users/"+ userToken());
        String blItem = dataSet.getBucketListName();
        //dataSet.setBucketListImage(image);
        Query updateQuery = ref.orderByChild("name").equalTo(blItem);
        //Addlistener For this Query
        updateQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot updateList: dataSnapshot.getChildren()) {
                    updateList.child("dataList").getRef().setValue(dataSet);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }*/

    public String userToken() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        Log.d("User getting ID", user.getUid());
        return user.getUid();
    }

    private void uploadFile() {
        //checking if file is available
        if (filePath != null) {
            //displaying progress dialog while image is uploading
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading");
            progressDialog.show();

            //getting the storage reference
            dataSet.get(index).setBucketListUrl("gs://thebucketlist-bf5bf.appspot.com/Users/"+userToken()+"/"+dataSet.get(index).getBucketListItem());
            dataSet.get(index).getBucketListUrl();
            updateDatabase();
            StorageReference sRef = storageReference.child("Users/"+userToken()+"/"+dataSet.get(index).getBucketListItem());
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
        Query updateQuery = updateList.orderByChild("name").equalTo(dataSet.get(index).getBucketListName());
        //Addlistener For this Query
        updateQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot x : dataSnapshot.getChildren()) {
                    x.child("dataList").getRef().setValue(dataSet);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
