package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.ChatMessage
import com.example.data.PaymentRecord
import com.example.data.ServiceRepository
import com.example.data.Technician
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

class ServiceViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ServiceRepository

    init {
        val db = AppDatabase.getDatabase(application, viewModelScope)
        repository = ServiceRepository(db.serviceDao())
        viewModelScope.launch {
            repository.ensureDefaultData()
        }
    }

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("সব সেবা")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _selectedTechnician = MutableStateFlow<Technician?>(null)
    val selectedTechnician: StateFlow<Technician?> = _selectedTechnician.asStateFlow()

    private val _paymentSuccessMessage = MutableStateFlow<String?>(null)
    val paymentSuccessMessage: StateFlow<String?> = _paymentSuccessMessage.asStateFlow()

    val allTechnicians: StateFlow<List<Technician>> = repository.allTechnicians
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val filteredTechnicians: StateFlow<List<Technician>> = combine(
        repository.allTechnicians,
        _searchQuery,
        _selectedCategory
    ) { techs, query, category ->
        techs.filter { tech ->
            val matchesCategory = (category == "সব সেবা") ||
                    tech.category.contains(category, ignoreCase = true) ||
                    (category == "অন্যান্য" && isOtherCategory(tech.category))

            val matchesQuery = query.isBlank() ||
                    tech.name.contains(query, ignoreCase = true) ||
                    tech.category.contains(query, ignoreCase = true) ||
                    tech.address.contains(query, ignoreCase = true) ||
                    tech.phone.contains(query, ignoreCase = true)

            matchesCategory && matchesQuery
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private fun isOtherCategory(category: String): Boolean {
        val standard = listOf("ইলেকট্রিক", "সিসি ক্যামেরা", "সোলার", "আইপিএস", "এসি", "পেইন্টিং")
        return standard.none { category.contains(it, ignoreCase = true) }
    }

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val currentMessages: StateFlow<List<ChatMessage>> = _selectedTechnician
        .flatMapLatest { tech ->
            if (tech != null) {
                repository.getMessagesForTechnician(tech.id)
            } else {
                flowOf(emptyList())
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allPayments: StateFlow<List<PaymentRecord>> = repository.allPayments
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onCategorySelect(category: String) {
        _selectedCategory.value = category
    }

    fun selectTechnician(technician: Technician?) {
        _selectedTechnician.value = technician
    }

    fun addTechnician(name: String, category: String, phone: String, address: String) {
        viewModelScope.launch {
            val formattedAddress = if (address.isBlank()) "মেঘনা, কুমিল্লা" else address
            val formattedPhone = if (phone.isBlank()) "N/A" else phone
            val tech = Technician(
                name = name.trim(),
                category = category.trim(),
                phone = formattedPhone.trim(),
                address = formattedAddress.trim()
            )
            repository.insertTechnician(tech)
        }
    }

    fun deleteTechnician(technician: Technician) {
        viewModelScope.launch {
            repository.deleteTechnician(technician)
            if (_selectedTechnician.value?.id == technician.id) {
                _selectedTechnician.value = null
            }
        }
    }

    fun sendMessage(text: String) {
        val tech = _selectedTechnician.value ?: return
        if (text.isBlank()) return

        viewModelScope.launch {
            // Insert user message
            val userMsg = ChatMessage(
                technicianId = tech.id,
                sender = "user",
                message = text.trim()
            )
            repository.insertMessage(userMsg)

            // Simulate technician response if appropriate
            kotlinx.coroutines.delay(600)
            val replyText = generateTechnicianAutoReply(tech.name, text.trim())
            if (replyText != null) {
                repository.insertMessage(
                    ChatMessage(
                        technicianId = tech.id,
                        sender = "technician",
                        message = replyText
                    )
                )
            }
        }
    }

    fun makePayment(amount: Double, paymentMethod: String, note: String) {
        val tech = _selectedTechnician.value ?: return
        if (amount <= 0) return

        viewModelScope.launch {
            val randomNum = Random.nextInt(100000, 999999)
            val txnId = "MGN-$randomNum"

            val paymentRecord = PaymentRecord(
                technicianId = tech.id,
                technicianName = tech.name,
                amount = amount,
                paymentMethod = paymentMethod,
                transactionId = txnId,
                note = if (note.isBlank()) "সেবা বিল পরিশোধ" else note.trim()
            )
            repository.insertPayment(paymentRecord)

            // Also post payment message to chat thread
            val paymentChatMsg = ChatMessage(
                technicianId = tech.id,
                sender = "user",
                message = "৳ ${String.format(Locale.US, "%.2f", amount)} টাকা পরিশোধ করা হয়েছে। মাধ্যম: $paymentMethod (Txn: $txnId)",
                isPayment = true,
                paymentAmount = amount,
                transactionId = txnId,
                paymentMethod = paymentMethod
            )
            repository.insertMessage(paymentChatMsg)

            // Tech acknowledgement
            kotlinx.coroutines.delay(500)
            repository.insertMessage(
                ChatMessage(
                    technicianId = tech.id,
                    sender = "technician",
                    message = "ধন্যবাদ! আপনার ৳ ${String.format(Locale.US, "%.2f", amount)} টাকার পেমেন্ট সফলভাবে পেয়েছি। (Txn: $txnId)"
                )
            )

            _paymentSuccessMessage.value = "${String.format(Locale.US, "%.0f", amount)} টাকা সফলভাবে ট্রান্সফার করা হয়েছে!"
        }
    }

    fun clearPaymentSuccessMessage() {
        _paymentSuccessMessage.value = null
    }

    private fun generateTechnicianAutoReply(techName: String, userMsg: String): String? {
        val lower = userMsg.lowercase(Locale.getDefault())
        return when {
            lower.contains("কখন") || lower.contains("সময়") || lower.contains("time") ->
                "আসসালামু আলাইকুম! আমি আজ বিকেলের মধ্যেই আপনার ঠিকানায় উপস্থিত হতে পারব।"
            lower.contains("কত") || lower.contains("খরচ") || lower.contains("দাম") || lower.contains("রেট") ->
                "কাজের ধরন দেখে বিল নির্ধারণ করা হবে। সাধারণ ভিজিট ফি ১০০-২০০ টাকা এবং কাজের উপর নির্ভর করবে।"
            lower.contains("ঠিকানা") || lower.contains("কোথায়") || lower.contains("মেঘনা") ->
                "আমি মেঘনা উপজেলার যেকোনো ইউনিয়নে হোম সার্ভিস দিয়ে থাকি।"
            else ->
                "ধন্যবাদ আপনার বার্তার জন্য। আমি খুব দ্রুত আপনার সাথে যোগাযোগ করছি অথবা সরাসরি কল করতে পারেন।"
        }
    }
}
