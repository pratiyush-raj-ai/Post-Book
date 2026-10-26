package com.example.ui.screens

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ai.GeneratedImageResult
import com.example.ai.GeminiService
import com.example.data.SocialRepository
import com.example.ui.theme.InstagramGradient
import com.example.ui.theme.InstaPink
import kotlinx.coroutines.launch

@Composable
fun CreatePostScreen(
    onPostCreated: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var selectedTab by remember { mutableStateOf(0) } // 0: AI Generator, 1: Presets / Gallery
    var aiPrompt by remember { mutableStateOf("Cozy coffee shop corner with warm rainy window lighting") }
    var caption by remember { mutableStateOf("") }
    var isGeneratingImage by remember { mutableStateOf(false) }

    var currentImageUrl by remember {
        mutableStateOf("https://images.unsplash.com/photo-1501339847302-ac426a4a7cbb?auto=format&fit=crop&w=1080&q=80")
    }
    var currentBitmap by remember { mutableStateOf<Bitmap?>(null) }

    val sampleGalleryOptions = listOf(
        "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=1080&q=80",
        "https://images.unsplash.com/photo-1506744038136-46273834b3fb?auto=format&fit=crop&w=1080&q=80",
        "https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?auto=format&fit=crop&w=1080&q=80",
        "https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&w=1080&q=80",
        "https://images.unsplash.com/photo-1518770660439-4636190af475?auto=format&fit=crop&w=1080&q=80"
    )

    val quickStylePrompts = listOf(
        "Golden Hour Mountains" to "Dramatic alpine summit at golden hour with soft clouds and sun rays",
        "Cyberpunk Neon" to "Futuristic rain-slicked city streets with glowing magenta and cyan neon signs",
        "Minimalist Loft" to "Bright Scandinavian interior design with fiddle leaf fig tree and warm oak wood",
        "Artisan Coffee" to "Barista pouring latte art rosette into ceramic cup with morning light",
        "Vintage 35mm" to "Warm vintage film grain snapshot of coastal cliffs and gentle blue waves"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Top Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "New Post",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    val tags = caption.split(" ")
                        .filter { it.startsWith("#") && it.length > 1 }
                    val cleanCaption = caption.split(" ")
                        .filter { !it.startsWith("#") }
                        .joinToString(" ")
                        .trim()

                    SocialRepository.createPost(
                        mediaUrl = currentImageUrl,
                        caption = if (cleanCaption.isNotBlank()) cleanCaption else "Created with InstaVibe ✨",
                        hashtags = if (tags.isNotEmpty()) tags else listOf("#InstaVibe", "#Creative")
                    )
                    onPostCreated()
                },
                colors = ButtonDefaults.buttonColors(containerColor = InstaPink),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.testTag("publish_post_button")
            ) {
                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Share", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Image Preview Card
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(16.dp))
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                if (currentBitmap != null) {
                    Image(
                        bitmap = currentBitmap!!.asImageBitmap(),
                        contentDescription = "Post preview",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    AsyncImage(
                        model = currentImageUrl,
                        contentDescription = "Post preview",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                if (isGeneratingImage) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.6f))
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = Color.White)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Gemini AI is generating image...",
                                color = Color.White,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Mode Selector: AI Generation vs Gallery Presets
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
            modifier = Modifier.clip(RoundedCornerShape(12.dp))
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Gemini AI Image")
                    }
                }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Preset Photos")
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedTab == 0) {
            // AI Image Generator UI
            Text(
                text = "Describe the image you want to create:",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
            )

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = aiPrompt,
                onValueChange = { aiPrompt = it },
                shape = RoundedCornerShape(12.dp),
                maxLines = 3,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("ai_prompt_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Style Chips
            Text(
                text = "Inspiration presets:",
                style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )

            Spacer(modifier = Modifier.height(6.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(quickStylePrompts) { (title, fullPrompt) ->
                    SuggestionChip(
                        onClick = { aiPrompt = fullPrompt },
                        label = { Text(title) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    if (aiPrompt.isNotBlank() && !isGeneratingImage) {
                        isGeneratingImage = true
                        coroutineScope.launch {
                            val result = GeminiService.generateImage(aiPrompt)
                            isGeneratingImage = false
                            result.onSuccess { res ->
                                when (res) {
                                    is GeneratedImageResult.BitmapResult -> {
                                        currentBitmap = res.bitmap
                                    }
                                    is GeneratedImageResult.UrlResult -> {
                                        currentBitmap = null
                                        currentImageUrl = res.url
                                    }
                                }
                            }
                        }
                    }
                },
                enabled = !isGeneratingImage && aiPrompt.isNotBlank(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("generate_image_button")
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Generate with Gemini AI", fontWeight = FontWeight.Bold)
            }
        } else {
            // Preset / Gallery Media Selector
            Text(
                text = "Choose from curated photo library:",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(sampleGalleryOptions) { url ->
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(
                                width = if (currentImageUrl == url && currentBitmap == null) 3.dp else 1.dp,
                                color = if (currentImageUrl == url && currentBitmap == null) InstaPink else Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                currentBitmap = null
                                currentImageUrl = url
                            }
                    ) {
                        AsyncImage(
                            model = url,
                            contentDescription = "Gallery item",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Caption Input
        Text(
            text = "Write a caption & hashtags:",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
        )

        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = caption,
            onValueChange = { caption = it },
            placeholder = { Text("Write a caption... #photography #vibes") },
            shape = RoundedCornerShape(12.dp),
            minLines = 3,
            maxLines = 5,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("post_caption_input")
        )

        Spacer(modifier = Modifier.height(60.dp))
    }
}
