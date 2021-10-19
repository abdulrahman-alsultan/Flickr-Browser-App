package com.example.flickrbrowserapp

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.images_grid_view.view.*

class SearchGridView(private val images: List<ImageData>, private val ctx: MainActivity): BaseAdapter() {
    override fun getCount(): Int = images.size

    override fun getItem(p0: Int): Any {
        return p0
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val image = images[p0]

        val inflater: LayoutInflater = ctx!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val appView = inflater.inflate(R.layout.images_grid_view, null)

        Glide.with(appView.context).load(image.url).into(appView.search_grid_view_IV_image)
        appView.search_grid_view_tv_title.text = image.title

        appView.setOnClickListener{
            ctx.openImage(image.url)
        }

        return appView
    }
}