package com.example.data

import com.example.model.*

object SampleDataProvider {

    val currentUser = User(
        id = "u_me",
        username = "alex_rivers",
        fullName = "Alex Rivers",
        avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=400&q=80",
        bio = "Visual storyteller & coffee enthusiast ☕️📸\nExploring architectural gems & sunset peaks.\nNew Lightroom presets linked below 👇",
        website = "alexrivers.design",
        postsCount = 9,
        followersCount = 4280,
        followingCount = 392,
        isFollowing = false,
        isVerified = true
    )

    val userElena = User(
        id = "u_1",
        username = "elena_wander",
        fullName = "Elena Rostova",
        avatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=400&q=80",
        bio = "Alpine trails & fjord mornings 🏔️ National Geographic Contributor",
        postsCount = 184,
        followersCount = 89400,
        followingCount = 412,
        isFollowing = true,
        isVerified = true
    )

    val userMarco = User(
        id = "u_2",
        username = "chef_marco",
        fullName = "Marco Venti",
        avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=400&q=80",
        bio = "Sourdough alchemist & pasta artisan 🥖 Florence -> Kyoto",
        postsCount = 92,
        followersCount = 34100,
        followingCount = 280,
        isFollowing = true,
        isVerified = false
    )

    val userLiam = User(
        id = "u_3",
        username = "urban_lens",
        fullName = "Liam Chen",
        avatarUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=400&q=80",
        bio = "Concrete geometry & brutalist shadows 📐 Tokyo / Berlin",
        postsCount = 143,
        followersCount = 61200,
        followingCount = 195,
        isFollowing = true,
        isVerified = true
    )

    val userMaya = User(
        id = "u_4",
        username = "maya_botanics",
        fullName = "Maya Patel",
        avatarUrl = "https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&w=400&q=80",
        bio = "Cultivating urban jungles & rare tropical monsteras 🌿🪴",
        postsCount = 67,
        followersCount = 21500,
        followingCount = 310,
        isFollowing = false,
        isVerified = false
    )

    val userNova = User(
        id = "u_5",
        username = "nova_tech",
        fullName = "Nova Creative",
        avatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=400&q=80",
        bio = "Designing next-gen digital experiences & creative robotics 🤖⚡️",
        postsCount = 51,
        followersCount = 47800,
        followingCount = 160,
        isFollowing = true,
        isVerified = true
    )

    val userGemini = User(
        id = "u_ai",
        username = "gemini_copilot",
        fullName = "Gemini Creative AI",
        avatarUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?auto=format&fit=crop&w=400&q=80",
        bio = "Your built-in AI companion for caption crafting, idea sparking & storytelling 🪄",
        postsCount = 365,
        followersCount = 999000,
        followingCount = 1,
        isFollowing = true,
        isVerified = true
    )

    fun getInitialStories(): List<Story> = listOf(
        Story(
            id = "s_me",
            userId = currentUser.id,
            username = "Your Story",
            userAvatarUrl = currentUser.avatarUrl,
            mediaUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=1080&q=80",
            timestamp = "Just now",
            caption = "Early morning ocean breeze 🌊",
            isViewed = true
        ),
        Story(
            id = "s_1",
            userId = userElena.id,
            username = userElena.username,
            userAvatarUrl = userElena.avatarUrl,
            mediaUrl = "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?auto=format&fit=crop&w=1080&q=80",
            timestamp = "1h ago",
            caption = "First light hitting the peaks at 2,400 meters ☀️🏔️",
            isViewed = false
        ),
        Story(
            id = "s_2",
            userId = userMarco.id,
            username = userMarco.username,
            userAvatarUrl = userMarco.avatarUrl,
            mediaUrl = "https://images.unsplash.com/photo-1555396273-367ea4eb4db5?auto=format&fit=crop&w=1080&q=80",
            timestamp = "3h ago",
            caption = "Freshly pulled espresso with velvety crema ☕️",
            isViewed = false
        ),
        Story(
            id = "s_3",
            userId = userLiam.id,
            username = userLiam.username,
            userAvatarUrl = userLiam.avatarUrl,
            mediaUrl = "https://images.unsplash.com/photo-1513694203232-719a280e022f?auto=format&fit=crop&w=1080&q=80",
            timestamp = "4h ago",
            caption = "Minimalist gallery spaces in Omotesando 🏛️",
            isViewed = false
        ),
        Story(
            id = "s_4",
            userId = userMaya.id,
            username = userMaya.username,
            userAvatarUrl = userMaya.avatarUrl,
            mediaUrl = "https://images.unsplash.com/photo-1459411552884-841db9b3cc2a?auto=format&fit=crop&w=1080&q=80",
            timestamp = "6h ago",
            caption = "Watering session complete. Look at this fenestration! 🍃",
            isViewed = false
        ),
        Story(
            id = "s_5",
            userId = userNova.id,
            username = userNova.username,
            userAvatarUrl = userNova.avatarUrl,
            mediaUrl = "https://images.unsplash.com/photo-1518770660439-4636190af475?auto=format&fit=crop&w=1080&q=80",
            timestamp = "8h ago",
            caption = "Testing new generative haptic interface designs 💻✨",
            isViewed = false
        )
    )

    fun getInitialPosts(): List<Post> = listOf(
        Post(
            id = "p_1",
            author = userElena,
            mediaUrl = "https://images.unsplash.com/photo-1506744038136-46273834b3fb?auto=format&fit=crop&w=1080&q=80",
            caption = "Woke up at 4:30 AM to catch the mist rising over the glacial valley. The silence was absolute. Moments like this remind me why we explore.",
            hashtags = listOf("#AlpineMornings", "#WildernessCulture", "#LandscapePhotography", "#TravelDiaries"),
            likesCount = 3842,
            isLiked = false,
            isSaved = false,
            commentsCount = 84,
            timestamp = "2h ago",
            comments = listOf(
                Comment("c_1", "p_1", userLiam, "That layer of mist is pure poetry!", "1h ago", 14),
                Comment("c_2", "p_1", userMarco, "Incredible color palette Elena! 👏", "45m ago", 8),
                Comment("c_3", "p_1", currentUser, "Which lens did you shoot this with? The clarity is unreal.", "20m ago", 3)
            )
        ),
        Post(
            id = "p_2",
            author = userMarco,
            mediaUrl = "https://images.unsplash.com/photo-1501339847302-ac426a4a7cbb?auto=format&fit=crop&w=1080&q=80",
            caption = "Nothing beats the aroma of freshly roasted Ethiopian single-origin beans at sunrise. Hand pour-over with notes of jasmine and wild berries ☕️",
            hashtags = listOf("#ArtisanCoffee", "#MorningRitual", "#BaristaLife", "#SpecialtyCoffee"),
            likesCount = 1420,
            isLiked = true,
            isSaved = true,
            commentsCount = 32,
            timestamp = "4h ago",
            comments = listOf(
                Comment("c_4", "p_2", userMaya, "Send some over to the studio please! ☕️", "3h ago", 5),
                Comment("c_5", "p_2", userElena, "Best coffee in town without a doubt.", "2h ago", 4)
            )
        ),
        Post(
            id = "p_3",
            author = userLiam,
            mediaUrl = "https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?auto=format&fit=crop&w=1080&q=80",
            caption = "Symmetry and light bouncing through the glass atrium. When architecture meets midday sunshine, geometric shadows come alive 📐✨",
            hashtags = listOf("#ArchitecturePhotography", "#MinimalDesign", "#UrbanGeometry", "#Brutalism"),
            likesCount = 2190,
            isLiked = false,
            isSaved = false,
            commentsCount = 47,
            timestamp = "6h ago",
            comments = listOf(
                Comment("c_6", "p_3", userNova, "The leading lines in this composition are top tier.", "5h ago", 12)
            )
        ),
        Post(
            id = "p_4",
            author = userMaya,
            mediaUrl = "https://images.unsplash.com/photo-1470071459604-3b5ec3a7fe05?auto=format&fit=crop&w=1080&q=80",
            caption = "Golden sunbeams filtering through ancient redwood canopies. Nature has the best natural studio lighting 🌲🌿",
            hashtags = listOf("#ForestSanctuary", "#PlantLover", "#GoldenHour", "#MotherNature"),
            likesCount = 980,
            isLiked = false,
            isSaved = true,
            commentsCount = 19,
            timestamp = "9h ago",
            comments = listOf(
                Comment("c_7", "p_4", userElena, "Breathtaking depth of field Maya!", "7h ago", 6)
            )
        ),
        Post(
            id = "p_5",
            author = userNova,
            mediaUrl = "https://images.unsplash.com/photo-1518770660439-4636190af475?auto=format&fit=crop&w=1080&q=80",
            caption = "Silicon synapses and luminous quantum circuits. The intersection of generative AI hardware and micro-optics is advancing faster than ever ⚡️💡",
            hashtags = listOf("#FutureTech", "#AIRevolution", "#HardwareDesign", "#Innovation"),
            likesCount = 4520,
            isLiked = true,
            isSaved = false,
            commentsCount = 112,
            timestamp = "12h ago",
            comments = listOf(
                Comment("c_8", "p_5", userLiam, "Cleanest macro shot of the year.", "10h ago", 19)
            )
        )
    )

    fun getUserPosts(): List<Post> = listOf(
        Post(
            id = "my_p_1",
            author = currentUser,
            mediaUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=1080&q=80",
            caption = "Golden coast wanderings. Salty air and warm sands 🏖️",
            hashtags = listOf("#OceanVibes", "#Coastlines", "#TravelPhotography"),
            likesCount = 512,
            isLiked = true,
            isSaved = false,
            commentsCount = 18,
            timestamp = "1d ago"
        ),
        Post(
            id = "my_p_2",
            author = currentUser,
            mediaUrl = "https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&w=1080&q=80",
            caption = "Portrait session under diffuse afternoon overcast. Texture and warmth.",
            hashtags = listOf("#PortraitsDaily", "#NaturalLight"),
            likesCount = 743,
            isLiked = false,
            isSaved = true,
            commentsCount = 29,
            timestamp = "3d ago"
        ),
        Post(
            id = "my_p_3",
            author = currentUser,
            mediaUrl = "https://images.unsplash.com/photo-1490750967868-88aa4486c946?auto=format&fit=crop&w=1080&q=80",
            caption = "Spring blossoms breaking out in the botanical gardens 🌸",
            hashtags = listOf("#SpringVibes", "#FloralBeauty"),
            likesCount = 389,
            isLiked = false,
            isSaved = false,
            commentsCount = 14,
            timestamp = "5d ago"
        )
    )

    fun getInitialConversations(): List<Conversation> = listOf(
        Conversation(
            id = "conv_ai",
            participant = userGemini,
            lastMessage = "Hey Alex! Need catchy caption ideas or creative hashtag strategies for your new post?",
            lastTimestamp = "Just now",
            unreadCount = 1,
            isAiAssistant = true
        ),
        Conversation(
            id = "conv_1",
            participant = userElena,
            lastMessage = "Let's definitely plan that sunrise shoot next weekend in the valley!",
            lastTimestamp = "15m ago",
            unreadCount = 2,
            isAiAssistant = false
        ),
        Conversation(
            id = "conv_2",
            participant = userMarco,
            lastMessage = "Dropped a fresh bag of coffee beans for you at the front desk ☕️",
            lastTimestamp = "2h ago",
            unreadCount = 0,
            isAiAssistant = false
        ),
        Conversation(
            id = "conv_3",
            participant = userLiam,
            lastMessage = "Did you check out the new architectural gallery in Soho?",
            lastTimestamp = "Yesterday",
            unreadCount = 0,
            isAiAssistant = false
        )
    )

    fun getInitialMessages(): Map<String, List<ChatMessage>> = mapOf(
        "conv_ai" to listOf(
            ChatMessage("m_ai_1", "conv_ai", userGemini.id, userGemini.fullName, "Hello Alex! ✨ I'm your Gemini creative assistant. Ask me for captions, hashtag stacks, visual concept prompts, or chat naturally!", "10:00 AM", false, true)
        ),
        "conv_1" to listOf(
            ChatMessage("m_1_1", "conv_1", userElena.id, userElena.fullName, "Hey Alex, loved your coastal photos!", "Yesterday", false),
            ChatMessage("m_1_2", "conv_1", currentUser.id, currentUser.fullName, "Thanks Elena! The light was unbelievable that evening.", "Yesterday", true),
            ChatMessage("m_1_3", "conv_1", userElena.id, userElena.fullName, "Let's definitely plan that sunrise shoot next weekend in the valley!", "15m ago", false)
        ),
        "conv_2" to listOf(
            ChatMessage("m_2_1", "conv_2", userMarco.id, userMarco.fullName, "Dropped a fresh bag of coffee beans for you at the front desk ☕️", "2h ago", false)
        )
    )

    fun getInitialNotifications(): List<NotificationItem> = listOf(
        NotificationItem(
            id = "n_1",
            type = NotificationType.LIKE,
            user = userElena,
            postImage = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=400&q=80",
            text = "liked your photo.",
            timestamp = "10m ago",
            isRead = false
        ),
        NotificationItem(
            id = "n_2",
            type = NotificationType.COMMENT,
            user = userMarco,
            postImage = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=400&q=80",
            text = "commented: \"The light reflection on the water is magic!\"",
            timestamp = "1h ago",
            isRead = false
        ),
        NotificationItem(
            id = "n_3",
            type = NotificationType.FOLLOW,
            user = userMaya,
            text = "started following you.",
            timestamp = "3h ago",
            isRead = true
        ),
        NotificationItem(
            id = "n_4",
            type = NotificationType.LIKE,
            user = userLiam,
            postImage = "https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&w=400&q=80",
            text = "liked your photo.",
            timestamp = "1d ago",
            isRead = true
        )
    )
}
