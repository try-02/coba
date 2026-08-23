package com.sentral.org.ui.screen.pos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PosProductItem(
    product: ProductUi,
    onAddToCart: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val avatarColor = avatarColors[(product.colorSeed % avatarColors.size).toInt()]

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp),
        tonalElevation = 0.dp,
        color = MaterialTheme.colorScheme.surface,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // ── LEFT: Artwork / Avatar ──
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(avatarColor),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = product.name.take(1).uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                )
            }

            Spacer(Modifier.width(10.dp))

            // ── CENTER: Name + metadata ──
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(1.dp),
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = buildString {
                        append(product.category)
                        if (product.sku.isNotBlank()) {
                            append(" · ${product.sku}")
                        }
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            // ── RIGHT: Price + Add button ──
            Text(
                text = product.priceFormatted,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(end = 8.dp),
            )

            // Compact add button with minimum touch target
            Surface(
                onClick = onAddToCart,
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier
                    .sizeIn(
                        minWidth = LocalMinimumInteractiveComponentSize,
                        minHeight = LocalMinimumInteractiveComponentSize,
                    )
                    .defaultMinSize(
                        minWidth = LocalMinimumInteractiveComponentSize,
                        minHeight = LocalMinimumInteractiveComponentSize,
                    ),
            ) {
                Box(
                    modifier = Modifier
                        .width(32.dp)
                        .height(32.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Tambah",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        }
    }
}
