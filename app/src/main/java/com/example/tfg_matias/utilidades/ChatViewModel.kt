package com.example.tfg_matias.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class Chat(
    val chatId: String = "",
    val participants: List<String> = emptyList(),
    val cocheId: String = "",
    val lastMessage: String = "",
    val updatedAt: Long = 0L

)

data class Message(
    val senderId: String = "",
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

class ChatViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _chatList = MutableStateFlow<List<Chat>>(emptyList())
    val chatList: StateFlow<List<Chat>> = _chatList

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages


    fun loadChats() {
        val userId = auth.currentUser?.uid ?: return
        db.collection("chats")
            .whereArrayContains("participants", userId)
            .orderBy("updatedAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    println("❌ Error cargando chats: ${e.localizedMessage}")
                    return@addSnapshotListener
                }
                val chats = snapshot?.documents?.mapNotNull { it.toObject(Chat::class.java)?.copy(chatId = it.id) } ?: emptyList()
                _chatList.value = chats
            }
    }

    fun loadMessages(chatId: String) {
        db.collection("chats").document(chatId).collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    println("❌ Error cargando mensajes: ${e.localizedMessage}")
                    return@addSnapshotListener
                }
                val msgs = snapshot?.documents?.mapNotNull { it.toObject(Message::class.java) } ?: emptyList()
                _messages.value = msgs
            }
    }

    fun sendMessage(chatId: String, text: String, cocheId: String, receiverId: String) {
        val userId = auth.currentUser?.uid ?: return
        val message = Message(
            senderId = userId,
            text = text,
            timestamp = System.currentTimeMillis()
        )
        val chatRef = db.collection("chats").document(chatId)
        val msgRef = chatRef.collection("messages").document()

        viewModelScope.launch {
            try {
                msgRef.set(message).await()

                chatRef.set(
                    hashMapOf(
                        "participants" to listOf(userId, receiverId),
                        "cocheId" to cocheId,
                        "lastMessage" to text,
                        "updatedAt" to System.currentTimeMillis()
                    ),
                    com.google.firebase.firestore.SetOptions.merge()
                ).await()
            } catch (e: Exception) {
                println("❌ Error enviando mensaje: ${e.localizedMessage}")
            }
        }
    }

    suspend fun getOrCreateChat(cocheId: String, sellerId: String): String {
        val userId = auth.currentUser?.uid ?: return ""
        val query = db.collection("chats")
            .whereEqualTo("cocheId", cocheId)
            .whereArrayContains("participants", userId)
            .get()
            .await()

        return if (query.documents.isNotEmpty()) {
            query.documents.first().id
        } else {
            val newChat = hashMapOf(
                "participants" to listOf(userId, sellerId),
                "cocheId" to cocheId,
                "lastMessage" to "",
                "updatedAt" to System.currentTimeMillis()
            )
            val docRef = db.collection("chats").document()
            docRef.set(newChat).await()
            docRef.id
        }
    }
}
