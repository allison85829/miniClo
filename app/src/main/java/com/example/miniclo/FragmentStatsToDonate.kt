package com.example.miniclo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_stats_to_donate.*

/**
 * A simple [Fragment] subclass.
 */
class FragmentStatsToDonate : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        (activity as AppCompatActivity).setSupportActionBar(toolbar_to_donate)

        // Adds back button to get back to map
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)

        /*
        back_most_worn.setOnClickListener{
            (activity as AppCompatActivity).finish()
        }

         */

        return inflater.inflate(R.layout.fragment_stats_to_donate, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up back button
        toolbar_to_donate.setNavigationIcon(R.drawable.ic_home_black_24dp) // need to set the icon here to have a navigation icon. You can simple create an vector image by "Vector Asset" and using here
        toolbar_to_donate.setNavigationOnClickListener {
            // do something when click navigation
            //(activity as AppCompatActivity).finish()
            getFragmentManager()?.popBackStackImmediate()
        }

    }
}
