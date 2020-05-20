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
import com.imb.newtask.room.RecentSearchedMansion
import com.imb.newtask.utils.Constants.Companion.BASE_URL
import com.imb.newtask.utils.Converter
import kotlinx.android.synthetic.main.mansions_list_item_view.view.*
import kotlinx.android.synthetic.main.mansions_list_item_view.view.mName
import kotlinx.android.synthetic.main.mansions_result_list_item_view.view.*
import kotlinx.android.synthetic.main.searched_mansions_list_item.view.*

class RecentSearchedMansionsListAdapter(private var mList: List<RecentSearchedMansion>) :
    RecyclerView.Adapter<RecentSearchedMansionsListAdapter.ViewHolder>() {

    private lateinit var listener: RecentSearchedMansionsListAdapter.OnMansionClickListener

    fun setMansionClickListener(listener: RecentSearchedMansionsListAdapter.OnMansionClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.searched_mansions_list_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {

        return mList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mansion = mList[position]
        holder.itemView.mansionName.text = mansion.title
        holder.itemView.addressText.text = "${mansion.address}, ${mansion.region.title}"

        if (mansion.image != "") {
            Glide.with(holder.itemView.context).load(BASE_URL + mansion.image)
                .into(holder.itemView.mansionImage)
        } else
            Glide.with(holder.itemView.context).load(holder.itemView.resources.getDrawable(R.drawable.mansion))
                .into(holder.itemView.mansionImage)

        val mansionJson = Gson().toJson(mansion)
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