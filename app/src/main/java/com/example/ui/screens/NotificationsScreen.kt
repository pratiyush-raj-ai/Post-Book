package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.SocialRepository
import com.example.model.NotificationItem
import com.example.model.NotificationType
import com.example.ui.theme.InstaPink
import com.example.ui.theme.LikeRed
import com.example.ui.theme.VerifiedBlue

@Composable
fun NotificationsScreen(
    modifier: Modifier = Modifier
) {
    val notifications by SocialRepository.notifications.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        // Top Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = "Notifications",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("notifications_list")
        ) {
            item {
                Text(
                    text = "Recent Activity",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }

            items(notifications, key = { it.id }) { item ->
                NotificationRow(notification = item)
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                    modifier = Modifier.padding(start = 72.dp)
                )
            }
        }
    }
}

@Composable
fun NotificationRow(
    notification: NotificationItem,
    modifier: Modifier = Modifier
) {
    var isFollowingBack by remember { mutableStateOf(notification.user.isFollowing) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .testTag("notification_item_${notification.id}")
    ) {
        // User Avatar with Badge Icon for Action Type
        Box(contentAlignment = Alignment.BottomEnd) {
            AsyncImage(
                model = notification.user.avatarUrl,
                contentDescription = notification.user.username,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
            )

            Surface(
                shape = CircleShape,
                color = when (notification.type) {
                    NotificationType.LIKE -> LikeRed
                    NotificationType.COMMENT -> VerifiedBlue
                    NotificationType.FOLLOW -> InstaPink
                },
                modifier = Modifier.size(16.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = when (notification.type) {
                            NotificationType.LIKE -> Icons.Default.Favorite
                            NotificationType.COMMENT -> Icons.Outlined.ChatBubbleOutline
                            NotificationType.FOLLOW -> Icons.Default.PersonAdd
                        },
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(10.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Notification Text
        val textAnnotated = buildAnnotatedString {
            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)) {
                append("${notification.user.username} ")
            }
            append(notification.text)
            append(" ")
            withStyle(SpanStyle(color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)) {
                append(notification.timestamp)
            }
        }

        Text(
            text = textAnnotated,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Right side preview or action
        if (notification.type == NotificationType.FOLLOW) {
            Button(
                onClick = { isFollowingBack = !isFollowingBack },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isFollowingBack) MaterialTheme.colorScheme.surfaceVariant else InstaPink
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                modifier = Modifier.height(32.dp)
            ) {
                Text(
                    text = if (isFollowingBack) "Following" else "Follow Back",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isFollowingBack) MaterialTheme.colorScheme.onSurface else Color.White
                )
            }
        } else if (notification.postImage != null) {
            AsyncImage(
                model = notification.postImage,
                contentDescription = "Post thumbnail",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(6.dp))
            )
        }
    }
}
