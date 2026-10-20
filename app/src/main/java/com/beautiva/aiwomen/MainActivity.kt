package com.beautiva.aiwomen

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

private val Ink = Color(0xFF442C31)
private val Muted = Color(0xFF8C7177)
private val Rose = Color(0xFFB56F7D)
private val RoseLight = Color(0xFFF5E6E8)
private val Canvas = Color(0xFFFCF8F5)
private val Line = Color(0xFFEDE0DC)
private val White = Color(0xFFFFFFFF)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { BeautivaApp() }
    }
}

private enum class AppTab(val label: String) {
    Discover("Discover"), Studio("Studio"), Looks("My Looks"), Profile("Profile")
}

private data class BeautyCategory(
    val name: String,
    val subtitle: String,
    val emoji: String,
    val description: String
)

private val categories = listOf(
    BeautyCategory("Hairstyle", "Find your next hair look", "✧", "Explore soft waves, sleek styles, elegant bobs and curls."),
    BeautyCategory("Makeup", "Your signature glow", "✦", "Discover natural glow, soft glam and evening makeup."),
    BeautyCategory("Outfits", "Style your moment", "◇", "Try elegant, casual, office and evening outfit concepts."),
    BeautyCategory("Nails", "Small details, big mood", "♡", "Browse French, milky nude, rose chrome and minimal nail art.")
)

private val looksByCategory = mapOf(
    "Hairstyle" to listOf("Soft waves", "Long straight", "Elegant bob", "Curly volume"),
    "Makeup" to listOf("Natural glow", "Soft glam", "Evening glam", "Rose nude"),
    "Outfits" to listOf("Elegant dress", "Casual chic", "Office elegance", "Evening look"),
    "Nails" to listOf("French manicure", "Milky nude", "Rose chrome", "Minimal art")
)

@Composable
private fun BeautivaApp() {
    var selectedTab by remember { mutableStateOf(AppTab.Discover) }
    var selectedCategory by remember { mutableStateOf("Hairstyle") }
    var selectedLook by remember { mutableStateOf("Soft waves") }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var notice by remember { mutableStateOf<String?>(null) }
    var savedLooks by remember { mutableStateOf(listOf<String>()) }

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        photoUri = uri
        notice = if (uri != null) "Photo selected. AI editing will be enabled after backend setup." else null
    }

    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Rose,
            onPrimary = White,
            secondary = Ink,
            onSecondary = White,
            background = Canvas,
            surface = White,
            onSurface = Ink,
            surfaceVariant = RoseLight,
            onSurfaceVariant = Muted,
            outline = Line
        ),
        typography = Typography(
            headlineLarge = androidx.compose.ui.text.TextStyle(fontSize = 30.sp, fontWeight = FontWeight.SemiBold, lineHeight = 36.sp),
            headlineMedium = androidx.compose.ui.text.TextStyle(fontSize = 24.sp, fontWeight = FontWeight.SemiBold),
            titleLarge = androidx.compose.ui.text.TextStyle(fontSize = 20.sp, fontWeight = FontWeight.SemiBold),
            titleMedium = androidx.compose.ui.text.TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold),
            bodyLarge = androidx.compose.ui.text.TextStyle(fontSize = 15.sp, lineHeight = 22.sp),
            bodyMedium = androidx.compose.ui.text.TextStyle(fontSize = 14.sp, lineHeight = 20.sp),
            bodySmall = androidx.compose.ui.text.TextStyle(fontSize = 12.sp, lineHeight = 17.sp)
        )
    ) {
        Scaffold(
            containerColor = Canvas,
            bottomBar = {
                NavigationBar(
                    containerColor = White,
                    contentColor = Ink,
                    tonalElevation = 8.dp
                ) {
                    AppTab.entries.forEach { tab ->
                        NavigationBarItem(
                            selected = selectedTab == tab,
                            onClick = { selectedTab = tab; notice = null },
                            icon = {
                                Icon(
                                    imageVector = when (tab) {
                                        AppTab.Discover -> Icons.Rounded.Home
                                        AppTab.Studio -> Icons.Rounded.AutoAwesome
                                        AppTab.Looks -> Icons.Rounded.FavoriteBorder
                                        AppTab.Profile -> Icons.Rounded.PersonOutline
                                    },
                                    contentDescription = tab.label
                                )
                            },
                            label = { Text(tab.label, maxLines = 1) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Rose,
                                selectedTextColor = Rose,
                                indicatorColor = RoseLight,
                                unselectedIconColor = Muted,
                                unselectedTextColor = Muted
                            )
                        )
                    }
                }
            }
        ) { padding ->
            AnimatedContent(
                targetState = selectedTab,
                label = "main_tab",
                modifier = Modifier.padding(padding)
            ) { tab ->
                when (tab) {
                    AppTab.Discover -> DiscoverScreen(
                        onCategoryClick = { selectedCategory = it; selectedLook = looksByCategory[it]?.firstOrNull() ?: ""; selectedTab = AppTab.Studio; notice = null },
                        onStart = { selectedTab = AppTab.Studio; notice = null }
                    )
                    AppTab.Studio -> StudioScreen(
                        category = selectedCategory,
                        look = selectedLook,
                        photoUri = photoUri,
                        notice = notice,
                        onCategory = { selectedCategory = it; selectedLook = looksByCategory[it]?.firstOrNull() ?: ""; notice = null },
                        onLook = { selectedLook = it; notice = null },
                        onPickPhoto = { photoPicker.launch("image/*") },
                        onPreview = { notice = "Preview flow is ready, but real AI image generation is not connected yet. No generated result has been fabricated." },
                        onSave = {
                            val item = "$selectedCategory • $selectedLook"
                            if (item !in savedLooks) savedLooks = savedLooks + item
                            notice = "Look saved to your local preview list."
                        }
                    )
                    AppTab.Looks -> LooksScreen(savedLooks = savedLooks, onOpenStudio = { selectedTab = AppTab.Studio })
                    AppTab.Profile -> ProfileScreen()
                }
            }
        }
    }
}

@Composable
private fun DiscoverScreen(onCategoryClick: (String) -> Unit, onStart: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Spacer(Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text("BEAUTY, YOUR WAY", color = Muted, fontSize = 11.sp, letterSpacing = 2.sp, fontWeight = FontWeight.SemiBold)
                Text("Beautiva AI", style = MaterialTheme.typography.headlineMedium, color = Ink)
            }
            Box(
                modifier = Modifier.size(46.dp).clip(CircleShape).background(RoseLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.AutoAwesome, contentDescription = null, tint = Rose, modifier = Modifier.size(24.dp))
            }
        }

        HeroCard(onStart = onStart)

        Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Text("Your beauty studio", style = MaterialTheme.typography.titleLarge, color = Ink)
            Text("A little inspiration for your next look.", style = MaterialTheme.typography.bodyMedium, color = Muted)
        }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            categories.chunked(2).forEach { rowCategories ->
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    rowCategories.forEach { category ->
                        CategoryCard(category, Modifier.weight(1f)) { onCategoryClick(category.name) }
                    }
                    if (rowCategories.size == 1) Spacer(Modifier.weight(1f))
                }
            }
        }

        Box(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(Brush.horizontalGradient(listOf(Color(0xFFF6E8E7), Color(0xFFF9F0E9)))).padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(Modifier.size(42.dp).clip(CircleShape).background(White.copy(alpha = 0.85f)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Rounded.Shield, contentDescription = null, tint = Rose)
                }
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text("Your photo, your choice", color = Ink, fontWeight = FontWeight.SemiBold)
                    Text("Privacy-first design. AI processing will be enabled only after secure backend setup.", color = Muted, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun HeroCard(onStart: () -> Unit) {
    Box(
        Modifier.fillMaxWidth().height(242.dp).clip(RoundedCornerShape(28.dp))
            .background(Brush.linearGradient(listOf(Color(0xFFF1DDE0), Color(0xFFE9D6D0), Color(0xFFF7EDE4))))
    ) {
        Box(Modifier.fillMaxSize().padding(22.dp)) {
            Column(
                modifier = Modifier.align(Alignment.CenterStart).fillMaxWidth(0.75f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(color = White.copy(alpha = 0.75f), shape = RoundedCornerShape(50)) {
                    Text("THE AI BEAUTY EDIT", modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp), color = Rose, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                }
                Text("Your beauty,\nreimagined.", color = Ink, style = MaterialTheme.typography.headlineLarge)
                Text("Discover a look that feels like you.", color = Color(0xFF795C61), style = MaterialTheme.typography.bodyMedium)
                Button(
                    onClick = onStart,
                    shape = RoundedCornerShape(50),
                    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 11.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Ink, contentColor = White)
                ) {
                    Text("Explore studio")
                    Spacer(Modifier.width(7.dp))
                    Icon(Icons.Rounded.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                }
            }
            Text("✧", color = White.copy(alpha = 0.8f), fontSize = 90.sp, modifier = Modifier.align(Alignment.TopEnd).offset(x = 8.dp, y = (-18).dp))
            Text("✦", color = Rose.copy(alpha = 0.45f), fontSize = 38.sp, modifier = Modifier.align(Alignment.BottomEnd).offset(x = (-15).dp, y = (-24).dp))
        }
    }
}

@Composable
private fun CategoryCard(category: BeautyCategory, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        color = White,
        border = BorderStroke(1.dp, Line),
        shadowElevation = 1.dp
    ) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                Modifier.fillMaxWidth().height(100.dp).clip(RoundedCornerShape(16.dp))
                    .background(Brush.linearGradient(listOf(Color(0xFFF7E8E8), Color(0xFFF5EDE5)))),
                contentAlignment = Alignment.Center
            ) {
                Text(category.emoji, color = Rose, fontSize = 44.sp, fontWeight = FontWeight.Light)
                Box(Modifier.align(Alignment.TopEnd).padding(8.dp).size(26.dp).clip(CircleShape).background(White.copy(alpha = 0.9f)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Rounded.ArrowOutward, contentDescription = null, tint = Rose, modifier = Modifier.size(14.dp))
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(category.name, color = Ink, style = MaterialTheme.typography.titleMedium)
                Text(category.subtitle, color = Muted, style = MaterialTheme.typography.bodySmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
private fun StudioScreen(
    category: String,
    look: String,
    photoUri: Uri?,
    notice: String?,
    onCategory: (String) -> Unit,
    onLook: (String) -> Unit,
    onPickPhoto: () -> Unit,
    onPreview: () -> Unit,
    onSave: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Spacer(Modifier.height(8.dp))
        Text("AI Beauty Studio", style = MaterialTheme.typography.headlineMedium, color = Ink)
        Text("Choose a photo and explore your next look.", color = Muted, style = MaterialTheme.typography.bodyMedium)

        Surface(shape = RoundedCornerShape(24.dp), color = White, border = BorderStroke(1.dp, Line)) {
            Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(Icons.Rounded.AddAPhoto, contentDescription = null, tint = Rose)
                    Column(Modifier.weight(1f)) {
                        Text("Your photo", color = Ink, style = MaterialTheme.typography.titleMedium)
                        Text("Choose a photo stored on your device.", color = Muted, style = MaterialTheme.typography.bodySmall)
                    }
                }
                if (photoUri != null) {
                    AsyncImage(
                        model = photoUri,
                        contentDescription = "Selected photo",
                        modifier = Modifier.fillMaxWidth().heightIn(max = 300.dp).clip(RoundedCornerShape(18.dp)),
                        contentScale = ContentScale.Fit
                    )
                    Text("Photo selected", color = Rose, style = MaterialTheme.typography.bodySmall)
                } else {
                    Box(
                        Modifier.fillMaxWidth().height(170.dp).clip(RoundedCornerShape(18.dp))
                            .background(Brush.linearGradient(listOf(Color(0xFFF7E7E9), Color(0xFFF7F0E8)))),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Rounded.AddPhotoAlternate, contentDescription = null, tint = Rose, modifier = Modifier.size(38.dp))
                            Text("Add your photo", color = Ink, fontWeight = FontWeight.SemiBold)
                            Text("JPG, PNG or other gallery images", color = Muted, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
                OutlinedButton(
                    onClick = onPickPhoto,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Rose)
                ) {
                    Icon(Icons.Rounded.PhotoLibrary, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(if (photoUri == null) "Choose from gallery" else "Change photo")
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Choose transformation", color = Ink, style = MaterialTheme.typography.titleLarge)
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { item ->
                    FilterChip(
                        selected = category == item.name,
                        onClick = { onCategory(item.name) },
                        label = { Text(item.name) },
                        leadingIcon = if (category == item.name) ({ Icon(Icons.Rounded.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }) else null
                    )
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Select a look", color = Ink, style = MaterialTheme.typography.titleMedium)
            looksByCategory[category].orEmpty().forEach { item ->
                Surface(
                    onClick = { onLook(item) },
                    shape = RoundedCornerShape(16.dp),
                    color = if (look == item) RoseLight else White,
                    border = BorderStroke(1.dp, if (look == item) Rose.copy(alpha = 0.65f) else Line)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 13.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(Modifier.size(38.dp).clip(RoundedCornerShape(12.dp)).background(if (look == item) White else Canvas), contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = when (category) {
                                    "Hairstyle" -> Icons.Rounded.Waves
                                    "Makeup" -> Icons.Rounded.FaceRetouchingNatural
                                    "Outfits" -> Icons.Rounded.Checkroom
                                    else -> Icons.Rounded.Brush
                                },
                                contentDescription = null,
                                tint = Rose
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Text(item, modifier = Modifier.weight(1f), color = Ink, fontWeight = if (look == item) FontWeight.SemiBold else FontWeight.Normal)
                        if (look == item) Icon(Icons.Rounded.CheckCircle, contentDescription = "Selected", tint = Rose)
                    }
                }
            }
        }

        Button(
            onClick = onPreview,
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Ink, contentColor = White)
        ) {
            Icon(Icons.Rounded.AutoAwesome, contentDescription = null)
            Spacer(Modifier.width(9.dp))
            Text("Preview my look")
        }
        OutlinedButton(
            onClick = onSave,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(18.dp),
            border = BorderStroke(1.dp, Rose)
        ) {
            Icon(Icons.Rounded.FavoriteBorder, contentDescription = null, tint = Rose)
            Spacer(Modifier.width(8.dp))
            Text("Save look to My Looks", color = Ink)
        }

        if (notice != null) {
            Surface(color = RoseLight, shape = RoundedCornerShape(16.dp)) {
                Row(Modifier.padding(14.dp), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.Top) {
                    Icon(Icons.Rounded.Info, contentDescription = null, tint = Rose)
                    Text(notice, color = Ink, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
        Text("Note: the current preview build selects photos and looks locally. AI image editing will be connected through a secure backend in the next stage.", color = Muted, style = MaterialTheme.typography.bodySmall)
        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun LooksScreen(savedLooks: List<String>, onOpenStudio: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(Modifier.height(8.dp))
        Text("My Looks", style = MaterialTheme.typography.headlineMedium, color = Ink)
        Text("Your saved style ideas will appear here.", color = Muted)
        if (savedLooks.isEmpty()) {
            Box(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(White).padding(28.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(Modifier.size(66.dp).clip(CircleShape).background(RoseLight), contentAlignment = Alignment.Center) {
                        Icon(Icons.Rounded.FavoriteBorder, contentDescription = null, tint = Rose, modifier = Modifier.size(30.dp))
                    }
                    Text("No saved looks yet", color = Ink, style = MaterialTheme.typography.titleMedium)
                    Text("Choose a look in the Studio and save it here.", color = Muted, style = MaterialTheme.typography.bodyMedium)
                    Button(onClick = onOpenStudio, shape = RoundedCornerShape(50)) { Text("Open Studio") }
                }
            }
        } else {
            savedLooks.forEach { item ->
                Surface(shape = RoundedCornerShape(16.dp), color = White, border = BorderStroke(1.dp, Line)) {
                    Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Favorite, contentDescription = null, tint = Rose)
                        Spacer(Modifier.width(12.dp))
                        Text(item, color = Ink, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileScreen() {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(Modifier.height(8.dp))
        Text("Your space", style = MaterialTheme.typography.headlineMedium, color = Ink)
        Text("A personal place for your beauty journey.", color = Muted)
        Surface(shape = RoundedCornerShape(24.dp), color = White, border = BorderStroke(1.dp, Line)) {
            Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(Modifier.size(54.dp).clip(CircleShape).background(RoseLight), contentAlignment = Alignment.Center) {
                        Icon(Icons.Rounded.PersonOutline, contentDescription = null, tint = Rose, modifier = Modifier.size(28.dp))
                    }
                    Column {
                        Text("Beauty profile", color = Ink, style = MaterialTheme.typography.titleMedium)
                        Text("Personalization options coming next", color = Muted, style = MaterialTheme.typography.bodySmall)
                    }
                }
                HorizontalDivider(color = Line)
                ProfileRow(Icons.Rounded.Lock, "Privacy & photo controls", "Planned for secure AI integration")
                ProfileRow(Icons.Rounded.Settings, "App preferences", "Theme and language settings planned")
                ProfileRow(Icons.Rounded.HelpOutline, "Help & feedback", "Support screen planned")
            }
        }
        Surface(color = RoseLight, shape = RoundedCornerShape(18.dp)) {
            Text(
                "Beautiva AI is a preview starter. Sign-in, cloud sync and AI processing are not enabled in this build.",
                modifier = Modifier.padding(16.dp),
                color = Ink,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun ProfileRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Icon(icon, contentDescription = null, tint = Rose)
        Column(verticalArrangement = Arrangement.spacedBy(2.dp), modifier = Modifier.weight(1f)) {
            Text(title, color = Ink, fontWeight = FontWeight.Medium)
            Text(subtitle, color = Muted, style = MaterialTheme.typography.bodySmall)
        }
        Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = Muted)
    }
}
