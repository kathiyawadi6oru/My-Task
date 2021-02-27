package com.example.authentication.Activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import android.widget.Toast.makeText
import androidx.appcompat.app.AppCompatActivity
import com.example.authentication.R
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.AuthUI.IdpConfig
import com.firebase.ui.auth.AuthUI.IdpConfig.*
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdCallback
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.firebase.auth.FirebaseAuth
import java.util.*


class loginActivity : AppCompatActivity() {

    lateinit var button:Button
    //////////////////////////////////////////////////////////////////////////////////////
    private var rewardedAd: RewardedAd? = null

    //creating Object of RewardedAdLoadCallback
    var rewardedAdLoadCallback: RewardedAdLoadCallback? = null

    //creating Object of RewardedAdCallback
    var rewardedAdCallback: RewardedAdCallback? = null
    /////////////////////////////////////////////////////////////////////////////////////
    val REQUEST_CODE=1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        getSupportActionBar()!!.hide()
        setContentView(R.layout.activity_login)
        button = findViewById<Button>(R.id.blogin)

        MobileAds.initialize(this)
        rewardedAd = RewardedAd(this, "ca-app-pub-3940256099942544/5224354917")

        loadRewardedAd()


        rewardedAdLoadCallback = object : RewardedAdLoadCallback() {
            override fun onRewardedAdLoaded() {
                // Showing a simple Toast message to user when Rewarded Ad Failed to Load
                Toast.makeText(this@loginActivity, "Rewarded Ad is Loaded", Toast.LENGTH_SHORT).show()
            }

            override fun onRewardedAdFailedToLoad(adError: LoadAdError) {
                // Showing a simple Toast message to user when Rewarded Ad Failed to Load
                Toast.makeText(this@loginActivity, "Rewarded Ad is not Loaded $adError", Toast.LENGTH_SHORT).show()
            }
        }

           button.setOnClickListener {

               showRewardedAd()

           }
    }


///////////////////////////////////////////////////////////////////////////////////

    private fun loadRewardedAd() {
        val adRequest = AdRequest.Builder ()
            .addTestDevice("F7E4B4A2F920BA9D6255DA48ED8A0493")
            . build ()

        // load Rewarded Ad with the Request
        rewardedAd!!.loadAd(adRequest, rewardedAdLoadCallback)

        // Showing a simple Toast message to user when Rewarded an ad is Loading
       // makeText(this, "Rewarded Ad is loading ", Toast.LENGTH_LONG).show()
    }

    private fun showRewardedAd() {
        if (rewardedAd!!.isLoaded) {

            //creating the Rewarded Ad Callback and showing the user appropriate message
            rewardedAdCallback = object : RewardedAdCallback() {
                override fun onRewardedAdOpened() {
                    // Rewarded Ad is opened
                   // makeText(this@loginActivity, "Rewarded Ad is Opened", Toast.LENGTH_LONG).show()
                }

                override fun onRewardedAdClosed() {
                    // Rewarded Ad is closed
                    userlogin()
                   // makeText(this@loginActivity, "Rewarded Ad Closed", Toast.LENGTH_LONG).show()
                }

                override fun onUserEarnedReward(reward: RewardItem) {
                    //  completely watching the Rewarded Ad
                  //  makeText(this@loginActivity,"You won the reward :" + reward.getAmount(),Toast.LENGTH_LONG).show()
                }

                override fun onRewardedAdFailedToShow(adError: AdError) {
                    // user when Rewarded Ad Failed to Show
                   // makeText(this@loginActivity, "Rewarded Ad failed to show due to error:$adError",Toast.LENGTH_LONG).show()
                }
            }

            //showing the ad Rewarded Ad if it is loaded
            rewardedAd!!.show(this@loginActivity, rewardedAdCallback)

            // Showing a simple Toast message to user when an Rewarded ad is shown to the user
           // makeText(this@loginActivity, "Rewarded Ad  is loaded and Now showing ad  ", Toast.LENGTH_LONG).show()
        } else {
            //Load the Rewarded ad if it is not loaded
            loadRewardedAd()
            userlogin()
            // Showing a simple Toast message to user when Rewarded ad is not loaded
            makeText(this@loginActivity, "Rewarded Ad is not Loaded ", Toast.LENGTH_LONG).show()
        }
    }


    ///////////////////////////////////////////////////////////////////////////////////////

    @SuppressLint("ResourceType")
            fun userlogin() {

                val provider: List<IdpConfig> = Arrays.asList(
                    EmailBuilder().build(),
                    PhoneBuilder().build(),
                    GoogleBuilder().build(),
                    //  FacebookBuilder().build(),
                    //TwitterBuilder().build()
                )

            val intent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(provider)
                .setTosAndPrivacyPolicyUrls("http://google.com", "http://www.google.com")
                .setLogo(R.drawable.ic_baseline_account_circle_24)
                .setTheme(R.style.LoginTheme)
                .build()

            startActivityForResult(intent, REQUEST_CODE)
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)

            if (REQUEST_CODE == requestCode) {

                if (resultCode == RESULT_OK) {
                    // Successfully signed in
                    val user = FirebaseAuth.getInstance().currentUser

                    if (user!!.metadata!!.creationTimestamp == user.metadata!!.lastSignInTimestamp){
                        makeText(this, "welcome new user!!!", Toast.LENGTH_SHORT).show()
                    }else{
                        makeText(this, "welcome ${user.displayName}", Toast.LENGTH_SHORT).show()
                    }

                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    // ...
                } else {
                    // Sign in failed. If response is null the user canceled the
                    // sign-in flow using the back button. Otherwise check
                    // response.getError().getErrorCode() and handle the error.
                    // ...

                }
            }else{
                val response = IdpResponse.fromResultIntent(data)
                if (response == null){
                    Log.d("back", "onActivity result: the user has cancelled the sign in request")
                }else{
                    Log.d("back", "onActivity result:${response.error}")
                }
            }
        }

}




