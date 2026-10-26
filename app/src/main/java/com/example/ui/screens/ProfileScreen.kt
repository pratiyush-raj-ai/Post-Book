package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.SocialRepository
import com.example.model.Post
import com.example.ui.components.CommentBottomSheet
import com.example.ui.components.PostCard
import com.example.ui.theme.VerifiedBlue

@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentUser by SocialRepository.currentUser.collectAsStateWithLifecycle()
    val allPosts by SocialRepository.posts.collectAsStateWithLifecycle()
    val myPosts by SocialRepository.myPosts.collectAsStateWithLifecycle()

    val savedPosts = remember(allPosts) {
        allPosts.filter { it.isSaved }
    }

    var selectedTab by remember { mutableStateOf(0) } // 0: Posts, 1: Saved
    var selectedPostForModal by remember { mutableStateOf<Post?>(null) }
    var activeCommentPost by remember { mutableStateOf<Post?>(null) }

    val user = currentUser ?: return

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        // Top App Bar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = user.username,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )

            if (user.isVerified) {
                Spacer(modifier = Modifier.width(4.dp))
                Surface(
                    shape = CircleShape,
                    color = VerifiedBlue,
                    modifier = Modifier.size(16.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("✓", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            IconButton(
                onClick = onLogout,
                modifier = Modifier.testTag("profile_logout_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Logout,
                    contentDescription = "Log Out",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

        // Profile Bio & Stats Header
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                AsyncImage(
                    model = user.avatarUrl,
                    contentDescription = user.username,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.width(24.dp))

                // Stats Row: Posts, Followers, Following
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.weight(1f)
                ) {
                    ProfileStatItem(count = user.postsCount.toString(), label = "Posts")
                    ProfileStatItem(count = String.format("%,d", user.followersCount), label = "Followers")
                    ProfileStatItem(count = user.followingCount.toString(), label = "Following")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // User Info
            Text(
                text = user.fullName,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )

            if (user.bio.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = user.bio,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (user.website.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = user.website,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = VerifiedBlue,
                        fontWeight = FontWeight.Medium
                    )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Edit Profile / Share Profile Buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { /* Edit profile */ },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Edit profile",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                }

                Button(
                    onClick = { /* Share profile */ },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Share profile",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                }
            }
        }

        // Tabs: Posts vs Saved
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.background
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                icon = { Icon(Icons.Default.GridOn, contentDescription = "My Posts") },
                text = { Text("Posts (${myPosts.size})") }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                icon = { Icon(Icons.Default.Bookmark, contentDescription = "Saved Posts") },
                text = { Text("Saved (${savedPosts.size})") }
            )
        }

        // Posts Grid
        val displayPosts = if (selectedTab == 0) myPosts else savedPosts

        if (displayPosts.isEmpty()) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp)
            ) {
                Text(
                    text = if (selectedTab == 0) "No posts yet. Tap '+' to create your first post!" else "No saved posts yet. Tap the bookmark icon on any post to save it here.",
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
                    .testTag("profile_posts_grid")
            ) {
                items(displayPosts, key = { it.id }) { post ->
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clickable { selectedPostForModal = post }
                    ) {
                        AsyncImage(
                            model = post.mediaUrl,
                            contentDescription = "Post thumbnail",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }

    // Modal Post Detail Viewer
    selectedPostForModal?.let { currentPost ->
        val updatedPost = allPosts.find { it.id == currentPost.id } ?: currentPost
        Dialog(
            onDismissRequest = { selectedPostForModal = null }
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
                        onClick = { selectedPostForModal = null },
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

    // Comments Sheet
    activeCommentPost?.let { currentPost ->
        val updatedPost = allPosts.find { it.id == currentPost.id } ?: currentPost
        CommentBottomSheet(
            post = updatedPost,
            onDismiss = { activeCommentPost = null },
            onAddComment = { text ->
                SocialRepository.addComment(updatedPost.id, text)
            }
        )
    }
}

@Composable
fun ProfileStatItem(
    count: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = count,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}
