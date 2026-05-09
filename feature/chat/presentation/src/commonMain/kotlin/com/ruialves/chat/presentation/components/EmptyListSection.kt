package com.ruialves.chat.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import chirp.feature.chat.presentation.generated.resources.Res
import chirp.feature.chat.presentation.generated.resources.empty_chat
import com.ruialves.core.designsystem.theme.ChirpTheme
import com.ruialves.core.designsystem.theme.extended
import com.ruialves.core.presentation.util.DeviceConfiguration
import com.ruialves.core.presentation.util.VerticalSpacer
import com.ruialves.core.presentation.util.currentDeviceConfiguration
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun EmptyListSection(
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    val configuration = currentDeviceConfiguration()
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(Res.drawable.empty_chat),
            contentDescription = title,
            modifier = Modifier.size(
                when(configuration){
                    DeviceConfiguration.MOBILE_LANDSCAPE -> 125.dp
                    else -> 200.dp
                }
            )
        )
        VerticalSpacer(4.dp)
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.extended.textPrimary
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.extended.textSecondary
        )
    }
}

@Composable
@Preview
fun EmptyChatSectionPreview() {
    ChirpTheme {
        EmptyListSection(
            title = "No messages",
            description = "Be the first one to send a message"
        )
    }
}
