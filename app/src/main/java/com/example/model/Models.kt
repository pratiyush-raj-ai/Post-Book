package com.example.model

data class User(
    val id: String,
    val username: String,
    val fullName: String,
    val avatarUrl: String,
    val bio: String = "",
    val website: String = "",
    val postsCount: Int = 0,
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val isFollowing: Boolean = false,
    val isVerified: Boolean = false
)

data class Story(
    val id: String,
    val userId: String,
    val username: String,
    val userAvatarUrl: String,
    val mediaUrl: String,
    val timestamp: String,
    val caption: String = "",
    val isViewed: Boolean = false
)

data class Comment(
    val id: String,
    val postId: String,
    val author: User,
    val text: String,
    val timestamp: String,
    val likesCount: Int = 0,
    val isLiked: Boolean = false
)

data class Post(
    val id: String,
    val author: User,
    val mediaUrl: String,
    val caption: String,
    val hashtags: List<String> = emptyList(),
    val likesCount: Int = 0,
    val isLiked: Boolean = false,
    val isSaved: Boolean = false,
    val commentsCount: Int = 0,
    val timestamp: String,
    val comments: List<Comment> = emptyList()
)

data class ChatMessage(
    val id: String,
    val conversationId: String,
    val senderId: String,
    val senderName: String,
    val text: String,
    val timestamp: String,
    val isFromMe: Boolean,
    val isAi: Boolean = false
)

data class Conversation(
    val id: String,
    val participant: User,
    val lastMessage: String,
    val lastTimestamp: String,
    val unreadCount: Int = 0,
    val isAiAssistant: Boolean = false
)

enum class NotificationType {
    LIKE,
    COMMENT,
    FOLLOW
}

data class NotificationItem(
    val id: String,
    val type: NotificationType,
    val user: User,
    val postImage: String? = null,
    val text: String,
    val timestamp: String,
    val isRead: Boolean = false
)
