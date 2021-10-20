package com.leafcode.petmint.petDetails

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.leafcode.petmint.databinding.ImageSliderImageViewBinding
import com.squareup.picasso.Picasso

class ImageSliderAdapter(val imageUriList : MutableList<String>) : RecyclerView.Adapter<ImageSliderAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(imageUriList[position],position,imageUriList.size)
    }

    override fun getItemCount(): Int {
        return imageUriList.size
    }


    class ViewHolder(val binding : ImageSliderImageViewBinding) : RecyclerView.ViewHolder(binding.root){

        //Function binding the layout of the viewholder with the data
        fun bind(item : String,position: Int,size : Int){
            binding.totalImageNumber.text = size.toString()
            binding.currentImageNumber.text = (position+1).toString()
            Picasso.get().load(item).into(binding.imageSliderView)
        }

        //Function creating the viewholder item
        companion object {
            fun from(parent: ViewGroup): ImageSliderAdapter.ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ImageSliderImageViewBinding.inflate(layoutInflater,parent,false)
                return ImageSliderAdapter.ViewHolder(binding)
            }
        }

    }

}