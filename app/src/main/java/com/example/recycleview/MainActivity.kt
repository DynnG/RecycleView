package com.example.recycleview

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    private lateinit var adapter: ItemAdapter
    private lateinit var personList: MutableList<Person>
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.title = "Geraldyn A. Cataluna"
        supportActionBar?.setDisplayShowHomeEnabled(true)

        dbHelper = DatabaseHelper(this)
        personList = dbHelper.getAllPersons()

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = ItemAdapter(personList) { person, action ->
            when (action) {
                "edit" -> {
                    val intent = Intent(this, AddPersonActivity::class.java)
                    intent.putExtra("person_id", person.id)
                    startActivity(intent)
                }
                "delete" -> {
                    dbHelper.deletePerson(person.id)
                    personList.remove(person)
                    adapter.notifyDataSetChanged()
                }
            }
        }

        recyclerView.adapter = adapter


        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val fromPos = viewHolder.adapterPosition
                val toPos = target.adapterPosition
                val movedItem = personList.removeAt(fromPos)
                personList.add(toPos, movedItem)
                adapter.notifyItemMoved(fromPos, toPos)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val person = personList[position]
                dbHelper.deletePerson(person.id)
                personList.removeAt(position)
                adapter.notifyItemRemoved(position)
            }
        }

        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView)


        val searchView = findViewById<SearchView>(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                val filtered = if (newText.isNullOrEmpty()) {
                    dbHelper.getAllPersons()
                } else {
                    dbHelper.searchPersons(newText)
                }
                adapter.filterList(filtered)
                return true
            }
        })
    }

    override fun onResume() {
        super.onResume()
        personList.clear()
        personList.addAll(dbHelper.getAllPersons())
        adapter.notifyDataSetChanged()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.my_menu, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu -> {
                val intent = Intent(this, AddPersonActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
