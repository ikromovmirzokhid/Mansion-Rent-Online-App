package com.imb.newtask.adapters

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.imb.newtask.R
import com.imb.newtask.activities.MainActivity
import com.imb.newtask.fragments.FavoriteMansionsFragment
import com.imb.newtask.pojos.MansionsResult
import com.imb.newtask.room.FavoriteMansion
import com.imb.newtask.room.RecentSearchedMansion
import com.imb.newtask.utils.Constants.Companion.BASE_URL
import com.imb.newtask.utils.Converter
import com.imb.newtask.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.mansions_list_item_view.view.*
import kotlinx.android.synthetic.main.mansions_list_item_view.view.mName
import kotlinx.android.synthetic.main.mansions_result_list_item_view.view.*
import kotlinx.android.synthetic.main.searched_mansions_list_item.view.*

class FavoriteMansionsListAdapter(
    private var mList: List<FavoriteMansion>,
    private var nights: Int,
    private var context: FavoriteMansionsFragment
) :
    RecyclerView.Adapter<FavoriteMansionsListAdapter.ViewHolder>() {

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

        return mList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val userViewModel = ViewModelProvider(context).get(UserViewModel::class.java)

        val mansion = mList[position]
        holder.itemView.mName.text = mansion.title
        holder.itemView.mLocationText.text = "${mansion.address}, ${mansion.region.title}"
        holder.itemView.otherDetailsText.text = "$nights-night stay: UZS ${mansion.price}"

        holder.itemView.likeBtn.visibility = View.VISIBLE
        Glide.with(holder.itemView.context)
            .load(holder.itemView.resources.getDrawable(R.drawable.ic_favorite_red_24dp))
            .into(holder.itemView.likeBtn)

        holder.itemView.ratingBar.rating = mansion.rates.avgRate.toFloat()

        if (mansion.images != "") {
            Glide.with(holder.itemView.context).load(BASE_URL + mansion.images)
                .into(holder.itemView.manImage)
        } else
            Glide.with(holder.itemView.context).load(holder.itemView.resources.getDrawable(R.drawable.mansion))
                .into(holder.itemView.manImage)

        holder.itemView.likeBtn.setOnClickListener {
            Glide.with(holder.itemView.context)
                .load(holder.itemView.resources.getDrawable(R.drawable.ic_favorite_border_black_24dp))
                .into(holder.itemView.likeBtn)
            userViewModel.deleteMansionFromFavoriteDB(mansion)

            userViewModel.getAllFavoriteMansions().observe(context, Observer {
                mList = it
                notifyItemChanged(position)
            })
        }

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