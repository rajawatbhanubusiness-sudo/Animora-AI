package com.animora.ai

import android.os.Bundle
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AnimoraHome()
        }
    }
}

@Composable
fun AnimoraHome() {
     var selectedStyle by remember {
        mutableStateOf("2D Cartoon")
    }
var customStylePrompt by remember {
    mutableStateOf("")
}
    var selectedVideoUri by remember {
        mutableStateOf<Uri?>(null)
    }
var selectedAudioUri by remember {
    mutableStateOf<Uri?>(null)
}
var audioDurationMs by remember {
    mutableStateOf<Long?>(null)
}
var extractedFrameCount by remember {
    mutableIntStateOf(0)
}
var extractedFrames by remember {
    mutableStateOf<List<Bitmap>>(emptyList())
}

var isProcessing by remember {
    mutableStateOf(false)
}

    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        selectedVideoUri = uri
    }
val audioPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.GetContent()
) { uri ->
    selectedAudioUri = uri
}
val context = LocalContext.current
val scope = rememberCoroutineScope()
LaunchedEffect(selectedAudioUri) {
    val audioUri = selectedAudioUri

    audioDurationMs = if (audioUri != null) {
        withContext(Dispatchers.IO) {
            AudioProcessor.getAudioDurationMs(context, audioUri)
        }
    } else {
        null
    }
}

    val navy = Color(0xFF080D20)
    val purple = Color(0xFF873CFF)
    val cyan = Color(0xFF00CFFF)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(navy)
            .verticalScroll(rememberScrollState())
            .padding(22.dp)
    ) {
        Text(
            "ANIMORA AI",
            color = cyan,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(35.dp))

        Text(
            "Welcome Back!",
            color = Color.White,
            fontSize = 29.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            "Turn your videos into animations",
            color = Color.LightGray,
            fontSize = 15.sp
        )

        Spacer(Modifier.height(30.dp))

        Button(
            onClick = {
    videoPickerLauncher.launch(
        PickVisualMediaRequest(
            ActivityResultContracts.PickVisualMedia.VideoOnly
        )
    )
},
            modifier = Modifier
                .fillMaxWidth()
                .height(62.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = purple
            ),
            shape = RoundedCornerShape(18.dp)
        ) {
            Text("＋  Upload Video", fontSize = 19.sp)
}

if (selectedVideoUri != null) {
    Text(
        "Video selected successfully!",
        color = cyan,
        fontSize = 15.sp
    )
}
if (selectedStyle.contains("Music-to-AI Animation")) {
    Spacer(Modifier.height(16.dp))

    Button(
        onClick = {
            audioPickerLauncher.launch("audio/*")
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("🎵 Upload Song")
    }

    if (selectedAudioUri != null) {
        Text(
            "Song selected successfully!",
            color = cyan
        )
    }
}
if (audioDurationMs != null) {
    Text(
        "Song duration: ${audioDurationMs!! / 1000} seconds",
        color = cyan
    )
}
Button(
    onClick = {
        val videoUri = selectedVideoUri
        if (videoUri != null) {
            scope.launch {
                isProcessing = true
                val frames = withContext(Dispatchers.IO) {
    VideoProcessor.extractFrames(context, videoUri)
}
val processedFrames = withContext(Dispatchers.Default) {
    frames.map { frame ->
        ImageStylizer.applyStyle(frame, selectedStyle)
    }
}

extractedFrames = processedFrames
extractedFrameCount = processedFrames.size
                isProcessing = false
            }
        }
    },
    enabled = selectedVideoUri != null && !isProcessing
) {
    Text(if (isProcessing) "Preparing Video..." else "Prepare Video")
    }
if (isProcessing) {
    Spacer(Modifier.height(12.dp))

    CircularProgressIndicator(
        color = cyan,
        modifier = Modifier.align(Alignment.CenterHorizontally)
    )

    Text(
        "Preparing your video...",
        color = cyan,
        modifier = Modifier.align(Alignment.CenterHorizontally)
    )
}

if (extractedFrames.isNotEmpty()) {
    Spacer(Modifier.height(16.dp))

    Text(
        "Processed Frame Preview",
        color = cyan,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold
    )

    Spacer(Modifier.height(12.dp))

    Image(
        bitmap = extractedFrames.first().asImageBitmap(),
        contentDescription = "First processed video frame",
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 280.dp),
        contentScale = ContentScale.Fit
    )
}

        Spacer(Modifier.height(32.dp))

        Text(
            "Choose Your Style",
            color = Color.White,
            fontSize = 21.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(15.dp))
val animationStyles = listOf(
    "🎌 Anime",
    "🎬 3D Cartoon",
    "🎨 2D Cartoon",
    "🖌️ Watercolor",
    "🧱 Clay Animation",
    "💥 Comic Book",
    "👾 Pixel Art",
    "🎥 Realistic Animation",
    "🧙 Fantasy Animation",
    "📺 Vintage Cartoon",
    "✂️ Paper Cutout",
    "🌃 Cyberpunk",
    "🎵 Music-to-AI Animation"
)

animationStyles.forEach { style ->
    Card(
        onClick = { selectedStyle = style },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selectedStyle == style)
                Color(0xFF302052)
            else Color(0xFF17203A)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = style,
            color = Color.White,
            fontSize = 17.sp,
            modifier = Modifier.padding(18.dp)
        )
    }
}
        Spacer(Modifier.height(30.dp))
Text(
    "✨ Custom Animation Style",
    color = Color.White,
    fontSize = 21.sp,
    fontWeight = FontWeight.Bold
)

Spacer(Modifier.height(12.dp))

OutlinedTextField(
    value = customStylePrompt,
    onValueChange = { customStylePrompt = it },
    placeholder = {
        Text("Describe your own animation style...")
    },
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp)
)
        Text(
            "Recent Projects",
            color = Color.White,
            fontSize = 21.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(12.dp))

        Text(
            "Your projects will appear here.",
            color = Color.LightGray
        )

        Spacer(Modifier.weight(1f))

        Text(
            "HOME       PROJECTS       PROFILE",
            color = cyan,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}
