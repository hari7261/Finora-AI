package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AiMessageEntity
import com.example.ui.MainViewModel
import com.example.ui.components.FinoraHeaderBar
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun AiAdvisorScreen(
    viewModel: MainViewModel,
    aiMessages: List<AiMessageEntity>,
    isThinking: Boolean,
    modifier: Modifier = Modifier
) {
    var promptInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val suggestedPrompts = listOf(
        "How can I save more based on my records?",
        "Analyse my income vs expenses",
        "How can I reach my financial goals?",
        "Predict my cash flow for next month",
        "Am I overspending in any category?"
    )

    LaunchedEffect(aiMessages.size, isThinking) {
        if (aiMessages.isNotEmpty()) {
            listState.animateScrollToItem(aiMessages.size - 1)
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("ai_advisor_screen"),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = BackgroundFinora,
        topBar = {
            FinoraHeaderBar(
                title = "Ask Finora AI",
                subtitle = "Powered by Gemini 3.5 Flash (Live CFO Advisor)",
                actionIcon = Icons.Default.Psychology,
                onActionClick = {}
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Messages Conversation List
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(aiMessages) { msg ->
                    if (msg.isUser) {
                        UserMessageCard(prompt = msg.prompt)
                    } else {
                        AiResponseCard(message = msg)
                    }
                }

                if (isThinking) {
                    item {
                        ThinkingIndicatorCard()
                    }
                }
            }

            // Bottom Prompt Input Box & Suggestions
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .padding(bottom = 80.dp)
            ) {
                // Prompt Chips Row
                Text(
                    text = "Suggested Prompts",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(suggestedPrompts) { prompt ->
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {
                                    promptInput = prompt
                                    viewModel.askAi(prompt)
                                    promptInput = ""
                                }
                                .testTag("prompt_chip_$prompt"),
                            color = SecondaryFinora.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderFinora)
                        ) {
                            Text(
                                text = prompt,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = PrimaryFinora
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Input Field
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = promptInput,
                        onValueChange = { promptInput = it },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("ai_prompt_input"),
                        placeholder = { Text("Ask Finora CFO about your money...", fontSize = 13.sp) },
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = OnBackgroundFinora,
                            unfocusedTextColor = OnBackgroundFinora,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = PrimaryFinora,
                            unfocusedBorderColor = CardBorderFinora,
                            focusedPlaceholderColor = Color.Gray,
                            unfocusedPlaceholderColor = Color.Gray,
                            cursorColor = AccentFinora
                        ),
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            if (promptInput.isNotBlank()) {
                                viewModel.askAi(promptInput)
                                promptInput = ""
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(if (promptInput.isNotBlank()) AccentFinora else PrimaryFinora.copy(alpha = 0.4f))
                            .testTag("send_ai_prompt_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UserMessageCard(prompt: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Card(
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 4.dp, bottomStart = 20.dp, bottomEnd = 20.dp),
            colors = CardDefaults.cardColors(containerColor = PrimaryFinora),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Text(
                text = prompt,
                modifier = Modifier.padding(14.dp),
                fontSize = 14.sp,
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun AiResponseCard(message: AiMessageEntity) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(24.dp))
            .border(1.dp, CardBorderFinora, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(AccentFinora),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Finora AI Advisor",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryFinora
                    )
                }

                Surface(
                    color = Color(0xFFE8F5E9),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "${message.confidencePercent}% Confidence",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Summary Block
            Text(
                text = message.summary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryFinora,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Reason Block
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = BackgroundFinora,
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Financial Reasoning",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SecondaryFinora
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = message.reason,
                        fontSize = 12.sp,
                        color = Color.DarkGray,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Suggestion Block
            Text(
                text = "Recommendation:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryFinora
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = message.suggestion,
                fontSize = 13.sp,
                color = Color.Black,
                lineHeight = 19.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Action Button
            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = AccentFinora),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = message.actionText,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
fun ThinkingIndicatorCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = AccentFinora,
                strokeWidth = 2.5.dp
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Finora AI is thinking deeply...",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryFinora
                )
                Text(
                    text = "Analyzing cash flows, emergency reserves, and budget limits",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
        }
    }
}
