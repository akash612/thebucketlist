package com.tfs.akash612.thebucketlist;

import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AddEditSlates extends AppCompatActivity {

    private FirebaseAuth mAuth;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference("Users");
    ArrayList<BucketListObject> bucketList = new ArrayList<BucketListObject>();

    RecyclerView.LayoutManager mLayoutManager;
    AddEditSlatesAdapter adapter;
    RecyclerView recyclerView;
    FloatingActionButton fab;
    private String m_Text = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_slates);

        mAuth = FirebaseAuth.getInstance();
        DatabaseReference usersRef = ref.child(userToken());
        usersRef.keepSynced(true);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        adapter = new AddEditSlatesAdapter(this, bucketList);
        recyclerView = (RecyclerView) findViewById(R.id.cardViewSlates);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                getAllList(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        usersRef.addValueEventListener(listener);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItem();
            }
        });
    }

    private void getAllList(DataSnapshot dataSnapshot) {
        ArrayList<DataListObject> dataHolder = new ArrayList<>();
        bucketList.clear();
        for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
            //BucketListObject blItems = singleSnapshot.getValue(BucketListObject.class);
            //Store the database in objects
            bucketList.add(singleSnapshot.getValue(BucketListObject.class));
            for (BucketListObject i : bucketList) {
                i.getPercent();
            }
            adapter.notifyDataSetChanged();
        }

    }

    public String userToken() {
        FirebaseUser user = mAuth.getCurrentUser();
        Log.d("User getting ID", user.getUid());
        return user.getUid();
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
