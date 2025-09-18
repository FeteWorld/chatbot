package com.example.chatbot

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.lifecycle.ViewModelProvider
class MainActivity : ComponentActivity() {
    private lateinit var viewModel: Viewmodal
    private lateinit var adapter: Adapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        val recyclerView = findViewById<RecyclerView>(R.id.chatRecyclerView)
        val sendButton = findViewById<Button>(R.id.sendButton)
        val userInput = findViewById<EditText>(R.id.userInput)
        //val info = findViewById<Button>(R.id.info)

        adapter = Adapter(mutableListOf())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        viewModel = ViewModelProvider(this)[Viewmodal::class.java]

        viewModel.messages.observe(this) {
            adapter.updateMessages(it)
            recyclerView.scrollToPosition(it.size - 1)
        }

        sendButton.setOnClickListener {
            val text = userInput.text.toString()
            if (text.isNotBlank()) {
                userInput.text.clear()
//                viewModel.sendUserMessage1(text)
                viewModel.sendUserMessage(this, text)

            }
        }
    }

    fun addinfo(view: View){
        val intent = Intent(this,profiledetails::class.java)
        startActivity(intent);
//        Toast.makeText(this,"test",Toast.LENGTH_LONG).show()
    }

}