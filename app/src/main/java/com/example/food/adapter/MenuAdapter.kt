package com.example.food.adapter
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.food.DetailsActivity
import com.example.food.databinding.ActivityDetailsBinding
import com.example.food.databinding.MenuItemBinding
import com.example.food.model.CartItems
import com.example.food.model.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MenuAdapter(
    private val menuItems: List<MenuItem>,
    private val requireContext: Context,

) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {


    private lateinit var binding : ActivityDetailsBinding
    private var foodName: String? = null
    private var foodImage: String? = null
    private var foodPrice: String? = null

    private val itemClickListener: OnClickListener ?= null
    private lateinit var auth: FirebaseAuth

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val binding = MenuItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MenuViewHolder(binding)
    }



    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(position)


    }
    override fun getItemCount(): Int = menuItems.size
    inner class MenuViewHolder(private val binding: MenuItemBinding) :RecyclerView.ViewHolder(binding.root) {



        init {

            binding.addToCartButton.setOnClickListener {
                val position = adapterPosition
                auth = FirebaseAuth.getInstance()
                foodName=menuItems[position].foodName
                foodPrice=menuItems[position].foodPrice
                foodImage=menuItems[position].foodImage
                addItemToCart()
            }
            binding.root.setOnClickListener{

                val position = adapterPosition
                if(position!=RecyclerView.NO_POSITION){
                    itemClickListener?.onItemClick(position)
                }

            }
        }
        fun bind(position: Int) {
            val menuItem = menuItems[position]
            binding.apply {

                menuFoodName.text = menuItem.foodName

                menuPrice.text = menuItem.foodPrice


                val uri = Uri.parse(menuItem.foodImage)
                Glide.with(requireContext).load(uri).into(menuImage)




            }
        }

    }

    private fun addItemToCart() {
        val database = FirebaseDatabase.getInstance().reference
        val userId=auth.currentUser?.uid?:""

        val cartItem= CartItems(foodName.toString(), foodPrice.toString(), foodImage.toString(),1)
        database.child("user").child(userId).child("CartItems").push().setValue(cartItem).addOnSuccessListener {
            Toast.makeText(requireContext,"Item added into cart Succesfully!", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(requireContext,"Item not added into cart!", Toast.LENGTH_SHORT).show()
        }
    }

    interface OnClickListener{
        fun onItemClick(position: Int)
    }

}


