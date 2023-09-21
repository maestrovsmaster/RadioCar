package com.maestrovs.radiocar.ui.settings.logos_fragment

import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.maestrovs.radiocar.R
import com.squareup.picasso.Picasso

class ImagesGridAdapter(private val images: List<Int>, val onClick: (Int?)->(Unit)) :
    RecyclerView.Adapter<ImagesGridAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
        val btLogo = view.findViewById<View>(R.id.btLogo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_logo, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = images.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val image = images[position]
        holder.imageView.setImageResource(image)
        holder.btLogo.setOnClickListener { onClick(
            if(position == 0){ null } else {
                images[position]
            }
        ) }
    }
}