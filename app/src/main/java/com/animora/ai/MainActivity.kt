package com.animora.ai

import android.os.Bundle
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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

    var selectedVideoUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        selectedVideoUri = uri
    }

    val navy = Color(0xFF080D20)
    val purple = Color(0xFF873CFF)
    val cyan = Color(0xFF00CFFF)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(navy)
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
            Text("＋  Upload Video", fontSize = 19.sp)if (selectedVideoUri != null) {
    Text(
        "Video selected successfully!",
        color = cyan,
        fontSize = 15.sp
    )
}
        }

        Spacer(Modifier.height(32.dp))

        Text(
            "Choose Your Style",
            color = Color.White,
            fontSize = 21.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(15.dp))

        listOf("2D Cartoon", "3D Animation").forEach {
            Card(
                onClick = { selectedStyle = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 7.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (selectedStyle == it)
                        Color(0xFF302052)
                    else Color(0xFF17203A)
                ),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text(
                    text = if (it == "2D Cartoon")
                        "🎨   2D Cartoon"
                    else "🎬   3D Animation",
                    color = Color.White,
                    fontSize = 19.sp,
                    modifier = Modifier.padding(22.dp)
                )
            }
        }

        Spacer(Modifier.height(30.dp))

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
