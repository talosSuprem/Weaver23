package com.talos.weaver.Fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.talos.weaver.Adapter.UserAdapter
import com.talos.weaver.Model.User
import com.talos.weaver.R
import java.util.ArrayList


class Shearsh2Fragment : Fragment() {
    private var recyclerView: RecyclerView? = null
    private var userAdapter: UserAdapter? = null
    private var userList: MutableList<User?>? = null
    var search_bar: EditText? = null




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_shearsh2, container, false)
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.setLayoutManager(LinearLayoutManager(getContext()))
        search_bar = view.findViewById(R.id.search_bar)
        userList = ArrayList()
        userAdapter = getContext()?.let { UserAdapter(it, userList, true) }
        recyclerView!!.setAdapter(userAdapter)
        readUsers()
        search_bar!!.addTextChangedListener(object : TextWatcher {
            public override fun beforeTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
            }

            public override fun onTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
                searchUsers(charSequence.toString().toLowerCase())
            }

            public override fun afterTextChanged(editable: Editable) {}
        })
        return view
    }

    private fun searchUsers(s: String) {
        val query: Query =
            FirebaseDatabase.getInstance().getReference("Users").orderByChild("username")
                .startAt(s)
                .endAt(s + "\uf8ff")
        query.addValueEventListener(object : ValueEventListener {
            public override fun onDataChange(dataSnapshot: DataSnapshot) {
                userList!!.clear()
                for (snapshot: DataSnapshot in dataSnapshot.getChildren()) {
                    val user: User? = snapshot.getValue(
                        User::class.java
                    )
                    userList!!.add(user)
                }
                userAdapter!!.notifyDataSetChanged()
            }

            public override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun readUsers() {
        val firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().getCurrentUser()
        val reference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users")
        reference.addValueEventListener(object : ValueEventListener {
            public override fun onDataChange(dataSnapshot: DataSnapshot) {
                if ((search_bar!!.getText().toString() == "")) {
                    userList!!.clear()
                    for (snapshot: DataSnapshot in dataSnapshot.getChildren()) {
                        val user: User? = snapshot.getValue(
                            User::class.java
                        )
                        userList!!.add(user)
                    }
                    userAdapter!!.notifyDataSetChanged()
                }
            }

            public override fun onCancelled(databaseError: DatabaseError) {}
        })
    }
}