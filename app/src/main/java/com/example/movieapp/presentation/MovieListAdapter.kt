package com.example.movieapp.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.movieapp.databinding.ItemMovieLayoutBinding
import com.example.movieapp.domain.model.MovieInfo

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
    ): MovieListAdapter.MoviesViewHolder {
        val binding = ItemMovieLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MoviesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieListAdapter.MoviesViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int {
        return 5
    }

    inner class MoviesViewHolder(val binding:ItemMovieLayoutBinding):RecyclerView.ViewHolder(binding.root){

        fun bind(movieInfo: MovieInfo){

            binding.apply {
                movieTitleText.text = movieInfo.originalTitle
            }
        }
    }

    private var movieItemClickListener:((MovieInfo)->Unit)? = null
    fun setOnMovieClickListener(listener:((MovieInfo)->Unit)){
        movieItemClickListener = listener
    }


}