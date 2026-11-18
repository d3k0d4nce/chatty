package ru.d3k0d4nce.chatty

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import ru.d3k0d4nce.chatty.ui.ChattyRoot
import ru.d3k0d4nce.chatty.ui.theme.ChattyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChattyTheme {
                ChattyRoot()
            }
        }
    }
}
