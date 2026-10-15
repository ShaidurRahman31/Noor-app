package com.example.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.DuaCategory
import com.example.data.model.DuaItem
import com.example.ui.theme.IslamicGoldBadge
import com.example.ui.theme.VerifiedGreenBadge
import com.example.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminReviewScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("noor_editor_security", Context.MODE_PRIVATE) }

    var isUnlocked by remember { mutableStateOf(false) }
    var inputPin by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf(false) }
    var isPinVisible by remember { mutableStateOf(false) }
    var showChangePinDialog by remember { mutableStateOf(false) }

    val allDuas by viewModel.allDuasForAdmin.collectAsState()
    val categories by viewModel.categories.collectAsState()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("সকল এন্ট্রি", "যাচাইকৃত (Verified)", "পর্যালোচিত (Reviewed)", "অপেক্ষমান (Pending)")

    var searchQuery by remember { mutableStateOf("") }
    var editingDua by remember { mutableStateOf<DuaItem?>(null) }
    var isAddingNewDua by remember { mutableStateOf(false) }
    var duaToDelete by remember { mutableStateOf<DuaItem?>(null) }

    val savedPin = prefs.getString("editor_secret_pin", "7860") ?: "7860"

    // If locked: display the secure passcode barrier
    if (!isUnlocked) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "সম্পাদক সুরক্ষা গেটওয়ে",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            },
            modifier = modifier.testTag("admin_lock_screen")
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Security Lock",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(42.dp)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "শুধুমাত্র অনুমোদিত সম্পাদকের জন্য",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "পবিত্র দোয়ার বিশুদ্ধতা ও ধর্মীয় রেফারেন্স রক্ষা করতে এই রিভিউ ও এডিটর প্যানেলটি সুরক্ষিত। সাধারণ পাঠকরা এতে পরিবর্তন করতে পারবে না। শুধুমাত্র গোপন পিন দিয়ে সম্পাদক এতে প্রবেশ করতে পারবেন।",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        OutlinedTextField(
                            value = inputPin,
                            onValueChange = {
                                inputPin = it
                                pinError = false
                            },
                            label = { Text("গোপন সম্পাদক পিন (PIN)") },
                            visualTransformation = if (isPinVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            trailingIcon = {
                                IconButton(onClick = { isPinVisible = !isPinVisible }) {
                                    Icon(
                                        imageVector = if (isPinVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = null
                                    )
                                }
                            },
                            isError = pinError,
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("admin_pin_input")
                        )

                        if (pinError) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "ভুল পিন কোড! পুনরায় চেষ্টা করুন।",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                if (inputPin == savedPin) {
                                    isUnlocked = true
                                    inputPin = ""
                                    pinError = false
                                    Toast.makeText(context, "সম্পাদক প্যানেল সফলভাবে আনলক হয়েছে", Toast.LENGTH_SHORT).show()
                                } else {
                                    pinError = true
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("admin_unlock_btn")
                        ) {
                            Icon(imageVector = Icons.Default.LockOpen, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("আনলক করুন ও প্রবেশ করুন")
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                        ) {
                            Text(
                                text = "💡 প্রাথমিক ডিফল্ট পিন: 7860 (প্রবেশের পর পরিবর্তনযোগ্য)",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }
        return
    }

    // Unlocked State: Full Admin / Editorial Power
    val filteredList = allDuas.filter { dua ->
        val matchesTab = when (selectedTabIndex) {
            1 -> dua.verificationStatus == "VERIFIED"
            2 -> dua.verificationStatus == "EDITORIAL_REVIEWED"
            3 -> dua.verificationStatus == "PENDING_REVIEW"
            else -> true
        }
        val matchesSearch = if (searchQuery.isBlank()) true else {
            val q = searchQuery.trim().lowercase()
            dua.titleBn.lowercase().contains(q) ||
            dua.reference.lowercase().contains(q) ||
            dua.arabicText.contains(q)
        }
        matchesTab && matchesSearch
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "ধর্মীয় মান নিয়ন্ত্রণ ও এডিটর প্যানেল",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "লগইন অবস্থা: অনুমোদিত সম্পাদক (${allDuas.size} টি এন্ট্রি)",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }
                },
                actions = {
                    // Change PIN action
                    IconButton(onClick = { showChangePinDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Key,
                            contentDescription = "Change PIN",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    // Reset Database
                    IconButton(onClick = {
                        viewModel.resetStarterData()
                        Toast.makeText(context, "ডাটাবেজ পুনরায় প্রাথমিক অবস্থায় রিসেট হয়েছে", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Restore,
                            contentDescription = "Reset Database",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    // Lock Session
                    IconButton(onClick = {
                        isUnlocked = false
                        Toast.makeText(context, "এডিটর সেশন লক করা হয়েছে", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Lock Editor",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { isAddingNewDua = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                modifier = Modifier
                    .padding(bottom = 60.dp)
                    .testTag("admin_add_dua_fab")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add New Dua")
            }
        },
        modifier = modifier.testTag("admin_review_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Tabs
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                edgePadding = 16.dp,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(text = title, fontSize = 13.sp) }
                    )
                }
            }

            // Search
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("দোয়ার নাম বা রেফারেন্স দিয়ে খুঁজুন…") },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            )

            // Dua List for Admin
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 120.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredList, key = { it.id }) { dua ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Status Badge
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = when (dua.verificationStatus) {
                                        "VERIFIED" -> VerifiedGreenBadge.copy(alpha = 0.15f)
                                        "EDITORIAL_REVIEWED" -> IslamicGoldBadge.copy(alpha = 0.15f)
                                        else -> MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
                                    },
                                    contentColor = when (dua.verificationStatus) {
                                        "VERIFIED" -> VerifiedGreenBadge
                                        "EDITORIAL_REVIEWED" -> IslamicGoldBadge
                                        else -> MaterialTheme.colorScheme.error
                                    }
                                ) {
                                    Text(
                                        text = when (dua.verificationStatus) {
                                            "VERIFIED" -> "✓ সম্পূর্ণ যাচাইকৃত (Verified)"
                                            "EDITORIAL_REVIEWED" -> "◉ পরীক্ষিত (Reviewed)"
                                            else -> "⏳ অপেক্ষমান (Pending Review)"
                                        },
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }

                                Row {
                                    IconButton(onClick = { editingDua = dua }) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Edit",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    IconButton(onClick = { duaToDelete = dua }) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = dua.titleBn,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "সূত্র: ${dua.reference} | মান: ${dua.authenticityGrade}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "রিভিউ মন্তব্য: ${dua.reviewerNotes}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Fast status toggle buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (dua.verificationStatus != "VERIFIED") {
                                    OutlinedButton(
                                        onClick = {
                                            viewModel.updateDuaVerificationStatus(dua, "VERIFIED", "সম্পাদক পরিষদ কর্তৃক যাচাই সম্পন্ন")
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(8.dp),
                                        border = BorderStroke(1.dp, VerifiedGreenBadge)
                                    ) {
                                        Text("যাচাইকৃত করুন", fontSize = 11.sp, color = VerifiedGreenBadge)
                                    }
                                }
                                if (dua.verificationStatus != "PENDING_REVIEW") {
                                    OutlinedButton(
                                        onClick = {
                                            viewModel.updateDuaVerificationStatus(dua, "PENDING_REVIEW", "পুনঃপর্যালোচনার জন্য অপেক্ষমান")
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(8.dp),
                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                                    ) {
                                        Text("অপেক্ষমানে নিন", fontSize = 11.sp, color = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Delete Confirmation Dialog
    duaToDelete?.let { dua ->
        AlertDialog(
            onDismissRequest = { duaToDelete = null },
            title = { Text("এন্ট্রি অপসারণ করবেন?") },
            text = { Text("'${dua.titleBn}' দোয়াটি কি মুছে ফেলতে চান?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteDua(dua)
                        duaToDelete = null
                        Toast.makeText(context, "দোয়া মুছে ফেলা হয়েছে", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("মুছে ফেলুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { duaToDelete = null }) {
                    Text("বাতিল")
                }
            }
        )
    }

    // Change PIN Dialog
    if (showChangePinDialog) {
        var newPin by remember { mutableStateOf("") }
        var confirmPin by remember { mutableStateOf("") }
        var errorMsg by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showChangePinDialog = false },
            title = { Text("নতুন সম্পাদক পিন নির্ধারণ") },
            text = {
                Column {
                    Text("ভবিষ্যতে শুধুমাত্র আপনি যাতে এডিটর অ্যাক্সেস করতে পারেন, সেজন্য নতুন গোপন ৪-সংখ্যার পিন দিন:")
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = newPin,
                        onValueChange = { newPin = it },
                        label = { Text("নতুন পিন") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = confirmPin,
                        onValueChange = { confirmPin = it },
                        label = { Text("পিন নিশ্চিত করুন") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (errorMsg.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = errorMsg, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (newPin.length < 4) {
                        errorMsg = "পিন কমপক্ষে ৪ সংখ্যার হতে হবে"
                    } else if (newPin != confirmPin) {
                        errorMsg = "দুই পিন মেলেনি"
                    } else {
                        prefs.edit().putString("editor_secret_pin", newPin).apply()
                        showChangePinDialog = false
                        Toast.makeText(context, "নতুন পিন সফলভাবে সংরক্ষিত হয়েছে", Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Text("সংরক্ষণ করুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { showChangePinDialog = false }) {
                    Text("বাতিল")
                }
            }
        )
    }

    // Add or Edit Dialog
    if (isAddingNewDua || editingDua != null) {
        val initialDua = editingDua
        AdminDuaEditorDialog(
            dua = initialDua,
            categories = categories,
            onDismiss = {
                isAddingNewDua = false
                editingDua = null
            },
            onSave = { updatedDua ->
                viewModel.saveOrUpdateDua(updatedDua, isNew = (initialDua == null))
                isAddingNewDua = false
                editingDua = null
                Toast.makeText(context, if (initialDua == null) "নতুন দোয়া যুক্ত হয়েছে" else "দোয়ার তথ্য সংরক্ষিত হয়েছে", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDuaEditorDialog(
    dua: DuaItem?,
    categories: List<DuaCategory>,
    onDismiss: () -> Unit,
    onSave: (DuaItem) -> Unit
) {
    var selectedCategoryId by remember { mutableStateOf(dua?.categoryId ?: categories.firstOrNull()?.id ?: "morning_evening") }
    var titleBn by remember { mutableStateOf(dua?.titleBn ?: "") }
    var titleEn by remember { mutableStateOf(dua?.titleEn ?: "") }
    var arabicText by remember { mutableStateOf(dua?.arabicText ?: "") }
    var transliterationBn by remember { mutableStateOf(dua?.transliterationBn ?: "") }
    var meaningBn by remember { mutableStateOf(dua?.meaningBn ?: "") }
    var sourceType by remember { mutableStateOf(dua?.sourceType ?: "HADITH") }
    var reference by remember { mutableStateOf(dua?.reference ?: "") }
    var referenceEdition by remember { mutableStateOf(dua?.referenceEdition ?: "") }
    var authenticityGrade by remember { mutableStateOf(dua?.authenticityGrade ?: "সহীহ (Sahih)") }
    var contextOrOccasion by remember { mutableStateOf(dua?.contextOrOccasion ?: "") }
    var repeats by remember { mutableIntStateOf(dua?.recommendedRepeats ?: 1) }
    var verificationStatus by remember { mutableStateOf(dua?.verificationStatus ?: "VERIFIED") }
    var reviewerNotes by remember { mutableStateOf(dua?.reviewerNotes ?: "যাচাইকৃত ইসলামিক রেফারেন্স অনুযায়ী পরীক্ষিত") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = if (dua == null) "নতুন দোয়া যুক্ত করুন" else "দোয়ার তথ্য ও রেফারেন্স সম্পাদনা",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Title Bn
                OutlinedTextField(
                    value = titleBn,
                    onValueChange = { titleBn = it },
                    label = { Text("দোয়ার শিরোনাম (বাংলা)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Title En
                OutlinedTextField(
                    value = titleEn,
                    onValueChange = { titleEn = it },
                    label = { Text("Title (English)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Arabic Text
                OutlinedTextField(
                    value = arabicText,
                    onValueChange = { arabicText = it },
                    label = { Text("মূল আরবি পাঠ্য (হরকত সহ)") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Transliteration
                OutlinedTextField(
                    value = transliterationBn,
                    onValueChange = { transliterationBn = it },
                    label = { Text("বাংলা উচ্চারণ") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Meaning
                OutlinedTextField(
                    value = meaningBn,
                    onValueChange = { meaningBn = it },
                    label = { Text("বাংলা অর্থ ও অনুবাদ") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Reference
                OutlinedTextField(
                    value = reference,
                    onValueChange = { reference = it },
                    label = { Text("রেফারেন্স (যেমন: সহীহ মুসলিম ৭১৩ / সূরা বাকারা ২:২০১)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Reference Edition
                OutlinedTextField(
                    value = referenceEdition,
                    onValueChange = { referenceEdition = it },
                    label = { Text("গ্রন্থ সংস্করণ / দারুসসালাম / ইফা নম্বর") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Authenticity Grade
                OutlinedTextField(
                    value = authenticityGrade,
                    onValueChange = { authenticityGrade = it },
                    label = { Text("হাদিসের মান (সহীহ / হাসান / কুরআনিক আয়াত)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Context
                OutlinedTextField(
                    value = contextOrOccasion,
                    onValueChange = { contextOrOccasion = it },
                    label = { Text("ফজিলত ও আমলের সময়") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Reviewer Notes
                OutlinedTextField(
                    value = reviewerNotes,
                    onValueChange = { reviewerNotes = it },
                    label = { Text("রিভিউয়ার নোট ও যাচাই বিবরণ") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("বাতিল")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (titleBn.isNotBlank() && arabicText.isNotBlank()) {
                                val newId = dua?.id ?: "dua_custom_${System.currentTimeMillis()}"
                                val savedDua = DuaItem(
                                    id = newId,
                                    categoryId = selectedCategoryId,
                                    titleBn = titleBn.trim(),
                                    titleEn = titleEn.trim(),
                                    arabicText = arabicText.trim(),
                                    transliterationBn = transliterationBn.trim(),
                                    meaningBn = meaningBn.trim(),
                                    sourceType = if (reference.contains("সূরা") || reference.contains("কুরআন")) "QURAN" else "HADITH",
                                    reference = reference.trim(),
                                    referenceEdition = referenceEdition.trim(),
                                    authenticityGrade = authenticityGrade.trim(),
                                    contextOrOccasion = contextOrOccasion.trim(),
                                    recommendedRepeats = repeats,
                                    isQuranic = reference.contains("সূরা") || reference.contains("কুরআন"),
                                    isProphetic = !reference.contains("সূরা"),
                                    verificationStatus = verificationStatus,
                                    reviewerNotes = reviewerNotes.trim()
                                )
                                onSave(savedDua)
                            }
                        }
                    ) {
                        Text("সংরক্ষণ করুন")
                    }
                }
            }
        }
    }
}
