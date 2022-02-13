package com.talos.weaver

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.*
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.StorageReference


class FirstFragment : Fragment() {


    var firebaseAuth: FirebaseAuth? = null
    var user: FirebaseUser? = null
    var firebaseDatabase: FirebaseDatabase? = null
    var databaseReference: DatabaseReference? = null
    var storageReference: StorageReference? = null

    lateinit var imageView1: ImageView
    var myFragment: View? = null
    var getCoin: Button? = null
    var getAllLike: TextView? = null
    var getAllToken: TextView? = null
    var getCoin2: Button? = null
    var getCoin3: Button? = null
    var getCoin4: Button? = null
    lateinit var coinuser: TextView
    var userid1: String? = null
    var progress: ProgressBar? = null


    var firebaseUser: FirebaseUser? = null
    var reference: StorageReference? = null

    private var mRewardedAd: RewardedAd? = null
    private var mRewardedAd2: RewardedAd? = null
    private var mRewardedAd3: RewardedAd? = null
    private final var TAG = "FirstFragment"





    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        myFragment = inflater.inflate(R.layout.fragment_first, container, false)
        MobileAds.initialize(context)
        firebaseAuth = FirebaseAuth.getInstance()
        user = firebaseAuth!!.currentUser
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase!!.getReference("Users")
        coinuser = myFragment!!.findViewById(R.id.usercoin)
        progress = myFragment!!.findViewById(R.id.progress_bar1)
        getAllLike = myFragment!!.findViewById(R.id.coinTv)


        val query = databaseReference!!.orderByChild("email").equalTo(
            user!!.email
        )
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (ds in snapshot.children) {
                    val getAllLike1 = "" + ds.child("likeUser").value

                    getAllLike!!.text = getAllLike1

                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })












        progress!!.setOnClickListener {
            progress!!.animate().apply {
                duration = 1000
                rotationYBy(3600f)
                Toast.makeText(context, "not now... wait", Toast.LENGTH_LONG).show()
            }.withEndAction {
                progress!!.animate().apply {
                    duration = 10000
                    rotationXBy(3600f)
                    Toast.makeText(context, "Need 10 000 regular users", Toast.LENGTH_LONG).show()

                    val intent = Intent(context, MainActivity::class.java)

                    startActivity(intent)

                }.start()
            }
        }

        getCoin = myFragment!!.findViewById(R.id.gentoken1)
        getCoin!!.setOnClickListener {
            showadreward()
        }



        var adRequest = AdRequest.Builder().build()

        RewardedAd.load(context,"ca-app-pub-1750096581756549/5418537585", adRequest, object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d(TAG, adError?.message)
                mRewardedAd = null
            }

            override fun onAdLoaded(rewardedAd: RewardedAd) {
                Log.d(TAG, "Ad was loaded.")
                mRewardedAd = rewardedAd

                mRewardedAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
                    override fun onAdShowedFullScreenContent() {
                        // Called when ad is shown.
                        Log.d(TAG, "Ad was shown.")
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                        // Called when ad fails to show.
                        Log.d(TAG, "Ad failed to show.")
                    }

                    override fun onAdDismissedFullScreenContent() {
                        // Called when ad is dismissed.
                        // Set the ad reference to null so you don't show the ad a second time.
                        Log.d(TAG, "Ad was dismissed.")

                        mRewardedAd = null
                    }
                }
            }
        })





        imageView1 = myFragment!!.findViewById(R.id.infoalgo);
        imageView1.setOnClickListener {
            val view = View.inflate(context, R.layout.popupinfoalgo, null)
            val builder = AlertDialog.Builder(context)
            builder.setView(view)
            val dialog = builder.create()
            dialog.show()
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)


        }
        return myFragment
    }


    private fun showadreward(){
        if (mRewardedAd != null) {
            mRewardedAd?.show(context as Activity, OnUserEarnedRewardListener() {
                fun onUserEarnedReward(rewardItem: RewardItem) {

                    var rewardType = rewardItem.getType()
                    Log.d(TAG, "User earned the reward.")
                }
            })
        } else {
            Log.d(TAG, "The rewarded ad wasn't ready yet.")
        }

    }


    }










