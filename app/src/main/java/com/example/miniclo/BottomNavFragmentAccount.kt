package com.example.miniclo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bottom_nav_fragment_account, container, false)
    }

    /*
    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.your_menu, menu)
    }*/
}
