package com.leafcode.petmint.myads

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.leafcode.petmint.databinding.PetListViewBinding
import com.leafcode.petmint.pet.Pet
import com.squareup.picasso.Picasso

class MyAdsAdapter(val clickListener: MyAdsClickListener) : ListAdapter<Pet,MyAdsAdapter.ViewHolder>(MyAdsDiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position),clickListener)
    }

    //Viewholder class
    class ViewHolder(val binding : PetListViewBinding) : RecyclerView.ViewHolder(binding.root){
        val res = itemView.context.resources

        //Function binding the layout of the viewholder with the data
        @SuppressLint("UseCompatLoadingForDrawables")
        fun bind(item : Pet,clickListener: MyAdsClickListener){
            binding.petNameView.text = item.name
            binding.petAddressView.text = item.address
            if (item.image1 != null){
                Picasso.get().load(item.image1).into(binding.mainImage)
            } else if (item.image2 != null){
                Picasso.get().load(item.image2).into(binding.mainImage)
            } else {
                Picasso.get().load(item.image3).into(binding.mainImage)
            }
            binding.petListViewRoot.setOnClickListener{
                clickListener.onClick(item)
            }
        }

        //Function creating the viewholder item
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = PetListViewBinding.inflate(layoutInflater,parent,false)
                return ViewHolder(binding)
            }
        }
    }
}

//Diffcallback method to reload the item which is changed
class MyAdsDiffCallback : DiffUtil.ItemCallback<Pet>() {
    override fun areItemsTheSame(oldItem: Pet, newItem: Pet): Boolean {
        return oldItem.adID == newItem.adID
    }

    override fun areContentsTheSame(oldItem: Pet, newItem: Pet): Boolean {
        return oldItem == newItem
    }

}

//Click listener class for the item
class MyAdsClickListener(val clickListener : (currentPet : Pet) -> Unit){
    fun onClick(currentPet: Pet) = clickListener(currentPet)
}