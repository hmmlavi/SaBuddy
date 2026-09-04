package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.example.sabuddy.ui.SaBuddyApp
import com.example.sabuddy.viewmodels.SaBuddyViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    val viewModel = ViewModelProvider(
      this,
      ViewModelProvider.AndroidViewModelFactory.getInstance(application)
    )[SaBuddyViewModel::class.java]

    setContent {
      SaBuddyApp(viewModel = viewModel)
    }
  }
}

@androidx.compose.runtime.Composable
fun Greeting(name: String, modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier) {
  androidx.compose.material3.Text(text = "Hello $name!", modifier = modifier)
}


