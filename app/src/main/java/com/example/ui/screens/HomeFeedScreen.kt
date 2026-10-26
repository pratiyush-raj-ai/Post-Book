package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.SocialRepository
import com.example.model.Post
import com.example.model.Story
import com.example.ui.components.CommentBottomSheet
import com.example.ui.components.PostCard
import com.example.ui.components.StoryAvatar
import com.example.ui.components.StoryViewerDialog
import com.example.ui.theme.LikeRed

@Composable
fun HomeFeedScreen(
    onNavigateToDMs: () -> Unit,
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    val posts by SocialRepository.posts.collectAsStateWithLifecycle()
    val stories by SocialRepository.stories.collectAsStateWithLifecycle()
    val conversations by SocialRepository.conversations.collectAsStateWithLifecycle()

    val totalUnreadDMs = remember(conversations) {
        conversations.sumOf { it.unreadCount }
    }

    var viewingStoryIndex by remember { mutableStateOf<Int?>(null) }
    var activeCommentPost by remember { mutableStateOf<Post?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top App Bar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "InstaVibe",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontFamily = com.example.ui.theme.AppLogoFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            // Dark Mode Toggle Button
            IconButton(
                onClick = onToggleDarkMode,
                modifier = Modifier.testTag("dark_mode_toggle_button")
            ) {
                Icon(
                    imageVector = if (isDarkMode) Icons.Default.Brightness7 else Icons.Default.Brightness4,
                    contentDescription = "Toggle Dark Mode",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }

            // Direct Messages Button with Unread Badge
            Box {
                IconButton(
                    onClick = onNavigateToDMs,
                    modifier = Modifier.testTag("home_dms_button")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ChatBubbleOutline,
                        contentDescription = "Direct Messages",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                if (totalUnreadDMs > 0) {
                    Badge(
                        containerColor = LikeRed,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 6.dp, end = 6.dp)
                    ) {
                        Text(
                            text = totalUnreadDMs.toString(),
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))

        // Main Feed (Stories Header + Post Feed)
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("home_feed_list")
        ) {
            // Horizontal Stories Tray
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("stories_row")
                ) {
                    itemsIndexed(stories, key = { _, s -> s.id }) { index, story ->
                        StoryAvatar(
                            story = story,
                            onClick = { viewingStoryIndex = index }
                        )
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            }

            // Vertical Feed of Posts
            items(posts, key = { it.id }) { post ->
                PostCard(
                    post = post,
                    onLikeClick = { SocialRepository.toggleLike(post.id) },
                    onDoubleTapLike = {
                        if (!post.isLiked) {
                            SocialRepository.toggleLike(post.id)
                        }
                    },
                    onCommentClick = { activeCommentPost = post },
                    onSaveClick = { SocialRepository.toggleSave(post.id) }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    // Full Screen Story Viewer Dialog
    viewingStoryIndex?.let { index ->
        StoryViewerDialog(
            stories = stories,
            initialIndex = index,
            onDismiss = { viewingStoryIndex = null },
            onStoryViewed = { id -> SocialRepository.markStoryViewed(id) }
        )
    }

    // Comments Bottom Sheet
    activeCommentPost?.let { currentPost ->
        // Keep activeCommentPost updated with latest state
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
