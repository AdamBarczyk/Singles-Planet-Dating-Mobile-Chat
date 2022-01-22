package com.example.singlesplanetchat.repository

import com.example.singlesplanetchat.model.User
import com.example.singlesplanetchat.util.Resource
import kotlinx.coroutines.flow.Flow

interface PairsRepository {

    suspend fun getUsers(user: User): Flow<Resource<List<User>>>

    suspend fun getPairs(user: User): Flow<Resource<List<User>>>
}