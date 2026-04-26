package com.ruialves.core.designsystem.components.avatar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.ruialves.core.designsystem.theme.ChirpTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ChirpStackedAvatars(
    avatars: List<AvatarUi>,
    modifier: Modifier = Modifier,
    size: AvatarSize = AvatarSize.SMALL,
    maxVisible: Int = 2,
    overlapPercentage: Float = 0.4f
) {
    val overlapOffset = -(size.dp * overlapPercentage)
    val visibleAvatars = avatars.take(maxVisible)
    val remainingCount = avatars.size - maxVisible.coerceAtLeast(0)

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(overlapOffset),
        verticalAlignment = Alignment.CenterVertically
    ) {
        visibleAvatars.forEach { avatar ->
            ChirpAvatarPhoto(
                displayText = avatar.initials,
                size = size,
                imageUrl = avatar.imageUrl
            )
        }

        if (remainingCount > 0) {
            ChirpAvatarPhoto(
                displayText = "$remainingCount+",
                size = size,
                textColor = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
@Preview
fun ChirpStackedAvatarsPreview() {
    ChirpTheme {
        ChirpStackedAvatars(
            avatars = listOf(
                AvatarUi(
                    id = "1",
                    username = "Rui",
                    initials = "RA",
                ),
                AvatarUi(
                    id = "2",
                    username = "John",
                    initials = "JA",
                ),
                AvatarUi(
                    id = "3",
                    username = "Brian",
                    initials = "BM",
                )
            )
        )
    }
}
