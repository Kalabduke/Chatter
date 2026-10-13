package com.example.chatter.feature.conversationscreen

import android.app.AlertDialog
import android.app.Application
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import com.elvishew.xlog.XLog
import com.example.chatter.MainActivity
import com.example.chatter.R
import com.example.chatter.appID
import com.example.chatter.appSign
import com.example.chatter.zegoUserIdFor
import com.google.firebase.auth.FirebaseAuth
import com.zegocloud.zimkit.common.ZIMKitRouter
import com.zegocloud.zimkit.common.enums.ZIMKitConversationType
import com.zegocloud.zimkit.components.conversation.ui.ZIMKitConversationFragment
import com.zegocloud.zimkit.services.ZIMKit
import com.zegocloud.zimkit.services.ZIMKitConfig

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationScreen(activity: FragmentActivity, navController: NavController) {
    val fragment = remember { ZIMKitConversationFragment() }
    val fragmentManager = activity.supportFragmentManager
    var showChatInput by remember { mutableStateOf(false) }
    var targetEmail by remember { mutableStateOf(TextFieldValue()) }
    var showMenu by remember { mutableStateOf(false) }
    var isInitialized by remember { mutableStateOf(false) }
    var connectionError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            // The session has ended. Send the user back to sign in instead of leaving
            // them on a chat screen that can never load anything.
            navController.navigate("login") {
                popUpTo(0) { inclusive = true }
            }
            return@LaunchedEffect
        }

        val displayName = user.email ?: user.uid
        try {
            ZIMKit.initWith(
                activity.applicationContext as Application?, appID, appSign, ZIMKitConfig()
            )

            // The conversation list can render as soon as the SDK is initialised - it
            // shows its own empty state while the user is still connecting. Previously
            // this flag was only set on a *successful* connection, so any failure left
            // the screen permanently blank with nothing on it and no explanation.
            isInitialized = true

            ZIMKit.connectUser(
                zegoUserIdFor(displayName),
                displayName,
                "android.resource://${activity.packageName}/drawable/images"
            ) { error ->
                if (error.code.value() == 0) {
                    Log.d("ZIMKit", "Connected successfully")
                    connectionError = null
                    (activity as? MainActivity)?.initZegoInviteService(
                        appID, appSign, zegoUserIdFor(displayName), displayName
                    )
                } else {
                    Log.e("ZIMKit", "connectUser failed with code ${error.code.value()}")
                    connectionError =
                        "Couldn't connect to chat (error ${error.code.value()}). " +
                            "Check your connection, then sign out and back in."
                }
            }

            ZIMKit.initNotifications()
            XLog.init()
        } catch (e: Exception) {
            Log.e("ZIMKit", "Error in initialization", e)
            connectionError = "Chat failed to start: ${e.message}"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Chatter",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                },
                actions = {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, "More")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Profile") },
                            onClick = {
                                showMenu = false
                                showProfileDialog(activity)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Voice and Video Call") },
                            onClick = {
                                showMenu = false
                                navController.navigate("home")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Logout") },
                            onClick = {
                                showMenu = false
                                showLogoutConfirmationDialog(activity, navController)
                            }
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showChatInput = !showChatInput },
                content = {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Chat")
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            connectionError?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            if (isInitialized) {
                AndroidView(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(top = 2.dp),
                    factory = { context ->
                        FrameLayout(context).apply {
                            id = View.generateViewId()
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                        }
                    },
                    update = { frameLayout ->
                        if (fragment.isAdded.not()) {
                            fragmentManager.beginTransaction()
                                .replace(frameLayout.id, fragment)
                                .commitAllowingStateLoss()
                        }
                    }
                )
            }

            if (showChatInput) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TextField(
                        value = targetEmail,
                        onValueChange = { targetEmail = it },
                        label = { Text("Enter Email to Chat") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Button(onClick = {
                        if (isValidEmail(targetEmail.text)) {
                            startChat(activity, targetEmail.text)
                            showChatInput = false
                        } else {
                            Toast.makeText(
                                activity,
                                "Please enter a valid Email.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }) {
                        Text("Start Chat")
                    }
                }
            }
        }
    }
}

fun startChat(activity: FragmentActivity, targetEmail: String) {
    ZIMKitRouter.toMessageActivity(
        activity,
        zegoUserIdFor(targetEmail),
        ZIMKitConversationType.ZIMKitConversationTypePeer
    )
}

fun isValidEmail(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

fun showProfileDialog(activity: FragmentActivity) {
    FirebaseAuth.getInstance().currentUser?.let { user ->
        AlertDialog.Builder(activity).apply {
            setTitle("Profile")
            setMessage("Email: ${user.email}")
            setIcon(R.drawable.images)
            setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            show()
        }
    }
}

fun showLogoutConfirmationDialog(activity: FragmentActivity, navController: NavController) {
    AlertDialog.Builder(activity).apply {
        setTitle("Log Out")
        setMessage("Are you sure you want to log out?")
        setPositiveButton("Yes") { _, _ ->
            FirebaseAuth.getInstance().signOut()
            ZIMKit.disconnectUser()
            Toast.makeText(activity, "Logged out successfully!", Toast.LENGTH_SHORT).show()
            navController.navigate("login") {
                popUpTo(0) { inclusive = true }
            }
        }
        setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        show()
    }
}
