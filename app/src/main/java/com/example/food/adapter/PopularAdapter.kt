package com.example.food.adapter
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.food.DetailsActivity
import com.example.food.databinding.PopularItemBinding

class PopularAdapter (private val items: List<String>,private val price : List<String>,private val image:List<Int>,private val requireContext: Context): RecyclerView.Adapter<PopularAdapter.PouplarViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PouplarViewHolder {
        return PouplarViewHolder(PopularItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }



    override fun onBindViewHolder(holder: PouplarViewHolder, position: Int) {
        val item = items[position]
        val images = image[position]
        val price = price[position]
        holder.bind(item,price,images)
        holder.itemView.setOnClickListener {
            val intent = Intent(requireContext, DetailsActivity::class.java)
            intent.putExtra("MenuItemName",item)
            intent.putExtra("MenuItemImage",images)
            requireContext.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class  PouplarViewHolder (private val binding: PopularItemBinding) : RecyclerView.ViewHolder(binding.root) {
        private val imagesView = binding.imageView7
        fun bind(item: String, price: String, images: Int) {
            binding.FoodNamePopular.text = item
            binding.PricePopular.text = price
            imagesView.setImageResource(images)
        }

    }
}