package com.talos.weaver.Fragments

import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.talos.weaver.Adapter.SectionPagerAdapter
import com.talos.weaver.R
import com.talos.weaver.SecondFragment
import com.talos.weaver.SocialUsersFragment
import com.talos.weaver.ThirdFragment

class SearchTabbedFragment : Fragment() {

    var myFragment: View? = null
    var viewPager: ViewPager? = null
    var tabLayout: TabLayout? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        myFragment = inflater.inflate(R.layout.fragment_search_tabbed, container, false)
        viewPager = myFragment!!.findViewById(R.id.viewPager3)
        tabLayout = myFragment!!.findViewById(R.id.tabLayout3)



        return myFragment
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setUpViewPager(viewPager)
        tabLayout!!.setupWithViewPager(viewPager)
        tabLayout!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {}
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun setUpViewPager(viewPager: ViewPager?) {
        val adapter = SectionPagerAdapter(childFragmentManager)
        adapter.addFragment(SearchFragment(), "Users")

        adapter.addFragment(SocialUsersFragment(), "Notifications")

        viewPager!!.adapter = adapter
    }

}