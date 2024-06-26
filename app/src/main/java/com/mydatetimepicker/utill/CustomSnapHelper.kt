package com.mydatetimepicker.utill

import android.graphics.Typeface
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView

class CustomSnapHelper (
    private val rv: RecyclerView,
    private val snapListener: SnapListener
) : LinearSnapHelper() {

    init {
        this.attachToRecyclerView(rv)
    }

    private var selectedPosition = -1

    private var lastSelectedView: TextView? = null

    override fun findSnapView(layoutManager: RecyclerView.LayoutManager?): View? {
        val view = super.findSnapView(layoutManager)

        if (view != null) {
            val newPosition = layoutManager!!.getPosition(view)
            if (newPosition != selectedPosition && rv.scrollState == RecyclerView.SCROLL_STATE_IDLE) {
                selectedPosition = newPosition
                snapListener.onViewSnapped(newPosition)

                setFont(newPosition)
            }
        }

        return view
    }

    private fun setFont(newPosition: Int) {
        if (lastSelectedView != null) {
            lastSelectedView?.typeface = Typeface.DEFAULT
        }

        lastSelectedView =
            (rv.findViewHolderForAdapterPosition(newPosition) as DateViewHolder).binding.tv
        lastSelectedView?.typeface = Typeface.DEFAULT_BOLD
    }


    interface SnapListener {
        fun onViewSnapped(position: Int)
    }
}