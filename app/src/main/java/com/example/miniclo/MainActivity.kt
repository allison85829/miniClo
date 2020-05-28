package com.example.miniclo

//import kotlinx.android.synthetic.main.activity_main.category
//import kotlinx.android.synthetic.main.activity_main.date_added
//import kotlinx.android.synthetic.main.activity_main.tags

//import androidx.navigation.ui.setupActionBarWithNavController
import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    //        create an instance of Firebase Database
    //private var mDatabase : FirebaseDatabase = FirebaseDatabase.getInstance()
    //        reference to the root node
   // private lateinit var itemReference : DatabaseReference

    //private lateinit var itemListener: ValueEventListener

    //private lateinit var storage: FirebaseStorage
    private lateinit var auth: FirebaseAuth

    //lateinit var tags : TextView
    //lateinit var date_added : TextView
    //lateinit var worn_frequency : TextView

    private val REQUEST_TO_DETAIL = 3
    private val PERMISSION_CODE = 1000
    private val IMAGE_CAPTURE_CODE = 1001
    var imguri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

        if(auth.currentUser == null){
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }else{
            Toast.makeText(this, "Already logged in", Toast.LENGTH_LONG).show()
        }
        setContentView(R.layout.activity_main)

        val navController = findNavController(R.id.mainNavFragment)

        // Set up ActionBar
        //setSupportActionBar(toolbar)
        //NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)

        //var appBarConfiguration = AppBarConfiguration(setOf(R.id.bottomNavFragmentCloset, R.id.bottomNavFragmentLaundry, R.id.bottomNavFragmentStats, R.id.bottomNavFragmentAccount))
        //setupActionBarWithNavController(navController, appBarConfiguration)

        // Set up navigation menu
        navigationView.setupWithNavController(navController)

    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(
            Navigation.findNavController(this, R.id.mainNavFragment), drawerLayout)
    }

    /*
    public override fun onStart() {
        super.onStart()

        val itemListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {

            }
        }

        itemReference = Firebase.database.reference.child("/items")
        itemReference.addValueEventListener(itemListener)
        this.itemListener = itemListener
    }

    public override fun onStop() {
        super.onStop()
        itemReference.removeEventListener(this.itemListener)
    }
    */

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 2 && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imguri = data.data
            //img.setImageURI(imguri)
            val intent = Intent(this, UploadItem::class.java)
            intent.putExtra("img_uri", imguri.toString())
            startActivity(intent)
        }
        if (requestCode == IMAGE_CAPTURE_CODE && resultCode == Activity.RESULT_OK) {
            //img.setImageURI(imguri)
            val intent = Intent(this, UploadItem::class.java)
            intent.putExtra("img_uri", imguri.toString())
            startActivity(intent)
        }
        if (requestCode == REQUEST_TO_DETAIL && resultCode == Activity.RESULT_OK) {
//            TextView textView = (TextView)findViewById(R.id.item_detail);
//            ImageView res_img = (ImageView)findViewById(R.id.detail_img);
            val detail = data!!.getStringExtra("message")
            //            textView.setText(detail);
            Log.i("---------- Msg", detail)
            //            res_img.setImageURI(imguri);
        }
    }

    fun clickSelectFile() {
        val intent = Intent()
        intent.type = "image/"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, 2)
    }

    fun clickTakePhoto() {
        // if system OS is >= marshmallow, request runtime permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_DENIED ||
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_DENIED
            ) {
                // permission to enabled, request it
                val permission = arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                // show popup to request permissions
                requestPermissions(permission, PERMISSION_CODE)
            } else {
                // permission already granted
                openCamera()
            }
        } else {
            // system OS < marshmallow
            openCamera()
        }
    }

    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        imguri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        // Camera intent
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imguri)
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE)
    }
}
