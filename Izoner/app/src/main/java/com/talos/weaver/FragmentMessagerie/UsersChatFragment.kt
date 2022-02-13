package com.talos.weaver.FragmentMessagerie

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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.talos.weaver.Adapter.AdapterUsers
import com.talos.weaver.Model.User
import com.talos.weaver.R

import java.util.ArrayList


class UsersChatFragment : Fragment() {
    private var recyclerView: RecyclerView? = null
    private var userChatAdapter: AdapterUsers? = null
    private var mUsers: MutableList<User?>? = null
    var search_users: EditText? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_users_chat, container, false)

        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.setLayoutManager(LinearLayoutManager(context))
        mUsers = ArrayList()
        readUsers()
        search_users = view.findViewById(R.id.search_users)
        search_users!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                searchUsers(charSequence.toString().toLowerCase())
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        return view
    }

    private fun searchUsers(s: String) {
        val fuser = FirebaseAuth.getInstance().currentUser
        val query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("search")
            .startAt(s)
            .endAt(s + "\uf8ff")
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mUsers!!.clear()
                for (snapshot in dataSnapshot.children) {
                    val user = snapshot.getValue(
                        User::class.java
                    )!!
                    assert(fuser != null)
                    if (user.id != fuser!!.uid) {
                        mUsers!!.add(user)
                    }
                }
                userChatAdapter = AdapterUsers(context!!, mUsers)
                recyclerView!!.adapter = userChatAdapter
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun readUsers() {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val reference = FirebaseDatabase.getInstance().getReference("Users")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (search_users!!.text.toString() == "") {
                    mUsers!!.clear()
                    for (snapshot in dataSnapshot.children) {
                        val user = snapshot.getValue(
                            User::class.java
                        )
                        if (user!!.id != firebaseUser!!.uid) {
                            mUsers!!.add(user)
                        }
                    }
                    userChatAdapter = AdapterUsers(context!!, mUsers)
                    recyclerView!!.adapter = userChatAdapter
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }
}