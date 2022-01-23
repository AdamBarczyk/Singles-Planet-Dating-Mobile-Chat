package com.example.singlesplanetchat.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.example.singlesplanetchat.model.Message

@Composable
fun RightMessageBox(
    msg: Message,
    photoURL: String
) {
    Row(
        modifier = Modifier.padding(8.dp).fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Column(
            modifier = Modifier.widthIn(0.dp, 225.dp),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = msg.name!!,
                color = MaterialTheme.colors.secondary,
                style = MaterialTheme.typography.subtitle2
            )

            Spacer(modifier = Modifier.height(4.dp))

            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colors.primaryVariant,
                elevation = 1.dp
            ) {
                Text(
                    text = msg.message!!,
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.padding(4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Image(
            painter = rememberImagePainter(photoURL),
            contentDescription = "Profile photo",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .border(3.5.dp, MaterialTheme.colors.secondary, CircleShape)
        )
    }

}