package com.minutecode.flicky.ui.search

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.minutecode.flicky.R
import com.minutecode.flicky.model.omdb.Movie
import com.minutecode.flicky.ui.result_detail.ResultDetailActivity

class SearchResultAdapter(private var dataSet: ArrayList<Movie>): RecyclerView.Adapter<SearchResultAdapter.SearchResultViewHolder>() {
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
            val detailIntent = Intent(holder.itemView.context, ResultDetailActivity::class.java).apply {
                putExtra("movie", dataSet[position])
            }
            holder.itemView.context.startActivity(detailIntent)
        }
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    fun setDataSet(to: ArrayList<Movie>) {
        dataSet = to
    }
}