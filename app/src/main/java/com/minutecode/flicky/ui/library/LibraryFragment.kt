package com.minutecode.flicky.ui.library

import android.animation.TimeAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.minutecode.flicky.MainActivity
import com.minutecode.flicky.R
import kotlin.math.sin

class LibraryFragment : Fragment() {

    private lateinit var viewModel: LibraryViewModel

    private lateinit var libraryRecyclerView: RecyclerView
    private val snapHelper: PagerSnapHelper = PagerSnapHelper()
    private var snapPosition = RecyclerView.NO_POSITION

    private val snapListener: SnapPositionChangeListener = object : SnapPositionChangeListener {
        override fun onSnapPositionChange(position: Int) {
            val snapView = snapHelper.findSnapView(libraryRecyclerView.layoutManager)
            val posterView: ImageView = snapView!!.findViewById(R.id.movie_poster)
            val appCompatActivity = (activity as AppCompatActivity)
            val display =
                (activity!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
            val refreshRating = display.refreshRate

            appCompatActivity.supportActionBar!!.title = viewModel.getMovie(position)!!.title
            setContextColorFor(posterView)
            val timeAnimator = TimeAnimator()
            timeAnimator.setTimeListener { animation, totalTime, deltaTime ->
                posterView.translationY = 8 * sin(totalTime / (15 * refreshRating))
                posterView.invalidate()
            }
            timeAnimator.start()

            snapView.requestLayout()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        activity!!.window.statusBarColor = Color.parseColor("#00FFFFFF")
        val rootView = inflater.inflate(R.layout.library_fragment, container, false)

        val libraryViewManager = LinearLayoutManager(context)
        val libraryAdapter = LibraryAdapter(listOf())
        libraryRecyclerView = rootView.findViewById(R.id.library_recyclerview)

        libraryViewManager.orientation = LinearLayoutManager.HORIZONTAL
        libraryRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = libraryViewManager
            adapter = libraryAdapter
            snapHelper.attachToRecyclerView(this)
            setUpParallaxScrollListener()
        }

        setHasOptionsMenu(true)

        viewModel = ViewModelProvider(this).get(LibraryViewModel::class.java)

        viewModel.userLibrary.observe(viewLifecycleOwner, Observer { library ->
            libraryAdapter.movies = library
            libraryAdapter.notifyDataSetChanged()
        })
        return rootView
    }

    override fun onStart() {
        super.onStart()
        viewModel.fetchUserLibrary()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.library_fragment_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.library_refresh -> viewModel.fetchUserLibrary()
        }
        return true
    }

    private fun maybeNotifySnapPositionChange(recyclerView: RecyclerView) {
        val snapPosition = snapHelper.getSnapPosition(recyclerView)
        if (snapPosition != this.snapPosition) {
            snapListener.onSnapPositionChange(snapPosition)
            this.snapPosition = snapPosition
        }
    }

    private fun RecyclerView.setUpParallaxScrollListener() {
        addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = layoutManager as? LinearLayoutManager ?: return

                val scrollOffset = recyclerView.computeHorizontalScrollOffset()
                val offsetFactor = (scrollOffset % measuredWidth) / measuredWidth.toFloat()

                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                findViewHolderForAdapterPosition(firstVisibleItemPosition)?.let {
                    (it as? LibraryAdapter.LibraryViewHolder)?.offset = -offsetFactor
                }

                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                if (firstVisibleItemPosition != lastVisibleItemPosition) {
                    findViewHolderForAdapterPosition(lastVisibleItemPosition)?.let {
                        (it as? LibraryAdapter.LibraryViewHolder)?.offset = 1 - offsetFactor
                    }
                }

                maybeNotifySnapPositionChange(recyclerView)
            }
        })
    }

    fun setContextColorFor(moviePoster: ImageView) {
        val appCompatActivity = (activity as MainActivity)
        moviePoster.drawable?.let {
            val movieBitmap: Bitmap = moviePoster.drawable.toBitmap()

            val palette = Palette.from(movieBitmap).generate()
            val darkVibrantPalette = palette.getDarkVibrantColor(ResourcesCompat.getColor(resources, R.color.colorPrimary, null))
            val darkMutedPalette = palette.getDarkMutedColor(ResourcesCompat.getColor(resources, R.color.colorPrimary, null))
            val lightMutedPalette = palette.getLightMutedColor(ResourcesCompat.getColor(resources, R.color.colorTextSecondary, null))

            appCompatActivity.supportActionBar!!.setBackgroundDrawable(
                GradientDrawable(GradientDrawable.Orientation.TL_BR, arrayListOf(darkVibrantPalette, darkMutedPalette).toIntArray())
            )
            appCompatActivity.window.statusBarColor = darkVibrantPalette
            appCompatActivity.bottomNavigationView.background = GradientDrawable(GradientDrawable.Orientation.TL_BR, arrayListOf(darkVibrantPalette, darkMutedPalette).toIntArray())
            appCompatActivity.bottomNavigationView.itemTextColor = ColorStateList.valueOf(lightMutedPalette)
            appCompatActivity.bottomNavigationView.itemIconTintList = ColorStateList.valueOf(lightMutedPalette)
        }
    }
}

fun SnapHelper.getSnapPosition(recyclerView: RecyclerView): Int {
    val layoutManager = recyclerView.layoutManager ?: return RecyclerView.NO_POSITION
    val snapView = findSnapView(layoutManager) ?: return RecyclerView.NO_POSITION
    return layoutManager.getPosition(snapView)
}

interface SnapPositionChangeListener {
    fun onSnapPositionChange(position: Int)
}
