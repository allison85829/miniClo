package com.example.miniclo;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.hardware.camera2.CameraAccessException;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ktx.Firebase;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.cloud.FirebaseVisionCloudDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.List;

public class AddItem extends AppCompatActivity {

    private static final int REQUEST_TO_DETAIL = 3;
    Button select, upload, take_photo;
    ImageView img;
    StorageReference mStorageRef;
    public Uri imguri;
    public Item item;
    public String res;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int PERMISSION_CODE = 1000;
    private static final int IMAGE_CAPTURE_CODE = 1001;
    private static final String CAMERA_ID = "my_camera_id";
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        mStorageRef = FirebaseStorage.getInstance().getReference("/images");

        select = (Button)findViewById(R.id.select_img_btn);
        upload = (Button)findViewById(R.id.upload_btn);
        take_photo = (Button)findViewById(R.id.take_photo_btn);
        img = (ImageView)findViewById(R.id.imageView);

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FileSelector();
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                try {
                    FileUploader(view);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }
        });

        take_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // if system OS is >= marshmallow, request runtime permission
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CAMERA) ==
                            PackageManager.PERMISSION_DENIED ||
                        checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_DENIED) {
                        // permission to enabled, request it
                        String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        // show popup to request permissions
                        requestPermissions(permission, PERMISSION_CODE);
                    }
                    else {
                        // permission already granted
                        openCamera();
                    }
                }
                else {
                    // system OS < marshmallow
                    openCamera();
                }
            }
        });


    }

    private void FileSelector() {
        Intent intent = new Intent();
        intent.setType("image/");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("---------- Msg", Integer.toString(requestCode));
        if (requestCode==2 && resultCode==RESULT_OK && data!=null && data.getData()!=null ) {
            imguri = data.getData();
            img.setImageURI(imguri);
        }
        if (requestCode == IMAGE_CAPTURE_CODE && resultCode == RESULT_OK) {
//            Bundle extras = data.getExtras();
//            Bitmap imageBitmap = (Bitmap) extras.get("data");
            img.setImageURI(imguri);
        }
        if (requestCode == REQUEST_TO_DETAIL && resultCode == RESULT_OK) {
            TextView textView = (TextView)findViewById(R.id.item_detail);
            ImageView res_img = (ImageView)findViewById(R.id.detail_img);
            String detail = data.getStringExtra("message");
            textView.setText(detail);
            Log.i("---------- Msg", detail);
            res_img.setImageURI(imguri);
        }
    }

    private String getExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void FileUploader(View view) throws CameraAccessException {
        // Uploading file to Storage
        String file_name = System.currentTimeMillis() + "." + getExtension(imguri);
        StorageReference Ref = mStorageRef.child(file_name);

        FirebaseVisionImage image = null;
        try {
            image = FirebaseVisionImage.fromFilePath(AddItem.this, imguri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FirebaseVisionImageLabeler labeler = FirebaseVision.getInstance().getCloudImageLabeler();
        VisionImage vision_img = new VisionImage();
        int rotation = vision_img.getRotationCompensation("1", AddItem.this, AddItem.this);

        labeler.processImage(image)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionImageLabel> labels) {
                        Toast.makeText(AddItem.this,
                                "Image successfully pass into vision api", Toast.LENGTH_LONG).show();
                        res = "";
                        for (FirebaseVisionImageLabel label: labels) {
                            String text = label.getText();
                            String entityId = label.getEntityId();
                            float confidence = label.getConfidence();

                            res += text + ", " + entityId + ", " + confidence + "\n";
                        }
                        Toast.makeText(AddItem.this,
                                res, Toast.LENGTH_LONG).show();

                        String finalRes = res;

                        Ref.putFile(imguri)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        // Get a URL to the uploaded content
//                                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                        Toast.makeText(AddItem.this, "Image Uploaded Successfully", Toast.LENGTH_LONG).show();
                                        toItemDetail(view);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Handle unsuccessful uploads
                                        // ...
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");
        imguri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        // Camera intent
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imguri);
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE);
    }

    // handling permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // this method is called when user presses Allow or Deny from Permission Request Popup
        switch (requestCode) {
            case PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] ==
                                            PackageManager.PERMISSION_GRANTED) {
                    // permission from popup was granted
                    openCamera();
                }
                else {
                    // permission from popup was denied
                    Toast.makeText(this, "Permission denied...", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void toItemDetail(View view) {
        Intent intent = new Intent(this, ItemDetail.class);
        intent.putExtra("message", res);
        intent.putExtra("imguri", imguri.toString());
        startActivityForResult(intent, REQUEST_TO_DETAIL);
    }

}
