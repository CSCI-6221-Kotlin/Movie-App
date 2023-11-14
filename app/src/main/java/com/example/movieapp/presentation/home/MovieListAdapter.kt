package com.example.movieapp.presentation.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movieapp.R
import com.example.movieapp.databinding.ItemMovieLayoutBinding
import com.example.movieapp.domain.model.MovieInfo
import com.example.movieapp.domain.model.MovieListUI
import com.example.movieapp.util.Constants

class MovieListAdapter: RecyclerView.Adapter<MovieListAdapter.MoviesViewHolder>() {

    private val differCallback = object: DiffUtil.ItemCallback<MovieInfo>(){
        override fun areItemsTheSame(oldItem: MovieInfo, newItem: MovieInfo): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MovieInfo, newItem: MovieInfo): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this,differCallback)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MoviesViewHolder {
        val binding = ItemMovieLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MoviesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MoviesViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    inner class MoviesViewHolder(val binding:ItemMovieLayoutBinding):RecyclerView.ViewHolder(binding.root){

        fun bind(movieInfo: MovieInfo){
            val posterURL = Constants.TMDB_IMAGE_BASE_URL + movieInfo.posterPath
            binding.apply {
                // movieTitleText.text = movieInfo.originalTitle
                Glide.with(ivMovieImage.context)
                    .load(posterURL)
                    .error(R.mipmap.ic_launcher)
                    .centerInside()
                    .into(ivMovieImage)

                root.setOnClickListener {
                    movieItemClickListener?.let {
                        it(movieInfo)
                    }
                }
            }
        }
    }


    private var movieItemClickListener:((MovieInfo)->Unit)? = null
    fun setOnMovieClickListener(listener:((MovieInfo)->Unit)){
        movieItemClickListener = listener
    }
}