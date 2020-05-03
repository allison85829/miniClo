package com.example.miniclo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class ItemDetail extends AppCompatActivity {
    TextView detail_text;
    ImageView res_img, upload_img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        Intent intent = getIntent();
        String detail = intent.getStringExtra("message");
        String imguri = intent.getStringExtra("imguri");
        Log.i(" ********** Msg", imguri);
        detail_text = (TextView)findViewById(R.id.item_detail);
        res_img = (ImageView)findViewById(R.id.detail_img);

        detail_text.setText(detail);
        res_img.setImageURI(Uri.parse(imguri));



    }
}
