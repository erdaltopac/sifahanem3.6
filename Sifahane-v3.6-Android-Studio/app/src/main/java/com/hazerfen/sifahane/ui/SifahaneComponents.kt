package com.hazerfen.sifahane.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Icon
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.draw.drawWithContent

val SifahaneLogoGreen = Color(0xFF72D4CD)
val SifahaneVantablack = Color(0xFF050505)
val SifahaneTurkishBlue = Color(0xFF00AEEF)

fun Modifier.sifahaneSoftBoundary(edgeSize: Dp = 2.dp): Modifier = drawWithContent {
    drawContent()
    val edge = edgeSize.toPx().coerceAtLeast(2f)
    val dark = SifahaneVantablack.copy(alpha = 0.10f)
    val middle = SifahaneVantablack.copy(alpha = 0.05f)
    drawRect(
        brush = Brush.verticalGradient(
            listOf(dark, middle, Color.Transparent),
            startY = 0f,
            endY = edge
        ),
        size = androidx.compose.ui.geometry.Size(size.width, edge)
    )
    drawRect(
        brush = Brush.verticalGradient(
            listOf(Color.Transparent, middle, dark),
            startY = size.height - edge,
            endY = size.height
        ),
        topLeft = androidx.compose.ui.geometry.Offset(0f, size.height - edge),
        size = androidx.compose.ui.geometry.Size(size.width, edge)
    )
    drawRect(
        brush = Brush.horizontalGradient(
            listOf(dark, middle, Color.Transparent),
            startX = 0f,
            endX = edge
        ),
        size = androidx.compose.ui.geometry.Size(edge, size.height)
    )
    drawRect(
        brush = Brush.horizontalGradient(
            listOf(Color.Transparent, middle, dark),
            startX = size.width - edge,
            endX = size.width
        ),
        topLeft = androidx.compose.ui.geometry.Offset(size.width - edge, 0f),
        size = androidx.compose.ui.geometry.Size(edge, size.height)
    )
}

@Composable
fun SifahaneCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(16.dp)
    Box(
        modifier = modifier
            .sifahaneSoftBoundary(2.dp)
            .padding(2.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
            shape = shape,
            border = BorderStroke(
                1.2.dp,
                Brush.linearGradient(
                    listOf(
                        SifahaneVantablack.copy(alpha = 0.28f),
                        SifahaneLogoGreen.copy(alpha = 0.62f)
                    )
                )
            ),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Box(
                modifier = Modifier
                    .border(0.7.dp, SifahaneTurkishBlue.copy(alpha = 0.50f), shape)
                    .padding(1.dp),
                content = content
            )
        }
    }
}

@Composable
fun OutlinedLogoIcon(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    size: Dp = 28.dp
) {
    Box(modifier = modifier.size(size + 2.dp), contentAlignment = Alignment.Center) {
        Icon(
            imageVector,
            contentDescription = null,
            tint = SifahaneVantablack.copy(alpha = 0.50f),
            modifier = Modifier.size(size + 2.dp)
        )
        Icon(
            imageVector,
            contentDescription = contentDescription,
            tint = SifahaneLogoGreen,
            modifier = Modifier.size(size)
        )
    }
}
