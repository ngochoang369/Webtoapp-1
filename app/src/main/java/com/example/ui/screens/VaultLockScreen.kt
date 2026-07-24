package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.VaultDarkBg
import com.example.ui.theme.VaultPrimary
import com.example.ui.theme.VaultSurfaceDark

@Composable
fun VaultLockScreen(
    isSettingNewPin: Boolean = false,
    onVerifyPin: (String) -> Boolean = { false },
    onSetNewPin: (String) -> Unit = {}
) {
    var enteredPin by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var stepTitle by remember { mutableStateOf(if (isSettingNewPin) "Thiết lập PIN Vault mới" else "Nhập PIN Khóa Vault") }

    fun handleKeyPress(key: String) {
        if (enteredPin.length < 4) {
            val newPin = enteredPin + key
            enteredPin = newPin
            errorMessage = ""
            if (newPin.length == 4) {
                if (isSettingNewPin) {
                    onSetNewPin(newPin)
                } else {
                    val success = onVerifyPin(newPin)
                    if (!success) {
                        errorMessage = "Mã PIN không đúng, vui lòng thử lại!"
                        enteredPin = ""
                    }
                }
            }
        }
    }

    fun handleBackspace() {
        if (enteredPin.isNotEmpty()) {
            enteredPin = enteredPin.dropLast(1)
            errorMessage = ""
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .testTag("vault_lock_screen"),
        color = VaultDarkBg
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(VaultSurfaceDark)
                    .border(2.dp, VaultPrimary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Vault Lock",
                    tint = VaultPrimary,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "PrivaDiary Vault",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stepTitle,
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFFDDD6FE)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // PIN Indicator Dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(4) { index ->
                    val isFilled = index < enteredPin.length
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(if (isFilled) VaultPrimary else VaultSurfaceDark)
                            .border(
                                1.5.dp,
                                if (isFilled) VaultPrimary else Color(0xFF4C1D95),
                                CircleShape
                            )
                    )
                }
            }

            AnimatedVisibility(visible = errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Keypad Grid
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val keyRows = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9"),
                    listOf("", "0", "DEL")
                )

                keyRows.forEach { row ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        row.forEach { key ->
                            if (key.isEmpty()) {
                                Spacer(modifier = Modifier.size(68.dp))
                            } else if (key == "DEL") {
                                Box(
                                    modifier = Modifier
                                        .size(68.dp)
                                        .clip(CircleShape)
                                        .background(VaultSurfaceDark)
                                        .clickable { handleBackspace() }
                                        .testTag("keypad_del"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Backspace,
                                        contentDescription = "Xóa PIN",
                                        tint = Color.LightGray
                                    )
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(68.dp)
                                        .clip(CircleShape)
                                        .background(VaultSurfaceDark)
                                        .border(1.dp, Color(0xFF382C54), CircleShape)
                                        .clickable { handleKeyPress(key) }
                                        .testTag("keypad_$key"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = key,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }

        }
    }
}
