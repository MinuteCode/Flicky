package com.minutecode.flicky.ui.result_detail

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.github.kittinunf.fuel.core.FuelError
import com.minutecode.flicky.R
import com.minutecode.flicky.model.omdb.Movie

class ResultDetailFragment : Fragment() {

    private val TAG = "ResultDetailFragment"

    companion object {
        fun newInstance(arguments: Bundle): Fragment {
            val fragment = ResultDetailFragment()
            fragment.arguments = arguments
            return fragment
        }
    }

    private lateinit var viewModel: ResultDetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val movie = arguments!!.getParcelable<Movie>("movie")
        viewModel = ViewModelProvider(this, ResultViewModelFactory(movie!!)).get(ResultDetailViewModel::class.java)
        viewModel.setListener(object: ResultDetailListener {
            override fun detailRetrieveSuccessful() {
                Log.d(TAG, "")
            }

            override fun detailRetrieveFailure(error: FuelError) {
                Log.d(TAG, "")
            }

            override fun addToLibrarySuccessful() {
                Log.d(TAG, "")
            }

            override fun addToLibraryFailure(exception: Exception) {
                Log.d(TAG, "")
            }
        })
        setHasOptionsMenu(true)

        val root = inflater.inflate(R.layout.result_detail_fragment, container, false)
        val movieTitle: TextView = root.findViewById(R.id.movie_title)
        val movieYear: TextView = root.findViewById(R.id.movie_year)
        val moviePoster: ImageView = root.findViewById(R.id.movie_poster)
        val moviePlot: TextView = root.findViewById(R.id.movie_plot)

        viewModel.movieTitle.observe(viewLifecycleOwner, Observer {
            movieTitle.text = it
        })
        viewModel.moviePoster.observe(viewLifecycleOwner, Observer {
            Glide.with(this)
                .load(it)
                .fallback(R.drawable.ic_broken_image_black_24dp)
                .centerCrop()
                .into(moviePoster)
        })
        viewModel.movieYear.observe(viewLifecycleOwner, Observer {
            movieYear.text = String.format("%d", it)
        })
        viewModel.moviePlot.observe(viewLifecycleOwner, Observer {
            moviePlot.text = it
        })

        viewModel.retrieveMovieDetail()
        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.result_detail_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_movie_library -> viewModel.addResultToLibrary()
        }
        return true
    }
}
