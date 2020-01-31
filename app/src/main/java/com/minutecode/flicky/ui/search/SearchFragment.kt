package com.minutecode.flicky.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
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
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val textView: TextView = root.findViewById(R.id.text_home)
        searchViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

        searchViewModel.omdbSearch(title = "Lord of the rings", type = OmdbType.movie)

        return root
    }
}