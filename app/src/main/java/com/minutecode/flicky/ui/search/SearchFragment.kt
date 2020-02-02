package com.minutecode.flicky.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.minutecode.flicky.R
import com.minutecode.flicky.model.omdb.OmdbType

class SearchFragment : Fragment() {

    private lateinit var searchViewModel: SearchViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        searchViewModel =
                ViewModelProviders.of(this).get(SearchViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_search, container, false)
        val textView: TextView = root.findViewById(R.id.text_home)
        val retryButton: Button = root.findViewById(R.id.search_retry_button)
        val searchResultsViewManager: RecyclerView.LayoutManager = LinearLayoutManager(this.context)
        val searchResultsAdapter = SearchResultAdapter(searchViewModel.searchResults.value!!)
        val searchResultsRecyclerView: RecyclerView = root.findViewById(R.id.search_results_recycler_view)

        retryButton.setOnClickListener {
            searchViewModel.omdbSearch(title = "Lord of the rings", type = OmdbType.movie)
            searchViewModel.setSearchResults(arrayListOf())
        }

        searchResultsRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = searchResultsViewManager
            adapter = searchResultsAdapter
        }
        searchViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        searchViewModel.searchResults.observe(viewLifecycleOwner, Observer {
            if (it.size > 0) {
                textView.visibility = View.INVISIBLE
                searchResultsAdapter.setDataSet(it)
                searchResultsAdapter.notifyDataSetChanged()
            } else {
                textView.visibility = View.VISIBLE
            }
        })

        searchViewModel.omdbSearch(title = "Lord of the rings", type = OmdbType.movie)

        return root
    }
}