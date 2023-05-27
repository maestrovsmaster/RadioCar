package com.maestrovs.radiocar.ui.radio

import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.maestrovs.radiocar.R
import com.maestrovs.radiocar.data.entities.radio.Station
import com.maestrovs.radiocar.enums.radio.PlayState
import com.maestrovs.radiocar.extensions.addRipple
import com.maestrovs.radiocar.extensions.setVisible
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_radio.view.btFavorite
import kotlinx.android.synthetic.main.item_radio.view.ivCover
import kotlinx.android.synthetic.main.item_radio.view.root
import kotlinx.android.synthetic.main.item_radio.view.tvName


class StationAdapter(val onItem: ItemListener) : RecyclerView.Adapter<StationAdapter.StationViewHolder>()
   // PagingDataAdapter<Station, StationAdapter.StationViewHolder>(diffCallback)
{

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<Station>() {
            override fun areItemsTheSame(oldItem: Station, newItem: Station): Boolean {
                return oldItem.stationuuid == newItem.stationuuid
            }

            override fun areContentsTheSame(oldItem: Station, newItem: Station): Boolean {
                return oldItem == newItem
            }
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)


    private var stationEvent: StationEvent? = null

    interface ItemListener {
        fun onClickedCharacter(item: Station?)
    }

    inner class StationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)


    fun submitList(list: List<Station>) {
        differ.submitList(list)
    }

    /**
     * Set station event about last selected Station
     */
    fun setStationEvent(stationEvent: StationEvent) {
        this.stationEvent = stationEvent
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

            var favoriteImgRes = R.drawable.ic_empty_24

            root.setOnClickListener {

                onItem.onClickedCharacter(item)

            }

            item.isFavorite?.let {
                if(it == 1) {
                    favoriteImgRes = R.drawable.ic_favorite
                }
            }
            btFavorite.setImageDrawable(ContextCompat.getDrawable(context, favoriteImgRes))


            var selected = false
            var selectedColor = ContextCompat.getColor(context, R.color.transparent)

            var drawPlayingAnim = false

            stationEvent?.let { event ->
                event.station?.let { selectedStation ->
                    if (selectedStation.stationuuid == item.stationuuid) {
                        selected = true
                        selectedColor = when (event.playState) {
                            PlayState.Play -> ContextCompat.getColor(context, R.color.pink)
                            PlayState.Stop -> ContextCompat.getColor(context, R.color.pink_gray)
                        }
                        drawPlayingAnim = when (event.playState) {
                            PlayState.Play -> true
                            PlayState.Stop -> false
                        }
                    }
                }
            }



            if (selected) {
                //root.setBackgroundColor(selectedColor)

                root.background = ContextCompat.getDrawable(context, R.drawable.ripple_stroke_white)
            } else {
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

           // animationView.setVisible(drawPlayingAnim)

            var imgUrl: String? = null

            Log.d("Picasso", "icon = ${item.favicon}")
            item.favicon.let { icon ->
                if (!icon.isNullOrEmpty()) {
                    imgUrl = item.favicon
                }
            }
            if (imgUrl != null) {

                Picasso.get()
                    .load(imgUrl)
                    .fit()
                    .centerCrop()
                    .into(ivCover)
            } else {
                Picasso.get()
                    .load(R.drawable.bg_music)
                    .resize(120, 120)
                    .centerCrop()
                    .into(ivCover)
            }

        }

    }


}
