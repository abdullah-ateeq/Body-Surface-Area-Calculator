package com.example.bodysurfaceareacalculator.ui.screens.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bodysurfaceareacalculator.ui.components.AbstractSilhouetteGraphic
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val animProgress = remember { Animatable(0f) }
    val titleAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Animation sequence takes 1.3 seconds
        animProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1100, easing = FastOutSlowInEasing)
        )
        titleAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 300)
        )
        delay(150)
        onSplashFinished()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .semantics {
                contentDescription = "Body Surface Area Calculator Splash Screen"
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            AbstractSilhouetteGraphic(
                progress = animProgress.value
            )

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "BODY SURFACE AREA",
                style = MaterialTheme.typography.titleMedium,
                letterSpacing = 3.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.alpha(titleAlpha.value)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "CALCULATOR",
                style = MaterialTheme.typography.headlineMedium,
                letterSpacing = 1.sp,
                fontWeight = FontWeight.Light,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.alpha(titleAlpha.value)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Clinical Estimation Utility",
                style = MaterialTheme.typography.bodySmall,
                letterSpacing = 1.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.alpha(titleAlpha.value)
            )
        }
    }
}
