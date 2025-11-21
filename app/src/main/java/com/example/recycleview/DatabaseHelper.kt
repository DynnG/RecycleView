package com.example.recycleview

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(private val context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "students.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "persons"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_COURSE = "course"
        private const val COLUMN_IMAGE_URI = "imageUri"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT NOT NULL,
                $COLUMN_COURSE TEXT NOT NULL,
                $COLUMN_IMAGE_URI TEXT NOT NULL
            )
        """.trimIndent()
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertPerson(person: Person): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, person.name)
            put(COLUMN_COURSE, person.course)
            put(COLUMN_IMAGE_URI, person.imageUri)
        }
        val result = db.insert(TABLE_NAME, null, values)
        db.close()
        return result
    }

    fun getAllPersons(): MutableList<Person> {
        val list = mutableListOf<Person>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
                val course = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COURSE))
                val imageUri = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URI))
                list.add(Person(id, name, course, imageUri))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return list
    }

    fun searchPersons(query: String): MutableList<Person> {
        val list = mutableListOf<Person>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_NAME WHERE $COLUMN_NAME LIKE ?",
            arrayOf("%$query%")
        )

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
                val course = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COURSE))
                val imageUri = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URI))
                list.add(Person(id, name, course, imageUri))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return list
    }

    fun deletePerson(id: Int): Int {
        val db = writableDatabase
        val result = db.delete(TABLE_NAME, "$COLUMN_ID=?", arrayOf(id.toString()))
        db.close()
        return result
    }

    fun updatePerson(person: Person): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, person.name)
            put(COLUMN_COURSE, person.course)
            put(COLUMN_IMAGE_URI, person.imageUri)
        }
        val result = db.update(TABLE_NAME, values, "$COLUMN_ID=?", arrayOf(person.id.toString()))
        db.close()
        return result
    }

}
