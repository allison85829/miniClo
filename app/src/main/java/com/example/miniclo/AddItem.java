package com.example.miniclo;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.hardware.camera2.CameraAccessException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.miniclo.com.example.miniclo.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;
import com.google.firebase.ml.vision.objects.FirebaseVisionObject;
import com.google.firebase.ml.vision.objects.FirebaseVisionObjectDetector;
import com.google.firebase.ml.vision.objects.FirebaseVisionObjectDetectorOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AddItem extends AppCompatActivity {

    private static final int REQUEST_TO_DETAIL = 3;
    Button select, upload, take_photo;
    ImageView img;
    StorageReference mStorageRef;
    public Uri imguri;
    public Item item  = new Item();
    public String res, file_name;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int PERMISSION_CODE = 1000;
    private static final int IMAGE_CAPTURE_CODE = 1001;
    private static final String CAMERA_ID = "my_camera_id";
    FirebaseUser user;
    User user_obj;
    int total_item;
    String item_key;
    String[] cat = new String[]  {"top", "bottom", "hat", "dress", "shoe", "accessory"};

    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference itemReference, userReference;
    private ValueEventListener itemListener, userListener;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        // set up views like buttons, image views and text
        setUpViews();
        setUpDBRefAndListeners();

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
                Log.i("ADD ITEM ", "upload btn clicked *****");
                try {
                    FileUploader(view);
                } catch (CameraAccessException | IOException e) {
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

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void FileSelector() {
        Intent intent = new Intent();
        intent.setType("image/");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 2);
    }

    private void setUpViews() {
        select = (Button)findViewById(R.id.select_img_btn);
        upload = (Button)findViewById(R.id.upload_btn);
        take_photo = (Button)findViewById(R.id.take_photo_btn);
        img = (ImageView)findViewById(R.id.imageView);
    }

    private void setUpDBRefAndListeners() {
        // ****** ITEM LISTENER *************
        itemListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        // ****** USER LISTENER *************
        userListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        // ****** add user and item listener
        user = FirebaseAuth.getInstance().getCurrentUser();
        itemReference = mDatabase.getReference();
        userReference = mDatabase.getReference();
        userReference.child("/users").push().addValueEventListener(userListener);
        itemReference.child("/items").push().addValueEventListener(itemListener);

        mStorageRef = FirebaseStorage.getInstance().getReference("/images");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==2 && resultCode==RESULT_OK && data!=null && data.getData()!=null ) {
            imguri = data.getData();
            img.setImageURI(imguri);
        }
        if (requestCode == IMAGE_CAPTURE_CODE && resultCode == RESULT_OK) {
            img.setImageURI(imguri);
        }

        if (requestCode == REQUEST_TO_DETAIL && resultCode == RESULT_OK) {
            String detail = data.getStringExtra("message");
        }
    }

    private String getExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void FileUploader(View view) throws CameraAccessException, IOException {
        // Uploading file to Storage
        file_name = System.currentTimeMillis() + "." + getExtension(imguri);
        StorageReference Ref = mStorageRef.child(file_name);

        FirebaseVisionImage image = null;
        try {
            image = FirebaseVisionImage.fromFilePath(AddItem.this, imguri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FirebaseVisionImageLabeler labeler = FirebaseVision.getInstance().getCloudImageLabeler();
        FirebaseVisionObjectDetectorOptions options =
                new FirebaseVisionObjectDetectorOptions.Builder()
                        .setDetectorMode(FirebaseVisionObjectDetectorOptions.SINGLE_IMAGE_MODE)
                        .enableMultipleObjects()
                        .enableClassification()  // Optional
                        .build();
        FirebaseVisionObjectDetector obj_detector =  FirebaseVision.getInstance().getOnDeviceObjectDetector(options);

        VisionImage vision_img = new VisionImage();
        int rotation = vision_img.getRotationCompensation("1", AddItem.this, AddItem.this);

        FirebaseVisionImage finalImage = image;
        obj_detector.processImage(image)
                .addOnSuccessListener(
                        new OnSuccessListener<List<FirebaseVisionObject>>() {
                            @Override
                            public void onSuccess(List<FirebaseVisionObject> detectedObjects) {
                                // Task completed successfully
                                Log.i("OBJECT", Integer.toString(detectedObjects.get(0).getClassificationCategory()));
                                getLabels(view, labeler, finalImage, Ref);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Task failed with an exception
                                // ...
                            }
                        });
    }

    public void getLabels(View view, FirebaseVisionImageLabeler labeler, FirebaseVisionImage image, StorageReference Ref) {
        labeler.processImage(image)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onSuccess(List<FirebaseVisionImageLabel> labels) {
                        Toast.makeText(AddItem.this,
                                "Image successfully pass into vision api", Toast.LENGTH_LONG).show();
                        res = "";
                        ArrayList<String> tags = new ArrayList<String>();
                        for (FirebaseVisionImageLabel label: labels) {
                            tags.add(label.getText());
                            String text = label.getText();
                            String entityId = label.getEntityId();
                            float confidence = label.getConfidence();
                            res += text + ", " + entityId + ", " + confidence + "\n";
                        }

                        item.setCategory(tags.get(0));
                        item.setDate_added(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE));
                        item.setLaundry_status(false);
                        item.setTags(tags);
                        item.setWorn_frequency(0);
                        String finalRes = res;

                        Ref.putFile(imguri)
                                .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                    @Override
                                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                        if (!task.isSuccessful()) {
                                            throw task.getException();
                                        }
                                        return Ref.getDownloadUrl();
                                    }
                                })
                                .addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if (task.isSuccessful()) {
                                            Uri download = task.getResult();
                                            UploadItem(download, item);
                                            Toast.makeText(AddItem.this,
                                                    "Image Uploaded Successfully",
                                                    Toast.LENGTH_LONG).show();
                                            toItemDetail(view, item);
                                        }
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

    public void UploadItem(Uri img_uri, Item item) {
        Log.i("FILENAME", file_name);
        item.setImg_url(img_uri.toString());
        item.setImg_name(file_name);
        itemReference = mDatabase.getReference().child("/items");
        userReference = mDatabase.getReference().child("/users");

        // add user id to item object
        item.setUser(user.getUid());
        item_key = itemReference.push().getKey();
        itemReference.child(item_key).setValue(item);

        userReference.child(user.getUid() + "/item_list/" + item_key).setValue(true);
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

    public void toItemDetail(View view, Item item) {
        Intent intent = new Intent(AddItem.this, ItemDetailPage.class);
        intent.putExtra("item_key", item_key);
        intent.putExtra("imguri", imguri.toString());
        intent.putExtra("item_obj", item);
        Resources res = getResources();

        startActivityForResult(intent, REQUEST_TO_DETAIL);
    }

}
