package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BadgeAchievement
import com.example.ui.theme.VibrantMintContainer
import com.example.ui.theme.VibrantMintDark
import com.example.ui.theme.VibrantPurpleDark
import com.example.ui.theme.VibrantPurplePrimary
import com.example.ui.theme.VibrantSunsetOrange

@Composable
fun BadgeAchievementCard(
  badge: BadgeAchievement,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (badge.isUnlocked) {
        VibrantMintContainer
      } else {
        MaterialTheme.colorScheme.surface
      }
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = if (badge.isUnlocked) 0.dp else 1.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(48.dp)
          .clip(RoundedCornerShape(16.dp))
          .background(
            if (badge.isUnlocked) Color.White else MaterialTheme.colorScheme.surfaceVariant
          ),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = badge.iconSymbol,
          fontSize = 24.sp
        )
      }

      Spacer(modifier = Modifier.width(14.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = badge.title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface
          )

          Surface(
            shape = RoundedCornerShape(10.dp),
            color = if (badge.isUnlocked) Color.White else MaterialTheme.colorScheme.surfaceVariant
          ) {
            Text(
              text = if (badge.isUnlocked) "UNLOCKED" else "${(badge.progressPercent * 100).toInt()}%",
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
              style = MaterialTheme.typography.labelSmall,
              fontSize = 10.sp,
              fontWeight = FontWeight.ExtraBold,
              color = if (badge.isUnlocked) VibrantMintDark else MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
          text = badge.description,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          fontSize = 12.sp
        )

        if (!badge.isUnlocked) {
          Spacer(modifier = Modifier.height(8.dp))
          LinearProgressIndicator(
            progress = { badge.progressPercent },
            modifier = Modifier
              .fillMaxWidth()
              .height(6.dp)
              .clip(RoundedCornerShape(3.dp)),
            color = VibrantPurplePrimary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
          )
        }
      }
    }
  }
}
