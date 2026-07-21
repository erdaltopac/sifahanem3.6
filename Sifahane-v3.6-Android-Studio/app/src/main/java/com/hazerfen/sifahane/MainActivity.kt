package com.hazerfen.sifahane
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Brush
import androidx.compose.animation.animateColorAsState
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.LinearEasing
import com.hazerfen.sifahane.alarm.AlarmRescheduler
import com.hazerfen.sifahane.alarm.AlarmRefreshResult
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlinx.coroutines.Job
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.Animatable
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import com.hazerfen.sifahane.security.AdminPinStore
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.Lifecycle
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.geometry.Size
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
import com.hazerfen.sifahane.security.PatternStore
import com.hazerfen.sifahane.security.AdminCredentialHasher
import com.hazerfen.sifahane.security.UserRoles
import androidx.compose.ui.graphics.Shadow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import com.hazerfen.sifahane.ui.SifahaneTheme
import com.hazerfen.sifahane.ui.ThemeConfiguration
import com.hazerfen.sifahane.ui.ThemePreferences
import com.hazerfen.sifahane.ui.SifahaneCard
import com.hazerfen.sifahane.ui.OutlinedLogoIcon
import com.hazerfen.sifahane.ui.sifahaneSoftBoundary
import org.json.JSONObject
import org.json.JSONArray
import java.util.zip.ZipOutputStream
import java.util.zip.ZipInputStream
import java.util.zip.ZipEntry
import java.io.BufferedOutputStream
import java.io.BufferedInputStream
import androidx.activity.result.contract.ActivityResultContracts.OpenDocument
import androidx.activity.result.contract.ActivityResultContracts.OpenDocumentTree
import androidx.activity.result.contract.ActivityResultContracts.CreateDocument
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.foundation.background
import androidx.compose.ui.zIndex
import androidx.compose.ui.draw.alpha
import kotlinx.coroutines.delay
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.CircleShape
import android.graphics.Color as AndroidColor
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import android.app.Activity
import android.view.WindowManager
import com.google.mlkit.vision.barcode.common.Barcode
import kotlin.math.min
import kotlin.math.max
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.Canvas
import android.provider.OpenableColumns
import android.graphics.RectF
import android.graphics.Paint
import android.graphics.Matrix
import android.graphics.Canvas
import java.io.FileOutputStream
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.foundation.gestures.detectTransformGestures
import android.graphics.BitmapFactory
import android.graphics.Bitmap

import android.app.*
import android.content.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.room.withTransaction
import coil.compose.rememberAsyncImagePainter
import com.hazerfen.sifahane.alarm.AlarmScheduler
import com.hazerfen.sifahane.alarm.AppointmentPreferences
import com.hazerfen.sifahane.alarm.AppointmentAlarmScheduler
import com.hazerfen.sifahane.data.*
import com.hazerfen.sifahane.backup.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
        setContent {
            SifahaneTheme {
                var showSplash by remember { mutableStateOf(true) }
                LaunchedEffect(Unit) {
                    delay(2000)
                    showSplash = false
                }
                if (showSplash) {
                    SplashScreen()
                } else {
                    SifahaneRoot()
                }
            }
        }
    }
}


@Composable
private fun SplashScreen() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val navigationBarHeight = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .clickable {
                runCatching {
                    context.startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://www.hazerfen.com.tr")
                        )
                    )
                }
            }
            .padding(horizontal = 22.dp)
            .padding(top = navigationBarHeight, bottom = navigationBarHeight)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                color = LogoColor,
                textAlign = TextAlign.Center,
                maxLines = 1,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    shadow = LogoTextShadow
                )
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Bismillâhirrahmânirrahîm",
                color = LogoColorDark,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    shadow = LogoTextShadow
                )
            )
            Spacer(Modifier.height(3.dp))
            Text(
                text = "İyiliği sonsuz, ikramı bol Allah’ın adıyla.",
                color = LogoColorDark,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium.copy(
                    shadow = LogoTextShadow
                )
            )
        }

        Image(
            painter = rememberAsyncImagePainter(R.drawable.sifahane_logo),
            contentDescription = "Şifahane logosu",
            modifier = Modifier
                .fillMaxSize(0.72f)
                .align(Alignment.Center)
                .alpha(0.25f),
            contentScale = ContentScale.Fit
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Geliştirici",
                color = LogoColorDark,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    shadow = LogoTextShadow
                )
            )
            Text(
                text = "Erdal Topaç",
                color = LogoColor,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    shadow = LogoTextShadow
                )
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = "Ücretsizdir",
                color = LogoColorDark.copy(alpha = 0.50f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontStyle = FontStyle.Italic,
                    shadow = LogoTextShadow
                )
            )
        }
    }
}

@Composable
private fun PartnerLogoBanner() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val density = LocalDensity.current
    val displayMetrics = context.resources.displayMetrics
    val physicalYdpi = displayMetrics.ydpi.takeIf { it.isFinite() && it > 0f }
        ?: displayMetrics.densityDpi.toFloat()
    val combinedHeight = with(density) { (physicalYdpi * 7.5f / 25.4f).toDp() }
    val transitionHeight = 8.dp
    val bannerHeight = (combinedHeight - transitionHeight).coerceAtLeast(1.dp)
    val logoHeight = bannerHeight * 0.95f
    // Son kaynak görsel 1200x250 pikseldir; genişlik yalnız yükseklik ve özgün
    // en-boy oranından üretilir. Böylece hiçbir kopya esnetilmez veya küçülmez.
    val logoWidth = logoHeight * (1200f / 250f)
    val logoWidthPx = with(density) { logoWidth.toPx() }
    // Akış hızı cihaz ekranının genişliğine göre belirlenir: beş dakikada bir ekran.
    val animationDurationMillis = (
        300_000f * logoWidthPx / displayMetrics.widthPixels.coerceAtLeast(1)
    ).roundToInt().coerceAtLeast(1)
    val animationsEnabled = remember { android.animation.ValueAnimator.areAnimatorsEnabled() }
    val transition = rememberInfiniteTransition(label = "partnerLogoBanner")
    val offset by transition.animateFloat(
        initialValue = 0f,
        targetValue = if (animationsEnabled) -logoWidthPx else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = animationDurationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "partnerLogoOffset"
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(combinedHeight)
            .clipToBounds()
            .clickable {
                runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.hazerfen.com.tr"))) }
            }
            .semantics { contentDescription = "Hazerfen kuruluş logoları. İnternet sitesini harici tarayıcıda açar." }
    ) {
        Column(Modifier.fillMaxSize()) {
            BottomShellSoftTransition(mirrored = true, height = transitionHeight)
            BoxWithConstraints(
                Modifier
                    .fillMaxWidth()
                    .height(bannerHeight)
                    .background(Color.White)
            ) {
                val copies = (maxWidth / logoWidth).toInt() + 3
                Row(
                    modifier = Modifier
                        .requiredWidth(logoWidth * copies)
                        .fillMaxHeight()
                        .graphicsLayer { translationX = offset },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    repeat(copies) {
                        Image(
                            painter = painterResource(R.drawable.banner_hazerfen),
                            contentDescription = null,
                            modifier = Modifier
                                .width(logoWidth)
                                .height(logoHeight)
                                .alpha(1.00f),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            }
        }
        // Tek cam katmanı hem yumuşak geçişi hem de kayan logo alanını kaplar.
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Color(0xFF050505).copy(alpha = 0.05f))
        )
    }
}

@Composable
private fun BottomShellSoftTransition(
    mirrored: Boolean,
    height: androidx.compose.ui.unit.Dp = 8.dp
) {
    val colors = listOf(
        Color(0xFF050505).copy(alpha = 0.10f),
        Color(0xFF050505).copy(alpha = 0.05f),
        Color.Transparent
    ).let { if (mirrored) it.reversed() else it }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .background(Brush.verticalGradient(colors))
    )
}

@Composable
private fun GlassLogoButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val shape = StandardFieldShape
    Box(
        modifier = modifier
            .height(52.dp)
            .sifahaneSoftBoundary(4.dp)
    ) {
        Button(
            onClick = onClick,
            enabled = enabled,
            modifier = Modifier.fillMaxSize(),
            shape = shape,
            colors = ButtonDefaults.buttonColors(
                containerColor = LogoColor.copy(alpha = 0.25f),
                contentColor = LogoColorDark,
                disabledContainerColor = LogoColor.copy(alpha = 0.12f),
                disabledContentColor = LogoColorDark.copy(alpha = 0.35f)
            )
        ) {
            Text(
                text = text,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    shadow = LogoTextShadow
                )
            )
        }
        Box(
            Modifier
                .matchParentSize()
                .clip(shape)
                .background(Color(0xFF050505).copy(alpha = 0.05f))
        )
    }
}

@Composable
private fun PlainImportOption(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextButton(
        onClick = onClick,
        modifier = modifier.heightIn(min = 48.dp),
        colors = ButtonDefaults.textButtonColors(
            containerColor = Color.Transparent,
            contentColor = LogoColorDark
        )
    ) {
        Text(
            text = text,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            maxLines = 2,
            style = MaterialTheme.typography.labelMedium.copy(shadow = LogoTextShadow)
        )
    }
}

private fun Modifier.vantablackGlassOverlay(): Modifier =
    clip(RoundedCornerShape(10.dp)).drawWithContent {
    drawContent()
    drawRect(Color(0xFF050505).copy(alpha = 0.05f))
}

private fun Modifier.vantablackPageGlassOverlay(): Modifier = drawWithContent {
    drawContent()
    drawRect(Color(0xFF050505).copy(alpha = 0.05f))
}

@Composable
private fun WatermarkContainer(content: @Composable BoxScope.() -> Unit) {
    Box(Modifier.fillMaxSize().sifahaneSoftBoundary(2.dp)) {
        Image(
            painter = rememberAsyncImagePainter(R.drawable.sifahane_logo),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center)
                .alpha(0.15f)
                .zIndex(0f),
            contentScale = ContentScale.Fit
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(1f),
            content = content
        )
    }
}


private val LogoColor = Color(0xFF72D4CD)
private val LogoColorDark = Color(0xFF050505)
private val Vantablack10 = Color(0x1A000000)
private val Vantablack05 = Color(0x0D000000)
private val LogoColorSoft = Color(0x1A72D4CD)
private val LogoTextShadow = Shadow(
    color = Color(0xFF050505),
    offset = Offset(0.35f, 0.35f),
    blurRadius = 0.45f
)

@Composable
private fun SifahanePageTitle(
    activeUser: String,
    pageKey: Any
) {
    var showSideLogos by remember(pageKey, activeUser) {
        mutableStateOf(true)
    }

    LaunchedEffect(pageKey, activeUser) {
        while (true) {
            showSideLogos = true
            delay(5000)
            showSideLogos = false

            val now = System.currentTimeMillis()
            val untilNextMinute = 60_000L - (now % 60_000L)
            delay(untilNextMinute)
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (showSideLogos) {
                Image(
                    painter = rememberAsyncImagePainter(R.drawable.sifahane_logo),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    contentScale = ContentScale.Fit
                )
                Spacer(Modifier.width(8.dp))
            }

            Text(
                text = "Şifahane",
                color = LogoColor,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontFamily = FontFamily.Serif,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.SemiBold,
                    shadow = LogoTextShadow
                ),
                textAlign = TextAlign.Center
            )

            if (showSideLogos) {
                Spacer(Modifier.width(8.dp))
                Image(
                    painter = rememberAsyncImagePainter(R.drawable.sifahane_logo),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }

        if (activeUser.isNotBlank()) {
            Text(
                text = activeUser,
                style = MaterialTheme.typography.bodyMedium.copy(
                    shadow = LogoTextShadow
                ),
                color = LogoColorDark,
                textAlign = TextAlign.Center
            )
        }
    }
}




@Composable
private fun logoFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = Vantablack10,
    unfocusedContainerColor = Vantablack10,
    disabledContainerColor = Vantablack10,
    focusedTextColor = LogoColorDark,
    unfocusedTextColor = LogoColorDark,
    focusedLabelColor = LogoColorDark,
    unfocusedLabelColor = LogoColorDark,
    focusedBorderColor = LogoColor,
    unfocusedBorderColor = LogoColorDark.copy(alpha = 0.55f),
    cursorColor = LogoColorDark
)


@Composable
private fun medicationFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = Vantablack05,
    unfocusedContainerColor = Vantablack05,
    disabledContainerColor = Vantablack05,
    focusedTextColor = LogoColorDark,
    unfocusedTextColor = LogoColorDark,
    disabledTextColor = LogoColorDark.copy(alpha = 0.65f),
    focusedLabelColor = LogoColor,
    unfocusedLabelColor = LogoColorDark,
    focusedBorderColor = LogoColor,
    unfocusedBorderColor = LogoColorDark.copy(alpha = 0.55f),
    cursorColor = LogoColorDark
)

@Composable
private fun medicationOutlinedButtonColors() =
    ButtonDefaults.outlinedButtonColors(
        containerColor = Vantablack05,
        contentColor = LogoColorDark,
        disabledContainerColor = Vantablack05,
        disabledContentColor = LogoColorDark.copy(alpha = 0.45f)
    )

@Composable
private fun MedicationCheckBox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: String,
    centered: Boolean = false,
    boldWhenChecked: Boolean = false,
    emphasized: Boolean? = null,
    checkedColor: Color = LogoColor
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (centered) Arrangement.Center else Arrangement.Start,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = if (checked) {
                Modifier.shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(4.dp),
                    ambientColor = Color.Black,
                    spotColor = Color.Black
                )
            } else {
                Modifier
            }
        ) {
            Checkbox(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = checkedColor,
                    uncheckedColor = LogoColorDark,
                    checkmarkColor = Color.Black
                )
            )
        }
        Text(
            text = label,
            color = LogoColorDark,
            style = (if (centered) MaterialTheme.typography.labelLarge else MaterialTheme.typography.bodyLarge).copy(
                fontWeight = if (emphasized ?: (boldWhenChecked && checked)) FontWeight.Bold else FontWeight.Normal,
                shadow = LogoTextShadow
            )
        )
    }
}


@Composable
private fun profileFieldColors() = standardFieldColors()

@Composable
private fun profileOutlinedButtonColors() =
    ButtonDefaults.outlinedButtonColors(
        containerColor = Vantablack05,
        contentColor = LogoColorDark,
        disabledContainerColor = Vantablack05,
        disabledContentColor = LogoColorDark.copy(alpha = 0.45f)
    )

@Composable
private fun IdleHeartOverlay(
    visible: Boolean,
    animationKey: Long
) {
    if (!visible) return

    val scale = remember(animationKey) { Animatable(0.05f) }
    val alpha = remember(animationKey) { Animatable(0f) }

    LaunchedEffect(animationKey) {
        alpha.snapTo(0f)
        scale.snapTo(0.05f)

        alpha.animateTo(0.25f, tween(250))
        scale.animateTo(1.00f, tween(900))
        scale.animateTo(0.88f, tween(220))
        scale.animateTo(1.00f, tween(220))
        scale.animateTo(0.88f, tween(220))
        scale.animateTo(1.00f, tween(220))
        delay(450)
        alpha.animateTo(0f, tween(350))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
                this.alpha = alpha.value
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = null,
            tint = LogoColor,
            modifier = Modifier.fillMaxSize(0.92f)
        )
    }
}


private fun titleCaseTr(value: String): String {
    val locale = Locale("tr", "TR")
    return value.lowercase(locale)
        .split(Regex("\\s+"))
        .joinToString(" ") { word ->
            if (word.isBlank()) word
            else word.substring(0, 1).uppercase(locale) + word.substring(1)
        }
}

@Composable
private fun ThemedDateButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth(),
    medicationStyle: Boolean = false
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        border = BorderStroke(1.5.dp, LogoColor),
        colors = if (medicationStyle) {
            ButtonDefaults.outlinedButtonColors(
                containerColor = Vantablack05,
                contentColor = LogoColorDark
            )
        } else {
            profileOutlinedButtonColors()
        }
    ) {
        Text(
            text = text,
            color = LogoColorDark,
            style = MaterialTheme.typography.labelLarge.copy(
                shadow = LogoTextShadow
            )
        )
    }
}

@Composable
private fun VitalLineChart(
    title: String,
    series: List<Pair<String, List<Pair<Long, Float>>>>,
    modifier: Modifier = Modifier
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    SifahaneCard(modifier = modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp)) {
            Text(
                titleCaseTr(title),
                color = LogoColorDark,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    shadow = LogoTextShadow
                )
            )
            Spacer(Modifier.height(8.dp))

            val allValues = series.flatMap { it.second }.map { it.second }
            if (allValues.isEmpty()) {
                Text("Seçilen tarih aralığında kayıt bulunamadı.")
            } else {
                val minValue = allValues.minOrNull() ?: 0f
                val maxValue = allValues.maxOrNull() ?: 1f
                val span = (maxValue - minValue).takeIf { abs(it) > 0.001f } ?: 1f
                val allTimes = series.flatMap { it.second }.map { it.first }
                val minTime = allTimes.minOrNull() ?: 0L
                val maxTime = allTimes.maxOrNull() ?: minTime + 1L
                val timeSpan = (maxTime - minTime).coerceAtLeast(1L)

                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .background(Color.White.copy(alpha = 0.42f), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    val left = 58f
                    val right = size.width - 8f
                    val top = 8f
                    val bottom = size.height - 30f
                    val plotWidth = (right - left).coerceAtLeast(1f)
                    val plotHeight = (bottom - top).coerceAtLeast(1f)
                    val labelPaint = Paint().apply {
                        color = AndroidColor.rgb(58, 143, 137)
                        textSize = 22f
                        isAntiAlias = true
                    }
                    repeat(5) { tick ->
                        val ratio = tick / 4f
                        val y = bottom - ratio * plotHeight
                        val value = minValue + ratio * span
                        drawLine(
                            color = Color.Black.copy(alpha = 0.10f),
                            start = Offset(left, y), end = Offset(right, y), strokeWidth = 1f
                        )
                        drawContext.canvas.nativeCanvas.drawText("%.0f".format(value), 2f, y + 7f, labelPaint)
                    }
                    val dateFormat = SimpleDateFormat("dd.MM", Locale.getDefault())
                    repeat(3) { tick ->
                        val ratio = tick / 2f
                        val x = left + ratio * plotWidth
                        val time = minTime + (timeSpan * ratio).toLong()
                        val label = dateFormat.format(Date(time))
                        val width = labelPaint.measureText(label)
                        drawContext.canvas.nativeCanvas.drawText(label, (x - width / 2).coerceIn(0f, size.width - width), size.height - 3f, labelPaint)
                    }
                    drawLine(
                        color = LogoColorDark.copy(alpha = 0.55f),
                        start = Offset(left, bottom),
                        end = Offset(right, bottom),
                        strokeWidth = 2f
                    )
                    drawLine(
                        color = LogoColorDark.copy(alpha = 0.55f),
                        start = Offset(left, top),
                        end = Offset(left, bottom),
                        strokeWidth = 2f
                    )

                    series.forEachIndexed { index, (_, points) ->
                        val lineColor = if (index % 2 == 0) LogoColor else LogoColorDark
                        points.sortedBy { it.first }.zipWithNext().forEach { (a, b) ->
                            val ax = left + ((a.first - minTime).toFloat() / timeSpan.toFloat()) * plotWidth
                            val bx = left + ((b.first - minTime).toFloat() / timeSpan.toFloat()) * plotWidth
                            val ay = bottom - ((a.second - minValue) / span) * plotHeight
                            val by = bottom - ((b.second - minValue) / span) * plotHeight
                            drawLine(
                                color = lineColor,
                                start = Offset(ax, ay),
                                end = Offset(bx, by),
                                strokeWidth = 6f,
                                cap = StrokeCap.Round
                            )
                        }
                        points.forEach { point ->
                            val x = left + ((point.first - minTime).toFloat() / timeSpan.toFloat()) * plotWidth
                            val y = bottom - ((point.second - minValue) / span) * plotHeight
                            drawCircle(lineColor, radius = 6f, center = Offset(x, y))
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))
                series.forEachIndexed { index, (name, _) ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier
                                .size(12.dp)
                                .background(
                                    if (index % 2 == 0) LogoColor else LogoColorDark,
                                    CircleShape
                                )
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(titleCaseTr(name), color = LogoColorDark)
                    }
                }
                Text(
                    "En düşük: ${"%.1f".format(minValue)} • En yüksek: ${"%.1f".format(maxValue)}",
                    color = LogoColorDark,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val file = createVitalChartPng(context, title, series)
                            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                            context.startActivity(Intent.createChooser(
                                Intent(Intent.ACTION_SEND).apply {
                                    type = "image/png"
                                    putExtra(Intent.EXTRA_STREAM, uri)
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    clipData = android.content.ClipData.newUri(context.contentResolver, file.name, uri)
                                }, "Grafik raporunu paylaş"
                            ))
                        },
                        modifier = Modifier.weight(1f)
                    ) { Icon(Icons.Default.Share, null); Spacer(Modifier.width(4.dp)); Text("PAYLAŞ") }
                    OutlinedButton(
                        onClick = {
                            val file = createVitalChartPng(context, title, series)
                            saveChartToGallery(context, file)
                        },
                        modifier = Modifier.weight(1f)
                    ) { Icon(Icons.Default.SaveAlt, null); Spacer(Modifier.width(4.dp)); Text("GALERİ") }
                }
            }
        }
    }
}

private fun createVitalChartPng(
    context: Context,
    title: String,
    series: List<Pair<String, List<Pair<Long, Float>>>>
): File {
    val bitmap = Bitmap.createBitmap(1600, 1000, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    canvas.drawColor(AndroidColor.WHITE)
    val text = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = AndroidColor.rgb(5, 5, 5) }
    text.textSize = 54f; text.isFakeBoldText = true
    canvas.drawText(titleCaseTr(title), 80f, 90f, text)
    text.textSize = 28f; text.isFakeBoldText = false
    canvas.drawText("Şifahane • ${BuildConfig.VERSION_NAME} • ${formatDateTime(System.currentTimeMillis())}", 80f, 140f, text)
    val values = series.flatMap { it.second }.map { it.second }
    val times = series.flatMap { it.second }.map { it.first }
    if (values.isNotEmpty() && times.isNotEmpty()) {
        val minValue = values.minOrNull() ?: 0f
        val maxValue = values.maxOrNull() ?: 1f
        val span = (maxValue - minValue).takeIf { abs(it) > .001f } ?: 1f
        val minTime = times.minOrNull() ?: 0L
        val maxTime = times.maxOrNull() ?: minTime + 1L
        val timeSpan = (maxTime - minTime).coerceAtLeast(1L)
        val left = 150f; val right = 1520f; val top = 210f; val bottom = 820f
        val axis = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = AndroidColor.rgb(5, 5, 5); strokeWidth = 3f }
        val grid = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = AndroidColor.argb(30, 5, 5, 5); strokeWidth = 2f }
        text.textSize = 26f
        repeat(5) { tick ->
            val ratio = tick / 4f
            val y = bottom - ratio * (bottom - top)
            canvas.drawLine(left, y, right, y, grid)
            canvas.drawText("%.0f".format(minValue + ratio * span), 35f, y + 9f, text)
        }
        canvas.drawLine(left, top, left, bottom, axis); canvas.drawLine(left, bottom, right, bottom, axis)
        val df = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        repeat(3) { tick ->
            val ratio = tick / 2f
            val x = left + ratio * (right - left)
            val label = df.format(Date(minTime + (timeSpan * ratio).toLong()))
            canvas.drawText(label, x - text.measureText(label) / 2, 865f, text)
        }
        series.forEachIndexed { index, (name, points) ->
            val color = if (index % 2 == 0) AndroidColor.rgb(114, 212, 205) else AndroidColor.rgb(5, 5, 5)
            val line = Paint(Paint.ANTI_ALIAS_FLAG).apply { this.color = color; strokeWidth = 8f; style = Paint.Style.STROKE }
            points.sortedBy { it.first }.zipWithNext().forEach { (a, b) ->
                fun x(t: Long) = left + ((t - minTime).toFloat() / timeSpan) * (right - left)
                fun y(v: Float) = bottom - ((v - minValue) / span) * (bottom - top)
                canvas.drawLine(x(a.first), y(a.second), x(b.first), y(b.second), line)
            }
            text.color = color; text.textSize = 28f
            canvas.drawText(titleCaseTr(name), 80f + index * 300f, 940f, text)
        }
    }
    val dir = File(context.cacheDir, "chart_reports").apply { mkdirs() }
    return File(dir, "Sifahane_Grafik_${System.currentTimeMillis()}.png").apply {
        outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        bitmap.recycle()
    }
}

private fun saveChartToGallery(context: Context, file: File) {
    val values = ContentValues().apply {
        put(android.provider.MediaStore.Images.Media.DISPLAY_NAME, file.name)
        put(android.provider.MediaStore.Images.Media.MIME_TYPE, "image/png")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(android.provider.MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Şifahane Raporları")
            put(android.provider.MediaStore.Images.Media.IS_PENDING, 1)
        }
    }
    val uri = context.contentResolver.insert(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values) ?: return
    context.contentResolver.openOutputStream(uri)?.use { output -> file.inputStream().use { it.copyTo(output) } }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        context.contentResolver.update(uri, ContentValues().apply {
            put(android.provider.MediaStore.Images.Media.IS_PENDING, 0)
        }, null, null)
    }
}


@Composable
private fun CenteredLabeledField(
    label: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            modifier = Modifier.fillMaxWidth(),
            color = LogoColorDark,
            textAlign = TextAlign.Center,
            fontSize = 13.sp,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.SemiBold,
                shadow = LogoTextShadow
            )
        )
        Spacer(Modifier.height(1.dp))
        content()
    }
}



@Composable
private fun CompactProfileTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    minLines: Int = 1,
    maxLines: Int = 1
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        singleLine = singleLine,
        minLines = minLines,
        maxLines = maxLines,
        textStyle = LocalTextStyle.current.copy(
            color = LogoColorDark,
            textAlign = TextAlign.Center,
            fontSize = 13.sp
        ),
        cursorBrush = SolidColor(LogoColorDark),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Vantablack05, StandardFieldShape)
                    .border(
                        width = 1.2.dp,
                        color = LogoColorDark.copy(alpha = 0.55f),
                        shape = StandardFieldShape
                    )
                    .padding(horizontal = 8.dp, vertical = 2.dp),
                contentAlignment = Alignment.Center
            ) {
                innerTextField()
            }
        }
    )
}

private val StandardFieldShape = RoundedCornerShape(14.dp)

@Composable
private fun standardFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = Vantablack05,
    unfocusedContainerColor = Vantablack05,
    disabledContainerColor = Vantablack05,
    focusedTextColor = LogoColorDark,
    unfocusedTextColor = LogoColorDark,
    disabledTextColor = LogoColorDark.copy(alpha = 0.65f),
    focusedLabelColor = LogoColor,
    unfocusedLabelColor = LogoColorDark,
    focusedBorderColor = LogoColor,
    unfocusedBorderColor = LogoColorDark.copy(alpha = 0.55f),
    cursorColor = LogoColorDark
)

@Composable
private fun CenteredCheckboxField(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            label,
            modifier = Modifier.fillMaxWidth(),
            color = LogoColorDark,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.SemiBold,
                shadow = LogoTextShadow
            )
        )
        Spacer(Modifier.height(0.dp))
        Box(
            modifier = Modifier
                .size(40.dp)
                .then(
                    if (checked) {
                        Modifier.shadow(
                            elevation = 4.dp,
                            shape = RoundedCornerShape(6.dp),
                            ambientColor = Color.Black,
                            spotColor = Color.Black
                        )
                    } else {
                        Modifier
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Checkbox(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = LogoColor,
                    uncheckedColor = LogoColorDark,
                    checkmarkColor = Color.Black
                )
            )
        }
    }
}

private enum class AdminPinPurpose {
    INITIAL_SETUP,
    UPDATE_VERIFICATION,
    UNLOCK_PROFILE,
    CHANGE_PATTERN,
    DELETE_PROFILE,
    ADMIN_SETTINGS
}

@Composable
private fun AdminPinDialog(
    purpose: AdminPinPurpose,
    profileName: String? = null,
    onDismiss: () -> Unit,
    onVerified: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var pin by remember { mutableStateOf("") }
    var newPin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    val initialSetup = purpose == AdminPinPurpose.INITIAL_SETUP
    val updateVerification = purpose == AdminPinPurpose.UPDATE_VERIFICATION

    AlertDialog(
        onDismissRequest = {
            if (!initialSetup && !updateVerification) onDismiss()
        },
        title = {
            Text(
                when {
                    initialSetup -> "Yönetici şifresi oluşturun"
                    updateVerification -> "Yönetici doğrulaması"
                    purpose == AdminPinPurpose.UNLOCK_PROFILE ->
                        "${profileName ?: "Kullanıcı"} için yönetici doğrulaması"
                    purpose == AdminPinPurpose.CHANGE_PATTERN ->
                        "Desen kilidini sıfırla"
                    purpose == AdminPinPurpose.DELETE_PROFILE ->
                        "Kullanıcı silme yetkisi"
                    else -> "Yönetici doğrulaması"
                },
                color = LogoColorDark,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    shadow = LogoTextShadow
                )
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    when {
                        initialSetup ->
                            "İlk kullanım için yalnız sizin bildiğiniz 4–12 haneli sayısal bir yönetici şifresi belirleyin. Uygulamada varsayılan veya arka kapı şifresi yoktur."
                        updateVerification ->
                            "Güncellemeyi tamamlamak için mevcut yönetici şifresini girin."
                        purpose == AdminPinPurpose.UNLOCK_PROFILE ->
                            "Bu kişinin desenini güvenli biçimde sıfırlamak için yönetici şifresini girin."
                        purpose == AdminPinPurpose.CHANGE_PATTERN ->
                            "Bu kişinin desenini değiştirmek için yönetici şifresini girin."
                        else ->
                            "Yönetici işlemini onaylamak için yönetici şifresini girin."
                    }
                )

                if (initialSetup) {
                    OutlinedTextField(
                        value = newPin,
                        onValueChange = { value ->
                            if (value.length <= 12 && value.all(Char::isDigit)) newPin = value
                        },
                        label = { Text("Yeni yönetici şifresi") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = logoFieldColors()
                    )
                    OutlinedTextField(
                        value = confirmPin,
                        onValueChange = { value ->
                            if (value.length <= 12 && value.all(Char::isDigit)) confirmPin = value
                        },
                        label = { Text("Yeni şifreyi tekrar girin") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = logoFieldColors()
                    )
                } else {
                    OutlinedTextField(
                        value = pin,
                        onValueChange = { value ->
                            if (value.length <= 12 && value.all(Char::isDigit)) pin = value
                        },
                        label = { Text("Yönetici şifresi") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = logoFieldColors()
                    )
                }

                error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    error = null
                    if (initialSetup) {
                        when {
                            newPin.length !in 4..12 ->
                                error = "Yeni şifre 4–12 haneli olmalıdır."
                            newPin != confirmPin ->
                                error = "Yeni şifreler eşleşmiyor."
                            else -> {
                                AdminPinStore.saveNewPin(context, newPin)
                                onVerified()
                            }
                        }
                    } else if (AdminPinStore.verify(context, pin)) {
                        onVerified()
                    } else {
                        val remaining = AdminPinStore.remainingLockoutMillis(context)
                        error = if (remaining > 0L) {
                            "Çok sayıda başarısız deneme yapıldı. ${((remaining + 999L) / 1000L)} saniye sonra yeniden deneyin."
                        } else {
                            "Yönetici şifresi yanlış."
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = LogoColor.copy(alpha = 0.25f),
                    contentColor = Color(0xFF123A37)
                )
            ) {
                Text(
                    if (initialSetup) "Şifreyi Kaydet" else "Doğrula",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        shadow = LogoTextShadow
                    )
                )
            }
        },
        dismissButton = {
            if (!initialSetup && !updateVerification) {
                TextButton(
                    onClick = onDismiss,
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = Vantablack10,
                        contentColor = LogoColorDark
                    )
                ) { Text("İptal") }
            }
        }
    )
}

private enum class PatternMode { CREATE, CONFIRM, VERIFY }


@Composable
private fun ProfilePatternGate(
    profile: UserProfile,
    forceCreate: Boolean = false,
    onUnlocked: () -> Unit,
    onCancel: () -> Unit,
    onAdminRequested: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var firstPattern by remember(profile.id, forceCreate) {
        mutableStateOf<List<Int>?>(null)
    }
    var drawnPattern by remember(profile.id, forceCreate) {
        mutableStateOf(emptyList<Int>())
    }
    var currentPointer by remember(profile.id, forceCreate) {
        mutableStateOf<Offset?>(null)
    }
    var saving by remember { mutableStateOf(false) }

    val creating = forceCreate || !PatternStore.hasPattern(context, profile.id)

    var message by remember(profile.id, creating) {
        mutableStateOf(
            if (creating)
                "Yeni deseninizi parmağınızı kaldırmadan çizin"
            else
                "Deseninizi çizerek giriş yapın"
        )
    }

    fun resetDrawing() {
        drawnPattern = emptyList()
        currentPointer = null
    }

    fun complete(pattern: List<Int>) {
        if (saving) return

        if (pattern.size < 4) {
            message = "Desen en az 4 noktadan oluşmalıdır."
            resetDrawing()
            return
        }

        if (creating) {
            if (firstPattern == null) {
                firstPattern = pattern.toList()
                message = "Aynı deseni ikinci kez çizin"
                resetDrawing()
                return
            }

            if (firstPattern != pattern) {
                firstPattern = null
                message = "İki desen eşleşmedi. İlk deseni yeniden çizin."
                resetDrawing()
                return
            }

            saving = true
            try {
                PatternStore.save(context, profile.id, pattern)

                if (PatternStore.matchesStoredPattern(context, profile.id, pattern)) {
                    message = "Desen başarıyla kaydedildi."
                    resetDrawing()
                    onUnlocked()
                } else {
                    firstPattern = null
                    message = "Desen kaydedilemedi. Yeniden deneyin."
                    resetDrawing()
                }
            } catch (_: Exception) {
                firstPattern = null
                message = "Desen kaydedilemedi. Yeniden deneyin."
                resetDrawing()
            } finally {
                saving = false
            }
            return
        }

        val remaining = PatternStore.remainingLockoutMillis(context, profile.id)
        if (remaining > 0) {
            message =
                "Çok fazla hatalı deneme. ${remaining / 1000 + 1} saniye bekleyin."
            resetDrawing()
            return
        }

        if (PatternStore.verify(context, profile.id, pattern)) {
            message = "Desen doğru."
            resetDrawing()
            onUnlocked()
        } else {
            val after = PatternStore.remainingLockoutMillis(context, profile.id)
            message = if (after > 0)
                "Beş hatalı deneme yapıldı. 30 saniye bekleyin."
            else
                "Desen yanlış. Tekrar deneyin."
            resetDrawing()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Box(Modifier.fillMaxSize()) {
            Image(
                painter = rememberAsyncImagePainter(R.drawable.sifahane_logo),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.10f),
                contentScale = ContentScale.Fit
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (!profile.photoUri.isNullOrBlank()) {
                    Image(
                        painter = rememberAsyncImagePainter(profile.photoUri),
                        contentDescription = null,
                        modifier = Modifier
                            .size(88.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = LogoColor,
                        modifier = Modifier.size(72.dp)
                    )
                }

                Text(
                    "${profile.name} ${profile.surname}".trim(),
                    color = LogoColorDark,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        shadow = LogoTextShadow
                    )
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    message,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(Modifier.height(20.dp))

                SwipePatternGrid(
                    selected = drawnPattern,
                    pointer = currentPointer,
                    enabled = !saving,
                    onPatternChanged = {
                        drawnPattern = it.first
                        currentPointer = it.second
                    },
                    onCompleted = { complete(it) }
                )

                Spacer(Modifier.height(18.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    TextButton(
                        onClick = onCancel,
                        enabled = !saving,
                        colors = ButtonDefaults.textButtonColors(
                            containerColor = Vantablack10,
                            contentColor = LogoColorDark
                        )
                    ) {
                        Text("Geri")
                    }

                    if (drawnPattern.isNotEmpty()) {
                        OutlinedButton(
                            onClick = { resetDrawing() },
                            enabled = !saving,
                            border = BorderStroke(1.5.dp, LogoColor),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Vantablack10,
                                contentColor = LogoColorDark
                            )
                        ) {
                            Text("Temizle")
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))

                OutlinedButton(
                    onClick = onAdminRequested,
                    enabled = !saving,
                    border = BorderStroke(1.5.dp, LogoColor),
                    colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Vantablack10,
                                contentColor = LogoColorDark
                            )
                ) {
                    Text(
                        "Yönetici şifresi gir",
                        style = MaterialTheme.typography.labelLarge.copy(
                            shadow = LogoTextShadow
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun SwipePatternGrid(
    selected: List<Int>,
    pointer: Offset?,
    enabled: Boolean = true,
    onPatternChanged: (Pair<List<Int>, Offset?>) -> Unit,
    onCompleted: (List<Int>) -> Unit
) {
    val nodeRadius = 20.dp
    val hitRadiusPx = with(androidx.compose.ui.platform.LocalDensity.current) {
        38.dp.toPx()
    }

    Canvas(
        modifier = Modifier
            .size(300.dp)
            .pointerInput(enabled) {
                if (!enabled) return@pointerInput
                var current = emptyList<Int>()

                fun nodeCenters(size: Size): List<Offset> {
                    val xs = listOf(size.width * 0.18f, size.width * 0.50f, size.width * 0.82f)
                    val ys = listOf(size.height * 0.18f, size.height * 0.50f, size.height * 0.82f)
                    return ys.flatMap { y -> xs.map { x -> Offset(x, y) } }
                }

                fun addHit(position: Offset) {
                    val centers = nodeCenters(Size(size.width.toFloat(), size.height.toFloat()))
                    val hit = centers.indexOfFirst {
                        (it - position).getDistance() <= hitRadiusPx
                    }
                    if (hit >= 0 && (hit + 1) !in current) {
                        current = current + (hit + 1)
                    }
                    onPatternChanged(current to position)
                }

                detectDragGestures(
                    onDragStart = { start ->
                        current = emptyList()
                        addHit(start)
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        addHit(change.position)
                    },
                    onDragEnd = {
                        val completed = current.toList()
                        onPatternChanged(completed to null)
                        current = emptyList()
                        onCompleted(completed)
                    },
                    onDragCancel = {
                        onPatternChanged(emptyList<Int>() to null)
                        current = emptyList()
                    }
                )
            }
    ) {
        val xs = listOf(size.width * 0.18f, size.width * 0.50f, size.width * 0.82f)
        val ys = listOf(size.height * 0.18f, size.height * 0.50f, size.height * 0.82f)
        val centers = ys.flatMap { y -> xs.map { x -> Offset(x, y) } }

        selected.zipWithNext().forEach { (a, b) ->
            drawLine(
                color = LogoColor,
                start = centers[a - 1],
                end = centers[b - 1],
                strokeWidth = 9f,
                cap = StrokeCap.Round
            )
        }

        if (selected.isNotEmpty() && pointer != null) {
            drawLine(
                color = LogoColor.copy(alpha = 0.75f),
                start = centers[selected.last() - 1],
                end = pointer,
                strokeWidth = 7f,
                cap = StrokeCap.Round
            )
        }

        centers.forEachIndexed { index, center ->
            val active = (index + 1) in selected
            drawCircle(
                color = if (active) LogoColor else Color.White,
                radius = nodeRadius.toPx(),
                center = center
            )
            drawCircle(
                color = LogoColorDark,
                radius = nodeRadius.toPx(),
                center = center,
                style = Stroke(width = if (active) 5f else 3f)
            )
            if (active) {
                drawCircle(
                    color = LogoColorDark,
                    radius = 6.dp.toPx(),
                    center = center
                )
            }
        }
    }
}


private enum class Screen { PROFILES, TODAY, MEDICINES, APPOINTMENTS, MEASUREMENTS, REPORTS, SETTINGS }
private enum class SettingsPage {
    HOME, ALARMS, APPOINTMENTS, BACKUP, SECURITY, PERMISSIONS, ACCESSIBILITY, THEME, DATABASE, HELP, ABOUT
}
private enum class MeasureTab { BP, GLUCOSE }

private enum class BottomMenuSize(val storageValue: String) {
    SMALL("small"), MEDIUM("medium"), LARGE("large");
    companion object {
        fun fromStorage(value: String?): BottomMenuSize =
            entries.firstOrNull { it.storageValue == value } ?: MEDIUM
    }
}

data class DailyDose(val medication: Medication, val time: String, val minutes: Int)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SifahaneRoot() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val db = remember { AppDatabase.get(context) }
    val scope = rememberCoroutineScope()
    val menuPreferences = remember {
        context.getSharedPreferences("sifahane_bottom_menu", Context.MODE_PRIVATE)
    }
    val defaultMenuOrder = remember {
        listOf(Screen.PROFILES, Screen.TODAY, Screen.MEDICINES, Screen.APPOINTMENTS,
            Screen.MEASUREMENTS, Screen.REPORTS, Screen.SETTINGS)
    }
    fun loadStoredMenuOrder(): List<Screen> {
        val stored = menuPreferences.getString("menu_order", null)
            ?.split(",")
            ?.mapNotNull { runCatching { Screen.valueOf(it) }.getOrNull() }
            .orEmpty()
        return (stored + defaultMenuOrder).distinct().filter { it in defaultMenuOrder }
    }
    var bottomMenuOrder by remember { mutableStateOf(loadStoredMenuOrder()) }
    var bottomMenuSize by remember {
        mutableStateOf(BottomMenuSize.fromStorage(menuPreferences.getString("menu_size", "medium")))
    }
    var bottomMenuEditing by remember { mutableStateOf(false) }
    var bottomMenuPopupVisible by remember { mutableStateOf(false) }

    fun persistBottomMenuOrder(order: List<Screen>) {
        bottomMenuOrder = order
        menuPreferences.edit().putString("menu_order", order.joinToString(",") { it.name }).apply()
    }
    fun changeBottomMenuSize(size: BottomMenuSize) {
        bottomMenuSize = size
        menuPreferences.edit().putString("menu_size", size.storageValue).apply()
    }

    val profiles by db.profileDao().observeAll().collectAsStateWithLifecycle(initialValue = emptyList())
    LaunchedEffect(Unit) { if (db.profileDao().count() == 0) db.profileDao().insert(UserProfile(name = "Kendim", role = UserRoles.ADMIN, permissionsCsv = UserRoles.ADMIN_PERMISSIONS)) }

    var activeProfileId by remember { mutableLongStateOf(1L) }
    var unlockedProfileId by remember { mutableStateOf<Long?>(null) }
    var pendingPatternProfile by remember { mutableStateOf<UserProfile?>(null) }
    var forcePatternCreate by remember { mutableStateOf(false) }
    var screen by remember { mutableStateOf(Screen.TODAY) }
    val screenHistory = remember { mutableStateListOf<Screen>() }
    var retainedSettingsPage by remember { mutableStateOf(SettingsPage.HOME) }
    var initialScreenSet by remember { mutableStateOf(false) }
    var backgroundedAt by remember { mutableLongStateOf(0L) }
    var adminPinPurpose by remember { mutableStateOf<AdminPinPurpose?>(null) }
    var adminTargetProfile by remember { mutableStateOf<UserProfile?>(null) }
    var adminSessionUnlocked by remember { mutableStateOf(false) }
    var patternChangeProfileId by remember { mutableStateOf<Long?>(null) }
    var adminDeleteProfile by remember { mutableStateOf<UserProfile?>(null) }
    var idleHeartVisible by remember { mutableStateOf(false) }
    var idleHeartKey by remember { mutableLongStateOf(0L) }
    var idleTimerJob by remember { mutableStateOf<Job?>(null) }
    var alarmRefreshResult by remember {
        mutableStateOf<AlarmRefreshResult?>(null)
    }
    var showAlarmStatus by remember { mutableStateOf(false) }
    var alarmRefreshBusy by remember { mutableStateOf(false) }

    val notificationPermissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { }

    fun restartIdleTimer() {
        idleHeartVisible = false
        idleTimerJob?.cancel()
        val timeout = AppPreferences.lockTimeoutMillis(context)
        if (timeout <= 0L) return
        idleTimerJob = scope.launch {
            delay(timeout)
            if (pendingPatternProfile == null && adminPinPurpose == null) {
                profiles.firstOrNull { it.id == activeProfileId }?.let { profile ->
                    if (PatternStore.hasPattern(context, profile.id)) {
                        unlockedProfileId = null
                        pendingPatternProfile = profile
                        forcePatternCreate = false
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        restartIdleTimer()
        if (
            android.os.Build.VERSION.SDK_INT >= 33 &&
            androidx.core.content.ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermissionLauncher.launch(
                android.Manifest.permission.POST_NOTIFICATIONS
            )
        }
        alarmRefreshResult = withContext(Dispatchers.IO) {
            AlarmRescheduler.refreshAll(context)
        }
        AdminPinStore.prepareForVersion(context, BuildConfig.VERSION_CODE)
        if (AdminPinStore.requiresInitialSetup(context)) {
            adminPinPurpose = AdminPinPurpose.INITIAL_SETUP
        } else {
            AppPreferences.markVersionVerified(context, BuildConfig.VERSION_CODE)
        }
    }

    LaunchedEffect(screen) {
        restartIdleTimer()
    }

    val activeProfile = profiles.firstOrNull { it.id == activeProfileId }
    val activeIsAdmin = UserRoles.isAdmin(activeProfile)

    androidx.activity.compose.BackHandler(
        enabled = screen != Screen.PROFILES && pendingPatternProfile == null && adminPinPurpose == null
    ) {
        if (screenHistory.isNotEmpty()) {
            screen = screenHistory.removeAt(screenHistory.lastIndex)
        } else {
            screen = Screen.PROFILES
            unlockedProfileId = null
        }
    }

    LaunchedEffect(profiles) {
        if (profiles.isNotEmpty() && profiles.none { it.id == activeProfileId }) {
            activeProfileId = profiles.first().id
            unlockedProfileId = null
        }
        if (!initialScreenSet && profiles.isNotEmpty()) {
            if (profiles.size > 1) {
                screen = Screen.PROFILES
            } else {
                activeProfileId = profiles.first().id
                if (PatternStore.hasPattern(context, profiles.first().id)) {
                    pendingPatternProfile = profiles.first()
                    forcePatternCreate = false
                } else {
                    adminTargetProfile = profiles.first()
                    adminPinPurpose = AdminPinPurpose.UNLOCK_PROFILE
                }
                screen = Screen.TODAY
            }
            initialScreenSet = true
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_STOP -> backgroundedAt = System.currentTimeMillis()
                Lifecycle.Event.ON_START -> {
                    if (
                        backgroundedAt > 0L &&
                        System.currentTimeMillis() - backgroundedAt > 120_000L
                    ) {
                        unlockedProfileId = null
                        profiles.firstOrNull { it.id == activeProfileId }?.let {
                            if (PatternStore.hasPattern(context, it.id)) {
                                pendingPatternProfile = it
                                forcePatternCreate = false
                            } else {
                                adminTargetProfile = it
                                adminPinPurpose = AdminPinPurpose.UNLOCK_PROFILE
                            }
                        }
                    }
                }
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val meds by remember(activeProfileId) {
        if (activeProfileId > 0) db.medicationDao().observeForProfile(activeProfileId) else flowOf(emptyList())
    }.collectAsStateWithLifecycle(initialValue = emptyList())

    val allMedications by db.medicationDao().observeAllMedications()
        .collectAsStateWithLifecycle(initialValue = emptyList())

    val reportGroups by remember(activeProfileId) {
        db.reportGroupDao().observeForProfile(activeProfileId)
    }.collectAsStateWithLifecycle(initialValue = emptyList())

    val archive by remember(activeProfileId) {
        if (activeProfileId > 0) db.medicationDao().observeArchive(activeProfileId) else flowOf(emptyList())
    }.collectAsStateWithLifecycle(initialValue = emptyList())

    val logs by remember(activeProfileId) { db.doseLogDao().observeForProfile(activeProfileId) }
        .collectAsStateWithLifecycle(initialValue = emptyList())
    val appointments by remember(activeProfileId) {
        if (activeProfileId > 0) db.appointmentDao().observeForProfile(activeProfileId) else flowOf(emptyList())
    }.collectAsStateWithLifecycle(initialValue = emptyList())
    val bp by remember(activeProfileId) { db.vitalsDao().observeBp(activeProfileId) }
        .collectAsStateWithLifecycle(initialValue = emptyList())
    val glucose by remember(activeProfileId) { db.vitalsDao().observeGlucose(activeProfileId) }
        .collectAsStateWithLifecycle(initialValue = emptyList())

    var editMedication by remember { mutableStateOf<Medication?>(null) }
    var newMedication by remember { mutableStateOf(false) }
    var editProfile by remember { mutableStateOf<UserProfile?>(null) }
    var newProfile by remember { mutableStateOf(false) }
    var showAddProfileChoice by remember { mutableStateOf(false) }
    var addBp by remember { mutableStateOf(false) }
    var addGlucose by remember { mutableStateOf(false) }
    var editBp by remember { mutableStateOf<BloodPressure?>(null) }
    var editGlucose by remember { mutableStateOf<BloodGlucose?>(null) }
    var editReportGroup by remember { mutableStateOf<ReportGroup?>(null) }
    var newReportGroup by remember { mutableStateOf(false) }
    var medicinesTab by remember { mutableIntStateOf(0) }

    var measureTab by remember { mutableStateOf(MeasureTab.BP) }
    var exportStatus by remember { mutableStateOf("") }
    var pendingExportProfileId by remember { mutableStateOf<Long?>(null) }
    var pendingExportFile by remember { mutableStateOf<File?>(null) }
    var showSecondCopyQuestion by remember { mutableStateOf(false) }
    var treeAction by remember { mutableStateOf("export") }
    var showImportSourceDialog by remember { mutableStateOf(false) }
    var folderBackups by remember { mutableStateOf<List<BackupDocument>>(emptyList()) }
    var showFolderBackups by remember { mutableStateOf(false) }
    var importPreview by remember { mutableStateOf<BackupPreview?>(null) }
    var showInitialImportChoice by remember { mutableStateOf(false) }
    var initialImportMode by remember { mutableStateOf(false) }
    var addProfileImportMode by remember { mutableStateOf(false) }
    var showImportRoleChoice by remember { mutableStateOf(false) }
    var importNewProfileAsAdmin by remember { mutableStateOf(false) }
    var passwordExportProfileId by remember { mutableStateOf<Long?>(null) }
    var exportPassword by remember { mutableStateOf("") }
    var exportPasswordConfirm by remember { mutableStateOf("") }
    var pendingExportPassword by remember { mutableStateOf<String?>(null) }
    var pendingEncryptedImportUri by remember { mutableStateOf<Uri?>(null) }
    var importPasswordEntry by remember { mutableStateOf("") }
    var importBackupPassword by remember { mutableStateOf<String?>(null) }

    val saveSecondCopyLauncher = rememberLauncherForActivityResult(CreateDocument("application/octet-stream")) { uri ->
        val file = pendingExportFile
        if (uri != null && file != null) {
            scope.launch {
                exportStatus = runCatching {
                    withContext(Dispatchers.IO) { SifahaneBackupManager.copyBackupToUri(context, file, uri) }
                    "Yedek iki konuma başarıyla kaydedildi."
                }.getOrElse { "İkinci kopya kaydedilemedi: ${it.message}" }
                pendingExportFile = null
            }
        } else if (file != null) {
            exportStatus = "Otomatik yedek kaydedildi; ikinci kopya oluşturulmadı."
            pendingExportFile = null
        }
    }

    if (showSecondCopyQuestion) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("İkinci yedek kopyası") },
            text = { Text("İlk kopya Şifahane Yedek klasörüne parola korumalı AES-GCM biçiminde kaydedildi. Dosya adı ve hedef uygulama paylaşım ekranında görünür; içerik doğru parola olmadan açılamaz. İkinci kopyayı paylaşabilir veya başka bir konuma kaydedebilirsiniz.") },
            confirmButton = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(
                        onClick = {
                            showSecondCopyQuestion = false
                            pendingExportFile?.let { file ->
                                val uri = FileProvider.getUriForFile(
                                    context, "${context.packageName}.fileprovider", file
                                )
                                context.startActivity(Intent.createChooser(
                                    Intent(Intent.ACTION_SEND).apply {
                                        type = "application/octet-stream"
                                        putExtra(Intent.EXTRA_STREAM, uri)
                                        putExtra(Intent.EXTRA_SUBJECT, "Şifahane kişi verileri yedeği")
                                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                        clipData = android.content.ClipData.newUri(
                                            context.contentResolver, file.name, uri
                                        )
                                    },
                                    "Yedek kopyasını paylaş"
                                ))
                                exportStatus = "İkinci yedek kopyası paylaşım ekranına gönderildi."
                                pendingExportFile = null
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("UYGULAMALARLA PAYLAŞ") }
                    OutlinedButton(
                        onClick = {
                            showSecondCopyQuestion = false
                            pendingExportFile?.let { saveSecondCopyLauncher.launch(it.name) }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("DOSYA KONUMU SEÇ") }
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showSecondCopyQuestion = false
                    pendingExportFile = null
                    exportStatus = "Yedek Şifahane Yedek klasörüne kaydedildi."
                }) { Text("HAYIR") }
            }
        )
    }

    fun openBackupPreview(uri: Uri, password: String?) {
        scope.launch {
            runCatching {
                withContext(Dispatchers.IO) {
                    SifahaneBackupManager.preview(context, uri, password?.toCharArray())
                }
            }
                .onSuccess {
                    importBackupPassword = password
                    importPreview = it
                }
                .onFailure {
                    importBackupPassword = null
                    exportStatus = it.message ?: "Yedek okunamadı."
                }
        }
    }

    fun previewBackup(uri: Uri) {
        val encrypted = runCatching { SifahaneBackupManager.isEncrypted(context, uri) }
            .getOrDefault(false)
        if (encrypted) {
            pendingEncryptedImportUri = uri
            importPasswordEntry = ""
        } else {
            importBackupPassword = null
            openBackupPreview(uri, null)
        }
    }

    pendingEncryptedImportUri?.let { encryptedUri ->
        AlertDialog(
            onDismissRequest = {
                pendingEncryptedImportUri = null
                importPasswordEntry = ""
            },
            title = { Text("Şifreli yedek parolası") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Bu yedek AES-GCM ile şifrelenmiştir. Oluştururken belirlediğiniz en az 8 karakterli parolayı girin.")
                    OutlinedTextField(
                        value = importPasswordEntry,
                        onValueChange = { if (it.length <= 128) importPasswordEntry = it },
                        label = { Text("Yedek parolası") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    enabled = importPasswordEntry.length >= 8,
                    onClick = {
                        val password = importPasswordEntry
                        pendingEncryptedImportUri = null
                        importPasswordEntry = ""
                        openBackupPreview(encryptedUri, password)
                    }
                ) { Text("YEDEĞİ DOĞRULA") }
            },
            dismissButton = {
                TextButton(onClick = {
                    pendingEncryptedImportUri = null
                    importPasswordEntry = ""
                }) { Text("İptal") }
            }
        )
    }

    val importLauncher = rememberLauncherForActivityResult(OpenDocument()) { uri ->
        if (uri != null) previewBackup(uri)
        else if (initialImportMode) {
            initialImportMode = false
            showInitialImportChoice = true
        }
    }

    if (showInitialImportChoice) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Kişi verilerini içeri aktar") },
            text = { Text("Daha önce dışarı aktardığınız kişi verilerini şimdi içeri aktarmak ister misiniz?") },
            confirmButton = {
                Button(onClick = {
                    showInitialImportChoice = false
                    initialImportMode = true
                    showImportSourceDialog = true
                }) { Text("İÇERİ AKTAR") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showInitialImportChoice = false
                    initialImportMode = false
                    profiles.firstOrNull()?.let {
                        activeProfileId = it.id
                        pendingPatternProfile = it
                        forcePatternCreate = true
                    }
                    AppPreferences.markInstallSetupDone(context)
                }) { Text("ŞİMDİLİK HAYIR") }
            }
        )
    }

    val treeLauncher = rememberLauncherForActivityResult(OpenDocumentTree()) { uri ->
        if (uri != null) {
            runCatching {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
            }
            SifahaneBackupManager.saveTreeUri(context, uri)
            if (treeAction == "export") {
                pendingExportProfileId?.let { profileId ->
                    scope.launch {
                        runCatching {
                            val file = withContext(Dispatchers.IO) {
                                val password = pendingExportPassword
                                    ?: error("Yedek parolası bulunamadı.")
                                SifahaneBackupManager.createBackup(context, db, profileId, password.toCharArray()).also {
                                    SifahaneBackupManager.copyBackupToTree(context, uri, it)
                                }
                            }
                            pendingExportFile = file
                            exportStatus = "Otomatik kopya Şifahane Yedek klasörüne kaydedildi."
                            showSecondCopyQuestion = true
                        }.onFailure { exportStatus = "Yedek oluşturulamadı: ${it.message}" }
                        pendingExportProfileId = null
                        pendingExportPassword = null
                    }
                }
            } else {
                scope.launch {
                    folderBackups = withContext(Dispatchers.IO) { SifahaneBackupManager.listBackups(context, uri) }
                    showFolderBackups = true
                }
            }
        } else if (initialImportMode) {
            initialImportMode = false
            showInitialImportChoice = true
        }
    }

    fun performExportProfile(profileId: Long, password: String) {
        val tree = SifahaneBackupManager.savedTreeUri(context)
        if (tree == null) {
            pendingExportProfileId = profileId
            pendingExportPassword = password
            treeAction = "export"
            exportStatus = "Şifahane Yedek klasörünü seçiniz veya oluşturunuz."
            treeLauncher.launch(null)
        } else {
            scope.launch {
                runCatching {
                    val file = withContext(Dispatchers.IO) {
                        SifahaneBackupManager.createBackup(context, db, profileId, password.toCharArray()).also {
                            SifahaneBackupManager.copyBackupToTree(context, tree, it)
                        }
                    }
                    pendingExportFile = file
                    exportStatus = "Otomatik kopya Şifahane Yedek klasörüne kaydedildi."
                    showSecondCopyQuestion = true
                }.onFailure {
                    exportStatus = "Yedek klasörüne erişilemedi. Klasörü yeniden seçiniz."
                    pendingExportProfileId = profileId
                    pendingExportPassword = password
                    treeAction = "export"
                    treeLauncher.launch(null)
                }
            }
        }
    }

    fun exportProfile(profileId: Long) {
        passwordExportProfileId = profileId
        exportPassword = ""
        exportPasswordConfirm = ""
    }

    passwordExportProfileId?.let { profileId ->
        AlertDialog(
            onDismissRequest = {
                passwordExportProfileId = null
                exportPassword = ""
                exportPasswordConfirm = ""
            },
            title = { Text("Şifreli yedek oluştur") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Yedek kişisel sağlık verileri içerir ve AES-GCM ile şifrelenir. Parola uygulamada saklanmaz; kaybedilirse yedek açılamaz.")
                    OutlinedTextField(
                        value = exportPassword,
                        onValueChange = { if (it.length <= 128) exportPassword = it },
                        label = { Text("Yedek parolası (en az 8 karakter)") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = exportPasswordConfirm,
                        onValueChange = { if (it.length <= 128) exportPasswordConfirm = it },
                        label = { Text("Yedek parolasını tekrar girin") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (exportPasswordConfirm.isNotEmpty() && exportPassword != exportPasswordConfirm) {
                        Text("Parolalar eşleşmiyor.", color = MaterialTheme.colorScheme.error)
                    }
                }
            },
            confirmButton = {
                Button(
                    enabled = exportPassword.length >= 8 && exportPassword == exportPasswordConfirm,
                    onClick = {
                        val password = exportPassword
                        passwordExportProfileId = null
                        exportPassword = ""
                        exportPasswordConfirm = ""
                        performExportProfile(profileId, password)
                    }
                ) { Text("ŞİFRELİ YEDEK OLUŞTUR") }
            },
            dismissButton = {
                TextButton(onClick = {
                    passwordExportProfileId = null
                    exportPassword = ""
                    exportPasswordConfirm = ""
                }) { Text("İptal") }
            }
        )
    }

    if (showImportSourceDialog) {
        AlertDialog(
            onDismissRequest = {
                showImportSourceDialog = false
                if (initialImportMode) {
                    initialImportMode = false
                    showInitialImportChoice = true
                }
            },
            title = { Text("Yedek kaynağını seçin") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Şifahane Yedek klasöründeki parola korumalı .sifbackup dosyalarından seçim yapabilir veya eski uyumlu ZIP yedeğini açabilirsiniz.")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        PlainImportOption(
                            text = "Başka Konum",
                            onClick = {
                                showImportSourceDialog = false
                                importLauncher.launch(arrayOf("application/zip", "application/octet-stream"))
                            },
                            modifier = Modifier.weight(1f)
                        )
                        PlainImportOption(
                            text = "Şifahane Yedek",
                            onClick = {
                                showImportSourceDialog = false
                                val tree = SifahaneBackupManager.savedTreeUri(context)
                                if (tree == null) { treeAction = "import"; treeLauncher.launch(null) }
                                else scope.launch {
                                    folderBackups = withContext(Dispatchers.IO) { SifahaneBackupManager.listBackups(context, tree) }
                                    showFolderBackups = true
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        PlainImportOption(
                            text = "İptal",
                            onClick = {
                                showImportSourceDialog = false
                                initialImportMode = false
                                addProfileImportMode = false
                            },
                            modifier = Modifier.weight(1f)
                        )
                        PlainImportOption(
                            text = "Geri",
                            onClick = {
                                showImportSourceDialog = false
                                when {
                                    addProfileImportMode -> showImportRoleChoice = true
                                    initialImportMode -> showInitialImportChoice = true
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            },
            confirmButton = {},
            dismissButton = {}
        )
    }

    if (showFolderBackups) {
        AlertDialog(
            onDismissRequest = { showFolderBackups = false },
            title = { Text("Şifahane yedekleri") },
            text = {
                if (folderBackups.isEmpty()) Text("Seçili klasörde .sifbackup veya uyumlu eski ZIP yedeği bulunamadı.")
                else LazyColumn(modifier = Modifier.heightIn(max = 360.dp)) {
                    items(folderBackups) { item ->
                        ListItem(
                            headlineContent = { Text(item.name) },
                            supportingContent = { Text("${item.size / 1024} KB • ${formatDateTime(item.modifiedAt)}") },
                            modifier = Modifier.clickable { showFolderBackups = false; previewBackup(item.uri) }
                        )
                        HorizontalDivider()
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showFolderBackups = false }) { Text("Kapat") } }
        )
    }

    importPreview?.let { preview ->
        AlertDialog(
            onDismissRequest = { importPreview = null; importBackupPassword = null },
            title = { Text("Şifahane yedeği doğrulandı") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Kullanıcı: ${preview.profileName}", fontWeight = FontWeight.Bold)
                    preview.birthDate?.let { Text("Doğum tarihi: $it") }
                    Text("Kan grubu: ${preview.bloodGroup}")
                    Text("Yedek tarihi: ${preview.createdAt}")
                    Text("${preview.medicationCount} ilaç • ${preview.doseLogCount} doz kaydı")
                    Text("${preview.bloodPressureCount} tansiyon • ${preview.glucoseCount} şeker kaydı")
                    Text("${preview.reportGroupCount} rapor grubu • ${preview.photoCount} fotoğraf")
                    Spacer(Modifier.height(8.dp))
                    Text("Bu yedek kişisel sağlık verileri içerebilir.", style = MaterialTheme.typography.bodySmall)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    importPreview = null
                    val passwordForImport = importBackupPassword
                    importBackupPassword = null
                    scope.launch {
                        exportStatus = runCatching {
                            val newId = withContext(Dispatchers.IO) {
                                val imported = SifahaneBackupManager.importBackup(context, db, preview.uri, null, passwordForImport?.toCharArray())
                                if (initialImportMode) {
                                    db.profileDao().updateRole(imported, UserRoles.ADMIN, UserRoles.ADMIN_PERMISSIONS)
                                    db.profileDao().allProfiles().filter { it.id != imported }.forEach {
                                        db.profileDao().deleteProfileCascade(it.id)
                                        PatternStore.clear(context, it.id)
                                    }
                                } else if (addProfileImportMode) {
                                    val role = if (importNewProfileAsAdmin) UserRoles.ADMIN else UserRoles.STANDARD
                                    val permissions = if (importNewProfileAsAdmin) UserRoles.ADMIN_PERMISSIONS else UserRoles.STANDARD_PERMISSIONS
                                    db.profileDao().updateRole(imported, role, permissions)
                                }
                                imported
                            }
                            activeProfileId = newId
                            if (initialImportMode) {
                                db.profileDao().byId(newId)?.let {
                                    pendingPatternProfile = it
                                    forcePatternCreate = true
                                }
                                initialImportMode = false
                                AppPreferences.markInstallSetupDone(context)
                            } else if (addProfileImportMode) {
                                db.profileDao().byId(newId)?.let {
                                    pendingPatternProfile = it
                                    forcePatternCreate = true
                                }
                                addProfileImportMode = false
                            }
                            "Kişi verileri yeni kullanıcı olarak içeri aktarıldı ve alarmlar yenilendi."
                        }.getOrElse { it.message ?: "İçe aktarma başarısız." }
                    }
                }) { Text("Yeni kullanıcı") }
            },
            dismissButton = {
                Row {
                    TextButton(onClick = {
                        importPreview = null
                        val passwordForImport = importBackupPassword
                        importBackupPassword = null
                        if (activeProfileId <= 0L) exportStatus = "Birleştirmek için önce mevcut bir kullanıcı seçiniz."
                        else scope.launch {
                            exportStatus = runCatching {
                                withContext(Dispatchers.IO) { SifahaneBackupManager.importBackup(context, db, preview.uri, activeProfileId, passwordForImport?.toCharArray()) }
                                "Yedek aktif kullanıcıyla birleştirildi ve alarmlar yenilendi."
                            }.getOrElse { it.message ?: "İçe aktarma başarısız." }
                        }
                    }) { Text("Aktif kişiyle birleştir") }
                    TextButton(onClick = { importPreview = null; importBackupPassword = null }) { Text("İptal") }
                }
            }
        )
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                while (true) {
                    awaitPointerEventScope {
                        awaitPointerEvent()
                    }
                    restartIdleTimer()
                }
            }
    ) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val p = profiles.firstOrNull { it.id == activeProfileId }
                    SifahanePageTitle(
                        activeUser = listOfNotNull(
                            p?.name,
                            p?.surname?.takeIf { it.isNotBlank() }
                        ).joinToString(" "),
                        pageKey = screen
                    )
                },
                actions = {
                    when (screen) {
                        Screen.PROFILES -> IconButton({ showAddProfileChoice = true }) {
                            OutlinedLogoIcon(Icons.Default.PersonAdd, "Kişi ekle", size = 28.dp)
                        }
                        else -> Unit
                    }
                }
            )
        },
        bottomBar = {
            val scrollState = rememberScrollState()
            val density = LocalDensity.current
            val bottomMenuDragScope = rememberCoroutineScope()
            val itemWidth = when (bottomMenuSize) {
                BottomMenuSize.SMALL -> 62.dp
                BottomMenuSize.MEDIUM -> 74.dp
                BottomMenuSize.LARGE -> 88.dp
            }
            val iconSize = when (bottomMenuSize) {
                BottomMenuSize.SMALL -> 21.dp
                BottomMenuSize.MEDIUM -> 25.dp
                BottomMenuSize.LARGE -> 29.dp
            }
            val menuTextSize = when (bottomMenuSize) {
                BottomMenuSize.SMALL -> 9.sp
                BottomMenuSize.MEDIUM -> 10.sp
                BottomMenuSize.LARGE -> 12.sp
            }
            val contentHeight = when (bottomMenuSize) {
                BottomMenuSize.SMALL -> 56.dp
                BottomMenuSize.MEDIUM -> 64.dp
                BottomMenuSize.LARGE -> 74.dp
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(LogoColorSoft.copy(alpha = 0.50f))
                    .navigationBarsPadding()
            ) {
                PartnerLogoBanner()
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    LogoColorSoft.copy(alpha = 0.52f),
                                    LogoColorSoft.copy(alpha = 0.48f)
                                )
                            )
                        )
                ) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.Transparent,
                        tonalElevation = 0.dp,
                        shadowElevation = 0.dp
                    ) {
                        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                            val menuButtonWidth = when (bottomMenuSize) {
                                BottomMenuSize.SMALL -> 50.dp
                                BottomMenuSize.MEDIUM -> 54.dp
                                BottomMenuSize.LARGE -> 60.dp
                            }
                            val dividerWidth = 0.5.dp
                            val scrollViewportWidth = (maxWidth - menuButtonWidth - dividerWidth)
                                .coerceAtLeast(itemWidth)
                            val visibleItemCount = max(
                                1,
                                kotlin.math.floor(
                                    with(density) { scrollViewportWidth.toPx() / itemWidth.toPx() }
                                ).toInt()
                            )
                            val fittedItemWidth = scrollViewportWidth / visibleItemCount
                            val fittedItemWidthPx = with(density) { fittedItemWidth.toPx() }

                            LaunchedEffect(screen, bottomMenuOrder, bottomMenuSize, fittedItemWidthPx, scrollState.maxValue, bottomMenuEditing) {
                                // Düzenleme sırasında her sıra değişiminde seçili ekrana geri kaymak,
                                // parmakla sürüklemeyi kesiyordu. Otomatik görünür kılma yalnızca
                                // normal gezinme modunda çalışır.
                                if (!bottomMenuEditing) {
                                    val index = bottomMenuOrder.indexOf(screen).coerceAtLeast(0)
                                    val target = (fittedItemWidthPx * index).toInt()
                                    scrollState.animateScrollTo(target.coerceIn(0, scrollState.maxValue))
                                }
                            }

                            LaunchedEffect(scrollState, fittedItemWidthPx) {
                                snapshotFlow { scrollState.isScrollInProgress }.collect { isScrolling ->
                                    if (!isScrolling && fittedItemWidthPx > 0f) {
                                        val snappedIndex = (scrollState.value / fittedItemWidthPx).roundToInt()
                                        val snappedOffset = (snappedIndex * fittedItemWidthPx)
                                            .roundToInt()
                                            .coerceIn(0, scrollState.maxValue)
                                        if (kotlin.math.abs(scrollState.value - snappedOffset) > 1) {
                                            scrollState.animateScrollTo(snappedOffset)
                                        }
                                    }
                                }
                            }

                            Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(contentHeight),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .horizontalScroll(scrollState),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                bottomMenuOrder.forEach { menuScreen ->
                                    // Sıra değiştiğinde remember durumlarının listedeki konumla değil,
                                    // gerçek menü öğesiyle birlikte taşınmasını sağlar. Bu olmadan
                                    // sürükleme durumu ilk komşu takasından sonra başka öğeye geçiyordu.
                                    key(menuScreen) {
                                    val icon = when (menuScreen) {
                                        Screen.PROFILES -> Icons.Default.People
                                        Screen.TODAY -> Icons.Default.Today
                                        Screen.MEDICINES -> Icons.Default.NightlightRound
                                        Screen.APPOINTMENTS -> Icons.Default.Event
                                        Screen.MEASUREMENTS -> Icons.Default.MonitorHeart
                                        Screen.REPORTS -> Icons.Default.Assessment
                                        Screen.SETTINGS -> Icons.Default.Settings
                                    }
                                    val label = when (menuScreen) {
                                        Screen.PROFILES -> "Kişiler"
                                        Screen.TODAY -> "Bugün"
                                        Screen.MEDICINES -> "İlaçlar"
                                        Screen.APPOINTMENTS -> "Randevular"
                                        Screen.MEASUREMENTS -> "Ölçümler"
                                        Screen.REPORTS -> "Raporlar"
                                        Screen.SETTINGS -> "Ayarlar"
                                    }
                                    var dragTranslationX by remember(menuScreen, bottomMenuEditing) {
                                        mutableFloatStateOf(0f)
                                    }
                                    var isBeingDragged by remember(menuScreen, bottomMenuEditing) {
                                        mutableStateOf(false)
                                    }
                                    val editJiggleTransition = rememberInfiniteTransition(
                                        label = "bottomMenuEditJiggle"
                                    )
                                    val editJiggle by editJiggleTransition.animateFloat(
                                        initialValue = -0.45f,
                                        targetValue = 0.45f,
                                        animationSpec = infiniteRepeatable(
                                            animation = tween(
                                                durationMillis = 1200,
                                                easing = LinearEasing
                                            ),
                                            repeatMode = RepeatMode.Reverse
                                        ),
                                        label = "bottomMenuEditJiggleAngle"
                                    )
                                    val selected = screen == menuScreen
                                    val selectedScale by animateFloatAsState(
                                        targetValue = when {
                                            isBeingDragged -> 1.03f
                                            selected -> 1.03f
                                            else -> 1.0f
                                        },
                                        animationSpec = spring(
                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                            stiffness = Spring.StiffnessMediumLow
                                        ),
                                        label = "bottomMenuScale"
                                    )
                                    val selectedElevation by animateFloatAsState(
                                        targetValue = when {
                                            isBeingDragged -> 5f
                                            selected -> 4f
                                            else -> 0f
                                        },
                                        animationSpec = spring(
                                            dampingRatio = Spring.DampingRatioNoBouncy,
                                            stiffness = Spring.StiffnessMedium
                                        ),
                                        label = "bottomMenuElevation"
                                    )
                                    val foregroundColor by animateColorAsState(
                                        targetValue = Color.Black.copy(
                                            alpha = if (selected) 0.70f else 0.50f
                                        ),
                                        animationSpec = tween(durationMillis = 140),
                                        label = "bottomMenuForeground"
                                    )
                                    val selectionColor by animateColorAsState(
                                        targetValue = if (selected) {
                                            LogoColor.copy(alpha = 0.24f)
                                        } else {
                                            Color.Transparent
                                        },
                                        animationSpec = tween(durationMillis = 140),
                                        label = "bottomMenuSelection"
                                    )
                                    val reorderModifier = if (bottomMenuEditing) {
                                        Modifier.pointerInput(
                                            menuScreen,
                                            bottomMenuEditing,
                                            fittedItemWidthPx,
                                            scrollViewportWidth
                                        ) {
                                            detectDragGesturesAfterLongPress(
                                                onDragStart = {
                                                    dragTranslationX = 0f
                                                    isBeingDragged = true
                                                },
                                                onDragEnd = {
                                                    dragTranslationX = 0f
                                                    isBeingDragged = false
                                                },
                                                onDragCancel = {
                                                    dragTranslationX = 0f
                                                    isBeingDragged = false
                                                }
                                            ) { change, dragAmount ->
                                                change.consume()
                                                dragTranslationX += dragAmount.x

                                                var currentIndex = bottomMenuOrder.indexOf(menuScreen)
                                                if (currentIndex < 0 || fittedItemWidthPx <= 0f) return@detectDragGesturesAfterLongPress

                                                // Parmağın geçtiği her tam yuvada öğeyi taşı; kalan ofset,
                                                // sürüklenen simgenin parmak altında kesintisiz kalmasını sağlar.
                                                while (dragTranslationX > fittedItemWidthPx * 0.50f &&
                                                    currentIndex < bottomMenuOrder.lastIndex
                                                ) {
                                                    val reordered = bottomMenuOrder.toMutableList()
                                                    val moved = reordered.removeAt(currentIndex)
                                                    reordered.add(currentIndex + 1, moved)
                                                    persistBottomMenuOrder(reordered)
                                                    dragTranslationX -= fittedItemWidthPx
                                                    currentIndex += 1
                                                }
                                                while (dragTranslationX < -fittedItemWidthPx * 0.50f &&
                                                    currentIndex > 0
                                                ) {
                                                    val reordered = bottomMenuOrder.toMutableList()
                                                    val moved = reordered.removeAt(currentIndex)
                                                    reordered.add(currentIndex - 1, moved)
                                                    persistBottomMenuOrder(reordered)
                                                    dragTranslationX += fittedItemWidthPx
                                                    currentIndex -= 1
                                                }

                                                // Sürüklenen öğe görünür alanın kenarına yaklaştığında
                                                // kaydırılabilir bölümü otomatik olarak hareket ettir.
                                                val viewportPx = with(density) { scrollViewportWidth.toPx() }
                                                val draggedCenterInViewport =
                                                    currentIndex * fittedItemWidthPx +
                                                        fittedItemWidthPx / 2f +
                                                        dragTranslationX -
                                                        scrollState.value
                                                val edgeZone = min(fittedItemWidthPx * 0.72f, viewportPx * 0.22f)
                                                val autoScrollDelta = when {
                                                    draggedCenterInViewport < edgeZone ->
                                                        -min(18f, edgeZone - draggedCenterInViewport)
                                                    draggedCenterInViewport > viewportPx - edgeZone ->
                                                        min(18f, draggedCenterInViewport - (viewportPx - edgeZone))
                                                    else -> 0f
                                                }
                                                if (autoScrollDelta != 0f) {
                                                    bottomMenuDragScope.launch {
                                                        scrollState.scrollBy(autoScrollDelta)
                                                    }
                                                }
                                            }
                                        }
                                    } else Modifier

                                    Column(
                                        modifier = Modifier
                                            .width(fittedItemWidth)
                                            .fillMaxHeight()
                                            .graphicsLayer {
                                                scaleX = selectedScale
                                                scaleY = selectedScale
                                                translationX = if (isBeingDragged) dragTranslationX else 0f
                                                // Üst/alt geçişlerden bağımsız optik merkez düzeltmesi.
                                                translationY = with(density) { 1.dp.toPx() }
                                                rotationZ = if (bottomMenuEditing && !isBeingDragged) {
                                                    editJiggle
                                                } else {
                                                    0f
                                                }
                                            }
                                            .zIndex(if (isBeingDragged) 2f else 0f)
                                            .then(reorderModifier)
                                            .clickable {
                                                if (!bottomMenuEditing) {
                                                    if (menuScreen == Screen.PROFILES) {
                                                        screen = menuScreen
                                                    } else {
                                                        val active = profiles.firstOrNull { it.id == activeProfileId }
                                                        if (active != null && unlockedProfileId == active.id) {
                                                            screen = menuScreen
                                                        } else if (active != null) {
                                                            pendingPatternProfile = active
                                                            forcePatternCreate = !PatternStore.hasPattern(context, active.id)
                                                        }
                                                    }
                                                }
                                            }
                                            .padding(horizontal = 2.dp, vertical = 2.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .shadow(
                                                    elevation = selectedElevation.dp,
                                                    shape = RoundedCornerShape(16.dp),
                                                    ambientColor = Color.Black.copy(alpha = 0.06f),
                                                    spotColor = Color.Black.copy(alpha = 0.08f)
                                                )
                                                .clip(RoundedCornerShape(16.dp))
                                                .background(selectionColor)
                                                .padding(horizontal = 7.dp, vertical = 2.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(14.dp))
                                                    .background(Color.Black.copy(alpha = 0.10f))
                                                    .then(
                                                        if (bottomMenuEditing) {
                                                            Modifier.border(
                                                                width = if (isBeingDragged) 2.dp else 1.dp,
                                                                color = Color.Black.copy(
                                                                    alpha = if (isBeingDragged) 0.20f else 0.10f
                                                                ),
                                                                shape = RoundedCornerShape(14.dp)
                                                            )
                                                        } else {
                                                            Modifier
                                                        }
                                                    )
                                                    .padding(
                                                        horizontal = if (isBeingDragged) 6.dp else 5.dp,
                                                        vertical = if (isBeingDragged) 3.dp else 2.dp
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = icon,
                                                    contentDescription = label,
                                                    modifier = Modifier.size(iconSize),
                                                    tint = foregroundColor
                                                )
                                            }
                                        }
                                        Spacer(Modifier.height(0.5.dp))
                                        Text(
                                            label,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            fontSize = menuTextSize,
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                            color = foregroundColor
                                        )
                                    }
                                    }
                                }
                            }

                            VerticalDivider(
                                modifier = Modifier
                                    .fillMaxHeight(0.58f)
                                    .width(0.5.dp),
                                color = Color.Black.copy(alpha = 0.10f)
                            )

                            Box(
                                modifier = Modifier
                                    .width(menuButtonWidth)
                                    .fillMaxHeight(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .combinedClickable(
                                            onClick = {
                                                if (bottomMenuEditing) {
                                                    bottomMenuEditing = false
                                                } else {
                                                    bottomMenuPopupVisible = true
                                                }
                                            },
                                            onLongClick = {
                                                if (!bottomMenuEditing) {
                                                    bottomMenuEditing = true
                                                    bottomMenuPopupVisible = false
                                                }
                                            }
                                        )
                                        .padding(vertical = 2.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = if (bottomMenuEditing) Icons.Default.Check
                                        else Icons.Outlined.Edit,
                                        contentDescription = if (bottomMenuEditing) {
                                            "Menü düzenlemeyi bitir"
                                        } else {
                                            "Alt menü seçenekleri"
                                        },
                                        modifier = Modifier.size(
                                            if (bottomMenuSize == BottomMenuSize.LARGE) 28.dp else 24.dp
                                        ),
                                        tint = Color.Black.copy(alpha = 0.60f)
                                    )
                                    Spacer(Modifier.height(0.5.dp))
                                    Text(
                                        if (bottomMenuEditing) "Bitti" else "Menü",
                                        fontSize = if (bottomMenuSize == BottomMenuSize.LARGE) 10.sp else 9.sp,
                                        color = Color.Black.copy(alpha = 0.60f),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                DropdownMenu(
                                    expanded = bottomMenuPopupVisible,
                                    onDismissRequest = { bottomMenuPopupVisible = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Menüyü düzenle") },
                                        leadingIcon = { Icon(Icons.Default.DragIndicator, null) },
                                        onClick = {
                                            bottomMenuPopupVisible = false
                                            bottomMenuEditing = true
                                        }
                                    )
                                    HorizontalDivider()
                                    Text(
                                        "Menü buton boyutu",
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                        style = MaterialTheme.typography.labelMedium,
                                        color = LogoColorDark
                                    )
                                    listOf(
                                        BottomMenuSize.SMALL to "Küçük",
                                        BottomMenuSize.MEDIUM to "Orta",
                                        BottomMenuSize.LARGE to "Büyük"
                                    ).forEach { (size, title) ->
                                        DropdownMenuItem(
                                            text = { Text(title) },
                                            leadingIcon = {
                                                Icon(
                                                    if (bottomMenuSize == size) {
                                                        Icons.Default.RadioButtonChecked
                                                    } else {
                                                        Icons.Default.RadioButtonUnchecked
                                                    },
                                                    null
                                                )
                                            },
                                            onClick = {
                                                changeBottomMenuSize(size)
                                                bottomMenuPopupVisible = false
                                            }
                                        )
                                    }
                                    HorizontalDivider()
                                    DropdownMenuItem(
                                        text = { Text("Varsayılan sıraya dön") },
                                        leadingIcon = { Icon(Icons.Default.Restore, null) },
                                        onClick = {
                                            persistBottomMenuOrder(defaultMenuOrder)
                                            bottomMenuPopupVisible = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                }

                BottomShellSoftTransition(mirrored = false)
            }
        }
    ) { padding ->
        WatermarkContainer {
            when (screen) {
            Screen.PROFILES -> ProfilesScreen(
                profiles = profiles,
                activeId = activeProfileId,
                modifier = Modifier.padding(padding),
                onSelect = {
                    activeProfileId = it.id
                    unlockedProfileId = null
                    if (PatternStore.hasPattern(context, it.id)) {
                        pendingPatternProfile = it
                        forcePatternCreate = false
                    } else {
                        adminTargetProfile = it
                        adminPinPurpose = AdminPinPurpose.UNLOCK_PROFILE
                    }
                },
                onEdit = { editProfile = it },
                onDelete = { profile ->
                    scope.launch {
                        val lastAdmin = profile.role == UserRoles.ADMIN &&
                            withContext(Dispatchers.IO) { db.profileDao().enabledAdminCount() } <= 1
                        if (lastAdmin) {
                            exportStatus = "Tek kalan yönetici profili silinemez. Önce başka bir yönetici atayın."
                        } else {
                            adminDeleteProfile = profile
                            adminTargetProfile = profile
                            adminPinPurpose = AdminPinPurpose.DELETE_PROFILE
                        }
                    }
                },
                onChangePattern = { profile ->
                    activeProfileId = profile.id
                    unlockedProfileId = null
                    patternChangeProfileId = profile.id
                    if (PatternStore.hasPattern(context, profile.id)) {
                        pendingPatternProfile = profile
                        forcePatternCreate = false
                    } else {
                        adminTargetProfile = profile
                        adminPinPurpose = AdminPinPurpose.CHANGE_PATTERN
                    }
                }
            )
            Screen.TODAY -> TodayScreen(
                meds = meds,
                logs = logs,
                appointments = appointments,
                modifier = Modifier.padding(padding),
                onOpenAppointments = { screen = Screen.APPOINTMENTS }
            )
            Screen.APPOINTMENTS -> AppointmentsScreen(
                appointments = appointments,
                profileId = activeProfileId,
                db = db,
                modifier = Modifier.padding(padding)
            )
            Screen.MEDICINES -> MedicineScreen(
                active = meds,
                archive = archive,
                reportGroups = reportGroups,
                modifier = Modifier.padding(padding),
                onEditMedication = { editMedication = it },
                onNewReportGroup = { newReportGroup = true },
                onEditReportGroup = { editReportGroup = it },
                selectedTab = medicinesTab,
                onTabChange = { medicinesTab = it },
                onAdd = {
                    if (medicinesTab == 2) {
                        newReportGroup = true
                    } else {
                        newMedication = true
                    }
                },
                onDeleteReportGroup = { group ->
                    scope.launch(Dispatchers.IO) {
                        db.reportGroupDao().unlinkMedications(group.id)
                        db.reportGroupDao().delete(group)
                    }
                }
            )
            Screen.MEASUREMENTS -> MeasurementsScreen(
                tab = measureTab,
                onTab = { measureTab = it },
                bp = bp,
                glucose = glucose,
                modifier = Modifier.padding(padding),
                onEditBp = { editBp = it },
                onEditGlucose = { editGlucose = it },
                onAddMeasurement = {
                    if (measureTab == MeasureTab.BP) addBp = true else addGlucose = true
                }
            )
            Screen.REPORTS -> ReportsScreen(activeProfileId, db, Modifier.padding(padding))
            Screen.SETTINGS -> SettingsScreen(
                modifier = Modifier.padding(padding),
                initialPage = retainedSettingsPage,
                onPageChanged = { retainedSettingsPage = it },
                alarmRefreshBusy = alarmRefreshBusy,
                alarmRefreshResult = alarmRefreshResult,
                onAlarmRefresh = {
                    alarmRefreshBusy = true
                    scope.launch {
                        alarmRefreshResult = withContext(Dispatchers.IO) {
                            AlarmRescheduler.refreshAll(context)
                        }
                        alarmRefreshBusy = false
                        showAlarmStatus = true
                    }
                },
                onClearStaleAlarms = {
                    alarmRefreshBusy = true
                    scope.launch {
                        val result = withContext(Dispatchers.IO) {
                            AlarmRescheduler.clearStaleAndRefresh(context)
                        }
                        alarmRefreshResult = result.second
                        exportStatus = "${result.first} eski alarm isteği temizlendi; geçerli alarmlar yeniden oluşturuldu."
                        alarmRefreshBusy = false
                        showAlarmStatus = true
                    }
                },
                onAlarmStatus = { showAlarmStatus = true },
                onTestAlarm = {
                    AlarmScheduler.scheduleTest(
                        context,
                        System.currentTimeMillis() + 10_000L
                    )
                },
                hasActiveProfile = profiles.any { it.id == activeProfileId },
                backupStatusText = exportStatus,
                profiles = profiles,
                activeProfileId = activeProfileId,
                canManageSecurity = activeIsAdmin,
                databaseVersion = 10,
                bottomMenuSize = bottomMenuSize,
                onBottomMenuSizeChange = { changeBottomMenuSize(it) },
                onResetBottomMenuOrder = { persistBottomMenuOrder(defaultMenuOrder) },
                onExportPersonData = {
                    profiles.firstOrNull { it.id == activeProfileId }?.let {
                        exportProfile(it.id)
                    }
                },
                onImportPersonData = {
                    if (activeIsAdmin) showImportSourceDialog = true
                    else exportStatus = "Veri içeri aktarma işlemi için yönetici yetkisi gereklidir."
                },
                onSetRole = { profileId, makeAdmin ->
                    scope.launch {
                        val target = profiles.firstOrNull { it.id == profileId } ?: return@launch
                        if (!makeAdmin && target.role == UserRoles.ADMIN) {
                            val count = withContext(Dispatchers.IO) { db.profileDao().enabledAdminCount() }
                            if (count <= 1) {
                                exportStatus = "Son yönetici standart kullanıcıya dönüştürülemez."
                                return@launch
                            }
                        }
                        withContext(Dispatchers.IO) {
                            db.profileDao().updateRole(
                                profileId,
                                if (makeAdmin) UserRoles.ADMIN else UserRoles.STANDARD,
                                if (makeAdmin) UserRoles.ADMIN_PERMISSIONS else UserRoles.STANDARD_PERMISSIONS
                            )
                        }
                        exportStatus = if (makeAdmin) "${target.name} yönetici olarak atandı."
                        else "${target.name} standart kullanıcı olarak ayarlandı."
                    }
                },
                onSetAdminPin = { profileId, pin ->
                    scope.launch {
                        withContext(Dispatchers.IO) {
                            db.profileDao().updateAdminPinHash(
                                profileId,
                                AdminCredentialHasher.hash(profileId, pin)
                            )
                        }
                        exportStatus = "Yönetici şifresi güvenli biçimde kaydedildi."
                    }
                },
                onNavigate = { target ->
                    if (target != screen) {
                        screenHistory += screen
                        screen = target
                    }
                }
            )
            }
        }
    }

    }

    IdleHeartOverlay(
        visible = idleHeartVisible,
        animationKey = idleHeartKey
    )

    if (showAlarmStatus) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        val notificationManager =
            context.getSystemService(NotificationManager::class.java)
        val powerManager =
            context.getSystemService(android.os.PowerManager::class.java)

        val exactAlarmAllowed =
            android.os.Build.VERSION.SDK_INT < 31 ||
                alarmManager.canScheduleExactAlarms()
        val notificationAllowed =
            android.os.Build.VERSION.SDK_INT < 33 ||
                androidx.core.content.ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        val fullScreenAllowed =
            android.os.Build.VERSION.SDK_INT < 34 ||
                notificationManager.canUseFullScreenIntent()
        val batteryExcluded =
            powerManager.isIgnoringBatteryOptimizations(context.packageName)

        AlertDialog(
            onDismissRequest = { showAlarmStatus = false },
            title = {
                Text(
                    "Alarm Durumu",
                    modifier = Modifier.fillMaxWidth(),
                    color = LogoColorDark,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        shadow = LogoTextShadow
                    )
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    fun stateText(ok: Boolean) =
                        if (ok) "Açık" else "İzin Gerekli"

                    Text("Bildirim İzni: ${stateText(notificationAllowed)}")
                    Text("Kesin Alarm İzni: ${stateText(exactAlarmAllowed)}")
                    Text("Tam Ekran Alarm İzni: ${stateText(fullScreenAllowed)}")
                    Text(
                        "Pil Optimizasyonu Muafiyeti: ${
                            stateText(batteryExcluded)
                        }"
                    )
                    val diagnostics = context.getSharedPreferences("sifahane_alarm_diagnostics", Context.MODE_PRIVATE)
                    val lastTriggered = diagnostics.getLong("last_triggered_at", 0L)
                    if (lastTriggered > 0L) {
                        HorizontalDivider(color = Color(0xFF00AEEF).copy(alpha = 0.50f))
                        Text("Son alarm: ${SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale("tr", "TR")).format(Date(lastTriggered))}")
                        Text("Son grup: ${diagnostics.getString("last_group_key", "-")}")
                        Text("Son gruptaki ilaç sayısı: ${diagnostics.getInt("last_dose_count", 0)}")
                    }
                    diagnostics.getLong("last_snooze_trigger_at", 0L).takeIf { it > 0L }?.let {
                        Text("Son erteleme: ${diagnostics.getInt("last_snooze_minutes", 0)} dk → ${SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale("tr", "TR")).format(Date(it))}")
                    }
                    diagnostics.getLong("next_scheduled_at", 0L).takeIf { it > System.currentTimeMillis() }?.let {
                        Text("Sonraki planlanan alarm: ${SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale("tr", "TR")).format(Date(it))}")
                    }

                    alarmRefreshResult?.let { result ->
                        HorizontalDivider(color = LogoColor)
                        Text("Aktif İlaç: ${result.medicationCount}")
                        Text("Gelecek Alarm: ${result.futureAlarmCount}")
                        Text(
                            "Bugünün Geçmiş ve Yanıtsız Dozu: ${
                                result.catchUpAlarmCount
                            }"
                        )
                        Text("Bekleyen Erteleme: ${result.snoozeAlarmCount}")
                    }

                    Button(
                        onClick = {
                            AlarmScheduler.scheduleTest(
                                context,
                                System.currentTimeMillis() + 10_000L
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LogoColor.copy(alpha = 0.25f),
                            contentColor = Color(0xFF123A37)
                        )
                    ) {
                        Text("10 Saniye Sonra Deneme Alarmı")
                    }

                    if (!exactAlarmAllowed && android.os.Build.VERSION.SDK_INT >= 31) {
                        OutlinedButton(
                            onClick = {
                                runCatching {
                                    context.startActivity(
                                        Intent(
                                            android.provider.Settings
                                                .ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                                            Uri.parse("package:${context.packageName}")
                                        )
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Kesin Alarm İznini Aç")
                        }
                    }

                    if (!fullScreenAllowed && android.os.Build.VERSION.SDK_INT >= 34) {
                        OutlinedButton(
                            onClick = {
                                runCatching {
                                    context.startActivity(
                                        Intent(
                                            android.provider.Settings
                                                .ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT,
                                            Uri.parse("package:${context.packageName}")
                                        )
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Tam Ekran Alarm İznini Aç")
                        }
                    }

                    if (!batteryExcluded) {
                        OutlinedButton(
                            onClick = {
                                runCatching {
                                    context.startActivity(
                                        Intent(
                                            android.provider.Settings
                                                .ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                                            Uri.parse("package:${context.packageName}")
                                        )
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Pil Optimizasyonundan Muaf Tut")
                        }
                    }
                }
            },
            confirmButton = {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    TextButton(
                        onClick = { showAlarmStatus = false }
                    ) {
                        Text("Kapat")
                    }
                }
            }
        )
    }

    if (newReportGroup || editReportGroup != null) {
        ReportGroupEditorDialog(
            existing = editReportGroup,
            profileId = activeProfileId,
            onDismiss = {
                newReportGroup = false
                editReportGroup = null
            },
            onSave = { group ->
                scope.launch(Dispatchers.IO) {
                    if (group.id == 0L) {
                        db.reportGroupDao().insert(group)
                    } else {
                        db.reportGroupDao().update(group)
                        db.medicationDao().updateReportGroupDates(
                            group.id,
                            group.startDate,
                            group.endDate,
                            group.warningDays
                        )
                    }
                }
                newReportGroup = false
                editReportGroup = null
            }
        )
    }

    if (newMedication || editMedication != null) {
        MedicationEditorDialog(
            existing = editMedication,
            profileId = activeProfileId,
            suggestions = allMedications,
            reportGroups = reportGroups,
            onCreateReportGroup = { group ->
                scope.launch(Dispatchers.IO) {
                    db.reportGroupDao().insert(group)
                }
            },
            onUpdateReportGroup = { group ->
                scope.launch(Dispatchers.IO) {
                    db.reportGroupDao().update(group)
                    db.medicationDao().updateReportGroupDates(
                        group.id,
                        group.startDate,
                        group.endDate,
                        group.warningDays
                    )
                }
            },
            onDismiss = { newMedication = false; editMedication = null },
            onSave = { med ->
                scope.launch(Dispatchers.IO) {
                    if (med.id == 0L) {
                        db.medicationDao().insert(med)
                    } else {
                        AlarmScheduler.cancelMedication(context, med.id)
                        db.medicationDao().update(med)
                    }
                    AlarmRescheduler.refreshAll(context)
                }
                newMedication = false
                editMedication = null
            },
            onDelete = { med ->
                scope.launch(Dispatchers.IO) {
                    AlarmScheduler.cancelMedication(context, med.id)
                    db.medicationDao().delete(med)
                    AlarmRescheduler.refreshAll(context)
                }
                editMedication = null
            }
        )
    }

    if (newProfile || editProfile != null) {
        ProfileEditorDialog(
            existing = editProfile,
            onDismiss = { newProfile = false; editProfile = null },
            onSave = { p ->
                scope.launch {
                    val savedId = withContext(Dispatchers.IO) {
                        if (p.id == 0L) db.profileDao().insert(p)
                        else {
                            db.profileDao().update(p)
                            p.id
                        }
                    }
                    if (p.id == 0L) {
                        activeProfileId = savedId
                        unlockedProfileId = null
                        pendingPatternProfile = p.copy(id = savedId)
                        forcePatternCreate = true
                    }
                }
                newProfile = false
                editProfile = null
            }
        )
    }

    if (showAddProfileChoice) {
        AlertDialog(
            onDismissRequest = { showAddProfileChoice = false },
            title = { Text("Yeni kişi nasıl oluşturulsun?") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Kişi bilgilerini elle girebilir veya parola korumalı .sifbackup / uyumlu eski ZIP yedeğinden içe aktarabilirsiniz.")
                    GlassLogoButton("Bilgileri Gir", {
                        showAddProfileChoice = false
                        newProfile = true
                    }, Modifier.fillMaxWidth())
                    GlassLogoButton("Yedekten İçe Aktar", {
                        showAddProfileChoice = false
                        showImportRoleChoice = true
                    }, Modifier.fillMaxWidth())
                    GlassLogoButton("İptal", {
                        showAddProfileChoice = false
                        addProfileImportMode = false
                    }, Modifier.fillMaxWidth())
                    GlassLogoButton("Geri", {
                        showAddProfileChoice = false
                    }, Modifier.fillMaxWidth())
                }
            },
            confirmButton = {},
            dismissButton = {}
        )
    }

    if (showImportRoleChoice) {
        AlertDialog(
            onDismissRequest = { showImportRoleChoice = false },
            title = { Text("Yeni kullanıcının yetkisi") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("İçe aktarılacak kişi yönetici mi, standart kullanıcı mı olacak?")
                    GlassLogoButton("Standart Kullanıcı", {
                        importNewProfileAsAdmin = false
                        addProfileImportMode = true
                        showImportRoleChoice = false
                        showImportSourceDialog = true
                    }, Modifier.fillMaxWidth())
                    GlassLogoButton("Yönetici", {
                        importNewProfileAsAdmin = true
                        addProfileImportMode = true
                        showImportRoleChoice = false
                        showImportSourceDialog = true
                    }, Modifier.fillMaxWidth())
                    GlassLogoButton("İptal", {
                        showImportRoleChoice = false
                        addProfileImportMode = false
                    }, Modifier.fillMaxWidth())
                    GlassLogoButton("Geri", {
                        showImportRoleChoice = false
                        showAddProfileChoice = true
                    }, Modifier.fillMaxWidth())
                }
            },
            confirmButton = {},
            dismissButton = {}
        )
    }

    if (addBp) BpDialog(
        onDismiss = { addBp = false },
        onSave = { s, d, p, note ->
            scope.launch(Dispatchers.IO) {
                db.vitalsDao().insertBp(BloodPressure(profileId = activeProfileId, systolic = s, diastolic = d, pulse = p, measuredAt = System.currentTimeMillis(), note = note))
            }
            addBp = false
        }
    )

    if (addGlucose) GlucoseDialog(
        onDismiss = { addGlucose = false },
        onSave = { v, t, note ->
            scope.launch(Dispatchers.IO) {
                db.vitalsDao().insertGlucose(BloodGlucose(profileId = activeProfileId, valueMgDl = v, measurementType = t, measuredAt = System.currentTimeMillis(), note = note))
            }
            addGlucose = false
        }
    )

    editBp?.let { item ->
        EditBpDialog(
            item = item,
            onDismiss = { editBp = null },
            onSave = { updated ->
                scope.launch(Dispatchers.IO) { db.vitalsDao().updateBp(updated) }
                editBp = null
            },
            onDelete = {
                scope.launch(Dispatchers.IO) { db.vitalsDao().deleteBp(item) }
                editBp = null
            }
        )
    }

    editGlucose?.let { item ->
        EditGlucoseDialog(
            item = item,
            onDismiss = { editGlucose = null },
            onSave = { updated ->
                scope.launch(Dispatchers.IO) { db.vitalsDao().updateGlucose(updated) }
                editGlucose = null
            },
            onDelete = {
                scope.launch(Dispatchers.IO) { db.vitalsDao().deleteGlucose(item) }
                editGlucose = null
            }
        )
    }

    pendingPatternProfile?.let { profile ->
        ProfilePatternGate(
            profile = profile,
            forceCreate = forcePatternCreate,
            onUnlocked = {
                if (
                    patternChangeProfileId == profile.id &&
                    !forcePatternCreate
                ) {
                    PatternStore.clear(context, profile.id)
                    unlockedProfileId = null
                    pendingPatternProfile = profile
                    forcePatternCreate = true
                } else {
                    activeProfileId = profile.id
                    forcePatternCreate = false
                    pendingPatternProfile = null
                    unlockedProfileId = profile.id
                    patternChangeProfileId = null
                    screen = Screen.TODAY
                }
            },
            onCancel = {
                pendingPatternProfile = null
                forcePatternCreate = false
                patternChangeProfileId = null
                if (profiles.size > 1) screen = Screen.PROFILES
            },
            onAdminRequested = {
                adminTargetProfile = profile
                adminPinPurpose =
                    if (patternChangeProfileId == profile.id)
                        AdminPinPurpose.CHANGE_PATTERN
                    else
                        AdminPinPurpose.UNLOCK_PROFILE
            }
        )
    }

    adminPinPurpose?.let { purpose ->
        AdminPinDialog(
            purpose = purpose,
            profileName = adminTargetProfile?.let {
                "${it.name} ${it.surname}".trim()
            },
            onDismiss = {
                adminPinPurpose = null
                adminTargetProfile = null
                adminDeleteProfile = null
            },
            onVerified = {
                when (purpose) {
                    AdminPinPurpose.INITIAL_SETUP -> {
                        AppPreferences.markVersionVerified(context, BuildConfig.VERSION_CODE)
                        adminPinPurpose = null
                        adminSessionUnlocked = true
                        pendingPatternProfile = null
                        forcePatternCreate = false
                        initialScreenSet = true
                        screen = Screen.PROFILES
                        showInitialImportChoice = true
                    }
                    AdminPinPurpose.UPDATE_VERIFICATION -> {
                        AppPreferences.markVersionVerified(context, BuildConfig.VERSION_CODE)
                        adminPinPurpose = null
                        adminSessionUnlocked = false
                        unlockedProfileId = null
                        screen = Screen.PROFILES
                    }
                    AdminPinPurpose.UNLOCK_PROFILE -> {
                        adminTargetProfile?.let { profile ->
                            PatternStore.clear(context, profile.id)
                            activeProfileId = profile.id
                            unlockedProfileId = null
                            pendingPatternProfile = profile
                            forcePatternCreate = true
                        }
                        adminPinPurpose = null
                        adminTargetProfile = null
                    }
                    AdminPinPurpose.CHANGE_PATTERN -> {
                        adminTargetProfile?.let { profile ->
                            PatternStore.clear(context, profile.id)
                            activeProfileId = profile.id
                            unlockedProfileId = null
                            patternChangeProfileId = profile.id
                            pendingPatternProfile = profile
                            forcePatternCreate = true
                        }
                        adminPinPurpose = null
                        adminTargetProfile = null
                    }
                    AdminPinPurpose.DELETE_PROFILE -> {
                        adminDeleteProfile?.let { profile ->
                            scope.launch(Dispatchers.IO) {
                                db.profileDao().deleteProfileCascade(profile.id)
                                PatternStore.clear(context, profile.id)
                            }
                            if (activeProfileId == profile.id) {
                                unlockedProfileId = null
                                pendingPatternProfile = null
                                patternChangeProfileId = null
                            }
                        }
                        adminDeleteProfile = null
                        adminTargetProfile = null
                        adminPinPurpose = null
                        screen = Screen.PROFILES
                    }
                    AdminPinPurpose.ADMIN_SETTINGS -> {
                        adminSessionUnlocked = true
                        adminPinPurpose = null
                    }
                }
            }
        )
    }
}

@Composable
private fun ProfilesScreen(
    profiles: List<UserProfile>,
    activeId: Long,
    modifier: Modifier,
    onSelect: (UserProfile) -> Unit,
    onEdit: (UserProfile) -> Unit,
    onDelete: (UserProfile) -> Unit,
    onChangePattern: (UserProfile) -> Unit
) {
    var deleteTarget by remember { mutableStateOf<UserProfile?>(null) }
    var secondConfirm by remember { mutableStateOf(false) }

    LazyColumn(
        modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(profiles, key = { it.id }) { p ->
            SifahaneCard(modifier = Modifier.fillMaxWidth(), onClick = { onSelect(p) }) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (!p.photoUri.isNullOrBlank()) {
                        Image(
                            painter = rememberAsyncImagePainter(p.photoUri),
                            contentDescription = "Kişi fotoğrafı",
                            modifier = Modifier
                                .size(104.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = LogoColorDark,
                            modifier = Modifier.size(92.dp)
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "${p.name} ${p.surname}".trim(),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        if (!p.birthDate.isNullOrBlank()) {
                            Text(
                                "Doğum tarihi: ${p.birthDate}",
                                textAlign = TextAlign.Center
                            )
                        }
                        Text(
                            "Kan grubu: ${p.bloodGroup}",
                            textAlign = TextAlign.Center
                        )
                        if (p.relation.isNotBlank()) {
                            Text(
                                "Yakınlık: ${p.relation}",
                                textAlign = TextAlign.Center
                            )
                        }
                        if (p.id == activeId) {
                            Text(
                                "Aktif kullanıcı",
                                color = LogoColor,
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    shadow = LogoTextShadow
                                )
                            )
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { onEdit(p) }) {
                            Icon(
                                Icons.Default.Edit,
                                "Düzenle",
                                tint = LogoColorDark
                            )
                        }
                        IconButton(onClick = { onChangePattern(p) }) {
                            Icon(
                                Icons.Default.Pattern,
                                "Deseni değiştir",
                                tint = LogoColorDark
                            )
                        }
                        IconButton(
                            enabled = profiles.size > 1,
                            onClick = {
                                deleteTarget = p
                                secondConfirm = false
                            }
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                "Kişiyi sil"
                            )
                        }
                    }
                }
            }
        }
    }

    deleteTarget?.let { target ->
        AlertDialog(
            onDismissRequest = {
                deleteTarget = null
                secondConfirm = false
            },
            title = { Text(if (secondConfirm) "Son onay" else "Kullanıcıyı sil") },
            text = {
                Text(
                    if (secondConfirm)
                        "${target.name} kullanıcısı ile ilişkili ilaçlar, ölçümler ve geçmiş kayıtları kalıcı olarak silinecek."
                    else
                        "${target.name} kullanıcısını silmek istediğinizden emin misiniz?"
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (!secondConfirm) secondConfirm = true
                    else {
                        onDelete(target)
                        deleteTarget = null
                        secondConfirm = false
                    }
                }) { Text(if (secondConfirm) "Kalıcı olarak sil" else "Devam et") }
            },
            dismissButton = {
                TextButton(onClick = {
                    deleteTarget = null
                    secondConfirm = false
                }) { Text("İptal") }
            }
        )
    }
}

@Composable
private fun TodayScreen(
    meds: List<Medication>,
    logs: List<DoseLog>,
    appointments: List<Appointment>,
    modifier: Modifier,
    onOpenAppointments: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val db = remember(context) { AppDatabase.get(context) }
    val scope = rememberCoroutineScope()
    val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    val timeFormatter = remember { SimpleDateFormat("HH:mm", Locale("tr", "TR")) }
    var selectedDose by remember { mutableStateOf<DailyDose?>(null) }
    var selectedDoseLog by remember { mutableStateOf<DoseLog?>(null) }
    var selectedTakenTime by remember { mutableStateOf<Long?>(null) }
    var takenTimeMenuExpanded by remember { mutableStateOf(false) }
    var undoDose by remember { mutableStateOf<DailyDose?>(null) }
    var undoDoseLog by remember { mutableStateOf<DoseLog?>(null) }
    var restoreLegacyStock by remember { mutableStateOf(true) }
    val doses = meds.filter {
        it.active && !it.archived && it.startDate <= today && (it.continuous || it.endDate == null || it.endDate >= today)
    }.flatMap { med ->
        med.timesCsv.split(",").mapNotNull { t ->
            val p = t.trim().split(":")
            if (p.size == 2) DailyDose(med, t.trim(), (p[0].toIntOrNull() ?: 0) * 60 + (p[1].toIntOrNull() ?: 0)) else null
        }
    }.sortedBy { it.minutes }

    val nextAppointment = appointments
        .filter { it.status == "PLANNED" && it.active && it.appointmentDateTime >= System.currentTimeMillis() }
        .minByOrNull { it.appointmentDateTime }

    LazyColumn(modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
        nextAppointment?.let { appointment ->
            item {
                SifahaneCard(modifier = Modifier.fillMaxWidth(), onClick = onOpenAppointments) {
                    Column(
                        Modifier.fillMaxWidth().padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            if (SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date(appointment.appointmentDateTime)) == today)
                                "Bugünkü Doktor Randevusu"
                            else
                                "Yaklaşan Doktor Randevusu",
                            color = LogoColorDark,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedLogoIcon(Icons.Default.Event, null, size = 20.dp)
                            Spacer(Modifier.width(7.dp))
                            Text(
                                SimpleDateFormat("dd.MM.yyyy EEEE • HH:mm", Locale("tr", "TR"))
                                    .format(Date(appointment.appointmentDateTime)),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        if (appointment.doctorName.isNotBlank()) Text(appointment.doctorName)
                        if (appointment.branch.isNotBlank()) Text(appointment.branch)
                        if (appointment.institution.isNotBlank()) Text(appointment.institution)
                        Text("Randevu ayrıntılarını açmak için dokunun.", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
        item {
            Text(
                "Bugünkü Tedavi Planı",
                modifier = Modifier.fillMaxWidth(),
                color = LogoColorDark,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    shadow = LogoTextShadow
                )
            )
        }
        items(doses, key = { "${it.medication.id}-${it.time}" }) { d ->
            val plannedForCard = Calendar.getInstance().apply {
                val parts = d.time.split(":")
                set(Calendar.HOUR_OF_DAY, parts.getOrNull(0)?.toIntOrNull() ?: 0)
                set(Calendar.MINUTE, parts.getOrNull(1)?.toIntOrNull() ?: 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
            val doseLog = logs
                .filter {
                    it.medicationId == d.medication.id &&
                    SimpleDateFormat("yyyy-MM-dd", Locale.US)
                        .format(Date(it.scheduledDateTime)) == today &&
                    kotlin.math.abs(it.scheduledDateTime - plannedForCard) < 90 * 60 * 1000L
                }
                .maxByOrNull { it.timestamp }

            SifahaneCard(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                        selectedDose = d
                        selectedDoseLog = doseLog
                        selectedTakenTime = doseLog
                            ?.takeIf { it.action == "ALINDI" }
                            ?.actualDateTime
                    }
            ) {
                Column {
                    if (!d.medication.photoUri.isNullOrBlank()) {
                        Image(
                            rememberAsyncImagePainter(d.medication.photoUri),
                            null,
                            Modifier.fillMaxWidth().height(260.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                    HorizontalDivider(thickness = 2.dp, color = LogoColor.copy(alpha = 0.85f))
                    Column(Modifier.padding(16.dp)) {
                        Text(d.time, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Text(d.medication.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        if (d.medication.purpose.isNotBlank()) Text(d.medication.purpose)
                        Text(d.medication.dose)
                        if (d.medication.notes.isNotBlank()) Text("Not: ${d.medication.notes}")
                        when (doseLog?.action) {
                            "ALINDI" -> Text(
                                "Alındı • ${doseLog.actualDateTime?.let { timeFormatter.format(Date(it)) } ?: "-"}",
                                color = LogoColorDark,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    shadow = LogoTextShadow
                                )
                            )
                            "10 DK ERTELENDİ" -> Text(
                                "10 Dakika Ertelendi",
                                color = LogoColorDark,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    shadow = LogoTextShadow
                                )
                            )
                            "BUGÜN ALINMADI" -> Text(
                                "Bugün Alınmayacak",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            "ALINDI GERİ ALINDI" -> Text(
                                "Henüz Alınmadı • Önceki alındı kaydı geri alındı",
                                color = LogoColorDark.copy(alpha = 0.82f),
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            else -> Text(
                                "Henüz Alınmadı",
                                color = LogoColorDark.copy(alpha = 0.72f)
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Alarm seçeneklerini açmak için karta dokunun",
                            color = LogoColorDark,
                            style = MaterialTheme.typography.bodySmall.copy(
                                shadow = LogoTextShadow
                            )
                        )
                    }
                }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 3.dp, color = LogoColor)
        }
    }

    selectedDose?.let { dose ->
        val planned = Calendar.getInstance().apply {
            val parts = dose.time.split(":")
            set(Calendar.HOUR_OF_DAY, parts.getOrNull(0)?.toIntOrNull() ?: 0)
            set(Calendar.MINUTE, parts.getOrNull(1)?.toIntOrNull() ?: 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        AlertDialog(
            onDismissRequest = { selectedDose = null; selectedDoseLog = null; selectedTakenTime = null; takenTimeMenuExpanded = false },
            title = {
                Text(
                    dose.medication.name,
                    modifier = Modifier.fillMaxWidth(),
                    color = LogoColorDark,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        shadow = LogoTextShadow
                    )
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Planlanan Saat: ${dose.time}", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                    Text(dose.medication.dose, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                    if (dose.medication.purpose.isNotBlank()) {
                        Text(dose.medication.purpose, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                    }

                    if (selectedDoseLog?.action == "ALINDI") {
                        Text(
                            "Kayıtlı Alınma Saati: ${
                                selectedDoseLog?.actualDateTime?.let {
                                    timeFormatter.format(Date(it))
                                } ?: "-"
                            }",
                            color = LogoColorDark,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                shadow = LogoTextShadow
                            )
                        )
                    }

                    fun saveTakenTime(takenAt: Long) {
                        scope.launch {
                            AlarmScheduler.cancelSnooze(
                                context,
                                dose.medication.id
                            )
                            val existingTaken = selectedDoseLog
                                ?.takeIf { it.action == "ALINDI" }
                            val savedLog = db.withTransaction {
                                if (existingTaken != null) {
                                    existingTaken.copy(
                                        actualDateTime = takenAt,
                                        timestamp = System.currentTimeMillis()
                                    ).also {
                                        db.doseLogDao().update(it)
                                    }
                                } else {
                                    val stockDecreased = db.medicationDao()
                                        .decreaseStock(dose.medication.id) > 0
                                    DoseLog(
                                        profileId = dose.medication.profileId,
                                        medicationId = dose.medication.id,
                                        medicationName = dose.medication.name,
                                        scheduledDateTime = planned,
                                        actualDateTime = takenAt,
                                        action = "ALINDI",
                                        stockDecreased = stockDecreased
                                    ).also {
                                        db.doseLogDao().insert(it)
                                    }
                                }
                            }
                            selectedTakenTime = takenAt
                            selectedDoseLog = savedLog
                            takenTimeMenuExpanded = false
                        }
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "İlacın Alındığı Saat",
                            modifier = Modifier.fillMaxWidth().sifahaneSoftBoundary(2.dp),
                            color = LogoColorDark,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                shadow = LogoTextShadow
                            )
                        )

                        Spacer(Modifier.height(5.dp))

                        Box(
                            modifier = Modifier.width(230.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            OutlinedButton(
                                onClick = { takenTimeMenuExpanded = true },
                                modifier = Modifier.fillMaxWidth(),
                                border = BorderStroke(1.5.dp, LogoColor),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = Vantablack05,
                                    contentColor = LogoColorDark
                                )
                            ) {
                                Text(
                                    selectedTakenTime?.let {
                                        timeFormatter.format(Date(it))
                                    } ?: "Saat Seçiniz",
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        shadow = LogoTextShadow
                                    )
                                )
                            }

                            DropdownMenu(
                                expanded = takenTimeMenuExpanded,
                                onDismissRequest = {
                                    takenTimeMenuExpanded = false
                                },
                                modifier = Modifier
                                    .width(230.dp)
                                    .border(
                                        1.5.dp,
                                        LogoColor,
                                        RoundedCornerShape(14.dp)
                                    ),
                                shape = RoundedCornerShape(14.dp),
                                containerColor = Color(0xFFF2F2F2),
                                tonalElevation = 0.dp,
                                shadowElevation = 6.dp
                            ) {
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            "Zamanında",
                                            modifier = Modifier.fillMaxWidth(),
                                            color = LogoColorDark,
                                            textAlign = TextAlign.Center,
                                            style = MaterialTheme.typography.bodyLarge.copy(
                                                shadow = LogoTextShadow
                                            )
                                        )
                                    },
                                    onClick = { saveTakenTime(planned) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Vantablack05)
                                )
                                HorizontalDivider(
                                    color = LogoColor.copy(alpha = 0.45f)
                                )
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            "Şimdi",
                                            modifier = Modifier.fillMaxWidth(),
                                            color = LogoColorDark,
                                            textAlign = TextAlign.Center,
                                            style = MaterialTheme.typography.bodyLarge.copy(
                                                shadow = LogoTextShadow
                                            )
                                        )
                                    },
                                    onClick = {
                                        saveTakenTime(System.currentTimeMillis())
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Vantablack05)
                                )
                                HorizontalDivider(
                                    color = LogoColor.copy(alpha = 0.45f)
                                )
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            "Saat Seç",
                                            modifier = Modifier.fillMaxWidth(),
                                            color = LogoColorDark,
                                            textAlign = TextAlign.Center,
                                            style = MaterialTheme.typography.bodyLarge.copy(
                                                shadow = LogoTextShadow
                                            )
                                        )
                                    },
                                    onClick = {
                                        takenTimeMenuExpanded = false
                                        val initial = Calendar.getInstance().apply {
                                            timeInMillis = selectedTakenTime
                                                ?: System.currentTimeMillis()
                                        }
                                        TimePickerDialog(
                                            context,
                                            { _, hour, minute ->
                                                val chosen = Calendar.getInstance().apply {
                                                    set(Calendar.HOUR_OF_DAY, hour)
                                                    set(Calendar.MINUTE, minute)
                                                    set(Calendar.SECOND, 0)
                                                    set(Calendar.MILLISECOND, 0)
                                                }.timeInMillis
                                                saveTakenTime(chosen)
                                            },
                                            initial.get(Calendar.HOUR_OF_DAY),
                                            initial.get(Calendar.MINUTE),
                                            true
                                        ).show()
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Vantablack05)
                                )
                            }
                        }
                    }

                    Button(
                        onClick = {
                            scope.launch {
                                saveTakenTime(
                                    selectedTakenTime ?: System.currentTimeMillis()
                                )
                                selectedDose = null
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LogoColor,
                            contentColor = Color(0xFF123A37)
                        )
                    ) {
                        Text(
                            "İLACI ALDIM",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium.copy(
                                shadow = LogoTextShadow
                            )
                        )
                    }

                    selectedDoseLog
                        ?.takeIf { it.action == "ALINDI" }
                        ?.let { takenLog ->
                            OutlinedButton(
                                onClick = {
                                    undoDose = dose
                                    undoDoseLog = takenLog
                                    restoreLegacyStock = takenLog.stockDecreased ?: true
                                    selectedDose = null
                                    selectedDoseLog = null
                                    selectedTakenTime = null
                                    takenTimeMenuExpanded = false
                                },
                                modifier = Modifier.fillMaxWidth(),
                                border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.error),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.28f),
                                    contentColor = MaterialTheme.colorScheme.error
                                )
                            ) {
                                Icon(Icons.Default.Undo, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "ALINDI KAYDINI GERİ AL",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                    OutlinedButton(
                        onClick = {
                            scope.launch {
                                AlarmScheduler.cancelSnooze(
                                    context,
                                    dose.medication.id
                                )
                                val snoozeAt = System.currentTimeMillis() + 10 * 60_000L
                                AlarmScheduler.snoozeAt(
                                    context,
                                    dose.medication,
                                    dose.time,
                                    planned,
                                    snoozeAt
                                )
                                db.doseLogDao().insert(
                                    DoseLog(
                                        profileId = dose.medication.profileId,
                                        medicationId = dose.medication.id,
                                        medicationName = dose.medication.name,
                                        scheduledDateTime = planned,
                                        actualDateTime = snoozeAt,
                                        action = "10 DK ERTELENDİ"
                                    )
                                )
                                selectedDose = null
                                selectedDoseLog = null
                                selectedTakenTime = null
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        border = BorderStroke(1.5.dp, LogoColor),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Vantablack10,
                            contentColor = LogoColorDark
                        )
                    ) {
                        Text("10 DK ERTELE")
                    }

                    OutlinedButton(
                        onClick = {
                            scope.launch {
                                AlarmScheduler.cancelSnooze(
                                    context,
                                    dose.medication.id
                                )
                                db.doseLogDao().insert(
                                    DoseLog(
                                        profileId = dose.medication.profileId,
                                        medicationId = dose.medication.id,
                                        medicationName = dose.medication.name,
                                        scheduledDateTime = planned,
                                        actualDateTime = System.currentTimeMillis(),
                                        action = "BUGÜN ALINMADI"
                                    )
                                )
                                selectedDose = null
                                selectedDoseLog = null
                                selectedTakenTime = null
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        border = BorderStroke(1.5.dp, LogoColor),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Vantablack10,
                            contentColor = LogoColorDark
                        )
                    ) {
                        Text("BUGÜN ALMAYACAĞIM")
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    TextButton(
                        onClick = {
                            selectedDose = null
                            selectedDoseLog = null
                            selectedTakenTime = null
                        },
                        colors = ButtonDefaults.textButtonColors(
                            containerColor = Vantablack10,
                            contentColor = LogoColorDark
                        )
                    ) {
                        Text("Kapat", textAlign = TextAlign.Center)
                    }
                }
            }
        )
    }

    val pendingUndoDose = undoDose
    val pendingUndoLog = undoDoseLog
    if (pendingUndoDose != null && pendingUndoLog != null) {
        AlertDialog(
            onDismissRequest = {
                undoDose = null
                undoDoseLog = null
            },
            title = {
                Text(
                    "Alındı Kaydını Geri Al",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "${pendingUndoDose.medication.name} için ${pendingUndoDose.time} dozuna ait ‘Alındı’ kaydı geri alınacak. Doz yeniden ‘Henüz Alınmadı’ durumuna dönecektir.",
                        textAlign = TextAlign.Center
                    )
                    when (pendingUndoLog.stockDecreased) {
                        true -> Text(
                            "Bu kayıt oluşturulurken stoktan 1 adet düşülmüştü. Geri alma işleminde stoka 1 adet eklenecek.",
                            fontWeight = FontWeight.Bold
                        )
                        false -> Text(
                            "Bu kayıt oluşturulurken stok azaltılmamıştı. Geri alma işleminde stok değişmeyecek.",
                            fontWeight = FontWeight.Bold
                        )
                        null -> {
                            Text(
                                "Bu kayıt önceki bir Şifahane sürümünde oluşturulduğu için stok hareketi kesin olarak belirlenemiyor.",
                                color = MaterialTheme.colorScheme.error
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { restoreLegacyStock = !restoreLegacyStock },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = restoreLegacyStock,
                                    onCheckedChange = { restoreLegacyStock = it }
                                )
                                Text("Stoka 1 adet geri ekle")
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val restoreStock = when (pendingUndoLog.stockDecreased) {
                            true -> true
                            false -> false
                            null -> restoreLegacyStock
                        }
                        scope.launch {
                            runCatching {
                                db.withTransaction {
                                    if (restoreStock) {
                                        db.medicationDao().increaseStock(
                                            pendingUndoDose.medication.id
                                        )
                                    }
                                    db.doseLogDao().insert(
                                        DoseLog(
                                            profileId = pendingUndoDose.medication.profileId,
                                            medicationId = pendingUndoDose.medication.id,
                                            medicationName = pendingUndoDose.medication.name,
                                            scheduledDateTime = pendingUndoLog.scheduledDateTime,
                                            actualDateTime = System.currentTimeMillis(),
                                            action = "ALINDI GERİ ALINDI",
                                            stockDecreased = false
                                        )
                                    )
                                }
                                AlarmScheduler.cancelSnooze(
                                    context,
                                    pendingUndoDose.medication.id
                                )
                            }.onSuccess {
                                android.widget.Toast.makeText(
                                    context,
                                    if (restoreStock)
                                        "Alındı kaydı geri alındı ve stok düzeltildi."
                                    else
                                        "Alındı kaydı geri alındı.",
                                    android.widget.Toast.LENGTH_SHORT
                                ).show()
                            }.onFailure {
                                android.widget.Toast.makeText(
                                    context,
                                    "Kayıt geri alınamadı: ${it.message ?: "Bilinmeyen hata"}",
                                    android.widget.Toast.LENGTH_LONG
                                ).show()
                            }
                            undoDose = null
                            undoDoseLog = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                ) {
                    Text("Evet, Kaydı Geri Al")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        undoDose = null
                        undoDoseLog = null
                    }
                ) {
                    Text("İptal")
                }
            }
        )
    }

}


private data class HelpTopic(
    val title: String,
    val body: String,
    val target: Screen? = null,
    val targetLabel: String? = null
)


private suspend fun dbAppointmentRefresh(context: Context) {
    val db = AppDatabase.get(context)
    val appointments = db.appointmentDao().activeFuture(System.currentTimeMillis())
    appointments.forEach {
        AppointmentAlarmScheduler.cancel(context, it.id)
        AppointmentAlarmScheduler.schedule(context, it)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsScreen(
    modifier: Modifier,
    initialPage: SettingsPage,
    onPageChanged: (SettingsPage) -> Unit,
    alarmRefreshBusy: Boolean,
    alarmRefreshResult: AlarmRefreshResult?,
    onAlarmRefresh: () -> Unit,
    onClearStaleAlarms: () -> Unit,
    onAlarmStatus: () -> Unit,
    onTestAlarm: () -> Unit,
    hasActiveProfile: Boolean,
    backupStatusText: String,
    profiles: List<UserProfile>,
    activeProfileId: Long,
    canManageSecurity: Boolean,
    databaseVersion: Int,
    bottomMenuSize: BottomMenuSize,
    onBottomMenuSizeChange: (BottomMenuSize) -> Unit,
    onResetBottomMenuOrder: () -> Unit,
    onExportPersonData: () -> Unit,
    onImportPersonData: () -> Unit,
    onSetRole: (Long, Boolean) -> Unit,
    onSetAdminPin: (Long, String) -> Unit,
    onNavigate: (Screen) -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val accessibilityPrefs = remember {
        context.getSharedPreferences("sifahane_accessibility", Context.MODE_PRIVATE)
    }

    var page by remember(initialPage) { mutableStateOf(initialPage) }
    LaunchedEffect(page) { onPageChanged(page) }
    fun openSettings(target: SettingsPage) {
        page = target
        AppPreferences.recordSettingsUse(context, target.name)
    }
    fun settingsLabel(target: SettingsPage) = when (target) {
        SettingsPage.ALARMS -> "Alarm ve Bildirimler"
        SettingsPage.APPOINTMENTS -> "Randevu Hatırlatıcıları"
        SettingsPage.BACKUP -> "Yedekleme ve Geri Yükleme"
        SettingsPage.SECURITY -> "Güvenlik ve Yetkilendirme"
        SettingsPage.PERMISSIONS -> "Android İzinleri"
        SettingsPage.ACCESSIBILITY -> "Görünüm ve Erişilebilirlik"
        SettingsPage.THEME -> "Tema Ayarları"
        SettingsPage.DATABASE -> "Veritabanı ve Sistem"
        SettingsPage.HELP -> "Yardım ve Kullanım"
        SettingsPage.ABOUT -> "Uygulama Hakkında"
        SettingsPage.HOME -> "Ayarlar"
    }
    var query by remember { mutableStateOf("") }
    var expandedHelpTitle by remember { mutableStateOf<String?>(null) }
    var showComprehensiveGuide by remember { mutableStateOf(false) }
    var guideQuery by remember { mutableStateOf("") }
    var largeSettingsText by remember {
        mutableStateOf(accessibilityPrefs.getBoolean("large_settings_text", false))
    }
    var highContrastSettings by remember {
        mutableStateOf(accessibilityPrefs.getBoolean("high_contrast_settings", false))
    }
    var permissionRefresh by remember { mutableIntStateOf(0) }
    val notificationPermissionRequest = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { permissionRefresh++ }
    val cameraPermissionRequest = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { permissionRefresh++ }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) permissionRefresh++
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    var pinTarget by remember { mutableStateOf<UserProfile?>(null) }
    var newAdminPin by remember { mutableStateOf("") }
    var confirmAdminPin by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf<String?>(null) }
    var snoozeValues by remember { mutableStateOf(AppPreferences.snoozeMinutes(context).map(Int::toString)) }
    var snoozeError by remember { mutableStateOf<String?>(null) }
    var alarmRingDuration by remember { mutableLongStateOf(AppPreferences.alarmRingDurationMillis(context)) }
    var alarmDurationMenuExpanded by remember { mutableStateOf(false) }
    var confirmClearAlarms by remember { mutableStateOf(false) }
    var lockTimeout by remember { mutableLongStateOf(AppPreferences.lockTimeoutMillis(context)) }
    var lockTimeoutMenuExpanded by remember { mutableStateOf(false) }
    val storedTheme = remember { ThemePreferences.load(context) }
    var themePreset by remember { mutableStateOf(storedTheme.preset) }
    var themeFont by remember { mutableStateOf(when (storedTheme.font) { "serif" -> "noto_serif"; "sans" -> "roboto"; else -> storedTheme.font }) }
    var themeScale by remember { mutableFloatStateOf(storedTheme.fontScale) }
    var themeOpacity by remember { mutableFloatStateOf(storedTheme.accentOpacity) }
    var themePresetExpanded by remember { mutableStateOf(false) }
    var themeFontExpanded by remember { mutableStateOf(false) }

    val titleSize = if (largeSettingsText) 21.sp else 18.sp
    val bodySize = if (largeSettingsText) 17.sp else 14.sp
    val supportingSize = if (largeSettingsText) 15.sp else 13.sp
    val settingsBackground = if (highContrastSettings) Color.White else Color.Transparent
    val cardBackground = if (highContrastSettings) Color.White else Vantablack05
    val cardBorder = if (highContrastSettings) LogoColorDark else LogoColor.copy(alpha = 0.65f)

    val helpTopics = remember {
        listOf(
            HelpTopic(
                "İlaç ekleme ve düzenleme",
                "İlaçlar sayfasındaki artı düğmesiyle yeni ilaç ekleyin. Kayıtlı ilaca dokunarak doz, saat, stok, doktor ve rapor bilgilerini düzenleyin.",
                Screen.MEDICINES,
                "İlaçlara Git"
            ),
            HelpTopic(
                "Alarm çalışmıyorsa",
                "Alarm Yenile işlemini çalıştırın. Ardından bildirim, kesin alarm, tam ekran alarm ve pil optimizasyonu izinlerini Alarm Durumu bölümünden kontrol edin."
            ),
            HelpTopic(
                "Bugünün geçmiş ilaç saatleri",
                "Bugün saati geçmiş ve henüz cevaplanmamış dozlar Alarm Yenile sonrasında sırayla gösterilir. İlacın alınıp alınmadığı ve alındıysa gerçek saat kaydedilir.",
                Screen.TODAY,
                "Bugüne Git"
            ),
            HelpTopic(
                "Raporlu ilaçlar",
                "İlaç kaydında Raporlu İlaç seçeneğini açın. Rapor başlangıç-bitiş tarihlerini veya bağlı rapor grubunu belirleyin.",
                Screen.MEDICINES,
                "İlaçlara Git"
            ),
            HelpTopic(
                "Tansiyon ve şeker ölçümü",
                "Ölçümler sayfasında ilgili sekmeyi seçip artı düğmesine dokunun. Kaydı daha sonra üzerine dokunarak düzenleyebilirsiniz.",
                Screen.MEASUREMENTS,
                "Ölçümlere Git"
            ),
            HelpTopic(
                "Rapor ve Excel işlemleri",
                "Raporlar sayfasında tarih aralığını seçin, grafik rapor oluşturun veya kayıtları Excel dosyası olarak dışa aktarın.",
                Screen.REPORTS,
                "Raporlara Git"
            ),
            HelpTopic(
                "Kullanıcı profili ve desen",
                "Kişiler sayfasında kullanıcı ekleyebilir, profil bilgilerini düzenleyebilir, desen şifresini değiştirebilir ve aktif kişiyi seçebilirsiniz.",
                Screen.PROFILES,
                "Kişilere Git"
            ),
            HelpTopic(
                "Yedekleme ve geri yükleme",
                "Yedekleme ve Geri Yükleme bölümünden parola korumalı AES-GCM .sifbackup dosyası oluşturabilir veya yeni/eski uyumlu bir Şifahane yedeğini içeri aktarabilirsiniz."
            ),
            HelpTopic(
                "Kullanıcı rolleri ve yöneticiler",
                "Güvenlik ve Yetkilendirme bölümünden kullanıcıları yönetici veya standart kullanıcı olarak atayabilir, her yönetici için ayrı şifre belirleyebilirsiniz."
            ),
            HelpTopic(
                "Gizlilik ve veri güvenliği",
                "Sağlık kayıtları cihazdaki uygulama veritabanında tutulur. Cihaz kilidi ve kullanıcı desenlerini etkin tutun; dışa aktarılan dosyaları güvenli yerde saklayın."
            ),
            HelpTopic(
                "İlk kurulum ve güncelleme doğrulaması",
                "İlk kurulumda kullanıcı kendi 4–12 haneli yönetici şifresini oluşturur; uygulamada varsayılan veya arka kapı şifresi bulunmaz. PIN ve profil desenleri salt’lı PBKDF2 ile korunur; art arda hatalı denemelerde kalıcı ve artan bekleme süresi uygulanır."
            ),
            HelpTopic(
                "Grup ilaç alarmı",
                "Aynı dakikadaki ilaçlar tek ses ve tek kartta gösterilir. Farklı kişilerin ilaçları kullanıcı sekmeleriyle ayrılır; ilaçlar tek tek veya seçili kullanıcı için topluca cevaplanabilir."
            ),
            HelpTopic(
                "Erteleme sürelerini değiştirme",
                "Ayarlar > Alarm ve Bildirimler bölümünde üç farklı erteleme süresi belirleyin. Alarm kartındaki Ertele seçimi bu üç süreyi gösterir."
            ),
            HelpTopic(
                "Otomatik desen kilidi",
                "Ayarlar > Güvenlik ve Yetkilendirme bölümünden işlem yapılmadığında kilitlenme süresini seçin. Alarm ekranı bu otomatik kilitten etkilenmez."
            ),
            HelpTopic(
                "Yedek alma ve ikinci kopya",
                "İlk dışa aktarmada dahili depolama konumunu bir kez seçin. Şifahane Yedek klasörü otomatik oluşturulur; ilk kopyadan sonra isteğe bağlı ikinci hedef seçilebilir."
            ),
            HelpTopic(
                "Telefon değiştirirken",
                "Eski telefonda kişi verilerini dışarı aktarın, ZIP dosyasını güvenli biçimde yeni telefona taşıyın ve ilk kurulumdaki içe aktarma seçeneğini kullanın."
            ),
            HelpTopic(
                "Grafik raporları okumak",
                "Raporlar bölümünde tarih aralığını seçip grafik oluşturun. Yatay eksen tarihleri, dikey eksen ölçüm değerlerini gösterir; açıklamalar veri serilerini belirtir."
            )
        )
    }

    val filteredHelp = helpTopics.filter {
        query.isBlank() ||
            it.title.contains(query, ignoreCase = true) ||
            it.body.contains(query, ignoreCase = true)
    }

    if (showComprehensiveGuide) {
        val guideSections = comprehensiveUserGuideSections().filter { (title, body) ->
            guideQuery.isBlank() || title.contains(guideQuery, true) || body.contains(guideQuery, true)
        }
        AlertDialog(
            onDismissRequest = { showComprehensiveGuide = false },
            title = { Text("Kapsamlı Kullanıcı Kılavuzu") },
            text = {
                Column(Modifier.fillMaxWidth().heightIn(max = 620.dp)) {
                    OutlinedTextField(
                        value = guideQuery,
                        onValueChange = { guideQuery = it },
                        label = { Text("Kılavuzda ara") },
                        leadingIcon = { Icon(Icons.Default.Search, null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(Modifier.height(8.dp))
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        item { Text("Uygulama ve kılavuz sürümü: ${BuildConfig.VERSION_NAME}", fontWeight = FontWeight.Bold) }
                        items(guideSections, key = { it.first }) { (title, body) ->
                            Column {
                                Text(title, fontWeight = FontWeight.Bold, color = LogoColorDark)
                                Text(body, color = LogoColorDark)
                            }
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showComprehensiveGuide = false }) { Text("KAPAT") } }
        )
    }

    androidx.activity.compose.BackHandler(enabled = page != SettingsPage.HOME) {
        page = SettingsPage.HOME
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(settingsBackground)
    ) {
        SettingsPageHeader(
            title = when (page) {
                SettingsPage.HOME -> "Ayarlar"
                SettingsPage.ALARMS -> "Alarm ve Bildirimler"
                SettingsPage.APPOINTMENTS -> "Randevu Hatırlatıcıları"
                SettingsPage.BACKUP -> "Yedekleme ve Geri Yükleme"
                SettingsPage.SECURITY -> "Güvenlik ve Yetkilendirme"
                SettingsPage.PERMISSIONS -> "Android İzinleri"
                SettingsPage.ACCESSIBILITY -> "Görünüm ve Erişilebilirlik"
                SettingsPage.THEME -> "Tema Ayarları"
                SettingsPage.DATABASE -> "Veritabanı ve Sistem"
                SettingsPage.HELP -> "Yardım ve Kullanım"
                SettingsPage.ABOUT -> "Uygulama Hakkında"
            },
            showBack = page != SettingsPage.HOME,
            titleSize = titleSize,
            onBack = { page = SettingsPage.HOME }
        )

        when (page) {
            SettingsPage.HOME -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Text(
                            "Değiştirmek veya incelemek istediğiniz bölümü seçin.",
                            modifier = Modifier.fillMaxWidth().padding(bottom = 2.dp),
                            textAlign = TextAlign.Center,
                            color = LogoColorDark,
                            fontSize = bodySize
                        )
                    }
                    AppPreferences.lastUsedSetting(context)
                        ?.let { runCatching { SettingsPage.valueOf(it) }.getOrNull() }
                        ?.takeIf { it != SettingsPage.HOME }
                        ?.let { recent ->
                            item {
                                SettingsCategoryCard(
                                    title = "Son kullanılan: ${settingsLabel(recent)}",
                                    description = "En son açtığınız ayara hızlı erişim",
                                    icon = Icons.Default.PushPin,
                                    titleSize = titleSize, bodySize = supportingSize,
                                    cardBackground = cardBackground, borderColor = cardBorder
                                ) { openSettings(recent) }
                            }
                        }
                    val settingKeys = SettingsPage.entries.filter { it != SettingsPage.HOME }.map { it.name }
                    val lastKey = AppPreferences.lastUsedSetting(context)
                    AppPreferences.frequentlyUsedSettings(context, settingKeys)
                        .filter { it != lastKey }
                        .mapNotNull { runCatching { SettingsPage.valueOf(it) }.getOrNull() }
                        .forEach { frequent ->
                            item {
                                SettingsCategoryCard(
                                    title = "Sık kullanılan: ${settingsLabel(frequent)}",
                                    description = "Kullanım sıklığınıza göre önerildi",
                                    icon = Icons.Default.Star,
                                    titleSize = titleSize, bodySize = supportingSize,
                                    cardBackground = cardBackground, borderColor = cardBorder
                                ) { openSettings(frequent) }
                            }
                        }
                    item {
                        SettingsCategoryCard(
                            title = "Alarm ve Bildirimler",
                            description = "Alarm yenileme, izin kontrolü ve test alarmı",
                            icon = Icons.Default.NotificationsActive,
                            titleSize = titleSize,
                            bodySize = supportingSize,
                            cardBackground = cardBackground,
                            borderColor = cardBorder
                        ) { openSettings(SettingsPage.ALARMS) }
                    }
                    item {
                        SettingsCategoryCard(
                            title = "Randevu Hatırlatıcıları",
                            description = "Randevu alarmları ve varsayılan hatırlatma zamanları",
                            icon = Icons.Default.EventNote,
                            titleSize = titleSize,
                            bodySize = supportingSize,
                            cardBackground = cardBackground,
                            borderColor = cardBorder
                        ) { openSettings(SettingsPage.APPOINTMENTS) }
                    }
                    item {
                        SettingsCategoryCard(
                            title = "Yedekleme ve Geri Yükleme",
                            description = "Kişi verilerini dışa aktarın veya yedekten geri yükleyin",
                            icon = Icons.Default.Backup,
                            titleSize = titleSize,
                            bodySize = supportingSize,
                            cardBackground = cardBackground,
                            borderColor = cardBorder
                        ) { openSettings(SettingsPage.BACKUP) }
                    }
                    item {
                        SettingsCategoryCard(
                            title = "Güvenlik ve Yetkilendirme",
                            description = "Kullanıcı rolleri, yöneticiler ve yönetici şifreleri",
                            icon = Icons.Default.AdminPanelSettings,
                            titleSize = titleSize,
                            bodySize = supportingSize,
                            cardBackground = cardBackground,
                            borderColor = cardBorder
                        ) { openSettings(SettingsPage.SECURITY) }
                    }
                    item {
                        SettingsCategoryCard(
                            title = "Android İzinleri",
                            description = "Bildirim, alarm, pil ve kamera izinlerini tek merkezden yönetin",
                            icon = Icons.Default.Security,
                            titleSize = titleSize,
                            bodySize = supportingSize,
                            cardBackground = cardBackground,
                            borderColor = cardBorder
                        ) { openSettings(SettingsPage.PERMISSIONS) }
                    }
                    item {
                        SettingsCategoryCard(
                            title = "Görünüm ve Erişilebilirlik",
                            description = "Ayarlar ekranında büyük yazı ve yüksek kontrast",
                            icon = Icons.Default.AccessibilityNew,
                            titleSize = titleSize,
                            bodySize = supportingSize,
                            cardBackground = cardBackground,
                            borderColor = cardBorder
                        ) { openSettings(SettingsPage.ACCESSIBILITY) }
                    }
                    item {
                        SettingsCategoryCard(
                            title = "Tema Ayarları",
                            description = "Renk düzeni, yazı tipi ve yazı büyüklüğü",
                            icon = Icons.Default.Palette,
                            titleSize = titleSize,
                            bodySize = supportingSize,
                            cardBackground = cardBackground,
                            borderColor = cardBorder
                        ) { openSettings(SettingsPage.THEME) }
                    }
                    item {
                        SettingsCategoryCard(
                            title = "Veritabanı ve Sistem",
                            description = "Veritabanı sürümü ve veri geçiş durumu",
                            icon = Icons.Default.Storage,
                            titleSize = titleSize,
                            bodySize = supportingSize,
                            cardBackground = cardBackground,
                            borderColor = cardBorder
                        ) { openSettings(SettingsPage.DATABASE) }
                    }
                    item {
                        SettingsCategoryCard(
                            title = "Yardım ve Kullanım",
                            description = "Arama yapılabilen kullanım açıklamaları",
                            icon = Icons.Default.HelpOutline,
                            titleSize = titleSize,
                            bodySize = supportingSize,
                            cardBackground = cardBackground,
                            borderColor = cardBorder
                        ) { openSettings(SettingsPage.HELP) }
                    }
                    item {
                        SettingsCategoryCard(
                            title = "Uygulama Hakkında",
                            description = "Şifahane sürüm ve uygulama bilgileri",
                            icon = Icons.Default.Info,
                            titleSize = titleSize,
                            bodySize = supportingSize,
                            cardBackground = cardBackground,
                            borderColor = cardBorder
                        ) { openSettings(SettingsPage.ABOUT) }
                    }
                }
            }

            SettingsPage.ALARMS -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        SettingsInformationCard(
                            title = "Alarm erteleme süreleri",
                            body = "Alarm kartındaki üç erteleme seçeneğini dakika olarak belirleyin (1–180).",
                            titleSize = bodySize,
                            bodySize = supportingSize,
                            cardBackground = cardBackground,
                            borderColor = cardBorder
                        )
                    }
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth().sifahaneSoftBoundary(2.dp),
                            border = BorderStroke(1.dp, cardBorder),
                            colors = CardDefaults.cardColors(containerColor = cardBackground)
                        ) {
                            Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                snoozeValues.forEachIndexed { index, value ->
                                    OutlinedTextField(
                                        value = value,
                                        onValueChange = { input ->
                                            if (input.length <= 3 && input.all(Char::isDigit)) {
                                                snoozeValues = snoozeValues.toMutableList().also { it[index] = input }
                                            }
                                        },
                                        label = { Text("${index + 1}. erteleme süresi (dk)") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = standardFieldColors()
                                    )
                                }
                                snoozeError?.let { Text(it, color = Color(0xFFE30A17)) }
                                Button(onClick = {
                                    val parsed = snoozeValues.mapNotNull(String::toIntOrNull)
                                    if (parsed.size != 3 || parsed.any { it !in 1..180 } || parsed.distinct().size != 3) {
                                        snoozeError = "Üç farklı süreyi 1–180 dakika arasında girin."
                                    } else {
                                        AppPreferences.saveSnoozeMinutes(context, parsed)
                                        snoozeError = null
                                    }
                                }, modifier = Modifier.fillMaxWidth()) { Text("ERTELEME SÜRELERİNİ KAYDET") }
                            }
                        }
                    }
                    item {
                        val durationOptions = listOf(
                            30_000L to "30 saniye", 60_000L to "1 dakika",
                            120_000L to "2 dakika", 180_000L to "3 dakika",
                            300_000L to "5 dakika", 600_000L to "10 dakika"
                        )
                        ExposedDropdownMenuBox(
                            expanded = alarmDurationMenuExpanded,
                            onExpandedChange = { alarmDurationMenuExpanded = !alarmDurationMenuExpanded },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = durationOptions.firstOrNull { it.first == alarmRingDuration }?.second ?: "2 dakika",
                                onValueChange = {}, readOnly = true,
                                label = { Text("Alarm çalma süresi") },
                                supportingText = { Text("Süre dolunca bekleyen alarm 5 dakika ertelenir.") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(alarmDurationMenuExpanded) },
                                modifier = Modifier.menuAnchor().fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = alarmDurationMenuExpanded,
                                onDismissRequest = { alarmDurationMenuExpanded = false }
                            ) {
                                durationOptions.forEach { (value, label) ->
                                    DropdownMenuItem(text = { Text(label) }, onClick = {
                                        alarmRingDuration = value
                                        AppPreferences.saveAlarmRingDurationMillis(context, value)
                                        alarmDurationMenuExpanded = false
                                    })
                                }
                            }
                        }
                    }
                    item {
                        AccessibleSettingsButton(
                            text = if (alarmRefreshBusy) "Alarmlar Yenileniyor" else "Alarmları Yenile",
                            description = "Aktif ilaçların alarm planını yeniden oluşturur.",
                            icon = Icons.Default.Refresh,
                            enabled = !alarmRefreshBusy,
                            titleSize = bodySize,
                            supportingSize = supportingSize,
                            onClick = onAlarmRefresh
                        )
                    }
                    item {
                        AccessibleSettingsButton(
                            text = if (alarmRefreshBusy) "Alarm İstekleri İşleniyor" else "Önceden Kalmış Alarm İsteklerini Sil",
                            description = "Şifahane'nin kayıtlı eski alarm isteklerini temizler ve geçerli alarmları yeniden kurar.",
                            icon = Icons.Default.DeleteSweep,
                            enabled = !alarmRefreshBusy,
                            titleSize = bodySize,
                            supportingSize = supportingSize,
                            onClick = { confirmClearAlarms = true }
                        )
                    }
                    item {
                        AccessibleSettingsButton(
                            text = "Alarm Durumunu Kontrol Et",
                            description = "Bildirim, kesin alarm ve tam ekran alarm izinlerini gösterir.",
                            icon = Icons.Default.NotificationsActive,
                            titleSize = bodySize,
                            supportingSize = supportingSize,
                            onClick = onAlarmStatus
                        )
                    }
                    item {
                        AccessibleSettingsButton(
                            text = "10 Saniye Sonra Test Alarmı",
                            description = "Alarm ekranını ve sesini kısa süre içinde denemenizi sağlar.",
                            icon = Icons.Default.Timer,
                            titleSize = bodySize,
                            supportingSize = supportingSize,
                            onClick = onTestAlarm
                        )
                    }
                    alarmRefreshResult?.let { result ->
                        item {
                            SettingsInformationCard(
                                title = "Son alarm yenileme sonucu",
                                body = "Aktif ilaç: ${result.medicationCount}\nGelecek alarm: ${result.futureAlarmCount}\nGeçmiş yanıtsız doz: ${result.catchUpAlarmCount}",
                                titleSize = bodySize,
                                bodySize = supportingSize,
                                cardBackground = cardBackground,
                                borderColor = cardBorder
                            )
                        }
                    }
                }
            }

            SettingsPage.APPOINTMENTS -> {
                var alarmsEnabled by remember {
                    mutableStateOf(AppointmentPreferences.alarmsEnabled(context))
                }
                var defaultReminderSet by remember {
                    mutableStateOf(
                        AppointmentPreferences.defaultRemindersCsv(context)
                            .split(",").mapNotNull { it.toIntOrNull() }.toSet()
                    )
                }
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        AccessibilitySwitchCard(
                            title = "Randevu alarmları",
                            description = "Kayıtlı tüm kullanıcıların yaklaşan randevu alarmlarını etkinleştirir.",
                            checked = alarmsEnabled,
                            titleSize = bodySize,
                            supportingSize = supportingSize,
                            cardBackground = cardBackground,
                            borderColor = cardBorder
                        ) {
                            alarmsEnabled = it
                            AppointmentPreferences.setAlarmsEnabled(context, it)
                            kotlinx.coroutines.CoroutineScope(Dispatchers.IO).launch {
                                dbAppointmentRefresh(context)
                            }
                        }
                    }
                    item {
                        SettingsInformationCard(
                            title = "Yeni randevular için varsayılan hatırlatmalar",
                            body = "Seçimler yeni oluşturulan randevulara uygulanır. Her randevuda ayrıca değiştirilebilir.",
                            titleSize = bodySize,
                            bodySize = supportingSize,
                            cardBackground = cardBackground,
                            borderColor = cardBorder
                        )
                    }
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth().sifahaneSoftBoundary(2.dp),
                            border = BorderStroke(1.dp, cardBorder),
                            colors = CardDefaults.cardColors(containerColor = cardBackground)
                        ) {
                            Column(Modifier.fillMaxWidth().padding(12.dp)) {
                                listOf(
                                    10080 to "1 hafta önce",
                                    4320 to "3 gün önce",
                                    1440 to "1 gün önce",
                                    180 to "3 saat önce",
                                    60 to "1 saat önce"
                                ).forEach { (minutes, label) ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth().clickable {
                                            defaultReminderSet = if (minutes in defaultReminderSet)
                                                defaultReminderSet - minutes else defaultReminderSet + minutes
                                            AppointmentPreferences.setDefaultRemindersCsv(
                                                context,
                                                defaultReminderSet.sortedDescending().joinToString(",")
                                            )
                                        },
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Checkbox(
                                            checked = minutes in defaultReminderSet,
                                            onCheckedChange = {
                                                defaultReminderSet = if (minutes in defaultReminderSet)
                                                    defaultReminderSet - minutes else defaultReminderSet + minutes
                                                AppointmentPreferences.setDefaultRemindersCsv(
                                                    context,
                                                    defaultReminderSet.sortedDescending().joinToString(",")
                                                )
                                            }
                                        )
                                        Text(label, fontSize = bodySize)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            SettingsPage.BACKUP -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        AccessibleSettingsButton(
                            text = "Kişi Verilerini Dışarı Aktar",
                            description = "Aktif kişiye ait verileri ve fotoğrafları parola korumalı .sifbackup dosyasına kaydeder.",
                            icon = Icons.Default.FileUpload,
                            enabled = hasActiveProfile,
                            titleSize = bodySize,
                            supportingSize = supportingSize,
                            onClick = onExportPersonData
                        )
                    }
                    item {
                        AccessibleSettingsButton(
                            text = "Kişi Verilerini İçeri Aktar",
                            description = if (canManageSecurity)
                                "Şifreli .sifbackup veya uyumlu eski ZIP yedeğini doğrulayarak içeri aktarır."
                            else
                                "Bu işlem yalnızca yöneticiler tarafından yapılabilir.",
                            icon = Icons.Default.FileDownload,
                            enabled = canManageSecurity,
                            titleSize = bodySize,
                            supportingSize = supportingSize,
                            onClick = onImportPersonData
                        )
                    }
                    item {
                        SettingsInformationCard(
                            title = "Yedekleme bilgisi",
                            body = buildString {
                                append("Yedekler fotoğrafları da içeren standart ZIP dosyalarıdır.")
                                if (backupStatusText.isNotBlank()) {
                                    append("\n\n")
                                    append(backupStatusText)
                                }
                            },
                            titleSize = bodySize,
                            bodySize = supportingSize,
                            cardBackground = cardBackground,
                            borderColor = cardBorder
                        )
                    }
                }
            }

            SettingsPage.SECURITY -> {
                val active = profiles.firstOrNull { it.id == activeProfileId }
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        SettingsInformationCard(
                            title = "Otomatik ekran kilidi",
                            body = "Uygulamada işlem yapılmadığında aktif kullanıcı yeniden desen kilidine alınır.",
                            titleSize = bodySize,
                            bodySize = supportingSize,
                            cardBackground = cardBackground,
                            borderColor = cardBorder
                        )
                    }
                    item {
                        val lockOptions = listOf(
                            30_000L to "30 saniye", 60_000L to "1 dakika",
                            120_000L to "2 dakika", 300_000L to "5 dakika",
                            600_000L to "10 dakika", 1_800_000L to "30 dakika",
                            0L to "Hiçbir zaman"
                        )
                        ExposedDropdownMenuBox(
                            expanded = lockTimeoutMenuExpanded,
                            onExpandedChange = { lockTimeoutMenuExpanded = !lockTimeoutMenuExpanded },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = lockOptions.firstOrNull { it.first == lockTimeout }?.second ?: "2 dakika",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Hareketsizlik sonrası kilitle") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(lockTimeoutMenuExpanded) },
                                modifier = Modifier.menuAnchor().fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = lockTimeoutMenuExpanded,
                                onDismissRequest = { lockTimeoutMenuExpanded = false }
                            ) {
                                lockOptions.forEach { (value, label) ->
                                    DropdownMenuItem(
                                        text = { Text(label) },
                                        onClick = {
                                            lockTimeout = value
                                            AppPreferences.saveLockTimeoutMillis(context, value)
                                            lockTimeoutMenuExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                    item {
                        SettingsInformationCard(
                            title = "Aktif kullanıcı rolü",
                            body = if (active?.role == UserRoles.ADMIN) "Yönetici" else "Standart Kullanıcı",
                            titleSize = bodySize,
                            bodySize = bodySize,
                            cardBackground = cardBackground,
                            borderColor = cardBorder
                        )
                    }

                    if (canManageSecurity) {
                        items(profiles, key = { it.id }) { profile ->
                            Card(
                                modifier = Modifier.fillMaxWidth().sifahaneSoftBoundary(2.dp),
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.dp, cardBorder),
                                colors = CardDefaults.cardColors(containerColor = cardBackground)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text(
                                        "${profile.name} ${profile.surname}".trim(),
                                        modifier = Modifier.fillMaxWidth(),
                                        fontSize = titleSize,
                                        fontWeight = FontWeight.Bold,
                                        color = LogoColorDark
                                    )
                                    Text(
                                        if (profile.role == UserRoles.ADMIN) "Yönetici" else "Standart Kullanıcı",
                                        modifier = Modifier.fillMaxWidth(),
                                        fontSize = supportingSize,
                                        color = LogoColorDark
                                    )
                                    OutlinedButton(
                                        onClick = { onSetRole(profile.id, profile.role != UserRoles.ADMIN) },
                                        modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp)
                                    ) {
                                        Text(
                                            if (profile.role == UserRoles.ADMIN)
                                                "Standart Kullanıcı Yap"
                                            else
                                                "Yönetici Olarak Ata",
                                            fontSize = bodySize
                                        )
                                    }
                                    if (profile.role == UserRoles.ADMIN) {
                                        OutlinedButton(
                                            onClick = {
                                                pinTarget = profile
                                                newAdminPin = ""
                                                confirmAdminPin = ""
                                                pinError = null
                                            },
                                            modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp)
                                        ) {
                                            Text(
                                                if (profile.adminPinHash.isNullOrBlank())
                                                    "Yönetici Şifresi Belirle"
                                                else
                                                    "Yönetici Şifresini Değiştir",
                                                fontSize = bodySize
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        item {
                            Text(
                                "Son yönetici kaldırılamaz. Yönetici şifreleri benzersiz salt ve yavaş PBKDF2 özetiyle saklanır.",
                                modifier = Modifier.fillMaxWidth().padding(6.dp),
                                textAlign = TextAlign.Center,
                                color = LogoColorDark,
                                fontSize = supportingSize
                            )
                        }
                    } else {
                        item {
                            SettingsInformationCard(
                                title = "Yetki gerekli",
                                body = "Rol ve yönetici hesaplarını değiştirmek için yönetici hesabıyla oturum açın.",
                                titleSize = bodySize,
                                bodySize = supportingSize,
                                cardBackground = cardBackground,
                                borderColor = cardBorder
                            )
                        }
                    }
                }
            }

            SettingsPage.PERMISSIONS -> {
                val ignoredRefresh = permissionRefresh
                val notificationGranted = Build.VERSION.SDK_INT < 33 ||
                    androidx.core.content.ContextCompat.checkSelfPermission(
                        context, android.Manifest.permission.POST_NOTIFICATIONS
                    ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                val cameraGranted = androidx.core.content.ContextCompat.checkSelfPermission(
                    context, android.Manifest.permission.CAMERA
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                val alarmManager = context.getSystemService(AlarmManager::class.java)
                val exactGranted = Build.VERSION.SDK_INT < 31 || alarmManager.canScheduleExactAlarms()
                val notificationManager = context.getSystemService(NotificationManager::class.java)
                val fullScreenGranted = Build.VERSION.SDK_INT < 34 || notificationManager.canUseFullScreenIntent()
                val powerManager = context.getSystemService(android.os.PowerManager::class.java)
                val batteryGranted = powerManager.isIgnoringBatteryOptimizations(context.packageName)
                fun openAppSettings() {
                    context.startActivity(Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.parse("package:${context.packageName}")
                    })
                }
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        SettingsInformationCard(
                            title = "Android izinleri merkezi",
                            body = "Android bazı özel izinleri uygulama içinden doğrudan değiştirmeye izin vermez. Bu izinlerde ilgili güvenli sistem ekranı açılır.",
                            titleSize = bodySize, bodySize = supportingSize,
                            cardBackground = cardBackground, borderColor = cardBorder
                        )
                    }
                    item { PermissionStatusCard("Bildirimler", "Alarm ve hatırlatma bildirimlerini gösterir.", notificationGranted, cardBackground, cardBorder) {
                        if (Build.VERSION.SDK_INT >= 33) notificationPermissionRequest.launch(android.Manifest.permission.POST_NOTIFICATIONS) else openAppSettings()
                    } }
                    item { PermissionStatusCard("Kesin alarm", "İlaç alarmını belirlenen dakikada çalıştırır.", exactGranted, cardBackground, cardBorder) {
                        if (Build.VERSION.SDK_INT >= 31) context.startActivity(Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM, Uri.parse("package:${context.packageName}")))
                    } }
                    item { PermissionStatusCard("Tam ekran alarm", "Kilitli veya açık ekranda alarm kartını gösterebilir.", fullScreenGranted, cardBackground, cardBorder) {
                        if (Build.VERSION.SDK_INT >= 34) context.startActivity(Intent(android.provider.Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT, Uri.parse("package:${context.packageName}")))
                    } }
                    item { PermissionStatusCard("Pil optimizasyonu muafiyeti", "Üretici güç yönetiminin alarmı geciktirmesini önlemeye yardımcı olur.", batteryGranted, cardBackground, cardBorder) {
                        context.startActivity(Intent(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, Uri.parse("package:${context.packageName}")))
                    } }
                    item { PermissionStatusCard("Kamera", "İlaç barkodu taraması için kullanılır.", cameraGranted, cardBackground, cardBorder) {
                        cameraPermissionRequest.launch(android.Manifest.permission.CAMERA)
                    } }
                    item {
                        Button(onClick = {
                            if (!notificationGranted && Build.VERSION.SDK_INT >= 33) notificationPermissionRequest.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                            else openAppSettings()
                        }, modifier = Modifier.fillMaxWidth()) { Text("TÜMÜNÜ AÇ / EKSİKLERİ YÖNET") }
                    }
                    item {
                        OutlinedButton(onClick = { openAppSettings() }, modifier = Modifier.fillMaxWidth()) {
                            Text("TÜMÜNÜ KAPATMAK İÇİN SİSTEM AYARLARINA GİT")
                        }
                        Text("Android güvenliği nedeniyle tüm izinler uygulama içinden doğrudan kapatılamaz.", fontSize = supportingSize)
                    }
                }
            }

            SettingsPage.ACCESSIBILITY -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        AccessibilitySwitchCard(
                            title = "Büyük ayar metinleri",
                            description = "Ayarlar bölümündeki başlık ve açıklama yazılarını büyütür.",
                            checked = largeSettingsText,
                            titleSize = bodySize,
                            supportingSize = supportingSize,
                            cardBackground = cardBackground,
                            borderColor = cardBorder
                        ) {
                            largeSettingsText = it
                            accessibilityPrefs.edit().putBoolean("large_settings_text", it).apply()
                        }
                    }
                    item {
                        AccessibilitySwitchCard(
                            title = "Yüksek kontrastlı ayarlar",
                            description = "Ayar kartlarının zemin ve kenar ayrımını belirginleştirir.",
                            checked = highContrastSettings,
                            titleSize = bodySize,
                            supportingSize = supportingSize,
                            cardBackground = cardBackground,
                            borderColor = cardBorder
                        ) {
                            highContrastSettings = it
                            accessibilityPrefs.edit().putBoolean("high_contrast_settings", it).apply()
                        }
                    }
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth().sifahaneSoftBoundary(2.dp),
                            border = BorderStroke(1.dp, cardBorder),
                            colors = CardDefaults.cardColors(containerColor = cardBackground)
                        ) {
                            Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text("Alt menü buton boyutu", fontSize = bodySize, fontWeight = FontWeight.Bold, color = LogoColorDark)
                                Text("Alt menü simgeleri, yazıları ve menü yüksekliği için standart boyutu seçin.",
                                    fontSize = supportingSize, color = LogoColorDark.copy(alpha = 0.82f))
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    listOf(BottomMenuSize.SMALL to "Küçük", BottomMenuSize.MEDIUM to "Orta", BottomMenuSize.LARGE to "Büyük")
                                        .forEach { (size, title) ->
                                            if (bottomMenuSize == size) Button(
                                                onClick = { onBottomMenuSizeChange(size) },
                                                modifier = Modifier.weight(1f).heightIn(min = 48.dp)
                                            ) { Text(title, maxLines = 1) }
                                            else OutlinedButton(
                                                onClick = { onBottomMenuSizeChange(size) },
                                                modifier = Modifier.weight(1f).heightIn(min = 48.dp)
                                            ) { Text(title, maxLines = 1) }
                                        }
                                }
                                OutlinedButton(onClick = onResetBottomMenuOrder,
                                    modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                                    Icon(Icons.Default.Restore, null)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Alt menüyü varsayılan sıraya döndür")
                                }
                            }
                        }
                    }
                    item {
                        SettingsInformationCard(
                            title = "Erişilebilir tasarım",
                            body = "Ayar kategorileri ayrı ekranlara bölünmüştür. Alt menü yatay kaydırılabilir, alt menüdeki Menü düğmesinden sürükle-bırak düzenlenebilir ve Küçük, Orta veya Büyük boyutta kullanılabilir. İşlem alanları en az 48 dp olacak şekilde korunur.",
                            titleSize = bodySize,
                            bodySize = supportingSize,
                            cardBackground = cardBackground,
                            borderColor = cardBorder
                        )
                    }
                }
            }

            SettingsPage.THEME -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        SettingsInformationCard(
                            title = "Şifahane varsayılan teması",
                            body = "Beyaz zemin, vantablack metin, tek logo yeşili ve yalnızca uyarılarda Türk kırmızısı kullanılır. Sıfırla düğmesi bu güvenli temaya döner.",
                            titleSize = bodySize, bodySize = supportingSize,
                            cardBackground = cardBackground, borderColor = cardBorder
                        )
                    }
                    item {
                        val colorOptions = listOf(
                            "default" to "Varsayılan Şifahane yeşili",
                            "soft" to "Yumuşak beyaz",
                            "turkish_blue" to "Türk mavisi",
                            "turkish_red" to "Türk kırmızısı"
                        )
                        ExposedDropdownMenuBox(
                            expanded = themePresetExpanded,
                            onExpandedChange = { themePresetExpanded = !themePresetExpanded }
                        ) {
                            OutlinedTextField(
                                value = colorOptions.firstOrNull { it.first == themePreset }?.second ?: colorOptions.first().second,
                                onValueChange = {}, readOnly = true,
                                label = { Text("Renk görünümü") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(themePresetExpanded) },
                                modifier = Modifier.menuAnchor().fillMaxWidth()
                            )
                            ExposedDropdownMenu(themePresetExpanded, { themePresetExpanded = false }) {
                                colorOptions.forEach { (value, label) -> DropdownMenuItem(
                                    text = { Text(label) },
                                    onClick = { themePreset = value; themePresetExpanded = false }
                                ) }
                            }
                        }
                    }
                    item {
                        val fontOptions = listOf(
                            "roboto" to "Roboto", "noto_sans" to "Noto Sans",
                            "atkinson" to "Atkinson Hyperlegible", "lexend" to "Lexend",
                            "noto_serif" to "Noto Serif"
                        )
                        ExposedDropdownMenuBox(
                            expanded = themeFontExpanded,
                            onExpandedChange = { themeFontExpanded = !themeFontExpanded }
                        ) {
                            OutlinedTextField(
                                value = fontOptions.firstOrNull { it.first == themeFont }?.second ?: fontOptions.first().second,
                                onValueChange = {}, readOnly = true,
                                label = { Text("Yazı tipi") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(themeFontExpanded) },
                                modifier = Modifier.menuAnchor().fillMaxWidth()
                            )
                            ExposedDropdownMenu(themeFontExpanded, { themeFontExpanded = false }) {
                                fontOptions.forEach { (value, label) -> DropdownMenuItem(
                                    text = { Text(label) },
                                    onClick = { themeFont = value; themeFontExpanded = false }
                                ) }
                            }
                        }
                    }
                    item {
                        SettingsSectionCard("Yazı büyüklüğü: %${(themeScale * 100).roundToInt()}") {
                            Slider(value = themeScale, onValueChange = { themeScale = it }, valueRange = .85f..1.35f, steps = 4)
                        }
                    }
                    item {
                        SettingsSectionCard("Vurgu opaklığı: %${(themeOpacity * 100).roundToInt()}") {
                            Slider(value = themeOpacity, onValueChange = { themeOpacity = it }, valueRange = .35f..1f)
                        }
                    }
                    item {
                        Button(onClick = {
                            ThemePreferences.save(context, ThemeConfiguration(themePreset, themeFont, themeScale, themeOpacity))
                            (context as? Activity)?.recreate()
                        }, modifier = Modifier.fillMaxWidth()) { Text("TEMAYI UYGULA") }
                    }
                    item {
                        OutlinedButton(onClick = {
                            ThemePreferences.reset(context)
                            (context as? Activity)?.recreate()
                        }, modifier = Modifier.fillMaxWidth()) { Text("VARSAYILAN TEMAYA DÖN") }
                    }
                }
            }

            SettingsPage.DATABASE -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        SettingsInformationCard(
                            title = "Room veritabanı sürümü",
                            body = databaseVersion.toString(),
                            titleSize = bodySize,
                            bodySize = titleSize,
                            cardBackground = cardBackground,
                            borderColor = cardBorder
                        )
                    }
                    item {
                        SettingsInformationCard(
                            title = "Veri geçiş durumu",
                            body = "7 → 8 ve 8 → 9 migration uygulanır. Randevular tablosu eklenirken mevcut kullanıcı, ilaç, ölçüm, rapor ve alarm kayıtları korunur.",
                            titleSize = bodySize,
                            bodySize = supportingSize,
                            cardBackground = cardBackground,
                            borderColor = cardBorder
                        )
                    }
                }
            }

            SettingsPage.HELP -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        OutlinedTextField(
                            value = query,
                            onValueChange = { query = it },
                            modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
                            placeholder = { Text("Yardım konularında ara", fontSize = bodySize) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            singleLine = true,
                            shape = StandardFieldShape,
                            colors = standardFieldColors()
                        )
                    }
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth().sifahaneSoftBoundary(2.dp),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, cardBorder),
                            colors = CardDefaults.cardColors(containerColor = cardBackground)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    "Kapsamlı Kullanıcı Kılavuzu",
                                    color = LogoColorDark,
                                    fontSize = bodySize,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "Uygulamanın tüm temel işlevlerini tek belgede okuyun veya kişisel veri içermeyen erişilebilir Word belgesini bir yapay zekâ uygulamasıyla paylaşın.",
                                    color = LogoColorDark,
                                    fontSize = supportingSize
                                )
                                OutlinedButton(
                                    onClick = { showComprehensiveGuide = true },
                                    modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)
                                ) {
                                    Icon(Icons.Default.MenuBook, null)
                                    Spacer(Modifier.width(8.dp))
                                    Text("KILAVUZU OKU VE ARA", fontSize = bodySize)
                                }
                                Button(
                                    onClick = {
                                        val plainGuide = comprehensiveUserGuideSections()
                                            .joinToString("\n\n") { (title, body) -> "$title\n$body" }
                                        runCatching {
                                            val guide = createComprehensiveUserGuide(context)
                                            val uri = FileProvider.getUriForFile(
                                                context, "${context.packageName}.fileprovider", guide
                                            )
                                            context.startActivity(Intent.createChooser(
                                                Intent(Intent.ACTION_SEND).apply {
                                                    type = "*/*"
                                                    putExtra(Intent.EXTRA_STREAM, uri)
                                                    putExtra(Intent.EXTRA_SUBJECT, "Şifahane Kapsamlı Kullanıcı Kılavuzu")
                                                    putExtra(Intent.EXTRA_TEXT, "Bu kılavuzu esas alarak Şifahane uygulamasıyla ilgili sorularımı Türkçe cevapla.\n\n$plainGuide")
                                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                                    clipData = android.content.ClipData.newUri(
                                                        context.contentResolver, guide.name, uri
                                                    )
                                                },
                                                "Kılavuzu yapay zekâ ile paylaş"
                                            ))
                                        }.onFailure {
                                            context.startActivity(Intent.createChooser(
                                                Intent(Intent.ACTION_SEND).apply {
                                                    type = "text/plain"
                                                    putExtra(Intent.EXTRA_SUBJECT, "Şifahane Kapsamlı Kullanıcı Kılavuzu")
                                                    putExtra(Intent.EXTRA_TEXT, plainGuide)
                                                }, "Kılavuzu yapay zekâ ile paylaş"
                                            ))
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)
                                ) {
                                    Icon(Icons.Default.Share, null)
                                    Spacer(Modifier.width(8.dp))
                                    Text("YAPAY ZEKÂYA SOR", fontSize = bodySize)
                                }
                            }
                        }
                    }
                    items(filteredHelp, key = { it.title }) { topic ->
                        val expanded = expandedHelpTitle == topic.title
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .sifahaneSoftBoundary(2.dp)
                                .clickable {
                                    expandedHelpTitle = if (expanded) null else topic.title
                                },
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, cardBorder),
                            colors = CardDefaults.cardColors(containerColor = cardBackground)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        topic.title,
                                        modifier = Modifier.weight(1f),
                                        color = LogoColorDark,
                                        fontSize = bodySize,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Icon(
                                        if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                        contentDescription = if (expanded) "Açıklamayı kapat" else "Açıklamayı aç",
                                        tint = LogoColorDark
                                    )
                                }
                                if (expanded) {
                                    Text(
                                        topic.body,
                                        modifier = Modifier.fillMaxWidth(),
                                        color = LogoColorDark,
                                        fontSize = supportingSize
                                    )
                                    if (topic.target != null && topic.targetLabel != null) {
                                        OutlinedButton(
                                            onClick = { onNavigate(topic.target) },
                                            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)
                                        ) {
                                            Text(topic.targetLabel, fontSize = bodySize)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (filteredHelp.isEmpty()) {
                        item {
                            Text(
                                "Aramanızla eşleşen yardım konusu bulunamadı.",
                                modifier = Modifier.fillMaxWidth().padding(18.dp),
                                textAlign = TextAlign.Center,
                                color = LogoColorDark,
                                fontSize = bodySize
                            )
                        }
                    }
                }
            }

            SettingsPage.ABOUT -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        SettingsInformationCard(
                            title = "Şifahane",
                            body = "Sağlık Takip Uygulaması\nVersion Name: ${BuildConfig.VERSION_NAME}\nVersion Code: ${BuildConfig.VERSION_CODE}\nDerleme türü: ${BuildConfig.BUILD_TYPE}\nDerleme zamanı: ${if (BuildConfig.BUILD_TIME_MILLIS == 0L) "Tekrarlanabilir derleme" else SimpleDateFormat("dd.MM.yyyy HH:mm", Locale("tr", "TR")).format(Date(BuildConfig.BUILD_TIME_MILLIS))}",
                            titleSize = titleSize,
                            bodySize = bodySize,
                            cardBackground = cardBackground,
                            borderColor = cardBorder
                        )
                    }
                    item {
                        OutlinedButton(
                            onClick = {
                                val buildTime = if (BuildConfig.BUILD_TIME_MILLIS == 0L) "Tekrarlanabilir derleme" else SimpleDateFormat("dd.MM.yyyy HH:mm", Locale("tr", "TR")).format(Date(BuildConfig.BUILD_TIME_MILLIS))
                                val info = "Şifahane ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE}) - ${BuildConfig.BUILD_TYPE} - $buildTime"
                                val clipboard = context.getSystemService(android.content.ClipboardManager::class.java)
                                clipboard.setPrimaryClip(android.content.ClipData.newPlainText("Şifahane sürüm bilgisi", info))
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("SÜRÜM BİLGİSİNİ KOPYALA") }
                    }
                    item {
                        SettingsInformationCard(
                            title = "Bu sürümde",
                            body = "Güvenlik temeli yenilendi: varsayılan yönetici şifresi kaldırıldı, PIN ve desenler salt’lı PBKDF2 ile korunmaya başladı, kalıcı artan deneme sınırı eklendi, hassas ekranlarda ekran görüntüsü engellendi ve cihaz yedeklemesi kapatıldı. Room veritabanı SQLCipher ile şifrelenir; mevcut düz veriler ilk açılışta kayıp olmadan dönüştürülür.",
                            titleSize = bodySize,
                            bodySize = supportingSize,
                            cardBackground = cardBackground,
                            borderColor = cardBorder
                        )
                    }
                }
            }
        }
    }

    pinTarget?.let { target ->
        AlertDialog(
            onDismissRequest = { pinTarget = null },
            title = {
                Text(
                    "${target.name} için yönetici şifresi",
                    color = LogoColorDark,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newAdminPin,
                        onValueChange = {
                            if (it.length <= 12 && it.all(Char::isDigit)) newAdminPin = it
                        },
                        label = { Text("Yeni şifre") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = confirmAdminPin,
                        onValueChange = {
                            if (it.length <= 12 && it.all(Char::isDigit)) confirmAdminPin = it
                        },
                        label = { Text("Yeni şifreyi tekrar girin") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    pinError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                }
            },
            confirmButton = {
                Button(onClick = {
                    pinError = when {
                        newAdminPin.length !in 4..12 -> "Şifre 4–12 haneli olmalıdır."
                        newAdminPin != confirmAdminPin -> "Şifreler eşleşmiyor."
                        else -> null
                    }
                    if (pinError == null) {
                        onSetAdminPin(target.id, newAdminPin)
                        pinTarget = null
                    }
                }) { Text("Kaydet") }
            },
            dismissButton = {
                TextButton(onClick = { pinTarget = null }) { Text("İptal") }
            }
        )
    }

    if (confirmClearAlarms) {
        AlertDialog(
            onDismissRequest = { confirmClearAlarms = false },
            title = { Text("Eski alarm isteklerini temizle") },
            text = { Text("Şifahane'ye ait kayıtlı eski alarm ve bildirim istekleri temizlenecek, ardından güncel ilaç alarmları yeniden kurulacak. Devam edilsin mi?") },
            confirmButton = {
                Button(onClick = {
                    confirmClearAlarms = false
                    onClearStaleAlarms()
                }) { Text("TEMİZLE VE YENİLE") }
            },
            dismissButton = { TextButton(onClick = { confirmClearAlarms = false }) { Text("İPTAL") } }
        )
    }
}

@Composable
private fun SettingsPageHeader(
    title: String,
    showBack: Boolean,
    titleSize: androidx.compose.ui.unit.TextUnit,
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().heightIn(min = 64.dp).padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showBack) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Ayarlar ana sayfasına dön")
            }
        } else {
            Spacer(Modifier.size(48.dp))
        }
        Text(
            title,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            color = LogoColorDark,
            fontSize = titleSize,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineSmall.copy(shadow = LogoTextShadow)
        )
        Spacer(Modifier.size(48.dp))
    }
}

@Composable
private fun SettingsCategoryCard(
    title: String,
    description: String,
    icon: ImageVector,
    titleSize: androidx.compose.ui.unit.TextUnit,
    bodySize: androidx.compose.ui.unit.TextUnit,
    cardBackground: Color,
    borderColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().heightIn(min = 82.dp).sifahaneSoftBoundary(2.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, borderColor),
        colors = CardDefaults.cardColors(containerColor = cardBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(0.7.dp, Color(0xFF00AEEF).copy(alpha = 0.50f), RoundedCornerShape(18.dp))
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = LogoColor,
                modifier = Modifier.size(32.dp)
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                AutoSizeSettingsTitle(
                    title = title,
                    maximumSize = titleSize,
                    textAlign = TextAlign.Start
                )
                Text(
                    description,
                    color = LogoColorDark,
                    fontSize = bodySize
                )
            }
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = "Bölümü aç",
                tint = LogoColorDark,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun AccessibleSettingsButton(
    text: String,
    description: String,
    icon: ImageVector,
    enabled: Boolean = true,
    titleSize: androidx.compose.ui.unit.TextUnit,
    supportingSize: androidx.compose.ui.unit.TextUnit,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth().heightIn(min = 72.dp),
        shape = RoundedCornerShape(16.dp),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp)
    ) {
        OutlinedLogoIcon(icon, contentDescription = null, size = 28.dp)
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text, fontSize = titleSize, fontWeight = FontWeight.Bold)
            Text(description, fontSize = supportingSize)
        }
    }
}

@Composable
private fun SettingsInformationCard(
    title: String,
    body: String,
    titleSize: androidx.compose.ui.unit.TextUnit,
    bodySize: androidx.compose.ui.unit.TextUnit,
    cardBackground: Color,
    borderColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth().sifahaneSoftBoundary(2.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, borderColor),
        colors = CardDefaults.cardColors(containerColor = cardBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(0.7.dp, Color(0xFF00AEEF).copy(alpha = 0.50f), RoundedCornerShape(16.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                title,
                modifier = Modifier.fillMaxWidth(),
                color = LogoColorDark,
                fontSize = titleSize,
                fontWeight = FontWeight.Bold
            )
            Text(
                body,
                modifier = Modifier.fillMaxWidth(),
                color = LogoColorDark,
                fontSize = bodySize
            )
        }
    }
}

@Composable
private fun PermissionStatusCard(
    title: String,
    description: String,
    granted: Boolean,
    cardBackground: Color,
    borderColor: Color,
    onManage: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().sifahaneSoftBoundary(2.dp),
        border = BorderStroke(1.dp, if (granted) LogoColor.copy(alpha = 0.70f) else Color(0xFFE30A17).copy(alpha = 0.65f)),
        colors = CardDefaults.cardColors(containerColor = cardBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(0.7.dp, Color(0xFF00AEEF).copy(alpha = 0.50f), RoundedCornerShape(16.dp))
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                if (granted) Icons.Default.CheckCircle else Icons.Default.ErrorOutline,
                contentDescription = null,
                tint = if (granted) LogoColorDark else Color(0xFFE30A17)
            )
            Column(Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, color = LogoColorDark)
                Text(description, style = MaterialTheme.typography.bodySmall)
                Text(if (granted) "Durum: Açık" else "Durum: Kapalı veya kullanıcı işlemi gerekli", style = MaterialTheme.typography.labelMedium)
            }
            TextButton(onClick = onManage) { Text("YÖNET") }
        }
    }
}

@Composable
private fun AccessibilitySwitchCard(
    title: String,
    description: String,
    checked: Boolean,
    titleSize: androidx.compose.ui.unit.TextUnit,
    supportingSize: androidx.compose.ui.unit.TextUnit,
    cardBackground: Color,
    borderColor: Color,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().sifahaneSoftBoundary(2.dp).clickable { onCheckedChange(!checked) },
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, borderColor),
        colors = CardDefaults.cardColors(containerColor = cardBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(0.7.dp, Color(0xFF00AEEF).copy(alpha = 0.50f), RoundedCornerShape(16.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    title,
                    color = LogoColorDark,
                    fontSize = titleSize,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    description,
                    color = LogoColorDark,
                    fontSize = supportingSize
                )
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}

@Composable
private fun SettingsSectionCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().sifahaneSoftBoundary(2.dp),
        shape = StandardFieldShape,
        border = BorderStroke(1.5.dp, LogoColor),
        colors = CardDefaults.cardColors(containerColor = Vantablack05)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(0.7.dp, Color(0xFF00AEEF).copy(alpha = 0.50f), StandardFieldShape)
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AutoSizeSettingsTitle(title)
            content()
        }
    }
}

@Composable
private fun AutoSizeSettingsTitle(
    title: String,
    maximumSize: androidx.compose.ui.unit.TextUnit = 18.sp,
    textAlign: TextAlign = TextAlign.Center
) {
    var size by remember(title, maximumSize) { mutableStateOf(maximumSize) }
    Text(
        text = title,
        modifier = Modifier.fillMaxWidth(),
        textAlign = textAlign,
        color = LogoColorDark,
        fontSize = size,
        fontWeight = FontWeight.Bold,
        maxLines = 1,
        softWrap = false,
        overflow = TextOverflow.Clip,
        style = MaterialTheme.typography.titleMedium.copy(shadow = LogoTextShadow),
        onTextLayout = { result ->
            if (result.hasVisualOverflow && size > 11.sp) size = (size.value - 1f).sp
        }
    )
}

@Composable
private fun SettingsActionButton(
    text: String,
    icon: ImageVector,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = StandardFieldShape,
        border = BorderStroke(1.5.dp, LogoColor),
        colors = profileOutlinedButtonColors()
    ) {
        Icon(icon, contentDescription = null)
        Spacer(Modifier.width(6.dp))
        Text(
            text,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            fontSize = 13.sp,
            color = LogoColorDark
        )
    }
}


@Composable
private fun MedicineScreen(
    active: List<Medication>,
    archive: List<Medication>,
    reportGroups: List<ReportGroup>,
    modifier: Modifier,
    onEditMedication: (Medication) -> Unit,
    onNewReportGroup: () -> Unit,
    onEditReportGroup: (ReportGroup) -> Unit,
    selectedTab: Int,
    onTabChange: (Int) -> Unit,
    onAdd: () -> Unit,
    onDeleteReportGroup: (ReportGroup) -> Unit
) {
    val tab = selectedTab
    var deleteGroup by remember { mutableStateOf<ReportGroup?>(null) }

    Column(modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.weight(1f)) {
                TabRow(
                    selectedTabIndex = tab,
                    containerColor = Color.Transparent,
                    contentColor = LogoColor,
                    indicator = { tabPositions ->
                        TabRowDefaults.PrimaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[tab]),
                            color = LogoColor
                        )
                    }
                ) {
                    listOf("Aktif İlaçlar", "İlaç Arşivi", "Rapor Grupları")
                        .forEachIndexed { index, label ->
                            Tab(
                                selected = tab == index,
                                onClick = { onTabChange(index) },
                                text = {
                                    Text(
                                        titleCaseTr(label),
                                        color = LogoColorDark,
                                        maxLines = 1,
                                        fontSize = 11.sp,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            shadow = LogoTextShadow
                                        )
                                    )
                                }
                            )
                        }
                }
            }
            IconButton(
                onClick = onAdd,
                modifier = Modifier
                    .padding(end = 6.dp)
                    .size(42.dp)
                    .background(Vantablack05, CircleShape)
            ) {
                OutlinedLogoIcon(
                    imageVector = Icons.Default.AddCircle,
                    contentDescription =
                        if (tab == 2) "Rapor Grubu Ekle" else "İlaç Ekle",
                    size = 28.dp
                )
            }
        }

        if (tab < 2) {
            val list = if (tab == 0) active else archive
            LazyColumn(
                Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                items(list, key = { it.id }) { med ->
                    SifahaneCard(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onEditMedication(med) }
                    ) {
                        Column {
                            if (!med.photoUri.isNullOrBlank()) {
                                Image(
                                    rememberAsyncImagePainter(med.photoUri),
                                    null,
                                    Modifier.fillMaxWidth().height(240.dp),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            HorizontalDivider(
                                thickness = 2.dp,
                                color = LogoColor.copy(alpha = 0.85f)
                            )
                            Column(Modifier.padding(16.dp)) {
                                Text(
                                    titleCaseTr(med.name),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                if (med.purpose.isNotBlank()) Text(med.purpose)
                                Text("${med.dose} • ${med.timesCsv}")
                                Text("Stok: ${med.stock} • Kritik Sınır: ${med.lowStockLimit}")
                                if (med.doctorName.isNotBlank()) {
                                    Text("Doktor: ${med.doctorName} – ${med.doctorBranch}")
                                }
                                if (med.isReported) {
                                    val groupName = reportGroups
                                        .firstOrNull { it.id == med.reportGroupId }?.name
                                    Text(
                                        "Rapor: ${med.reportStartDate ?: "-"} – ${med.reportEndDate ?: "-"}" +
                                            if (groupName != null) " • ${titleCaseTr(groupName)}" else ""
                                    )
                                }
                            }
                        }
                    }
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        thickness = 3.dp,
                        color = LogoColor
                    )
                }
            }
        } else {
            Column(Modifier.fillMaxSize()) {
                if (reportGroups.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            "Henüz Rapor Grubu Oluşturulmadı.",
                            color = LogoColorDark,
                            style = MaterialTheme.typography.titleMedium.copy(
                                shadow = LogoTextShadow
                            )
                        )
                    }
                } else {
                    LazyColumn(
                        Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(reportGroups, key = { it.id }) { group ->
                            val linked = (active + archive).count {
                                it.reportGroupId == group.id
                            }
                            SifahaneCard(modifier = Modifier.fillMaxWidth()) {
                                Column(Modifier.padding(16.dp)) {
                                    Text(
                                        titleCaseTr(group.name),
                                        color = LogoColorDark,
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            shadow = LogoTextShadow
                                        )
                                    )
                                    Text(
                                        "Rapor Başlangıç: ${group.startDate}",
                                        color = LogoColorDark
                                    )
                                    Text(
                                        "Rapor Bitiş: ${group.endDate}",
                                        color = LogoColorDark
                                    )
                                    Text(
                                        "Uyarı: ${group.warningDays} Gün Önce • Bağlı İlaç: $linked",
                                        color = LogoColorDark
                                    )
                                    Row(
                                        Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        TextButton(
                                            onClick = { onEditReportGroup(group) },
                                            colors = ButtonDefaults.textButtonColors(
                                                containerColor = Vantablack10,
                                                contentColor = LogoColorDark
                                            )
                                        ) {
                                            Icon(Icons.Default.Edit, null)
                                            Spacer(Modifier.width(4.dp))
                                            Text("Düzenle")
                                        }
                                        TextButton(
                                            onClick = { deleteGroup = group },
                                            colors = ButtonDefaults.textButtonColors(
                                                containerColor = Vantablack10,
                                                contentColor = MaterialTheme.colorScheme.error
                                            )
                                        ) {
                                            Icon(Icons.Default.Delete, null)
                                            Spacer(Modifier.width(4.dp))
                                            Text("Sil")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    deleteGroup?.let { group ->
        AlertDialog(
            onDismissRequest = { deleteGroup = null },
            title = { Text("Rapor Grubunu Sil") },
            text = {
                Text(
                    "${titleCaseTr(group.name)} silinecek. Bu gruba bağlı ilaçlar silinmeyecek; yalnızca grup bağlantıları kaldırılacak."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteReportGroup(group)
                        deleteGroup = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LogoColor.copy(alpha = 0.25f),
                        contentColor = Color(0xFF123A37)
                    )
                ) {
                    Text("Evet, Sil")
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteGroup = null }) {
                    Text("İptal")
                }
            }
        )
    }
}

@Composable
private fun ReportGroupEditorDialog(
    existing: ReportGroup?,
    profileId: Long,
    onDismiss: () -> Unit,
    onSave: (ReportGroup) -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val today = remember {
        SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    }
    var name by remember(existing?.id) {
        mutableStateOf(existing?.name ?: "")
    }
    var startDate by remember(existing?.id) {
        mutableStateOf(existing?.startDate ?: today)
    }
    var endDate by remember(existing?.id) {
        mutableStateOf(existing?.endDate ?: today)
    }
    var warningDays by remember(existing?.id) {
        mutableStateOf((existing?.warningDays ?: 7).toString())
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (existing == null)
                    "Yeni Rapor Grubu"
                else
                    "Rapor Grubunu Düzenle",
                modifier = Modifier.fillMaxWidth(),
                color = LogoColorDark,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    shadow = LogoTextShadow
                )
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CenteredLabeledField("Rapor Grubu Adı") {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(
                            textAlign = TextAlign.Center
                        ),
                        colors = medicationFieldColors()
                    )
                }
                CenteredLabeledField("Rapor Başlangıç Tarihi") {
                    ThemedDateButton(
                        text = startDate,
                        onClick = {
                            pickDateString(context) { startDate = it }
                        },
                        medicationStyle = true
                    )
                }
                CenteredLabeledField("Rapor Bitiş Tarihi") {
                    ThemedDateButton(
                        text = endDate,
                        onClick = {
                            pickDateString(context) { endDate = it }
                        },
                        medicationStyle = true
                    )
                }
                CenteredLabeledField("Kaç Gün Önce Uyarı") {
                    OutlinedTextField(
                        value = warningDays,
                        onValueChange = {
                            warningDays = it.filter(Char::isDigit)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(
                            textAlign = TextAlign.Center
                        ),
                        colors = medicationFieldColors()
                    )
                }
            }
        },
        confirmButton = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    enabled = name.isNotBlank(),
                    onClick = {
                        onSave(
                            ReportGroup(
                                id = existing?.id ?: 0,
                                profileId = profileId,
                                name = name.trim(),
                                startDate = startDate,
                                endDate = endDate,
                                warningDays = warningDays.toIntOrNull() ?: 7
                            )
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LogoColor.copy(alpha = 0.25f),
                        contentColor = Color(0xFF123A37)
                    )
                ) {
                    Text("Kaydet")
                }
            }
        },
        dismissButton = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                TextButton(onClick = onDismiss) {
                    Text("İptal")
                }
            }
        }
    )
}

@Composable
private fun MeasurementsScreen(
    tab: MeasureTab,
    onTab: (MeasureTab) -> Unit,
    bp: List<BloodPressure>,
    glucose: List<BloodGlucose>,
    modifier: Modifier,
    onEditBp: (BloodPressure) -> Unit,
    onEditGlucose: (BloodGlucose) -> Unit,
    onAddMeasurement: () -> Unit
) {
    Column(modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TabRow(
                selectedTabIndex = if (tab == MeasureTab.BP) 0 else 1,
                modifier = Modifier.weight(1f),
                containerColor = Color.Transparent,
                contentColor = LogoColor,
                indicator = { tabPositions ->
                    val selectedIndex = if (tab == MeasureTab.BP) 0 else 1
                    TabRowDefaults.PrimaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedIndex]),
                        color = LogoColor
                    )
                }
            ) {
                Tab(
                    selected = tab == MeasureTab.BP,
                    onClick = { onTab(MeasureTab.BP) },
                    icon = { OutlinedLogoIcon(Icons.Default.Favorite, null, size = 30.dp) },
                    text = { Text("Tansiyon", color = LogoColorDark, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, shadow = LogoTextShadow)) }
                )
                Tab(
                    selected = tab == MeasureTab.GLUCOSE,
                    onClick = { onTab(MeasureTab.GLUCOSE) },
                    icon = { OutlinedLogoIcon(Icons.Default.Bloodtype, null, size = 30.dp) },
                    text = { Text("Kan Şekeri", color = LogoColorDark, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, shadow = LogoTextShadow)) }
                )
            }
            Column(
                modifier = Modifier.width(72.dp).heightIn(min = 72.dp).clickable(onClick = onAddMeasurement),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                OutlinedLogoIcon(Icons.Default.AddCircle, "Yeni ölçüm ekle", size = 30.dp)
                Spacer(Modifier.height(4.dp))
                Text("Ekle", color = LogoColorDark, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, shadow = LogoTextShadow))
            }
        }
        LazyColumn(
            Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (tab == MeasureTab.BP) {
                if (bp.isEmpty()) item { Text("Henüz tansiyon kaydı yok. Sağ üstteki + düğmesine basın.") }
                items(bp, key = { it.id }) { x ->
                    SifahaneCard(modifier = Modifier.fillMaxWidth(), onClick = { onEditBp(x) }) {
                        Column(Modifier.padding(14.dp)) {
                            Text("${x.systolic}/${x.diastolic} mmHg", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Text("Nabız: ${x.pulse ?: "-"}")
                            Text(formatDateTime(x.measuredAt))
                            if (x.note.isNotBlank()) Text(x.note)
                            Text("Düzenlemek veya silmek için dokunun.", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            } else {
                if (glucose.isEmpty()) item { Text("Henüz kan şekeri kaydı yok. Sağ üstteki + düğmesine basın.") }
                items(glucose, key = { it.id }) { x ->
                    SifahaneCard(modifier = Modifier.fillMaxWidth(), onClick = { onEditGlucose(x) }) {
                        Column(Modifier.padding(14.dp)) {
                            Text("${x.valueMgDl} mg/dL", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Text(x.measurementType)
                            Text(formatDateTime(x.measuredAt))
                            if (x.note.isNotBlank()) Text(x.note)
                            Text("Düzenlemek veya silmek için dokunun.", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReportsScreen(profileId: Long, db: AppDatabase, modifier: Modifier) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()
    var from by remember { mutableStateOf<Long?>(null) }
    var to by remember { mutableStateOf<Long?>(null) }
    var status by remember { mutableStateOf("") }
    var confirmDelete by remember { mutableStateOf(false) }
    var includeBpChart by remember { mutableStateOf(true) }
    var includeGlucoseChart by remember { mutableStateOf(true) }
    var bpSelectionTouched by remember { mutableStateOf(false) }
    var glucoseSelectionTouched by remember { mutableStateOf(false) }
    var chartBp by remember { mutableStateOf<List<BloodPressure>>(emptyList()) }
    var chartGlucose by remember { mutableStateOf<List<BloodGlucose>>(emptyList()) }
    var chartGenerated by remember { mutableStateOf(false) }
    var allRecordsSelected by remember { mutableStateOf(false) }
    var excelGenerated by remember { mutableStateOf(false) }
    var deleteCompleted by remember { mutableStateOf(false) }
    val reportControlShape = RoundedCornerShape(10.dp)

    Column(
        modifier
            .fillMaxSize()
            .vantablackPageGlassOverlay()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "Raporlar",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                shadow = LogoTextShadow
            ),
            color = LogoColorDark
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Column(Modifier.weight(1f)) {
                Text(
                    "Başlangıç Tarihi",
                    modifier = Modifier.fillMaxWidth(),
                    color = LogoColorDark,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium.copy(
                        shadow = LogoTextShadow
                    )
                )
                OutlinedButton(
                    onClick = { pickDate(context) { from = it; allRecordsSelected = false } },
                    modifier = Modifier.fillMaxWidth().height(52.dp).sifahaneSoftBoundary(5.dp).vantablackGlassOverlay(),
                    shape = reportControlShape,
                    border = BorderStroke(1.dp, Color.Transparent),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = LogoColor.copy(alpha = 0.50f),
                        contentColor = LogoColorDark
                    )
                ) {
                    Text(
                        from?.let { formatDate(it) } ?: "Seçilmedi",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = if (from != null) FontWeight.Bold else FontWeight.Normal,
                            shadow = LogoTextShadow
                        )
                    )
                }
            }
            Column(Modifier.weight(1f)) {
                Text(
                    "Bitiş Tarihi",
                    modifier = Modifier.fillMaxWidth(),
                    color = LogoColorDark,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium.copy(
                        shadow = LogoTextShadow
                    )
                )
                OutlinedButton(
                    onClick = { pickDate(context) { to = it; allRecordsSelected = false } },
                    modifier = Modifier.fillMaxWidth().height(52.dp).sifahaneSoftBoundary(5.dp).vantablackGlassOverlay(),
                    shape = reportControlShape,
                    border = BorderStroke(1.dp, Color.Transparent),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = LogoColor.copy(alpha = 0.50f),
                        contentColor = LogoColorDark
                    )
                ) {
                    Text(
                        to?.let { formatDate(it) } ?: "Seçilmedi",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = if (to != null) FontWeight.Bold else FontWeight.Normal,
                            shadow = LogoTextShadow
                        )
                    )
                }
            }
        }
        OutlinedButton(
            onClick = { from = null; to = null; allRecordsSelected = true },
            modifier = Modifier.fillMaxWidth().height(52.dp).sifahaneSoftBoundary(5.dp).vantablackGlassOverlay(),
            shape = reportControlShape,
            border = BorderStroke(1.dp, Color.Transparent),
            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = LogoColor.copy(alpha = 0.50f),
                                contentColor = LogoColorDark
                            )
        ) {
            Text(
                "Tüm Kayıtlar",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = if (allRecordsSelected) FontWeight.Bold else FontWeight.Normal,
                    shadow = LogoTextShadow
                )
            )
        }


        SifahaneCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "Grafik Rapor",
                    color = LogoColorDark,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        shadow = LogoTextShadow
                    )
                )
                Box(
                    Modifier.fillMaxWidth().heightIn(min = 52.dp)
                        .background(LogoColor.copy(alpha = 0.50f), reportControlShape)
                        .sifahaneSoftBoundary(5.dp).vantablackGlassOverlay().padding(horizontal = 8.dp)
                ) {
                    MedicationCheckBox(
                        checked = includeBpChart,
                        onCheckedChange = { includeBpChart = it; bpSelectionTouched = true },
                        label = "Tansiyon Grafiği",
                        centered = true,
                        boldWhenChecked = true,
                        emphasized = bpSelectionTouched && includeBpChart,
                        checkedColor = LogoColor.copy(alpha = 0.50f)
                    )
                }
                Box(
                    Modifier.fillMaxWidth().heightIn(min = 52.dp)
                        .background(LogoColor.copy(alpha = 0.50f), reportControlShape)
                        .sifahaneSoftBoundary(5.dp).vantablackGlassOverlay().padding(horizontal = 8.dp)
                ) {
                    MedicationCheckBox(
                        checked = includeGlucoseChart,
                        onCheckedChange = { includeGlucoseChart = it; glucoseSelectionTouched = true },
                        label = "Kan Şekeri Grafiği",
                        centered = true,
                        boldWhenChecked = true,
                        emphasized = glucoseSelectionTouched && includeGlucoseChart,
                        checkedColor = LogoColor.copy(alpha = 0.50f)
                    )
                }
                Button(
                    enabled = includeBpChart || includeGlucoseChart,
                    onClick = {
                        scope.launch {
                            val start = from ?: Long.MIN_VALUE
                            val endExclusive = to?.plus(86_400_000L) ?: Long.MAX_VALUE
                            chartBp = if (includeBpChart) {
                                withContext(Dispatchers.IO) {
                                    db.vitalsDao().allBp(profileId)
                                        .filter { it.measuredAt in start until endExclusive }
                                        .sortedBy { it.measuredAt }
                                }
                            } else emptyList()
                            chartGlucose = if (includeGlucoseChart) {
                                withContext(Dispatchers.IO) {
                                    db.vitalsDao().allGlucose(profileId)
                                        .filter { it.measuredAt in start until endExclusive }
                                        .sortedBy { it.measuredAt }
                                }
                            } else emptyList()
                            chartGenerated = true
                            status = "Grafik Rapor Oluşturuldu."
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp).sifahaneSoftBoundary(5.dp).vantablackGlassOverlay(),
                    shape = reportControlShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LogoColor.copy(alpha = 0.50f),
                        contentColor = LogoColorDark,
                        disabledContainerColor = LogoColor.copy(alpha = 0.50f),
                        disabledContentColor = LogoColorDark.copy(alpha = 0.25f)
                    )
                ) {
                    Icon(Icons.Default.ShowChart, null)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Grafik Rapor Oluştur",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = if (chartGenerated) FontWeight.Bold else FontWeight.Normal,
                            shadow = LogoTextShadow
                        )
                    )
                }
            }
        }

        if (chartGenerated) {
            if (includeBpChart) {
                VitalLineChart(
                    title = "Tansiyon Grafiği",
                    series = listOf(
                        "Büyük Tansiyon" to chartBp.map {
                            it.measuredAt to it.systolic.toFloat()
                        },
                        "Küçük Tansiyon" to chartBp.map {
                            it.measuredAt to it.diastolic.toFloat()
                        }
                    )
                )
            }
            if (includeGlucoseChart) {
                VitalLineChart(
                    title = "Kan Şekeri Grafiği",
                    series = listOf(
                        "Kan Şekeri" to chartGlucose.map {
                            it.measuredAt to it.valueMgDl.toFloat()
                        }
                    )
                )
            }
        }

        Button(
            onClick = {
                scope.launch {
                    status = "Excel hazırlanıyor…"
                    val file = withContext(Dispatchers.IO) { exportExcel(context, db, profileId, from, to) }
                    status = "Hazır: ${file.name}"
                    excelGenerated = true
                    shareFile(context, file)
                }
            },
            modifier = Modifier.fillMaxWidth().height(52.dp).sifahaneSoftBoundary(5.dp).vantablackGlassOverlay(),
            shape = reportControlShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = LogoColor.copy(alpha = 0.50f),
                contentColor = LogoColorDark
            )
        ) {
            Icon(Icons.Default.TableChart, null, tint = LogoColorDark)
            Spacer(Modifier.width(8.dp))
            Text(
                "Excel Raporu Oluştur Ve Paylaş",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = if (excelGenerated) FontWeight.Bold else FontWeight.Normal,
                    shadow = LogoTextShadow
                )
            )
        }

        val rangeDeleteEnabled = from != null && to != null
        OutlinedButton(
            enabled = rangeDeleteEnabled,
            onClick = { confirmDelete = true },
            modifier = Modifier.fillMaxWidth().height(52.dp).sifahaneSoftBoundary(5.dp).vantablackGlassOverlay(),
            shape = reportControlShape,
            border = BorderStroke(1.dp, Color.Transparent),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = LogoColor.copy(alpha = 0.50f),
                disabledContainerColor = LogoColor.copy(alpha = 0.50f),
                contentColor = Color(0xFFE30A17),
                disabledContentColor = Color(0xFFE30A17).copy(alpha = 0.25f)
            )
        ) {
            Icon(
                Icons.Default.DeleteSweep,
                contentDescription = null,
                tint = if (rangeDeleteEnabled) Color(0xFFE30A17) else Color(0xFFE30A17).copy(alpha = 0.25f)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                "Seçilen Tarih Aralığındaki Verileri Sil",
                color = if (rangeDeleteEnabled) Color(0xFFE30A17) else Color(0xFFE30A17).copy(alpha = 0.25f),
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = if (deleteCompleted) FontWeight.Bold else FontWeight.Normal,
                    shadow = LogoTextShadow
                )
            )
        }

        Text(status, color = LogoColorDark, style = MaterialTheme.typography.bodyMedium.copy(shadow = LogoTextShadow))
    }

    if (confirmDelete && from != null && to != null) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text("Tarih Aralığındaki Verileri Sil") },
            text = {
                Text(
                    "${formatDate(from!!)} – ${formatDate(to!!)} arasındaki ilaç alma geçmişi, tansiyon ve kan şekeri kayıtları kalıcı olarak silinecek. İlaç tanımları ve kullanıcı bilgileri korunacaktır."
                )
            },
            confirmButton = {
                Button(onClick = {
                    scope.launch(Dispatchers.IO) {
                        val endExclusive = to!! + 86_400_000L
                        db.doseLogDao().deleteDoseLogRange(profileId, from!!, endExclusive)
                        db.vitalsDao().deleteBpRange(profileId, from!!, endExclusive)
                        db.vitalsDao().deleteGlucoseRange(profileId, from!!, endExclusive)
                    }
                    confirmDelete = false
                    deleteCompleted = true
                    status = "Seçilen tarih aralığındaki geçmiş ve ölçüm kayıtları silindi."
                },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LogoColor.copy(alpha = 0.25f),
                        contentColor = Color(0xFF123A37)
                    )
                ) { Text("Evet, kalıcı olarak sil") }
            },
            dismissButton = {
                TextButton(onClick = { confirmDelete = false }) { Text("İptal") }
            }
        )
    }
}

private suspend fun exportExcel(context: Context, db: AppDatabase, profileId: Long, from: Long?, to: Long?): File {
    val wb = XSSFWorkbook()

    val meds = db.medicationDao().allForProfile(profileId)
    val medSheet = wb.createSheet("İlaçlar")
    listOf("İlaç", "Fonksiyon", "Doz", "Saatler", "Stok", "Kritik Sınır", "Doktor", "Branş", "Kurum", "Raporlu", "Rapor Bitiş")
        .forEachIndexed { i, h -> medSheet.createRow(0).createCell(i).setCellValue(h) }
    meds.forEachIndexed { index, m ->
        val r = medSheet.createRow(index + 1)
        listOf(m.name, m.purpose, m.dose, m.timesCsv, m.stock.toString(), m.lowStockLimit.toString(),
            m.doctorName, m.doctorBranch, m.doctorInstitution, if (m.isReported) "Evet" else "Hayır", m.reportEndDate ?: "")
            .forEachIndexed { i, v -> r.createCell(i).setCellValue(v) }
    }

    val logs = db.doseLogDao().allLogsForProfile(profileId).filter {
        (from == null || it.timestamp >= from!!) && (to == null || it.timestamp < to!! + 86_400_000L)
    }
    val logSheet = wb.createSheet("Doz Geçmişi")
    listOf("İlaç", "Planlanan", "Gerçek", "Durum").forEachIndexed { i, h -> logSheet.createRow(0).createCell(i).setCellValue(h) }
    logs.forEachIndexed { index, l ->
        val r = logSheet.createRow(index + 1)
        r.createCell(0).setCellValue(l.medicationName)
        r.createCell(1).setCellValue(formatDateTime(l.scheduledDateTime))
        r.createCell(2).setCellValue(l.actualDateTime?.let { formatDateTime(it) } ?: "")
        r.createCell(3).setCellValue(l.action)
    }

    val bp = db.vitalsDao().allBp(profileId).filter {
        (from == null || it.measuredAt >= from!!) && (to == null || it.measuredAt < to!! + 86_400_000L)
    }
    val bpSheet = wb.createSheet("Tansiyon")
    listOf("Tarih", "Büyük", "Küçük", "Nabız", "Not").forEachIndexed { i, h -> bpSheet.createRow(0).createCell(i).setCellValue(h) }
    bp.forEachIndexed { index, x ->
        val r = bpSheet.createRow(index + 1)
        r.createCell(0).setCellValue(formatDateTime(x.measuredAt))
        r.createCell(1).setCellValue(x.systolic.toDouble())
        r.createCell(2).setCellValue(x.diastolic.toDouble())
        r.createCell(3).setCellValue((x.pulse ?: 0).toDouble())
        r.createCell(4).setCellValue(x.note)
    }

    val gl = db.vitalsDao().allGlucose(profileId).filter {
        (from == null || it.measuredAt >= from!!) && (to == null || it.measuredAt < to!! + 86_400_000L)
    }
    val glSheet = wb.createSheet("Kan Şekeri")
    listOf("Tarih", "Tür", "mg/dL", "Not").forEachIndexed { i, h -> glSheet.createRow(0).createCell(i).setCellValue(h) }
    gl.forEachIndexed { index, x ->
        val r = glSheet.createRow(index + 1)
        r.createCell(0).setCellValue(formatDateTime(x.measuredAt))
        r.createCell(1).setCellValue(x.measurementType)
        r.createCell(2).setCellValue(x.valueMgDl.toDouble())
        r.createCell(3).setCellValue(x.note)
    }

    val dir = File(context.cacheDir, "reports").apply { mkdirs() }
    val file = File(dir, "Sifahane_Rapor_${System.currentTimeMillis()}.xlsx")
    file.outputStream().use { wb.write(it) }
    wb.close()
    return file
}

private fun shareFile(context: Context, file: File) {
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    context.startActivity(
        Intent.createChooser(
            Intent(Intent.ACTION_SEND).apply {
                type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            },
            "Excel raporunu paylaş"
        )
    )
}

@Composable
private fun ProfileEditorDialog(existing: UserProfile?, onDismiss: () -> Unit, onSave: (UserProfile) -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var name by remember { mutableStateOf(existing?.name ?: "") }
    var surname by remember { mutableStateOf(existing?.surname ?: "") }
    var relation by remember { mutableStateOf(existing?.relation ?: "") }
    var birthDate by remember { mutableStateOf(existing?.birthDate) }
    var bloodGroup by remember { mutableStateOf(existing?.bloodGroup ?: "Bilinmiyor") }
    var note by remember { mutableStateOf(existing?.profileNote ?: "") }
    var photo by remember { mutableStateOf(existing?.photoUri) }
    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }
    var cropCandidate by remember { mutableStateOf<String?>(null) }

    val gallery = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            persistImageToAppStorage(context, uri, "profile_photos", "profile")?.let {
                cropCandidate = it
            }
        }
    }
    val camera = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { ok ->
        if (ok) pendingCameraUri?.toString()?.let { cropCandidate = it }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Kullanıcı Profili",
                modifier = Modifier.fillMaxWidth(),
                color = LogoColorDark,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    shadow = LogoTextShadow
                )
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(3.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(bottom = 4.dp)
            ) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                    if (!photo.isNullOrBlank()) {
                        Image(
                            rememberAsyncImagePainter(photo),
                            null,
                            Modifier.size(96.dp).clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                
                    }
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(
                            8.dp,
                            Alignment.CenterHorizontally
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = {
                                gallery.launch(
                                    PickVisualMediaRequest(
                                        ActivityResultContracts.PickVisualMedia.ImageOnly
                                    )
                                )
                            },
                            modifier = Modifier.weight(1f).height(42.dp),
                            shape = StandardFieldShape,
                            colors = profileOutlinedButtonColors()
                        ) { Text("Galeriden", color = LogoColorDark, fontSize = 13.sp, style = MaterialTheme.typography.labelMedium.copy(shadow = LogoTextShadow)) }
                        OutlinedButton(
                            onClick = {
                            val dir = File(context.filesDir, "profile_photos").apply { mkdirs() }
                            val file = File(dir, "profile_${System.currentTimeMillis()}.jpg")
                            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                            pendingCameraUri = uri
                            camera.launch(uri)
                            },
                            modifier = Modifier.weight(1f).height(42.dp),
                            shape = StandardFieldShape,
                            colors = profileOutlinedButtonColors()
                        ) {
                            Text(
                                "Kamera",
                                modifier = Modifier.fillMaxWidth(),
                                color = LogoColorDark,
                                textAlign = TextAlign.Center,
                                fontSize = 13.sp,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    shadow = LogoTextShadow
                                )
                            )
                        }
                    }
                }
                item {
                    CenteredLabeledField("Ad") {
                        CompactProfileTextField(
                            value = name,
                            onValueChange = { name = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                        )
                    }
                }
                item {
                    CenteredLabeledField("Soyad") {
                        CompactProfileTextField(
                            value = surname,
                            onValueChange = { surname = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                        )
                    }
                }
                item {
                    CenteredLabeledField("Doğum Tarihi") {
                        ThemedDateButton(
                            text = birthDate ?: "Seçilmedi",
                            modifier = Modifier.fillMaxWidth().height(44.dp),
                            onClick = {
                                pickDateString(context) { birthDate = it }
                            }
                        )
                    }
                }
                item {
                    var expanded by remember { mutableStateOf(false) }
                    CenteredLabeledField("Kan Grubu") {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            OutlinedButton(
                                onClick = { expanded = true },
                                modifier = Modifier.fillMaxWidth().height(44.dp),
                                border = BorderStroke(1.5.dp, LogoColor),
                                shape = StandardFieldShape,
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = Vantablack05,
                                    contentColor = LogoColorDark
                                )
                            ) {
                                Text(
                                    bloodGroup,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center,
                                    color = LogoColorDark,
                                    fontSize = 13.sp,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        shadow = LogoTextShadow
                                    )
                                )
                            }
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                modifier = Modifier.fillMaxWidth(0.86f),
                                shape = StandardFieldShape,
                                containerColor = Color(0xFFF2F2F2)
                            ) {
                                listOf(
                                    "A Rh+", "A Rh-", "B Rh+", "B Rh-",
                                    "AB Rh+", "AB Rh-", "0 Rh+", "0 Rh-",
                                    "Bilinmiyor"
                                ).forEach { option ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                option,
                                                modifier = Modifier.fillMaxWidth(),
                                                textAlign = TextAlign.Center,
                                                color = LogoColorDark
                                            )
                                        },
                                        onClick = {
                                            bloodGroup = option
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
                item {
                    CenteredLabeledField("Yakınlık") {
                        CompactProfileTextField(
                            value = relation,
                            onValueChange = { relation = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                        )
                    }
                }
                item {
                    CenteredLabeledField("Not") {
                        CompactProfileTextField(
                            value = note,
                            onValueChange = { note = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 44.dp, max = 116.dp),
                            singleLine = false,
                            minLines = 1,
                            maxLines = 5
                        )
                    }
                }
            }
        },
        confirmButton = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    enabled = name.isNotBlank(),
                    onClick = {
                        onSave(
                            UserProfile(
                                existing?.id ?: 0,
                                name,
                                relation,
                                surname,
                                photo,
                                birthDate,
                                bloodGroup,
                                note
                            )
                        )
                    }
                ) {
                    Text(
                        "Kaydet",
                        color = LogoColorDark,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            shadow = LogoTextShadow
                        )
                    )
                }
            }
        },
        dismissButton = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                TextButton(onClick = onDismiss) {
                    Text(
                        "İptal",
                        color = LogoColorDark,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            shadow = LogoTextShadow
                        )
                    )
                }
            }
        }
    )

    cropCandidate?.let { candidate ->
        CropImageDialog(
            sourceUri = candidate,
            circularPreview = true,
            targetAspectRatio = 1f,
            outputSubfolder = "profile_photos",
            outputPrefix = "profile",
            onDismiss = { cropCandidate = null },
            onConfirm = {
                photo = it
                cropCandidate = null
            }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SuggestionTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    options: List<String>,
    modifier: Modifier = Modifier.fillMaxWidth(),
    medicationStyle: Boolean = false
) {
    var expanded by remember { mutableStateOf(false) }
    val filtered = remember(value, options) {
        options.asSequence()
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .distinct()
            .filter { value.isBlank() || it.contains(value, ignoreCase = true) }
            .take(12)
            .toList()
    }

    ExposedDropdownMenuBox(
        expanded = expanded && filtered.isNotEmpty(),
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {
                onValueChange(it)
                expanded = true
            },
            label = { Text(label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = modifier.menuAnchor(),
            colors = if (medicationStyle) medicationFieldColors() else logoFieldColors(),
            textStyle = LocalTextStyle.current.copy(
                color = if (medicationStyle) LogoColorDark else LocalContentColor.current
            )
        )

        ExposedDropdownMenu(
            expanded = expanded && filtered.isNotEmpty(),
            onDismissRequest = { expanded = false }
        ) {
            filtered.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onValueChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun MedicationEditorDialog(
    existing: Medication?,
    profileId: Long,
    suggestions: List<Medication>,
    reportGroups: List<ReportGroup>,
    onCreateReportGroup: (ReportGroup) -> Unit,
    onUpdateReportGroup: (ReportGroup) -> Unit,
    onDismiss: () -> Unit,
    onSave: (Medication) -> Unit,
    onDelete: (Medication) -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val today = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()) }

    var name by remember { mutableStateOf(existing?.name ?: "") }
    var purpose by remember { mutableStateOf(existing?.purpose ?: "") }
    var dose by remember { mutableStateOf(existing?.dose ?: "1 tablet") }
    var times by remember { mutableStateOf(existing?.timesCsv?.split(",")?.toMutableList() ?: mutableListOf("08:00")) }
    var stock by remember { mutableStateOf((existing?.stock ?: 28).toString()) }
    var lowStock by remember { mutableStateOf((existing?.lowStockLimit ?: 5).toString()) }
    var notes by remember { mutableStateOf(existing?.notes ?: "") }
    var startDate by remember { mutableStateOf(existing?.startDate ?: today) }
    var endDate by remember { mutableStateOf(existing?.endDate ?: today) }
    var continuous by remember { mutableStateOf(existing?.continuous ?: false) }
    var archived by remember { mutableStateOf(existing?.archived ?: false) }
    var active by remember { mutableStateOf(existing?.active ?: true) }
    var photo by remember { mutableStateOf(existing?.photoUri) }
    var barcode by remember { mutableStateOf(existing?.barcode ?: "") }
    var prospectus by remember { mutableStateOf(existing?.prospectusUrl ?: "") }
    var doctorName by remember { mutableStateOf(existing?.doctorName ?: "") }
    var doctorBranch by remember { mutableStateOf(existing?.doctorBranch ?: "") }
    var doctorInstitution by remember { mutableStateOf(existing?.doctorInstitution ?: "") }
    var doctorPhone by remember { mutableStateOf(existing?.doctorPhone ?: "") }
    var isReported by remember { mutableStateOf(existing?.isReported ?: false) }
    var reportStart by remember { mutableStateOf(existing?.reportStartDate ?: today) }
    var reportEnd by remember { mutableStateOf(existing?.reportEndDate ?: today) }
    var reportWarning by remember { mutableStateOf((existing?.reportWarningDays ?: 7).toString()) }
    var selectedReportGroupId by remember { mutableStateOf(existing?.reportGroupId) }
    var reportModeGroup by remember { mutableStateOf(existing?.reportGroupId != null) }
    var showNewGroupDialog by remember { mutableStateOf(false) }

    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }
    var confirmDelete by remember { mutableStateOf(false) }
    val nameOptions = remember(suggestions) { suggestions.map { it.name } }
    val purposeOptions = remember(suggestions) { suggestions.map { it.purpose } }
    val doseOptions = remember(suggestions) { suggestions.map { it.dose } }
    val notesOptions = remember(suggestions) { suggestions.map { it.notes } }
    val doctorNameOptions = remember(suggestions) { suggestions.map { it.doctorName } }
    val doctorBranchOptions = remember(suggestions) { suggestions.map { it.doctorBranch } }
    val doctorInstitutionOptions = remember(suggestions) { suggestions.map { it.doctorInstitution } }
    val doctorPhoneOptions = remember(suggestions) { suggestions.map { it.doctorPhone } }

    
    var scannerMessage by remember { mutableStateOf("") }

    val localScanner = rememberLauncherForActivityResult(StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val raw = result.data?.getStringExtra(ScannerActivity.EXTRA_CODE).orEmpty()
            if (raw.isNotBlank()) {
                barcode = raw
                scannerMessage = "Kod okundu: $raw"
                val query = listOf(name, raw, "resmi üretici prospektüs PDF")
                    .filter { it.isNotBlank() }
                    .joinToString(" ")
                prospectus = "https://www.google.com/search?q=" + Uri.encode(query)
            } else {
                scannerMessage = "Kod okunamadı."
            }
        } else {
            scannerMessage = "Tarama iptal edildi."
        }
    }

    fun startBarcodeScan() {
        scannerMessage = "Kamera açılıyor…"
        localScanner.launch(Intent(context, ScannerActivity::class.java))
    }


    var cropCandidate by remember { mutableStateOf<String?>(null) }

    val gallery = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            persistImageToAppStorage(context, uri, "medicine_photos", "medicine")?.let {
                cropCandidate = it
            }
        }
    }
    val camera = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { ok ->
        if (ok) pendingCameraUri?.toString()?.let { cropCandidate = it }
    }

    fun pickTime(index: Int) {
        val p = times[index].split(":")
        TimePickerDialog(context, { _, h, m ->
            val n = times.toMutableList()
            n[index] = "%02d:%02d".format(h, m)
            times = n
        }, p[0].toInt(), p[1].toInt(), true).show()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (existing == null) "Yeni İlaç" else "İlacı Düzenle",
                modifier = Modifier.fillMaxWidth(),
                color = LogoColorDark,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    shadow = LogoTextShadow
                )
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item { if (!photo.isNullOrBlank()) Image(rememberAsyncImagePainter(photo), null, Modifier.fillMaxWidth().height(190.dp), contentScale = ContentScale.Crop) }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(
                            8.dp,
                            Alignment.CenterHorizontally
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = { gallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                            modifier = Modifier.weight(1f).height(44.dp),
                            shape = StandardFieldShape,
                            colors = medicationOutlinedButtonColors()
                        ) {
                            Text(
                                "Galeriden",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                fontSize = 12.sp
                            )
                        }
                        OutlinedButton(
                            onClick = {
                            val dir = File(context.filesDir, "medicine_photos").apply { mkdirs() }
                            val file = File(dir, "medicine_${System.currentTimeMillis()}.jpg")
                            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                            pendingCameraUri = uri
                            camera.launch(uri)
                            },
                            modifier = Modifier.weight(1f).height(44.dp),
                            shape = StandardFieldShape,
                            colors = medicationOutlinedButtonColors()
                        ) {
                            Text(
                                "Kamera",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
                item { SuggestionTextField(name, { name = it }, "İlaç adı", nameOptions, medicationStyle = true) }
                item { SuggestionTextField(purpose, { purpose = it }, "Fonksiyonu", purposeOptions, medicationStyle = true) }
                item { SuggestionTextField(dose, { dose = it }, "Doz", doseOptions, medicationStyle = true) }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton({ if (times.size > 1) times = times.dropLast(1).toMutableList() }) { Icon(Icons.Default.RemoveCircle, null) }
                        Text(
                            "Günlük Doz: ${times.size}",
                            textAlign = TextAlign.Center,
                            color = LogoColorDark,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                shadow = LogoTextShadow
                            )
                        )
                        IconButton({ if (times.size < 8) times = (times + "08:00").toMutableList() }) { Icon(Icons.Default.AddCircle, null) }
                    }
                }
                items(times.size) { i -> OutlinedButton(
                            onClick = { pickTime(i) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = medicationOutlinedButtonColors()
                        ) { Text("${i + 1}. doz: ${times[i]}") } }
                item { OutlinedButton({ pickDateString(context) { startDate = it } }, Modifier.fillMaxWidth()) { Text("Başlangıç: $startDate") } }
                item {
                    CenteredCheckboxField(
                        label = "Sürekli Kullanım",
                        checked = continuous,
                        onCheckedChange = { continuous = it }
                    )
                }
                if (!continuous) item { OutlinedButton({ pickDateString(context) { endDate = it } }, Modifier.fillMaxWidth()) { Text("Bitiş: $endDate") } }
                item { Spacer(Modifier.height(10.dp)) }
                item {
                    CenteredCheckboxField(
                        label = "Arşiv İlacı",
                        checked = archived,
                        onCheckedChange = { archived = it }
                    )
                }
                item { OutlinedTextField(stock, { stock = it.filter(Char::isDigit) }, label = { Text("Kalan stok") },
                        textStyle = LocalTextStyle.current.copy(
                            textAlign = TextAlign.Center
                        ),
                        shape = StandardFieldShape,
                        colors = standardFieldColors()
                    ) }
                item { OutlinedTextField(lowStock, { lowStock = it.filter(Char::isDigit) }, label = { Text("Kritik stok sınırı") },
                        textStyle = LocalTextStyle.current.copy(
                            textAlign = TextAlign.Center
                        ),
                        shape = StandardFieldShape,
                        colors = standardFieldColors()
                    ) }
                item { SuggestionTextField(notes, { notes = it }, "Kullanım talimatı ve not", notesOptions, medicationStyle = true) }
                item {
                    SuggestionTextField(
                        doctorName,
                        { selected ->
                            doctorName = selected
                            suggestions.firstOrNull { it.doctorName == selected }?.let { source ->
                                if (doctorBranch.isBlank()) doctorBranch = source.doctorBranch
                                if (doctorInstitution.isBlank()) doctorInstitution = source.doctorInstitution
                                if (doctorPhone.isBlank()) doctorPhone = source.doctorPhone
                            }
                        },
                        "Doktor adı soyadı",
                        doctorNameOptions,
                        medicationStyle = true
                    )
                }
                item { SuggestionTextField(doctorBranch, { doctorBranch = it }, "Doktor branşı", doctorBranchOptions, medicationStyle = true) }
                item { SuggestionTextField(doctorInstitution, { doctorInstitution = it }, "Hastane / kurum", doctorInstitutionOptions, medicationStyle = true) }
                item { SuggestionTextField(doctorPhone, { doctorPhone = it }, "Doktor telefonu", doctorPhoneOptions, medicationStyle = true) }
                item {
                    CenteredCheckboxField(
                        label = "Raporlu İlaç",
                        checked = isReported,
                        onCheckedChange = {
                            isReported = it
                            if (!it) {
                                selectedReportGroupId = null
                                reportModeGroup = false
                            }
                        }
                    )
                }
                if (isReported) {
                    item {
                        var groupExpanded by remember { mutableStateOf(false) }
                        Box {
                            OutlinedButton(
                                onClick = { groupExpanded = true },
                                modifier = Modifier.fillMaxWidth(),
                                border = BorderStroke(1.5.dp, LogoColor),
                                colors = medicationOutlinedButtonColors()
                            ) {
                                val selectedName = reportGroups
                                    .firstOrNull { it.id == selectedReportGroupId }
                                    ?.name
                                Text(
                                    selectedName?.let {
                                        "Rapor Grubu: ${titleCaseTr(it)}"
                                    } ?: "Rapor Grubu Seçilmedi",
                                    maxLines = 1
                                )
                            }
                            DropdownMenu(
                                expanded = groupExpanded,
                                onDismissRequest = { groupExpanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Rapor Grubu Seçilmedi") },
                                    onClick = {
                                        selectedReportGroupId = null
                                        reportModeGroup = false
                                        groupExpanded = false
                                    }
                                )
                                reportGroups.forEach { group ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                "${titleCaseTr(group.name)} • ${group.startDate} – ${group.endDate}"
                                            )
                                        },
                                        onClick = {
                                            selectedReportGroupId = group.id
                                            reportModeGroup = true
                                            reportStart = group.startDate
                                            reportEnd = group.endDate
                                            reportWarning = group.warningDays.toString()
                                            groupExpanded = false
                                        }
                                    )
                                }
                                HorizontalDivider()
                                DropdownMenuItem(
                                    text = { Text("Yeni Rapor Grubu Oluştur") },
                                    onClick = {
                                        groupExpanded = false
                                        showNewGroupDialog = true
                                    }
                                )
                            }
                        }
                    }
                    item {
                        ThemedDateButton(
                            text = "Rapor Başlangıç: $reportStart",
                            onClick = {
                                if (!reportModeGroup) {
                                    pickDateString(context) { reportStart = it }
                                }
                            },
                            medicationStyle = true
                        )
                    }
                    item {
                        ThemedDateButton(
                            text = "Rapor Bitiş: $reportEnd",
                            onClick = {
                                if (!reportModeGroup) {
                                    pickDateString(context) { reportEnd = it }
                                }
                            },
                            medicationStyle = true
                        )
                    }
                    if (reportModeGroup) {
                        item {
                            Text(
                                "Tarihler seçilen rapor grubundan otomatik alınmıştır. Elle değiştirmek için “Rapor Grubu Seçilmedi” seçeneğini kullanın.",
                                color = LogoColorDark,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    shadow = LogoTextShadow
                                )
                            )
                        }
                    }
                    item {
                        OutlinedTextField(
                            reportWarning,
                            { reportWarning = it.filter(Char::isDigit) },
                            label = { Text("Kaç Gün Önce Uyarı") },
                            enabled = !reportModeGroup,
                            colors = standardFieldColors()
                        )
                    }
                }
                item {
                    OutlinedButton(
                        onClick = { startBarcodeScan() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = medicationOutlinedButtonColors()
                    ) {
                        Icon(Icons.Default.QrCodeScanner, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Barkod / Karekod Oku")
                    }
                }
                if (scannerMessage.isNotBlank()) item { Text(scannerMessage) }
                if (barcode.isNotBlank()) item { Text("Okunan kod: $barcode") }
                item {
                    Button(
                        enabled = barcode.isNotBlank() || name.isNotBlank(),
                        onClick = {
                            val query = listOf(name, barcode, "resmi üretici prospektüs PDF")
                                .filter { it.isNotBlank() }
                                .joinToString(" ")
                            prospectus = "https://www.google.com/search?q=" + Uri.encode(query)
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(prospectus)))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = 4.dp,
                                shape = RoundedCornerShape(24.dp),
                                ambientColor = Color.Black,
                                spotColor = Color.Black
                            ),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LogoColor.copy(alpha = 0.25f),
                            contentColor = Color.Black,
                            disabledContainerColor = LogoColor.copy(alpha = 0.45f),
                            disabledContentColor = Color.Black.copy(alpha = 0.55f)
                        ),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 12.dp)
                    ) {
                        Icon(
                            Icons.Default.Description,
                            null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(5.dp))
                        Text(
                            "Üretici Prospektüsünü Bul ve Aç",
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Clip,
                            fontSize = 12.sp
                        )
                    }
                }
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Vantablack05, StandardFieldShape)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Switch(active, { active = it })
                        Text(
                            "Aktif",
                            color = LogoColorDark,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                shadow = LogoTextShadow
                            )
                        )
                    }
                }
                if (existing != null) item {
                    OutlinedButton({ confirmDelete = true }, colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)) { Text("İLACI SİL") }
                }
            }
        },
        confirmButton = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    enabled = name.isNotBlank(),
                    onClick = {
                        onSave(
                            Medication(
                                id = existing?.id ?: 0,
                                profileId = profileId,
                                name = name,
                                purpose = purpose,
                                dose = dose,
                                timesCsv = times.joinToString(","),
                                stock = stock.toIntOrNull() ?: 0,
                                lowStockLimit = lowStock.toIntOrNull() ?: 5,
                                photoUri = photo,
                                notes = notes,
                                startDate = startDate,
                                endDate = if (continuous) null else endDate,
                                continuous = continuous,
                                active = active,
                                archived = archived,
                                barcode = barcode.ifBlank { null },
                                prospectusUrl = prospectus.ifBlank { null },
                                doctorName = doctorName,
                                doctorBranch = doctorBranch,
                                doctorInstitution = doctorInstitution,
                                doctorPhone = doctorPhone,
                                isReported = isReported,
                                reportStartDate = if (isReported) reportStart else null,
                                reportEndDate = if (isReported) reportEnd else null,
                                reportWarningDays = reportWarning.toIntOrNull() ?: 7,
                                reportGroupId =
                                    if (isReported && reportModeGroup)
                                        selectedReportGroupId
                                    else
                                        null
                            )
                        )
                    }
                ) {
                    Text("Kaydet")
                }
            }
        },
        dismissButton = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                TextButton(onClick = onDismiss) {
                    Text("İptal")
                }
            }
        }
    )

    if (confirmDelete && existing != null) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text("Emin misiniz?") },
            text = { Text("${existing.name} silinecek.") },
            confirmButton = { Button({ onDelete(existing); confirmDelete = false }) { Text("Evet, sil") } },
            dismissButton = { TextButton({ confirmDelete = false }) { Text("İptal") } }
        )
    }

    cropCandidate?.let { candidate ->
        CropImageDialog(
            sourceUri = candidate,
            circularPreview = false,
            targetAspectRatio = 4f / 3f,
            outputSubfolder = "medicine_photos",
            outputPrefix = "medicine",
            onDismiss = { cropCandidate = null },
            onConfirm = {
                photo = it
                cropCandidate = null
            }
        )
    }

    if (showNewGroupDialog) {
        var groupName by remember { mutableStateOf("") }
        var groupStart by remember { mutableStateOf(reportStart) }
        var groupEnd by remember { mutableStateOf(reportEnd) }
        var groupWarning by remember { mutableStateOf(reportWarning) }

        AlertDialog(
            onDismissRequest = { showNewGroupDialog = false },
            title = { Text("Yeni Rapor Grubu") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(groupName, { groupName = it }, label = { Text("Rapor Grubu Adı") },
                        textStyle = LocalTextStyle.current.copy(
                            textAlign = TextAlign.Center
                        ),
                        shape = StandardFieldShape,
                        colors = standardFieldColors()
                    )
                    ThemedDateButton(
                        text = "Rapor Başlangıç: $groupStart",
                        onClick = { pickDateString(context) { groupStart = it } },
                        medicationStyle = true
                    )
                    ThemedDateButton(
                        text = "Rapor Bitiş: $groupEnd",
                        onClick = { pickDateString(context) { groupEnd = it } },
                        medicationStyle = true
                    )
                    OutlinedTextField(groupWarning, { groupWarning = it.filter(Char::isDigit) }, label = { Text("Kaç gün önce uyarı") },
                        textStyle = LocalTextStyle.current.copy(
                            textAlign = TextAlign.Center
                        ),
                        shape = StandardFieldShape,
                        colors = standardFieldColors()
                    )
                }
            },
            confirmButton = {
                Button(
                    enabled = groupName.isNotBlank(),
                    onClick = {
                        onCreateReportGroup(
                            ReportGroup(
                                profileId = profileId,
                                name = groupName,
                                startDate = groupStart,
                                endDate = groupEnd,
                                warningDays = groupWarning.toIntOrNull() ?: 7
                            )
                        )
                        reportStart = groupStart
                        reportEnd = groupEnd
                        reportWarning = groupWarning
                        showNewGroupDialog = false
                    }
                ) { Text("Kaydet") }
            },
            dismissButton = { TextButton(onClick = { showNewGroupDialog = false }) { Text("İptal") } }
        )
    }
}

@Composable
private fun BpDialog(onDismiss: () -> Unit, onSave: (Int, Int, Int?, String) -> Unit) {
    var s by remember { mutableStateOf("") }
    var d by remember { mutableStateOf("") }
    var p by remember { mutableStateOf("") }
    var n by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tansiyon Ölçümü Ekle") },
        text = { Column { OutlinedTextField(s, { s = it.filter(Char::isDigit) }, label = { Text("Büyük tansiyon") },
                        colors = logoFieldColors()
                    ); OutlinedTextField(d, { d = it.filter(Char::isDigit) }, label = { Text("Küçük tansiyon") },
                        colors = logoFieldColors()
                    ); OutlinedTextField(p, { p = it.filter(Char::isDigit) }, label = { Text("Nabız") },
                        colors = logoFieldColors()
                    ); OutlinedTextField(n, { n = it }, label = { Text("Not") },
                        colors = logoFieldColors()
                    ) } },
        confirmButton = { Button(enabled = s.toIntOrNull() != null && d.toIntOrNull() != null, onClick = { onSave(s.toInt(), d.toInt(), p.toIntOrNull(), n) }) { Text("Kaydet") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("İptal") } }
    )
}

@Composable
private fun GlucoseDialog(onDismiss: () -> Unit, onSave: (Int, String, String) -> Unit) {
    var v by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Açlık") }
    var n by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Kan Şekeri Ölçümü Ekle") },
        text = {
            Column {
                OutlinedTextField(v, { v = it.filter(Char::isDigit) }, label = { Text("mg/dL") },
                        colors = logoFieldColors()
                    )
                listOf("Açlık", "Tokluk", "Rastgele", "Gece").forEach { x ->
                    Row(verticalAlignment = Alignment.CenterVertically) { RadioButton(type == x, { type = x }); Text(x) }
                }
                OutlinedTextField(n, { n = it }, label = { Text("Not") },
                        colors = logoFieldColors()
                    )
            }
        },
        confirmButton = { Button(enabled = v.toIntOrNull() != null, onClick = { onSave(v.toInt(), type, n) }) { Text("Kaydet") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("İptal") } }
    )
}


@Composable
private fun EditBpDialog(
    item: BloodPressure,
    onDismiss: () -> Unit,
    onSave: (BloodPressure) -> Unit,
    onDelete: () -> Unit
) {
    var systolic by remember { mutableStateOf(item.systolic.toString()) }
    var diastolic by remember { mutableStateOf(item.diastolic.toString()) }
    var pulse by remember { mutableStateOf(item.pulse?.toString() ?: "") }
    var note by remember { mutableStateOf(item.note) }
    var measuredAt by remember { mutableLongStateOf(item.measuredAt) }
    var confirmDelete by remember { mutableStateOf(false) }
    val context = androidx.compose.ui.platform.LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tansiyon Kaydını Düzenle") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(systolic, { systolic = it.filter(Char::isDigit) }, label = { Text("Büyük tansiyon") },
                        colors = logoFieldColors()
                    )
                OutlinedTextField(diastolic, { diastolic = it.filter(Char::isDigit) }, label = { Text("Küçük tansiyon") },
                        colors = logoFieldColors()
                    )
                OutlinedTextField(pulse, { pulse = it.filter(Char::isDigit) }, label = { Text("Nabız") },
                        colors = logoFieldColors()
                    )
                OutlinedTextField(note, { note = it }, label = { Text("Not") },
                        colors = logoFieldColors()
                    )
                OutlinedButton(
                    onClick = { pickDate(context) { measuredAt = it } },
                    modifier = Modifier.fillMaxWidth(),
                    border = BorderStroke(1.5.dp, LogoColor),
                    colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Vantablack10,
                                contentColor = LogoColorDark
                            )
                ) {
                    Text("Tarih: ${formatDate(measuredAt)}", color = LogoColorDark, style = MaterialTheme.typography.labelLarge.copy(shadow = LogoTextShadow))
                }
                OutlinedButton(
                    onClick = { confirmDelete = true },
                    modifier = Modifier.fillMaxWidth(),
                    border = BorderStroke(1.5.dp, LogoColor),
                    colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Vantablack10,
                                contentColor = LogoColorDark
                            )
                ) { Text("Kaydı sil", color = LogoColorDark, style = MaterialTheme.typography.labelLarge.copy(shadow = LogoTextShadow)) }
            }
        },
        confirmButton = {
            Button(
                enabled = systolic.toIntOrNull() != null && diastolic.toIntOrNull() != null,
                onClick = {
                    onSave(
                        item.copy(
                            systolic = systolic.toInt(),
                            diastolic = diastolic.toInt(),
                            pulse = pulse.toIntOrNull(),
                            measuredAt = measuredAt,
                            note = note
                        )
                    )
                }
            ,
                colors = ButtonDefaults.buttonColors(
                    containerColor = LogoColor.copy(alpha = 0.25f),
                    contentColor = Color(0xFF123A37)
                )
            ) { Text("Kaydet") }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                            containerColor = Vantablack10,
                            contentColor = LogoColorDark
                        )
            ) { Text("İptal") }
        }
    )

    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text("Ölçümü sil") },
            text = { Text("Bu tansiyon kaydını silmek istediğinizden emin misiniz?") },
            confirmButton = {
                Button(
                    onClick = onDelete,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LogoColor.copy(alpha = 0.25f),
                        contentColor = Color(0xFF123A37)
                    )
                ) { Text("Evet, sil") }
            },
            dismissButton = {
                TextButton(
                    onClick = { confirmDelete = false },
                    colors = ButtonDefaults.textButtonColors(
                            containerColor = Vantablack10,
                            contentColor = LogoColorDark
                        )
                ) { Text("İptal") }
            }
        )
    }
}

@Composable
private fun EditGlucoseDialog(
    item: BloodGlucose,
    onDismiss: () -> Unit,
    onSave: (BloodGlucose) -> Unit,
    onDelete: () -> Unit
) {
    var value by remember { mutableStateOf(item.valueMgDl.toString()) }
    var type by remember { mutableStateOf(item.measurementType) }
    var note by remember { mutableStateOf(item.note) }
    var measuredAt by remember { mutableLongStateOf(item.measuredAt) }
    var confirmDelete by remember { mutableStateOf(false) }
    val context = androidx.compose.ui.platform.LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Kan Şekeri Kaydını Düzenle") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value, { value = it.filter(Char::isDigit) }, label = { Text("mg/dL") },
                        colors = logoFieldColors()
                    )
                listOf("Açlık", "Tokluk", "Rastgele", "Gece").forEach { option ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = type == option, onClick = { type = option })
                        Text(option)
                    }
                }
                OutlinedTextField(note, { note = it }, label = { Text("Not") },
                        colors = logoFieldColors()
                    )
                OutlinedButton(
                    onClick = { pickDate(context) { measuredAt = it } },
                    modifier = Modifier.fillMaxWidth(),
                    border = BorderStroke(1.5.dp, LogoColor),
                    colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Vantablack10,
                                contentColor = LogoColorDark
                            )
                ) {
                    Text("Tarih: ${formatDate(measuredAt)}", color = LogoColorDark, style = MaterialTheme.typography.labelLarge.copy(shadow = LogoTextShadow))
                }
                OutlinedButton(
                    onClick = { confirmDelete = true },
                    modifier = Modifier.fillMaxWidth(),
                    border = BorderStroke(1.5.dp, LogoColor),
                    colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Vantablack10,
                                contentColor = LogoColorDark
                            )
                ) { Text("Kaydı sil", color = LogoColorDark, style = MaterialTheme.typography.labelLarge.copy(shadow = LogoTextShadow)) }
            }
        },
        confirmButton = {
            Button(
                enabled = value.toIntOrNull() != null,
                onClick = {
                    onSave(
                        item.copy(
                            valueMgDl = value.toInt(),
                            measurementType = type,
                            measuredAt = measuredAt,
                            note = note
                        )
                    )
                }
            ,
                colors = ButtonDefaults.buttonColors(
                    containerColor = LogoColor.copy(alpha = 0.25f),
                    contentColor = Color(0xFF123A37)
                )
            ) { Text("Kaydet") }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                            containerColor = Vantablack10,
                            contentColor = LogoColorDark
                        )
            ) { Text("İptal") }
        }
    )

    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text("Ölçümü sil") },
            text = { Text("Bu kan şekeri kaydını silmek istediğinizden emin misiniz?") },
            confirmButton = {
                Button(
                    onClick = onDelete,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LogoColor.copy(alpha = 0.25f),
                        contentColor = Color(0xFF123A37)
                    )
                ) { Text("Evet, sil") }
            },
            dismissButton = {
                TextButton(
                    onClick = { confirmDelete = false },
                    colors = ButtonDefaults.textButtonColors(
                            containerColor = Vantablack10,
                            contentColor = LogoColorDark
                        )
                ) { Text("İptal") }
            }
        )
    }
}


private fun pickDate(context: Context, onPicked: (Long) -> Unit) {
    val c = Calendar.getInstance()
    DatePickerDialog(context, { _, y, m, d ->
        onPicked(Calendar.getInstance().apply { set(y, m, d, 0, 0, 0); set(Calendar.MILLISECOND, 0) }.timeInMillis)
    }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
}

private fun pickDateString(context: Context, onPicked: (String) -> Unit) {
    val c = Calendar.getInstance()
    DatePickerDialog(context, { _, y, m, d ->
        onPicked("%04d-%02d-%02d".format(y, m + 1, d))
    }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
}

private fun formatDate(m: Long) = SimpleDateFormat("dd.MM.yyyy", Locale("tr", "TR")).format(Date(m))
private fun formatDateTime(m: Long) = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale("tr", "TR")).format(Date(m))



private fun persistImageToAppStorage(
    context: Context,
    sourceUri: Uri,
    subfolder: String,
    prefix: String
): String? {
    return runCatching {
        val dir = File(context.filesDir, subfolder).apply { mkdirs() }
        val target = File(dir, "${prefix}_${System.currentTimeMillis()}.jpg")
        context.contentResolver.openInputStream(sourceUri).use { input ->
            requireNotNull(input)
            FileOutputStream(target).use { output -> input.copyTo(output) }
        }
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", target).toString()
    }.getOrNull()
}



private fun cropAndSaveImage(
    context: Context,
    sourceUri: String,
    scale: Float,
    offset: Offset,
    viewport: IntSize,
    targetAspectRatio: Float,
    subfolder: String,
    prefix: String
): String? {
    return runCatching {
        val bitmap = context.contentResolver.openInputStream(Uri.parse(sourceUri)).use { input ->
            BitmapFactory.decodeStream(input)
        } ?: return null

        val viewW = viewport.width.toFloat()
        val viewH = viewport.height.toFloat()
        if (viewW <= 0f || viewH <= 0f) return null

        val frameW: Float
        val frameH: Float
        if (viewW / viewH > targetAspectRatio) {
            frameH = viewH * 0.86f
            frameW = frameH * targetAspectRatio
        } else {
            frameW = viewW * 0.86f
            frameH = frameW / targetAspectRatio
        }
        val frameLeft = (viewW - frameW) / 2f
        val frameTop = (viewH - frameH) / 2f

        // Preview uses ContentScale.Fit. Recreate exactly the same base placement.
        val baseScale = min(viewW / bitmap.width.toFloat(), viewH / bitmap.height.toFloat())
        val totalScale = baseScale * scale
        val displayedW = bitmap.width * totalScale
        val displayedH = bitmap.height * totalScale
        val imageLeft = (viewW - displayedW) / 2f + offset.x
        val imageTop = (viewH - displayedH) / 2f + offset.y

        val outW = if (targetAspectRatio >= 1f) 1200 else 1000
        val outH = (outW / targetAspectRatio).toInt().coerceAtLeast(1)
        val output = Bitmap.createBitmap(outW, outH, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(output)
        canvas.drawColor(AndroidColor.WHITE)

        val outputScaleX = outW / frameW
        val outputScaleY = outH / frameH

        val matrix = android.graphics.Matrix().apply {
            postScale(totalScale * outputScaleX, totalScale * outputScaleY)
            postTranslate(
                (imageLeft - frameLeft) * outputScaleX,
                (imageTop - frameTop) * outputScaleY
            )
        }

        canvas.save()
        canvas.clipRect(0f, 0f, outW.toFloat(), outH.toFloat())
        canvas.drawBitmap(bitmap, matrix, android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG))
        canvas.restore()

        val dir = File(context.filesDir, subfolder).apply { mkdirs() }
        val outFile = File(dir, "${prefix}_crop_${System.currentTimeMillis()}.jpg")
        FileOutputStream(outFile).use { out ->
            output.compress(Bitmap.CompressFormat.JPEG, 94, out)
        }

        output.recycle()
        bitmap.recycle()

        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            outFile
        ).toString()
    }.getOrNull()
}

@Composable
private fun CropImageDialog(
    sourceUri: String,
    circularPreview: Boolean,
    targetAspectRatio: Float,
    outputSubfolder: String,
    outputPrefix: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var viewport by remember { mutableStateOf(IntSize.Zero) }
    var saving by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Fotoğrafı çerçeveye göre ayarla") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Fotoğrafın tamamı gösterilir. Tek parmakla kaydırın, iki parmakla büyütüp küçültün.")
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(360.dp)
                        .onSizeChanged { viewport = it }
                        .pointerInput(Unit) {
                            detectTransformGestures { _, pan, zoom, _ ->
                                scale = (scale * zoom).coerceIn(0.5f, 8f)
                                offset += pan
                            }
                        }
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(sourceUri),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer(
                                scaleX = scale,
                                scaleY = scale,
                                translationX = offset.x,
                                translationY = offset.y
                            )
                    )

                    Canvas(Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height
                        val aspect = targetAspectRatio
                        val frameW: Float
                        val frameH: Float
                        if (w / h > aspect) {
                            frameH = h * 0.86f
                            frameW = frameH * aspect
                        } else {
                            frameW = w * 0.86f
                            frameH = frameW / aspect
                        }
                        val left = (w - frameW) / 2f
                        val top = (h - frameH) / 2f

                        drawRect(Color.Black.copy(alpha = 0.48f), Offset.Zero, androidx.compose.ui.geometry.Size(w, top))
                        drawRect(Color.Black.copy(alpha = 0.48f), Offset(0f, top + frameH), androidx.compose.ui.geometry.Size(w, h - top - frameH))
                        drawRect(Color.Black.copy(alpha = 0.48f), Offset(0f, top), androidx.compose.ui.geometry.Size(left, frameH))
                        drawRect(Color.Black.copy(alpha = 0.48f), Offset(left + frameW, top), androidx.compose.ui.geometry.Size(w - left - frameW, frameH))
                        if (circularPreview) {
                            drawCircle(
                                color = Color.White,
                                radius = min(frameW, frameH) / 2f,
                                center = Offset(left + frameW / 2f, top + frameH / 2f),
                                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 5f)
                            )
                        } else {
                            drawRect(
                                color = Color.White,
                                topLeft = Offset(left, top),
                                size = androidx.compose.ui.geometry.Size(frameW, frameH),
                                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 5f)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                enabled = !saving && viewport != IntSize.Zero,
                onClick = {
                    saving = true
                    val result = cropAndSaveImage(
                        context = context,
                        sourceUri = sourceUri,
                        scale = scale,
                        offset = offset,
                        viewport = viewport,
                        targetAspectRatio = targetAspectRatio,
                        subfolder = outputSubfolder,
                        prefix = outputPrefix
                    )
                    saving = false
                    if (result != null) onConfirm(result)
                }
            ) { Text(if (saving) "Kaydediliyor…" else "Kırp ve kullan") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("İptal") }
        }
    )
}
