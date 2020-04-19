package com.minutecode.flicky.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.minutecode.flicky.R
import com.minutecode.flicky.model.omdb.Movie

class SearchResultAdapter(private var dataSet: ArrayList<Movie>): RecyclerView.Adapter<SearchResultAdapter.SearchResultViewHolder>() {
    private lateinit var clickListener: OnResultClickListener

    class SearchResultViewHolder(searchResultView: View): RecyclerView.ViewHolder(searchResultView) {
        lateinit var resultTitle: TextView
        lateinit var resultYear: TextView
        lateinit var resultPoster: ImageView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder {
        val rootLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.search_result_line,parent, false)
        val searchResultLine = SearchResultViewHolder(rootLayout)

        searchResultLine.resultTitle = rootLayout.findViewById(R.id.result_title)
        searchResultLine.resultYear = rootLayout.findViewById(R.id.result_year)
        searchResultLine.resultPoster = rootLayout.findViewById(R.id.result_image)

        return searchResultLine
    }

    override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
        holder.resultTitle.text = dataSet[position].title
        holder.resultYear.text = dataSet[position].year.toString()
        holder.resultPoster.scaleType = ImageView.ScaleType.CENTER_CROP
        Glide.with(holder.itemView)
            .load(dataSet[position].poster)
            .fallback(R.drawable.ic_broken_image_black_24dp)
            .centerInside()
            .into(holder.resultPoster)

        holder.itemView.setOnClickListener {
            clickListener.resultClick(dataSet, position, holder.itemView)
        }
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    fun setDataSet(to: ArrayList<Movie>) {
        dataSet = to
        notifyDataSetChanged()
    }

    fun setClickListener(listener: OnResultClickListener) {
        this.clickListener = listener
    }
}

interface OnResultClickListener {
    fun resultClick(dataset: List<Movie>, position: Int, view: View)
}