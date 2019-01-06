package com.tfs.akash612.thebucketlist;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.tfs.akash612.thebucketlist.DataListObject;
import com.tfs.akash612.thebucketlist.ImageDateUpload;
import com.tfs.akash612.thebucketlist.R;
import com.tfs.akash612.thebucketlist.ZoomedImage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    private ArrayList<DataListObject> dataSet;
    private Context mContext;
    String[] colors = {"#42e5f4", "#a142f4", "#f45c42", "#426ef4", "#f4e542", "#53f4c4", "#53f491", "#53f458", "#86f453", "#b9f453", "#f44268"};


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textViewItem;
        TextView textViewStatus;
        CardView bucketListCards;
        ImageView bucketListImage;
        TextView bucketListDate;
        RelativeLayout relativeLayout;


        public MyViewHolder(View itemView) {
            super(itemView);
            this.bucketListCards = (CardView) itemView.findViewById(R.id.bucketListCards);
            this.textViewItem = (TextView) itemView.findViewById(R.id.dataListItem);
            this.textViewStatus = (TextView) itemView.findViewById(R.id.dataListStatus);
            this.bucketListImage = (ImageView) itemView.findViewById(R.id.imageCard);
            this.bucketListDate = (TextView) itemView.findViewById(R.id.dataListDate);
            this.relativeLayout = (RelativeLayout) itemView.findViewById(R.id.relativeLayout);
        }
    }

    public CustomAdapter(Context mContext, ArrayList<DataListObject> data) {
        this.dataSet = data;
        this.mContext = mContext;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_data_list, parent, false);

        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {

        CardView bucketListCards = holder.bucketListCards;
        TextView textViewItem = holder.textViewItem;
        TextView textViewStatus = holder.textViewStatus;
        ImageView bucketListImage = holder.bucketListImage;
        TextView bucketListDate = holder.bucketListDate;
        RelativeLayout relativeLayout = holder.relativeLayout;
        Log.d("XXXXXXXXXX : : Debugging Recycler View", String.valueOf(textViewItem));
        textViewItem.setText(dataSet.get(listPosition).getBucketListItem());
        textViewStatus.setText(dataSet.get(listPosition).getBucketListStatus());
        //Check if Image is Set or else set it to invisible
        StorageReference storage;
        storage = FirebaseStorage.getInstance().getReference();

        String url = dataSet.get(listPosition).getBucketListUrl();

        if (url.equals("Null")){
            Log.d("XXX: url is not set : ", url);
        }
        else {
            url = "Users/"+userToken()+"/"+dataSet.get(listPosition).getBucketListItem();
        }
        final String urlSent = url;
        final StorageReference ref = storage.child(url);

        try {
            if (dataSet.get(listPosition).getBucketListUrl().contains(dataSet.get(listPosition).getBucketListItem())) {
                bucketListImage.setVisibility(View.VISIBLE);
                Glide.with(mContext).using(new FirebaseImageLoader()).load(ref).into(bucketListImage);
            }
            else {
                setColor(listPosition, holder);
            }
        } catch (Exception e) {
            bucketListImage.setVisibility(View.GONE);
        }
        //Check if Date is Set or set view to invisible
        try {
            if (dataSet.get(listPosition).getBucketListDate() != null){
                bucketListDate.setVisibility(View.VISIBLE);
            }
            bucketListDate.setText(dataSet.get(listPosition).getBucketListDate());
        } catch (Exception e) {
            bucketListDate.setVisibility(View.GONE);
        }

        //Log.d("XXXXXXXXXX : : Debugging Pop Up Menu", String.valueOf(holder.buttonViewOption));
        // On click listener for ImageView
        /*bucketListImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ZoomedImage.class);
                intent.putExtra("imageRef", url);
                mContext.startActivity(intent);
            }
        });*/

        bucketListCards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ViewListItems.class);
                intent.putExtra("listObject", dataSet);
                intent.putExtra("index", listPosition);
                intent.putExtra("url", urlSent);
                intent.putExtra("backgroundColor", colors[listPosition]);
                mContext.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public void setColor(int i, MyViewHolder holder){
        String[] colors = {"#42e5f4", "#a142f4", "#f45c42", "#426ef4", "#f4e542", "#53f4c4", "#53f491", "#53f458", "#86f453", "#b9f453", "#f44268"};
        int x = i % colors.length;
        holder.relativeLayout.setBackgroundColor(Color.parseColor(colors[x]));
    }

    public void deleteItem(final int i) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Users/"+ userToken());
        String blItem = dataSet.get(i).getBucketListName();
        dataSet.remove(i);
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
        notifyDataSetChanged();
    }

    public void updateItem(int i){
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Users/"+ userToken());
        String blItem = dataSet.get(i).getBucketListName();
        dataSet.get(i).setBucketListStatus("Status : Done");
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
        notifyDataSetChanged();
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
        DatePickerDialog datePickerDialog = new DatePickerDialog(mContext,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        String date = "Done Date : "+(monthOfYear + 1)+"-"+dayOfMonth+"-"+year;
                        dataSet.get(i).setBucketListDate(date);
                        final FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference ref = database.getReference("Users/"+ userToken());
                        String blItem = dataSet.get(i).getBucketListName();
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
                        notifyDataSetChanged();
                    }
                },mYear, mMonth, mDay);
        datePickerDialog.show();
    }
}

