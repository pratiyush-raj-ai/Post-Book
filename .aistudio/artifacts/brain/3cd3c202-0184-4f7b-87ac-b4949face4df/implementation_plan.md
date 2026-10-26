# InstaVibe — Instagram-Style Social Experience with Gemini AI

A native Android social application built in Jetpack Compose and Material Design 3, providing an authentic Instagram-grade experience: dynamic Stories with timed auto-advance, interactive double-tap heart animations on photo feeds, direct messaging with an intelligent Gemini-powered conversational assistant, explore discovery, in-app post creation with AI image generation, rich user profiles, and notifications.

### User Review & Critical Decisions

> [!IMPORTANT]
> The following key decisions have been aligned with your answers and specifications:

- **Target Architecture**: Confirmed native Android application using Kotlin, Jetpack Compose, and Material Design 3 with edge-to-edge support, smooth spring animations, and responsive layouts.
- **Data Persistence**: In-memory reactive state architecture (per specification: "Keep all data in memory, no external backend"), pre-seeded with realistic, high-quality family-friendly creators, stories, comments, and posts across diverse genres (photography, travel, culinary arts, architecture, digital design, pets, and science).
- **Gemini AI Integration**:
  - **In-Feed & Creator AI**: AI-assisted image generation and prompt editing integrated directly into the Create Post flow.
  - **Direct Messages AI**: Integrated Gemini conversational assistant (`gemini-3.8-flash` / `gemini-3.1-flash-lite-preview`) inside DMs with persistent multi-turn history and persona system instructions.
- **Authentication**: Realistic mock multi-method onboarding (Username/Password, quick Google Sign-In, Apple Sign-In, and Mobile SMS OTP login) with session state management and account switching.

---

### 1. Overview & Core Concept

- **What It Does**: InstaVibe provides a complete social media mobile platform. Users can browse stories with gradient unread rings and full-screen auto-advancing viewers, scroll an algorithmic home feed, double-tap to like with heart burst animations, bookmark posts, search trending hashtags and accounts on Explore, create posts with image upload and Gemini AI generation, chat in direct messages with both simulated contacts and an AI companion, view notifications, and manage their profile with posts and saved tabs.
- **Target Audience / Persona**: Mobile creators, social media enthusiasts, and everyday users who appreciate a fluid, modern Instagram experience with instant AI creative tools.
- **Key Value**: A standalone, zero-friction social media environment that works out of the box with zero external backend setup required, demonstrating modern Android Jetpack Compose architecture and AI multimodal features.

---

### 2. User Experience & Visual Design

#### Key User Flows
1. **Onboarding & Auth Flow**:
   - Welcome screen with Instagram-style aesthetics: Log In, or create an account with email/password, One-Tap Google / Apple ID, or phone number.
   - Smooth transition into the main authenticated feed once authenticated or bypassed via instant demo login.
2. **Feed & Stories Flow**:
   - At the top, a horizontal row of circular story avatars with vibrant Instagram sunset gradient rings (Magenta → Coral → Golden Yellow) for unread stories.
   - Tapping an avatar opens a full-screen story viewer with top segmented progress indicators, 5-second automatic progression, left/right tap navigation, pause on hold, and reply input.
   - Vertical feed with edge-to-edge images, header with user avatar and options, action bar (Heart, Comment, Share, Bookmark), like counts, captions with clickable `#hashtags`, and expandable comment bottom sheet.
   - Double-tapping any post image triggers a spring-animated heart burst in the center of the image.
3. **Explore & Discovery Flow**:
   - Top search bar filtering by usernames, captions, and hashtags.
   - Asymmetric staggered grid layout showcasing trending content. Tapping opens post detail with comments.
4. **Create & AI Studio Flow**:
   - Post composer allowing image selection from gallery or entering a text prompt to generate images using Gemini AI.
   - Caption editor with character counter, hashtag suggestions, and instant "Publish" that prepends the new post to the home feed and user profile.
5. **Direct Messages & AI Chatbot Flow**:
   - Message inbox displaying recent conversations and unread badges.
   - AI Assistant thread ("Gemini AI Companion") with role instructions, streaming responses, suggested quick prompts, and multi-turn contextual memory.
   - Standard peer threads with simulated auto-replies.
6. **Profile & Notifications Flow**:
   - Profile header with high-res avatar, bio, follower/following stats, "Edit Profile" and "Share Profile" buttons.
   - Sticky tab bar: Grid of user's posts, and bookmarked "Saved" collection.
   - Activity notifications feed grouped by Today, This Week, and Earlier.

#### Visual Identity & Theme
- **Color Palette**:
  - *Primary Background*: Pure crisp light background (`#FFFFFF`) with dark mode support (`#000000` / `#121212`).
  - *Accent Gradient*: Signature Instagram Sunset Gradient (`#833AB4` → `#FD1D1D` → `#FCAF45`).
  - *Interactive Accents*: Vibrant Like Red (`#ED4956`), Save/Action Tint (`#262626` / `#F5F5F5`), Border hairline (`#EFEFEF` / `#262626`).
- **Typography & Hierarchy**: Clean, high-readability sans-serif typographic scale with bold usernames, medium captions, and muted timestamps (`12sp`).
- **Motion & Micro-interactions**:
  - Floating heart pop animation on double-tap with scale and fade out.
  - Smooth spring transitions between navigation destinations.
  - Story progress animation with linear interpolation.

---

### 3. Key Product Decisions & Trade-Offs

- **In-Memory State Store with Seed Data**:
  - *Chosen Approach*: Centralized `SocialRepository` state flow holding reactive lists of posts, stories, users, messages, and notifications in memory, initialized with diverse high-quality realistic posts.
  - *Why*: Ensures instantaneous interactions, zero network latency for local actions, and requires no external database credentials while satisfying the prompt's explicit requirement.
- **Gemini Direct REST Client**:
  - *Chosen Approach*: Clean Retrofit & OkHttp client with `BuildConfig.GEMINI_API_KEY` for text chat (`gemini-3.8-flash` / `gemini-3.1-flash-lite-preview`) and image generation (`gemini-2.5-flash-image` / `gemini-nano-banana-2.1`).
  - *Why*: Allows rapid prototyping in the Android environment while adhering to platform secret management guidelines.
- **Custom Gesture Engine for Double-Tap & Stories**:
  - *Chosen Approach*: Compose `pointerInput` with `detectTapGestures` for double-tap detection without interfering with vertical feed scrolling, and horizontal touch partitioning for story navigation.

---

### 4. Technical Architecture & Data Strategy

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                             MainActivity & Theme                            │
└──────────────────────────────────────┬──────────────────────────────────────┘
                                       │
                ┌──────────────────────┴──────────────────────┐
                ▼                                             ▼
     ┌────────────────────┐                        ┌────────────────────┐
     │   Auth / Welcome   │                        │ Main Scaffold Host │
     └────────────────────┘                        └──────────┬─────────┘
                                                              │
   ┌───────────────┬─────────────────┬────────────────┬───────┴────────┬───────────────┐
   ▼               ▼                 ▼                ▼                ▼               ▼
┌───────┐     ┌─────────┐      ┌───────────┐    ┌───────────┐    ┌───────────┐   ┌───────────┐
│ Feed  │     │ Explore │      │  Create   │    │ Direct DMs│    │ Activity  │   │  Profile  │
│ Screen│     │ Screen  │      │  Post/AI  │    │  & Gemini │    │  Notifs   │   │  Screen   │
└───────┘     └─────────┘      └───────────┘    └───────────┘    └───────────┘   └───────────┘
   │               │                 │                │                │               │
   └───────────────┴─────────────────┼────────────────┴────────────────┴───────────────┘
                                     ▼
                    ┌─────────────────────────────────┐
                    │     SocialViewModel & State     │
                    └────────────────┬────────────────┘
                                     │
           ┌─────────────────────────┴─────────────────────────┐
           ▼                                                   ▼
┌─────────────────────────┐                         ┌─────────────────────┐
│    SocialRepository     │                         │  GeminiApiService   │
│   (In-Memory Reactive)  │                         │ (Chat & Image Gen)  │
└─────────────────────────┘                         └─────────────────────┘
```

#### Core Data Entities
- **User**: `id`, `username`, `name`, `avatarUrl`, `bio`, `followerCount`, `followingCount`, `isFollowing`.
- **Story**: `id`, `userId`, `username`, `avatarUrl`, `mediaUrl`, `timestamp`, `isViewed`.
- **Post**: `id`, `author: User`, `mediaUrl`, `caption`, `tags: List<String>`, `likesCount`, `isLiked`, `isSaved`, `commentsCount`, `timestamp`.
- **Comment**: `id`, `postId`, `user: User`, `text`, `timestamp`, `likesCount`.
- **ChatMessage**: `id`, `conversationId`, `senderId`, `text`, `timestamp`, `isFromMe`, `isAi`.
- **Notification**: `id`, `type` (LIKE, COMMENT, FOLLOW), `user: User`, `postId`, `timestamp`, `isRead`.

#### Component & Feature Breakdown
1. **Feed & Stories**: `HomeFeedScreen`, `StoryRow`, `StoryViewerDialog`, `PostCard`, `DoubleTapHeartOverlay`.
2. **Post Composer**: `CreatePostScreen` with gallery picker simulation, caption input, hashtag adder, and Gemini AI Image Generator modal with quick inspiration prompts.
3. **Explore**: `ExploreScreen` with live search query filter and responsive staggered grid.
4. **Direct Messages**: `DirectMessagesScreen` with recent conversation list, active chat bubble view, auto-replies, and dedicated Gemini AI Chatbot mode supporting multi-turn conversation.
5. **Profile & Activity**: `ProfileScreen` with header stats, tab bar (Posts / Saved), post grid, and `NotificationsScreen`.
