package com.example.food

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.food.databinding.ActivityPayOutBinding
import com.example.food.databinding.FragmentCongratsBottomSheetBinding
import com.example.food.model.OrderDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PayOutActivity : AppCompatActivity() {
    lateinit var binding: ActivityPayOutBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var name: String
    private lateinit var address: String
    private lateinit var phone: String
    private lateinit var totalAmount: String
    private lateinit var foodItemName:ArrayList<String>
    private lateinit var foodItemPrice:ArrayList<String>
    private lateinit var foodItemImage:ArrayList<String>
    private lateinit var foodItemQuantities:ArrayList<Int>
    private lateinit var databaseReference: DatabaseReference
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityPayOutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference()

        SetUserData()

        val intent = intent
        foodItemName=intent.getStringArrayListExtra("FoodItemName") as ArrayList<String>
        foodItemPrice=intent.getStringArrayListExtra("FoodItemPrice") as ArrayList<String>
        foodItemImage=intent.getStringArrayListExtra("FoodItemImage") as ArrayList<String>
        foodItemQuantities=intent.getIntegerArrayListExtra("FoodItemQuantities") as ArrayList<Int>

        totalAmount= "₹ "+calculateTotalAmount().toString()
        binding.totalAmount.isEnabled = false
        binding.totalAmount.setText(totalAmount)

        binding.button10.setOnClickListener {
            finish()
        }

        binding.placeMyOrder.setOnClickListener{
            name = binding.name.text.toString().trim()
            address = binding.address.text.toString().trim()
            phone = binding.phone.text.toString().trim()
            if(name.isBlank() || address.isBlank() || phone.isBlank()){
                Toast.makeText(this,"Please Enter All the details!",Toast.LENGTH_SHORT).show()
            }else{
                placeOrder()
            }


        }
    }

    private fun placeOrder() {
        userId = auth.currentUser?.uid?:""
        val time = System.currentTimeMillis()
        val itemPushKey = databaseReference.child("OrderDetails").push().key
        val orderDetails = OrderDetails(userId,name,foodItemName,foodItemPrice,foodItemImage,foodItemQuantities,address,totalAmount,phone,time,itemPushKey,false,false)
        val orderReference = databaseReference.child("OrderDetails").child(itemPushKey!!)
        orderReference.setValue(orderDetails).addOnSuccessListener {
            val bottomSheetDialog = CongratsBottomSheet()
            bottomSheetDialog.show(supportFragmentManager,"Test")
            removeItemsFromCart()
            addOrderToHistory(orderDetails)
        }
            .addOnFailureListener {
                Toast.makeText(this,"Failed to place order!",Toast.LENGTH_SHORT).show()
            }
    }

    private fun addOrderToHistory(orderDetails: OrderDetails) {
        databaseReference.child("user").child(userId).child("BuyHistory")
            .child(orderDetails.itemPushKey!!)
            .setValue(orderDetails).addOnSuccessListener {

            }
    }

    private fun removeItemsFromCart() {
        val cartItemsReference = databaseReference.child("user").child(userId).child("CartItems")
        cartItemsReference.removeValue()
    }

    private fun calculateTotalAmount(): Int {
        var totalAmount = 0
        for(i in 0 until foodItemPrice.size){
            var price = foodItemPrice[i]

            val priceIntValue = if(price.startsWith("₹ ")){
                price.substring(2).toInt()
            }else{
                price.toInt()
            }

            var quantity = foodItemQuantities[i]
            totalAmount += priceIntValue*quantity
        }
        return totalAmount
    }

    private fun SetUserData() {
        val user = auth.currentUser
        if(user!=null){
            val userId = user.uid
            val userReference= databaseReference.child("user").child(userId)

            userReference.addListenerForSingleValueEvent(object  : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        val names = snapshot.child("name").getValue(String::class.java)?:""
                        val addresses = snapshot.child("address").getValue(String::class.java)?:""
                        val phones = snapshot.child("phone").getValue(String::class.java)?:""
                        binding.apply {
                            name.setText(names)
                            address.setText(addresses)
                            phone.setText(phones)
                        }
                    }

                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }

    }
}