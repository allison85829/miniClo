package com.example.miniclo

import android.hardware.camera2.CameraAccessException
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.miniclo.com.example.miniclo.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_upload_item.*
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class UploadItem : AppCompatActivity() {
    var mStorageRef: StorageReference? = null
    var imgUri: Uri? = null
    var item = Item()
    var res: String? = null
    var user: FirebaseUser? = null
    var item_key: String? = null
    var cat = arrayOf("top", "bottom", "hat", "dress", "shoe", "accessory")

    private val mDatabase = FirebaseDatabase.getInstance()
    private var storageRef: StorageReference? = null
    private var itemReference: DatabaseReference? = null
    private var itemListener: ValueEventListener? = null

    private var userReference: DatabaseReference? = null
    private var userListener: ValueEventListener? = null

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_item)

        val intent = intent
        var imgUriStr = intent.getStringExtra("img_uri")
        imgUri = Uri.parse(imgUriStr)
        uploadImgPreview.setImageURI(Uri.parse(imgUriStr))

        setUpActionBar()
        setUpDBRefAndListeners()
        processImage()

        upload_btn.setOnClickListener(View.OnClickListener { view ->
            Log.i("ADD ITEM ", "upload btn clicked *****")
            try {
                //FileUploader(view)
                uploadImage(view)
            } catch (e: CameraAccessException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        finish() // close this activity as oppose to navigating up
        return false
    }

    fun setUpActionBar() {
        val actionbar = supportActionBar
        actionbar!!.title = "Upload Closet Item"
        actionbar!!.setDisplayHomeAsUpEnabled(true)
        actionbar!!.setHomeAsUpIndicator(R.drawable.ic_close)
    }

    private fun getExtension(uri: Uri): String? {
        val cr = contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri))
    }

    private fun setUpDBRefAndListeners() {
        // ****** ITEM LISTENER *************
        itemListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        }

        // ****** USER LISTENER *************
        userListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        }

        // ****** add user and item listener
        user = FirebaseAuth.getInstance().currentUser
        itemReference = mDatabase.reference
        userReference = mDatabase.reference
        userReference!!.child("/users").push().addValueEventListener(userListener!!)
        itemReference!!.child("/items").push().addValueEventListener(itemListener!!)
        mStorageRef = FirebaseStorage.getInstance().getReference("/images")
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun processImage() {
        var image: FirebaseVisionImage? = null
        try {
            image = FirebaseVisionImage.fromFilePath(this@UploadItem, imgUri!!)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val labeler = FirebaseVision.getInstance().cloudImageLabeler
        val vision_img = VisionImage()
        val rotation = vision_img.getRotationCompensation("1", this@UploadItem, this@UploadItem)
        labeler.processImage(image!!)
            .addOnSuccessListener { labels ->
                res = ""
                val tags = ArrayList<String>()
                for (label in labels) {
                    tags.add(label.text)
                    val text = label.text
                    val entityId = label.entityId
                    val confidence = label.confidence
                    res += "$text, $entityId, $confidence\n"
                }
                item.category = tags[0]
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    item.date_added = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE)
                }
                item.laundry_status = false
                item.tags = tags
                item.worn_frequency = 0

                category_value.setText(item.category)
                tag_1_value.setText(item.tags[1])
                tag_2_value.setText(item.tags[2])
                tag_3_value.setText(item.tags[3])
                tag_4_value.setText(item.tags[4])
            }
            .addOnFailureListener { }
    }

    private fun uploadImage(view: View) {
        val file_name = System.currentTimeMillis().toString() + "." + getExtension(imgUri!!)
        val Ref: StorageReference = mStorageRef!!.child(file_name)

        Ref.putFile(imgUri!!)
            .continueWithTask { task ->
                Log.i("TASK ", "Continue task")
                if (!task.isSuccessful) {
                    throw task.exception!!
                }
                Ref.downloadUrl
            }
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.i("TASK ", "before push to firebase")
                    val download = task.result
                    uploadImageToDB(download, item)
                    Toast.makeText(
                        this@UploadItem,
                        "Item Uploaded Successfully",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                }
            }
    }

    private fun uploadImageToDB(img_uri: Uri?, item: Item) {
        item.image = img_uri.toString()
        itemReference = mDatabase.getReference().child("/items")
        userReference = mDatabase.getReference().child("/users")

        // add user id to item object
        item.user = user!!.getUid()
        item_key = itemReference!!.push().getKey()
        itemReference!!.child(item_key!!).setValue(item)
        val list_entry_key: String =
            userReference!!.child(user!!.getUid()).child("item_list").push().getKey()!!

        userReference!!.child(user!!.getUid()).child("item_list").child(list_entry_key)
            .setValue(item_key)
    }

    /*
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Throws(CameraAccessException::class, IOException::class)
    private fun FileUploader(view: View) {
        // Uploading file to Storage
        val file_name = System.currentTimeMillis().toString() + "." + getExtension(imgUri!!)
        val Ref: StorageReference = mStorageRef!!.child(file_name)
        var image: FirebaseVisionImage? = null
        try {
            image = FirebaseVisionImage.fromFilePath(this@UploadItem, imgUri!!)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val labeler = FirebaseVision.getInstance().cloudImageLabeler
        val vision_img = VisionImage()
        val rotation = vision_img.getRotationCompensation("1", this@UploadItem, this@UploadItem)
        labeler.processImage(image!!)
            .addOnSuccessListener { labels ->
                res = ""
                val tags = ArrayList<String>()
                for (label in labels) {
                    tags.add(label.text)
                    val text = label.text
                    val entityId = label.entityId
                    val confidence = label.confidence
                    res += "$text, $entityId, $confidence\n"
                }
                //                        Toast.makeText(AddItem.this,
                //                                res, Toast.LENGTH_LONG).show();
                item.category = tags[0]
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    item.date_added = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE)
                }
                item.laundry_status = false
                item.tags = tags
                item.worn_frequency = 0
                //val finalRes: String = res

                Ref.putFile(imgUri!!)
                    .continueWithTask { task ->
                        Log.i("TASK ", "Continue task")
                        if (!task.isSuccessful) {
                            throw task.exception!!
                        }
                        Ref.downloadUrl
                    }
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.i("TASK ", "before push to firebase")
                            val download = task.result
                            UploadItem(download, item)
                            Toast.makeText(
                                this@UploadItem,
                                "Item Uploaded Successfully",
                                Toast.LENGTH_LONG
                            ).show()
                            finish()
                        }
                    }
            }
            .addOnFailureListener { }
    }

    fun UploadItem(img_uri: Uri?, item: Item) {
        item.image = img_uri.toString()
        itemReference = mDatabase.getReference().child("/items")
        userReference = mDatabase.getReference().child("/users")

        // add user id to item object
        item.user = user!!.getUid()
        item_key = itemReference!!.push().getKey()
        itemReference!!.child(item_key!!).setValue(item)
        val list_entry_key: String =
            userReference!!.child(user!!.getUid()).child("item_list").push().getKey()!!

        userReference!!.child(user!!.getUid()).child("item_list").child(list_entry_key)
            .setValue(item_key)
    }
     */
}
