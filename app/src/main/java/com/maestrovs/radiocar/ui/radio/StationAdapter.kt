package com.maestrovs.radiocar.ui.radio

import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.maestrovs.radiocar.R
import com.maestrovs.radiocar.data.entities.radio.Station
import com.maestrovs.radiocar.databinding.ItemRadioBinding
import com.maestrovs.radiocar.enums.radio.PlayAction
import com.maestrovs.radiocar.extensions.addRipple
import com.maestrovs.radiocar.extensions.setVisible
import com.murgupluoglu.flagkit.FlagKit
import com.squareup.picasso.Picasso
//import kotlinx.android.synthetic.main.item_radio.view.btFavorite
//import kotlinx.android.synthetic.main.item_radio.view.ivCountry
//import kotlinx.android.synthetic.main.item_radio.view.ivCover
//import kotlinx.android.synthetic.main.item_radio.view.root
//import kotlinx.android.synthetic.main.item_radio.view.tvName


class StationAdapter(val onItem: ItemListener) :
    RecyclerView.Adapter<StationAdapter.StationViewHolder>() {

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
    private var recyclerView: RecyclerView? = null

    interface ItemListener {
        fun onClickedItem(station: Station?, mustUpdateList: Boolean)
        fun onLongClickedItem(station: Station?)
    }

    inner class StationViewHolder(val binding: ItemRadioBinding) : RecyclerView.ViewHolder(binding.root)

    fun submitList(list: List<Station>) {
        differ.submitList(list)
    }

    fun setStation(station: Station) {
        this.station = station
        notifyDataSetChanged()
    }

    fun setPlayAction(playAction: PlayAction) {
        this.playAction = playAction
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StationViewHolder {
        val binding = ItemRadioBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StationViewHolder(binding)
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: StationViewHolder, position: Int) {
        val item = differ.currentList[position]
        val binding = holder.binding

        binding.root.setOnClickListener { onItem.onClickedItem(item, true) }
        binding.root.setOnLongClickListener {
            onItem.onLongClickedItem(item)
            false
        }

        val flagId = FlagKit.getResId(binding.root.context, item.countrycode)
        binding.ivCountry.setImageResource(flagId)
        binding.ivCountry.isVisible = !item.countrycode.isNullOrEmpty()

        val favoriteImgRes = if (item.isFavorite == 1) R.drawable.ic_favorite else R.drawable.ic_empty_24
        binding.btFavorite.setImageDrawable(ContextCompat.getDrawable(binding.root.context, favoriteImgRes))

        var selected = false
        var drawPlayingAnim = false
        var loadSuccess = true

        station?.let { selectedStation ->
            if (selectedStation.stationuuid == item.stationuuid) {
                selected = true
                playAction?.let { playAction ->
                    loadSuccess = playAction !is PlayAction.Error
                    drawPlayingAnim = playAction is PlayAction.Resume
                }
            }
        }

        binding.root.background = if (selected) {
            ContextCompat.getDrawable(binding.root.context, R.drawable.ripple_stroke_white)
        } else {
            with(TypedValue()) {
                binding.root.context.theme.resolveAttribute(
                    android.R.attr.selectableItemBackground, this, true
                )
                ContextCompat.getDrawable(binding.root.context, resourceId)
            }
        }

        binding.tvName.text = item.name
       // binding.animationView.isVisible = drawPlayingAnim

        val imgUrl = item.favicon.takeIf { !it.isNullOrEmpty() }
        if (imgUrl != null) {
            Picasso.get().load(imgUrl).fit().centerCrop().into(binding.ivCover)
            binding.ivCover.scaleType = ImageView.ScaleType.CENTER_INSIDE
        } else {
            binding.ivCover.setImageDrawable(AppCompatResources.getDrawable(binding.root.context, R.drawable.ic_podcasts))
            binding.ivCover.scaleType = ImageView.ScaleType.CENTER_INSIDE
        }
    }

    fun setRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = recyclerView
    }

    fun nextStation(currentStation: Station?) {
        if (differ.currentList.isEmpty()) return
        currentStation?.let { station ->
            val currentPosition = differ.currentList.indexOf(station)
            val nextPosition = if (currentPosition == -1) 0 else (currentPosition + 1) % differ.currentList.size
            val nextStation = differ.currentList[nextPosition]
            onItem.onClickedItem(nextStation, false)
            recyclerView?.scrollToPosition(nextPosition)
        }
    }

    fun previousStation(currentStation: Station?) {
        if (differ.currentList.isEmpty()) return
        currentStation?.let { station ->
            val currentPosition = differ.currentList.indexOf(station)
            val previousPosition = if (currentPosition > 0) currentPosition - 1 else differ.currentList.size - 1
            val previousStation = differ.currentList[previousPosition]
            onItem.onClickedItem(previousStation, false)
            recyclerView?.scrollToPosition(previousPosition)
        }
    }
}
