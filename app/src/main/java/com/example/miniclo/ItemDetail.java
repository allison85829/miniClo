package com.example.miniclo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemDetail extends AppCompatActivity {
    TextView tags, date_added, worn_frequency, laundry_status;
    TextView tag1, tag2, tag3;
    ImageView detail_img;
    Button add_laundry, rm_laundry;
    FloatingActionButton del;
    DatabaseReference itemReference;
    ValueEventListener itemListener;
    FirebaseStorage storage;
    StorageReference mStorageRef;
    FirebaseAuth auth;
    Item item;
    View v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        Intent intent = getIntent();
        String item_key = intent.getStringExtra("item_key");
        String imguri = intent.getStringExtra("imguri");
        Log.i("ITEM KEY", item_key);

        tags = (TextView)findViewById(R.id.detail_tags_label);
        tag1 = (TextView)findViewById(R.id.detail_tag_1);
        tag2 = (TextView)findViewById(R.id.detail_tag_2);
        tag3 = (TextView)findViewById(R.id.detail_tag_3);

        date_added = (TextView)findViewById(R.id.detail_date_added_text);
        worn_frequency = (TextView)findViewById(R.id.detail_worn_frequency);
        laundry_status = (TextView)findViewById(R.id.detail_laundry_status);
        detail_img = (ImageView)findViewById(R.id.detail_item_img);
        add_laundry = (Button)findViewById(R.id.add_laundry);
        rm_laundry = (Button)findViewById(R.id.remove_laundry);
        del = (FloatingActionButton) findViewById(R.id.del_btn);

        itemListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                item = dataSnapshot.getValue(Item.class);

                if (item != null) {
                    Log.i("ITEM", item.toString());
                    List<String> tags_lst = item.getTags();
                    tag1.setText(tags_lst.get(0));
                    tag2.setText(tags_lst.get(1));
                    tag3.setText(tags_lst.get(2));

                    date_added.setText(item.getDate_added().toString());
                    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("/items/" + item_key);
                    Map<String, Object> update = new HashMap<>();
                    if (item.getLaundry_status() & item.getIncr() == false) {
                        update.put("worn_frequency", item.getWorn_frequency() + 1);
                        update.put("incr", true);
                        updateFreq(update, dbRef);
                    } else if (item.getLaundry_status() == false & item.getIncr() == true) {
                        update.put("incr", false);
                        updateFreq(update, dbRef);
                    }
                    worn_frequency.setText(Integer.toString(item.getWorn_frequency()));
                    String stat = "";
                    if (item.getLaundry_status()) {
                        stat = "Yes";
                    } else {
                        stat = "No";
                    }
                    laundry_status.setText(stat);
                    Glide.with(ItemDetail.this).load(item.getImage()).into(detail_img);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        itemReference = FirebaseDatabase.getInstance().getReference().child("/items").child(item_key);
        itemReference.addValueEventListener(itemListener);
        this.itemListener = itemListener;

        add_laundry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                itemReference = FirebaseDatabase.getInstance().getReference().child("/items").child(item_key).child("laundry_status");
                itemReference.setValue(true);
            }
        });

        rm_laundry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                itemReference = FirebaseDatabase.getInstance().getReference().child("/items").child(item_key).child("laundry_status");
                itemReference.setValue(false);
            }
        });

        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopUpClass popUpClass = new PopUpClass();
                popUpClass.showPopupWindow(v);
            }
        });

        mStorageRef = FirebaseStorage.getInstance().getReference("/images");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.item_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_edit:
                Toast.makeText(
                        this,
                        "Action Edit Clicked",
                        Toast.LENGTH_SHORT
                ).show();
                return true;
            case R.id.action_delete:
                Toast.makeText(
                        this,
                        "Action Delete Clicked",
                        Toast.LENGTH_SHORT
                ).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void updateFreq(Map<String, Object> update, DatabaseReference dbRef) {
        dbRef.updateChildren(update).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                    Log.i("OUT", "Success!");
                else
                    Log.i("OUT", "Failure");
            }
        });
    }

    public void displayPopupWindow() {
        PopupWindow popup = new PopupWindow(this);
        View layout = getLayoutInflater().inflate(R.layout.popup_window, null);
        popup.setContentView(layout);
        popup.setOutsideTouchable(true);
        popup.setFocusable(true);
        popup.showAtLocation(layout, Gravity.CENTER, 0, 0);
    }
}
