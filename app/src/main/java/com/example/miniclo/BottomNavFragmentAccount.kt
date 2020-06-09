package com.example.miniclo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import android.content.Intent
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.miniclo.com.example.miniclo.User
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_bottom_nav_fragment_account.*

/**
 * A simple [Fragment] subclass.
 */
class BottomNavFragmentAccount : androidx.fragment.app.Fragment() {
    lateinit var user_name : TextView
    lateinit var user_email : TextView
    lateinit var profile_pic : ImageView
    var user : FirebaseUser? = FirebaseAuth.getInstance().currentUser
    lateinit var edit_btn : Button
    private var usersReference : DatabaseReference =
        FirebaseDatabase.getInstance().reference
            .child("/users/${FirebaseAuth.getInstance().currentUser?.uid}");

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        (activity as AppCompatActivity).setSupportActionBar(toolbar_account)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bottom_nav_fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpUserInfo()
        edit_btn = getView()!!.findViewById(R.id.edit_profile_button)
        edit_btn.setOnClickListener{ view ->
            val edit_acc_info : EditAccountInfo = EditAccountInfo()
            val transaction : FragmentTransaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.layout_account, edit_acc_info)
            transaction.commit()
        }

        logout_btn.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
            (activity as AppCompatActivity).finish()
        }

        update_pass_btn.setOnClickListener{
            val intent = Intent(activity, UpdatePassword::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        setUpUserInfo()
    }

    private fun setUpUserInfo() {
        profile_pic = getView()!!.findViewById(R.id.imageView2)
        val storageRef = FirebaseStorage.getInstance()
            .reference
            .child("profile_pics/${FirebaseAuth.getInstance().currentUser?.uid}")
            .downloadUrl.addOnSuccessListener {
                // Got the download URL for 'users/me/profile.png'
                Picasso.get().load(it).into(profile_pic);
            }.addOnFailureListener {
                // Handle any errors
            }

        user_name = getView()!!.findViewById(R.id.user_name)
        user_email = getView()!!.findViewById(R.id.user_email)
        val usersListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented")
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user_obj = dataSnapshot!!.getValue<User>()
                Log.i("USER ", user_obj?.user_name)
                user_name.setText(user_obj?.user_name)
                user_email.setText(FirebaseAuth.getInstance().currentUser?.email)
            }
        }
        usersReference.addValueEventListener(usersListener)
    }
}