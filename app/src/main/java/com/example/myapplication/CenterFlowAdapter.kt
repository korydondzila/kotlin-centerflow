package com.example.myapplication

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.core.view.updatePadding
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.NumberBallItemBinding
import com.example.myapplication.utilities.toPx
import com.google.android.flexbox.FlexboxItemDecoration
import kotlin.math.ceil
import kotlin.math.floor

class CenterFlowAdapter(
    private val dataBindingComponent: DataBindingComponent,
    appExecutors: AppExecutors,
    private val recyclerView: RecyclerView,
    private val cellSize: Int,
    private val interItemSpacing: Int,
    private val lineSpacing: Int
) : DataBoundListAdapter<Int>(
    appExecutors = appExecutors,
    diffCallback = object: DiffUtil.ItemCallback<Int>() {
        override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean {
            return oldItem == newItem
        }
    }
) {
    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.number_ball_item, parent, false, dataBindingComponent)
    }

    override fun bind(binding: ViewDataBinding, item: Int) {
        (binding as? NumberBallItemBinding)?.let {
            binding.number = item.toString()
        }
    }

    fun setPadding(numberOfItems: Int) {
        if (recyclerView.itemDecorationCount == 0) {
            val divider = GradientDrawable()
            divider.setSize(interItemSpacing.toPx(), lineSpacing.toPx())
            val decoration = FlexboxItemDecoration(recyclerView.context)
            decoration.setDrawable(divider)
            decoration.setOrientation(FlexboxItemDecoration.BOTH)
            recyclerView.addItemDecoration(decoration)
        }

        val width = recyclerView.width

        if (width > 0) {
            computAndUpdatePadding(numberOfItems, width)
        } else {
            val vto = recyclerView.viewTreeObserver
            vto.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    recyclerView.viewTreeObserver.removeOnPreDrawListener(this)
                    computAndUpdatePadding(numberOfItems, recyclerView.width)
                    return true
                }
            })
        }
    }

    private fun findMaxColumns(numItems: Double, cellsPerRow: Double): Double {
        val columns = floor(numItems / ceil(numItems / cellsPerRow))
        val rows = numItems / columns

        if (rows > rows.toInt()) {
            return findMaxColumns(numItems, columns)
        }

        return columns
    }

    private fun computAndUpdatePadding(numberOfItems: Int, width: Int) {
        var cellsPerRow = (width + interItemSpacing.toPx()).toDouble() / (cellSize.toPx() + interItemSpacing.toPx()).toDouble()
        val columns = findMaxColumns(numberOfItems.toDouble(), cellsPerRow)
        cellsPerRow = if (columns == 1.0) ceil(cellsPerRow / 2) else columns

        val remainingWidth = width - cellsPerRow * (cellSize.toPx() + interItemSpacing.toPx())
        val padding = (remainingWidth / 2).toInt()
        recyclerView.updatePadding(left = padding, right = padding)
        recyclerView.invalidateItemDecorations()
    }
}
