package com.example.randommatrix

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.randommatrix.MatrixAdapter.GridHolder
import kotlinx.android.synthetic.main.item_view.view.*
import java.util.*

class MatrixAdapter(private val mList: MutableList<MatrixModel>) :
    RecyclerView.Adapter<GridHolder>() {
    private val randomColor = Random()
    var onItemClick: ((MutableList<MatrixModel>) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridHolder {
        return GridHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_view,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: GridHolder, position: Int) {
        if (mList[position].color != 0) {
            holder.itemView.tvNumber.text = mList[position].number.toString()
            mList[position].color?.let { holder.itemView.setBackgroundColor(it) }
        } else {
            holder.itemView.tvNumber.text = mList[position].number.toString()
            holder.itemView.setBackgroundColor(0)
        }
        holder.itemView.tvNumber.setOnClickListener {
            if (mList[position].color != 0) {
                mList[position].color = Color.argb(
                    randomColor.nextInt(255),
                    randomColor.nextInt(256),
                    randomColor.nextInt(256),
                    randomColor.nextInt(256)
                )
                notifyItemChanged(position)
                onItemClick?.invoke(mList)
            }
        }

    }

    class GridHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

}