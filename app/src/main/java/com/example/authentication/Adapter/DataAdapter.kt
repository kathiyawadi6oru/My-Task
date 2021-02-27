package com.example.authentication.Adapter

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import java.util.*
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.authentication.Activity.MainActivity
import com.example.authentication.Model.Datas
import com.example.authentication.R
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DataSnapshot
import java.util.Collections.swap


class dataAdapter(

    options: FirebaseRecyclerOptions<Datas>, val dataListener: MainActivity,

    //,val cdataListener : completedtask
) : FirebaseRecyclerAdapter<Datas, dataAdapter.dataViewHolder>(options) {

    class dataViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val txtData:TextView = itemView.findViewById(R.id.display_data)
        val txtdate:TextView = itemView.findViewById(R.id.display_time)
        val checkboxcomplete:CheckBox = itemView.findViewById(R.id.cb)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): dataViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.record, parent, false)
        return dataViewHolder(view)
    }


    override fun onBindViewHolder(holder: dataViewHolder, position: Int, model: Datas) {
        holder.txtData.text = model.text
        holder.checkboxcomplete.isChecked = model.taskcomplete == true
        val date: CharSequence = DateFormat.format("EEEE,MMM d,yyyy h:mm:ss a", model.date!!)
        holder.txtdate.text = date
        holder.checkboxcomplete.setOnCheckedChangeListener { compoundButton: CompoundButton, b: Boolean ->
            val dataSnapshot = snapshots.getSnapshot(holder.adapterPosition)
            dataListener.handlecheckedchange(b, dataSnapshot)

        }

        holder.itemView.setOnClickListener {
            val dataSnapshot = snapshots.getSnapshot(holder.adapterPosition)
            dataListener.handleeditclicklistener(dataSnapshot)
        }

    }

        public fun DeleteItem(position: Int){
            dataListener.handleDelete(snapshots.getSnapshot(position))

        }


    fun swapItems(fromPosition: Int, toPosition: Int): Boolean {
        /*if (fromPosition < toPosition) {
            for (i in fromPosition..toPosition - 1) {
                nameList.set(i, nameList.set(i + 1, nameList.get(i)));
            }
        } else {
            for (i in fromPosition..toPosition + 1) {
                nameList.set(i, nameList.set(i - 1, nameList.get(i)));
            }

        }*/
        snapshots.add(toPosition, snapshots.removeAt(fromPosition));
        notifyItemMoved(fromPosition, toPosition);

        notifyItemMoved(fromPosition, toPosition)
        return true
    }


    interface datalistener{
        public fun handlecheckedchange(isCheck: Boolean, dataSnapshot: DataSnapshot)
        public fun handleeditclicklistener(dataSnapshot: DataSnapshot)
        public fun handleDelete(dataSnapshot: DataSnapshot)
        public fun handleundo(dataSnapshot: DataSnapshot)
    }


    /*interface cdatalistener{
         public fun handlecheckedchange(isCheck: Boolean, dataSnapshot: DataSnapshot)
         public fun handleeditclicklistener(dataSnapshot: DataSnapshot)
         public fun handleDelete(dataSnapshot: DataSnapshot)
         public fun handleundo(dataSnapshot: DataSnapshot)
     }*/



}

