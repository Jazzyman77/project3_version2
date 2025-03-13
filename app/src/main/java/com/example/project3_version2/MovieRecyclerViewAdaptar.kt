package com.example.project3_version2
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
class MovieRecyclerViewAdapter(
    private val onDelete: (Movie) -> Unit // Callback to delete item
) : ListAdapter<Movie, MovieRecyclerViewAdapter.ViewHolderClass>(DiffCallback()) {

    private var originalList: List<Movie> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_row, parent, false)
        return ViewHolderClass(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val currentItem = getItem(position)
        holder.title.text = currentItem.title

        holder.itemView.setOnClickListener {
            Log.i("CS3680", "item clicked on")
        }

        holder.deleteBtn.setOnClickListener {
            onDelete(currentItem) // Calls delete function from ViewModel
        }
    }

    override fun submitList(list: List<Movie>?) {
        super.submitList(list)
        originalList = list ?: emptyList()  // Store the original list for reset functionality
    }

    fun filter(query: String?) {
        if (query.isNullOrEmpty()) {
            submitList(originalList)  // Reset to the original list when the query is empty
        } else {
            val filteredList = originalList.filter { it.title.contains(query, ignoreCase = true) }
            submitList(filteredList)
        }
    }

    class ViewHolderClass(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.rowTitle)
        val deleteBtn: Button = itemView.findViewById(R.id.rowDelete)
    }

    class DiffCallback : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie) = oldItem.movieId == newItem.movieId
        override fun areContentsTheSame(oldItem: Movie, newItem: Movie) = oldItem == newItem
    }
}
