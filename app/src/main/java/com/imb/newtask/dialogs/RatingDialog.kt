package com.imb.newtask.dialogs

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.RatingBar
import com.imb.newtask.R
import kotlinx.android.synthetic.main.dialog_rating_mansion.*

class RatingDialog(context: Context?, rateDialog: RateMansion) : AlertDialog(context),
    RatingBar.OnRatingBarChangeListener {

    private var rateDialog: RateMansion
    private var rating = 0f


    init {
        val v = LayoutInflater.from(context).inflate(R.layout.dialog_rating_mansion, null, false)
        this.rateDialog = rateDialog
        setView(v)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ratingBar.onRatingBarChangeListener = this

        notNowBtn.setOnClickListener {
            rateDialog.notNowBtnClicked()
            dismiss()
        }

        rateBtn.setOnClickListener {
            rateDialog.rateMansion(rating)
            dismiss()
        }


    }

    override fun onRatingChanged(ratingBar: RatingBar?, rating: Float, fromUser: Boolean) {
        this.rating = rating
    }

    interface RateMansion {

        fun rateMansion(rating: Float)

        fun notNowBtnClicked()
    }
}