package com.example.miniclo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class ItemDetail extends AppCompatActivity {
    TextView tags, date_added, worn_frequency, laundry_status;
    ImageView detail_img;
    Button add_laundry, rm_laundry;
    DatabaseReference itemReference;
    ValueEventListener itemListener;
    FirebaseStorage storage;
    StorageReference mStorageRef;
    FirebaseAuth auth;
    Item item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        Intent intent = getIntent();
        String item_key = intent.getStringExtra("item_key");
        String imguri = intent.getStringExtra("imguri");
        Log.i("ITEM KEY", item_key);

        tags = (TextView)findViewById(R.id.detail_tags_text);
        date_added = (TextView)findViewById(R.id.detail_date_added_text);
        worn_frequency = (TextView)findViewById(R.id.detail_worn_frequency);
        laundry_status = (TextView)findViewById(R.id.detail_laundry_status);
        detail_img = (ImageView)findViewById(R.id.detail_item_img);
        add_laundry = (Button)findViewById(R.id.add_laundry);
        rm_laundry = (Button)findViewById(R.id.remove_laundry);

        itemListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                item = dataSnapshot.getValue(Item.class);

                if (item != null) {
                    Log.i("ITEM", item.toString());
                    tags.setText(item.getCategory().toString());
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
                    laundry_status.setText(Boolean.toString(item.getLaundry_status()));
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

        mStorageRef = FirebaseStorage.getInstance().getReference("/images");
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
}
