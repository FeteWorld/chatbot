package com.example.chatbot

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Context
import android.view.View

class profiledetails : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profiledetails)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        val editText = findViewById<EditText>(R.id.userdetails)
        val saveButton = findViewById<Button>(R.id.button) // Make sure this exists in XML

        // Load saved data from SharedPreferences
        val prefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val savedText = prefs.getString("userInput", "")
        editText.setText(savedText)

        // Save button logic
        saveButton.setOnClickListener {
            val input = editText.text.toString()
            prefs.edit().putString("userInput", input).apply()
            Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show()
        }
    }

    fun addinfo1(view: View){
        val editText = findViewById<EditText>(R.id.userdetails)

        val sharedPref = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString("userInput", editText.text.toString())
        editor.apply()

        Toast.makeText(this,"added",Toast.LENGTH_LONG).show()

    }
}