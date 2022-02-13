package com.talos.weaver.Fragments


import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseUser
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle

import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import androidx.recyclerview.widget.LinearLayoutManager


import android.view.View
import androidx.fragment.app.Fragment
import com.talos.weaver.Adapter.NotificationAdapter
import com.talos.weaver.Model.Notification
import com.talos.weaver.R

import java.util.*

class NotificationFragment constructor() : Fragment() {
    private var recyclerView: RecyclerView? = null
    private var notificationAdapter: NotificationAdapter? = null
    private var notificationList: MutableList<Notification?>? = null
    public override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_notification, container, false)
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView!!.setHasFixedSize(true)
        val mLayoutManager: LinearLayoutManager = LinearLayoutManager(getContext())
        recyclerView!!.setLayoutManager(mLayoutManager)
        notificationList = ArrayList()
        notificationAdapter = getContext()?.let { NotificationAdapter(it, notificationList) }
        recyclerView!!.setAdapter(notificationAdapter)
        readNotifications()
        return view
    }

    private fun readNotifications() {
        val firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().getCurrentUser()
        val reference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("Notifications").child(
                firebaseUser!!.getUid()
            )
        reference.addValueEventListener(object : ValueEventListener {
            public override fun onDataChange(dataSnapshot: DataSnapshot) {
                notificationList!!.clear()
                for (snapshot: DataSnapshot in dataSnapshot.getChildren()) {
                    val notification: Notification? = snapshot.getValue(
                        Notification::class.java
                    )
                    notificationList!!.add(notification)
                }
                Collections.reverse(notificationList)
                notificationAdapter!!.notifyDataSetChanged()
            }

            public override fun onCancelled(databaseError: DatabaseError) {}
        })
    }
}