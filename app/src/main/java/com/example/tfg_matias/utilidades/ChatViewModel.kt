package com.example.tfg_matias.utilidades

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody



// Modelos de datos
data class Chat(
    val chatId: String = "",
    val participants: List<String> = emptyList(),
    val cocheId: String = "",
    val lastMessage: String = "",
    val updatedAt: Long = 0L
)

data class Message(
    val senderId: String = "",
    val receiverId: String = "",
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val seen: Boolean = false
)


data class MessageCount(val count: Int)

class ChatViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _chatList = MutableStateFlow<List<Chat>>(emptyList())
    val chatList: StateFlow<List<Chat>> = _chatList

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    private val _globalUnreadCounts = MutableStateFlow<Map<String, MessageCount>>(emptyMap())
    val globalUnreadCounts: StateFlow<Map<String, MessageCount>> = _globalUnreadCounts

    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount


    // Contexto de la app para poder mostrar Toast
    private var appContext: Context? = null
    fun setContext(context: Context) {
        appContext = context.applicationContext
    }

    init {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            observeUnreadMessagesSimplified(userId)
        }
    }

    fun loadChats() {
        val userId = auth.currentUser?.uid ?: return
        db.collection("chats")
            .whereArrayContains("participants", userId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    println("Error cargando chats: ${e.localizedMessage}")
                    return@addSnapshotListener
                }
                val chats = snapshot?.documents?.mapNotNull {
                    it.toObject(Chat::class.java)?.copy(chatId = it.id)
                } ?: emptyList()
                _chatList.value = chats
            }
    }

    // Escucha global de mensajes no leídos
    private fun observeUnreadMessagesSimplified(userId: String) {
        db.collection("chats")
            .whereArrayContains("participants", userId)
            .addSnapshotListener { chatsSnapshot, chatError ->
                if (chatError != null) {
                    println("Error al escuchar chats del usuario: ${chatError.localizedMessage}")
                    return@addSnapshotListener
                }

                val userChats = chatsSnapshot?.documents ?: return@addSnapshotListener

                val unreadCountsMap = mutableMapOf<String, MessageCount>()

                userChats.forEach { chatDoc ->
                    val chatId = chatDoc.id
                    val messagesRef = chatDoc.reference.collection("messages")
                        .whereEqualTo("seen", false) //  antes decía "seen"
                        .whereNotEqualTo("senderId", userId)


                    messagesRef.addSnapshotListener { messagesSnapshot, msgError ->
                        if (msgError != null) {
                            println("Error escuchando mensajes no leídos en $chatId: ${msgError.localizedMessage}")
                            return@addSnapshotListener
                        }

                        val count = messagesSnapshot?.documents?.size ?: 0
                        if (count > 0) {
                            unreadCountsMap[chatId] = MessageCount(count)
                        } else {
                            unreadCountsMap.remove(chatId)
                        }

                        _globalUnreadCounts.value = unreadCountsMap
                        _unreadCount.value = unreadCountsMap.values.sumOf { it.count }
                    }
                }
            }
    }



    fun loadMessages(chatId: String) {
        db.collection("chats").document(chatId).collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    println("Error cargando mensajes: ${e.localizedMessage}")
                    return@addSnapshotListener
                }
                val msgs = snapshot?.documents?.mapNotNull {
                    it.toObject(Message::class.java)
                } ?: emptyList()
                _messages.value = msgs
            }
    }

    fun sendMessage(chatId: String, text: String, cocheId: String, receiverId: String) {
        val userId = auth.currentUser?.uid ?: return

        val messageMap = hashMapOf(
            "senderId" to userId,
            "receiverId" to receiverId,
            "text" to text,
            "timestamp" to System.currentTimeMillis(),
            "seen" to false //  asegúrate de que esto sea "seen", no "visto"
            //  Campo corregido
        )


        val chatRef = db.collection("chats").document(chatId)
        val msgRef = chatRef.collection("messages").document()

        viewModelScope.launch {
            try {
                msgRef.set(messageMap).await()

                chatRef.set(
                    hashMapOf(
                        "participants" to listOf(userId, receiverId),
                        "cocheId" to cocheId,
                        "lastMessage" to text,
                        "updatedAt" to System.currentTimeMillis()
                    ),
                    com.google.firebase.firestore.SetOptions.merge()
                ).await()

                //  SOLO ENVIAMOS PUSH AL RECEPTOR (NO a uno mismo)
                if (receiverId != userId) {
                    val receiverDoc = db.collection("users").document(receiverId).get().await()
                    val token = receiverDoc.getString("fcmToken")
                    if (!token.isNullOrBlank()) {
                        val notificationData = mapOf(
                            "to" to token,
                            "priority" to "high",
                            "data" to mapOf(
                                "title" to "Nuevo mensaje",
                                "body" to text,
                                "chatId" to chatId,
                                "cocheId" to cocheId,
                                "sellerId" to userId
                            )
                        )
                        sendPushNotification(notificationData)
                    }
                }

            } catch (e: Exception) {
                println("Error enviando mensaje o push: ${e.localizedMessage}")
            }
        }
    }




    fun markMessagesAsSeen(chatId: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        viewModelScope.launch {
            try {
                val mensajesSnapshot = db.collection("chats")
                    .document(chatId)
                    .collection("messages")
                    .whereNotEqualTo("senderId", currentUser)
                    .whereEqualTo("seen", false)
                    .get()
                    .await()

                for (doc in mensajesSnapshot.documents) {
                    doc.reference.update("seen", true)
                }

                println("Mensajes marcados como vistos en $chatId")

            } catch (e: Exception) {
                println("Error marcando mensajes como vistos: ${e.localizedMessage}")
            }
        }
    }



    fun deleteChat(chatId: String) {
        db.collection("chats").document(chatId)
            .delete()
            .addOnSuccessListener {
                _chatList.value = _chatList.value.filterNot { it.chatId == chatId }
                db.collection("chats").document(chatId).collection("messages")
                    .get()
                    .addOnSuccessListener { snapshot ->
                        for (doc in snapshot.documents) {
                            doc.reference.delete()
                        }
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

    private fun sendPushNotification(payload: Map<String, Any>) {
        val fcmUrl = "https://fcm.googleapis.com/fcm/send"
        val serverKey = "Ux1NvXnDiZjKm-wmFajto3EnfuqNUv2eQm2chn5GqYM" // Sustituye por tu clave real

        viewModelScope.launch {
            try {
                val json = Gson().toJson(payload)
                val client = OkHttpClient()
                val body = json.toRequestBody("application/json; charset=utf-8".toMediaType())

                val request = Request.Builder()
                    .url(fcmUrl)
                    .post(body)
                    .addHeader("Authorization", serverKey)
                    .addHeader("Content-Type", "application/json")
                    .build()

                val response = client.newCall(request).execute()
                println(" Notificación enviada: ${response.code}")
                println(" Respuesta: ${response.body?.string()}")
            } catch (e: Exception) {
                println(" Error al enviar push: ${e.localizedMessage}")
            }
        }
    }
}
