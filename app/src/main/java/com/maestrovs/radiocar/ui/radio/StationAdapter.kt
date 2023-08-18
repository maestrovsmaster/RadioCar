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
import com.maestrovs.radiocar.enums.radio.PlayAction
import com.maestrovs.radiocar.extensions.addRipple
import com.maestrovs.radiocar.extensions.setVisible
import com.murgupluoglu.flagkit.FlagKit
import com.squareup.picasso.Picasso

import kotlinx.android.synthetic.main.item_radio.view.btFavorite
import kotlinx.android.synthetic.main.item_radio.view.ivCountry
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


    private var station: Station? = null
    private var playAction: PlayAction? = null

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
    fun setStation(station: Station) {
        this.station = station
        notifyDataSetChanged()
    }


    /**
     * Set station event about last selected Station
     */
    fun setPlayAction( playAction: PlayAction) {
        this.playAction = playAction
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

            var favoriteImgRes = R.drawable.ic_empty_24

            root.setOnClickListener {
                onItem.onClickedCharacter(item)
            }

           // Log.d("RadioCountry","country = ${item.country}   code = ${item.countrycode}  name = ${item.name}")

            val flagId =  FlagKit.getResId(context, item.countrycode)
            ivCountry.setImageResource(flagId)

            ivCountry.setVisible(!item.countrycode.isNullOrEmpty())

           // val drawable = FlagKit.getDrawable(this, "tr")

            item.isFavorite?.let {
                if(it == 1) {
                    favoriteImgRes = R.drawable.ic_favorite
                }
            }
            btFavorite.setImageDrawable(ContextCompat.getDrawable(context, favoriteImgRes))


            var selected = false
            var selectedColor = ContextCompat.getColor(context, R.color.transparent)

            var drawPlayingAnim = false

            var loadSuccess = true

            station?.let {  selectedStation ->
                    if (selectedStation.stationuuid == item.stationuuid) {
                        selected = true

                        playAction?.let { playAction ->
                            Log.d("Station", "+++2 $playAction")
                            selectedColor = if(playAction is PlayAction.Resume) {
                                ContextCompat.getColor(context, R.color.pink)
                            }else{
                                ContextCompat.getColor(context, R.color.pink_gray)
                            }

                            loadSuccess = !(playAction is PlayAction.Error)

                            drawPlayingAnim = playAction is PlayAction.Resume
                        }
                    }
            }

            if (selected) {
               // root.setBackgroundColor(selectedColor)

                root.background =
                    if(loadSuccess) {
                        ContextCompat.getDrawable(context, R.drawable.ripple_stroke_white)
                    }else{
                        ContextCompat.getDrawable(context, R.drawable.ripple_yellow_err)
                    }
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

            //Log.d("Picasso", "icon = ${item.favicon}")
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
