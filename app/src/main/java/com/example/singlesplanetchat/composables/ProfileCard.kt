package com.example.singlesplanetchat.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import coil.transform.RoundedCornersTransformation
import com.example.singlesplanetchat.model.User
import com.example.singlesplanetchat.util.Screen

@Composable
fun ProfileCard(
    user: User,
    navController: NavController
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(30.0f),
        elevation = 5.dp
    ) {
        Box(
            modifier = Modifier.height(550.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Image(
                painter = rememberImagePainter(
                    data = user.photoURL,
                    builder = {
                        transformations(RoundedCornersTransformation(10.0f))
                    }
                ),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 8.dp)
            ) {
                Text(
                    text = "${user.name}, ${user.userAge}",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.primary
                )
                IconButton(
                    onClick = {
                        navController.navigate(Screen.DetailsScreen.route + "/${user.uid}")
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colors.primary,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }
    }
}