package com.datn.cookingguideapp.utils

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import coil.load
import coil.transform.CircleCropTransformation
import com.datn.cookingguideapp.R
import com.google.android.material.card.MaterialCardView

class CircleImageView(context: Context, attrRes: AttributeSet?) : FrameLayout(context, attrRes) {
    private val cardView: MaterialCardView = LayoutInflater.from(context)
        .inflate(R.layout.layout_circle_image, null, false) as MaterialCardView
    private val layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)

    init {
        cardView.strokeColor = Color.rgb(191, 168, 168)
        cardView.strokeWidth = 1
        setBackgroundColor(Color.TRANSPARENT)
        addView(cardView)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        layoutParams.width = widthMeasureSpec - 20
        layoutParams.height = heightMeasureSpec - 20
        layoutParams.setMargins(10, 10, 10, 10)
        cardView.layoutParams = layoutParams
        cardView.radius = widthMeasureSpec / 2f
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    fun loadUrl(url: String?) {
        cardView.findViewById<ImageView>(R.id.image_view).load(url) {
            crossfade(true)
            placeholder(R.drawable.avatar_default)
            error(R.drawable.avatar_default)
            transformations(CircleCropTransformation())
        }
    }

    fun setCardElevation(elevation: Float) {
        cardView.cardElevation = elevation
    }
}
