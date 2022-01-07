package com.example.singlesplanetchat.model

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

val gson = Gson()

//convert a data class to a map
fun <T> T.serializeToMap(): Map<String, Any> {
    return convert()
}

//convert a map to a data class
inline fun <reified T> Map<String, Any>.toDataClass(): T {
    return convert()
}

//convert an object of type I to type O
inline fun <I, reified O> I.convert(): O {
    val json = gson.toJson(this)
    return gson.fromJson(json, object : TypeToken<O>() {}.type)
}


data class User(
    val uid: String? = null,
    val email: String? = null,
    val name: String? = null,
    val birthDate: String? = null,
    val userAge: Int? = null,
    val bio: String? = null,
    val gender: String? = null,
    val interestedGender: String? = null,
    val searchDistance: Float = 20.0f,
    val searchAge: List<Float> = listOf(18f, 25f),
    val photoURL: String? = null,
    val location: List<Double>? = null,
    val selectedProfiles: List<String> = listOf(),
    val pairs: List<String> = listOf()
)