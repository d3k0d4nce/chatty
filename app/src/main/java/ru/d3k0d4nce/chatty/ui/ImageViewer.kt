package ru.d3k0d4nce.chatty.ui

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.d3k0d4nce.chatty.R
import ru.d3k0d4nce.chatty.data.BASE_URL

@Composable
internal fun ImageViewer(link: String, onClose: () -> Unit) {
    Box(Modifier.fillMaxSize().background(Color.Black)) {
        AsyncImage(
            model = imageUrl("img", link),
            contentDescription = stringResource(R.string.image_description),
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize().padding(12.dp)
        )
        Button(
            onClick = onClose,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(24.dp)
        ) {
            Text(stringResource(R.string.close_image))
        }
    }
}

internal fun imageUrl(folder: String, link: String): String =
    BASE_URL + folder + "/" + Uri.encode(link.trimStart('/'), "/")
