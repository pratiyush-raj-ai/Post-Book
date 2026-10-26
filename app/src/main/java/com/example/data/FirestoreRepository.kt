package com.example.data

import android.content.Context
import android.util.Log
import com.example.R
import com.example.model.Post
import com.example.model.User
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class FirestoreRepository(private val db: FirebaseFirestore) {

    // Mandatory custom database ID resolution
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val auth = Firebase.auth

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
    }

    /**
     * Fetch the latest posts from Firestore
     */
    suspend fun fetchLatestPosts(): List<Post> {
        return try {
            val snapshot = db.collection("posts")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(20)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                val data = doc.data ?: return@mapNotNull null
                val author = User(
                    id = data["userId"] as? String ?: "u_creator",
                    username = data["authorUsername"] as? String ?: "creator",
                    fullName = data["authorUsername"] as? String ?: "InstaVibe Creator",
                    avatarUrl = data["authorAvatarUrl"] as? String ?: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=400&q=80",
                    isVerified = true
                )
                @Suppress("UNCHECKED_CAST")
                val hashtags = (data["hashtags"] as? List<String>) ?: emptyList()
                val likes = (data["likesCount"] as? Number)?.toInt() ?: 1
                val comments = (data["commentsCount"] as? Number)?.toInt() ?: 0

                Post(
                    id = doc.id,
                    author = author,
                    mediaUrl = data["mediaUrl"] as? String ?: "",
                    caption = data["caption"] as? String ?: "",
                    hashtags = hashtags,
                    likesCount = likes,
                    isLiked = false,
                    isSaved = false,
                    commentsCount = comments,
                    timestamp = "Just now"
                )
            }
        } catch (e: Exception) {
            Log.w("FirestoreRepo", "Error fetching latest posts from Firestore: ${e.message}")
            emptyList()
        }
    }

    /**
     * Sync user profile to Firestore
     */
    suspend fun saveUserProfile(user: User) {
        val uid = try { requireUserId() } catch (_: Exception) { return }
        val docRef = db.collection("users").document(uid)
        try {
            val data = mapOf(
                "username" to user.username,
                "fullName" to user.fullName,
                "avatarUrl" to user.avatarUrl,
                "bio" to user.bio,
                "website" to user.website,
                "postsCount" to user.postsCount,
                "followersCount" to user.followersCount,
                "followingCount" to user.followingCount,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.set(data).await()
            Log.d("FirestoreRepo", "User profile saved: $uid")
        } catch (e: Exception) {
            Log.w("FirestoreRepo", "Failed to save user profile: ${e.message}")
        }
    }

    /**
     * Publish a new post to Firestore
     */
    suspend fun publishPost(post: Post) {
        val uid = try { requireUserId() } catch (_: Exception) { return }
        val docRef = db.collection("posts").document(post.id)
        try {
            val data = mapOf(
                "userId" to uid,
                "authorUsername" to post.author.username,
                "authorAvatarUrl" to post.author.avatarUrl,
                "mediaUrl" to post.mediaUrl,
                "caption" to post.caption,
                "hashtags" to post.hashtags,
                "likesCount" to post.likesCount,
                "commentsCount" to post.commentsCount,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.set(data).await()
            Log.d("FirestoreRepo", "Post published to Firestore: ${post.id}")
        } catch (e: Exception) {
            Log.w("FirestoreRepo", "Failed to publish post: ${e.message}")
        }
    }

    /**
     * Save post to user's saved_posts subcollection
     */
    suspend fun toggleSavedPost(postId: String, isSaved: Boolean) {
        val uid = try { requireUserId() } catch (_: Exception) { return }
        val docRef = db.collection("users").document(uid).collection("saved_posts").document(postId)
        try {
            if (isSaved) {
                docRef.set(mapOf("postId" to postId, "savedAt" to FieldValue.serverTimestamp())).await()
            } else {
                docRef.delete().await()
            }
        } catch (e: Exception) {
            Log.w("FirestoreRepo", "Failed to update saved post: ${e.message}")
        }
    }
}
