package com.imb.newtask.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.imb.newtask.R
import com.imb.newtask.pojos.MansionsResult
import com.imb.newtask.utils.Constants.Companion.BASE_URL
import kotlinx.android.synthetic.main.mansions_list_item_view.view.*

class MansionsListAdapter(private var mList: MansionsResult) :
    RecyclerView.Adapter<MansionsListAdapter.ViewHolder>() {

    private lateinit var listener: OnMansionClickListener

    fun setMansionClickListener(listener: OnMansionClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.mansions_list_item_view,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return mList.mansions.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mansion = mList.mansions[position]
        holder.itemView.mName.text = mansion.title
        holder.itemView.mLocation.text = mansion.address
        holder.itemView.mPrice.text = "${mansion.price} so'm"
        if (mansion.images != null) {
            Glide.with(holder.itemView.context).load(BASE_URL + mansion.images[0].picture)
                .into(holder.itemView.mImage)
        } else
            Glide.with(holder.itemView.context).load(holder.itemView.resources.getDrawable(R.drawable.mansion))
                .into(holder.itemView.mImage)

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