package com.example.data

import com.example.ai.GeminiService
import com.example.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

object SocialRepository {
    private val scope = CoroutineScope(Dispatchers.Main)
    private var firestoreRepo: FirestoreRepository? = null

    fun initFirestore(context: android.content.Context) {
        if (firestoreRepo == null) {
            try {
                firestoreRepo = FirestoreRepository(context)
            } catch (e: Exception) {
                android.util.Log.w("SocialRepository", "Firestore init skipped: ${e.message}")
            }
        }
    }

    private val _currentUser = MutableStateFlow<User?>(SampleDataProvider.currentUser)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _isAuthenticated = MutableStateFlow(true)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    private val _posts = MutableStateFlow(SampleDataProvider.getInitialPosts())
    val posts: StateFlow<List<Post>> = _posts.asStateFlow()

    private val _myPosts = MutableStateFlow(SampleDataProvider.getUserPosts())
    val myPosts: StateFlow<List<Post>> = _myPosts.asStateFlow()

    private val _stories = MutableStateFlow(SampleDataProvider.getInitialStories())
    val stories: StateFlow<List<Story>> = _stories.asStateFlow()

    private val _conversations = MutableStateFlow(SampleDataProvider.getInitialConversations())
    val conversations: StateFlow<List<Conversation>> = _conversations.asStateFlow()

    private val _messages = MutableStateFlow(SampleDataProvider.getInitialMessages())
    val messages: StateFlow<Map<String, List<ChatMessage>>> = _messages.asStateFlow()

    private val _notifications = MutableStateFlow(SampleDataProvider.getInitialNotifications())
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    private val _isAiTyping = MutableStateFlow(false)
    val isAiTyping: StateFlow<Boolean> = _isAiTyping.asStateFlow()

    suspend fun refreshPostsFromFirestore() {
        delay(600) // Small delay for natural pull-to-refresh feel
        val cloudPosts = firestoreRepo?.fetchLatestPosts().orEmpty()
        if (cloudPosts.isNotEmpty()) {
            val cloudIds = cloudPosts.map { it.id }.toSet()
            val merged = cloudPosts + _posts.value.filter { it.id !in cloudIds }
            _posts.value = merged
        } else {
            // Keep local posts refreshed
            _posts.value = _posts.value.toList()
        }
    }

    fun login(identifier: String, pass: String): Boolean {
        if (identifier.isNotBlank()) {
            _currentUser.value = SampleDataProvider.currentUser.copy(
                username = if (identifier.contains("@")) identifier.substringBefore("@") else identifier
            )
            _isAuthenticated.value = true
            return true
        }
        return false
    }

    fun loginWithFirebaseUser(firebaseUser: com.google.firebase.auth.FirebaseUser) {
        val email = firebaseUser.email ?: ""
        val username = if (email.contains("@")) email.substringBefore("@") else "creator_${firebaseUser.uid.take(5)}"
        val user = User(
            id = firebaseUser.uid,
            username = username,
            fullName = firebaseUser.displayName ?: username,
            avatarUrl = firebaseUser.photoUrl?.toString() ?: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=400&q=80",
            bio = "Visual creator on InstaVibe ✨",
            website = "",
            postsCount = 0,
            followersCount = 1,
            followingCount = 4,
            isFollowing = false,
            isVerified = true
        )
        _currentUser.value = user
        _isAuthenticated.value = true
        scope.launch {
            firestoreRepo?.saveUserProfile(user)
        }
    }

    fun loginWithSocial(provider: String) {
        val user = when (provider.lowercase()) {
            "google" -> SampleDataProvider.currentUser.copy(
                username = "alex_google",
                fullName = "Alex Rivers (Google)"
            )
            "apple" -> SampleDataProvider.currentUser.copy(
                username = "alex_apple",
                fullName = "Alex Rivers (Apple)"
            )
            else -> SampleDataProvider.currentUser.copy(
                username = "alex_mobile",
                fullName = "Alex Rivers (SMS)"
            )
        }
        _currentUser.value = user
        _isAuthenticated.value = true
    }

    fun logout() {
        _isAuthenticated.value = false
    }

    fun toggleLike(postId: String) {
        _posts.value = _posts.value.map { post ->
            if (post.id == postId) {
                val newLiked = !post.isLiked
                val newCount = if (newLiked) post.likesCount + 1 else (post.likesCount - 1).coerceAtLeast(0)
                post.copy(isLiked = newLiked, likesCount = newCount)
            } else post
        }
        _myPosts.value = _myPosts.value.map { post ->
            if (post.id == postId) {
                val newLiked = !post.isLiked
                val newCount = if (newLiked) post.likesCount + 1 else (post.likesCount - 1).coerceAtLeast(0)
                post.copy(isLiked = newLiked, likesCount = newCount)
            } else post
        }
    }

    fun toggleSave(postId: String) {
        var isNowSaved = false
        _posts.value = _posts.value.map { post ->
            if (post.id == postId) {
                isNowSaved = !post.isSaved
                post.copy(isSaved = isNowSaved)
            } else post
        }
        _myPosts.value = _myPosts.value.map { post ->
            if (post.id == postId) post.copy(isSaved = !post.isSaved) else post
        }
        scope.launch {
            firestoreRepo?.toggleSavedPost(postId, isNowSaved)
        }
    }

    fun addComment(postId: String, text: String) {
        if (text.isBlank()) return
        val author = _currentUser.value ?: SampleDataProvider.currentUser
        val newComment = Comment(
            id = "c_${System.currentTimeMillis()}",
            postId = postId,
            author = author,
            text = text.trim(),
            timestamp = "Just now"
        )
        _posts.value = _posts.value.map { post ->
            if (post.id == postId) {
                post.copy(
                    commentsCount = post.commentsCount + 1,
                    comments = post.comments + newComment
                )
            } else post
        }
    }

    fun createPost(mediaUrl: String, caption: String, hashtags: List<String>) {
        val author = _currentUser.value ?: SampleDataProvider.currentUser
        val newPost = Post(
            id = "p_${System.currentTimeMillis()}",
            author = author,
            mediaUrl = mediaUrl,
            caption = caption,
            hashtags = hashtags,
            likesCount = 1,
            isLiked = true,
            isSaved = false,
            commentsCount = 0,
            timestamp = "Just now",
            comments = emptyList()
        )
        _posts.value = listOf(newPost) + _posts.value
        _myPosts.value = listOf(newPost) + _myPosts.value
        _currentUser.value = author.copy(postsCount = author.postsCount + 1)
        scope.launch {
            firestoreRepo?.publishPost(newPost)
        }
    }

    fun markStoryViewed(storyId: String) {
        _stories.value = _stories.value.map { story ->
            if (story.id == storyId) story.copy(isViewed = true) else story
        }
    }

    fun toggleFollow(userId: String) {
        val updateFollowing: (User) -> User = { user ->
            if (user.id == userId) {
                val newFollowing = !user.isFollowing
                val newFollowers = if (newFollowing) user.followersCount + 1 else (user.followersCount - 1).coerceAtLeast(0)
                user.copy(isFollowing = newFollowing, followersCount = newFollowers)
            } else user
        }
        _posts.value = _posts.value.map { it.copy(author = updateFollowing(it.author)) }
    }

    fun sendMessage(conversationId: String, text: String) {
        if (text.isBlank()) return
        val author = _currentUser.value ?: SampleDataProvider.currentUser
        val timeNow = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())

        val userMsg = ChatMessage(
            id = "m_${System.currentTimeMillis()}",
            conversationId = conversationId,
            senderId = author.id,
            senderName = author.fullName,
            text = text.trim(),
            timestamp = timeNow,
            isFromMe = true,
            isAi = false
        )

        val currentList = _messages.value[conversationId].orEmpty()
        val updatedList = currentList + userMsg
        _messages.value = _messages.value + (conversationId to updatedList)

        // Update conversation last message
        _conversations.value = _conversations.value.map { conv ->
            if (conv.id == conversationId) conv.copy(lastMessage = text, lastTimestamp = "Just now") else conv
        }

        // Handle auto-reply or Gemini AI reply
        val targetConv = _conversations.value.find { it.id == conversationId }
        if (targetConv?.isAiAssistant == true) {
            handleAiReply(conversationId, updatedList, text)
        } else {
            handlePeerAutoReply(conversationId, targetConv?.participant)
        }
    }

    private fun handleAiReply(conversationId: String, chatHistory: List<ChatMessage>, promptText: String) {
        _isAiTyping.value = true
        scope.launch {
            // Build history pairs
            val historyPairs = chatHistory.dropLast(1).takeLast(10).map { msg ->
                Pair(if (msg.isFromMe) "user" else "model", msg.text)
            }

            val result = GeminiService.sendChatMessage(historyPairs, promptText)
            _isAiTyping.value = false

            val replyText = result.getOrDefault("I'm here to help you spark amazing creativity! What else would you like to explore?")
            val aiUser = SampleDataProvider.userGemini
            val timeNow = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())

            val aiMsg = ChatMessage(
                id = "m_${System.currentTimeMillis()}",
                conversationId = conversationId,
                senderId = aiUser.id,
                senderName = aiUser.fullName,
                text = replyText,
                timestamp = timeNow,
                isFromMe = false,
                isAi = true
            )

            val current = _messages.value[conversationId].orEmpty()
            _messages.value = _messages.value + (conversationId to (current + aiMsg))
            _conversations.value = _conversations.value.map { conv ->
                if (conv.id == conversationId) conv.copy(lastMessage = replyText, lastTimestamp = "Just now") else conv
            }
        }
    }

    private fun handlePeerAutoReply(conversationId: String, peer: User?) {
        scope.launch {
            delay(1800)
            val peerUser = peer ?: SampleDataProvider.userElena
            val timeNow = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
            val replies = listOf(
                "Awesome! Looking forward to it 👍",
                "Love that idea! Let's do it this weekend 📸",
                "Sounds great, let me check my schedule and ping you!",
                "100%! That sounds super inspiring ✨"
            )
            val peerReply = replies.random()
            val replyMsg = ChatMessage(
                id = "m_${System.currentTimeMillis()}",
                conversationId = conversationId,
                senderId = peerUser.id,
                senderName = peerUser.fullName,
                text = peerReply,
                timestamp = timeNow,
                isFromMe = false,
                isAi = false
            )
            val current = _messages.value[conversationId].orEmpty()
            _messages.value = _messages.value + (conversationId to (current + replyMsg))
            _conversations.value = _conversations.value.map { conv ->
                if (conv.id == conversationId) conv.copy(lastMessage = peerReply, lastTimestamp = "Just now") else conv
            }
        }
    }
}
