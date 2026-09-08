package com.salmantoha.neolauncher.ui.drawer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.salmantoha.neolauncher.model.AppCategory
import com.salmantoha.neolauncher.model.AppItem
import com.salmantoha.neolauncher.ui.components.NeoAppIcon
import com.salmantoha.neolauncher.ui.components.NeoBrutalCard
import com.salmantoha.neolauncher.ui.components.NeoCategoryChips
import com.salmantoha.neolauncher.ui.components.NeoDotGridBackground
import com.salmantoha.neolauncher.ui.components.NeoSearchBar
import com.salmantoha.neolauncher.ui.theme.AccentIndigo
import com.salmantoha.neolauncher.ui.theme.BgAmoled
import com.salmantoha.neolauncher.ui.theme.TextMuted
import com.salmantoha.neolauncher.ui.theme.TextPrimary
import com.salmantoha.neolauncher.ui.theme.TextSecondary
import java.util.Locale

@Composable
fun AppDrawerScreen(
    apps: List<AppItem>,
    onAppClick: (AppItem) -> Unit,
    onAppLongClick: (AppItem) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(AppCategory.ALL) }
    var dragOffsetY by remember { mutableStateOf(0f) }

    val filteredApps = remember(apps, searchQuery, selectedCategory) {
        apps.filter { app ->
            val matchesCategory = (selectedCategory == AppCategory.ALL) || (app.category == selectedCategory)
            val matchesQuery = searchQuery.isBlank() ||
                    app.label.lowercase(Locale.ROOT).contains(searchQuery.lowercase(Locale.ROOT)) ||
                    app.packageName.lowercase(Locale.ROOT).contains(searchQuery.lowercase(Locale.ROOT))
            matchesCategory && matchesQuery
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BgAmoled)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClose
            )
            .draggable(
                orientation = Orientation.Vertical,
                state = rememberDraggableState { delta ->
                    dragOffsetY += delta
                },
                onDragStopped = {
                    if (dragOffsetY > 60f) {
                        onClose()
                    }
                    dragOffsetY = 0f
                }
            )
    ) {
        NeoDotGridBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(top = 8.dp, start = 16.dp, end = 16.dp, bottom = 8.dp)
        ) {
            // Drag handle / close bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NeoBrutalCard(
                    shadowOffset = 2.dp,
                    cornerRadius = 8.dp,
                    borderWidth = 1.5.dp,
                    onClick = onClose
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Close Drawer",
                            tint = AccentIndigo,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "SWIPE DOWN TO CLOSE",
                            style = TextStyle(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp,
                                color = TextSecondary
                            )
                        )
                    }
                }

                NeoBrutalCard(
                    shadowOffset = 2.dp,
                    cornerRadius = 8.dp,
                    borderWidth = 1.5.dp,
                    onClick = onClose
                ) {
                    Box(modifier = Modifier.padding(6.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Search Bar
            NeoSearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                onClear = { searchQuery = "" }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Category Filter Pills
            NeoCategoryChips(
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it },
                modifier = Modifier.padding(horizontal = 0.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // App count badge
            Text(
                text = "// ${filteredApps.size} APPLICATIONS FOUND",
                style = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = AccentIndigo
                ),
                modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
            )

            if (filteredApps.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    NeoBrutalCard(
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = 16.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "[NO RESULTS FOUND]",
                                style = TextStyle(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = TextPrimary
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Try adjusting your search query or filter",
                                style = TextStyle(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            )
                        }
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(filteredApps, key = { it.packageName + it.activityName }) { app ->
                        NeoAppIcon(
                            app = app,
                            onClick = { onAppClick(app) },
                            onLongClick = { onAppLongClick(app) }
                        )
                    }
                }
            }
        }
    }
}
