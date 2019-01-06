package com.tfs.akash612.thebucketlist;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class AddEditSlatesAdapter extends RecyclerView.Adapter<AddEditSlatesAdapter.MyViewHolder> {
    private ArrayList<BucketListObject> dataSet;
    private Context mContext;
    String m_Text = "";

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView bucketList;
        TextView percentComplete;
        CardView bucketListCards;
        RelativeLayout layout;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.bucketListCards = (CardView) itemView.findViewById(R.id.bucketListCards);
            this.bucketList = (TextView) itemView.findViewById(R.id.bucketList);
            this.percentComplete = (TextView) itemView.findViewById(R.id.percentComplete);
            this.layout = (RelativeLayout) itemView.findViewById(R.id.layout);
        }
    }

    public AddEditSlatesAdapter(Context mContext, ArrayList<BucketListObject> data) {
        this.dataSet = data;
        this.mContext = mContext;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                             final int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.main_card_view, parent, false);



        MyViewHolder myViewHolder = new MyViewHolder(view);
        //Set OnClick listener for the CardView

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {

        CardView bucketListCards = holder.bucketListCards;
        final TextView bucketList = holder.bucketList;
        TextView percentComplete = holder.percentComplete;
        //setColor(listPosition, holder);
        bucketList.setText(dataSet.get(listPosition).getName());
        percentComplete.setText(dataSet.get(listPosition).getPercent());
        //Log.d("XXXXXXXXXX : : Debugging Pop Up Menu", String.valueOf(holder.buttonViewOption));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //will show popup menu here
                PopupMenu popup = new PopupMenu(mContext, holder.itemView);
                //inflating menu from xml resource
                popup.inflate(R.menu.main_options_menu);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.update:
                                //handle menu2 click
                                //deleteItem(listPosition);
                                updateItem(dataSet.get(listPosition).getName(), listPosition);
                                break;
                        }
                        return false;
                    }
                });
                //displaying the popup
                popup.show();

            }
        });

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public void setColor(int i, MyViewHolder holder){
        String[] colors = {"#53f4c4", "#53f491", "#53f458", "#86f453", "#b9f453"};
        int x = i % colors.length;
        holder.layout.setBackgroundColor(Color.parseColor(colors[x]));
    }

    public String userToken() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        Log.d("User getting ID", user.getUid());
        return user.getUid();
    }

    public void updateItem(final String blItem, final int listPosition) {

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        AlertDialog.Builder updateDialog = new AlertDialog.Builder(mContext);
        updateDialog.setTitle("Update Bucket List Item");
        //Setting up the input
        final EditText input = new EditText(mContext);

        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(blItem);

        input.setSelection(input.getText().length());
        updateDialog.setView(input);

        //Setting up buttons
        updateDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatabaseReference updateList = database.getReference("Users/" + userToken());
                Query updateQuery = updateList.orderByChild("name").equalTo(blItem);

                //Addlistener For this Query
                updateQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot updateList: dataSnapshot.getChildren()) {
                            updateList.getRef().removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }});
        updateDialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatabaseReference updateList = database.getReference("Users/" + userToken());
                Query updateQuery = updateList.orderByChild("name").equalTo(blItem);

                //Addlistener For this Query
                updateQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        m_Text = input.getText().toString();
                        for (DataSnapshot updateList: dataSnapshot.getChildren()) {
                            dataSet.get(listPosition).setName(m_Text);
                            for (DataListObject i : dataSet.get(listPosition).getDataList()){
                                i.setBucketListName(m_Text);
                            }
                            //BucketListObject bucketListObject = new BucketListObject(m_Text);
                            updateList.getRef().setValue(dataSet.get(listPosition));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        });
        updateDialog.show();
    }
}
