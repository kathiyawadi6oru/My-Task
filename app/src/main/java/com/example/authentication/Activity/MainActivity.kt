package com.example.authentication.Activity


import android.content.DialogInterface
import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.authentication.Adapter.dataAdapter
import com.example.authentication.Model.Datas
import com.example.authentication.R
import com.example.authentication.R.*
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import java.util.*
import kotlin.collections.HashMap


class MainActivity : AppCompatActivity(),dataAdapter.datalistener{
    lateinit var fab: FloatingActionButton
    private var recyclerView: RecyclerView? = null
    private var mInterstitialAd: InterstitialAd? = null
    private var mRewardedVideoAd: RewardedAd? = null
    var adapters: dataAdapter? = null
    private var drawer: DrawerLayout? = null
    private var navigationView: NavigationView? = null
    private var toolbar: Toolbar? = null
    private var toggle: ActionBarDrawerToggle? = null
    private var imgview: ImageView?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MobileAds.initialize(this)

        //intersitial ad
        loadad()


        fab = findViewById(R.id.adddata)
        recyclerView = findViewById(R.id.displayrecycleView)
        drawer = findViewById<View>(id.drawer) as DrawerLayout
        navigationView = findViewById<View>(id.navigation) as NavigationView
        toolbar = findViewById<Toolbar>(id.toolbar)

        setSupportActionBar(toolbar)
        toggle = ActionBarDrawerToggle(this, drawer, toolbar, string.open, string.close)
        drawer!!.addDrawerListener(toggle!!)
        toggle!!.syncState()

        navigationView!!.setNavigationItemSelectedListener { item ->
            drawer!!.closeDrawer(GravityCompat.START)
            when (item.itemId) {
                R.id.task -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
                R.id.tcomplete -> {
                    val intent = Intent(this, completedtask::class.java)
                    startActivity(intent)
                }
                R.id.menu_logout -> {
                    AuthUI.getInstance()
                        .signOut(this)
                    val intent = Intent(this, loginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                R.id.subscription -> Toast.makeText(this, "subscription", Toast.LENGTH_SHORT).show()

                R.id.share -> Toast.makeText(this, "share", Toast.LENGTH_SHORT).show()
                else -> {
                }
            }
            true
        }



                if (FirebaseAuth.getInstance().currentUser == null) {
            intent = Intent(this, loginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

       // MobileAds.initialize(this)


        val mAdView = findViewById<AdView>(R.id.adView)
        val adRequest = AdRequest.Builder ()
            .addTestDevice("F7E4B4A2F920BA9D6255DA48ED8A0493")
            . build ()
        mAdView.loadAd(adRequest)


       /* mInterstitialAd.adUnitId = "ca-app-pub-3940256099942544/1033173712"
        mInterstitialAd.loadAd(AdRequest.Builder().build())
*/


        fab.setOnClickListener {


            if (mInterstitialAd != null) {
                mInterstitialAd!!.show(this)

            } else {
                Log.d("ad", "The interstitial wasn't loaded yet.")
            }
            createAlertDialog()
        }

        initRecycleAdapter()
        val user = FirebaseAuth.getInstance().currentUser
        val imgview = findViewById<ImageView>(R.id.imageView)
        val view = navigationView!!.getHeaderView(0)
        val email = view.findViewById<TextView>(id.email)
        val name = view.findViewById<TextView>(id.name)

        name.text = "${user!!.displayName}"
        email.text = "${user!!.email}"
        val img = user!!.photoUrl.toString()
        /*Glide.with(this)
            .load(img)
            .fitCenter()
            .into(imgview);*/

        /*if (img != null) {
            Glide.with(this)
                .load(img)
                .fitCenter()
                .into(imgview);
        } else {
            Glide.with(this)
                .load(R.drawable.add_24)
                .fitCenter()
                .into(imgview);
        }*/

            

    }

    private fun loadad(){
        InterstitialAd.load(this,
            "ca-app-pub-3940256099942544/1033173712",
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(p0: InterstitialAd) {
                    super.onAdLoaded(p0)
                    mInterstitialAd = p0
                    Log.d("ad", "onad loaded")
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                    mInterstitialAd = null

                    Log.d("ad", "onad loaded  faild+${p0.message}")
                }
            })
    }

    override fun onBackPressed() {
        if (drawer!!.isDrawerOpen(GravityCompat.START)) {
            drawer!!.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun initRecycleAdapter() {
        val query: Query = FirebaseDatabase.getInstance().reference
                .child("Data")
                .child(FirebaseAuth.getInstance().currentUser!!.uid)

            .orderByChild("taskcomplete").equalTo(false)
//                .orderByChild("taskcomplete").equalTo(false)
//                .orderByChild("taskcomplete").equalTo(true)

        val option: FirebaseRecyclerOptions<Datas> = FirebaseRecyclerOptions.Builder<Datas>()
                .setQuery(query, Datas::class.java)
                .build()
        //option.Sort((x, y) => DateTime.Compare(x.CreateTime, y.CreateTime));
        adapters = dataAdapter(option, this)
        recyclerView!!.scrollToPosition(0)
        recyclerView?.adapter = adapters
        val itemTouchHelper =ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    var simpleCallback:ItemTouchHelper.SimpleCallback = object : ItemTouchHelper.SimpleCallback(
        ItemTouchHelper.UP.or(
            ItemTouchHelper.DOWN
        ),
        ItemTouchHelper.LEFT.or(ItemTouchHelper.RIGHT)
    ){
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder,
        ): Boolean {
            adapters!!.swapItems(viewHolder.adapterPosition, target.adapterPosition)
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            if (direction==ItemTouchHelper.LEFT || direction==ItemTouchHelper.RIGHT){

                Toast.makeText(this@MainActivity, "delete", Toast.LENGTH_SHORT).show()
                adapters!!.DeleteItem(viewHolder.adapterPosition)


            }
            if(direction==ItemTouchHelper.UP || direction==ItemTouchHelper.DOWN){
               // adapters!!.swapItems(onSelectedChanged(),chooseDropTarget())
            /*viewHolder.adapterPosition
            .adapterPosition
            */
                val fromPosition: Int = viewHolder.adapterPosition
                //val toPosition: Int = .adapterPosition
           

            }

        }
        private val dragCallback: ItemTouchHelper.Callback = object : ItemTouchHelper.Callback() {
            var dragFrom = -1
            var dragTo = -1
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
            ): Int {
                return makeMovementFlags(ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
                    0)
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder,
            ): Boolean {
                val fromPosition = viewHolder.adapterPosition
                val toPosition = target.adapterPosition
                if (dragFrom == -1) {
                    dragFrom = fromPosition
                }
                dragTo = toPosition
                //adapter.onItemMove(fromPosition, toPosition)
                return true
            }

            private fun reallyMoved(from: Int, to: Int) {
                // I guessed this was what you want...
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
            override fun isLongPressDragEnabled(): Boolean {
                return true
            }

            override fun isItemViewSwipeEnabled(): Boolean {
                return false
            }

            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
            ) {
                super.clearView(recyclerView, viewHolder)
                if (dragFrom != -1 && dragTo != -1 && dragFrom != dragTo) {
                    reallyMoved(dragFrom, dragTo)
                }
                dragTo = -1
                dragFrom = dragTo
            }
        }

        //https://github.com/xabaras/RecyclerViewSwipeDecorator
        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean,
        ) {
            RecyclerViewSwipeDecorator.Builder(
                c,
                recyclerView,
                viewHolder,
                dX,
                dY,
                actionState,
                isCurrentlyActive
            )
                    .addBackgroundColor(
                        ContextCompat.getColor(
                            this@MainActivity,
                            R.color.colorWhite
                        )
                    )
                    .addActionIcon(R.drawable.delete)
                    .create()
                    .decorate()
            super.onChildDraw(
                c,
                recyclerView!!,
                viewHolder!!,
                dX,
                dY,
                actionState,
                isCurrentlyActive
            )
        }
    }

    override fun onStart() {
        super.onStart()
        adapters?.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapters?.stopListening()
    }

    //add data
    private fun createAlertDialog() {
        loadad()
        val data: EditText = EditText(this)

        AlertDialog.Builder(this)
                .setTitle("Add Data")
                .setMessage("enter your data")
                .setView(data)
                .setCancelable(false)
                .setPositiveButton("Save", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        if (data.text != null && data.text.trim().length == 0) {
                            Toast.makeText(
                                this@MainActivity,
                                "Please enter a data",
                                Toast.LENGTH_SHORT
                            ).show()
                            createAlertDialog()
                        } else {
                            adddatas(data.text.toString())
                        }

                    }
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show()
    }

    private fun adddatas(text: String) {
        val ref = FirebaseDatabase.getInstance().reference
        val data = Datas(text, false, System.currentTimeMillis())
        ref.child("Data")
                .child(FirebaseAuth.getInstance().uid.toString())
                .child(UUID.randomUUID().toString())
                .setValue(data)
                .addOnSuccessListener {
                    recyclerView!!.smoothScrollToPosition(adapters!!.itemCount - 1)
                    //recyclerView!!.scrollToPosition(recyclerView!!.childCount)
                    Toast.makeText(this, "data added sucessfully!!!", Toast.LENGTH_SHORT).show()
                }
    }

//delete
    override fun handleDelete(dataSnapshot: DataSnapshot) {
        dataSnapshot.ref.removeValue()
        handleundo(dataSnapshot)
    }
//undo
    override fun handleundo(dataSnapshot: DataSnapshot) {

        val databaseReference = dataSnapshot.ref
        val data : Datas = dataSnapshot.getValue<Datas>(Datas::class.java)!!
        databaseReference.removeValue()
                .addOnSuccessListener {
                    Toast.makeText(this, "undo sucess", Toast.LENGTH_SHORT).show()
                }
        Snackbar.make(recyclerView!!, "Item Deleted.", Snackbar.LENGTH_LONG)
                .setAction("UNDO"){ listener ->
                    databaseReference.setValue(data)
                }.show()

    }
//edit
    override fun handlecheckedchange(isCheck: Boolean, dataSnapshot: DataSnapshot) {
        val mapof = HashMap<String, Any>()
        mapof.put("taskcomplete", isCheck)
        dataSnapshot.ref.updateChildren(mapof)
                .addOnSuccessListener {
                    Toast.makeText(this, "done", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "error", Toast.LENGTH_SHORT).show()
                }



    }
//edit
    override fun handleeditclicklistener(dataSnapshot: DataSnapshot) {
        val data = dataSnapshot.getValue<Datas>(Datas::class.java)
        val editText = EditText(this)
        editText.setText(data!!.text)
        editText.setSelection(data.text!!.length)
        AlertDialog.Builder(this)
                .setTitle("Edit Data")
                .setView(editText)
                .setCancelable(false)
                .setPositiveButton("Update"){ dialogInterface, I ->
                    val newdata = editText.text.toString()
                     data!!.text = newdata
                    // data.text = newdata
                    dataSnapshot.ref.setValue(data)

                }
                .setNegativeButton("Cancel", null)
                .show()
    }


}


