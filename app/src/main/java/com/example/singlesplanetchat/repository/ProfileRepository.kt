package com.example.singlesplanetchat.repository

import android.net.Uri
import com.example.singlesplanetchat.model.User
import com.example.singlesplanetchat.util.Resource
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {

    suspend fun getUserData(uid: String): Flow<Resource<User>>

    suspend fun setOrUpdateUserData(user: User): Flow<Resource<Void>>

    suspend fun uploadUserPhoto(uid: String, photoURI: Uri): Flow<Resource<Uri>>
}