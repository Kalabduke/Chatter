package com.example.chatter.feature.home

import android.view.View
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.chatter.MainActivity
import com.example.chatter.appID
import com.example.chatter.appSign
import com.example.chatter.zegoUserIdFor
import com.google.firebase.auth.FirebaseAuth
import com.zegocloud.uikit.prebuilt.call.invite.widget.ZegoSendCallInvitationButton
import com.zegocloud.uikit.service.defines.ZegoUIKitUser

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current as MainActivity
    val currentUser = FirebaseAuth.getInstance().currentUser
    var targetUserId by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        // The call service must know the same identity the chat session registered,
        // otherwise a call to this user never reaches the person who is signed in.
        currentUser?.let { user ->
            val displayName = user.email ?: user.uid
            context.initZegoInviteService(
                appID, appSign, zegoUserIdFor(displayName), displayName
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Voice and Video Call",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1E88E5))
            )
        },
        containerColor = Color(0xFFF0F2F5)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Text(
                    text = "Hello, ${currentUser?.email ?: "User"}!",
                    style = MaterialTheme.typography.headlineMedium.copy(fontSize = 18.sp, color = Color(0xFF1E88E5)),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "Voice and Video Call",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp, color = Color(0xFF424242)),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Start a Call",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFF424242)
                        )

                        OutlinedTextField(
                            value = targetUserId,
                            onValueChange = { 
                                targetUserId = it
                                showError = false 
                            },
                            label = { Text("Recipient Email") },
                            singleLine = true,
                            isError = showError,
                            supportingText = if (showError) {
                                { Text("Please enter a valid email") }
                            } else null,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            CallButton(
                                label = "Voice Call",
                                isVideoCall = false,
                                targetUserId = targetUserId
                            )
                            CallButton(
                                label = "Video Call",
                                isVideoCall = true,
                                targetUserId = targetUserId
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CallButton(label: String, isVideoCall: Boolean, targetUserId: String) {
    var zegoButton by remember { mutableStateOf<ZegoSendCallInvitationButton?>(null) }

    ElevatedButton(
        onClick = {
            if (targetUserId.isNotEmpty() && zegoButton != null) {
                zegoButton?.setInvitees(
                    listOf(ZegoUIKitUser(zegoUserIdFor(targetUserId), targetUserId))
                )
                zegoButton?.performClick()
            }
        },
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5)),
        modifier = Modifier
            .padding(4.dp)
            .size(width = 130.dp, height = 48.dp)
    ) {
        AndroidView(
            factory = { context ->
                ZegoSendCallInvitationButton(context).apply {
                    setIsVideoCall(isVideoCall)
                    resourceID = "zego_data"
                    visibility = View.INVISIBLE
                    zegoButton = this
                }
            },
            modifier = Modifier.size(0.dp)  // Hide the actual Zego button
        )
        
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.White,
            textAlign = TextAlign.Center
        )
    }
}
