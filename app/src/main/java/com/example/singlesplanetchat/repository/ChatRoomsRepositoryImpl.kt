package com.example.singlesplanetchat.repository

import com.example.singlesplanetchat.model.ChatRoom
import com.example.singlesplanetchat.model.Message
import com.example.singlesplanetchat.model.User
import com.example.singlesplanetchat.util.Constants
import com.example.singlesplanetchat.util.Resource
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.io.IOException

@ExperimentalCoroutinesApi
class ChatRoomsRepositoryImpl(
    private val _firebaseFirestore: FirebaseFirestore
) : ChatRoomsRepository {

    override fun getChatRooms(loggedUser: User): Flow<Resource<List<ChatRoom>>> = flow {
        emit(Resource.Loading())

        try {

            val result = if (loggedUser.chats.isEmpty())
                listOf<ChatRoom>()
            else
                _firebaseFirestore
                    .collection(Constants.USER_CHATROOMS)
                    .whereIn("chatId", loggedUser.chats)
                    .get()
                    .await()
                    .toObjects(ChatRoom::class.java)

            emit(Resource.Success(result))


        } catch (e: HttpException) {
            emit(Resource.Error(message = "Something went wrong!"))
        } catch (e: IOException) {
            emit(Resource.Error(message = "Couldn't reach server, check your internet connection!"))
        } catch (e: FirebaseFirestoreException) {
            emit(Resource.Error(message = e.localizedMessage!!))
        }
    }

    override fun getChatRoomById(chatRoomId: String): Flow<Resource<ChatRoom>> = flow {
        emit(Resource.Loading())

        try {

            val result = _firebaseFirestore
                    .collection(Constants.USER_CHATROOMS)
                    .document(chatRoomId)
                    .get()
                    .await()
                    .toObject(ChatRoom::class.java)

            emit(Resource.Success(result!!))


        } catch (e: HttpException) {
            emit(Resource.Error(message = "Something went wrong!"))
        } catch (e: IOException) {
            emit(Resource.Error(message = "Couldn't reach server, check your internet connection!"))
        } catch (e: FirebaseFirestoreException) {
            emit(Resource.Error(message = e.localizedMessage!!))
        }
    }

    override fun loadMessages(chatRoom: ChatRoom): Flow<Resource<List<Message>>> = callbackFlow {
        val subscription = _firebaseFirestore
            .collection(Constants.USER_CHATROOMS)
            .document(chatRoom.chatId!!)
            .collection(Constants.CHAT_ROOM_MESSAGES)
            .orderBy("time", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                error?.let {
                    trySend(Resource.Error(error.message!!))
                    return@addSnapshotListener
                }
                trySend(Resource.Success(snapshot?.toObjects(Message::class.java)!!))
            }
        awaitClose { subscription.remove() }
    }

    override fun sendMessage(message: Message, chatRoom: ChatRoom): Flow<Resource<Void>> = flow {
        emit(Resource.Loading())

        try {

            val result = _firebaseFirestore
                .collection(Constants.USER_CHATROOMS)
                .document(chatRoom.chatId!!)
                .collection(Constants.CHAT_ROOM_MESSAGES)
                .document()
                .set(message)
                .await()

            emit(Resource.Success(result))


        } catch (e: HttpException) {
            emit(Resource.Error(message = "Something went wrong!"))
        } catch (e: IOException) {
            emit(Resource.Error(message = "Couldn't reach server, check your internet connection!"))
        } catch (e: FirebaseFirestoreException) {
            emit(Resource.Error(message = e.localizedMessage!!))
        }
    }

}