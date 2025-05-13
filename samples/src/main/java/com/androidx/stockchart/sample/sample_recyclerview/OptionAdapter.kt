package com.androidx.stockchart.sample.sample_recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.androidx.stockchart.sample.R

class OptionAdapter : RecyclerView.Adapter<OptionAdapter.ViewHolder>() {

    private val items = mutableListOf<OptionItem>()

    fun submitList(newItems: List<OptionItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvLeft1: TextView = view.findViewById(R.id.tvLeft1)
        val tvLeft2: TextView = view.findViewById(R.id.tvLeft2)
        val tvCenter: TextView = view.findViewById(R.id.tvCenter)
        val tvRight1: TextView = view.findViewById(R.id.tvRight1)
        val tvRight2: TextView = view.findViewById(R.id.tvRight2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recycler, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        with(holder) {
            tvLeft1.text = item.left1
            tvLeft2.text = item.left2
            tvCenter.text = item.center
            tvRight1.text = item.right1
            tvRight2.text = item.right2
        }
    }

    override fun getItemCount() = items.size
}