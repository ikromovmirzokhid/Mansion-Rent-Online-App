package com.imb.newtask.adapters

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.imb.newtask.R
import com.imb.newtask.pojos.MansionsResult
import com.imb.newtask.utils.Constants.Companion.BASE_URL
import com.imb.newtask.utils.Converter
import kotlinx.android.synthetic.main.mansions_list_item_view.view.*
import kotlinx.android.synthetic.main.mansions_list_item_view.view.mName
import kotlinx.android.synthetic.main.mansions_result_list_item_view.view.*
import kotlinx.android.synthetic.main.searched_mansions_list_item.view.*

class SearchedMansionsListAdapter(private var mList: MansionsResult, private var nights: Int) :
    RecyclerView.Adapter<SearchedMansionsListAdapter.ViewHolder>() {

    private lateinit var listener: OnMansionClickListener

    fun setMansionClickListener(listener: OnMansionClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.mansions_result_list_item_view,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
//        return if (mList.count < 10)
//            mList.count
//        else
//            10
        return mList.mansions.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mansion = mList.mansions[position]
        holder.itemView.mName.text = mansion.title
        holder.itemView.mLocationText.text = "${mansion.address}, ${mansion.region.title}"
        holder.itemView.otherDetailsText.text = "$nights-night stay: UZS ${mansion.price}"

        holder.itemView.ratingBar.rating = mansion.rates.avgRate.toFloat()

        if (mansion.images != null) {
            Glide.with(holder.itemView.context).load(BASE_URL + mansion.images[0].picture)
                .into(holder.itemView.manImage)
        } else
            Glide.with(holder.itemView.context).load(holder.itemView.resources.getDrawable(R.drawable.mansion))
                .into(holder.itemView.manImage)

        val mansionJson = Gson().toJson(Converter.convertToRecentSearchedMansion(mansion))
        val bundle = Bundle()
        bundle.putString("mansion", mansionJson)
        holder.itemView.setOnClickListener {
            listener.onMansionClickListener(bundle)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    interface OnMansionClickListener {
        fun onMansionClickListener(bundle: Bundle)
    }
}