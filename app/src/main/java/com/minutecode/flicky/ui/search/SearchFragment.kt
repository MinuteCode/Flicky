package com.minutecode.flicky.ui.search

import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.minutecode.flicky.R
import com.minutecode.flicky.model.omdb.Movie
import com.minutecode.flicky.model.omdb.OmdbType
import com.minutecode.flicky.ui.result_detail.ResultDetailActivity

class SearchFragment : Fragment() {

    private lateinit var searchViewModel: SearchViewModel
    private lateinit var searchResultsRecyclerView: RecyclerView
    private lateinit var searchProgress: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        searchViewModel = ViewModelProvider(this).get(SearchViewModel::class.java)

        setHasOptionsMenu(true)

        val root = inflater.inflate(R.layout.fragment_search, container, false)
        val textView: TextView = root.findViewById(R.id.text_home)
        val retryButton: Button = root.findViewById(R.id.search_retry_button)
        val searchResultsViewManager: RecyclerView.LayoutManager = LinearLayoutManager(this.context)
        val searchResultsAdapter = SearchResultAdapter(searchViewModel.searchResults.value!!)
        searchResultsRecyclerView = root.findViewById(R.id.search_results_recycler_view)
        searchProgress = root.findViewById(R.id.search_loader)
        searchProgress.visibility = View.INVISIBLE

        retryButton.visibility = View.GONE
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
            if (it.isNotEmpty()) {
                textView.visibility = View.INVISIBLE
                searchResultsAdapter.setDataSet(it)
                searchResultsAdapter.notifyDataSetChanged()
            } else {
                textView.visibility = View.VISIBLE
            }
            searchProgress.visibility = View.INVISIBLE
        })

        searchViewModel.setSearchListener(object: SearchListener {
            override fun searchFailure() {
                retryButton.visibility = View.VISIBLE
            }

            override fun searchSuccess(results: ArrayList<Movie>) {
                Log.d("Search", "Result count: ${results.size}")
            }
        })

        searchResultsAdapter.setClickListener(object: OnResultClickListener {
            override fun resultClick(dataset: List<Movie>, position: Int) {
                val detailIntent = Intent(context, ResultDetailActivity::class.java).apply {
                    putExtra("movie", dataset[position])
                }
                startActivity(detailIntent)
            }
        })

        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_fragment_menu, menu)
        val searchManager = activity?.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.search).actionView as SearchView
        searchView.apply {
            setSearchableInfo(searchManager.getSearchableInfo(activity?.componentName))
            setIconifiedByDefault(true)
            isSubmitButtonEnabled = true

            setOnQueryTextListener(object: SearchView.OnQueryTextListener {
                override fun onQueryTextChange(newText: String?): Boolean {
                    return true
                }

                override fun onQueryTextSubmit(query: String?): Boolean {
                    val inputMethodManager = activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(searchView.windowToken, 0)
                    searchProgress.visibility = View.VISIBLE
                    query?.let {
                        searchViewModel.omdbSearch(title = it, type = OmdbType.movie)
                    }
                    return true
                }
            })
        }
    }
}
