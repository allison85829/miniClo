package com.example.miniclo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import android.content.Intent
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.fragment_bottom_nav_fragment_account.*

/**
 * A simple [Fragment] subclass.
 */
class BottomNavFragmentAccount : androidx.fragment.app.Fragment() {
    lateinit var user_name : TextView
    lateinit var user_email : TextView
    var user : FirebaseUser? = FirebaseAuth.getInstance().currentUser
    lateinit var edit_btn : Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        //(activity as AppCompatActivity).setSupportActionBar(account_toolbar)
        //(activity as AppCompatActivity).getSupportActionBar()?.setTitle("Account");
        //setHasOptionsMenu(true)

        (activity as AppCompatActivity).setSupportActionBar(toolbar_account)


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bottom_nav_fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        user_email = getView()!!.findViewById(R.id.user_email)
        user_email.setText(user?.email)
        edit_btn = getView()!!.findViewById(R.id.edit_profile_button)
        edit_btn.setOnClickListener{ view ->
            val edit_acc_info : EditAccountInfo = EditAccountInfo()
            val acc_info : BottomNavFragmentAccount = BottomNavFragmentAccount()
            val transaction : FragmentTransaction = parentFragmentManager.beginTransaction()
//            transaction.add(R.id.layout_account, acc_info)
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

    /*
    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.your_menu, menu)
    }*/
}
