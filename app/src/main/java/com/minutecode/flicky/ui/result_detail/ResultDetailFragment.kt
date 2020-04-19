package com.minutecode.flicky.ui.result_detail

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.palette.graphics.Palette
import androidx.palette.graphics.Target
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.github.kittinunf.fuel.core.FuelError
import com.google.android.material.snackbar.Snackbar
import com.minutecode.flicky.R
import com.minutecode.flicky.model.omdb.Movie

class ResultDetailFragment : Fragment() {

    private val TAG = "ResultDetailFragment"
    private var canAddMovieToLibrary = true

    companion object {
        fun newInstance(arguments: Bundle): Fragment {
            val fragment = ResultDetailFragment()
            fragment.arguments = arguments
            return fragment
        }
    }

    private lateinit var viewModel: ResultDetailViewModel

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val movie = arguments!!.getParcelable<Movie>("movie")
        viewModel = ViewModelProvider(this, ResultViewModelFactory(movie!!)).get(ResultDetailViewModel::class.java)

        val root = inflater.inflate(R.layout.result_detail_fragment, container, false)
        val movieTitle: TextView = root.findViewById(R.id.movie_title)
        val movieYear: TextView = root.findViewById(R.id.movie_year)
        val moviePoster: ImageView = root.findViewById(R.id.movie_poster)
        val moviePlot: TextView = root.findViewById(R.id.movie_plot)
        val genreLayout: LinearLayout = root.findViewById(R.id.genre_layout)

        viewModel.setListener(object: ResultDetailListener {
            override fun detailRetrieveSuccessful() {
                Log.d(TAG, "")
            }

            override fun detailRetrieveFailure(error: FuelError) {
                Log.d(TAG, "")
            }

            override fun addToLibrarySuccessful() {
                Snackbar
                    .make(root, getText(R.string.add_library_success), Snackbar.LENGTH_SHORT)
                    .setAction(getText(R.string.undo)) {}
                    .show()
            }

            override fun addToLibraryFailure(exception: Exception) {
                Log.d(TAG, "")
            }
        })
        setHasOptionsMenu(true)

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
        viewModel.movieGenre.observe(viewLifecycleOwner, Observer {
            genreLayout.removeAllViews()
            val genreTitle = TextView(this.activity)
            genreTitle.text = resources.getQuantityString(R.plurals.number_of_genres, it.size) + " :"
            genreLayout.addView(genreTitle)

            for (genre: String in it) {
                val genreTextView = TextView(this.activity)
                genreTextView.setPadding(8,0,0,0)
                genreTextView.text = genre
                genreLayout.addView(genreTextView)
            }
        })

        viewModel.retrieveMovieDetail()
        viewModel.retrieveUserDocPath()
        return root
    }

    override fun onStart() {
        super.onStart()
        Glide.with(this)
            .asBitmap()
            .load(viewModel.movie.poster)
            .into(object: CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    val palette = Palette.from(resource).generate()
                    activity!!.window.statusBarColor = palette.getColorForTarget(
                        Target.DARK_VIBRANT,
                        ResourcesCompat.getColor(resources, R.color.colorPrimary, null))
                    (activity as AppCompatActivity).supportActionBar!!.setBackgroundDrawable(
                        ColorDrawable(palette.getColorForTarget(
                            Target.DARK_VIBRANT,
                            ResourcesCompat.getColor(resources, R.color.colorPrimary, null))))
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    Log.w(TAG, "Glide load cleared")
                }
            })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.result_detail_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_movie_library -> {
                if (canAddMovieToLibrary) viewModel.addResultToLibrary()
                else {
                    Snackbar
                        .make(activity!!.findViewById(R.id.container), "Movie already in library !", Snackbar.LENGTH_SHORT)
                        .show()
                }
            }
        }
        return true
    }
}
