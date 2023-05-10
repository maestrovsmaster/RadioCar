package com.maestrovs.radiocar.ui.radio

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.maestrovs.radiocar.R
import com.maestrovs.radiocar.data.entities.Station
import com.maestrovs.radiocar.extensions.addRipple
import kotlinx.android.synthetic.main.item_radio.view.root
import kotlinx.android.synthetic.main.item_radio.view.tvName
import java.security.AccessController
import java.security.AccessController.getContext


class StationAdapter(val onItem: ItemListener): RecyclerView.Adapter<StationAdapter.StationViewHolder>(){


    private var selectedStation: Station? = null;
    interface ItemListener {
        fun onClickedCharacter(item: Station?)
    }

    inner class StationViewHolder(itemView:View):RecyclerView.ViewHolder(itemView)

    private val diffCallback = object : DiffUtil.ItemCallback<Station>(){
        override fun areItemsTheSame(oldItem: Station, newItem: Station): Boolean {
            return oldItem.stationuuid == newItem.stationuuid
        }

        override fun areContentsTheSame(oldItem: Station, newItem: Station): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    private val differ = AsyncListDiffer(this,diffCallback)

    fun submitList(list: List<Station>) {differ.submitList(list)
    }

    fun setSelectedStation(station: Station) {
        this.selectedStation = station
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StationViewHolder {
        return StationViewHolder(
            LayoutInflater.from(
                parent.context
            ).inflate(
                R.layout.item_radio,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: StationViewHolder, position: Int) {

        val item = differ.currentList[position]

        holder.itemView.apply {
           // tvName.text = "${item.employee_name}"
            //tvSalary.text = "Salary: Rs.${item.employee_salary}"
            //tvAge.text = "Age: ${item.employee_age}"

            root.setOnClickListener {
                if(item != selectedStation) {
                    onItem.onClickedCharacter(item)
                }else{
                    onItem.onClickedCharacter(null)
                }
            }



            var selected = false
            selectedStation?.let {
                if(it.stationuuid == item.stationuuid) {
                    selected = true
                }
            }
            if(selected) {
                root.setBackgroundColor(ContextCompat.getColor(context, R.color.pink))
            }else {
                root.addRipple().apply {
                    background = with(TypedValue()) {
                        context.theme.resolveAttribute(
                            android.R.attr.selectableItemBackground, this, true
                        )
                        ContextCompat.getDrawable(context, resourceId)
                    }
                }
            }


            tvName.text = "${item.name}"
        }

    }



}
