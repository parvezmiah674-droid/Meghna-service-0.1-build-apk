package com.example.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PaymentRecord
import com.example.data.Technician
import com.example.ui.theme.BrandOnPrimaryContainer
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandPrimaryContainer
import com.example.ui.theme.FooterBarColor
import com.example.ui.theme.OutlineVariantLight
import com.example.ui.theme.SuccessGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    technicians: List<Technician>,
    payments: List<PaymentRecord>,
    selectedCategory: String,
    searchQuery: String,
    onCategorySelect: (String) -> Unit,
    onSearchChange: (String) -> Unit,
    onTechnicianClick: (Technician) -> Unit,
    onAddTechnician: (name: String, category: String, phone: String, address: String) -> Unit,
    onDeleteTechnician: (Technician) -> Unit
) {
    val context = LocalContext.current
    var showAddDialog by remember { mutableStateOf(false) }
    var showPaymentHistory by remember { mutableStateOf(false) }
    var showProfileInfo by remember { mutableStateOf(false) }
    var techToDelete by remember { mutableStateOf<Technician?>(null) }
    var currentTab by remember { mutableStateOf("home") }

    val categories = listOf(
        "সব সেবা",
        "ইলেকট্রিক ও স্যানিটারি",
        "সিসি ক্যামেরা",
        "সোলার",
        "আইপিএস",
        "এসি ও ফ্রিজ",
        "পেইন্টিং"
    )

    Scaffold(
        topBar = {
            // Professional Polish Header: bg-[#005FAF] text-white px-4 pt-10 pb-4 shadow-md
            Surface(
                color = BrandPrimary,
                shadowElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 40.dp, bottom = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Tool icon with bg-white/20
                        Box(
                            modifier = Modifier
                                .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Handyman,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "Meghna Service",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                lineHeight = 22.sp
                            )
                            Text(
                                text = "মেঘনা, কুমিল্লা",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 2.sp,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }

                    // Notification/Receipt action button with bg-white/10
                    IconButton(
                        onClick = { showPaymentHistory = true },
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.White.copy(alpha = 0.12f), CircleShape)
                            .testTag("payment_history_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "বিজ্ঞপ্তি ও পেমেন্ট হিস্ট্রি",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        },
        bottomBar = {
            // Professional Polish Footer: bg-[#F3F4F9] border-t border-[#C4C6D0] p-4 with center FAB
            Surface(
                color = FooterBarColor,
                border = BorderStroke(1.dp, OutlineVariantLight),
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(68.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Home Tab
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable { currentTab = "home" }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .testTag("nav_home")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = "হোম",
                                tint = if (currentTab == "home") BrandPrimary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                modifier = Modifier.size(22.dp)
                            )
                            Text(
                                text = "হোম",
                                fontSize = 10.sp,
                                fontWeight = if (currentTab == "home") FontWeight.Bold else FontWeight.Normal,
                                color = if (currentTab == "home") BrandPrimary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        }

                        // Order/Services Tab
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable {
                                    currentTab = "orders"
                                    onCategorySelect("সব সেবা")
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .testTag("nav_orders")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Folder,
                                contentDescription = "অর্ডার",
                                tint = if (currentTab == "orders") BrandPrimary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                modifier = Modifier.size(22.dp)
                            )
                            Text(
                                text = "অর্ডার",
                                fontSize = 10.sp,
                                fontWeight = if (currentTab == "orders") FontWeight.Bold else FontWeight.Normal,
                                color = if (currentTab == "orders") BrandPrimary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        }

                        // Placeholder spacer for center Add FAB
                        Spacer(modifier = Modifier.width(48.dp))

                        // Payment Tab
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable {
                                    currentTab = "payment"
                                    showPaymentHistory = true
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .testTag("nav_payment")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ReceiptLong,
                                contentDescription = "পেমেন্ট",
                                tint = if (currentTab == "payment") BrandPrimary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                modifier = Modifier.size(22.dp)
                            )
                            Text(
                                text = "পেমেন্ট",
                                fontSize = 10.sp,
                                fontWeight = if (currentTab == "payment") FontWeight.Bold else FontWeight.Normal,
                                color = if (currentTab == "payment") BrandPrimary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        }

                        // Profile / Info Tab
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable {
                                    currentTab = "profile"
                                    showProfileInfo = true
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .testTag("nav_profile")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "প্রোফাইল",
                                tint = if (currentTab == "profile") BrandPrimary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                modifier = Modifier.size(22.dp)
                            )
                            Text(
                                text = "প্রোফাইল",
                                fontSize = 10.sp,
                                fontWeight = if (currentTab == "profile") FontWeight.Bold else FontWeight.Normal,
                                color = if (currentTab == "profile") BrandPrimary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        }
                    }

                    // Floating Add Button positioned over the bottom bar
                    Surface(
                        onClick = { showAddDialog = true },
                        shape = RoundedCornerShape(16.dp),
                        color = BrandPrimary,
                        border = BorderStroke(3.dp, Color(0xFFFDFBFF)),
                        shadowElevation = 8.dp,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .offset(y = (-14).dp)
                            .size(52.dp)
                            .testTag("add_tech_fab")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "নতুন সেবা যোগ করুন",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFFDFBFF))
        ) {
            // Main Content Area with padding 16.dp and space-y-4
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("technicians_list")
            ) {
                // Promotional Banner: bg-[#D3E4FF] p-4 rounded-3xl flex items-center justify-between
                item {
                    Surface(
                        color = BrandPrimaryContainer,
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "দ্রুত সেবা প্রয়োজন?",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = BrandOnPrimaryContainer
                                )
                                Text(
                                    text = "নিকটস্থ মিস্ত্রি খুঁজুন এখনই",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = BrandOnPrimaryContainer.copy(alpha = 0.75f)
                                )
                            }

                            Button(
                                onClick = {
                                    // Focus on search or trigger add dialog
                                    showAddDialog = true
                                },
                                shape = RoundedCornerShape(50),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = BrandPrimary,
                                    contentColor = Color.White
                                ),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Text(
                                    text = "সার্চ করুন",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Search & Filter Card
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = onSearchChange,
                            placeholder = { Text("নাম, সেবা বা ফোন দিয়ে খুঁজুন...", fontSize = 13.sp) },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = null,
                                    tint = BrandPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { onSearchChange("") }) {
                                        Icon(Icons.Default.Clear, contentDescription = "মুছুন", modifier = Modifier.size(18.dp))
                                    }
                                }
                            },
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BrandPrimary,
                                unfocusedBorderColor = OutlineVariantLight,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("search_field")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Category Chips Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            categories.forEach { category ->
                                val isSelected = selectedCategory == category
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { onCategorySelect(category) },
                                    label = {
                                        Text(
                                            text = category,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 12.sp
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = BrandPrimary,
                                        selectedLabelColor = Color.White,
                                        containerColor = Color.White,
                                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        enabled = true,
                                        selected = isSelected,
                                        borderColor = OutlineVariantLight,
                                        selectedBorderColor = BrandPrimary,
                                        borderWidth = 1.dp
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }
                        }
                    }
                }

                // Technicians list empty state
                if (technicians.isEmpty()) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White,
                            border = BorderStroke(1.dp, OutlineVariantLight),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 20.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Handyman,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = BrandPrimary.copy(alpha = 0.5f)
                                )
                                Text(
                                    text = "কোনো সেবা বা মিস্ত্রি পাওয়া যায়নি",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "নতুন মিস্ত্রি বা টেকনিশিয়ান যোগ করতে '+' বাটনে চাপুন।",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    items(technicians, key = { it.id }) { tech ->
                        TechnicianCard(
                            technician = tech,
                            onChatClick = { onTechnicianClick(tech) },
                            onCallClick = {
                                makePhoneCall(context, tech.phone)
                            },
                            onDeleteClick = { techToDelete = tech }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddTechnicianDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, category, phone, address ->
                onAddTechnician(name, category, phone, address)
                showAddDialog = false
                Toast.makeText(context, "নতুন সেবা সফলভাবে যোগ করা হয়েছে!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (showPaymentHistory) {
        PaymentHistoryDialog(
            payments = payments,
            onDismiss = { showPaymentHistory = false }
        )
    }

    if (showProfileInfo) {
        ServiceInfoDialog(onDismiss = { showProfileInfo = false })
    }

    techToDelete?.let { tech ->
        DeleteConfirmationDialog(
            technician = tech,
            onDismiss = { techToDelete = null },
            onConfirm = {
                onDeleteTechnician(tech)
                techToDelete = null
                Toast.makeText(context, "সার্ভিসটি মুছে ফেলা হয়েছে", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
fun TechnicianCard(
    technician: Technician,
    onChatClick: () -> Unit,
    onCallClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    // Professional Polish Card: bg-white border border-[#C4C6D0] p-4 rounded-2xl flex items-start gap-4
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onChatClick() }
            .testTag("tech_card_${technician.id}"),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, OutlineVariantLight),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Circular Avatar in #005FAF
            TechnicianAvatar(
                name = technician.name,
                category = technician.category
            )

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = technician.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CategoryBadge(
                            category = technician.category,
                            statusText = "সক্রিয়",
                            isHighlighted = technician.category.contains("সোলার")
                        )

                        // More Menu for delete
                        Box {
                            IconButton(
                                onClick = { showMenu = true },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    Icons.Default.MoreVert,
                                    contentDescription = "অপশন",
                                    tint = MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("মুছে ফেলুন", color = MaterialTheme.colorScheme.error) },
                                    leadingIcon = {
                                        Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                                    },
                                    onClick = {
                                        showMenu = false
                                        onDeleteClick()
                                    }
                                )
                            }
                        }
                    }
                }

                Text(
                    text = "${technician.category} • ${technician.address}",
                    fontSize = 12.sp,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Bottom actions row matching Professional Polish HTML design
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Call Action
                    Row(
                        modifier = Modifier
                            .clickable { onCallClick() }
                            .testTag("call_button_${technician.id}"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "কল",
                            tint = BrandPrimary,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = technician.phone,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandPrimary
                        )
                    }

                    // Chat Action
                    Row(
                        modifier = Modifier
                            .clickable { onChatClick() }
                            .testTag("chat_button_${technician.id}"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Chat,
                            contentDescription = "চ্যাট",
                            tint = SuccessGreen,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "চ্যাট করুন",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SuccessGreen
                        )
                    }
                }
            }
        }
    }
}

private fun makePhoneCall(context: Context, phone: String) {
    if (phone.isBlank() || phone == "N/A") {
        Toast.makeText(context, "ফোন নম্বর পাওয়া যায়নি", Toast.LENGTH_SHORT).show()
        return
    }
    try {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phone")
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "কল করা সম্ভব হয়নি", Toast.LENGTH_SHORT).show()
    }
}

