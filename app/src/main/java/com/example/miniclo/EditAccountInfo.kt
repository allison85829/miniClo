package com.example.miniclo

import android.app.Activity.RESULT_OK
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_bottom_nav_fragment_account.*
import kotlinx.android.synthetic.main.fragment_edit_account_info.*
import java.io.ByteArrayOutputStream
import java.io.OutputStream

/**
 * A simple [Fragment] subclass.
 * Use the [EditAccountInfo.newInstance] factory method to
 * create an instance of this fragment.
 */
class EditAccountInfo :  androidx.fragment.app.Fragment() {
    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null
    private lateinit var user_name_input : TextInputEditText
    private lateinit var user_email_input : TextInputEditText
    private lateinit var img_view: ImageView
    private lateinit var progressbar_pic: ProgressBar
    private lateinit var save_btn : Button
    private lateinit var imageUri: Uri
    private lateinit var imageBitmap : Bitmap
    var baos : OutputStream = ByteArrayOutputStream()
    private val REQUEST_IMAGE_CAPTURE = 100

    private var usersReference : DatabaseReference =
        FirebaseDatabase.getInstance().reference.child("/users");
    private lateinit var usersListener: ValueEventListener

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar_edit_account.setNavigationIcon(R.drawable.ic_back) // need to set the icon here to have a navigation icon. You can simple create an vector image by "Vector Asset" and using here
        toolbar_edit_account.setNavigationOnClickListener {
            // do something when click navigation
            //(activity as AppCompatActivity).finish()
            getFragmentManager()?.popBackStackImmediate()
        }

        progressbar_pic = getView()!!.findViewById(R.id.progressbar_pic)
        img_view = getView()!!.findViewById(R.id.profile_pic)
        user_name_input =  getView()!!.findViewById(R.id.user_name_text)
        user_email_input =  getView()!!.findViewById(R.id.user_email_text)
        img_view.setOnClickListener{
            takePictureIntent()
        }
        save_btn = getView()!!.findViewById(R.id.save_change_btn)
        save_btn.setOnClickListener{
            if (this::imageBitmap.isInitialized) {
                uploadImageAndSaveUri(imageBitmap)
            }
            updateUserInfo()
            // go back to user account page
            val acc_info : BottomNavFragmentAccount = BottomNavFragmentAccount()
            val edit_acc : EditAccountInfo = EditAccountInfo()
            val transaction : FragmentTransaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.layout_edit_account, acc_info)
            transaction.remove(edit_acc)
            transaction.commit()
        }
    }

    private fun updateUserInfo() {
        val name = user_name_input.editableText.toString().trim()
        val email = user_email_input.editableText.toString().trim()
        Log.d("EMAIL", email)
        Toast.makeText(
            activity, name,
            Toast.LENGTH_SHORT
        ).show()
        if (email != "") {
            val user = FirebaseAuth.getInstance().currentUser
            Log.d("USER ", user!!.email)
            user!!.updateEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "User email address updated.")
                    }
                }
        }
        if (name != "") {
            usersListener = object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented")
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    Toast.makeText(
                        activity, "Name updated",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            usersReference.addValueEventListener(usersListener)
            usersReference
                .child("/${FirebaseAuth.getInstance().currentUser?.uid}/user_name")
                .setValue(name)
        }
    }

    private fun takePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { pictureIntent ->
            pictureIntent.resolveActivity(activity?.packageManager!!)?.also {
                startActivityForResult(pictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            imageBitmap = data?.extras?.get("data") as Bitmap
            // set image view with the captured image
            baos = ByteArrayOutputStream()
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            img_view.setImageBitmap(imageBitmap)
        }
    }

    private fun uploadImageAndSaveUri(bitmap: Bitmap) {
        val baos = ByteArrayOutputStream()
        val storageRef = FirebaseStorage.getInstance()
            .reference
            .child("profile_pics/${FirebaseAuth.getInstance().currentUser?.uid}")
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val image = baos.toByteArray()

        val upload = storageRef.putBytes(image)

        progressbar_pic.visibility = View.VISIBLE

        upload.addOnCompleteListener { uploadTask ->
            progressbar_pic.visibility = View.INVISIBLE
            if (uploadTask.isSuccessful) {
                storageRef.downloadUrl.addOnCompleteListener { urlTask ->
                    urlTask.result?.let {
//                        Toast.makeText(
//                            activity,
//                            "Uploaded",
//                            Toast.LENGTH_SHORT
//                        ).show()
                    }
                }
            } else {
                uploadTask.exception?.let {
                    Toast.makeText(
                        activity,
                        it.message!!,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        (activity as AppCompatActivity).setSupportActionBar(toolbar_account)
        return inflater.inflate(R.layout.fragment_edit_account_info, container, false)
    }
}