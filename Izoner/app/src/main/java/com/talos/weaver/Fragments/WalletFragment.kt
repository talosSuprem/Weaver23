package com.talos.weaver.Fragments

import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.talos.weaver.R
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.talos.weaver.Adapter.SectionPagerAdapter
import com.talos.weaver.FirstFragment
import com.talos.weaver.SecondFragment
import com.talos.weaver.ThirdFragment
import com.talos.weaver.Fragments.WalletFragment
import com.talos.weaver.Model.Post
import com.talos.weaver.Model.User

class WalletFragment : Fragment() {
    var myFragment: View? = null
    var viewPager: ViewPager? = null
    var tabLayout: TabLayout? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        myFragment = inflater.inflate(R.layout.fragment_wallet, container, false)
        viewPager = myFragment!!.findViewById(R.id.viewPager)
        tabLayout = myFragment!!.findViewById(R.id.tabLayout)
        return myFragment
    }



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setUpViewPager(viewPager)
        tabLayout!!.setupWithViewPager(viewPager)
        tabLayout!!.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {}
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun setUpViewPager(viewPager: ViewPager?) {
        val adapter = SectionPagerAdapter(childFragmentManager)
        adapter.addFragment(FirstFragment(), "generation 1")
        adapter.addFragment(SecondFragment(), "generation 2")
        adapter.addFragment(ThirdFragment(), "generation 2")
        viewPager!!.adapter = adapter
    }

    companion object {
        val instance: WalletFragment
            get() = WalletFragment()
    }



    /**private fun pubInf2**/

}