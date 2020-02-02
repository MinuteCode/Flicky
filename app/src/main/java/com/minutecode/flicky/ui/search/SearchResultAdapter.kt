package com.minutecode.flicky.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.minutecode.flicky.R
import com.minutecode.flicky.model.omdb.Movie

class SearchResultAdapter(private var dataSet: ArrayList<Movie>): RecyclerView.Adapter<SearchResultAdapter.SearchResultViewHolder>() {
    class SearchResultViewHolder(searchResultView: View): RecyclerView.ViewHolder(searchResultView) {
        lateinit var resultTitle: TextView
        lateinit var resultYear: TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder {
        val rootLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.search_result_line,parent, false)
        val searchResultLine = SearchResultViewHolder(rootLayout)

        searchResultLine.resultTitle = rootLayout.findViewById(R.id.result_title)
        searchResultLine.resultYear = rootLayout.findViewById(R.id.result_year)

        return searchResultLine
    }

    override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
        holder.resultTitle.text = dataSet[position].title
        holder.resultYear.text = dataSet[position].year.toString()
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    public fun setDataSet(to: ArrayList<Movie>) {
        dataSet = to
    }
}