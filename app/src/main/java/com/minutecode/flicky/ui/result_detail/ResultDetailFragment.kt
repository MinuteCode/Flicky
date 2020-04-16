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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.minutecode.flicky.R
import com.minutecode.flicky.model.omdb.Movie

class ResultDetailFragment : Fragment() {

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
            R.id.add_movie_library -> {
                Log.d("Add movie to library", "${viewModel.movie}")
                Firebase.firestore
                    .collection("Movies")
                    .add(viewModel.movie.asHashMap())
                    .addOnSuccessListener { docRef ->
                        Log.d("Result Detail", "DocumentSnapshot added with ID: ${docRef.id}")
                    }
                    .addOnFailureListener { e ->
                        Log.w("Result Detail", "Error adding document", e)
                    }
            }
        }
        return true
    }
}
