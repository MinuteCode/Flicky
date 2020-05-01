package com.minutecode.flicky.ui.library

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.interpolator.view.animation.FastOutLinearInInterpolator
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.minutecode.flicky.R
import com.minutecode.flicky.model.omdb.FullMovie
import kotlin.math.abs


class LibraryAdapter(var movies: List<FullMovie>): RecyclerView.Adapter<LibraryAdapter.LibraryViewHolder>() {

    class LibraryViewHolder(movieView: View): RecyclerView.ViewHolder(movieView) {
        lateinit var posterHero: ImageView
        lateinit var poster: ImageView
        lateinit var info: TextView
        lateinit var genres: TextView
        lateinit var castTitle: TextView
        lateinit var castSeparator: FrameLayout
        lateinit var cast: TextView
        lateinit var plotTitle: TextView
        lateinit var plotSeparator: FrameLayout
        lateinit var plot: TextView

        private val interpolator = FastOutLinearInInterpolator()
        var offset: Float = 0f
        set(v) {
            field = v.coerceIn(-1f, 1f)
            val direction = if (field < 0) -1f else 1f
            val interpolatedValue = interpolator.getInterpolation(abs(field))
            val translationX = direction * interpolatedValue * itemView.measuredWidth

            poster.translationX = translationX
            info.translationX = translationX
            genres.translationX = translationX
            castTitle.translationX = translationX
            castSeparator.translationX = translationX
            cast.translationX = translationX
            plotTitle.translationX = translationX
            plotSeparator.translationX = translationX
            plot.translationX = translationX
            itemView.scaleX = abs(4 - abs(direction) * interpolatedValue) / 4
            itemView.scaleY = abs(4 - abs(direction) * interpolatedValue) / 4
            itemView.alpha = abs(1 - abs(direction) * interpolatedValue)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibraryViewHolder {
        val rootLayout = LayoutInflater.from(parent.context).inflate(R.layout.library_movie_layout, parent, false)
        val libraryMovieView = LibraryViewHolder(rootLayout)

        libraryMovieView.posterHero = rootLayout.findViewById(R.id.movie_poster_hero)
        libraryMovieView.poster = rootLayout.findViewById(R.id.movie_poster)
        libraryMovieView.info = rootLayout.findViewById(R.id.movie_info)
        libraryMovieView.genres = rootLayout.findViewById(R.id.movie_genres)
        libraryMovieView.castTitle = rootLayout.findViewById(R.id.movie_cast_title)
        libraryMovieView.castSeparator = rootLayout.findViewById(R.id.movie_cast_separator)
        libraryMovieView.cast = rootLayout.findViewById(R.id.movie_cast)
        libraryMovieView.plotTitle = rootLayout.findViewById(R.id.movie_plot_title)
        libraryMovieView.plotSeparator = rootLayout.findViewById(R.id.movie_plot_separator)
        libraryMovieView.plot = rootLayout.findViewById(R.id.movie_plot)

        return libraryMovieView
    }

    override fun getItemCount(): Int {
        return movies.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: LibraryViewHolder, position: Int) {
        holder.info.text = "${movies[position].title} - ${movies[position].year}"
        Glide.with(holder.itemView)
            .load(movies[position].poster)
            .fallback(R.drawable.ic_broken_image_black_24dp)
            .centerCrop()
            .into(holder.poster)
        Glide.with(holder.itemView)
            .load(movies[position].poster)
            .centerCrop()
            .into(holder.posterHero)
        holder.genres.text = movies[position].genre.joinToString()
        holder.cast.text = movies[position].actors.joinToString()
        holder.plot.text = movies[position].plot
    }
}