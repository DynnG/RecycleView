package com.example.recycleview

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ItemAdapter(
    private val personList: MutableList<Person>,
    private val onAction: (Person, String) -> Unit
) : RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameText: TextView = itemView.findViewById(R.id.nameText)
        val courseText: TextView = itemView.findViewById(R.id.courseText)
        val imageView: ImageView = itemView.findViewById(R.id.personImage)
        val optionsMenu: ImageView = itemView.findViewById(R.id.optionsMenu)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val person = personList[position]
        holder.nameText.text = person.name
        holder.courseText.text = person.course

        // ✅ Safely set image URI
        try {
            holder.imageView.setImageURI(Uri.parse(person.imageUri))
        } catch (e: Exception) {
            // Optional: use a fallback image if URI is invalid
            holder.imageView.setImageResource(R.drawable.default_image)
        }

        // ✅ Popup menu
        holder.optionsMenu.setOnClickListener {
            val popup = PopupMenu(holder.itemView.context, holder.optionsMenu)
            popup.inflate(R.menu.item_menu)
            popup.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menu_edit -> onAction(person, "edit")
                    R.id.menu_delete -> onAction(person, "delete")
                }
                true
            }
            popup.show()
        }
    }

    override fun getItemCount(): Int = personList.size

    fun filterList(filteredList: List<Person>) {
        personList.clear()
        personList.addAll(filteredList)
        notifyDataSetChanged()
    }
}
