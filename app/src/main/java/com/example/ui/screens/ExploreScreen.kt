package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.SocialRepository
import com.example.model.Post
import com.example.ui.components.CommentBottomSheet
import com.example.ui.components.PostCard

@Composable
fun ExploreScreen(
    modifier: Modifier = Modifier
) {
    val posts by SocialRepository.posts.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }
    var selectedPost by remember { mutableStateOf<Post?>(null) }
    var activeCommentPost by remember { mutableStateOf<Post?>(null) }

    val filteredPosts = remember(posts, searchQuery) {
        if (searchQuery.isBlank()) {
            posts
        } else {
            val q = searchQuery.trim().lowercase()
            posts.filter { post ->
                post.author.username.lowercase().contains(q) ||
                post.caption.lowercase().contains(q) ||
                post.hashtags.any { it.lowercase().contains(q) }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Search Bar Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search accounts, captions, or #hashtags...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear search")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("explore_search_input")
            )
        }

        // Explore Mosaic Grid
        if (filteredPosts.isEmpty()) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp)
            ) {
                Text(
                    text = "No results found for \"$searchQuery\"\nTry searching for #AlpineMornings, coffee, or creators",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(1.dp),
                horizontalArrangement = Arrangement.spacedBy(1.5.dp),
                verticalArrangement = Arrangement.spacedBy(1.5.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("explore_grid")
            ) {
                items(filteredPosts, key = { it.id }) { post ->
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clickable { selectedPost = post }
                    ) {
                        AsyncImage(
                            model = post.mediaUrl,
                            contentDescription = "Explore photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }

    // Post Detail Preview Modal
    selectedPost?.let { currentPost ->
        val updatedPost = posts.find { it.id == currentPost.id } ?: currentPost
        Dialog(
            onDismissRequest = { selectedPost = null }
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .clip(RoundedCornerShape(16.dp))
            ) {
                Column {
                    PostCard(
                        post = updatedPost,
                        onLikeClick = { SocialRepository.toggleLike(updatedPost.id) },
                        onDoubleTapLike = {
                            if (!updatedPost.isLiked) {
                                SocialRepository.toggleLike(updatedPost.id)
                            }
                        },
                        onCommentClick = {
                            activeCommentPost = updatedPost
                        },
                        onSaveClick = { SocialRepository.toggleSave(updatedPost.id) }
                    )

                    Button(
                        onClick = { selectedPost = null },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Text("Close")
                    }
                }
            }
        }
    }

    // Comment Bottom Sheet from Explore
    activeCommentPost?.let { currentPost ->
        val updatedPost = posts.find { it.id == currentPost.id } ?: currentPost
        CommentBottomSheet(
            post = updatedPost,
            onDismiss = { activeCommentPost = null },
            onAddComment = { text ->
                SocialRepository.addComment(updatedPost.id, text)
            }
        )
    }
}
