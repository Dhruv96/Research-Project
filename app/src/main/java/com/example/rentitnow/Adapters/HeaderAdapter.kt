package com.example.rentitnow.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rentitnow.Data.Booking
import com.example.rentitnow.databinding.SectionHeaderRecyclerviewBinding
import kotlinx.android.synthetic.main.section_header_recyclerview.view.*

class HeaderAdapter(private val bookingList: List<List<Booking>>,private val bookingIds:List<List<String>>,
                    private val sections: List<String>,val context: Context):
    RecyclerView.Adapter<HeaderAdapter.SectionViewHolder>() {

    inner class SectionViewHolder(itemView: SectionHeaderRecyclerviewBinding) : RecyclerView.ViewHolder(itemView.root) {
        fun bindView(sectionTitle: String, bookings: List<Booking>, bookingIds: List<String>) {
            itemView.headerTitle.text = sectionTitle
            println("*********INSIDE HEADER ADAPTER")
            println(bookings.size)
            itemView.innerRecyclerView.apply {
                adapter = UserBookingsAdapter(bookings,bookingIds, context)
                layoutManager = LinearLayoutManager(context)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionViewHolder {
        println("*********INSIDE HEADER ADAPTER")
        val from = LayoutInflater.from(parent.context)
        val binding = SectionHeaderRecyclerviewBinding.inflate(from, parent, false)
        return SectionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SectionViewHolder, position: Int) {
        if(sections.size > 0 && position<bookingIds.size) {
            holder.bindView(sections[position], bookingList[position], bookingIds[position])
        }
    }

    override fun getItemCount(): Int {
        return sections.size
    }
}