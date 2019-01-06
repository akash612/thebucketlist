package com.tfs.akash612.thebucketlist;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class BucketList extends AppCompatActivity {

    Button addNewItemBtn;
    public BucketListObject receivedName;
    private String m_Text = "";
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView recyclerView;
    EditText input;
    //Define an array to hold the list
    ArrayList<DataListObject> dataList = new ArrayList<DataListObject>();
    ArrayList<DataListObject> dataListHolder = new ArrayList<DataListObject>();//ArrayList<String> listItems=new ArrayList<String>();
    //Define adapter to handle the listView
    CustomAdapter adapter;
    //ArrayAdapter<String> adapter;
    FirebaseAuth mAuth;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bucket_list_card);
        mAuth = FirebaseAuth.getInstance();
        adapter = new CustomAdapter(this, dataList);
        recyclerView = (RecyclerView) findViewById(R.id.cardView);
        mLayoutManager = new LinearLayoutManager(BucketList.this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
        receivedName = (BucketListObject) getIntent().getSerializableExtra("listName");
        receivedName.getPercent();
        DatabaseReference usersRef = ref.child(userToken());

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                getAllList(dataSnapshot, receivedName.getName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        usersRef.addValueEventListener(listener);


        //Set Title To Bucket List Title

        //testRecycler(receivedName);
        setTitle(receivedName.getName());
    }

    public void addItem() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Bucket List Item");
        //final BucketListObject receivedName = (BucketListObject) getIntent().getSerializableExtra("listName");
        //Setting up the input
        final EditText input = new EditText(this);

        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        //Setting up buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_Text = input.getText().toString();
                //DataListObject dataListObject = new DataListObject(receivedName, m_Text, "Status : Not Done");
                //dataList.add(dataListObject);
                DatabaseReference updateList = database.getReference("Users/" + userToken());
                Query updateQuery = updateList.orderByChild("name").equalTo(receivedName.getName());
                dataListHolder = receivedName.addToList(receivedName.getName(), m_Text, "Status : Not Done", "Not Done Yet", "Null");
                //Addlistener For this Query
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
                Log.d("XXXXXXXXXX : : Debugging Adapter", String.valueOf(dataList));
                for (DataListObject i : dataListHolder) {
                    dataList.add(i);
                    adapter.notifyDataSetChanged();
                }
                adapter.notifyDataSetChanged();
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.actionbarbucketlist, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.add_item) {
            //check if any items to add
            addItem();
        }
        return true;
    }

    public String userToken() {
        FirebaseUser user = mAuth.getCurrentUser();
        Log.d("User getting ID", user.getUid());
        return user.getUid();
    }

    private void getAllList(DataSnapshot dataSnapshot, String name) {
        dataList.clear();
        dataListHolder.clear();
        for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
            BucketListObject blItems = singleSnapshot.getValue(BucketListObject.class);
            if (blItems.getName().equals(name)) {
                receivedName = blItems;
                dataListHolder = blItems.getDataList();
                blItems.getPercent();
            }
        }
        for (DataListObject i : dataListHolder) {
            dataList.add(i);
            adapter.notifyDataSetChanged();
        }

    }


}
