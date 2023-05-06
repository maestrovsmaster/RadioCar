package com.maestrovs.radiocar.ui.radio

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

import com.maestrovs.radiocar.R
import com.maestrovs.radiocar.data.entities.Station

import kotlinx.android.synthetic.main.item_radio.view.tvName

class StationAdapter: RecyclerView.Adapter<StationAdapter.EmployeeViewHolder>(){

    inner class EmployeeViewHolder(itemView:View):RecyclerView.ViewHolder(itemView)

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
        return EmployeeViewHolder(
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

    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) {

        val item = differ.currentList[position]

        holder.itemView.apply {
           // tvName.text = "${item.employee_name}"
            //tvSalary.text = "Salary: Rs.${item.employee_salary}"
            //tvAge.text = "Age: ${item.employee_age}"

            tvName.text = "${item.name}"
        }

    }
}