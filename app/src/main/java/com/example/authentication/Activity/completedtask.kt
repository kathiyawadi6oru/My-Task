package com.example.authentication.Activity

import android.content.Intent
import android.graphics.Canvas
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.authentication.Adapter.cdataAdapter
import com.example.authentication.Adapter.dataAdapter
import com.example.authentication.Model.Datas
import com.example.authentication.R
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator

class completedtask : AppCompatActivity(), cdataAdapter.cdatalistener {

    private var crecyclerView: RecyclerView? = null
    var adapters: cdataAdapter? = null
    private var drawer: DrawerLayout? = null
    private var navigationView: NavigationView? = null
    private var toolbar: Toolbar? = null
    private var toggle: ActionBarDrawerToggle? = null
    private var imgview: ImageView?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_completedtask)




        drawer = findViewById<View>(R.id.completedtask) as DrawerLayout
        navigationView = findViewById<View>(R.id.navigation) as NavigationView
        toolbar = findViewById<Toolbar>(R.id.toolbar)

        setSupportActionBar(toolbar)
        crecyclerView = findViewById(R.id.displaycompleterecycleView)
        toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.open, R.string.close)
        drawer!!.addDrawerListener(toggle!!)
        toggle!!.syncState()

        navigationView!!.setNavigationItemSelectedListener { item ->
            drawer!!.closeDrawer(GravityCompat.START)
            when (item.itemId) {
                R.id.task -> {
                    val intent = Intent(this,MainActivity::class.java)
                    startActivity(intent)
                }
                R.id.tcomplete -> {
                    val intent = Intent(this,completedtask::class.java)
                    startActivity(intent)
                }
                R.id.menu_logout -> {
                    AuthUI.getInstance()
                        .signOut(this)
                    val intent = Intent(this, loginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                R.id.subscription -> Toast.makeText(
                    this@completedtask,
                    "subscription",
                    Toast.LENGTH_SHORT
                )
                    .show()
                R.id.share -> Toast.makeText(this@completedtask, "share", Toast.LENGTH_SHORT).show()
                else -> {
                }
            }
            true
        }

        initRecycleAdapter()

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
            .orderByChild("taskcomplete").equalTo(true)

        val option: FirebaseRecyclerOptions<Datas> = FirebaseRecyclerOptions.Builder<Datas>()
            .setQuery(query, Datas::class.java)
            .build()

        adapters = cdataAdapter(option,this)

        crecyclerView?.adapter = adapters
        val itemTouchHelper =ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(crecyclerView)
    }

    /*private fun dataAdapter(options: completedtask): dataAdapter {
        return dataAdapter(completedtask())
    }*/


    var simpleCallback:ItemTouchHelper.SimpleCallback = object : ItemTouchHelper.SimpleCallback(
        ItemTouchHelper.UP.or(
            ItemTouchHelper.DOWN
        ),
        ItemTouchHelper.LEFT.or(ItemTouchHelper.RIGHT)
    ){
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            adapters!!.swapItems(viewHolder.adapterPosition, target.adapterPosition)
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            if (direction==ItemTouchHelper.LEFT || direction==ItemTouchHelper.RIGHT){

                Toast.makeText(this@completedtask, "delete", Toast.LENGTH_SHORT).show()
                adapters!!.DeleteItem(viewHolder.adapterPosition)


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
            isCurrentlyActive: Boolean
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
                        this@completedtask,
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
        Snackbar.make(crecyclerView!!, "Item Deleted.", Snackbar.LENGTH_LONG)
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
            /*  .setNegativeButton("Cancel"){
                  null
              }*/
            .setNegativeButton("Cancel", null)
            .show()
    }

}