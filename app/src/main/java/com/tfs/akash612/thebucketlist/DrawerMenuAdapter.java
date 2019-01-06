package com.tfs.akash612.thebucketlist;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.tfs.akash612.thebucketlist.DataListObject;
import com.tfs.akash612.thebucketlist.DrawerMenuObject;
import com.tfs.akash612.thebucketlist.ImageDateUpload;
import com.tfs.akash612.thebucketlist.R;
import com.tfs.akash612.thebucketlist.ZoomedImage;

import java.util.ArrayList;
import java.util.Set;

public class DrawerMenuAdapter extends RecyclerView.Adapter<DrawerMenuAdapter.MyViewHolder> {

    private Context mContext;
    public ArrayList<DrawerMenuObject> menuList;
    private FirebaseAuth mAuth;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView menuItem;
        ImageView menuImage;


        public MyViewHolder(View itemView) {
            super(itemView);
            this.menuItem = (TextView) itemView.findViewById(R.id.menuItem);
            this.menuImage = (ImageView) itemView.findViewById(R.id.menuImage);
        }
    }

    public DrawerMenuAdapter(Context mContext, ArrayList<DrawerMenuObject> data) {
        this.menuList = data;
        this.mContext = mContext;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.drawer_menu_list, parent, false);

        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {

        TextView menuItem = holder.menuItem;
        ImageView menuImage =holder.menuImage;
        menuItem.setText(menuList.get(listPosition).getMenuItem());
        menuImage.setImageResource(menuList.get(listPosition).getMenuImage());
        Log.d("XXX : Debugging the adapter", menuList.get(listPosition).getMenuItem());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch(listPosition) {

                    case 0:
                        Intent intent = new Intent(mContext, AddEditSlates.class);
                        mContext.startActivity(intent);
                        break;

                    case 1:
                        areYouSure();
                        break;

                    case 2:
                        about();
                        break;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return menuList.size();
    }

    public void about(){

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("About");
        builder.setIcon(R.drawable.ic_info_outline_black_24dp);
        builder.setMessage("The Log Book is an app which assists you to live your dreams.");
        builder.show();
    }

    public void areYouSure() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(mContext);
        builder.setTitle("Are you Sure?");

        //Setting up the input
        final EditText input = new EditText(mContext);

        //Setting up buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //listItems.add(m_Text);
                //adapter.notifyDataSetChanged();
                signOut();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }
        );
        builder.show();
    }

    private void signOut() {
        mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
        Intent i = mContext.getPackageManager()
                .getLaunchIntentForPackage( mContext.getPackageName() );
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mContext.startActivity(i);
    }

}

