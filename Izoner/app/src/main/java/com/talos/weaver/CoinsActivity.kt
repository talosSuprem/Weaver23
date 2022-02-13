package com.talos.weaver

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*

import com.google.android.gms.ads.*

import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback


class CoinsActivity : AppCompatActivity() {
    lateinit var mAdView2 : AdView
    lateinit var imageView: ImageView
    lateinit var imageView1: ImageView
    lateinit var imageView2: ImageView
    lateinit var mDialog : Dialog
    var progress: ProgressBar? = null

    private var mInterstitialAd: InterstitialAd? = null

    private var progr = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coins)



       MobileAds.initialize(this)

        progress = findViewById(R.id.progress_bar)
        progress!!.setOnClickListener {
            progress!!.animate().apply {
                duration = 1000
                rotationYBy(3600f)
                Toast.makeText(this@CoinsActivity, "not now... wait", Toast.LENGTH_LONG).show()
            }.withEndAction {
                progress!!.animate().apply {
                    duration = 10000
                    rotationXBy(3600f)
                    Toast.makeText(this@CoinsActivity, "Need 10 000 regular users", Toast.LENGTH_LONG).show()

                }.start()
            }
        }


        imageView1 = findViewById(R.id.infoalgo);
        imageView1.setOnClickListener {
           val view = View.inflate(this@CoinsActivity, R.layout.popupinfoalgo, null)
           val builder = AlertDialog.Builder(this@CoinsActivity)
            builder.setView(view)
            val dialog = builder.create()
            dialog.show()
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)



            }



       mAdView2 = findViewById(R.id.adView2)
       val adRequest = AdRequest.Builder().build()
        mAdView2.loadAd(adRequest)

        loadInterstitial()

        val button = findViewById<Button>(R.id.gentoken)
        button.setOnClickListener {
            showInter()

        }


        val imageView1 = findViewById<ImageView>(R.id.returnToCoin);
        imageView1.setOnClickListener {
            val intent = Intent(this, StartActivity::class.java)

            startActivity(intent)

        }


    }
    private fun showInter() {
        if (mInterstitialAd != null) {
            mInterstitialAd?.show(this)
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.")
        }

    }

    private fun loadInterstitial() {
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(this,"ca-app-pub-1750096581756549/7941561472", adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                mInterstitialAd = interstitialAd
            }
        })


    }



}