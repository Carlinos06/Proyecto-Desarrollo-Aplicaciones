package com.example.corpalivapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

import com.example.corpalivapp.ui.theme.CorpalivAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            setContent {
                setContent {
                    CorpalivAppTheme {
                        AppNavigation()
                    }
                }
                CorpalivAppTheme {
                    HomeScreen()
                }
            }
        }
    }
}

