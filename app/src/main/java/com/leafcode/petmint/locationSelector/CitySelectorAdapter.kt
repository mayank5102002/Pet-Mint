package com.leafcode.petmint.locationSelector

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.leafcode.petmint.databinding.ListItemLocationSelectorBinding

//Adapter to show the list of cities for selection
class CitySelectorAdapter(val cities: Array<String>, val clickListener: CitySelectorListener) :
    RecyclerView.Adapter<CitySelectorAdapter.ViewHolder>() {

    //Creating the viewholder by calling the function present in viewholder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    //Binding the viewholders to the views of different objects
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(cities[position],clickListener)
    }

    //Viewholder class
    class ViewHolder(val binding: ListItemLocationSelectorBinding) : RecyclerView.ViewHolder(binding.root){
        val res = itemView.context.resources

        //Function binding the layout of the viewholder with the data
        fun bind(item : String,clickListener: CitySelectorListener){
            binding.location.text = item
            binding.arrowLocation.visibility = View.GONE
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

    //Returning the number of items present in the list
    override fun getItemCount(): Int {
        return cities.size
    }
}

//Click listener for the objects of the item
class CitySelectorListener(val clickListener : (state: String) -> Unit){
    fun onClick(state : String) = clickListener(state)
}