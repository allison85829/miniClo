package com.example.miniclo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import android.content.Intent
import kotlinx.android.synthetic.main.fragment_bottom_nav_fragment_account.*

/**
 * A simple [Fragment] subclass.
 */
class BottomNavFragmentAccount : androidx.fragment.app.Fragment() {

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
