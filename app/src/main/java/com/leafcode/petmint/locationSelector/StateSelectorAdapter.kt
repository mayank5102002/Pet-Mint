package com.leafcode.petmint.locationSelector

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.leafcode.petmint.databinding.ListItemLocationSelectorBinding

//Adapter for selecting the state from the list
class StateSelectorAdapter(val clickListener : StateSelectorListener) : ListAdapter<Location, StateSelectorAdapter.ViewHolder>(StateDiffCallback()) {

    //Creating the view from the function of the viewholder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    //Binding the views to the viewholder
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position),clickListener)
    }

    //Viewholder class
    class ViewHolder(val binding: ListItemLocationSelectorBinding) : RecyclerView.ViewHolder(binding.root){
        val res = itemView.context.resources

        //Function binding the layout of the viewholder with the data
        fun bind(item : Location,clickListener: StateSelectorListener){
            binding.location.text = item.state
            binding.listItem.setOnClickListener {
                clickListener.onClick(item)
            }
        }

        //Function creating the viewholder item
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemLocationSelectorBinding.inflate(layoutInflater,parent,false)
                return ViewHolder(binding)
            }
        }
    }
}

//Diffcallback method to reload the item which is changed
class StateDiffCallback : DiffUtil.ItemCallback<Location>() {
    override fun areItemsTheSame(oldItem: Location, newItem: Location): Boolean {
        return oldItem.state == newItem.state
    }

    override fun areContentsTheSame(oldItem: Location, newItem: Location): Boolean {
        return oldItem == newItem
    }

}

//Click listener class for the item
class StateSelectorListener(val clickListener : (state: Location) -> Unit){
    fun onClick(state : Location) = clickListener(state)
}