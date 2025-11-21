package com.example.recycleview

import android.content.Intent
import android.content.Intent.*
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AddPersonActivity : AppCompatActivity() {
    private var selectedImageUri: Uri? = null
    private lateinit var dbHelper: DatabaseHelper
    private var personId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intent)

        dbHelper = DatabaseHelper(this)
        personId = intent.getIntExtra("person_id", -1)

        val nameInput = findViewById<EditText>(R.id.nameText)
        val courseSpinner = findViewById<Spinner>(R.id.courseText)
        val imageView = findViewById<ImageView>(R.id.personImageView)
        val selectImageButton = findViewById<Button>(R.id.selectImageButton)
        val saveButton = findViewById<Button>(R.id.saveButton)
        val cancelButton = findViewById<Button>(R.id.cancelButton)

        // Add a hint as the first item
        val courses = arrayOf("Select Course", "BSIT", "BSCS", "BSECE", "BSCE", "BSA", "BSCRIM")

        val adapter = object : ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            courses
        ) {
            override fun isEnabled(position: Int): Boolean {
                // Disable the first item ("Select Course")
                return position != 0
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent) as TextView
                // Gray color for the hint
                if (position == 0) {
                    view.setTextColor(Color.GRAY)
                } else {
                    view.setTextColor(Color.BLACK)
                }
                return view
            }
        }

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        courseSpinner.adapter = adapter
        courseSpinner.setSelection(0) // Default to hint

        val pickImage =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK && result.data != null) {
                    val uri = result.data!!.data

                    if (uri != null) {
                        // ✅ Persist access permission
                        val takeFlags = result.data!!.flags and
                                (Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                        contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)

                        // ✅ Display image (example)
                        imageView.setImageURI(uri)

                        // ✅ Save URI string to database if needed
                        uri.toString().also { selectedImageUri = uri }
                    }
                }
            }

        fun openImagePicker() {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/*"
            }
            pickImage.launch(intent)
        }

        selectImageButton.setOnClickListener {
            openImagePicker()
        }

        // ✅ If editing, load the existing person
        if (personId != -1) {
            val person = dbHelper.getAllPersons().find { it.id == personId }
            person?.let {
                nameInput.setText(it.name)
                val coursePosition = courses.indexOf(it.course)
                if (coursePosition >= 0) courseSpinner.setSelection(coursePosition)
                selectedImageUri = Uri.parse(it.imageUri)
                imageView.setImageURI(selectedImageUri)
                saveButton.text = "Update Student"
            }
        }

        saveButton.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val course = courseSpinner.selectedItem.toString()

            when {
                name.isEmpty() -> {
                    Toast.makeText(this, "Please enter the student's name.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                course == "Select Course" -> {
                    Toast.makeText(this, "Please select a course.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                selectedImageUri == null -> {
                    Toast.makeText(this, "Please choose an image.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            val person = Person(
                id = personId,
                name = name,
                course = course,
                imageUri = selectedImageUri.toString()
            )

            if (personId == -1) {
                // Add new
                val result = dbHelper.insertPerson(person)
                if (result > 0) Toast.makeText(this, "Student added!", Toast.LENGTH_SHORT).show()
            } else {
                // ✅ Update existing
                val result = dbHelper.updatePerson(person)
                if (result > 0) Toast.makeText(this, "Student updated!", Toast.LENGTH_SHORT).show()
            }

            finish()
        }

        cancelButton.setOnClickListener { finish() }
    }
}
