package com.example.ogym_kmp.core.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ogym_kmp.core.designsystem.theme.OGymTheme

data class OGymBottomBarItem(
    val label: String,
    val icon: ImageVector,
)

/** Floating pill navigation bar; the selected tab is a light pill inside a dark one. */
@Composable
fun OGymBottomBar(
    items: List<OGymBottomBarItem>,
    selectedLabel: String,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = OGymTheme.colors
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 16.dp),
        shape = RoundedCornerShape(24.dp),
        color = colors.floatingBar,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
    ) {
        Row(
            modifier = Modifier.padding(6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items.forEach { item ->
                val selected = item.label == selectedLabel
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(if (selected) colors.floatingBarSelected else Color.Transparent)
                        .selectable(
                            selected = selected,
                            role = Role.Tab,
                            onClick = { onItemSelected(item.label) },
                        ),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = if (selected) MaterialTheme.colorScheme.primary else colors.onFloatingBar,
                    )
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (selected) colors.onFloatingBarSelected else colors.onFloatingBar,
                        fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Bold,
                    )
                }
            }
        }
    }
}
