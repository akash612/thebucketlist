package com.tfs.akash612.thebucketlist;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.LayoutManager drawerLayoutManager;
    RecyclerView.LayoutManager bucketListLayoutManager;
    public BucketListObject receivedName;
    private String m_Text = "";
    RecyclerView recyclerView;
    RecyclerView drawerRecyclerView;
    RecyclerView bucketListRecyclerView;
    private FirebaseAuth mAuth;
    MainCustomAdapter adapter;
    DrawerMenuAdapter drawerAdapter;
    CustomAdapter bucketListAdapter;
    ArrayList<BucketListObject> bucketList = new ArrayList<BucketListObject>();
    ArrayList<DrawerMenuObject> menuObject = new ArrayList<DrawerMenuObject>();
    ArrayList<DataListObject> dataList = new ArrayList<DataListObject>();
    ArrayList<DataListObject> dataListHolder = new ArrayList<DataListObject>();

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference("Users");
    private DrawerLayout mDrawerLayout;
    TextView profileId;
    TextView profileName;
    Uri profilePic;
    FloatingActionButton floatingActionButton;
    CircularImageView profilePicView;
    List<String> menuItemList = Arrays.asList("Add/Edit Slate", "Logout", "About");
    List<Integer> menuImageList = Arrays.asList(R.drawable.ic_add_black_24dp, R.drawable.ic_exit_to_app_black_24dp, R.drawable.ic_info_outline_black_24dp);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_drawer);

        // Setting the toolbar for the activity
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_layout);
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        setSupportActionBar(toolbar);
        final ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);


        //Get Handle for drawer layout
        mDrawerLayout = findViewById(R.id.drawer_layout);


        //Set the navigation header attributes
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.findViewById(R.id.nav_header);
        profileId = (TextView) headerView.findViewById(R.id.profileId);
        profileName = (TextView) headerView.findViewById(R.id.profileName);
        profilePicView = (CircularImageView) headerView.findViewById(R.id.profilePic);

        //Get logged in user information and populate drawer header
        mAuth = FirebaseAuth.getInstance();
        String email = mAuth.getCurrentUser().getEmail();
        profilePic = mAuth.getCurrentUser().getPhotoUrl();
        String name = mAuth.getCurrentUser().getDisplayName();
        Glide.with(this).load(profilePic).into(profilePicView);
        profileId.setText(email);
        profileName.setText(name);

        try {
            receivedName = (BucketListObject) getIntent().getSerializableExtra("listName");
            receivedName.getPercent();
        }
        catch (NullPointerException e) {
            Log.d("Trying Something..","Yo");
        }

        // There are 3 recycler adapter views in this activity. Setting the attributes for
        // each one below

        //Setting the adapter attributes for the drawer menu recycler view
        drawerAdapter = new DrawerMenuAdapter(this, menuObject);
        drawerRecyclerView = (RecyclerView) navigationView.findViewById(R.id.optionsList);
        drawerLayoutManager = new LinearLayoutManager(this);
        drawerRecyclerView.setLayoutManager(drawerLayoutManager);
        drawerRecyclerView.setAdapter(drawerAdapter);

        //Setting the adapter attributes for the slate list
        adapter = new MainCustomAdapter(this, bucketList);
        recyclerView = (RecyclerView) navigationView.findViewById(R.id.cardView);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);

        //Setting the adapter attributes for the item list
        bucketListAdapter = new CustomAdapter(this,dataList);
        bucketListRecyclerView = (RecyclerView) findViewById(R.id.cardViewBucketList);
        bucketListLayoutManager = new LinearLayoutManager(this);
        bucketListRecyclerView.setLayoutManager(bucketListLayoutManager);
        bucketListRecyclerView.setAdapter(bucketListAdapter);


        // Fill the navigation List Adapter List with appropriate values

        // Populate the menu list for the navigation header drawer
        for ( int i = 0; i < menuImageList.size(); i++){
            menuObject.add(new DrawerMenuObject(menuItemList.get(i), menuImageList.get(i)));
            drawerAdapter.notifyDataSetChanged();
        }

        final DatabaseReference usersRef = ref.child(userToken());
        usersRef.keepSynced(true);
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final ActionBar toolbarFinal = actionbar;
                try {
                    getFirebaseObject(dataSnapshot, receivedName.getName());
                    actionbar.setTitle(receivedName.getName());
                }
                catch (NullPointerException e) {
                    getFirebaseObject(dataSnapshot, usersRef);
                    try {
                        actionbar.setTitle(receivedName.getName());
                    }
                    catch (NullPointerException d) {
                        actionbar.setTitle("The Log Book");
                    }
                }
                if (bucketList.size() == 0){
                    adddefaultSlate();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        // Setting the floating action button
        floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItem();
            }
        });
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public String userToken() {
        FirebaseUser user = mAuth.getCurrentUser();
        Log.d("User getting ID", user.getUid());
        return user.getUid();
    }

    private void signOut() {
        mAuth.signOut();
        Intent i = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage( getBaseContext().getPackageName() );
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    public void areYouSure() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you Sure?");

        //Setting up the input
        final EditText input = new EditText(this);

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

    // Method Overloading used for when the user logs in the first time

    private void getFirebaseObject(DataSnapshot dataSnapshot, String name) {
        dataList.clear();
        dataListHolder.clear();
        bucketList.clear();
        for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
            bucketList.add(singleSnapshot.getValue(BucketListObject.class));
            BucketListObject currentItem = bucketList.get(bucketList.size() -1);
            if (currentItem.getName().equals(name)) {
                receivedName = currentItem;
                dataListHolder = currentItem.getDataList();
                currentItem.getPercent();
                adapter.notifyDataSetChanged();
            }

        }
        for (DataListObject i : dataListHolder) {
            dataList.add(i);
            adapter.notifyDataSetChanged();
        }
        bucketListAdapter.notifyDataSetChanged();
    }

    private void getFirebaseObject(DataSnapshot dataSnapshot, DatabaseReference dbref) {
        dataList.clear();
        dataListHolder.clear();
        bucketList.clear();
        for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
            try {
                bucketList.add(singleSnapshot.getValue(BucketListObject.class));
                Log.d("XXX: Size of list", String.valueOf(bucketList));
                if (bucketList.size() > 0){
                    BucketListObject defaultItem = bucketList.get(0);
                    receivedName = defaultItem;
                    dataListHolder = defaultItem.getDataList();
                    defaultItem.getPercent();
                }
                else
                    adddefaultSlate();

            }
            catch (NullPointerException e) {
                Log.d("Catch", "Caught Nothing");
            }
        }
        bucketListAdapter.notifyDataSetChanged();

        for (DataListObject i : dataListHolder) {
            dataList.add(i);
            adapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onBackPressed() {
        AlertDialog.Builder ab = new AlertDialog.Builder(MainActivity.this);
        ab.setTitle("Question!!");
        ab.setMessage("This will Exit the app. Are you sure you want to do this ?");
        ab.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //dialog.dismiss();
                //android.os.Process.killProcess(android.os.Process.myPid());
                finish();
                //System.exit(0);
            }
        });
        ab.setNegativeButton("no", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        ab.show();
    }

    public void addItem() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Bucket List Item");

        //Setting up the input
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        //Setting up buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_Text = input.getText().toString();
                //Open connection to database
                DatabaseReference updateList = database.getReference("Users/" + userToken());
                Query updateQuery = updateList.orderByChild("name").equalTo(receivedName.getName());
                dataListHolder = receivedName.addToList(receivedName.getName(), m_Text, "Status : Not Done", "Date : Not Done Yet", "Null");
                //Add listener for this Query
                updateQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot x : dataSnapshot.getChildren()) {
                            x.getRef().setValue(receivedName);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
                for (DataListObject i : dataListHolder) {
                    dataList.add(i);
                    adapter.notifyDataSetChanged();
                }
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

    public void adddefaultSlate() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Your First Slate");

        //Setting up the input
        final EditText input = new EditText(this);

        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        //Setting up buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatabaseReference usersRef = ref.child(userToken());
                m_Text = input.getText().toString();
                String userId = usersRef.push().getKey();
                // Trying to add values to database, this should eventually have an object defined
                BucketListObject bucketListObject = new BucketListObject(m_Text);
                usersRef.child(userId).setValue(bucketListObject);
                //listItems.add(m_Text);
                //adapter.notifyDataSetChanged();
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
}