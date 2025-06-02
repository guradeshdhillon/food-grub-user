package com.example.food

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.food.Fragment.NotificationAdapter
import com.example.food.databinding.FragmentNotificationBottomBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class notification_bottom : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentNotificationBottomBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentNotificationBottomBinding.inflate(layoutInflater,container,false)
        val notifications = listOf("Your order has been canceled successfully", "Order has been taken by the driver", "Congrats your order is placed")
        val notificationImages = listOf(R.drawable.sademoji,R.drawable.truck,R.drawable.congrats)
        val adapter = NotificationAdapter(
            ArrayList(notifications),
            ArrayList(notificationImages)
        )
        binding.notificationRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.notificationRecyclerView.adapter = adapter
        return binding.root
    }

    companion object {
    }
}